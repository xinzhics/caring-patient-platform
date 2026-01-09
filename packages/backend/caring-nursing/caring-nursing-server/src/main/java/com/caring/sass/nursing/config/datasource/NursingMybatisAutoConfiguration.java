package com.caring.sass.nursing.config.datasource;


import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 护理计划-Mybatis 常用重用拦截器
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({DatabaseProperties.class})
public class NursingMybatisAutoConfiguration extends BaseMybatisConfiguration {

    public NursingMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }

    @Override
    protected List<InnerInterceptor> getPaginationBeforeInnerInterceptor() {
        List<InnerInterceptor> list = new ArrayList<>();
        Boolean isDataScope = databaseProperties.getIsDataScope();
        if (isDataScope) {
            list.add(new DataScopeInterceptor(userId -> SpringUtils.getBean(UserApi.class).getDataScopeById(userId)));
        }
        return list;
    }
}
