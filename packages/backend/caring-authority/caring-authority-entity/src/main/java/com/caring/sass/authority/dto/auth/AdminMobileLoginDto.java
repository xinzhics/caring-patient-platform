package com.caring.sass.authority.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @className: AdminMobileLoginDto
 * @author: 杨帅
 * @date:  2023/4/12
 * @Desc: 超管使用手机号登录
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminMobileLoginDto implements Serializable {

    @NotEmpty(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobile;


    @ApiModelProperty("密码")
    private String password;


    @NotEmpty(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String verifyCode;

}
