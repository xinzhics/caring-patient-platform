package com.caring.sass.nursing.dto.form;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "IndicatorsResultInformationPageDTO", description = "患者监测数据结果及处理表")
public class IndicatorsResultInformationPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;
    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    private String patientName;
    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    @Length(max = 255, message = "患者头像长度不能超过255")
    private String avatar;
    /**
     * 患者所属医生ID
     */
    @ApiModelProperty(value = "患者所属医生ID")
    private Long doctorId;
    /**
     * 患者所属医生名称
     */
    @ApiModelProperty(value = "患者所属医生名称")
    private String doctorName;
    /**
     * 异常处理状态 (1 未处理， 2 已处理)
     */
    @ApiModelProperty(value = "异常处理状态 (1 未处理， 2 已处理)")
    private Integer handleStatus;
    /**
     * 清理状态 (1 未清理， 2 已清理)
     */
    @ApiModelProperty(value = "清理状态 (1 未清理， 2 已清理)")
    private Integer clearStatus;
    /**
     * 异常处理时间
     */
    @ApiModelProperty(value = "异常处理时间")
    private LocalDateTime handleTime;
    /**
     * 异常处理人id
     */
    @ApiModelProperty(value = "异常处理人id")
    private Long handleUser;
    /**
     * 监控指标条件ID
     */
    @ApiModelProperty(value = "监控指标条件ID")
    private Long indicatorsConditionId;
    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    private Long planId;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    private Long formId;
    /**
     * 表单结果ID
     */
    @ApiModelProperty(value = "表单结果ID")
    private Long formResultId;
    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID")
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    private String fieldId;
    /**
     * 表单字段
     */
    @ApiModelProperty(value = "表单字段")
    @Length(max = 300, message = "表单字段长度不能超过300")
    private String fieldLabel;
    /**
     * 表单监控条件字段实际填写值
     */
    @ApiModelProperty(value = "表单监控条件字段实际填写值")
    @Length(max = 40, message = "表单监控条件字段实际填写值长度不能超过40")
    private String realWriteValue;
    /**
     * 表单监控条件字段右侧单位
     */
    @ApiModelProperty(value = "表单监控条件字段右侧单位")
    @Length(max = 40, message = "表单监控条件字段右侧单位长度不能超过40")
    private String realWriteValueRightUnit;
    /**
     * 表单填写后提交时间
     */
    @ApiModelProperty(value = "表单填写后提交时间")
    private LocalDateTime formWriteTime;
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
