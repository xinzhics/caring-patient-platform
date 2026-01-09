package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CreateFollowerPermanentQrCode", description = "接口：createFollowerPermanentQrCode的参数表单")
public class CreateFollowerPermanentQrCode extends GeneralForm {
    @ApiModelProperty(value = "参数值", name = "params", dataType = "String", example = "参数，可以是用户Id，或者其他必要的参数", allowEmptyValue = false)
    String params;
    @ApiModelProperty(value = "二维码过期时间（单位：秒）", name = "expireSeconds", dataType = "Integer", example = "临时二维参数，临时二维码有效时间最大不能超过2592000（即30天）", allowEmptyValue = true)
    Integer expireSeconds;
}