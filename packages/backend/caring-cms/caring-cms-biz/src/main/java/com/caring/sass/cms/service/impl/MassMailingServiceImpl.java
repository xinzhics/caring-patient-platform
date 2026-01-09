package com.caring.sass.cms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.constant.MassMailingMessageStatus;
import com.caring.sass.cms.constant.MediaTypeEnum;
import com.caring.sass.cms.constant.SendTarget;
import com.caring.sass.cms.dao.MassMailingMapper;
import com.caring.sass.cms.dto.*;
import com.caring.sass.cms.entity.ArticleNews;
import com.caring.sass.cms.entity.ArticleOther;
import com.caring.sass.cms.entity.MassMailing;
import com.caring.sass.cms.service.ArticleNewsService;
import com.caring.sass.cms.service.ArticleOtherService;
import com.caring.sass.cms.service.MassMailingService;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.MassMessagePreview;
import com.caring.sass.wx.dto.config.MassMpNewsMessage;
import com.caring.sass.wx.dto.config.MassVideoMessage;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.WxMpMassNews;
import me.chanjar.weixin.mp.bean.WxMpMassPreviewMessage;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.WxMpMassVideo;
import me.chanjar.weixin.mp.bean.material.WxMpNewsArticle;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName MassMailingServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/11/22 11:29
 * @Version 1.0
 */
@Slf4j
@Service
public class MassMailingServiceImpl extends SuperServiceImpl<MassMailingMapper, MassMailing> implements MassMailingService {

    @Autowired
    ArticleNewsService articleNewsService;

    @Autowired
    ArticleOtherService articleOtherService;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    DistributedLock distributedLock;

    private static final NamedThreadFactory MASS_MAILING_FACTORY = new NamedThreadFactory("mass-mailing-", false);

