package com.caring.sass.cms.config;

import com.caring.sass.boot.config.BaseConfig;
import org.springframework.context.annotation.Configuration;
import com.caring.sass.oauth.api.LogApi;
import com.caring.sass.log.event.SysLogListener;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

/**
 * 内容管理-Web配置
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Configuration
public class CmsWebConfiguration extends BaseConfig {

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
