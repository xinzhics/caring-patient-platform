package com.caring.sass.ai.article.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.article.ArticleVolcengineVoiceTask;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 火山方案视频任务
 * </p>
 *
 * @author 杨帅
 * @date 2025-08-13
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface ArticleVolcengineVoiceTaskMapper extends SuperMapper<ArticleVolcengineVoiceTask> {

}
