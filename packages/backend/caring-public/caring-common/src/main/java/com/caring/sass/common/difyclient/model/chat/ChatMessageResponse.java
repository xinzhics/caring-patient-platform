package com.caring.sass.common.difyclient.model.chat;

import com.caring.sass.common.difyclient.model.common.Metadata;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话消息响应（阻塞模式）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageResponse {
    /**
     * 消息唯一 ID
     */
    private String messageId;

    /**
     * 会话 ID
     */
    private String conversationId;

    /**
     * App 模式，固定为 chat
     */
    private String mode;

    /**
     * 完整回复内容
     */
    private String answer;

    /**
     * 元数据
     */
    private Metadata metadata;

    /**
     * 消息创建时间戳
     */
    private Long createdAt;
}
