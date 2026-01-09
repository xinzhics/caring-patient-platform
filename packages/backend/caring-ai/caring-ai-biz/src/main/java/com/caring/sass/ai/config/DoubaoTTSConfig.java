package com.caring.sass.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "doubao.tts")
public class DoubaoTTSConfig {
    
    private String apiUrl = "https://openspeech.bytedance.com/api/v1/tts";
    private String appId;
    private String token;
    private String cluster = "volcano_tts";
    private String uid = "uid123";
    private String filePath = "/saas/podcast/audio/downloads";
}