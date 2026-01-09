package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName PatientFormFieldReferenceSaveDTO
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 15:36
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientFormFieldReferenceSaveDTO", description = "患者基准更新")
public class PatientFormFieldReferenceUpdateDTO implements Serializable {


    @ApiModelProperty(value = "表单ID")
    private Long formId;

    @ApiModelProperty(value = "字段ID")
    private String fieldId;

    @ApiModelProperty(value = "患者ID")
    private Long patientId;

    @ApiModelProperty(value = "基准值")
    private Double referenceValue;

    @ApiModelProperty(value = "目标值")
    private Double targetValue;


}
