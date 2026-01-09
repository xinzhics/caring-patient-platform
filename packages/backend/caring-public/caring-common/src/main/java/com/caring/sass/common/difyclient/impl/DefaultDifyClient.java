package com.caring.sass.common.difyclient.impl;

import com.caring.sass.common.difyclient.DifyClient;
import com.caring.sass.common.difyclient.callback.*;
import com.caring.sass.common.difyclient.enums.EventType;
import com.caring.sass.common.difyclient.enums.ResponseMode;
import com.caring.sass.common.difyclient.event.BaseEvent;
import com.caring.sass.common.difyclient.event.PingEvent;
import com.caring.sass.common.difyclient.exception.DifyApiException;
import com.caring.sass.common.difyclient.model.chat.*;
import com.caring.sass.common.difyclient.model.common.SimpleResponse;
import com.caring.sass.common.difyclient.model.completion.CompletionRequest;
import com.caring.sass.common.difyclient.model.completion.CompletionResponse;
import com.caring.sass.common.difyclient.model.workflow.*;
import com.caring.sass.common.difyclient.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Dify API 客户端默认实现
 * 提供对话型应用、文本生成型应用和工作流应用的完整功能
 */
@Slf4j
public class DefaultDifyClient extends DifyBaseClientImpl implements DifyClient {

    // 流式响应相关常量
    private static final String DATA_PREFIX = "data:";
    private static final String PING_EVENT = "event: ping";

    // API 路径常量
    // 对话型应用相关路径
    private static final String CHAT_MESSAGES_PATH = "/chat-messages";
    private static final String MESSAGES_PATH = "/messages";
    private static final String CONVERSATIONS_PATH = "/conversations";
    private static final String AUDIO_TO_TEXT_PATH = "/audio-to-text";
    private static final String TEXT_TO_AUDIO_PATH = "/text-to-audio";
    private static final String META_PATH = "/meta";
    private static final String STOP_PATH = "/stop";
    private static final String FEEDBACKS_PATH = "/feedbacks";
    private static final String SUGGESTED_QUESTIONS_PATH = "/suggested";
    private static final String NAME_PATH = "/name";

    // 文本生成型应用相关路径
    private static final String COMPLETION_MESSAGES_PATH = "/completion-messages";

    // 工作流应用相关路径
    private static final String WORKFLOWS_PATH = "/workflows";
    private static final String WORKFLOWS_RUN_PATH = "/workflows/run";
    private static final String WORKFLOWS_TASKS_PATH = "/workflows/tasks";
    private static final String WORKFLOWS_LOGS_PATH = "/workflows/logs";

    /**
     * 构造函数
     *
     * @param baseUrl API基础URL
     * @param apiKey  API密钥
     */
    public DefaultDifyClient(String baseUrl, String apiKey) {
        super(baseUrl, apiKey);
    }

