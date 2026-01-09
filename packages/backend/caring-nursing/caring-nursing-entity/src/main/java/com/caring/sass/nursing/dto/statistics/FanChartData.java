package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 统计图表 Y 轴数据
 */
@Data
public class FanChartData {

    /**
     * Y轴数据名称
     */
    @ApiModelProperty("区间名称")
    private String name;

    @ApiModelProperty(value = "最大值")
    private double maxValue;

    @ApiModelProperty(value = "最小值")
    private double minValue;

    @ApiModelProperty(value = "颜色")
    private String color;

    @ApiModelProperty("占比")
    private int proportion;

    @ApiModelProperty("统计人数")
    private int total;

}
