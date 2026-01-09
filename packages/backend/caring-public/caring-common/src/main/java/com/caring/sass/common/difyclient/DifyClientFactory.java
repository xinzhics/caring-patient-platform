package com.caring.sass.common.difyclient;

import com.caring.sass.common.difyclient.impl.DefaultDifyClient;
import com.caring.sass.common.difyclient.impl.DefaultDifyDatasetsClient;
import com.caring.sass.common.difyclient.model.DifyConfig;
import com.caring.sass.common.difyclient.util.HttpClientUtils;
import okhttp3.OkHttpClient;

/**
 * Dify客户端工厂类
 */
public class DifyClientFactory {

    /**
     * 从配置创建HTTP客户端
     *
     * @param config 配置
     * @return HTTP客户端
     */
    private static OkHttpClient createHttpClient(DifyConfig config) {
        return HttpClientUtils.createClient(
                config.getConnectTimeout(),
                config.getReadTimeout(),
                config.getWriteTimeout()
        );
    }

    /**
     * 创建完整的Dify客户端
     *
     * @param baseUrl 基础URL
     * @param apiKey  API密钥
     * @return Dify客户端
     */
    public static DifyClient createClient(String baseUrl, String apiKey) {
        return createClient(baseUrl, apiKey, HttpClientUtils.createDefaultClient());
    }

    /**
     * 创建完整的Dify客户端
     *
     * @param baseUrl    基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     * @return Dify客户端
     */
    public static DifyClient createClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        return new DefaultDifyClient(baseUrl, apiKey, httpClient);
    }

    /**
     * 使用配置创建完整的Dify客户端
     *
     * @param config 配置
     * @return Dify客户端
     */
    public static DifyClient createClient(DifyConfig config) {
        return new DefaultDifyClient(config.getBaseUrl(), config.getApiKey(), createHttpClient(config));
    }

    /**
     * 创建对话型应用客户端
     *
     * @param baseUrl 基础URL
     * @param apiKey  API密钥
     * @return 对话型应用客户端
     */
    public static DifyChatClient createChatClient(String baseUrl, String apiKey) {
        return createClient(baseUrl, apiKey);
    }

    /**
     * 创建对话型应用客户端
     *
     * @param baseUrl    基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     * @return 对话型应用客户端
     */
    public static DifyChatClient createChatClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        return createClient(baseUrl, apiKey, httpClient);
    }

    /**
     * 使用配置创建对话型应用客户端
     *
     * @param config 配置
     * @return 对话型应用客户端
     */
    public static DifyChatClient createChatClient(DifyConfig config) {
        return createClient(config);
    }

    /**
     * 创建工作流编排对话型应用客户端
     *
     * @param baseUrl 基础URL
     * @param apiKey  API密钥
     * @return 工作流编排对话型应用客户端
     */
    public static DifyChatflowClient createChatWorkflowClient(String baseUrl, String apiKey) {
        return createClient(baseUrl, apiKey);
    }

    /**
     * 创建工作流编排对话型应用客户端
     *
     * @param baseUrl    基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     * @return 工作流编排对话型应用客户端
     */
    public static DifyChatflowClient createChatWorkflowClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        return createClient(baseUrl, apiKey, httpClient);
    }

    /**
     * 使用配置创建工作流编排对话型应用客户端
     *
     * @param config 配置
     * @return 工作流编排对话型应用客户端
     */
    public static DifyChatflowClient createChatWorkflowClient(DifyConfig config) {
        return createClient(config);
    }

    /**
     * 创建文本生成型应用客户端
     *
     * @param baseUrl 基础URL
     * @param apiKey  API密钥
     * @return 文本生成型应用客户端
     */
    public static DifyCompletionClient createCompletionClient(String baseUrl, String apiKey) {
        return createClient(baseUrl, apiKey);
    }

    /**
     * 创建文本生成型应用客户端
     *
     * @param baseUrl    基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     * @return 文本生成型应用客户端
     */
    public static DifyCompletionClient createCompletionClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        return createClient(baseUrl, apiKey, httpClient);
    }

    /**
     * 使用配置创建文本生成型应用客户端
     *
     * @param config 配置
     * @return 文本生成型应用客户端
     */
    public static DifyCompletionClient createCompletionClient(DifyConfig config) {
        return createClient(config);
    }

    /**
     * 创建Workflow应用客户端
     *
     * @param baseUrl 基础URL
     * @param apiKey  API密钥
     * @return Workflow应用客户端
     */
    public static DifyWorkflowClient createWorkflowClient(String baseUrl, String apiKey) {
        return createClient(baseUrl, apiKey);
    }

    /**
     * 创建Workflow应用客户端
     *
     * @param baseUrl    基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     * @return Workflow应用客户端
     */
    public static DifyWorkflowClient createWorkflowClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        return createClient(baseUrl, apiKey, httpClient);
    }

    /**
     * 使用配置创建Workflow应用客户端
     *
     * @param config 配置
     * @return Workflow应用客户端
     */
    public static DifyWorkflowClient createWorkflowClient(DifyConfig config) {
        return createClient(config);
    }

    /**
     * 创建知识库客户端
     *
     * @param baseUrl 基础URL
     * @param apiKey  API密钥
     * @return 知识库客户端
     */
    public static DifyDatasetsClient createDatasetsClient(String baseUrl, String apiKey) {
        return createDatasetsClient(baseUrl, apiKey, HttpClientUtils.createDefaultClient());
    }

    /**
     * 创建知识库客户端
     *
     * @param baseUrl    基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     * @return 知识库客户端
     */
    public static DifyDatasetsClient createDatasetsClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        return new DefaultDifyDatasetsClient(baseUrl, apiKey, httpClient);
    }

    /**
     * 使用配置创建知识库客户端
     *
     * @param config 配置
     * @return 知识库客户端
     */
    public static DifyDatasetsClient createDatasetsClient(DifyConfig config) {
        return new DefaultDifyDatasetsClient(config.getBaseUrl(), config.getApiKey(), createHttpClient(config));
    }
}
