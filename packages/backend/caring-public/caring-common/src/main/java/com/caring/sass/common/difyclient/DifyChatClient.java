package com.caring.sass.common.difyclient;

import com.caring.sass.common.difyclient.callback.ChatStreamCallback;
import com.caring.sass.common.difyclient.exception.DifyApiException;
import com.caring.sass.common.difyclient.model.chat.*;
import com.caring.sass.common.difyclient.model.common.SimpleResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Dify 对话型应用客户端接口
 * 包含对话型应用相关的功能
 */
public interface DifyChatClient extends DifyBaseClient {

    /**
     * 发送对话消息（阻塞模式）
     *
     * @param message 消息
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    ChatMessageResponse sendChatMessage(ChatMessage message) throws IOException, DifyApiException;

    /**
     * 发送对话消息（流式模式）
     * 注：Agent模式下不允许blocking
     *
     * @param message  消息
     * @param callback 回调
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    void sendChatMessageStream(ChatMessage message, ChatStreamCallback callback) throws IOException, DifyApiException;

    /**
     * 停止对话消息
     *
     * @param taskId 任务 ID
     * @param user   用户标识
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SimpleResponse stopChatMessage(String taskId, String user) throws IOException, DifyApiException;

    /**
     * 消息反馈（点赞）
     *
     * @param messageId 消息 ID
     * @param rating    评分
     * @param user      用户标识
     * @param content   内容
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SimpleResponse feedbackMessage(String messageId, String rating, String user, String content) throws IOException, DifyApiException;

    /**
     * 获取下一轮建议问题列表
     *
     * @param messageId 消息 ID
     * @param user      用户标识
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SuggestedQuestionsResponse getSuggestedQuestions(String messageId, String user) throws IOException, DifyApiException;

    /**
     * 获取会话历史消息
     *
     * @param conversationId 会话 ID
     * @param user           用户标识
     * @param firstId        当前页第一条聊天记录的 ID
     * @param limit          一次请求返回多少条聊天记录
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    MessageListResponse getMessages(String conversationId, String user, String firstId, Integer limit) throws IOException, DifyApiException;

    /**
     * 获取会话列表
     *
     * @param user   用户标识
     * @param lastId 当前页最后面一条记录的 ID
     * @param limit  一次请求返回多少条记录
     * @param sortBy 排序字段
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    ConversationListResponse getConversations(String user, String lastId, Integer limit, String sortBy) throws IOException, DifyApiException;

    /**
     * 删除会话
     *
     * @param conversationId 会话 ID
     * @param user           用户标识
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SimpleResponse deleteConversation(String conversationId, String user) throws IOException, DifyApiException;

    /**
     * 会话重命名
     *
     * @param conversationId 会话 ID
     * @param name           名称
     * @param autoGenerate   自动生成标题
     * @param user           用户标识
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    Conversation renameConversation(String conversationId, String name, Boolean autoGenerate, String user) throws IOException, DifyApiException;

    /**
     * 语音转文字
     *
     * @param file 文件
     * @param user 用户标识
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    AudioToTextResponse audioToText(File file, String user) throws IOException, DifyApiException;

    /**
     * 语音转文字
     *
     * @param inputStream 输入流
     * @param fileName    文件名
     * @param user        用户标识
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    AudioToTextResponse audioToText(InputStream inputStream, String fileName, String user) throws IOException, DifyApiException;

    /**
     * 文字转语音
     *
     * @param messageId 消息 ID
     * @param text      文本
     * @param user      用户标识
     * @return 音频数据
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    byte[] textToAudio(String messageId, String text, String user) throws IOException, DifyApiException;

    /**
     * 获取应用元数据
     *
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    AppMetaResponse getAppMeta() throws IOException, DifyApiException;

}
