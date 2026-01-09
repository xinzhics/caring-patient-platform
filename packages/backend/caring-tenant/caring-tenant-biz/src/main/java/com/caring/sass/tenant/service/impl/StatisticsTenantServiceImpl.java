package com.caring.sass.tenant.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.dao.GlobalUserTenantMapper;
import com.caring.sass.tenant.dao.StatisticsTenantMapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dto.StatisticsTenantPageDTO;
import com.caring.sass.tenant.entity.GlobalUserTenant;
import com.caring.sass.tenant.entity.StatisticsTenant;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.StatisticsTenantService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.user.entity.StatisticsNursingStaff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 项目统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Slf4j
@Service
public class StatisticsTenantServiceImpl extends SuperServiceImpl<StatisticsTenantMapper, StatisticsTenant> implements StatisticsTenantService {

    @Override
    public void syncFromDb() {
        // 清空数据表
        baseMapper.delete(Wrappers.emptyWrapper());
        // 重新查找统计数据
        List<StatisticsTenant> staffs = baseMapper.queryDb();
        // 持久化
        baseMapper.insertBatchSomeColumn(staffs);
    }

    @Autowired
    GlobalUserTenantMapper globalUserTenantMapper;

    @Autowired
    TenantMapper tenantMapper;

    @Override
    public void page(IPage<StatisticsTenant> iPage, StatisticsTenantPageDTO model) {
        LbqWrapper<StatisticsTenant> lbqWrapper = Wraps.<StatisticsTenant>lbQ();

        String userType = BaseContextHandler.getUserType();
        Long userId = BaseContextHandler.getUserId();
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            List<GlobalUserTenant> userTenants = globalUserTenantMapper.selectList(Wraps.<GlobalUserTenant>lbQ().eq(GlobalUserTenant::getAccountId, userId));
            if (CollUtil.isNotEmpty(userTenants)) {
                List<Long> tenantIds = userTenants.stream().map(GlobalUserTenant::getTenantId).collect(Collectors.toList());
                List<Tenant> tenantList = tenantMapper.selectList(Wraps.<Tenant>lbQ().in(SuperEntity::getId, tenantIds).select(SuperEntity::getId, Tenant::getCode));
                if (CollUtil.isNotEmpty(tenantList)) {
                    List<String> tenantCode = tenantList.stream().map(Tenant::getCode).collect(Collectors.toList());
                    lbqWrapper.in(StatisticsTenant::getCode, tenantCode);
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        baseMapper.selectPage(iPage, lbqWrapper);
    }


    @Override
    public Map<String, Long> queryMemberNum() {

        String userType = BaseContextHandler.getUserType();
        QueryWrapper<StatisticsTenant> lbqWrapper = new QueryWrapper<>();
        lbqWrapper.select("count(id) as tenantNum, sum(patient_num) as patientNum ,sum(doctor_num) as doctorNum, sum(nursing_staff_num) as nursingNum");
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            Long userId = BaseContextHandler.getUserId();
            lbqWrapper.apply(" code in (select dt.code FROM d_tenant dt where dt.id in (select tenant_id FROM d_global_user_tenant WHERE account_id = '"+ userId+"'))");
        }
        List<Map<String, Object>> mapList = baseMapper.selectMaps(lbqWrapper);
        Map<String, Long> ret = new HashMap<>();
        for (Map<String, Object> stringObjectMap : mapList) {
            Object tenantNum = stringObjectMap.get("tenantNum");
            if (tenantNum != null) {
                ret.put("tenantNum", Long.parseLong(tenantNum.toString()));
            }
            Object patientNum = stringObjectMap.get("patientNum");
            if (patientNum != null) {
                ret.put("patientNum", Long.parseLong(patientNum.toString()));
            }
            Object doctorNum = stringObjectMap.get("doctorNum");
            if (doctorNum != null) {
                ret.put("doctorNum", Long.parseLong(doctorNum.toString()));
            }
            Object nursingNum = stringObjectMap.get("nursingNum");
            if (nursingNum != null) {
                ret.put("nursingStaffNum", Long.parseLong(nursingNum.toString()));
            }
        }
        return ret;

    }
}
