package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlanExecutionCycleDTO {

    @ApiModelProperty("计划执行的天数")
    int planExecutionDay;

    @ApiModelProperty("计划执行的日期")
    private LocalDate localDate;

    @ApiModelProperty("此天数中执行的计划目录")
    private List<PlanDetailsDTO> planDetails = new ArrayList<>();

    public void addPlanDetails(PlanDetailsDTO planDetailsDTO) {
        planDetails.add(planDetailsDTO);
    }

}
