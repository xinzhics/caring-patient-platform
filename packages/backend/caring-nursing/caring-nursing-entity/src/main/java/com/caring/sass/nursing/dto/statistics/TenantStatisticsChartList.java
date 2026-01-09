package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName TenantStatisticsChartList
 * @Description
 * @Author yangShuai
 * @Date 2022/5/16 9:46
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "TenantStatisticsChartList", description = "各端项目任务顺序列表")
@AllArgsConstructor
public class TenantStatisticsChartList {

    @ApiModelProperty("统计图所属的任务ID")
    private Long statisticsTaskId;

    @ApiModelProperty(value = "统计图名称")
    private String formFieldLabel;

    @ApiModelProperty(value = "统计图宽度")
    private Integer chartWidth;

    @ApiModelProperty(value = "扇形图 柱状图 是否 显示数量")
    private Boolean showNumber;

    @ApiModelProperty(value = "扇形图 柱状图 是否 显示百分比")
    private Boolean showPercentage;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "统计图顺序")
    private Integer chartOrder;

    @ApiModelProperty(value = "统计图的类型 fanChart: 扇形图  histogram： 柱状图 lineChart : 折线图")
    private String chartType;

    @ApiModelProperty(value = "图表类型 compliance_rate： 达标率， base_line_value： 基线值， master_chart： 主统计图, return_rate : 复诊率统计图")
    private String statisticsDataType;

    public Integer getChartOrder() {
        if (chartOrder == null) {
            return 0;
        } else {
            return chartOrder;
        }
    }
}
