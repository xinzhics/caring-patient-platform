package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 统计图表 Y 轴数据
 */
public class TenantStatisticsYData {

    /**
     * Y轴数据名称
     */
    @ApiModelProperty("数据名称")
    private String name;


    @ApiModelProperty("Y轴的数据集合")
    private List<Integer> yData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getyData() {
        return yData;
    }

    public void setyData(List<Integer> yData) {
        this.yData = yData;
    }
}
