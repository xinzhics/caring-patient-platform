package com.caring.sass.user.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImGroup
 * @Description
 * @Author yangShuai
 * @Date 2021/9/15 17:05
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImGroupMember {

    private Long id;

    private Boolean me;

    private String name;

    private String type;

    private String avatar;

    private String imAccount;

    private String remark;

    @ApiModelProperty(value = "是否AI托管")
    private Boolean aiHosted;

    public ImGroupMember(NursingStaff nursingStaff, Long nursingId ) {
        if (nursingId != null) {
            this.me = nursingStaff.getId().equals(nursingId);
        } else {
            this.me = false;
        }
        this.type = UserType.UCENTER_NURSING_STAFF;

        this.id = nursingStaff.getId();
        this.name = nursingStaff.getName();
        this.avatar = nursingStaff.getAvatar();
        this.imAccount = nursingStaff.getImAccount();
    }

    public ImGroupMember(Doctor doctor, Long doctorId) {
        if (doctorId != null) {
            this.me = doctor.getId().equals(doctorId);
        } else {
            this.me = false;
        }
        this.type = UserType.UCENTER_DOCTOR;

        this.id = doctor.getId();
        this.name = doctor.getName();
        this.avatar = doctor.getAvatar();
        this.imAccount = doctor.getImAccount();
        this.aiHosted = doctor.getAiHosted();
    }
}
