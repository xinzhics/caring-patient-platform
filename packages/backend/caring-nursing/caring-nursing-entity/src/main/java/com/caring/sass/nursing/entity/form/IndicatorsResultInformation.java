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
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 患者监测数据结果及处理表
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
@TableName("t_nursing_indicators_result_information")
@ApiModel(value = "IndicatorsResultInformation", description = "患者监测数据结果及处理表")
@AllArgsConstructor
public class IndicatorsResultInformation extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    @TableField("patient_name")
    @Excel(name = "患者名称")
    private String patientName;

    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    @Length(max = 255, message = "患者头像长度不能超过255")
    @TableField(value = "avatar", condition = LIKE)
    @Excel(name = "患者头像")
    private String avatar;

    /**
     * 患者所属医生ID
     */
    @ApiModelProperty(value = "患者所属医生ID")
    @TableField("doctor_id")
    @Excel(name = "患者所属医生ID")
    private Long doctorId;

    /**
     * 患者所属医生名称
     */
    @ApiModelProperty(value = "患者所属医生名称")
    @TableField("doctor_name")
    @Excel(name = "患者所属医生名称")
    private String doctorName;

    /**
     * 异常处理状态 (1 未处理， 2 已处理)
     */
    @ApiModelProperty(value = "异常处理状态 (1 未处理， 2 已处理)")
    @TableField("handle_status")
    @Excel(name = "异常处理状态 (1 未处理， 2 已处理)")
    private Integer handleStatus;

    /**
     * 清理状态 (1 未清理， 2 已清理)
     */
    @ApiModelProperty(value = "清理状态 (1 未清理， 2 已清理)")
    @TableField("clear_status")
    @Excel(name = "清理状态 (1 未清理， 2 已清理)")
    private Integer clearStatus;

    /**
     * 异常处理时间
     */
    @ApiModelProperty(value = "异常处理时间")
    @TableField("handle_time")
    @Excel(name = "异常处理时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime handleTime;

    /**
     * 异常处理人id
     */
    @ApiModelProperty(value = "异常处理人id")
    @TableField("handle_user")
    @Excel(name = "异常处理人id")
    private Long handleUser;

    /**
     * 监控指标条件ID
     */
    @ApiModelProperty(value = "监控指标条件ID")
    @TableField("indicators_condition_id")
    @Excel(name = "监控指标条件ID")
    private Long indicatorsConditionId;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    @TableField("plan_id")
    @Excel(name = "计划ID")
    private Long planId;



    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 表单结果ID
     */
    @ApiModelProperty(value = "表单结果ID")
    @TableField("form_result_id")
    @Excel(name = "表单结果ID")
    private Long formResultId;

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
     * 表单监控条件字段实际填写值
     */
    @ApiModelProperty(value = "表单监控条件字段实际填写值")
    @Length(max = 40, message = "表单监控条件字段实际填写值长度不能超过40")
    @TableField(value = "real_write_value", condition = LIKE)
    @Excel(name = "表单监控条件字段实际填写值")
    private String realWriteValue;

    /**
     * 表单监控条件字段右侧单位
     */
    @ApiModelProperty(value = "表单监控条件字段右侧单位")
    @Length(max = 40, message = "表单监控条件字段右侧单位长度不能超过40")
    @TableField(value = "real_write_value_right_unit", condition = LIKE)
    @Excel(name = "表单监控条件字段右侧单位")
    private String realWriteValueRightUnit;

    /**
     * 表单填写后提交时间
     */
    @ApiModelProperty(value = "表单填写后提交时间")
    @TableField("form_write_time")
    @Excel(name = "表单填写后提交时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime formWriteTime;

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
    @ApiModelProperty(value = "最小值条件符号")
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
    /**
     * 医助id
     */
    @ApiModelProperty(value = "医助id")
    @TableField("nursing_id")
    @Excel(name = "医助id")
    private Long nursingId;
    @Builder
    public IndicatorsResultInformation(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long patientId, String patientName, Long doctorId, String doctorName, Integer handleStatus,
                    Integer clearStatus, LocalDateTime handleTime, Long handleUser, Long indicatorsConditionId, Long planId, Long formId, 
                    String fieldId, String fieldLabel, String realWriteValue, LocalDateTime formWriteTime, String maxConditionSymbol, String maxConditionValue, 
                    String minConditionSymbol, String minConditionValue) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.handleStatus = handleStatus;
        this.clearStatus = clearStatus;
        this.handleTime = handleTime;
        this.handleUser = handleUser;
        this.indicatorsConditionId = indicatorsConditionId;
        this.planId = planId;
        this.formId = formId;
        this.fieldId = fieldId;
        this.fieldLabel = fieldLabel;
        this.realWriteValue = realWriteValue;
        this.formWriteTime = formWriteTime;
        this.maxConditionSymbol = maxConditionSymbol;
        this.maxConditionValue = maxConditionValue;
        this.minConditionSymbol = minConditionSymbol;
        this.minConditionValue = minConditionValue;
    }

}
