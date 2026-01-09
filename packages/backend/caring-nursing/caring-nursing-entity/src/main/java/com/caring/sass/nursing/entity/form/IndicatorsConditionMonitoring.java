package com.caring.sass.nursing.entity.form;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_indicators_condition_monitoring")
@ApiModel(value = "IndicatorsConditionMonitoring", description = "患者管理-监测数据-监控指标条件表")
@AllArgsConstructor
public class IndicatorsConditionMonitoring extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    @TableField("plan_id")
    @Excel(name = "计划ID")
    private Long planId;

    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    @Length(max = 200, message = "计划名称长度不能超过200")
    @TableField(value = "plan_name", condition = LIKE)
    @Excel(name = "计划名称")
    private String planName;

    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID")
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    @TableField(value = "field_id", condition = LIKE)
    @Excel(name = "表单中字段ID")
    private String fieldId;

    /**
     * 表单字段
     */
    @ApiModelProperty(value = "表单字段")
    @Length(max = 300, message = "表单字段长度不能超过300")
    @TableField(value = "field_label", condition = LIKE)
    @Excel(name = "表单字段")
    private String fieldLabel;

    /**
     * 最大值条件符号
     */
    @ApiModelProperty(value = "最大值条件符号  1-小于 2-小于等于 3-等于")
    @Length(max = 40, message = "最大值条件符号长度不能超过40")
    @TableField(value = "max_condition_symbol", condition = LIKE)
    @Excel(name = "最大值条件符号")
    private String maxConditionSymbol;

    /**
     * 最大值条件值
     */
    @ApiModelProperty(value = "最大值条件值")
    @Length(max = 40, message = "最大值条件值长度不能超过40")
    @TableField(value = "max_condition_value", condition = LIKE)
    @Excel(name = "最大值条件值")
    private String maxConditionValue;

    /**
     * 最小值条件符号
     */
    @ApiModelProperty(value = "最小值条件符号  1-小于 2-小于等于 3-等于")
    @Length(max = 40, message = "最小值条件符号长度不能超过40")
    @TableField(value = "min_condition_symbol", condition = LIKE)
    @Excel(name = "最小值条件符号")
    private String minConditionSymbol;

    /**
     * 最小值条件值
     */
    @ApiModelProperty(value = "最小值条件值")
    @Length(max = 40, message = "最小值条件值长度不能超过40")
    @TableField(value = "min_condition_value", condition = LIKE)
    @Excel(name = "最小值条件值")
    private String minConditionValue;
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码")
    @TableField("tenant_code")
    @Excel(name = "租户编码")
    private String tenantCode;

    @Builder
    public IndicatorsConditionMonitoring(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long planId, String planName, Long formId, String fieldId, String fieldLabel, 
                    String maxConditionSymbol, String maxConditionValue, String minConditionSymbol, String minConditionValue) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.planId = planId;
        this.planName = planName;
        this.formId = formId;
        this.fieldId = fieldId;
        this.fieldLabel = fieldLabel;
        this.maxConditionSymbol = maxConditionSymbol;
        this.maxConditionValue = maxConditionValue;
        this.minConditionSymbol = minConditionSymbol;
        this.minConditionValue = minConditionValue;
    }

}
