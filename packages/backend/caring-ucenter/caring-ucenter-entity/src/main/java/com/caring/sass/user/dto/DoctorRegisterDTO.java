package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @className: DoctorRegisterDTO
 * @author: 杨帅
 * 医生端注册医生时的信息
 * @date: 2023/10/10
 */
@Data
public class DoctorRegisterDTO {

    @NotEmpty
    @ApiModelProperty("二级域名")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    private String domain;

    @NotEmpty
    @ApiModelProperty("医生名称")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    private String name;

    @ApiModelProperty("医生的openId")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    private String openId;

    @NotEmpty
    @ApiModelProperty("医生的手机号")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("医生注册时填写的机构名称")
    @Pattern(regexp = "^[^'#\\*%;]*$", message = "参数不能有特殊符号")
    private String registerOrgName;

    @ApiModelProperty(value = "医院")
    private Long hospitalId;

    @ApiModelProperty(value = "医院名称")
    @Length(max = 50, message = "医院名称长度不能超过50")
    @Pattern(regexp = "^[^'#\\*%;]*$", message = "参数不能有特殊符号")
    private String hospitalName;

}
