package com.caring.sass.nursing.entity.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName PatientFormFieldReference
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 15:18
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_form_patient_field_reference")
@ApiModel(value = "PatientFormFieldReference", description = "患者的监测字段被修改的基准值目标值")
@AllArgsConstructor
public class PatientFormFieldReference extends Entity<Long> {

    @ApiModelProperty(value = "护理计划ID")
    @Length(max = 32, message = "护理计划ID")
    @NotNull
    @TableField(value = "business_id", condition = EQUAL)
    private String businessId;

    @ApiModelProperty(value = "字段ID")
    @Length(max = 32, message = "字段ID")
    @NotNull
    @TableField(value = "field_id", condition = EQUAL)
    private String fieldId;

    @ApiModelProperty(value = "患者ID")
    @Length(max = 32, message = "患者ID")
    @NotNull
    @TableField(value = "patient_id", condition = EQUAL)
    private Long patientId;

    @ApiModelProperty(value = "患者的机构ID")
    @TableField(value = "org_id", condition = EQUAL)
    private Long orgId;

    @ApiModelProperty(value = "医生ID")
    @TableField(value = "doctor_id", condition = EQUAL)
    private Long doctorId;

    @ApiModelProperty(value = "医助ID")
    @TableField(value = "nursing_id", condition = EQUAL)
    private Long nursingId;

    @ApiModelProperty(value = "基准值")
    @TableField(value = "reference_value")
    private Double referenceValue;

    @ApiModelProperty(value = "目标值")
    @TableField(value = "target_value")
    private Double targetValue;

}
