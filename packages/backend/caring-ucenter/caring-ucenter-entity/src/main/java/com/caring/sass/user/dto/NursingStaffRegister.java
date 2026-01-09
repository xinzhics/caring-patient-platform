package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @ClassName NursingStaffRegister
 * @Description
 * @Author yangShuai
 * @Date 2020/10/13 16:21
 * @Version 1.0
 */
@Data
public class NursingStaffRegister {


    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    String phone;

    @ApiModelProperty(value = "密码")
    String password;


}
