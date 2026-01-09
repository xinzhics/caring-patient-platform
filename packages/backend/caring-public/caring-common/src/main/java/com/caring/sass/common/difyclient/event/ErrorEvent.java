package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 错误事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorEvent extends BaseMessageEvent {

    /**
     * HTTP 状态码
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 错误码
     */
    @JsonProperty("code")
    private String code;

    /**
     * 错误消息
     */
    @JsonProperty("message")
    private String message;
}
