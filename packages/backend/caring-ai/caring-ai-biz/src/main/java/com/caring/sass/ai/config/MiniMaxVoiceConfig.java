package com.caring.sass.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = MiniMaxVoiceConfig.PREFIX)
public class MiniMaxVoiceConfig {

    public static final String PREFIX = "minimax.config";

    private Integer maxRpm;
    
    private String groupIdKailing;
    
    private String groupIdOther;
    
    private String apiKeyKailing;
    
    private String apiKeyOther;

}
