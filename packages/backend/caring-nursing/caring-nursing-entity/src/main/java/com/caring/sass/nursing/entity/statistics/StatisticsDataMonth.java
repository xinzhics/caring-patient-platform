package com.caring.sass.nursing.entity.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName StatisticsData
 * @Description
 * @Author yangShuai
 * @Date 2022/4/18 13:27
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_form_statistics_data_month")
@ApiModel(value = "StatisticsDataMonth", description = "统计任务月度数据收集表")
@AllArgsConstructor
public class StatisticsDataMonth extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "统计任务ID")
    @TableField(value = "statistics_task_id", condition = EQUAL)
    @Excel(name = "统计任务ID")
    private Long statisticsTaskId;

    @ApiModelProperty(value = "表单的ID")
    @TableField(value = "form_id", condition = EQUAL)
    @Excel(name = "表单的ID")
    private Long formId;

    @ApiModelProperty(value = "表单结果的ID")
    @TableField(value = "form_result_id", condition = EQUAL)
    @Excel(name = "表单结果的ID")
    private Long formResultId;

    @ApiModelProperty(value = "字段的ID")
    @TableField(value = "form_field_id", condition = EQUAL)
    @Excel(name = "字段的ID")
    private String formFieldId;

    @ApiModelProperty(value = "所属机构ID")
    @TableField(value = "organ_id")
    @Excel(name = "所属机构ID")
    private Long organId;

    @ApiModelProperty(value = "所属机构代码")
    @TableField(value = "class_code", condition = EQUAL)
    @Excel(name = "所属机构代码")
    private String classCode;

    @ApiModelProperty(value = "患者ID")
    @TableField(value = "patient_id", condition = EQUAL)
    @Excel(name = "患者ID")
    private Long patientId;

    @ApiModelProperty(value = "医生ID")
    @TableField(value = "doctor_id", condition = EQUAL)
    @Excel(name = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "专员ID")
    @TableField(value = "nursing_id", condition = EQUAL)
    @Excel(name = "专员ID")
    private Long nursingId;

    @ApiModelProperty(value = "年份")
    @TableField(value = "submit_year", condition = EQUAL)
    @Excel(name = "年份")
    private String submitYear;

    @ApiModelProperty(value = "月份")
    @TableField(value = "submit_month", condition = EQUAL)
    @Excel(name = "月份")
    private String submitMonth;

    @ApiModelProperty(value = "提交日期")
    @TableField(value = "submit_date", condition = EQUAL)
    @Excel(name = "提交日期")
    private LocalDate submitDate;

    @ApiModelProperty(value = "字段类型")
    @TableField(value = "widget_type", condition = EQUAL)
    @Excel(name = "字段类型")
    private String widgetType;

    @ApiModelProperty(value = "值")
    @TableField(value = "submit_value", condition = EQUAL)
    @Excel(name = "值")
    private String submitValue;


    @ApiModelProperty(value = "目标值")
    @TableField(value = "target_value", condition = EQUAL)
    @Excel(name = "目标值")
    private Double targetValue;


    @ApiModelProperty(value = "是否达标")
    @TableField(value = "reach_the_standard", condition = EQUAL)
    @Excel(name = "是否达标")
    private Integer reachTheStandard;

}
