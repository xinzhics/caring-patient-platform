package com.caring.sass.common.difyclient.impl;

import com.caring.sass.common.difyclient.exception.DifyApiException;
import com.caring.sass.common.difyclient.util.HttpClientUtils;
import com.caring.sass.common.difyclient.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Dify API 客户端抽象基类
 * 提供通用的 HTTP 请求处理方法
 */
@Slf4j
public abstract class AbstractDifyClient {
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");
    protected static final MediaType AUDIO = MediaType.parse("audio/*");

    protected final OkHttpClient httpClient;
    protected final String baseUrl;
    protected final String apiKey;

    /**
     * 构造函数
     *
     * @param baseUrl API基础URL
     * @param apiKey  API密钥
     */
    public AbstractDifyClient(String baseUrl, String apiKey) {
        this(baseUrl, apiKey, HttpClientUtils.createDefaultClient());
    }

    /**
     * 构造函数
     *
     * @param baseUrl    API基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     */
    public AbstractDifyClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    /**
     * 执行GET请求
     *
     * @param path 请求路径
     * @param responseClass 响应类型
     * @param <T> 响应类型
     * @return 响应对象
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    protected <T> T executeGet(String path, Class<T> responseClass) throws IOException, DifyApiException {
        Request request = createGetRequest(path);
        return executeRequest(request, responseClass);
    }

    /**
     * 执行POST请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @param responseClass 响应类型
     * @param <T> 响应类型
     * @return 响应对象
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    protected <T> T executePost(String path, Object body, Class<T> responseClass) throws IOException, DifyApiException {
        RequestBody requestBody = createJsonRequestBody(body);
        Request request = createPostRequest(path, requestBody);
        return executeRequest(request, responseClass);
    }

    /**
     * 执行Patch请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @param responseClass 响应类型
     * @param <T> 响应类型
     * @return 响应对象
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    protected <T> T executePatch(String path, Object body, Class<T> responseClass) throws IOException, DifyApiException {
        RequestBody requestBody = createJsonRequestBody(body);
        Request request = createPatchRequest(path, requestBody);
        return executeRequest(request, responseClass);
    }

    /**
     * 执行DELETE请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @param responseClass 响应类型
     * @param <T> 响应类型
     * @return 响应对象
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    protected <T> T executeDelete(String path, Object body, Class<T> responseClass) throws IOException, DifyApiException {
        RequestBody requestBody = createJsonRequestBody(body);
        Request request = createDeleteRequest(path, requestBody);
        return executeRequest(request, responseClass);
    }

    /**
     * 执行请求并处理响应
     *
     * @param request 请求对象
     * @param responseClass 响应类型
     * @param <T> 响应类型
     * @return 响应对象
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    protected <T> T executeRequest(Request request, Class<T> responseClass) throws IOException, DifyApiException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw createApiException(response.code(), errorBody);
            }

            String responseBody = Objects.requireNonNull(response.body()).string();
            return JsonUtils.fromJson(responseBody, responseClass);
        }
    }

    /**
     * 处理HTTP响应
     *
     * @param response HTTP响应
     * @param clazz    目标类型
     * @param <T>      目标类型
     * @return 响应对象
     * @throws IOException      IO异常
     * @throws DifyApiException API异常
     */
    protected <T> T handleResponse(Response response, Class<T> clazz) throws IOException, DifyApiException {
        if (!response.isSuccessful()) {
            String errorBody = response.body() != null ? response.body().string() : "";
            log.error("API请求失败: {}, 状态码: {}, 错误信息: {}", response.request().url(), response.code(), errorBody);
            throw new DifyApiException(response.code(), "api_error", errorBody);
        }

        if (response.body() == null) {
            return null;
        }

        String responseBody = response.body().string();
        return JsonUtils.fromJson(responseBody, clazz);
    }

    /**
     * 执行请求并返回字节数组
     *
     * @param request 请求对象
     * @return 字节数组
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    protected byte[] executeRequestForBytes(Request request) throws IOException, DifyApiException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw createApiException(response.code(), errorBody);
            }

            return Objects.requireNonNull(response.body()).bytes();
        }
    }

    /**
     * 创建GET请求
     *
     * @param path 请求路径
     * @return 请求对象
     */
    protected Request createGetRequest(String path) {
        return new Request.Builder()
                .url(baseUrl + path)
                .get()
                .header("Authorization", "Bearer " + apiKey)
                .build();
    }

    /**
     * 创建POST请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @return 请求对象
     */
    protected Request createPostRequest(String path, RequestBody body) {
        return new Request.Builder()
                .url(baseUrl + path)
                .post(body)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .build();
    }

    /**
     * 创建PATCH请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @return 请求对象
     */
    protected Request createPatchRequest(String path, RequestBody body) {
        return new Request.Builder()
                .url(baseUrl + path)
                .patch(body)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .build();
    }

    /**
     * 创建DELETE请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @return 请求对象
     */
    protected Request createDeleteRequest(String path, RequestBody body) {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl + path)
                .delete()
                .header("Authorization", "Bearer " + apiKey);

        if (body != null) {
            builder.delete(body).header("Content-Type", "application/json");
        }

        return builder.build();
    }

    /**
     * 创建JSON请求体
     *
     * @param body 请求体对象
     * @return 请求体
     */
    protected RequestBody createJsonRequestBody(Object body) {
        if (body == null) {
            return null;
        }
        return RequestBody.create(JSON, JsonUtils.toJson(body));
    }

    /**
     * 创建API异常
     *
     * @param code HTTP状态码
     * @param message 错误消息
     * @return API异常
     */
    protected DifyApiException createApiException(int code, String message) {
        String errorCode = "unknown_error";
        String errorMessage = message;

        try {
            // 尝试解析错误响应体为JSON
            if (message != null && !message.isEmpty() && JsonUtils.isValidJson(message)) {
                Map<String, Object> errorJson = JsonUtils.fromJson(message, Map.class);
                if (errorJson != null) {
                    if (errorJson.containsKey("error_code")) {
                        errorCode = (String) errorJson.get("error_code");
                    } else if (errorJson.containsKey("code")) {
                        errorCode = String.valueOf(errorJson.get("code"));
                    }

                    if (errorJson.containsKey("error_message")) {
                        errorMessage = (String) errorJson.get("error_message");
                    } else if (errorJson.containsKey("message")) {
                        errorMessage = (String) errorJson.get("message");
                    }

                    if (errorJson.containsKey("params")) {
                        errorMessage += " 【" + errorJson.get("params") + "】";
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析错误响应体失败: {}", message, e);
        }

        return new DifyApiException(code, errorCode, errorMessage);
    }

    /**
     * 构建URL查询参数
     *
     * @param path 请求路径
     * @param params 参数映射
     * @return 完整URL
     */
    protected String buildUrlWithParams(String path, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return path;
        }

        StringBuilder urlBuilder = new StringBuilder(path);
        boolean isFirstParam = true;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                urlBuilder.append(isFirstParam ? "?" : "&")
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
                isFirstParam = false;
            }
        }

        return urlBuilder.toString();
    }

    /**
     * 添加非空字符串参数
     *
     * @param params 参数映射
     * @param key 键
     * @param value 值
     */
    protected void addIfNotEmpty(Map<String, Object> params, String key, String value) {
        if (value != null && !value.isEmpty()) {
            params.put(key, value);
        }
    }

    /**
     * 添加非空参数
     *
     * @param params 参数映射
     * @param key 键
     * @param value 值
     */
    protected void addIfNotNull(Map<String, Object> params, String key, Object value) {
        if (value != null) {
            params.put(key, value);
        }
    }
}
