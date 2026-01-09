package com.caring.sass.ai.article.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.dao.ArticleUserVoiceMapper;
import com.caring.sass.ai.article.service.ArticleUserFreeLimitService;
import com.caring.sass.ai.article.service.ArticleUserService;
import com.caring.sass.ai.article.service.ArticleUserVoiceService;
import com.caring.sass.ai.dto.article.VoiceCloneStatus;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.ai.entity.article.ConsumptionType;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.service.HumanVideoDownloadUtil;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.ai.utils.MiniMaxVoiceApi;
import com.caring.sass.ai.utils.miniMax.VoiceCloneResponse;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @date 2025-02-25
 */
@Slf4j
@Service
public class ArticleUserVoiceServiceImpl extends SuperServiceImpl<ArticleUserVoiceMapper, ArticleUserVoice> implements ArticleUserVoiceService {

    @Autowired
    MiniMaxVoiceApi miniMaxVoiceApi;

    @Autowired
    ArticleUserFreeLimitService articleUserFreeLimitService;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Autowired
    HumanVideoDownloadUtil humanVideoDownloadUtil;

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public static String text = "您好，正在试听由AI克隆的您的声音。";


    /**
     * 保存音色的时候，直接克隆
     *
     * @param model 实体对象
     * @return
     */
    @Override
    public boolean save(ArticleUserVoice model) {

        Boolean textual = model.getTextual();
        if (textual == null || !textual) {
            // 扣费。
            // 先确定用户是否有免费次数。
            // 如果用户没有免费次数，则扣除能量豆
            boolean deducted = articleUserFreeLimitService.deductLimit(model.getUserId(), 1);
            if (!deducted) {
                // 尝试扣除用户的能量豆 30
                articleUserService.deductEnergy(model.getUserId(), articleUserPayConfig.getSoundCloningSpend(), ConsumptionType.CUSTOM_VOICE_CREATION);
            }
            ArticleUser articleUser = articleUserService.getById(model.getUserId());
            model.setUserMobile(articleUser.getUserMobile());
        } else {
            textualInterpretationUserService.deductEnergy(model.getUserId(), textualUserPayConfig.getSoundCloningSpend(), TextualConsumptionType.CUSTOM_VOICE_CREATION);
            TextualInterpretationUser user = textualInterpretationUserService.getById(model.getUserId());
            model.setUserMobile(user.getUserMobile());
        }

        model.setDefaultVoice(false);
        model.setHasClone(false);
        if (model.getCloneStatus() == null) {
            model.setCloneStatus(VoiceCloneStatus.WAITING);
        }
        super.save(model);
        return true;
    }

    public void voiceClone(ArticleUserVoice voice, String text, boolean reClone) {

        try {
            Long fileId = miniMaxVoiceApi.uploadFile(voice.getVoiceUrl(), "voice_clone", voice.getAccount());
            VoiceCloneResponse r;
            String cloneVoiceId;
            if (!reClone) {
                cloneVoiceId = "h_" + voice.getId();
                r = miniMaxVoiceApi.cloneVoice(fileId, cloneVoiceId, text, voice.getAccount());
            } else {
                cloneVoiceId = "h_" + System.currentTimeMillis();
                r = miniMaxVoiceApi.cloneVoice(fileId, cloneVoiceId, text, voice.getAccount());
            }

            // 克隆成功
            if (r.getBaseResp().getStatusCode() == 0) {
                // 更新克隆状态
                voice.setCloneStatus(VoiceCloneStatus.SUCCESS);
                // 这里试听7天有效期，需要上传到阿里云oss。
                voice.setVoiceId(cloneVoiceId);
                voice.setHasClone(true);
                voice.setMiniExpired(0);
                voice.setCloneStatus(VoiceCloneStatus.SUCCESS);
                baseMapper.updateById(voice);
                // 异步处理。加快速度
                SaasGlobalThreadPool.execute(() -> downloadVoiceAndUploadAliOss(voice.getId(), r.getDemoAudio(), cloneVoiceId));
                if (reClone) {
                    // 重新克隆成功后，防止有用户依然使用的旧音色id。 这里进行缓存一下，当用户旧音色查不到时，尝试从缓存中获取
                    redisTemplate.opsForValue().set("h_" + voice.getId(), cloneVoiceId, 7, TimeUnit.DAYS);
                }
            } else {
                // 克隆失败
                voice.setCloneStatus(VoiceCloneStatus.FAILED);
                String ret = r.getBaseResp().getStatusMsg();
                voice.setFailReason(ret);
                baseMapper.updateById(voice);
            }
        } catch (Exception e) {
            log.error("声音克隆异常", e);
        }
    }


