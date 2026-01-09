package com.caring.sass.nursing.dto.information;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.dto.form.FormField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CompletenessInformationPatientEditDto", description = "患者端患者编辑信息完整度")
public class PatientInformationFieldPatientEditDto {

    private Long id;

    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段外键ID")
    @Excel(name = "表单中字段外键ID")
    private Long informationId;

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
    private String fieldId;

    /**
     * 基本信息，疾病信息，复查提醒...
     */
    @ApiModelProperty(value = "基本信息，疾病信息，复查提醒...")
    @Excel(name = "基本信息，疾病信息，复查提醒...")
    private String businessType;

    /**
     * 字段的名称
     */
    @ApiModelProperty(value = "字段的名称")
    @Excel(name = "字段的名称")
    private String fieldLabel;


    @ApiModelProperty(value = "字段的描述 （产品变更）")
    private String fieldLabelDesc;

    /**
     * 是否填写
     */
    @ApiModelProperty(value = "是否填写")
    private Integer fieldWrite;


    @ApiModelProperty(value = "计划名称")
    private String planName;


    @ApiModelProperty(value = "表单字段")
    private String formField;

    @ApiModelProperty(value = "字段的结果")
    private String fieldValues;

    /**
     * 字段的结果
     */
    @ApiModelProperty(value = "字段的结果")
    private String fieldValue;

    /**
     * 其他选项设置的备注
     */
    @ApiModelProperty(value = "其他选项设置的备注")
    private String otherValue;



}
