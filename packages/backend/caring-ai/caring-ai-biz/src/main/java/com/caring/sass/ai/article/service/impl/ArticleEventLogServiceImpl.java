package com.caring.sass.ai.article.service.impl;


import com.caring.sass.ai.article.dao.ArticleEventLogMapper;
import com.caring.sass.ai.article.service.ArticleEventLogService;
import com.caring.sass.ai.entity.article.ArticleEventLog;
import com.caring.sass.ai.entity.article.ArticleEventLogType;
import com.caring.sass.base.service.SuperServiceImpl;
import java.time.LocalDate;

import com.caring.sass.common.utils.SassDateUtis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * AI创作用户事件日志
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-02
 */
@Slf4j
@Service

public class ArticleEventLogServiceImpl extends SuperServiceImpl<ArticleEventLogMapper, ArticleEventLog> implements ArticleEventLogService {


    @Override
    public boolean save(ArticleEventLog model) {

        /**
         * 计算当前日期周次
         */
        model.setWeekNumber(SassDateUtis.getWeekNumberFromBaseDate(LocalDate.now()));
        model.setCreateDay(LocalDate.now().toString());
        model.setCreateMonth(LocalDate.now().toString().substring(0, 7));
        baseMapper.insert(model);
        return true;
    }



    @Override
    public boolean loginEvent(Long userId) {
        ArticleEventLog build = ArticleEventLog.builder()
                .userId(userId)
                .eventType(ArticleEventLogType.LOGIN)
                .weekNumber(SassDateUtis.getWeekNumberFromBaseDate(LocalDate.now()))
                .createDay(LocalDate.now().toString())
                .createMonth(LocalDate.now().toString().substring(0, 7))
                .build();
        baseMapper.insert(build);
        return true;
    }



}
