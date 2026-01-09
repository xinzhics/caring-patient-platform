package com.caring.sass.user.constant;

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
@ApiModel(value = "qrDoctorTypeEnum", description = "二维码医生类型 (1 推荐人原医生， 2 指定医生)")
public enum QrDoctorTypeEnum {
    /**
     * 推荐人原医生
     */
    SOURCE_DOCTOR(1, "推荐人原医生"),

    /**
     * 指定医生
     */
    APPOINT_DOCTOR(2, "指定医生");

    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static QrDoctorTypeEnum getPlanEnum(Integer code) {
        if (code == null) {
            return SOURCE_DOCTOR;
        }
        for (QrDoctorTypeEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