    /**
     * 构造函数
     *
     * @param baseUrl    API基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     */
    public DefaultDifyClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        super(baseUrl, apiKey, httpClient);
    }

    // ==================== 对话型应用相关方法 ====================

    @Override
    public ChatMessageResponse sendChatMessage(ChatMessage message) throws IOException, DifyApiException {
        log.debug("发送对话消息: {}", message);
        return executePost(CHAT_MESSAGES_PATH, message, ChatMessageResponse.class);
    }

    @Override
    public void sendChatMessageStream(ChatMessage message, ChatStreamCallback callback) throws IOException, DifyApiException {
        log.debug("发送流式对话消息: user={}, inputs={}", message.getUser(), message.getInputs() != null ? message.getInputs().keySet() : null);
        // 确保请求模式为流式
        message.setResponseMode(ResponseMode.STREAMING);

        // 执行流式请求
        executeStreamRequest(CHAT_MESSAGES_PATH, message, (line) -> processStreamLine(line, callback, (data, eventType) -> {
            StreamEventDispatcher.dispatchChatEvent(callback, data, eventType);
        }), callback::onException);
    }

    @Override
    public void sendChatMessageStream(ChatMessage message, ChatflowStreamCallback callback) throws IOException, DifyApiException {
        log.debug("发送流式对话消息: user={}, inputs={}", message.getUser(), message.getInputs() != null ? message.getInputs().keySet() : null);
        // 确保请求模式为流式
        message.setResponseMode(ResponseMode.STREAMING);

        // 执行流式请求
        executeStreamRequest(CHAT_MESSAGES_PATH, message, (line) -> processStreamLine(line, callback, (data, eventType) -> {
            StreamEventDispatcher.dispatchChatFlowEvent(callback, data, eventType);
        }), callback::onException);
    }

    @Override
    public SimpleResponse stopChatMessage(String taskId, String user) throws IOException, DifyApiException {
        log.debug("停止对话消息: taskId={}, user={}", taskId, user);
        Map<String, String> body = new HashMap<>();
        body.put("user", user);
        return executePost(CHAT_MESSAGES_PATH + "/" + taskId + STOP_PATH, body, SimpleResponse.class);
    }

    @Override
    public SimpleResponse feedbackMessage(String messageId, String rating, String user, String content) throws IOException, DifyApiException {
        log.debug("消息反馈: messageId={}, rating={}, user={}", messageId, rating, user);
        Map<String, String> body = new HashMap<>();
        body.put("rating", rating);
        body.put("user", user);
        if (content != null) {
            body.put("content", content);
        }
        return executePost(MESSAGES_PATH + "/" + messageId + FEEDBACKS_PATH, body, SimpleResponse.class);
    }

    @Override
    public SuggestedQuestionsResponse getSuggestedQuestions(String messageId, String user) throws IOException, DifyApiException {
        log.debug("获取建议问题: messageId={}, user={}", messageId, user);
        return executeGet(MESSAGES_PATH + "/" + messageId + SUGGESTED_QUESTIONS_PATH + "?user=" + user, SuggestedQuestionsResponse.class);
    }

    @Override
    public MessageListResponse getMessages(String conversationId, String user, String firstId, Integer limit) throws IOException, DifyApiException {
        log.debug("获取消息列表: conversationId={}, user={}, firstId={}, limit={}", conversationId, user, firstId, limit);
        Map<String, Object> params = new HashMap<>();
        params.put("conversation_id", conversationId);
        params.put("user", user);
        params.put("first_id", firstId);
        params.put("limit", limit);

        String url = buildUrlWithParams(MESSAGES_PATH, params);
        Request request = createGetRequest(url);
        return executeRequest(request, MessageListResponse.class);
    }

    @Override
    public ConversationListResponse getConversations(String user, String lastId, Integer limit, String sortBy) throws IOException, DifyApiException {
        log.debug("获取会话列表: user={}, lastId={}, limit={}, sortBy={}", user, lastId, limit, sortBy);
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("last_id", lastId);
        params.put("limit", limit);
        params.put("sort_by", sortBy);

        String url = buildUrlWithParams(CONVERSATIONS_PATH, params);
        Request request = createGetRequest(url);
        return executeRequest(request, ConversationListResponse.class);
    }

    @Override
    public SimpleResponse deleteConversation(String conversationId, String user) throws IOException, DifyApiException {
        log.debug("删除会话: conversationId={}, user={}", conversationId, user);
        Map<String, String> body = new HashMap<>();
        body.put("user", user);
        return executeDelete(CONVERSATIONS_PATH + "/" + conversationId, body, SimpleResponse.class);
    }

    @Override
    public Conversation renameConversation(String conversationId, String name, Boolean autoGenerate, String user) throws IOException, DifyApiException {
        log.debug("重命名会话: conversationId={}, name={}, autoGenerate={}, user={}", conversationId, name, autoGenerate, user);
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("auto_generate", autoGenerate);
        body.put("user", user);
        return executePost(CONVERSATIONS_PATH + "/" + conversationId + NAME_PATH, body, Conversation.class);
    }

    @Override
    public AudioToTextResponse audioToText(File file, String user) throws IOException, DifyApiException {
        log.debug("语音转文字: fileName={}, user={}", file.getName(), user);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(AUDIO, file)).addFormDataPart("user", user).build();

        Request request = new Request.Builder().url(baseUrl + AUDIO_TO_TEXT_PATH).post(requestBody).header("Authorization", "Bearer " + apiKey).build();

        return executeRequest(request, AudioToTextResponse.class);
    }

    @Override
    public AudioToTextResponse audioToText(InputStream inputStream, String fileName, String user) throws IOException, DifyApiException {
        log.debug("语音转文字: fileName={}, user={}", fileName, user);

        // 创建自定义 RequestBody，避免一次性读取整个文件
        RequestBody fileBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return AUDIO;
            }

            @Override
            public void writeTo(okio.BufferedSink sink) throws IOException {
                try (okio.Source source = okio.Okio.source(inputStream)) {
                    sink.writeAll(source);
                }
            }
        };

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", fileName, fileBody).addFormDataPart("user", user).build();

        Request request = new Request.Builder().url(baseUrl + AUDIO_TO_TEXT_PATH).post(requestBody).header("Authorization", "Bearer " + apiKey).build();

        return executeRequest(request, AudioToTextResponse.class);
    }

    @Override
    public byte[] textToAudio(String messageId, String text, String user) throws IOException, DifyApiException {
        log.debug("文字转语音: messageId={}, text={}, user={}", messageId, text, user);
        Map<String, String> body = new HashMap<>();
        if (messageId != null) {
            body.put("message_id", messageId);
        }
        if (text != null) {
            body.put("text", text);
        }
        body.put("user", user);

        RequestBody requestBody = createJsonRequestBody(body);
        Request request = createPostRequest(TEXT_TO_AUDIO_PATH, requestBody);
        return executeRequestForBytes(request);
    }

    @Override
    public AppMetaResponse getAppMeta() throws IOException, DifyApiException {
        return executeGet(META_PATH, AppMetaResponse.class);
    }

    // ==================== 文本生成型应用相关方法 ====================

    @Override
    public CompletionResponse sendCompletionMessage(CompletionRequest request) throws IOException, DifyApiException {
        log.debug("发送文本生成请求: {}", request);
        return executePost(COMPLETION_MESSAGES_PATH, request, CompletionResponse.class);
    }

    @Override
    public void sendCompletionMessageStream(CompletionRequest request, CompletionStreamCallback callback) throws IOException, DifyApiException {
        log.debug("发送流式文本生成请求: {}", request);
        // 确保请求模式为流式
        request.setResponseMode(ResponseMode.STREAMING);

        // 执行流式请求
        executeStreamRequest(COMPLETION_MESSAGES_PATH, request, (line) -> processStreamLine(line, callback, (data, eventType) -> {
            // 分发事件
            StreamEventDispatcher.dispatchCompletionEvent(callback, data);
        }), callback::onException);
    }

    @Override
    public SimpleResponse stopCompletion(String taskId, String user) throws IOException, DifyApiException {
        log.debug("停止文本生成: taskId={}, user={}", taskId, user);
        Map<String, String> body = new HashMap<>();
        body.put("user", user);
        return executePost(COMPLETION_MESSAGES_PATH + "/" + taskId + STOP_PATH, body, SimpleResponse.class);
    }

    // ==================== Workflow应用相关方法 ====================

    @Override
    public WorkflowRunResponse runWorkflow(WorkflowRunRequest request) throws IOException, DifyApiException {
        log.debug("执行工作流: {}", request);
        return executePost(WORKFLOWS_RUN_PATH, request, WorkflowRunResponse.class);
    }

    @Override
    public void runWorkflowStream(WorkflowRunRequest request, WorkflowStreamCallback callback) throws IOException, DifyApiException {
        log.debug("执行流式工作流: {}", request);
        // 确保请求模式为流式
        request.setResponseMode(ResponseMode.STREAMING);

        // 执行流式请求
        executeStreamRequest(WORKFLOWS_RUN_PATH, request, (line) -> processStreamLine(line, callback, (data, eventType) -> {
            // 分发事件
            StreamEventDispatcher.dispatchWorkflowEvent(callback, data);
        }), callback::onException);
    }

    @Override
    public WorkflowStopResponse stopWorkflow(String taskId, String user) throws IOException, DifyApiException {
        log.debug("停止工作流: taskId={}, user={}", taskId, user);
        Map<String, String> body = new HashMap<>();
        body.put("user", user);
        return executePost(WORKFLOWS_TASKS_PATH + "/" + taskId + STOP_PATH, body, WorkflowStopResponse.class);
    }

    @Override
    public WorkflowRunStatusResponse getWorkflowRun(String workflowId) throws IOException, DifyApiException {
        log.debug("获取工作流执行状态: workflowId={}", workflowId);
        return executeGet(WORKFLOWS_PATH + "/run/" + workflowId, WorkflowRunStatusResponse.class);
    }

    @Override
    public WorkflowLogsResponse getWorkflowLogs(String keyword, String status, Integer page, Integer limit) throws IOException, DifyApiException {
        log.debug("获取工作流日志: keyword={}, status={}, page={}, limit={}", keyword, status, page, limit);
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("status", status);
        params.put("page", page);
        params.put("limit", limit);

        String url = buildUrlWithParams(WORKFLOWS_LOGS_PATH, params);
        Request request = createGetRequest(url);
        return executeRequest(request, WorkflowLogsResponse.class);
    }

    /**
     * 执行流式请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @param lineProcessor 行处理器，返回false表示停止处理
     * @param errorHandler 错误处理器
     */
    private void executeStreamRequest(String path, Object body, LineProcessor lineProcessor, Consumer<Exception> errorHandler) {
        // 创建请求
        RequestBody requestBody = createJsonRequestBody(body);
        Request httpRequest = new Request.Builder().url(baseUrl + path).post(requestBody).header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json").header("Accept", "text/event-stream").build();

        // 执行请求并处理流式响应
        Call call = httpClient.newCall(httpRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("流式请求失败: {}", e.getMessage());
                errorHandler.accept(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    try {
                        String errorBody = response.body() != null ? response.body().string() : "";
                        DifyApiException exception = createApiException(response.code(), errorBody);
                        log.error("流式请求失败: {}", exception.getMessage());
                        errorHandler.accept(exception);
                    } catch (IOException e) {
                        log.error("读取错误响应失败", e);
                        errorHandler.accept(e);
                    }
                    return;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        IOException exception = new IOException("空响应体");
                        log.error("流式请求失败: {}", exception.getMessage());
                        errorHandler.accept(exception);
                        return;
                    }

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                continue;
                            }

                            // 处理行，如果返回false则停止处理
                            if (!lineProcessor.process(line)) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("处理流式响应失败: {}", e.getMessage(), e);
                    errorHandler.accept(e);
                }
            }
        });
    }

    /**
     * 行处理器接口
     */
    @FunctionalInterface
    private interface LineProcessor {
        /**
         * 处理一行数据
         *
         * @param line 行数据
         * @return 是否继续处理
         */
        boolean process(String line);
    }

    /**
     * 处理流式数据行
     *
     * @param line 数据行
     * @param callback 回调接口
     * @param eventProcessor 事件处理器
     * @return 是否继续处理
     */
    private boolean processStreamLine(String line, BaseStreamCallback callback, EventProcessor eventProcessor) {
        if (line.startsWith(DATA_PREFIX)) {
            String data = line.substring(DATA_PREFIX.length()).trim();

            try {
                // 解析事件类型
                BaseEvent baseEvent = JsonUtils.fromJson(data, BaseEvent.class);
                if (baseEvent == null) {
                    log.warn("解析事件数据为null: {}", data);
                    return true; // 继续处理
                }

                // 处理事件
                eventProcessor.process(data, baseEvent.getEvent());
            } catch (Exception e) {
                log.error("解析事件数据失败: {}", data, e);
                callback.onException(e);
            }
        } else if (PING_EVENT.equalsIgnoreCase(line)) {
            // 心跳事件与 Dify API 文档中描述不一致，返回的不是data开头
            PingEvent pingEvent = new PingEvent();
            pingEvent.setEvent(EventType.PING.getValue());
            callback.onPing(pingEvent);
        }
        return true; // 继续处理
    }

    /**
     * 事件处理器接口
     */
    @FunctionalInterface
    private interface EventProcessor {
        /**
         * 处理事件
         *
         * @param data 事件数据
         * @param eventType 事件类型
         */
        void process(String data, String eventType);
    }
}
