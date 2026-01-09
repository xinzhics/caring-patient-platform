package com.caring.sass.wx.dto.config;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("调起支付的参数")
@Data
public class PrepayWithRequestPaymentResponseDTO {

  private String appId;

  @ApiModelProperty("时间戳")
  private String timestamp;

  @ApiModelProperty(" 随机字符串")
  private String nonceStr;

  @ApiModelProperty("小程序下单接口返回的prepay_id参数值")
  private String packageVal;


  @ApiModelProperty("签名类型，默认为RSA，仅支持RSA。")
  private String signType;

  @ApiModelProperty("签名")
  private String paySign;




}
