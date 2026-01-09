package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName TenantStatisticsResult
 * @Description
 * @Author yangShuai
 * @Date 2022/5/18 11:53
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "TenantStatisticsResult", description = "项目统计数据结果")
@AllArgsConstructor
public class TenantStatisticsResult {

    @ApiModelProperty("折线图X轴数据")
    private List<LocalDate> xData;


    @ApiModelProperty("折线图Y轴数据")
    private List<TenantStatisticsYData> yData;


    @ApiModelProperty("柱状图扇形图数据")
    private List<FanChartData> fanCharts;

}
