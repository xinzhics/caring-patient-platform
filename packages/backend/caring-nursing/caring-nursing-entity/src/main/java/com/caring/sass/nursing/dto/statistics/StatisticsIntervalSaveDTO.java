package com.caring.sass.nursing.dto.statistics;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
@TableName("t_form_statistics_interval")
@ApiModel(value = "StatisticsInterval", description = "统计任务区间设置")
@AllArgsConstructor
public class StatisticsIntervalSaveDTO {


    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "统计任务ID")
    private Long statisticsTaskId;

    @ApiModelProperty(value = "统计任务区间名称")
    private String statisticsIntervalName;

    @ApiModelProperty(value = "最大值")
    private Double maxValue;

    @ApiModelProperty(value = "最小值")
    private Double minValue;

    @ApiModelProperty(value = "颜色")
    private String color;

}
