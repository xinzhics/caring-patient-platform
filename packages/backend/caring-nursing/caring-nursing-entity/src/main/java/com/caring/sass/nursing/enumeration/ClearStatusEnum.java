package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author xinzh
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "ClearStatusEnum", description = "清理状态 (1 未清理， 2 已清理)")
public enum ClearStatusEnum {
    /**
     * 未清理
     */
    UN_CLEAR(1, "未清理"),

    /**
     * 已清理
     */
    AL_CLEAR(2, "已清理");

    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static ClearStatusEnum getPlanEnum(Integer code) {
        if (code == null) {
            return UN_CLEAR;
        }
        for (ClearStatusEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
