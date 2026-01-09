package com.caring.sass.ai.textual.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.textual.TextualInterpretationPptTask;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 文献解读PPT
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface TextualInterpretationPptTaskMapper extends SuperMapper<TextualInterpretationPptTask> {

}
