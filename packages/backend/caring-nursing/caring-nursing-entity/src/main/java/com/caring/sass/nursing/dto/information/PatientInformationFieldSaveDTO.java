package com.caring.sass.nursing.dto.information;

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
 * 患者信息完整度字段
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientInformationFieldSaveDTO", description = "患者信息完整度字段")
public class PatientInformationFieldSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排序: 跟随监控指标中 monitorSort 字段更新
     */
    @ApiModelProperty(value = "排序: 跟随监控指标中 monitorSort 字段更新")
    private Integer fieldSort;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    private Long formId;
    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID")
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    private String fieldId;
    /**
     * 字段的业务类型
     */
    @ApiModelProperty(value = "字段的业务类型")
    @Length(max = 200, message = "字段的业务类型长度不能超过200")
    private String fieldExactType;
    /**
     * 字段的题型
     */
    @ApiModelProperty(value = "字段的题型")
    @Length(max = 200, message = "字段的题型长度不能超过200")
    private String fieldWidgetType;
    /**
     * 字段的结果
     */
    @ApiModelProperty(value = "字段的结果")
    @Length(max = 65535, message = "字段的结果长度不能超过65,535")
    private String values;
    /**
     * 字段的结果
     */
    @ApiModelProperty(value = "字段的结果")
    @Length(max = 65535, message = "字段的结果长度不能超过65,535")
    private String value;
    /**
     * 其他选项设置的备注
     */
    @ApiModelProperty(value = "其他选项设置的备注")
    @Length(max = 1000, message = "其他选项设置的备注长度不能超过1000")
    private String otherValue;
    /**
     * 基本信息，疾病信息，复查提醒...
     */
    @ApiModelProperty(value = "基本信息，疾病信息，复查提醒...")
    @Length(max = 300, message = "基本信息，疾病信息，复查提醒...长度不能超过300")
    private String businessType;
    /**
     * 字段的名称
     */
    @ApiModelProperty(value = "字段的名称")
    @Length(max = 1000, message = "字段的名称长度不能超过1000")
    private String fieldLabel;
    /**
     * 是否填写
     */
    @ApiModelProperty(value = "是否填写")
    private Integer write;
    /**
     * 填写时间
     */
    @ApiModelProperty(value = "填写时间")
    private LocalDateTime writeTime;

    @ApiModelProperty(value = "计划名称")
    private String planName;

}
