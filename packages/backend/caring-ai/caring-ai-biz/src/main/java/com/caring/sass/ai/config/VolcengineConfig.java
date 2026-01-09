package com.caring.sass.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "volcengine.api")
public class VolcengineConfig {
    
    private String accessKeyId;
    private String secretAccessKey;
    private String region = "cn-north-1";
    private String service = "cv";
}