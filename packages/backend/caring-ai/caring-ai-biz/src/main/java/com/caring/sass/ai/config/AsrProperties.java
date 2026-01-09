package com.caring.sass.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ASR配置
 *
 * @author leizhi
 */
@Data
@Component
@ConfigurationProperties(prefix = AsrProperties.PREFIX)
public class AsrProperties {
    static final String PREFIX = "caring.asr";

    /** DashScope API Key */
    private String apiKey = "${DASHSCOPE_API_KEY:}";
}