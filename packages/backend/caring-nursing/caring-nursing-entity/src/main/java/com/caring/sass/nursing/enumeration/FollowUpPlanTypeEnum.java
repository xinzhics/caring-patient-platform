package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName 随访计划类型  护理计划 care_plan， 监测数据 monitoring_data
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 13:31
 * @Version 1.0
 */
public enum FollowUpPlanTypeEnum {

    /**
     * 护理计划
     */
    CARE_PLAN("care_plan","随访计划"),

    /**
     * 监测数据
     */
    MONITORING_DATA("monitoring_data","指标监测");

    @ApiModelProperty(value = "类型")
    public String operateType;

    @ApiModelProperty(value = "类型名称")
    public String operateTypeName;

    FollowUpPlanTypeEnum(String operateType, String operateTypeName) {
        this.operateType = operateType;
        this.operateTypeName = operateTypeName;
    }
}
