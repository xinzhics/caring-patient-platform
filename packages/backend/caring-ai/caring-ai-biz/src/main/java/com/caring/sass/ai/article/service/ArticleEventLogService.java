package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleEventLog;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * AI创作用户事件日志
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-02
 */
public interface ArticleEventLogService extends SuperService<ArticleEventLog> {

    boolean loginEvent(Long userId);
}
