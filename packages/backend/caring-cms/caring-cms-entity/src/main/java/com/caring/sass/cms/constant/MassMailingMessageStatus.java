package com.caring.sass.cms.constant;

/**
 * @ClassName MassMailingMessageStatus
 * @Description
 * @Author yangShuai
 * @Date 2021/11/22 16:23
 * @Version 1.0
 **/
public enum MassMailingMessageStatus {

    /**
     * 等待发送
     */
    wait_send,

    /**
     * 提交中
     */
    submitting,

    /**
     * 发送中
     */
    sending,

    /**
     * 发送成功
     */
    sent_success,

    /**
     * 发送失败
     */
    sent_error;


}
