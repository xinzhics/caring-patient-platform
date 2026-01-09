package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleUserFreeLimit;
import com.caring.sass.base.service.SuperService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 业务接口
 * 科普创作会员免费额度
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-27
 */
public interface ArticleUserFreeLimitService extends SuperService<ArticleUserFreeLimit> {

    @Transactional
    boolean deductLimit(Long userId, int type);
}
