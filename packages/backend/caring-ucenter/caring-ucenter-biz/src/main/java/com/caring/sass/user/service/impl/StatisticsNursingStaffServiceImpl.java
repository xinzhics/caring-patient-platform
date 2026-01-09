package com.caring.sass.user.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.user.dao.StatisticsNursingStaffMapper;
import com.caring.sass.user.entity.StatisticsNursingStaff;
import com.caring.sass.user.service.StatisticsNursingStaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务实现类
 * 护理医助统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Slf4j
@Service

public class StatisticsNursingStaffServiceImpl extends SuperServiceImpl<StatisticsNursingStaffMapper, StatisticsNursingStaff> implements StatisticsNursingStaffService {

    /**
     * 通过数据库聚合函数查询统计数据
     */
    @Override
    public void syncFromDb() {
        // 清空数据表
        baseMapper.delete(Wrappers.emptyWrapper());
        // 重新查找统计数据
        List<StatisticsNursingStaff> staffs = baseMapper.queryDb();
        // 持久化
        baseMapper.insertBatchSomeColumn(staffs);
    }

    @Override
    public Map<String, Long> queryMemberNum() {
        String userType = BaseContextHandler.getUserType();
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            Map<String, Long> ret = new HashMap<>();
            Long userId = BaseContextHandler.getUserId();
            Long doctorNum = baseMapper.queryCustomersDoctorNum(userId);
            Long patientNum = baseMapper.queryCustomersPatientNum(userId);
            Long nursingStaffNum = baseMapper.queryCustomersNursingStaffNum(userId);
            Long tenantNum = baseMapper.queryCustomersTenantNum(userId);
            ret.put("doctorNum", doctorNum);
            ret.put("patientNum", patientNum);
            ret.put("nursingStaffNum", nursingStaffNum);
            ret.put("tenantNum", tenantNum);
            return ret;
        } else {
            Map<String, Long> ret = new HashMap<>();
            Long doctorNum = baseMapper.queryAllDoctorNum();
            Long patientNum = baseMapper.queryAllPatientNum();
            Long nursingStaffNum = baseMapper.queryAllNursingStaffNum();
            Long tenantNum = baseMapper.queryAllTenantNum();
            ret.put("doctorNum", doctorNum);
            ret.put("patientNum", patientNum);
            ret.put("nursingStaffNum", nursingStaffNum);
            ret.put("tenantNum", tenantNum);
            return ret;
        }
    }
}
