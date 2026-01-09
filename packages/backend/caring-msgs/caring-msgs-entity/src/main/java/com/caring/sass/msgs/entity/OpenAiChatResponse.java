package com.caring.sass.msgs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * openAI返回
 *
 * @author leizhi
 */
@Data
public class OpenAiChatResponse {

    /**
     * 问题消耗tokens
     */
    @JsonProperty("question_tokens")
    private long questionTokens = 0;
}
