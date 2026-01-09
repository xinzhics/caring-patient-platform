package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName NursingPlanUpdatePatientDTO
 * @Description
 * @Author yangShuai
 * @Date 2021/9/1 14:09
 * @Version 1.0
 */
@Data
public class NursingPlanUpdatePatientDTO {


    private Long id;

    @ApiModelProperty(value = "随访计划完成次数")
    private Integer examineCount;

    @ApiModelProperty(value = "随访计划开始时间")
    private LocalDateTime nursingTime;

    @ApiModelProperty(value = "入组时间")
    private LocalDateTime completeEnterGroupTime;

}
