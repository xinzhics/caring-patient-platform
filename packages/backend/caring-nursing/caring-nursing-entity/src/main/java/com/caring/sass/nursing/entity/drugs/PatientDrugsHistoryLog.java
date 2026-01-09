package com.caring.sass.nursing.entity.drugs;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName PatientDrugsHistory
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 11:27
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_patient_drugs_history_log")
@ApiModel(value = "PatientDrugsHistoryLog", description = "患者药箱操作的记录")
@AllArgsConstructor
public class PatientDrugsHistoryLog extends Entity<Long> {

    @ApiModelProperty(value = "患者ID")
    @TableField(value = "patient_id", condition = EQUAL)
    private Long patientId;

    @ApiModelProperty(value = "历史记录日期")
    @TableField(value = "history_date", condition = EQUAL)
    private LocalDate historyDate;

    @ApiModelProperty(value = "操作类型（created 4 ， updated 3 ， stop 2, current 1）")
    @TableField(value = "operate_type_sort", condition = EQUAL)
    private Integer operateTypeSort;

    @ApiModelProperty(value = "操作类型（created， updated， current， stop）")
    @TableField(value = "operate_type", condition = EQUAL)
    private String operateType;

    @ApiModelProperty(value = "药品ID")
    @TableField(value = "drugs_id", condition = EQUAL)
    private Long drugsId;

    @ApiModelProperty(value = "药品名称，冗余字段")
    @TableField(value = "medicine_name", condition = LIKE)
    private String medicineName;

    @ApiModelProperty(value = "当前每天用药次数")
    @TableField("current_number_of_day")
    private Integer currentNumberOfDay;

    @ApiModelProperty(value = "当前每次剂量")
    @TableField("current_dose")
    @Excel(name = "每次剂量")
    private Float currentDose;

    @ApiModelProperty(value = "当前单位")
    @Length(max = 20, message = "单位长度不能超过20")
    @TableField(value = "current_unit", condition = LIKE)
    @Excel(name = "单位")
    private String currentUnit;

    @ApiModelProperty(value = "改前每天用药次数")
    @TableField("before_current_number_of_day")
    private Integer beforeCurrentNumberOfDay;

    @ApiModelProperty(value = "改前每次剂量")
    @TableField("before_current_dose")
    private Float beforeCurrentDose;

    @ApiModelProperty(value = "改前单位")
    @Length(max = 20, message = "单位长度不能超过20")
    @TableField(value = "before_current_unit", condition = LIKE)
    private String beforeCurrentUnit;


    @ApiModelProperty(value = "时间周期：天(day), 小时(hour)，周(week),月(moon)")
    @TableField("before_time_period")
    private PatientDrugsTimePeriodEnum beforeTimePeriod;

    @ApiModelProperty(value = "时间周期：天(day), 小时(hour)，周(week),月(moon)")
    @TableField("current_time_period")
    private PatientDrugsTimePeriodEnum currentTimePeriod;

    @ApiModelProperty(value = "之前周期时长 1到30")
    @TableField("before_cycle_duration")
    private Integer beforeCycleDuration;


    @ApiModelProperty(value = "当前1到30")
    @TableField("current_cycle_duration")
    private Integer currentCycleDuration;

}
