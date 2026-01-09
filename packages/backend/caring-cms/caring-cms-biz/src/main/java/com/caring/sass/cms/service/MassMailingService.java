package com.caring.sass.cms.service;

import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.dto.*;
import com.caring.sass.cms.entity.MassMailing;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName MassMailingService
 * @Description
 * @Author yangShuai
 * @Date 2021/11/22 11:28
 * @Version 1.0
 */
public interface MassMailingService extends SuperService<MassMailing> {


    void sendMassMailing(Long id);

    MassMailingUpdateDto getDetail(Long id);

    void cancelTimingMassMailing(Long timingDtoId);

    void listToJsonString(MassMailing massMailing, List<Long> tagIds,
                          List<Long> openIds, List<Long> articleImageList,
                          List<Long> articleNewsList);

    /**
     * 预览 图文
     * @param previewDto
     */
    R<WxMpMassSendResult> previewArticleNews(MassMailingPreviewDto previewDto);


    void jsonStringToList(MassMailing massMailing, MassMailingUpdateDto updateDto);


    List<MassMailingVo> modelToVo(List<MassMailing> records);

    /**
     * 微信回调回来的群发结果
     * @param massCallBack
     */
    void massCallBack(MassCallBack massCallBack);

    /**
     * 发送定时发送的 消息
     */
    void sendTimingMassMailing(LocalDateTime dateTime);

    /**
     * 设置消息为定时发送
     * @param timingDto
     */
    void timingMassMailing(MassMailingTimingDto timingDto);

}
