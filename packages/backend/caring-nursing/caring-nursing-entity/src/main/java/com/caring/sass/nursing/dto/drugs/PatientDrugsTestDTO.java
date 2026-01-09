package com.caring.sass.nursing.dto.drugs;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientDrugsTestDTO")
public class PatientDrugsTestDTO {


    @ApiModelProperty("开始吃药的日期")
    private LocalDate startTakeMedicineDate;

    @ApiModelProperty("模拟结束的日期")
    private LocalDate simulationEndTime;

    @ApiModelProperty(value = "时间周期：天(day), 小时(hour)，周(week),月(moon)")
    @TableField("time_period")
    private PatientDrugsTimePeriodEnum timePeriod;

    @ApiModelProperty(value = "1到30")
    @TableField("cycle_duration")
    private Integer cycleDuration;

    @ApiModelProperty(value = "用药提醒的时间")
    private Integer reminderTime;

    @ApiModelProperty(value = "剂量")
    private Float dose;

}
