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
 * @ClassName StatisticsInterval
 * @Description
 * @Author yangShuai
 * @Date 2022/4/18 10:48
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_form_statistics_interval")
@ApiModel(value = "StatisticsInterval", description = "统计任务区间设置")
@AllArgsConstructor
public class StatisticsInterval extends Entity<Long> {


    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "统计任务ID")
    @TableField(value = "statistics_task_id", condition = EQUAL)
    @Excel(name = "统计任务ID")
    private Long statisticsTaskId;


    @ApiModelProperty(value = "统计任务区间名称")
    @TableField(value = "statistics_interval_name", condition = EQUAL)
    @Excel(name = "统计任务区间名称")
    private String statisticsIntervalName;

    @ApiModelProperty(value = "最大值")
    @TableField(value = "max_value", condition = EQUAL)
    @Excel(name = "最大值")
    private Double maxValue;

    @ApiModelProperty(value = "最小值")
    @TableField(value = "min_value", condition = EQUAL)
    @Excel(name = "最小值")
    private Double minValue;

    @ApiModelProperty(value = "颜色")
    @TableField(value = "color", condition = EQUAL)
    @Excel(name = "颜色")
    private String color;

}
