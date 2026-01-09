package com.caring.sass.ai.call.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = CallCostConfig.PREFIX)
public class CallCostConfig {

    public static final String PREFIX = "call.config";

    @ApiModelProperty(value = "每分钟的调用成本，单位是分")
    private Integer amountPerMinute;



}
