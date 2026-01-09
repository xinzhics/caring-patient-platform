package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看全部计划的时的模型
 */
@Data
public class FollowAllPlanDTO {

    @ApiModelProperty(value = "计划最大执行的天数")
    private Integer maxPlanDay;

    @ApiModelProperty(value = "时间范围")
    private String timeFrame;

    @ApiModelProperty("按天来查看执行的计划")
    private List<FollowAllExecutionCycleDTO> planExecutionCycles = new ArrayList<>();


}
