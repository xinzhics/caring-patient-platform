package com.caring.sass.nursing.dto.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@ApiModel(value = "StatisticsTaskSaveDTO", description = "统计任务")
@AllArgsConstructor
public class StatisticsTaskSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "随访计划ID")
    private Long planId;

    @ApiModelProperty(value = "表单ID")
    private Long formId;

    @ApiModelProperty(value = "统计名称")
    private String formFieldId;

    @ApiModelProperty(value = "表单字段名称")
    private String formFieldLabel;

    @ApiModelProperty(value = "表单字段单位")
    private String formFieldUnit;

    @ApiModelProperty(value = "任务配置完成的步骤  step_first  第一步")
    private String step = StatisticsStatus.STEP_FIRST;

    @ApiModelProperty("租户")
    @NotEmpty
    private String tenantCode;

}
