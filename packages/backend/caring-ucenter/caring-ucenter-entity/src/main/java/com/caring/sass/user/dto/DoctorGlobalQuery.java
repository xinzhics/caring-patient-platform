package com.caring.sass.user.dto;

import com.caring.sass.user.entity.DoctorCustomGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @className: DoctorGlobalQuery
 * @author: 杨帅
 * @date: 2024/1/9
 */
@Data
@ApiModel("医生全局查询的结果")
public class DoctorGlobalQuery {

    @ApiModelProperty("医生查询到的患者列表")
    private List<DoctorGlobalQueryPatientDto> patientDtos;

    @ApiModelProperty("诊断类型")
    private List<DoctorGlobalDiseaseDTO> diseaseDTOS;


    @ApiModelProperty("医生自定义小组")
    private List<DoctorCustomGroup> doctorCustomGroups;

}
