package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GeneratePubnoQrCodeForm", description = "二维码数据DTO模板类")
public class GenerateConfigQrCodeForm extends GeneralForm {

    @ApiModelProperty(value = "生成二维码所需要的ticket", name = "ticket", dataType = "String", allowEmptyValue = false)
    String ticket;

    @ApiModelProperty(value = "过期时间", name = "expire", dataType = "Integer", allowEmptyValue = false)
    Integer expire;

}
