package com.caring.sass.user.constant;

/**
 * @ClassName NotificationSendStatus
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 10:21
 * @Version 1.0
 **/
public enum NotificationSendStatus {

    /**
     * 等待发送
     */
    WAIT_SEND,

    /**
     * 发送中
     */
    PREPARE_SEND,

    /**
     * 发送中
     */
    SENDING,

    /**
     * 结束
     */
    FINISH,

    /**
     * 失败
     */
    ERROR
}
