package com.caring.sass.nursing.entity.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 护理目标
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_form_statistics_task")
@ApiModel(value = "StatisticsTask", description = "统计任务")
@AllArgsConstructor
public class StatisticsTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "统计名称")
    @Length(max = 50, message = "统计名称长度不能超过50")
    @TableField(value = "statistics_name", condition = LIKE)
    @Excel(name = "统计名称")
    private String statisticsName;

    @ApiModelProperty(value = "随访计划ID")
    @TableField(value = "plan_id", condition = EQUAL)
    @Excel(name = "随访计划ID")
    private Long planId;

    @ApiModelProperty(value = "表单ID")
    @TableField(value = "form_id", condition = EQUAL)
    @Excel(name = "表单ID")
    private Long formId;

    @ApiModelProperty(value = "统计名称")
    @TableField(value = "form_field_id", condition = EQUAL)
    @Excel(name = "表单字段ID")
    private String formFieldId;

    @ApiModelProperty(value = "表单字段名称")
    @TableField(value = "form_field_label", condition = EQUAL)
    @Excel(name = "表单字段名称")
    private String formFieldLabel;

    @ApiModelProperty(value = "表单字段单位")
    @TableField(value = "form_field_unit", condition = EQUAL)
    @Excel(name = "表单字段名称")
    private String formFieldUnit;

    @ApiModelProperty(value = "显示数量, 控制任务下所有图表的显示")
    @TableField(value = "show_number", condition = EQUAL)
    @Excel(name = "显示数量")
    private Boolean showNumber;


    @ApiModelProperty(value = "显示百分比, 控制任务下所有图表的显示")
    @TableField(value = "show_percentage", condition = EQUAL)
    @Excel(name = "显示百分比")
    private Boolean showPercentage;



    @ApiModelProperty(value = "开启达标率趋势图")
    @TableField(value = "compliance_rate_chart", condition = EQUAL)
    @Excel(name = "达标率趋势图")
    private Boolean complianceRateChart;


    @ApiModelProperty(value = "开启基线值统计图")
    @TableField(value = "baseline_value_chart", condition = EQUAL)
    @Excel(name = "基线值统计图")
    private Boolean baselineValueChart;


    @ApiModelProperty(value = "任务配置完成的步骤")
    @TableField(value = "step", condition = EQUAL)
    @Excel(name = "任务配置完成的步骤")
    private String step;

    @ApiModelProperty(value = "任务是否初始化数据")
    @TableField(value = "init_data", condition = EQUAL)
    @Excel(name = "任务是否初始化数据")
    private Boolean initData;


    @ApiModelProperty("主统计图")
    @TableField(exist = false)
    private StatisticsMaster statisticsMaster;

    @ApiModelProperty(value = "区间设置")
    @TableField(exist = false)
    private List<StatisticsInterval>  statisticsIntervals;

}
