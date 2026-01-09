package com.caring.open.service.config.datasource;


import com.caring.sass.database.datasource.BaseMybatisConfiguration;
import com.caring.sass.database.mybatis.auth.DataScopeInterceptor;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.oauth.api.UserApi;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 配置一些 Mybatis 常用重用拦截器
 *
 * @author caring
 * @createTime 2017-11-18 0:34
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({DatabaseProperties.class})
public class AuthorityMybatisAutoConfiguration extends BaseMybatisConfiguration {

    public AuthorityMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }

    /**
     * 数据权限插件
     *
     * @return DataScopeInterceptor
     */
    @Order(10)
    @Bean
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "isDataScope", havingValue = "true", matchIfMissing = true)
    public DataScopeInterceptor dataScopeInterceptor() {
        return new DataScopeInterceptor((userId) -> SpringUtils.getBean(UserApi.class).getDataScopeById(userId));
    }

}
