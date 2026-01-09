package com.caring.sass.ai.config;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = CardConfig.PREFIX)
public class CardConfig {

    public static final String PREFIX = "card.config";

    @ApiModelProperty("科普名片的小程序APPId")
    private String appId;

}
