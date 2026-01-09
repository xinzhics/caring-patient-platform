package com.caring.sass.msgs.entity;

import lombok.Data;

/**
 * openAi请求参数
 *
 * @author leizhi
 */
@Data
public class OpenAiChatRequest {

    /**
     * 默认的催眠词
     */
    private String greeting;

    /**
     * 对话
     */
    private String message;

    /**
     * 提问人标识
     */
    private String user;


}
