package com.caring.sass.ai.article.service.impl;


import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.article.dao.ArticleUserNoticeMapper;
import com.caring.sass.ai.article.service.ArticleUserNoticeService;
import com.caring.sass.ai.entity.article.ArticleNoticeType;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.article.ArticleUserNotice;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

/**
 * <p>
 * 业务实现类
 * 科普用户系统通知
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
@Slf4j
@Service

public class ArticleUserNoticeServiceImpl extends SuperServiceImpl<ArticleUserNoticeMapper, ArticleUserNotice> implements ArticleUserNoticeService {


    @Async
    @Override
    public void sendMembershipExpirationNotice(ArticleUser user) {

        Integer selected = baseMapper.selectCount(Wraps.<ArticleUserNotice>lbQ()
                .eq(ArticleUserNotice::getUserId, user.getId())
                .eq(ArticleUserNotice::getNoticeType, ArticleNoticeType.Membership_expiration)
                .between(SuperEntity::getCreateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.of(LocalDate.now(), LocalTime.MAX)));
        if (selected == null || selected == 0) {
            ArticleUserNotice articleUserNotice = ArticleUserNotice.builder()
                    .userId(user.getId())
                    .readStatus(0)
                    .noticeType(ArticleNoticeType.Membership_expiration)
                    .build();
            String userName = user.getUserName();
            String content = "您的会员即将到期，为保证您的权益，请及时续费。";
            if (StrUtil.isNotBlank(userName)) {
                articleUserNotice.setNoticeContent(userName + "，" + content);
            } else {
                articleUserNotice.setNoticeContent(content);
            }
            baseMapper.insert(articleUserNotice);
        }


    }
}
