package com.caring.sass.authority.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 医生登录请求参数
 *
 * @author xinzh
 */
@Data
public class DoctorLogin2_4Dto implements Serializable {

    @NotEmpty(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobile;

    @NotEmpty(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String verifyCode;

    @NotNull(message = "微信用户信息不能为空")
    @ApiModelProperty("微信用户信息")
    private Object wxMpUser;

}
