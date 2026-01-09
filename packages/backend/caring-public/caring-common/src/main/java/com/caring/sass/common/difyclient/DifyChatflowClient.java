package com.caring.sass.common.difyclient;

import com.caring.sass.common.difyclient.callback.ChatflowStreamCallback;
import com.caring.sass.common.difyclient.exception.DifyApiException;
import com.caring.sass.common.difyclient.model.chat.ChatMessage;

import java.io.IOException;

/**
 * Dify 工作流编排对话型应用客户端接口
 * 继承自 DifyChatClient，支持工作流编排对话型应用的特性
 */
public interface DifyChatflowClient extends DifyChatClient {

    /**
     * 发送对话消息（流式模式，支持工作流）
     * 注：Agent模式下不允许blocking
     *
     * @param message  消息
     * @param callback 工作流编排对话回调
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    void sendChatMessageStream(ChatMessage message, ChatflowStreamCallback callback) throws IOException, DifyApiException;
}
