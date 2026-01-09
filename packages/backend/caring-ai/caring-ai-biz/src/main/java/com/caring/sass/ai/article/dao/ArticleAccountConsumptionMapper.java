package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.article.ArticleAccountConsumption;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 能量豆明细关联表
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-12
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")

public interface ArticleAccountConsumptionMapper extends SuperMapper<ArticleAccountConsumption> {

}
