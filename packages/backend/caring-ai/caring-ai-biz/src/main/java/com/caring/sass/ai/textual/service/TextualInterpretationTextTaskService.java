package com.caring.sass.ai.textual.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.textual.TextualInterpretationTextTask;

/**
 * <p>
 * 业务接口
 * 文献解读txt
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
public interface TextualInterpretationTextTaskService extends SuperService<TextualInterpretationTextTask> {

    void reUpdate(Long id);
}
