package com.caring.sass.ai.textual.service;

import com.caring.sass.ai.entity.textual.TextualInterpretationPptTask;
import com.caring.sass.base.service.SuperService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * 文献解读PPT
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
public interface TextualInterpretationPptTaskService extends SuperService<TextualInterpretationPptTask> {

    void reCreatePptOutline(Long id);

    SseEmitter createSse(String uid, Long id);


    void startPpt(Long id, String uid);
}
