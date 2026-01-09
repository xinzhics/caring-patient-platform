package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Oauth2getUserInfoForm", description = "接口：oauth2getUserInfo的参数表单数据")
public class Oauth2getUserInfoForm extends GeneralForm {

    @ApiModelProperty(value = "微信返回的code", name = "code", dataType = "String", allowEmptyValue = false)
    String code;

}