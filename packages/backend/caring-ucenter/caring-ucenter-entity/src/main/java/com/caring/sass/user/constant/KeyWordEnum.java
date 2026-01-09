package com.caring.sass.user.constant;

/**
 * 关键字模板使用的枚举
 */
public enum KeyWordEnum {

    /**
     * 开关功能开关(open, close)
     */
    open,
    close,

    /**
     * 回复形式(system，medical_assistance 医助)
     */
    system,
    medical_assistance,

    /**
     * 匹配规则
     * full_match: 全匹配， semi_match 半匹配
     */
    semi_match,
    full_match,


    /**
     * 关注后
     */
    Reply_after_following,
    /**
     * 未注册回复
     */
    unregistered_reply,

    /**
     * 回复的消息类型
     */
    text,
    cms
}
