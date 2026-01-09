package com.caring.sass.ai.card.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = CardWxPayConfig.PREFIX)
public class CardWxPayConfig {

    public static final String PREFIX = "cardpay.config";

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "小程序APPID")
    private String appId;


    @ApiModelProperty(value = "基础会员的价格，单位是分")
    private Integer basicMembershipPrice;

    @ApiModelProperty(value = "基础会员名称")
    private String basicMembershipName;


    @ApiModelProperty(value = "会员的价格，单位是分")
    private Integer membershipPrice;


    @ApiModelProperty(value = "会员名称")
    private String membershipName;


}
