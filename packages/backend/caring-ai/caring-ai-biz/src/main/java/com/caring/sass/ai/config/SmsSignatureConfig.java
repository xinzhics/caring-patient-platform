package com.caring.sass.ai.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = SmsSignatureConfig.PREFIX)
public class SmsSignatureConfig {

    public static final String PREFIX = "signature.config";

    @ApiModelProperty(value = "ai应用的短信登录签名")
    private String name;

    @ApiModelProperty(value = "阿里云的模版code")
    private String templateCode;

    @ApiModelProperty(value = "阿里云参数")
    private String ALIBABA_CLOUD_ACCESS_KEY_ID;


    @ApiModelProperty(value = "阿里云参数")
    private String ALIBABA_CLOUD_ACCESS_KEY_SECRET;


}
