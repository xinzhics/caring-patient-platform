package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.article.ConsumptionType;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * Ai创作用户
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
public interface ArticleUserService extends SuperService<ArticleUser> {


    boolean deductEnergy(Long userId, int i, ConsumptionType consumptionType);


    String redirectWxAuthUrl(String redirectUri);

}
