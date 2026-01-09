package com.caring.sass.ai.service;

import com.caring.sass.ai.humanVideo.config.HumanVideoConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@ApiModel("数字人制作方面的参数配置")
@Component
@ConfigurationProperties(prefix = AliYunAccessKey.PREFIX)
public class AliYunAccessKey {

    public static final String PREFIX = "aliyun.oss";

    @ApiModelProperty("accessKeyId")
    public String accessKeyId;

    @ApiModelProperty("secret")
    public String secret;

    @ApiModelProperty("endpoint")
    public String endpoint;

    String bucketName;

    String TemplateId;

    String pipelineId;
}
