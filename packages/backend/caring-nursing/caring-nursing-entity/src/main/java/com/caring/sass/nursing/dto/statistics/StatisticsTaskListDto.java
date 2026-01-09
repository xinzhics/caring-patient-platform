package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName StatisticsTaskListDto
 * @Description
 * @Author yangShuai
 * @Date 2022/4/20 13:49
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "StatisticsTaskListDto", description = "统计任务列表")
@AllArgsConstructor
public class StatisticsTaskListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty(value = "统计名称")
    private String statisticsName;


    @ApiModelProperty(value = "统计图顺序")
    private Integer chartOrder;

    @ApiModelProperty(value = "统计图宽度")
    private Integer chartWidth;

    @ApiModelProperty(value = "显示或隐藏")
    private String showOrHide;

    @ApiModelProperty(value = "统计图归属 user_profile: 用户概要， diagnosis_type： 诊断类型， return_rate: 复诊率， customize: 自定义 ")
    private String belongType;

    @ApiModelProperty("自定义 统计任务下的统计图表")
    private List<ChartInfo> chartInfoList;



}
