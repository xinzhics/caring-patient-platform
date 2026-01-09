package com.caring.sass.user.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.user.dao.GroupMapper;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Group;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.GroupService;
import com.caring.sass.user.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 小组
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Service

public class GroupServiceImpl extends SuperServiceImpl<GroupMapper, Group> implements GroupService {

    @Autowired
    TenantApi tenantApi;

    @Autowired
    PatientService patientService;

    @Autowired
    DoctorService doctorService;

    @Override
    public Group createGroup(Group group) {
        int insert = baseMapper.insert(group);
        return group;
    }


    @Override
    public Group getById(Serializable id) {
        Group group = baseMapper.selectById(id);
        if (Objects.nonNull(group)) {
            long patient = patientService.countPatientByGroupId(group.getId());
            group.setPatientCount(patient);
            long doctor = doctorService.countDoctorByGroupId(group.getId());
            group.setDoctorCount(doctor);
        }
        return group;
    }


    @Override
    public IPage<Group> findPage(IPage<Group> page, LbqWrapper<Group> wrapper) {
        return baseMapper.findPage(page, wrapper, new DataScope());
    }


    @Override
    public void desensitization(List<Group> pageRecords) {

        if (pageRecords.isEmpty()) {
            return;
        }

        String userType = BaseContextHandler.getUserType();
        if (UserType.ADMIN.equals(userType)) {
            return;
        }

        String tenant = BaseContextHandler.getTenant();
        R<Boolean> securitySettings = tenantApi.queryDataSecuritySettings(tenant);
        if (securitySettings.getIsSuccess()) {
            Boolean data = securitySettings.getData();
            if (data == null || !data) {
                return;
            }
        } else {
            return;
        }

        for (Group record : pageRecords) {
            // 对 换着的 姓名。 联系方式 脱敏
            record.setNurseName(SensitiveInfoUtils.desensitizeName(record.getNurseName()));
        }

    }
}
