package com.caring.sass.ai.humanVideo.service;

import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.humanVideo.BusinessUserAudioTemplate;
import com.caring.sass.base.service.SuperService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * 用户提交的录音素材
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
public interface BusinessUserAudioTemplateService extends SuperService<BusinessUserAudioTemplate> {

    SseEmitter createSse(String uid);



    void getText(Long userId, String uid, BusinessCard businessCard);



    String getAllResult(String uid);
}
