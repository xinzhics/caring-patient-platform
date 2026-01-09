package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName ChartInfo
 * @Description
 * @Author yangShuai
 * @Date 2022/4/20 13:52
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "ChartInfo", description = "统计任务下的统计图表")
@AllArgsConstructor
public class ChartInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "图表的ID")
    private Long id;

    @ApiModelProperty(value = "表单字段名称")
    private String formFieldLabel;

    @ApiModelProperty(value = "统计图顺序")
    private Integer chartOrder;

    @ApiModelProperty(value = "统计图宽度 50/100")
    private Integer chartWidth;

    @ApiModelProperty(value = "显示或隐藏 show/hide")
    private String showOrHide;

    @ApiModelProperty(value = "图表类型 compliance_rate： 达标率， base_line_value： 基准值， master_chart： 主统计图")
    private String type;

}
