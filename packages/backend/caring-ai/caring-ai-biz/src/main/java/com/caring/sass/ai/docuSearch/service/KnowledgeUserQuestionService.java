package com.caring.sass.ai.docuSearch.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.docuSearch.KnowledgeUserQuestion;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * 知识库用户问题
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-17
 */
public interface KnowledgeUserQuestionService extends SuperService<KnowledgeUserQuestion> {



    SseEmitter submitQuestion(String uid, String question, String conversation);


    void cancelQuestion(String uid);


    SseEmitter createSse(String uid);

    KnowledgeUserQuestion saveQuestion(String question, String conversation, String uid);
}
