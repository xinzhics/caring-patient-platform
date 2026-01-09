package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.article.ArticleDailyUsageData;
import com.caring.sass.base.mapper.SuperMapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * <p>
 * Mapper 接口
 * AI创作日使用数据累计
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-02
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ArticleDailyUsageDataMapper extends SuperMapper<ArticleDailyUsageData> {


    int countActiveUsers(@Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);


    int countAllProduceUserNumber(@Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);


    int countAllProduce(@Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);


    int produceUnfinishedTotalNumber(@Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);
}
