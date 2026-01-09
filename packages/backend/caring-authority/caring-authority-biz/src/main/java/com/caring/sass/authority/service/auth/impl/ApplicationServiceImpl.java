package com.caring.sass.authority.service.auth.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.authority.dao.auth.ApplicationMapper;
import com.caring.sass.authority.entity.auth.Application;
import com.caring.sass.authority.service.auth.ApplicationService;
import com.caring.sass.base.service.SuperCacheServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.caring.sass.common.constant.CacheKey.APPLICATION;

/**
 * <p>
 * 业务实现类
 * 应用
 * </p>
 *
 * @author caring
 * @date 2019-12-15
 */
@Slf4j
@Service

public class ApplicationServiceImpl extends SuperCacheServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Override
    protected String getRegion() {
        return APPLICATION;
    }

    @Override
    public Application getByClient(String clientId, String clientSecret) {
        List<Application> applications = baseMapper.selectList(Wraps.<Application>lbQ()
                .eq(Application::getTenantCode, BaseContextHandler.getTenant())
                .eq(Application::getClientId, clientId)
                .eq(Application::getClientSecret, clientSecret));
        if (CollUtil.isNotEmpty(applications)) {
            return applications.get(0);
        }
        return null;
       /* String key = buildTenantKey(clientId, clientSecret);
        Function<String, Object> loader = (k) -> super.getObj(Wraps.<Application>lbQ()
                .select(Application::getId)
                .eq(Application::getTenantCode, BaseContextHandler.getTenant())
                .eq(Application::getClientId, clientId)
                .eq(Application::getClientSecret, clientSecret), Convert::toLong);
        return getByKey(APPLICATION_CLIENT, key, loader);*/
    }

    @Override
    public Application findByClientId(String clientId) {
        List<Application> applications = baseMapper.selectList(Wraps.<Application>lbQ().eq(Application::getClientId, clientId));
        if (CollUtil.isEmpty(applications)) {
            return null;
        }
        return applications.get(0);
    }

}
