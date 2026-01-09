package com.caring.sass.nursing.entity.traceInto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 选项跟踪异常题目明细记录表
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-07
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Builder
@TableName("t_trace_into_result_field_abnormal")
@ApiModel(value = "TraceIntoResultFieldAbnormal", description = "选项跟踪异常题目明细记录表")
@AllArgsConstructor
public class TraceIntoResultFieldAbnormal extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 记录当前选项异常结果所属哪条选项结果
     */
    @ApiModelProperty(value = "记录当前选项异常结果所属哪条选项结果")
    @NotNull(message = "记录当前选项异常结果所属哪条选项结果不能为空")
    @TableField("trace_into_config_id")
    @Excel(name = "记录当前选项异常结果所属哪条选项结果")
    private Long traceIntoConfigId;

    @ApiModelProperty(value = "表单ID")
    @NotNull(message = "表单ID不能为空")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 表单结果ID
     */
    @ApiModelProperty(value = "表单结果ID")
    @NotNull(message = "表单结果ID不能为空")
    @TableField("form_result_id")
    @Excel(name = "表单结果ID")
    private Long formResultId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @NotNull(message = "患者ID不能为空")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @NotNull(message = "医助ID不能为空")
    @TableField("nursing_id")
    @Excel(name = "医助ID")
    private Long nursingId;

    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生ID不能为空")
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 随访计划ID
     */
    @ApiModelProperty(value = "随访计划ID")
    @NotNull(message = "随访计划ID不能为空")
    @TableField("plan_id")
    @Excel(name = "随访计划ID")
    private Long planId;

    /**
     * 父题目的ID
     */
    @ApiModelProperty(value = "父题目的ID")
    @Length(max = 100, message = "父题目的ID长度不能超过100")
    @TableField(value = "parent_field_id", condition = EQUAL)
    @Excel(name = "父题目的ID")
    private String parentFieldId;

    /**
     * 父题目选项ID
     */
    @ApiModelProperty(value = "父题目选项ID")
    @Length(max = 100, message = "父题目选项ID长度不能超过100")
    @TableField(value = "parent_field_option_id", condition = EQUAL)
    @Excel(name = "父题目选项ID")
    private String parentFieldOptionId;

    /**
     * 异常选项所在的题目ID
     */
    @ApiModelProperty(value = "异常选项所在的题目ID")
    @Length(max = 100, message = "异常选项所在的题目ID长度不能超过100")
    @TableField(value = "form_field_id", condition = EQUAL)
    @Excel(name = "异常选项所在的题目ID")
    private String formFieldId;

    /**
     * 异常选项的ID
     */
    @ApiModelProperty(value = "异常选项的ID")
    @Length(max = 100, message = "异常选项的ID长度不能超过100")
    @TableField(value = "form_field_option_id", condition = EQUAL)
    @Excel(name = "异常选项的ID")
    private String formFieldOptionId;




}
