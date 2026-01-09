package com.caring.sass.nursing.dto.appointment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName AppointDayCountVo
 * @Description
 * @Author yangShuai
 * @Date 2021/1/27 15:59
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("每日预约详细统计VO")
public class AppointDayCountVo {

    @ApiModelProperty(value = "所属周次")
    private String weekName;

    @ApiModelProperty(value = "预约总数")
    private Integer appointmentTotal;

    @ApiModelProperty(value = "早上预约总数")
    private Integer appointmentMorningTotal;

    @ApiModelProperty(value = "下午预约总数")
    private Integer appointmentAfternoonTotal;

    @ApiModelProperty(value = "就诊总数")
    private Integer seeDoctorTotal;

    @ApiModelProperty(value = "早上就诊总数")
    private Integer seeDoctorMorningTotal;

    @ApiModelProperty(value = "下午就诊总数")
    private Integer seeDoctorAfternoonTotal;



}
