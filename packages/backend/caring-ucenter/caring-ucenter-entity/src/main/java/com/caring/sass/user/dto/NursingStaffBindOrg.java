package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @ClassName NursingStaffRegister
 * @Description
 * @Author yangShuai
 * @Date 2020/10/13 16:21
 * @Version 1.0
 */
@Data
public class NursingStaffBindOrg {

    @NotEmpty(message = "id不能为空")
    @ApiModelProperty(value = "id")
    Long id;

    @NotEmpty(message = "机构码不能为空")
    @ApiModelProperty(value = "机构码")
    String code;

    @ApiModelProperty(value = "头像")
    String avatar;

    @NotEmpty(message = "姓名不能为空")
    @ApiModelProperty(value = "姓名")
    String name;


}
