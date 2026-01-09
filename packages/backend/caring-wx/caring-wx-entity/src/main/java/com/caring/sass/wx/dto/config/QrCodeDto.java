package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "QrCodeDto", description = "二维码数据DTO模板类")
public class QrCodeDto {

    @ApiModelProperty(value = "图像的高度", name = "height", dataType = "int")
    int height;

    @ApiModelProperty(value = "图像的宽度", name = "width", dataType = "int")
    int width;

    @ApiModelProperty(value = "二维码图像的url地址", name = "url", dataType = "String", allowEmptyValue = false)
    String url;

}
