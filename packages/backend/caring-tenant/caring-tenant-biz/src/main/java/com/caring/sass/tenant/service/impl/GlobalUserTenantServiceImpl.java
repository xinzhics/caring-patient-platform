package com.caring.sass.tenant.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.dao.GlobalUserTenantMapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dto.GlobalUserTenantPageDTO;
import com.caring.sass.tenant.entity.GlobalUserTenant;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.GlobalUserTenantService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 用户项目管理表
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-11
 */
@Slf4j
@Service

public class GlobalUserTenantServiceImpl extends SuperServiceImpl<GlobalUserTenantMapper, GlobalUserTenant> implements GlobalUserTenantService {

    @Autowired
    TenantMapper tenantMapper;

    /**
     * TODO:可能需要增加名称搜索的条件。
     * @param page
     * @param paramsModel
     */
    @Override
    public void pageTenant(IPage<Tenant> page, GlobalUserTenantPageDTO paramsModel) {

        LbqWrapper<Tenant> lbqWrapper = Wraps.<Tenant>lbQ()
                .apply(" id not in (select tenant_id FROM d_global_user_tenant)");

        if (StrUtil.isNotEmpty(paramsModel.getName())) {
            lbqWrapper.and(wq -> wq.like(Tenant::getName, paramsModel.getName()).or().like(Tenant::getWxName, paramsModel.getName()));
        }
        tenantMapper.selectPage(page, lbqWrapper);


    }


    @Override
    public void setTenantInfo(List<GlobalUserTenant> records) {

        if (CollUtil.isNotEmpty(records)) {
            List<Long> tenantIds = records.stream().map(GlobalUserTenant::getTenantId).collect(Collectors.toList());
            List<Tenant> tenantList = tenantMapper.selectList(Wraps.<Tenant>lbQ()
                    .select(SuperEntity::getId, Tenant::getName, Tenant::getStatus, Tenant::getLogo, Tenant::getWxName, Tenant::getDomainName)
                    .in(SuperEntity::getId, tenantIds));
            Map<Long, Tenant> tenantMap = tenantList.stream().collect(Collectors.toMap(SuperEntity::getId, itme -> itme, (o1, o2) -> o1));

            for (GlobalUserTenant record : records) {
                Tenant tenant = tenantMap.get(record.getTenantId());
                record.setTenantIcon(tenant.getLogo());
                record.setTenantName(tenant.getName());
                record.setTenantStatus(tenant.getStatus());
                record.setTenantDomain(tenant.getDomainName());
                record.setWxName(tenant.getWxName());
            }
        }


    }
}
