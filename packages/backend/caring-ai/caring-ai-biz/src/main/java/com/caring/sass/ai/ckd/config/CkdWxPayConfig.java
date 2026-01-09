package com.caring.sass.ai.ckd.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = CkdWxPayConfig.PREFIX)
public class CkdWxPayConfig {

    public static final String PREFIX = "ckdpay.config";

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "小程序APPID")
    private String appId;


    @ApiModelProperty(value = "一个月的金额，单位是分")
    private Integer monthlyExpenses;

    @ApiModelProperty(value = "月度包")
    private String monthlyExpensesName;


    @ApiModelProperty(value = "一年的费用")
    private Integer annualCost;

    @ApiModelProperty(value = "年度包")
    private String annualCostName;


    @ApiModelProperty(value = "永久版，单位是分")
    private Integer lifetime;


    @ApiModelProperty(value = "终身版")
    private String lifeTimeName;



}
