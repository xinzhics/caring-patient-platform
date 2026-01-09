package com.caring.sass.ucenter.config.datasource;


import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.database.datasource.BaseMybatisConfiguration;
import com.caring.sass.database.mybatis.auth.DataScopeInterceptor;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置一些拦截器
 *
 * @author caring
 * @createTime 2017-11-18 0:34
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({DatabaseProperties.class})
public class OauthMybatisAutoConfiguration extends BaseMybatisConfiguration {


    public OauthMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }

    @Override
    protected List<InnerInterceptor> getPaginationBeforeInnerInterceptor() {
        List<InnerInterceptor> list = new ArrayList<>();
        Boolean isDataScope = databaseProperties.getIsDataScope();
        if (isDataScope) {
            list.add(new DataScopeInterceptor(userId -> SpringUtils.getBean(UserService.class).getDataScopeById(userId)));
        }
        return list;
    }

}