    private static final ExecutorService MASS_MAILING_EXECUTOR = new ThreadPoolExecutor(2, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000), MASS_MAILING_FACTORY);
    /**
     * 预览
     */
    @Override
    public R<WxMpMassSendResult> previewArticleNews(MassMailingPreviewDto massMailingPreviewDto) {

        MassMessagePreview messagePreview = createMassMessagePreview(massMailingPreviewDto);
        R<WxMpMassSendResult> preview = weiXinApi.massMessagePreview(messagePreview);
        return preview;
    }

    /**
     * 更新消息的发送状态
     * @param id
     * @param messageStatus
     */
    private void updateStatus(Long id, MassMailingMessageStatus messageStatus, String errorMessage) {

        // 获取 redis 锁吧。。
        String lock = "mass_mailing" + id;
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 20);
            if (lockBoolean) {
                MassMailing mailing = baseMapper.selectById(id);

                if (Objects.isNull(mailing)) {
                    throw new BizException("群发消息不存在");
                }
                if (MassMailingMessageStatus.sending.equals(messageStatus)) {
                    mailing.setSendTime(LocalDateTime.now());
                    mailing.setMassSort(0);
                }
                if (MassMailingMessageStatus.sent_error.equals(messageStatus)) {
                    mailing.setMassSort(1);
                }
                if (MassMailingMessageStatus.submitting.equals(messageStatus)) {
                    mailing.setMassSort(0);
                }
                mailing.setMessageStatus(messageStatus);
                if (StringUtils.isEmpty(mailing.getErrorMessage()) && StringUtils.isNotEmptyString(errorMessage)) {
                    mailing.setErrorMessage(errorMessage);
                }
                baseMapper.updateById(mailing);
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }

    }

    /**
     * 编辑时 ，如果之前发送了，并且发送失败了。则清除微信推送回来的结果
     * @param model
     * @return
     */
    @Transactional
    @Override
    public boolean updateById(MassMailing model) {
        MassMailing massMailing = baseMapper.selectById(model.getId());
        boolean clean = false;
        if (MassMailingMessageStatus.sent_error.equals(massMailing.getMessageStatus())) {
            clean = true;
        }
        BeanUtils.copyProperties(model, massMailing);
        if (clean) {
            // 之前是发送失败的状态。先 处理掉 回调回来的信息
            massMailing.setSentCount(0);
            massMailing.setErrorCount(0);
            massMailing.setSentResult("");
            massMailing.setErrorMessage("");
            massMailing.setMessageStatus(MassMailingMessageStatus.wait_send);
            if (MassMailing.TIMING.equals(massMailing.getSendType())) {
                massMailing.setMassSort(3);
            } else {
                massMailing.setMassSort(4);
            }
            massMailing.setTimingSendTime(null);
        }
        return super.updateById(massMailing);
    }

    /**
     * 开始群发
     * @param id
     */
    @Override
    public void sendMassMailing(Long id) {

        MassMailingUpdateDto detail = getDetail(id);
        MassMailingPreviewDto previewDto = new MassMailingPreviewDto();
        BeanUtils.copyProperties(detail, previewDto);
        previewDto.setId(id);
        MassMessagePreview massMessagePreview = createMassMessagePreview(previewDto);
        WxMpMassNews wxMpMassNews = massMessagePreview.getWxMpMassNews();
        WxMpMassPreviewMessage previewMessage = massMessagePreview.getWxMpMassPreviewMessage();
        WxMpMassVideo wxMpMassVideo = massMessagePreview.getWxMpMassVideo();
        MassMpNewsMessage massMpNewsMessage = new MassMpNewsMessage();
        MassVideoMessage videoMessage = new MassVideoMessage();
        WxMpMassTagMessage tagMessage = null;
        // 发送给全体用户
        if (SendTarget.all.equals(detail.getSendTarget())) {
            tagMessage = new WxMpMassTagMessage();
            tagMessage.setSendAll(true);
            tagMessage.setSendIgnoreReprint(detail.getSendIgnoreReprint());
            tagMessage.setContent(previewMessage.getContent());
            tagMessage.setMediaId(previewMessage.getMediaId());
            tagMessage.setMsgType(previewMessage.getMsgType());
            massMpNewsMessage.setTagMessage(tagMessage);
            videoMessage.setTagMessage(tagMessage);
        } else if (SendTarget.choose.equals(detail.getSendTarget())) {
            // 暂时不考虑
        }
        updateStatus(id, MassMailingMessageStatus.sending, null);
        try {
            R<WxMpMassSendResult> massSendResultR = null;
            if (MediaTypeEnum.mpvideo.equals(detail.getMediaTypeEnum())) {

                videoMessage.setWxMpMassVideo(wxMpMassVideo);
                massSendResultR = weiXinApi.massMessageSend(videoMessage);
            } else if (MediaTypeEnum.mpnews.equals(detail.getMediaTypeEnum())) {

                massMpNewsMessage.setWxMpMassNews(wxMpMassNews);
                massSendResultR = weiXinApi.massMessageSend(massMpNewsMessage);
            } else {
                if (SendTarget.all.equals(detail.getSendTarget())) {
                    massSendResultR = weiXinApi.massTagMessageSend(tagMessage);
                }
            }
            if (massSendResultR != null) {
                if (massSendResultR.getIsSuccess() != null && massSendResultR.getIsSuccess()) {
                    WxMpMassSendResult data = massSendResultR.getData();
                    String msgId = data.getMsgId();
                    UpdateWrapper<MassMailing> wrapper = new UpdateWrapper<>();
                    wrapper.eq("id", id);
                    wrapper.set("msg_id", msgId);
                    baseMapper.update(new MassMailing(), wrapper);
                } else {
                    if (massSendResultR.getCode() == 45028) {
                        updateStatus(id, MassMailingMessageStatus.sent_error, "本月群发次数不足");
                    } else {
                        updateStatus(id, MassMailingMessageStatus.sent_error, "系统繁忙");
                    }
                }
            }
        } catch (Exception e) {
            updateStatus(id, MassMailingMessageStatus.sent_error, "系统繁忙");
        }
    }

    @Override
    public MassMailingUpdateDto getDetail(Long id) {
        MassMailing massMailing = baseMapper.selectById(id);
        String tenantCode = massMailing.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        MassMailingUpdateDto updateDto = new MassMailingUpdateDto();
        BeanUtils.copyProperties(massMailing, updateDto);
        updateDto.setId(massMailing.getId());
        jsonStringToList(massMailing, updateDto);
        return updateDto;
    }

    /**
     * 封装预览 或者 群发 提交到微信的 表单
     * @param massMailingPreviewDto
     * @return
     */
    private MassMessagePreview createMassMessagePreview(MassMailingPreviewDto massMailingPreviewDto) {
        MassMessagePreview preview = new MassMessagePreview();
        WxMpMassPreviewMessage wxMpMassPreviewMessage = new WxMpMassPreviewMessage();
        preview.setWxMpMassPreviewMessage(wxMpMassPreviewMessage);
        wxMpMassPreviewMessage.setToWxUserName(massMailingPreviewDto.getWeiXinName());
        MediaTypeEnum mediaTypeEnum = massMailingPreviewDto.getMediaTypeEnum();
        if (MediaTypeEnum.mpnews.equals(mediaTypeEnum)) {

            // 封装 图文的消息体
            List<Long> articleNewsList = massMailingPreviewDto.getArticleNewsList();
            WxMpMassNews wxMpMassNews = new WxMpMassNews();
            List<ArticleNews> articleNews = articleNewsService.listByIds(articleNewsList);
            Map<Long, ArticleNews> articleNewsMap = articleNews.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
            for (Long id : articleNewsList) {
                ArticleNews news = articleNewsMap.get(id);
                if (Objects.nonNull(news)) {
                    articleNewsService.replaceContentUrl(news);
                    wxMpMassNews.addArticle(getWxMpNewsArticle(news));
                }
            }
            preview.setWxMpMassNews(wxMpMassNews);
            wxMpMassPreviewMessage.setMsgType("mpnews");
        } else if (MediaTypeEnum.image.equals(mediaTypeEnum)) {
            List<Long> articleImageList = massMailingPreviewDto.getArticleImageList();
            List<ArticleOther> articleOthers = articleOtherService.listByIds(articleImageList);
            Map<Long, String> mediaMapId = articleOthers.stream().collect(Collectors.toMap(SuperEntity::getId, ArticleOther::getMediaId));
            List<String> imageMediaIds = new ArrayList<>();
            for (Long id : articleImageList) {
                String s = mediaMapId.get(id);
                if (StringUtils.isNotEmptyString(s)) {
                    imageMediaIds.add(s);
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("media_ids", imageMediaIds);
            jsonObject.put("recommend", massMailingPreviewDto.getRecommend());
            jsonObject.put("need_open_comment", 0);
            jsonObject.put("only_fans_can_comment", 0);
            wxMpMassPreviewMessage.setContent(jsonObject.toJSONString());
            wxMpMassPreviewMessage.setMsgType("image");
        } else if (MediaTypeEnum.mpvideo.equals(mediaTypeEnum)) {

            // 封装 视频 的消息体
            Long mpvideoId = massMailingPreviewDto.getArticleOtherId();
            ArticleOther articleOther = articleOtherService.getById(mpvideoId);
            WxMpMassVideo wxMpMassVideo = new WxMpMassVideo();
            wxMpMassVideo.setTitle(articleOther.getTitle());
            wxMpMassVideo.setDescription(articleOther.getIntroduction());
            wxMpMassVideo.setMediaId(articleOther.getMediaId());
            preview.setWxMpMassVideo(wxMpMassVideo);
            wxMpMassPreviewMessage.setMsgType("mpvideo");
        } else if (MediaTypeEnum.voice.equals(mediaTypeEnum)) {

            Long voiceId = massMailingPreviewDto.getArticleOtherId();
            ArticleOther articleOther = articleOtherService.getById(voiceId);
            wxMpMassPreviewMessage.setMediaId(articleOther.getMediaId());
            wxMpMassPreviewMessage.setMsgType("voice");
        } else if (MediaTypeEnum.text.equals(mediaTypeEnum)) {

            wxMpMassPreviewMessage.setContent(massMailingPreviewDto.getTextContent());
            wxMpMassPreviewMessage.setMsgType("text");
            preview.setWxMpMassPreviewMessage(wxMpMassPreviewMessage);
        }
        return preview;
    }

    /**
     * 发送 图文到 微信去
     */
    private WxMpNewsArticle getWxMpNewsArticle(ArticleNews articleNews) {

        WxMpNewsArticle article = new WxMpNewsArticle();
        BeanUtils.copyProperties(articleNews, article);
        return article;
    }


    /**
     * 微信回调群发事件。 修改群发消息的状态
     * @param massCallBack
     */
    @Override
    public void massCallBack(MassCallBack massCallBack) {
        String tenantCode = massCallBack.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        String msgId = massCallBack.getMsgId();
        List<MassMailing> mailings = baseMapper.selectList(Wraps.<MassMailing>lbQ()
                .eq(MassMailing::getMsgId, msgId)
                .eq(MassMailing::getTenantCode, tenantCode));
        if (CollUtil.isEmpty(mailings)) {
            return;
        }
        // 理论上一个 msgId 只有一个群发
        MassMailing mailing = mailings.get(0);
        String lock = "mass_mailing" + mailing.getId();
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 20);
            if (lockBoolean) {
                mailing = super.getById(mailing.getId());
                mailing.setSentResult(JSON.toJSONString(massCallBack));
                String status = massCallBack.getStatus();
                mailing.setMessageStatus(MassMailingMessageStatus.sent_error);
                mailing.setMassSort(1);
                if ("send success".equals(status)) {
                    mailing.setMessageStatus(MassMailingMessageStatus.sent_success);
                    mailing.setMassSort(5);
                } else if ("err(10001)".equals(status)) {
                    mailing.setErrorMessage("涉嫌广告");
                } else if ("err(20001)".equals(status)) {
                    mailing.setErrorMessage("涉嫌政治");
                } else if ("err(20004)".equals(status)) {
                    mailing.setErrorMessage("涉嫌社会");
                } else if ("err(20002)".equals(status)) {
                    mailing.setErrorMessage("涉嫌色情");
                } else if ("err(20006)".equals(status)) {
                    mailing.setErrorMessage("涉嫌违法犯罪");
                } else if ("err(20008)".equals(status)) {
                    mailing.setErrorMessage("涉嫌欺诈");
                } else if ("err(20013)".equals(status)) {
                    mailing.setErrorMessage("涉嫌版权");
                } else if ("err(22000)".equals(status)) {
                    mailing.setErrorMessage("涉嫌互推(互相宣传)");
                } else if ("err(21000)".equals(status)) {
                    mailing.setErrorMessage("涉嫌其他");
                } else if ("err(30001)".equals(status)) {
                    mailing.setErrorMessage("原创校验出现系统错误且用户选择了被判为转载就不群发");
                } else if ("err(30002)".equals(status)) {
                    mailing.setErrorMessage("原创校验被判定为不能群发");
                } else if ("err(30003)".equals(status)) {
                    mailing.setErrorMessage("原创校验被判定为转载文章但用户选择了被判为转载就不群发");
                } else if ("err(40001)".equals(status)) {
                    mailing.setErrorMessage("管理员拒绝");
                } else if ("err(40002)".equals(status)) {
                    mailing.setErrorMessage("管理员30分钟内无响应，超时");
                }
                mailing.setSentCount(massCallBack.getSentCount());
                mailing.setErrorCount(massCallBack.getErrorCount());
                super.updateById(mailing);
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
        try {
            Map<String, Object> copyrightCheckResult = massCallBack.getCopyrightCheckResult();
            if (Objects.nonNull(copyrightCheckResult)) {
                Object list = copyrightCheckResult.get("ResultList");
                Map<String, Object> resultList = null;
                if (Objects.nonNull(list) && list.toString().length() > 0) {
                    resultList = (HashMap) list;
                    Object item1 = resultList.get("item");
                    if (Objects.nonNull(item1) && item1.toString().length() > 0) {
                        ArrayList<HashMap<String, Object>> items = (ArrayList) item1;
                        String articleNewsListJson = mailing.getArticleNewsListJson();
                        List<Long> newsIds = JSON.parseArray(articleNewsListJson, Long.class);
                        for (HashMap<String, Object> item : items) {
                            Object articleIdx = item.get("ArticleIdx");
                            if (Objects.nonNull(articleIdx)) {
                                int i = Integer.parseInt(articleIdx.toString());
                                if (i > 0 && i <= newsIds.size()) {
                                    Long aLong = newsIds.get(i - 1);
                                    ArticleNews articleNews = articleNewsService.getById(aLong);
                                    if (Objects.nonNull(articleNews)) {
                                        articleNews.setUserDeclareState(item.get("UserDeclareState") != null ? item.get("UserDeclareState").toString() : null);
                                        articleNews.setOriginalArticleUrl(item.get("OriginalArticleUrl") != null ? item.get("OriginalArticleUrl").toString() : null);
                                        articleNews.setNeedReplaceContent(item.get("NeedReplaceContent") != null ? item.get("NeedReplaceContent").toString() : null);
                                        articleNews.setNeedShowReprintSource(item.get("NeedShowReprintSource") != null ? item.get("NeedShowReprintSource").toString() : null);
                                        articleNews.setOriginalArticleType(item.get("OriginalArticleType") != null ? item.get("OriginalArticleType").toString() : null);
                                        articleNews.setCanReprint(item.get("CanReprint") != null ? item.get("CanReprint").toString() : null);
                                        articleNews.setAuditState(item.get("AuditState") != null ? item.get("AuditState").toString() : null);
                                        articleNewsService.updateById(articleNews);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }

    }

    /**
     * 查询大于等于 now  小于 future 的等待推送的 定时群发
     * @param now
     */
    @Override
    public void sendTimingMassMailing(LocalDateTime now) {

        now = now.withSecond(0);
        now = now.withNano(0);
        LocalDateTime future = now.plusMinutes(10);

        //查询 当前时间 到 10分钟后，这段时间中的 定时群发消息;
        LbqWrapper<MassMailing> timing = Wraps.<MassMailing>lbQ().eq(MassMailing::getMessageStatus, MassMailingMessageStatus.wait_send)
                .eq(MassMailing::getSendType, "timing")
                .ge(MassMailing::getTimingSendTime, now)
                .lt(MassMailing::getTimingSendTime, future)
                .select(SuperEntity::getId, MassMailing::getSendType);
        List<MassMailing> massMailings = baseMapper.selectList(timing);
        if (CollUtil.isEmpty(massMailings)) {
            return;
        }
        for (MassMailing massMailing : massMailings) {
            MASS_MAILING_EXECUTOR.execute(() -> sendMassMailing(massMailing.getId()));
        }

    }

    @Override
    public void timingMassMailing(MassMailingTimingDto timingDto) {

        Long timingDtoId = timingDto.getId();
        LocalDateTime timingSendTime = timingDto.getTimingSendTime();
        MassMailing massMailing = baseMapper.selectById(timingDtoId);
        massMailing.setSendType(MassMailing.TIMING);
        massMailing.setMassSort(3);
        timingSendTime.withNano(0);
        timingSendTime.withSecond(0);
        massMailing.setTimingSendTime(timingSendTime);
        massMailing.setMessageStatus(MassMailingMessageStatus.wait_send);
        massMailing.setMassSort(2);
        baseMapper.updateById(massMailing);

    }

    @Override
    public void cancelTimingMassMailing(Long timingDtoId) {

        UpdateWrapper<MassMailing> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("send_type", MassMailing.MANUAL);
        updateWrapper.set("mass_sort", 4);
        updateWrapper.set("timing_send_time", null);
        updateWrapper.eq("id", timingDtoId);
        baseMapper.update(new MassMailing(), updateWrapper);

    }


    /**
     * 将提交上来的数组， 转未json 保存到数据库
     * @param massMailing
     * @param tagIds
     * @param openIds
     * @param articleImageList
     * @param articleNewsList
     */
    @Override
    public void listToJsonString(MassMailing massMailing, List<Long> tagIds,
                                        List<Long> openIds, List<Long> articleImageList,
                                        List<Long> articleNewsList) {
        if (CollUtil.isNotEmpty(tagIds)) {
            massMailing.setTagIdsJson(JSON.toJSONString(tagIds));
        }
        if (CollUtil.isNotEmpty(openIds)) {
            massMailing.setOpenIdsJson(JSON.toJSONString(openIds));
        }
        if (CollUtil.isNotEmpty(articleImageList)) {
            massMailing.setArticleImageListJson(JSON.toJSONString(articleImageList));
        }
        if (CollUtil.isNotEmpty(articleNewsList)) {
            massMailing.setArticleNewsListJson(JSON.toJSONString(articleNewsList));

        }
    }

    /**
     * jsonString 转 list
     * @param massMailing
     * @param updateDto
     */
    @Override
    public void jsonStringToList(MassMailing massMailing, MassMailingUpdateDto updateDto) {
        String openIds = massMailing.getOpenIdsJson();
        if (StringUtils.isNotEmptyString(openIds)) {
            updateDto.setOpenIds(JSON.parseArray(openIds, Long.class));
        }
        String tagIds = massMailing.getTagIdsJson();
        if (StringUtils.isNotEmptyString(tagIds)) {
            updateDto.setTagIds(JSON.parseArray(tagIds, Long.class));
        }
        String articleImageList = massMailing.getArticleImageListJson();
        if (StringUtils.isNotEmptyString(articleImageList)) {
            updateDto.setArticleImageList(JSON.parseArray(articleImageList, Long.class));
        }
        String articleNewsList = massMailing.getArticleNewsListJson();
        if (StringUtils.isNotEmptyString(articleNewsList)) {
            updateDto.setArticleNewsList(JSON.parseArray(articleNewsList, Long.class));
        }
    }

    /**
     * 转化消息列表的对象
     * @param records
     * @return
     */
    @Override
    public List<MassMailingVo> modelToVo(List<MassMailing> records) {

        if (CollUtil.isEmpty(records)) {
            return new ArrayList<>();
        }
        List<MassMailingVo> objects = new ArrayList<>(records.size());
        MassMailingVo mailingVo;
        for (MassMailing record : records) {
            mailingVo = new MassMailingVo();
            BeanUtils.copyProperties(record, mailingVo);
            mailingVo.setMessageStatus(record.getMessageStatus());
            mailingVo.setId(record.getId());
            mailingVo.setErrorMessage(record.getErrorMessage());
            MediaTypeEnum mediaTypeEnum = record.getMediaTypeEnum();
            if (MediaTypeEnum.mpnews.equals(mediaTypeEnum)) {
                String articleNewsListJson = record.getArticleNewsListJson();
                List<Long> longList = JSON.parseArray(articleNewsListJson, Long.class);
                List<ArticleNews> articleNews = articleNewsService.listByIds(longList);
                Map<Long, ArticleNews> articleNewsMap = articleNews.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
                for (Long newsId : longList) {
                    ArticleNews news = articleNewsMap.get(newsId);
                    if (Objects.nonNull(news)) {
                        mailingVo.addArticleNewsList(news);
                    }
                }
            } else if (MediaTypeEnum.image.equals(mediaTypeEnum)) {
                String imageListJson = record.getArticleImageListJson();
                List<Long> longList = JSON.parseArray(imageListJson, Long.class);
                List<ArticleOther> articleOthers = articleOtherService.listByIds(longList);
                Map<Long, ArticleOther> articleOtherMap = articleOthers.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
                for (Long newsId : longList) {
                    ArticleOther other = articleOtherMap.get(newsId);
                    if (Objects.nonNull(other)) {
                        mailingVo.addArticleImageList(other);
                    }
                }
            } else if (MediaTypeEnum.mpvideo.equals(mediaTypeEnum) || MediaTypeEnum.voice.equals(mediaTypeEnum)) {
                Long otherId = record.getArticleOtherId();
                ArticleOther other = articleOtherService.getById(otherId);
                if (Objects.nonNull(other)) {
                    mailingVo.setArticleOther(other);
                }
            }
            objects.add(mailingVo);
        }
        return objects;
    }


}
