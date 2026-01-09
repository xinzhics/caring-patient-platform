package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 消息内容替换事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageReplaceEvent extends BaseMessageEvent {

    /**
     * 替换内容（直接替换 LLM 所有回复文本）
     */
    @JsonProperty("answer")
    private String answer;
}
