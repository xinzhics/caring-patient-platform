package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @date 2025-02-25
 */
public interface ArticleUserVoiceService extends SuperService<ArticleUserVoice> {

    /**
     * 同步可用的克隆声音id
     */
     void syncCloningVoiceId();

     /**
     * 检测克隆声音任务状态
     */
    void checkCloningVoiceTaskStatus();


    boolean reStartClone(ArticleUserVoice articleUserVoice);

}
