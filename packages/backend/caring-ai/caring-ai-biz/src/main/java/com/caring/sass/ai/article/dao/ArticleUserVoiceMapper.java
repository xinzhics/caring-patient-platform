package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @date 2025-02-25
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ArticleUserVoiceMapper extends SuperMapper<ArticleUserVoice> {

}
