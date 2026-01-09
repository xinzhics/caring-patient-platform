package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @ClassName MeetingGroup
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 9:42
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ConsultationSaveGroup", description = "会诊组")
public class ConsultationSaveGroup {


    @ApiModelProperty(value = "医助ID")
    private Long nurseId;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "环信的群组ID")
    private String imGroupId;

    @ApiModelProperty(value = "成员头像")
    private String memberAvatar;

    @ApiModelProperty(value = "会诊组名字")
    @Length(max = 255, message = "会议组名字长度不能超过20")
    private String groupName;

    @ApiModelProperty(value = "会诊组描述")
    @Length(max = 200, message = "会诊组描述长度不能超过200")
    private String groupDesc;

    @ApiModelProperty(value = "状态(processing, finish)")
    private String consultationStatus;

    @ApiModelProperty(value = "患者ID")
    private List<Long> patientIds;

    @ApiModelProperty(value = "患者ID,号隔开")
    private String stringPatientIds;

}
