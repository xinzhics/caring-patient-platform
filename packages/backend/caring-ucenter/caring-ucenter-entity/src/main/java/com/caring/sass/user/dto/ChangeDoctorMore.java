package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 转移医生的患者到其他医生
 */
@ApiModel
@Data
public class ChangeDoctorMore {

    @NotNull
    private Long toDoctorId;

    @NotNull
    private Long formDoctorId;

    @NotNull
    private Boolean all;

    private List<Long> patientIds;


}
