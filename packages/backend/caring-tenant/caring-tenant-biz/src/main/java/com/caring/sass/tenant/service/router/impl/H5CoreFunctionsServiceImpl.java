package com.caring.sass.tenant.service.router.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dao.router.H5CoreFunctionsMapper;
import com.caring.sass.tenant.dao.router.H5RouterMapper;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.service.router.H5CoreFunctionsService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 患者个人中心核心功能
 * </p>
 *
 * @author 杨帅
 * @date 2023-06-27
 */
@Slf4j
@Service

public class H5CoreFunctionsServiceImpl extends SuperServiceImpl<H5CoreFunctionsMapper, H5CoreFunctions> implements H5CoreFunctionsService {

    @Autowired
    TenantMapper tenantMapper;

    @Autowired
    H5RouterMapper h5RouterMapper;

    @Override
    public void initH5CoreFunction() {
        List<Tenant> tenantList = tenantMapper.selectList(Wraps.<Tenant>lbQ());
        List<H5CoreFunctions> h5CoreFunctions = new ArrayList<>();
        for (Tenant tenant : tenantList) {
            String code = tenant.getCode();
            BaseContextHandler.setTenant(code);

            Integer integer = baseMapper.selectCount(Wraps.<H5CoreFunctions>lbQ());
            if (integer == null || integer == 0) {
                H5Router imMenu = h5RouterMapper.selectOne(Wraps.<H5Router>lbQ().eq(H5Router::getDictItemId, 16).last("limit 0,1"));
                H5CoreFunctions coreFunctions;
                if (imMenu == null) {
                    coreFunctions = H5CoreFunctions.init(null);
                } else {
                    coreFunctions = H5CoreFunctions.init(imMenu.getStatus());
                }
                h5CoreFunctions.add(coreFunctions);
            }
        }
        if (CollUtil.isNotEmpty(h5CoreFunctions)) {
            baseMapper.insertBatchSomeColumn(h5CoreFunctions);
        }

    }








}
