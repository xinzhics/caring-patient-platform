package com.caring.sass.nursing.dto.statistics;

import com.caring.sass.nursing.entity.statistics.StatisticsMaster;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 实体类
 * 护理目标
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "StatisticsTaskUpdateDTO", description = "统计任务")
@AllArgsConstructor
public class StatisticsTaskUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "统计任务id")
    private Long id;

    @ApiModelProperty(value = "统计名称")
    @Length(max = 50, message = "统计名称长度不能超过50")
    private String statisticsName;

    @ApiModelProperty(value = "随访计划ID")
    private Long planId;

    @ApiModelProperty(value = "表单ID")
    private Long formId;

    @ApiModelProperty(value = "表单字段ID")
    private String formFieldId;

    @ApiModelProperty(value = "表单字段名称")
    private String formFieldLabel;

    @ApiModelProperty(value = "表单字段单位")
    private String formFieldUnit;

    @ApiModelProperty(value = "显示数量, 控制任务下所有图表的显示")
    private Boolean showNumber;

    @ApiModelProperty(value = "显示百分比, 控制任务下所有图表的显示")
    private Boolean showPercentage;


    @ApiModelProperty(value = "开启达标率趋势图")
    private Boolean complianceRateChart;


    @ApiModelProperty(value = "开启基线值统计图")
    private Boolean baselineValueChart;

    @ApiModelProperty(value = "任务配置完成的步骤 step_second 第二步， step_finish 完成 ")
    private String step;

    @ApiModelProperty(value = "主统计图")
    private StatisticsMaster statisticsMaster;

    @ApiModelProperty(value = "区间设置")
    private List<StatisticsIntervalSaveDTO>  statisticsIntervals;

    @ApiModelProperty("租户")
    @NotEmpty
    private String tenantCode;
}
