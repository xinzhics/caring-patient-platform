package com.caring.sass.nursing.entity.information;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

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
@ToString(callSuper = true)
@Builder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_patient_information_field")
@ApiModel(value = "PatientInformationField", description = "患者信息完整度字段")
@AllArgsConstructor
public class PatientInformationField extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段外键ID")
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    @TableField(value = "information_id", condition = EQUAL)
    @Excel(name = "表单中字段外键ID")
    private Long informationId;

    /**
     * 排序: 跟随监控指标中 monitorSort 字段更新
     */
    @ApiModelProperty(value = "排序: 跟随监控指标中 monitorSort 字段更新")
    @TableField("field_sort")
    @Excel(name = "排序: 跟随监控指标中 monitorSort 字段更新")
    private Integer fieldSort;

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
     * 字段的业务类型
     */
    @ApiModelProperty(value = "字段的业务类型")
    @Length(max = 200, message = "字段的业务类型长度不能超过200")
    @TableField(value = "field_exact_type", condition = LIKE)
    @Excel(name = "字段的业务类型")
    private String fieldExactType;

    /**
     * 字段的题型
     */
    @ApiModelProperty(value = "字段的题型")
    @Length(max = 200, message = "字段的题型长度不能超过200")
    @TableField(value = "field_widget_type", condition = LIKE)
    @Excel(name = "字段的题型")
    private String fieldWidgetType;

    /**
     * 字段的结果
     */
    @ApiModelProperty(value = "字段的结果")
    @Length(max = 65535, message = "字段的结果长度不能超过65535")
    @TableField("field_values")
    @Excel(name = "字段的结果")
    private String fieldValues;

    /**
     * 字段的结果
     */
    @ApiModelProperty(value = "字段的结果")
    @Length(max = 65535, message = "字段的结果长度不能超过65535")
    @TableField("field_value")
    @Excel(name = "字段的结果")
    private String fieldValue;

    /**
     * 其他选项设置的备注
     */
    @ApiModelProperty(value = "其他选项设置的备注")
    @Length(max = 1000, message = "其他选项设置的备注长度不能超过1000")
    @TableField(value = "field_other_value", condition = LIKE)
    @Excel(name = "其他选项设置的备注")
    private String otherValue;

    /**
     * 基本信息，疾病信息，复查提醒...
     */
    @ApiModelProperty(value = "基本信息，疾病信息，复查提醒...")
    @Length(max = 300, message = "基本信息，疾病信息，复查提醒...长度不能超过300")
    @TableField(value = "business_type", condition = LIKE)
    @Excel(name = "基本信息，疾病信息，复查提醒...")
    private String businessType;

    /**
     * 字段的名称
     */
    @ApiModelProperty(value = "字段的名称")
    @Length(max = 1000, message = "字段的名称长度不能超过1000")
    @TableField(value = "field_label", condition = LIKE)
    @Excel(name = "字段的名称")
    private String fieldLabel;

    @ApiModelProperty(value = "字段的描述 （产品变更）")
    @TableField(value = "field_label_desc", condition = LIKE)
    @Excel(name = "字段的描述")
    private String fieldLabelDesc;

    /**
     * 是否填写
     */
    @ApiModelProperty(value = "是否填写")
    @TableField("field_write")
    @Excel(name = "是否填写")
    private Integer fieldWrite;

    /**
     * 填写时间
     */
    @ApiModelProperty(value = "填写时间")
    @TableField("write_time")
    @Excel(name = "填写时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime writeTime;

    @ApiModelProperty(value = "计划名称")
    @TableField("plan_name")
    @Excel(name = "护理计划名称")
    private String planName;


}
