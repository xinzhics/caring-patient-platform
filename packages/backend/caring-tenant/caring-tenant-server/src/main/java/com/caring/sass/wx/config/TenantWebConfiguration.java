package com.caring.sass.wx.config;

import com.caring.sass.authority.service.common.OptLogService;
import com.caring.sass.boot.config.BaseConfig;
import com.caring.sass.log.event.SysLogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caring
 * @createTime 2017-12-15 14:42
 */
@Configuration
public class TenantWebConfiguration extends BaseConfig {

    /**
     * caring.log.enabled = true 并且 caring.log.type=DB时实例该类
     *
     * @param optLogService
     * @return
     */
    @Bean
    @ConditionalOnExpression("${caring.log.enabled:true} && 'DB'.equals('${caring.log.type:LOGGER}')")
    public SysLogListener sysLogListener(OptLogService optLogService) {
        return new SysLogListener((log) -> optLogService.save(log));
    }
}
