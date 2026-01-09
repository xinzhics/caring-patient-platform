package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.article.ArticleUserNotice;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 科普用户系统通知
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
public interface ArticleUserNoticeService extends SuperService<ArticleUserNotice> {


    void sendMembershipExpirationNotice(ArticleUser user);
}
