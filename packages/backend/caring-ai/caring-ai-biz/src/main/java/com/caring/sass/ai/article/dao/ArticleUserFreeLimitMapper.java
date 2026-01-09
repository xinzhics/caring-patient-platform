package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.article.ArticleUserFreeLimit;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 科普创作会员免费额度
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-27
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ArticleUserFreeLimitMapper extends SuperMapper<ArticleUserFreeLimit> {

}
