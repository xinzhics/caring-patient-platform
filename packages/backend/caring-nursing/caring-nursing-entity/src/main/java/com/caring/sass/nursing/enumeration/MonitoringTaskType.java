package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName MonitoringTaskType 1|2|3  待执行任务|执行任务中|已执行任务
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 13:31
 * @Version 1.0
 */
public enum MonitoringTaskType {

    /**
     * 待执行任务
     */
    WAIT("1","待执行任务"),

    /**
     * 执行任务中
     */
    RUNING("2","执行任务中"),

    /**
     * 已执行任务
     */
    FINISH("3","已执行任务");

    @ApiModelProperty(value = "操作类型")
    public String operateType;

    @ApiModelProperty(value = "操作类型名称")
    public String operateTypeName;


    MonitoringTaskType(String operateType, String operateTypeName) {
        this.operateType = operateType;
        this.operateTypeName = operateTypeName;
    }
}