    public void downloadVoiceAndUploadAliOss(Long id, String videoUrl, String fileName) {
        ArticleUserVoice userVoice = new ArticleUserVoice();
        String aliVoiceUrl = humanVideoDownloadUtil.downloadVoiceAndUploadAliOss(videoUrl, fileName);
        userVoice.setDemoAudio(aliVoiceUrl);
        userVoice.setId(id);
        baseMapper.updateById(userVoice);
    }



    @Autowired
    DistributedLock distributedLock;

    public boolean reStartClone(ArticleUserVoice articleUserVoice) {
        articleUserVoice = baseMapper.selectById(articleUserVoice.getId());
        if (articleUserVoice.getMiniExpired() == 0) {
            return true;
        }

        String lock = "ArticleUserVoice:" + articleUserVoice.getId() +":lock";

        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 10000L, 20);
            if (lockBoolean) {

                // 重新查一下。防止 其他线程已经对音色进行了续期
                articleUserVoice = baseMapper.selectById(articleUserVoice.getId());

                // 双重检查，防止重复执行
                if (articleUserVoice.getMiniExpired() == 0 &&
                        !articleUserVoice.getDeleteStatus()) {
                    return true;
                }

                // 执行voiceClone
                voiceClone(articleUserVoice, text, true);

                if (articleUserVoice.getCloneStatus().equals(VoiceCloneStatus.FAILED)) {
                    articleUserVoice.setDeleteStatus(true);
                    baseMapper.updateById(articleUserVoice);
                }
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }

        if (articleUserVoice.getMiniExpired() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 30秒触发一次这个方法
     */
    public void checkCloningVoiceTaskStatus() {

        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("checkCloningVoiceTaskStatus", "1", 5, TimeUnit.MINUTES);
        if (ifAbsent != null && !ifAbsent) {
            return;
        }
        List<ArticleUserVoice> list = baseMapper.selectList(Wraps.<ArticleUserVoice>lbQ()
                .last(" limit 0,10 ")
                .eq(ArticleUserVoice::getCloneStatus, VoiceCloneStatus.WAITING));
        try {

            for (ArticleUserVoice articleUserVoice : list) {
                if (StringUtils.isNotEmpty(articleUserVoice.getVoiceId())) {
                    continue;
                }
                try {
                    voiceClone(articleUserVoice, text, false);
                } catch (Exception e) {
                    log.error("声音克隆异常", e);
                    articleUserVoice.setCloneStatus(VoiceCloneStatus.FAILED);
                    articleUserVoice.setFailReason("克隆失败");
                    baseMapper.updateById(articleUserVoice);
                }
            }

        } finally {
            redisTemplate.delete("checkCloningVoiceTaskStatus");
        }

    }

    @Override
    public void syncCloningVoiceId() {
        try {
            cloneVoiceStatusCheck("leizhi");
            cloneVoiceStatusCheck("kailing");
        } catch (Exception e) {
            log.error("声音同步失败", e);
        }
    }

    private boolean cloneVoiceStatusCheck(String account) throws IOException {
        Map<String, Object> ret = miniMaxVoiceApi.getVoice("voice_cloning", account);
        JSONObject root = new JSONObject(ret);
        JSONObject baseResp = root.getJSONObject("base_resp");
        // 检查请求是否成功（status_code == 0）
        if (baseResp.getIntValue("status_code") != 0) {
            return true;
        }

        JSONArray voiceCloningArray = root.getJSONArray("voice_cloning");
        // 提取所有 voice_id
        List<String> voiceIds = new ArrayList<>();
        for (int i = 0; i < voiceCloningArray.size(); i++) {
            JSONObject voice = voiceCloningArray.getJSONObject(i);
            String voiceId = voice.getString("voice_id");
            voiceIds.add(voiceId);
        }

        List<ArticleUserVoice> voiceList = baseMapper.selectList(Wraps.<ArticleUserVoice>lbQ()
                .eq(ArticleUserVoice::getAccount, account));
        for (ArticleUserVoice voice : voiceList) {
            // 如果voiceId不在列表中，并且克隆过，则删除（已经不可用了）
            if (!voiceIds.contains(voice.getVoiceId()) && voice.getHasClone() && account.equals(voice.getAccount())) {
                voice.setMiniExpired(1);
                baseMapper.updateById(voice);
            }
        }
        return false;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        ArticleUserVoice videoTemplate = baseMapper.selectById(id);
        // 默认音色不能删除
        if (videoTemplate.getDefaultVoice()) {
            throw new BizException("默认音色不能删除");
        }
        videoTemplate.setDeleteStatus(true);
        int i = baseMapper.updateById(videoTemplate);
        return i > 0;
    }

}
