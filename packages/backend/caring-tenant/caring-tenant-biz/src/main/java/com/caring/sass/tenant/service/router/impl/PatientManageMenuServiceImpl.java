package com.caring.sass.tenant.service.router.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dao.router.PatientManageMenuMapper;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.router.PatientManageMenu;
import com.caring.sass.tenant.service.router.PatientManageMenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.taskdefs.War;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 患者管理平台菜单
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Service

public class PatientManageMenuServiceImpl extends SuperServiceImpl<PatientManageMenuMapper, PatientManageMenu> implements PatientManageMenuService {

    @Autowired
    TenantMapper tenantMapper;

    /**
     * 功能上线时，初始化
     */
    @Override
    public void init() {
        List<Tenant> tenantList = tenantMapper.selectList(Wraps.<Tenant>lbQ().select(SuperEntity::getId, Tenant::getCode));
        for (Tenant tenant : tenantList) {
            createMenu(tenant.getCode());
        }
    }

    /**
     * 创建患者管理平台默认菜单
     */
    @Override
    public List<PatientManageMenu> createMenu(String code) {
        BaseContextHandler.setTenant(code);
        List<PatientManageMenu> menuList = new ArrayList<>();
        menuList.add(PatientManageMenu.builder().name("信息完整度").menuType(PatientManageMenu.INFORMATION_INTEGRITY).icon("https://caring.obs.cn-north-4.myhuaweicloud.com/appIcon/information_integrity.png").showStatus(1).menuSort(0).build());
        menuList.add(PatientManageMenu.builder().name("监测数据").menuType(PatientManageMenu.MONITOR_DATA).icon("https://caring.obs.cn-north-4.myhuaweicloud.com/appIcon/monitor_data.png").showStatus(1).menuSort(1).build());
        menuList.add(PatientManageMenu.builder().name("用药预警").menuType(PatientManageMenu.MEDICATION_WARNING).icon("https://caring.obs.cn-north-4.myhuaweicloud.com/appIcon/medication_warning.png").showStatus(1).menuSort(2).build());
        menuList.add(PatientManageMenu.builder().name("异常选项跟踪").menuType(PatientManageMenu.EXCEPTION_OPTION_TRACKING).icon("https://caring.obs.cn-north-4.myhuaweicloud.com/appIcon/trace_into.png").showStatus(1).menuSort(3).build());
        menuList.add(PatientManageMenu.builder().name("未完成任务跟踪").menuType(PatientManageMenu.NOT_FINISHED_TRACKING).icon("https://caring.obs.cn-north-4.myhuaweicloud.com/appIcon/NOT_FINISHED_TRACKING.png").showStatus(0).menuSort(4).build());
        baseMapper.insertBatchSomeColumn(menuList);
        return menuList;
    }

    /**
     * 复制项目时 。复制菜单
     * @param formCode
     * @param toCode
     */
    @Override
    public void copy(String formCode, String toCode) {
        BaseContextHandler.setTenant(formCode);
        List<PatientManageMenu> manageMenus = baseMapper.selectList(Wraps.<PatientManageMenu>lbQ());
        if (CollUtil.isEmpty(manageMenus)) {
            createMenu(toCode);
        } else {
            manageMenus.forEach(item -> item.setId(null));
            BaseContextHandler.setTenant(toCode);
            baseMapper.insertBatchSomeColumn(manageMenus);
        }
        BaseContextHandler.setTenant(formCode);
    }
}
