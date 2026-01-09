package com.caring.sass.ai.humanVideo.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ApiModel("数字人制作方面的参数配置")
@Component
@ConfigurationProperties(prefix = HumanVideoConfig.PREFIX)
public class HumanVideoConfig {

    public static final String PREFIX = "human.config";


    @ApiModelProperty("火山声音复刻appId")
    private String speakerAppId;


    @ApiModelProperty("火山声音复刻AccessToken")
    private String speakerAccessToken;

    @ApiModelProperty("百度对口型数字人appId")
    private String baidu123AppId;


    @ApiModelProperty("百度对口型数字人appSecret")
    private String baidu123AppSecret;

}
