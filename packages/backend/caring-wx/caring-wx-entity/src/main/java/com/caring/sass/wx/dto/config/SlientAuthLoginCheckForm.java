package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SlientAuthLoginCheckForm", description = "接口：静默授权的登录检查接口(slientAuthLoginCheck) 所需的参数表单")
public class SlientAuthLoginCheckForm extends GeneralForm {
    @ApiModelProperty(value = "微信公众平台返回的code", dataType = "String", name = "code", required = true, notes = "这个code是由微信公众平台给我们的域名返回的")
    String code;

    @ApiModelProperty(value = "openId", dataType = "String", name = "openId", required = true, notes = "openId")
    String openId;

}