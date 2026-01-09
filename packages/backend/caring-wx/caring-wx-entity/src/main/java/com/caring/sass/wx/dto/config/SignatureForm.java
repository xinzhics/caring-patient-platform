package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SignatureForm", description = "签名的数据模板类")
public class SignatureForm extends GeneralForm {
    @ApiModelProperty(value = "需要签名的url", name = "url", dataType = "String", allowEmptyValue = false)
    String url;
}