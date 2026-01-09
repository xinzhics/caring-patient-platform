package com.caring.sass.authority.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @ClassName DoctorLoginByOpenId
 * @Description
 * @Author yangShuai
 * @Date 2022/1/7 16:39
 * @Version 1.0
 */
@Data
public class DoctorLoginByOpenId implements Serializable {

    @NotEmpty(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("是否加密")
    private Boolean decode;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("验证码")
    private String verifyCode;

    @ApiModelProperty("openId")
    private String openId;


}
