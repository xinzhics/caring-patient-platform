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
@ApiModel(value = "warningTypeEnum", description = "预警类型 (1 余药不足， 2 购药逾期)")
public enum WarningTypeEnum {
    /**
     * 余药不足
     */
    NOT_DRUGS(1, "余药不足"),

    /**
     * 购药逾期
     */
    BO_DRUGS(2, "购药逾期");

    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static WarningTypeEnum getPlanEnum(Integer code) {
        if (code == null) {
            return NOT_DRUGS;
        }
        for (WarningTypeEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
