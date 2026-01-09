package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName MonitorLineChart
 * @Description
 * @Author yangShuai
 * @Date 2022/2/15 11:26
 * @Version 1.0
 */
@ApiModel("监测数据折线图数据")
@Data
public class MonitorLineChart {


    @ApiModelProperty(value = "单日曲线，1 是， null 0 否")
    private Integer oneDayCurve;

    @ApiModelProperty(value = "是否显示趋势图")
    private Boolean showTrend;

    @ApiModelProperty("y轴的数据")
    private List<MonitorYData> yDataList;

    @ApiModelProperty("监控事件的数据")
    private List<MonitorYData> yMonitorEvent;

    @ApiModelProperty("x轴的数据")
    private List<LocalDateTime> xDataList;




}
