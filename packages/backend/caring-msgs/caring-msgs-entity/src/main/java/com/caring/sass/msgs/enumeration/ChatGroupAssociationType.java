package com.caring.sass.msgs.enumeration;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @className: ChatGroupAssociationType
 * @author: 杨帅
 * @date: 2024/1/10
 */
@Getter
@ApiModel(value = "ChatGroupAssociationType", description = "群发消息接收对象类型")
public enum ChatGroupAssociationType {

    ALL,
    /**
     * 疾病
     */
    DISEASE,

    /**
     * 自定义分组
     */
    CUSTOM_GROUP,

    /**
     * 患者
     */
    PATIENT
    ;


}
