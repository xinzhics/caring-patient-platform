package com.caring.sass.ai.know.service;

import com.caring.sass.ai.entity.know.KnowledgeDailyCollection;
import com.caring.sass.ai.entity.know.KnowledgeFile;

/**
 * <p>
 * 业务接口
 * 知识库文档标签表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-25
 */
public interface KnowledgeFileLabelService {


    void asyncAnalyzeTags(KnowledgeDailyCollection collection);


    void asyncAnalyzeTags(KnowledgeFile collection);

}
