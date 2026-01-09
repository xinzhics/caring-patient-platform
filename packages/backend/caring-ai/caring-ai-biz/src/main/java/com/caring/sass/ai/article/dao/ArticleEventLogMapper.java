package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.dto.article.EventCountDTO;
import com.caring.sass.ai.entity.article.ArticleEventLog;
import com.caring.sass.base.mapper.SuperMapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * AI创作用户事件日志
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-02
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ArticleEventLogMapper extends SuperMapper<ArticleEventLog> {


    List<EventCountDTO> countEventsByType(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );

}
