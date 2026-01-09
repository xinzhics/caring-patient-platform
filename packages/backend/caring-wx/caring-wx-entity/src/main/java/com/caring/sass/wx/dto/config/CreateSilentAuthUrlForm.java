package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CreateSilentAuthUrlForm", description = "创建一个静默授权的URL参数")
public class CreateSilentAuthUrlForm extends GeneralForm {
    @ApiModelProperty(value = "静默授权的URL", name = "redirectUri", dataType = "String", example = "http://.....", allowEmptyValue = false)
    String redirectUri;

    @ApiModelProperty(value = "openId", name = "openId", dataType = "String")
    String openId;
}