package com.caring.sass.wx.dto.config;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "VarifyTokenForVerifyDomainNameForm", description = "用于接收来自微信服务器的token验证请求，获取请求中的参数。")
public class VarifyTokenForVerifyDomainNameForm {
    @ApiModelProperty(value = "回显字符串", dataType = "String", name = "echostr", required = true, notes = "微信调用node端时会回传这个字符串")
    String echostr;
    @ApiModelProperty(value = "签名", dataType = "String", name = "signature", required = true, notes = "微信调用node端时会回传这个字符串")
    String signature;
    @ApiModelProperty(value = "随机字符串", dataType = "String", name = "nonce", required = true, notes = "微信调用node端时会回传这个字符串")
    String nonce;
    @ApiModelProperty(value = "时间戳", dataType = "String", name = "timestamp", required = true, notes = "微信调用node端时会回传这个字符串")
    String timestamp;
}