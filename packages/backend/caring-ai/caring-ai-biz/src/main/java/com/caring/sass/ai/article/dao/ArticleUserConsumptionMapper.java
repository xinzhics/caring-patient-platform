package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.article.ArticleUserConsumption;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 科普创作用户能量豆消费记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ArticleUserConsumptionMapper extends SuperMapper<ArticleUserConsumption> {

}
