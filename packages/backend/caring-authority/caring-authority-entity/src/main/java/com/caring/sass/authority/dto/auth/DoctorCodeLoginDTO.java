package com.caring.sass.authority.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 医生登录请求参数
 *
 * @author xinzh
 */
@Data
public class DoctorCodeLoginDTO implements Serializable {
    @NotEmpty(message = "微信code不能为空")
    @ApiModelProperty("微信code")
    private String code;

    @NotEmpty(message = "微信appId不能为空")
    @ApiModelProperty("微信appId")
    private String appId;
}
