package com.caring.sass.ai.config;

import com.caring.sass.ai.assistant.*;
import com.caring.sass.ai.assistant.tool.KnowledgeSearchTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 智能助手配置类
 *
 * @author leizhi
 */
@Component
public class AssistantConfig {

    private final KnowledgeSearchTool knowledgeSearchTool;

    public AssistantConfig(KnowledgeSearchTool knowledgeSearchTool) {

        this.knowledgeSearchTool = knowledgeSearchTool;

    }

    @Bean
    public SearchAssistant searchAssistant(StreamingChatLanguageModel streamingChatLanguageModel) {
        return AiServices.builder(SearchAssistant.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .build();
    }


    @Bean
    public KnowledgeSearchAssistant knowledgeSearchAssistant(OpenAiChatModel openAiChatModel) {
        return AiServices.builder(KnowledgeSearchAssistant.class)
                .chatLanguageModel(openAiChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(3))
                .build();
    }


}
