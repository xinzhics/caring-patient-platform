package com.caring.sass.common.difyclient.exception;

import lombok.Getter;

import java.io.IOException;

/**
 * Dify API 异常
 */
@Getter
public class DifyApiException extends IOException {
    /**
     * HTTP 状态码
     */
    private final int statusCode;

    /**
     * 错误代码
     */
    private final String errorCode;

    /**
     * 错误消息
     */
    private final String errorMessage;

    /**
     * 构造函数
     *
     * @param statusCode   HTTP 状态码
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public DifyApiException(int statusCode, String errorCode, String errorMessage) {
        super(String.format("API 错误: %s (%d) - %s", errorCode, statusCode, errorMessage));
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
