package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "DoctorResetPasswordDTO", description = "用户重置密码使用")
public class DoctorResetPasswordDTO {

    /**
     * 用户手机号
     */
    @NotEmpty
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 20, message = "用户手机号长度不能超过20")
    private String userMobile;

    @NotNull
    @ApiModelProperty(value = "手机号验证码")
    @Length(max = 20, message = "手机号验证码")
    private String smsCode;

    @NotNull
    @ApiModelProperty(value = "用户密码")
    @Length(max = 40, message = "用户密码")
    private String password;




}
