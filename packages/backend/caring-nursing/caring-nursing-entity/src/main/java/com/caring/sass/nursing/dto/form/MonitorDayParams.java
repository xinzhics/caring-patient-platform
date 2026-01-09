package com.caring.sass.nursing.dto.form;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 监控指标
 */
@Data
@ApiModel("监测数据趋势图参数")
public class MonitorDayParams {

    @ApiModelProperty(value = "此字段为 1时，默认根据表单设置，查询数据。" +
            " 如单日曲线开启，默认查询今日、关闭则默认查询30天。")
    private Integer defaultQuery;

    @NotNull
    @ApiModelProperty(value = "时间范围枚举")
    private MonitorDayType monitorDayType;

    @ApiModelProperty(value = "单日曲线查询时间")
    private LocalDate oneDayDate;

    @ApiModelProperty(value = "自定义的开始时间")
    private LocalDate customizeStartDate;

    @ApiModelProperty(value = "自定义的结束时间")
    private LocalDate customizeEndDate;

}
