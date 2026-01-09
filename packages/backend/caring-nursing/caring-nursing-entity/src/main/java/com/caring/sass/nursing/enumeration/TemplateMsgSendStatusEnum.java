package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "TemplateMsgSendStatusEnum", description = "模板消息发送状态 (1 未发送， 2 已发送)")
public enum TemplateMsgSendStatusEnum {
    /**
     * 余药不足
     */
    UN_SEND(1, "未发送"),

    /**
     * 购药逾期
     */
    AL_SEND(2, "已发送");

    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static TemplateMsgSendStatusEnum getPlanEnum(Integer code) {
        if (code == null) {
            return UN_SEND;
        }
        for (TemplateMsgSendStatusEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
