package com.caring.sass.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI搜索配置
 *
 * @author leizhi
 */
@Data
@Component
@ConfigurationProperties(prefix = SearchProperties.PREFIX)
public class SearchProperties {
    static final String PREFIX = "caring.bing";

    /** Bing Search V7 subscription key */
    private String subscriptionKey;

    /** Bing Search V7 subscription key */
    private String endpoint;
}
