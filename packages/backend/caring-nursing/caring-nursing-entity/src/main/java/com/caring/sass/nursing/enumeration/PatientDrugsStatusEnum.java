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
@ApiModel(value = "PatientDrugsStatusEnum", description = "患者添加的用药状态 0：使用  1：停用（不推送，没有终止）  2：终止用药（历史用药）")
public enum PatientDrugsStatusEnum {
    /**
     * 未处理
     */
    ON(0, "使用"),

    /**
     * 已处理
     */
    SOTP_NO_PUSH(1, "停用（不推送，没有终止）"),

    /**
     * 已处理
     */
    END_HIS(2, "终止用药（历史用药");
    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static PatientDrugsStatusEnum getPlanEnum(Integer code) {
        if (code == null) {
            return ON;
        }
        for (PatientDrugsStatusEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
