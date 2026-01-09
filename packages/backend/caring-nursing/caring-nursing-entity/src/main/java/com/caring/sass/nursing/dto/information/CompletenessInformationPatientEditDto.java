package com.caring.sass.nursing.dto.information;


import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.entity.information.PatientInformationField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CompletenessInformationPatientEditDto", description = "患者端患者编辑信息完整度")
public class CompletenessInformationPatientEditDto {

    private Long id;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;

    @ApiModelProperty(value = "医助ID")
    private Long nursingId;

    @ApiModelProperty(value = "患者所属医生")
    private String doctorName;

    @ApiModelProperty(value = "患者信息表单监控的表单字段")
    private List<PatientInformationFieldPatientEditDto> patientEditFields;






}
