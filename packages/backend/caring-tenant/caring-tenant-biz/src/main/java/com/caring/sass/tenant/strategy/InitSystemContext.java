package com.caring.sass.tenant.strategy;

import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.tenant.dto.TenantConnectDTO;
import com.caring.sass.utils.BizAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 初始化系统上下文
 *
 * @author caring
 * @date 2020年03月15日11:58:47
 */
@Component
public class InitSystemContext {
    private final Map<String, InitSystemStrategy> initSystemStrategyMap = new ConcurrentHashMap<>();
    private final DatabaseProperties databaseProperties;

    @Autowired
    public InitSystemContext(Map<String, InitSystemStrategy> strategyMap, DatabaseProperties databaseProperties) {
        strategyMap.forEach(this.initSystemStrategyMap::put);
        this.databaseProperties = databaseProperties;
    }

    /**
     * 初始化链接
     *
     * @param tenantConnect
     */
    public boolean initConnect(TenantConnectDTO tenantConnect) {
        InitSystemStrategy initSystemStrategy = initSystemStrategyMap.get(databaseProperties.getMultiTenantType().name());
        BizAssert.notNull(initSystemStrategy, String.format("您配置的租户模式:{}不可用", databaseProperties.getMultiTenantType().name()));

        return initSystemStrategy.initConnect(tenantConnect);
    }

    /**
     * 初始化链接附带项目的机构、栏目、模板消息数据
     *
     * @param tenantConnect
     */
    public boolean initConnectWithAdditional(TenantConnectDTO tenantConnect) {
        InitSystemStrategy initSystemStrategy = initSystemStrategyMap.get(databaseProperties.getMultiTenantType().name());
        BizAssert.notNull(initSystemStrategy, String.format("您配置的租户模式:{}不可用", databaseProperties.getMultiTenantType().name()));

        return initSystemStrategy.initConnectWithAdditional(tenantConnect);
    }


    /**
     * 重置系统
     *
     * @param tenant
     */
    public boolean reset(String tenant) {
        InitSystemStrategy initSystemStrategy = initSystemStrategyMap.get(databaseProperties.getMultiTenantType().name());
        BizAssert.notNull(initSystemStrategy, String.format("您配置的租户模式:{}不可用", databaseProperties.getMultiTenantType().name()));
        return initSystemStrategy.reset(tenant);
    }

    /**
     * 删除租户数据
     *
     * @param tenantCodeList
     */
    public boolean delete(List<Long> ids, List<String> tenantCodeList) {
        InitSystemStrategy initSystemStrategy = initSystemStrategyMap.get(databaseProperties.getMultiTenantType().name());
        BizAssert.notNull(initSystemStrategy, String.format("您配置的租户模式:{}不可用", databaseProperties.getMultiTenantType().name()));

        return initSystemStrategy.delete(ids, tenantCodeList);
    }
}
