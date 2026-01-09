package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FollowAllPlanDetailDTO {

    @ApiModelProperty("计划的类型")
    Integer planType;

    @ApiModelProperty(value = "随访计划类型  护理计划 学习计划 care_plan， 监测数据 monitoring_data")
    String followUpPlanType;

    @ApiModelProperty("计划的ID")
    Long planId;

    @ApiModelProperty("计划展示的第一标题 （计划的名称）")
    String firstShowTitle;

    @ApiModelProperty("计划执行的时间")
    LocalTime planExecutionDate;

    @ApiModelProperty("此计划在这一天内执行的多个任务")
    List<FollowAllPlanDetailTimeDTO> planDetailTimeDTOS = new ArrayList<>();

    @ApiModelProperty("计划完成状态 0 未完成， 1 已完成")
    int planFinishStatus = 0;

    public void addPlanDetailTimeDTOS(FollowAllPlanDetailTimeDTO followAllPlanDetailTimeDTO) {
        planDetailTimeDTOS.add(followAllPlanDetailTimeDTO);
    };


    public FollowAllPlanDetailDTO() {
    }

    public FollowAllPlanDetailDTO(Integer planType, Long planId, String firstShowTitle, LocalTime planExecutionDate) {
        this.planType = planType;
        this.planId = planId;
        this.firstShowTitle = firstShowTitle;
        this.planExecutionDate = planExecutionDate;
    }
}
