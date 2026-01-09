package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.article.ArticlePodcastJoin;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 播客和创作文章关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-18
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ArticlePodcastJoinMapper extends SuperMapper<ArticlePodcastJoin> {

}
