package com.caring.sass.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sms.zhongzhengyun")
public class SmsConfig {
    
    private String smsUser;
    private String password;
    private String restServiceUrl;
}