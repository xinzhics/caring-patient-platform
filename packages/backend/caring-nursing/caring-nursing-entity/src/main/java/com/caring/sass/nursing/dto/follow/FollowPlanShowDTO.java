package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FollowPlanShowDTO {

    @ApiModelProperty(value = "计划最大执行的天数")
    private Integer maxPlanDay;

    @ApiModelProperty(value = "0 表示长期计划")
    private Integer planEffectiveTime;

    @ApiModelProperty(value = "时间范围")
    private String timeFrame;

    @ApiModelProperty(value = "计划ID")
    private Long planId;

    @ApiModelProperty("计划的名称")
    private String planName;

    @ApiModelProperty(value = "患者是否订阅 -1 学习计划，不处理。 1 是订阅，0是未订阅")
    private int subscribe = 0;

    @ApiModelProperty("学习计划中推送的文章数")
    private int learnCmsNumber;

    @ApiModelProperty(value = "执行的计划")
    private List<PlanExecutionCycleDTO> planExecutionCycles;

}
