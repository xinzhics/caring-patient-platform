package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 2.3版本初始化 医助的 群组列表页使用
 * @ClassName InitNursingMsgListDTO
 * @Author yangShuai
 * @Date 2021/8/31 16:24
 * @Version 1.0
 */
@Deprecated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class InitNursingMsgListDTO {


    @ApiModelProperty(value = "患者", name = "patient_id", dataType = "String")
    protected Long patientId;

    @ApiModelProperty(value = "患者名字", name = "patientName", dataType = "String")
    protected String patientName;

    @ApiModelProperty(value = "患者备注", name = "patientRemark", dataType = "String")
    protected String patientRemark;

    @ApiModelProperty(value = "医生对患者的备注", name = "doctorRemark", dataType = "String")
    protected String doctorRemark;

    @ApiModelProperty(value = "患者头像", name = "patientAvatar", dataType = "String")
    private String patientAvatar;

    @ApiModelProperty(value = "列表上人员环信账号(患者)", name = "receiverImAccount", dataType = "String")
    protected String receiverImAccount;

    @ApiModelProperty(value = "医助", name = "serviceAdvisorId", dataType = "Long")
    protected Long serviceAdvisorId;

    @ApiModelProperty(value = "医生", name = "doctorId", dataType = "Long")
    protected Long doctorId;

}
