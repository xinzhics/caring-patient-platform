package com.caring.sass.common.difyclient;

/**
 * Dify API 客户端接口
 * 继承所有细分客户端接口，提供完整的功能
 */
public interface DifyClient extends DifyChatClient, DifyChatflowClient, DifyCompletionClient, DifyWorkflowClient {
    // 继承所有细分客户端接口的方法
}
