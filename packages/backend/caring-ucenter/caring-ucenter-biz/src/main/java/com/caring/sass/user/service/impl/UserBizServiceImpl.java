package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Group;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.GroupService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.UserBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName UserBizService
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 9:54
 * @Version 1.0
 */
@Service
public class UserBizServiceImpl implements UserBizService {

    @Autowired
    NursingStaffService nursingStaffService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    GroupService groupService;


    @Override
    public boolean createDefaultUser(Org org, String domain, String projectName) {
        NursingStaff nursingStaff = new NursingStaff();
        nursingStaff.setClassCode(org.getTreePath());
        nursingStaff.setLoginName(domain + Constant.SERVICE_LOGIN_NAME_SUFFIX);
        nursingStaff.setPassword(domain + Constant.DEFAULT_PASSWORD);
        nursingStaff.setOrganId(org.getId());
        nursingStaff.setOrganName(org.getLabel());
        nursingStaff.setOrganCode(org.getCode());
        nursingStaff.setName("默认医助");

        nursingStaff = nursingStaffService.createNursingStaff(nursingStaff);

        // 取消原先小组的设定
        Doctor doctor = new Doctor();
        doctor.setNursingId(nursingStaff.getId());
        doctor.setNursingName(nursingStaff.getName());
        doctor.setLoginName(domain + Constant.DOCTOR_LOGIN_NAME_SUFFIX);
        doctor.setOrganCode(org.getCode());
        doctor.setClassCode(org.getTreePath());
        doctor.setOrganId(org.getId());
        doctor.setOrganName(org.getLabel());
        doctor.setName("默认医生");
        doctor.setWxStatus(0);
        doctor.setImWxTemplateStatus(0);
        doctor.setImMsgStatus(1);
        doctor.setBuildIn(1);
        doctor.setDoctorLeader(0);
        doctor.setCloseAppoint(0);
        doctor.setHasGroup(0);
        doctor.setIndependence(1);
        doctorService.createDoctor(doctor);
        return true;
    }

    /**
     * 按项目删除用户信息
     */
    @Override
    public boolean deleteByTenant() {
        String tenant = BaseContextHandler.getTenant();
        if (StrUtil.isBlank(tenant)) {
            return true;
        }
        // 删除护理人员
        List<Long> staffIds = nursingStaffService.list(Wraps.<NursingStaff>lbQ().select(NursingStaff::getId))
                .stream().map(NursingStaff::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(staffIds)) {
            nursingStaffService.removeByIds(staffIds);
        }
        // 删除小组信息
        List<Long> groupIds = groupService.list(Wraps.<Group>lbQ().select(Group::getId))
                .stream().map(Group::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(staffIds)) {
            groupService.removeByIds(groupIds);
        }
        // 删除医生
        List<Long> doctorIds = doctorService.list(Wraps.<Doctor>lbQ().select(Doctor::getId))
                .stream().map(Doctor::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(doctorIds)) {
            doctorService.removeByIds(doctorIds);
        }
        return true;
    }
}
