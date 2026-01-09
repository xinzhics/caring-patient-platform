package com.caring.sass.file.config;

import com.caring.sass.boot.config.BaseConfig;
import com.caring.sass.log.event.SysLogListener;
import com.caring.sass.oauth.api.LogApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caring
 * @createTime 2017-12-15 14:42
 */
@Configuration
public class FileWebConfiguration extends BaseConfig {
    /**
     * caring.log.enabled = true 并且 caring.log.type=DB时实例该类
     *
     * @param logApi
     * @return
     */
    @Bean
    @ConditionalOnExpression("${caring.log.enabled:true} && 'DB'.equals('${caring.log.type:LOGGER}')")
    public SysLogListener sysLogListener(LogApi logApi) {
        return new SysLogListener((log) -> logApi.save(log));
    }
}
