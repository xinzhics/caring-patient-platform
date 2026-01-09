package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@ApiModel(value = "getPatientRecommendDoctorVo", description = "推荐患者-医生列表")
public class GetPatientRecommendDoctorVo {
    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    private String doctorName;
}
