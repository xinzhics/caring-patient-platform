package com.caring.sass.user.dto;

import com.caring.sass.user.entity.DoctorCustomGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @className: DoctorGlobalQueryPatientDto
 * @author: 杨帅
 * @date: 2024/1/9
 */
@Data
@ApiModel("医生全局搜索的患者信息")
public class DoctorGlobalQueryPatientDto {

    @ApiModelProperty("患者ID")
    private Long id;

    @ApiModelProperty(value = "真实姓名")
    private String name;

    @ApiModelProperty(value = "性别，0男，1女")
    private Integer sex;

    @ApiModelProperty(value = "出生年月")
    private String birthday;

    @ApiModelProperty(value = "会员状态（0:关注  1：正常  2：取关)")
    private Integer status;

    @ApiModelProperty(value = "诊断类型ID")
    private String diagnosisId;

    @ApiModelProperty(value = "诊断类型名称")
    private String diagnosisName;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "是否完成入组信息（0：否  1：是）")
    private Integer isCompleteEnterGroup;

    @ApiModelProperty(value = "医生备注")
    private String doctorRemark;

    @ApiModelProperty(value = "患者存在的医生小组列表")
    private List<DoctorCustomGroup> patientExistGroupList;


    public void addPatientExistGroupList(DoctorCustomGroup group) {
        if (group == null) {
            return;
        }
        if (patientExistGroupList == null) {
            patientExistGroupList = new ArrayList<>();
        }
        patientExistGroupList.add(group);
    }

}
