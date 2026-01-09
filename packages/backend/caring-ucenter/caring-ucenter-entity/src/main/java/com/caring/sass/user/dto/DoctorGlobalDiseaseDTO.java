package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: DoctorGlobalDiseaseDTO
 * @author: 杨帅
 * @date: 2024/1/9
 */
@Data
@ApiModel("医生全局搜索诊断类型分组")
public class DoctorGlobalDiseaseDTO {

    @ApiModelProperty("诊断类型ID")
    private String id;

    @ApiModelProperty("诊断类型名称")
    private String name;

    @ApiModelProperty("统计患者数量")
    private Long countPatientNumber;


}
