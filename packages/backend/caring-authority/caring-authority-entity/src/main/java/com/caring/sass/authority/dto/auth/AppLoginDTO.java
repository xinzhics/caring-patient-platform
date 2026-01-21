package com.caring.sass.authority.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AppLoginDtp
 * @Description
 * @Author yangShuai
 * @Date 2020/10/15 9:54
 * @Version 1.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppLoginDTO {

    @ApiModelProperty("手机号 或 登录名")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;


}
