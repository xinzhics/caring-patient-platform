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
@ApiModel(value = "HandleStatusEnum", description = "异常处理状态 (1 未处理， 2 已处理)")
public enum HandleStatusEnum {
    /**
     * 未处理
     */
    UN_HANDLE(1, "未处理"),

    /**
     * 已处理
     */
    AL_HANDLE(2, "已处理");

    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static HandleStatusEnum getPlanEnum(Integer code) {
        if (code == null) {
            return UN_HANDLE;
        }
        for (HandleStatusEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
