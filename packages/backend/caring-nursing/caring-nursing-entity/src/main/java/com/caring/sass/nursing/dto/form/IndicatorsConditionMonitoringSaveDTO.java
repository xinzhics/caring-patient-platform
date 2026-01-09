package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者管理-监测数据-监控指标条件表
 * </p>
 *
 * @author yangshuai
 * @since 2022-06-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "IndicatorsConditionMonitoringSaveDTO", description = "患者管理-监测数据-监控指标条件表")
public class IndicatorsConditionMonitoringSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "患者管理-监测数据-监控指标条件表ID 更新时候传入")
    private Long id;
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;
    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID",required = true)
    @NotNull(message = "计划ID不能为空")
    private Long planId;
    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称",required = true)
    @Length(max = 200, message = "计划名称长度不能超过200")
    @NotBlank(message = "计划名称不能为空")
    private String planName;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID",required = true)
    @NotNull(message = "表单ID不能为空")
    private Long formId;
    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID",required = true)
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    @NotBlank(message = "表单中字段ID不能为空")
    private String fieldId;
    /**
     * 表单字段
     */
    @ApiModelProperty(value = "表单字段",required = true)
    @Length(max = 300, message = "表单字段长度不能超过300")
    @NotBlank(message = "表单字段不能为空")
    private String fieldLabel;
    /**
     * 最大值条件符号
     */
    @ApiModelProperty(value = "最大值条件符号  1-小于 2-小于等于 3-等于")
    @Length(max = 40, message = "最大值条件符号长度不能超过40")
    private String maxConditionSymbol;
    /**
     * 最大值条件值
     */
    @ApiModelProperty(value = "最大值条件值")
    @Length(max = 40, message = "最大值条件值长度不能超过40")
    private String maxConditionValue;
    /**
     * 最小值条件符号
     */
    @ApiModelProperty(value = "最小值条件符号")
    @Length(max = 40, message = "最小值条件符号长度不能超过40")
    private String minConditionSymbol;
    /**
     * 最小值条件值
     */
    @ApiModelProperty(value = "最小值条件值")
    @Length(max = 40, message = "最小值条件值长度不能超过40")
    private String minConditionValue;

}
