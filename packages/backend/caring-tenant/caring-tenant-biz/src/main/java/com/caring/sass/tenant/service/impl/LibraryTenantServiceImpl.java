package com.caring.sass.tenant.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dao.LibraryTenantMapper;
import com.caring.sass.tenant.entity.LibraryTenant;
import com.caring.sass.tenant.service.LibraryTenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class LibraryTenantServiceImpl implements LibraryTenantService {

    @Autowired
    LibraryTenantMapper libraryTenantMapper;

    /**
     * 项目复制 cms 管理
     * @param formTenant
     * @param toFormTenant
     */
    @Override
    public void copyLibraryTenant(String formTenant, String toFormTenant) {
        try {
            BaseContextHandler.setTenant(formTenant);
            List<LibraryTenant> tenantList = libraryTenantMapper.selectList(Wraps.<LibraryTenant>lbQ());
            if (CollUtil.isNotEmpty(tenantList)) {
                BaseContextHandler.setTenant(toFormTenant);
                for (LibraryTenant tenant : tenantList) {
                    tenant.setId(null);
                }
                libraryTenantMapper.insertBatchSomeColumn(tenantList);
            }
        } catch (Exception e) {

        }finally {
            BaseContextHandler.setTenant(formTenant);
        }

    }




}
