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
public class NursingStaffFindPw {

    @NotEmpty(message = "id不能为空")
    @ApiModelProperty(value = "id")
    Long id;

    @NotEmpty(message = "新密码不能为空")
    @ApiModelProperty(value = "新密码")
    String newPassword;

    @NotEmpty(message = "旧密码不能为空")
    @ApiModelProperty(value = "旧密码")
    String oldPassword;

}
