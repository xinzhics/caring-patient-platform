package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModelProperty;

/**
 * 患者 添加的到药箱中的药品是否需要 用药预警 监听
 */
public enum EarlyWarningMonitorEnum {

    CAN_MONITOR(1, "可监听"),

    NO_NEED_MONITOR(2, "不需要监听");

    @ApiModelProperty(value = "类型")
    public Integer code;

    @ApiModelProperty(value = "描述")
    public String desc;


    EarlyWarningMonitorEnum(Integer code, String desc) {
        this.desc = desc;
        this.code = code;
    }
}
