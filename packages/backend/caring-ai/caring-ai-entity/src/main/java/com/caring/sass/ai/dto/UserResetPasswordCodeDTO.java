package com.caring.sass.ai.dto;

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
@ApiModel(value = "UserResetPasswordCodeDTO", description = "用户验证码是否正确")
public class UserResetPasswordCodeDTO {

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


}
