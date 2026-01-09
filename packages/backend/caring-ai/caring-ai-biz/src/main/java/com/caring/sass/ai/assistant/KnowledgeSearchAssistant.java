package com.caring.sass.ai.assistant;


import com.caring.sass.ai.know.model.KnowledgeFileQuery;
import dev.langchain4j.service.SystemMessage;


/**
 * 知识库搜索
 *
 * @author leizhi
 */
public interface KnowledgeSearchAssistant {

    @SystemMessage({
            "You are a natural language search agent.",
            "You must use KnowledgeSearch tool to analyze user requirements",
            "set parameters to academic materials conditions, return the final result as json mode to me. ",
    })
    KnowledgeFileQuery analyzeAcademicMaterials(String userMessage);


}