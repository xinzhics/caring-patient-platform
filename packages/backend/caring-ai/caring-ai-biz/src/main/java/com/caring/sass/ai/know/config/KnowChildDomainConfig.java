package com.caring.sass.ai.know.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "knowchild.config")
public class KnowChildDomainConfig {

    public static final String PREFIX = "knowchild.config";


    @ApiModelProperty(value = "赛柏蓝平台域名")
    private String saibolanDomain;


    @ApiModelProperty(value = "Ai工作室域名")
    private String aiStudioDomain;


    @ApiModelProperty(value = "docuknow主域名")
    private String docuknowMainDoamin = "knows";



}
