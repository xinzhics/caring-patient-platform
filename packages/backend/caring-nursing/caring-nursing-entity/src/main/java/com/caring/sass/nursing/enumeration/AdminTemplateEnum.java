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
@ApiModel(value = "AdminTemplateEnum", description = "系统或其他计划  0：系统计划   1：其他计划")
public enum AdminTemplateEnum {
    /**
     * 系统计划
     */
    SYSTEM_PLAN(0, "系统计划"),

    /**
     * 系统计划
     */
    OTHER_PLAN(1, "其他计划"),
    ;

    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static AdminTemplateEnum getPlanEnum(Integer code) {
        if (code == null) {
            return SYSTEM_PLAN;
        }
        for (AdminTemplateEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
