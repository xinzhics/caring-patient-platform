package com.caring.sass.user.config;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = DifyConfig.PREFIX)
public class DifyConfig {

    public static final String PREFIX = "saasdify.config";

    @ApiModelProperty(value = "dify的网络url配置")
    private String DIFY_BASE_URL;

    @ApiModelProperty(value = "自动回复的apikey")
    private String DIFY_SAAS_AI_REPLY_API_KEY;

}
