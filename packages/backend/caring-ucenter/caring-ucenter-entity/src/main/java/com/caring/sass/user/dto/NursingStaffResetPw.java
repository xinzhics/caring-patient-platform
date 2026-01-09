package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @ClassName NursingStaffRegister
 * @Description
 * @Author yangShuai
 * @Date 2020/10/13 16:21
 * @Version 1.0
 */
@Data
public class NursingStaffResetPw {

    @NotEmpty(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号")
    String mobile;

    @NotEmpty(message = "密码不能为空")
    @ApiModelProperty(value = "密码")
    String newPassword;


}
