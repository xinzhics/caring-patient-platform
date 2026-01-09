package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * LLM 返回文本块事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageEvent extends BaseMessageEvent {

    /**
     * LLM 返回文本块内容
     */
    @JsonProperty("answer")
    private String answer;
}
