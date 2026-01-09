package com.caring.sass.ai.textual.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.entity.textual.TextualInterpretationUserVoice;
import com.caring.sass.ai.service.HumanVideoDownloadUtil;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.dao.TextualInterpretationUserVoiceMapper;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserVoiceService;
import com.caring.sass.ai.utils.MiniMaxVoiceApi;
import com.caring.sass.ai.utils.miniMax.VoiceCloneResponse;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务实现类
 * 文献解读用户声音
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Service

public class TextualInterpretationUserVoiceServiceImpl extends SuperServiceImpl<TextualInterpretationUserVoiceMapper, TextualInterpretationUserVoice> implements TextualInterpretationUserVoiceService {


    @Autowired
    MiniMaxVoiceApi miniMaxVoiceApi;

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    HumanVideoDownloadUtil humanVideoDownloadUtil;

    public static String text = "您好，正在试听由AI克隆的您的声音。";


    /**
     * 保存音色的时候，直接克隆
     *
     * @param model 实体对象
     * @return
     */
    @Override
    public boolean save(TextualInterpretationUserVoice model) {

        textualInterpretationUserService.deductEnergy(model.getUserId(), textualUserPayConfig.getSoundCloningSpend(), TextualConsumptionType.CUSTOM_VOICE_CREATION);

        model.setDefaultVoice(false);
        model.setHasClone(false);
        super.save(model);

        try {
            // 调用海螺，进行音色克隆
            voiceClone(model.getId(), text);
        } catch (Exception e) {
            log.error("声音克隆异常", e);
        }

        return true;
    }

    @Override
    public R<String> voiceClone(Long voiceId, String text) {
        TextualInterpretationUserVoice voice = baseMapper.selectById(voiceId);
        if (StringUtils.isNotEmpty(voice.getVoiceId())) {
            return R.success("克隆成功");
        }

        // todo 这里克隆失败逻辑校验
        try {
            Long fileId = miniMaxVoiceApi.uploadFile(voice.getVoiceUrl(), "voice_clone", voice.getHAccount());
            String cloneVoiceId = "h_" + voiceId;
            VoiceCloneResponse r = miniMaxVoiceApi.cloneVoice(fileId, cloneVoiceId, text, voice.getHAccount());

            // 克隆成功
            if (r.getBaseResp().getStatusCode() == 0) {
                // 这里试听7天有效期，需要上传到阿里云oss。
                String aliVoiceUrl = humanVideoDownloadUtil.downloadVoiceAndUploadAliOss(r.getDemoAudio(), cloneVoiceId);
                voice.setVoiceId(cloneVoiceId).setHasClone(true).setDemoAudio(aliVoiceUrl);
                baseMapper.updateById(voice);
                return R.success("克隆成功");
            } else {
                // 克隆失败
                String ret = r.getBaseResp().getStatusMsg();
                voice.setFailReason(ret);
                baseMapper.updateById(voice);
                return R.fail(ret);
            }
        } catch (Exception e) {
            log.error("声音克隆异常", e);
        }
        return R.success("克隆失败");
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

        List<TextualInterpretationUserVoice> voiceList = baseMapper.selectList(Wraps.lbQ());
        for (TextualInterpretationUserVoice voice : voiceList) {
            // 如果voiceId不在列表中，并且克隆过，则删除（已经不可用了）
            if (!voiceIds.contains(voice.getVoiceId()) && voice.getHasClone() && account.equals(voice.getHAccount())) {
                voice.setDeleteStatus(true);
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
        TextualInterpretationUserVoice videoTemplate = baseMapper.selectById(id);
        // 默认音色不能删除
        if (videoTemplate.getDefaultVoice()) {
            throw new BizException("默认音色不能删除");
        }
        videoTemplate.setDeleteStatus(true);
        int i = baseMapper.updateById(videoTemplate);
        return i > 0;
    }



}
