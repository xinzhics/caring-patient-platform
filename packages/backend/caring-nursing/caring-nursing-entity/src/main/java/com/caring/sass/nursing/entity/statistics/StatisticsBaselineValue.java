package com.caring.sass.nursing.entity.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName StatisticsBaselineValue
 * @Description
 * @Author yangShuai
 * @Date 2022/4/18 10:44
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_form_statistics_baseline_value")
@ApiModel(value = "StatisticsBaselineValue", description = "基线值统计图")
@AllArgsConstructor
public class StatisticsBaselineValue extends Entity<Long> {



    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "统计任务ID")
    @TableField(value = "statistics_task_id", condition = EQUAL)
    @Excel(name = "统计任务ID")
    private Long statisticsTaskId;

    @ApiModelProperty(value = "表单字段名称")
    @TableField(value = "form_field_label", condition = EQUAL)
    @Excel(name = "表单字段名称")
    private String formFieldLabel;

    @ApiModelProperty(value = "统计图顺序")
    @TableField(value = "chart_order", condition = EQUAL)
    @Excel(name = "统计图顺序")
    private Integer chartOrder;

    @ApiModelProperty(value = "统计图宽度")
    @TableField(value = "chart_width", condition = EQUAL)
    @Excel(name = "统计图宽度")
    private Integer chartWidth;

    @ApiModelProperty(value = "显示或隐藏")
    @TableField(value = "show_or_hide", condition = EQUAL)
    @Excel(name = "显示或隐藏")
    private String showOrHide;




}
