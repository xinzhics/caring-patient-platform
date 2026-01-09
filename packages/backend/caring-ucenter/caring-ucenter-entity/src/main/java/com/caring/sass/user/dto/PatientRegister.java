package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class PatientRegister {

    @ApiModelProperty(value = "手机号")
    String phone;


    @ApiModelProperty(value = "短信验证码")
    String code;


    @ApiModelProperty(value = "密码")
    String password;


    @ApiModelProperty(value = "openId")
    String openId;


    @ApiModelProperty(value = "type: 1 注册， 0 登录")
    int type = 0;

}
