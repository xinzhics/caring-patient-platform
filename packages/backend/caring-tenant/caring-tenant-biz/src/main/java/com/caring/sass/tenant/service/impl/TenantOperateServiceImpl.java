package com.caring.sass.tenant.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.tenant.dao.GlobalUserMapper;
import com.caring.sass.tenant.dao.GlobalUserTenantMapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dao.TenantOperateMapper;
import com.caring.sass.tenant.dto.TenantDayRemindDto;
import com.caring.sass.tenant.entity.GlobalUser;
import com.caring.sass.tenant.entity.GlobalUserTenant;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.TenantOperate;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.tenant.service.TenantOperateService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.user.dto.NursingStaffUpdateDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 项目运营配置
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-10
 */
@Slf4j
@Service

public class TenantOperateServiceImpl extends SuperServiceImpl<TenantOperateMapper, TenantOperate> implements TenantOperateService {

    @Autowired
    GlobalUserMapper globalUserMapper;

    @Autowired
    GlobalUserTenantMapper globalUserTenantMapper;

    @Autowired
    TenantMapper tenantMapper;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    NursingStaffApi nursingStaffApi;

    @Autowired
    OrgService orgService;

    @Autowired
    UserService userService;


    /**
     * 更新运营配置，更新项目的剩余天数
     * @param entity
     * @return
     */
    @Transactional
    @Override
    public boolean saveOrUpdate(TenantOperate entity) {

        if (entity.getOrganLimitation() == 1) {
            if (entity.getOrganLimitationNumber() == null) {
                throw new BizException("机构限制数量不能为空");
            }
        }
        if (entity.getDoctorLimitation() == 1) {
            if (entity.getDoctorLimitationNumber() == null) {
                throw new BizException("医生限制数量不能为空");
            }
        }
        if (entity.getNursingLimitation() == 1) {
            if (entity.getNursingLimitationNumber() == null) {
                throw new BizException("医助限制数量不能为空");
            }
        }
        String tenantCode = entity.getTenantCode();
        Tenant tenant = tenantMapper.getByCode(tenantCode);
        if (Objects.isNull(tenant)) {
            throw new BizException("项目不存在");
        }
        TenantOperate operate = baseMapper.selectOne(Wraps.<TenantOperate>lbQ().eq(TenantOperate::getTenantCode, tenantCode)
                .last("limit 0,1"));
        if (Objects.nonNull(operate)) {
            entity.setId(operate.getId());
        }
        LocalDateTime expirationTime = entity.getExpirationTime();
        long days = expirationTime.toLocalDate().toEpochDay() - LocalDate.now().toEpochDay();
        int intDays = Integer.parseInt(days + "");
        tenant.setDaysRemaining(intDays);
        entity.setDaysRemaining(intDays);
        entity.setTenantId(tenant.getId());
        if (Objects.isNull(entity.getId())) {
            baseMapper.insert(entity);
        } else {
            baseMapper.updateById(entity);
        }
        // 更新项目的有效时长
        if (entity.getDurationLimitation()  != null) {
            if (entity.getDurationLimitation() == 1) {
                tenantMapper.updateById(tenant);
            } else {
                UpdateWrapper<Tenant> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", tenant.getId());
                updateWrapper.set("days_remaining", null);
                tenantMapper.update(new Tenant(), updateWrapper);
            }
        }
        return true;
    }


    /**
     * 查询 创建的或管理的项目 剩余时长是30天的项目名称
     * @param userId
     * @return
     */
    @Override
    public List<String> adminQuery30DayTenantName(Long userId) {
        GlobalUser globalUser = globalUserMapper.selectById(userId);
        if (Objects.isNull(globalUser)) {
            return new ArrayList<>();
        }
        if (globalUser.getLatestAccessTime() != null && globalUser.getLatestAccessTime().equals(LocalDate.now())) {
            return new ArrayList<>();
        }
        List<TenantOperate> operates = new ArrayList<>();
        if (BizConstant.THIRD_PARTY_CUSTOMERS.equals(globalUser.getGlobalUserType())) {
            List<GlobalUserTenant> userTenants = globalUserTenantMapper.selectList(Wraps.<GlobalUserTenant>lbQ()
                    .select(GlobalUserTenant::getTenantId, SuperEntity::getId)
                    .eq(GlobalUserTenant::getAccountId, userId));
            if (CollUtil.isEmpty(userTenants)) {
                return new ArrayList<>();
            }
            List<Long> tenantIds = userTenants.stream().map(GlobalUserTenant::getTenantId).collect(Collectors.toList());
            String idsJoin = ListUtils.getSqlIdsJoin(tenantIds);
            operates = baseMapper.selectList(Wraps.<TenantOperate>lbQ()
                    .select(SuperEntity::getId, TenantOperate::getTenantCode)
                    .eq(TenantOperate::getDurationLimitation, 1)
                    .eq(TenantOperate::getDaysRemaining, 30)
                    .apply(" tenant_code in (SELECT code from d_tenant where id in (" + idsJoin + ")) "));
        }
        if (BizConstant.ADMIN.equals(globalUser.getGlobalUserType())) {
            operates = baseMapper.selectList(Wraps.<TenantOperate>lbQ()
                    .select(SuperEntity::getId, TenantOperate::getTenantCode)
                    .eq(TenantOperate::getDurationLimitation, 1)
                    .eq(TenantOperate::getDaysRemaining, 30)
                    .apply(" tenant_code in (SELECT code from d_tenant where create_user = " + userId + ") "));
        }
        if (CollUtil.isEmpty(operates)) {
            return new ArrayList<>();
        }
        globalUser.setLatestAccessTime(LocalDate.now());
        globalUserMapper.updateById(globalUser);
        List<String> tenantCodes = operates.stream().map(TenantOperate::getTenantCode).collect(Collectors.toList());
        List<Tenant> tenantList = tenantMapper.selectList(Wraps.<Tenant>lbQ().select(SuperEntity::getId, Tenant::getName)
                .in(Tenant::getCode, tenantCodes));
        return tenantList.stream().map(Tenant::getName).collect(Collectors.toList());
    }

    /**
     * 管理员登录后 验证是否要提醒
     * @param userId
     * @return
     */
    @Override
    public TenantDayRemindDto userQueryTenantDay(Long userId) {

        User user = userService.getById(userId);
        TenantDayRemindDto dayRemindDto = new TenantDayRemindDto();
        dayRemindDto.setDaysRemaining(-1);
        if (Objects.isNull(user)) {
            return dayRemindDto;
        }
        if (user.getLatestAccessTime() != null && user.getLatestAccessTime().equals(LocalDate.now())) {
            return dayRemindDto;
        }
        String tenantCode = BaseContextHandler.getTenant();
        TenantOperate tenantOperate = getTenantOperate();
        if (Objects.isNull(tenantOperate)) {
            return dayRemindDto;
        }
        user.setLatestAccessTime(LocalDate.now());
        userService.updateUser(user);
        Integer daysRemaining = tenantOperate.getDaysRemaining();
        if (remindDay().contains(daysRemaining)) {
            dayRemindDto.setDaysRemaining(daysRemaining);

            // 查询项目是否是 admin 创建的。
            GlobalUser admin = globalUserMapper.selectOne(Wraps.<GlobalUser>lbQ()
                    .eq(GlobalUser::getAccount, "admin")
                    .last("limit 0,1"));
            if (Objects.isNull(admin)) {
                dayRemindDto.setShowCaringAdminContact(1);
                return dayRemindDto;
            }
            Tenant tenant = tenantMapper.getByCode(tenantCode);
            if (tenant.getCreateUser() == null || admin.getId().equals(tenant.getCreateUser())) {
                dayRemindDto.setShowCaringAdminContact(1);
            } else {
                GlobalUser globalUser = globalUserMapper.selectById(tenant.getCreateUser());
                if (Objects.isNull(globalUser)) {
                    dayRemindDto.setShowCaringAdminContact(1);
                } else {
                    dayRemindDto.setShowCaringAdminContact(0);
                    dayRemindDto.setContactsName(globalUser.getName());
                    dayRemindDto.setContactsPhone(globalUser.getMobile());
                }
            }
        }
        return dayRemindDto;
    }


    /**
     * 医助登录后 验证是否要提醒
     * @param userId
     * @return
     */
    @Override
    public Integer nursingQueryTenantDay(Long userId) {

        R<NursingStaff> nursingStaffR = nursingStaffApi.get(userId);
        NursingStaff staffRData = nursingStaffR.getData();
        if (Objects.isNull(staffRData)) {
            return -1;
        }
        if (staffRData.getLatestAccessTime() != null && staffRData.getLatestAccessTime().equals(LocalDate.now())) {
            return -1;
        }
        NursingStaffUpdateDTO nursingStaffUpdateDTO = new NursingStaffUpdateDTO();
        nursingStaffUpdateDTO.setId(userId);
        nursingStaffUpdateDTO.setName(staffRData.getName());
        nursingStaffUpdateDTO.setLatestAccessTime(LocalDate.now());
        nursingStaffApi.update(nursingStaffUpdateDTO);
        TenantOperate tenantOperate = getTenantOperate();
        return getDaysRemaining(tenantOperate);
    }



    /**
     * 医生登录查询项目剩余时长
     * @param userId
     * @return
     */
    @Override
    public Integer doctorQueryTenantDay(Long userId) {

        R<Doctor> doctorR = doctorApi.get(userId);
        Doctor doctor = doctorR.getData();
        if (Objects.isNull(doctor)) {
            return -1;
        }
        if (doctor.getLatestAccessTime() != null && doctor.getLatestAccessTime().equals(LocalDate.now())) {
            return -1;
        }
        doctor.setLatestAccessTime(LocalDate.now());
        doctorApi.update(doctor);
        TenantOperate tenantOperate = getTenantOperate();
        return getDaysRemaining(tenantOperate);
    }

    @Override
    public Integer createDoctorNumberCheck() {

        R<Integer> doctor = doctorApi.countDoctor();
        String tenant = BaseContextHandler.getTenant();
        TenantOperate operate = baseMapper.selectOne(Wraps.<TenantOperate>lbQ()
                .select(SuperEntity::getId, TenantOperate::getTenantCode, TenantOperate::getDoctorLimitationNumber)
                .eq(TenantOperate::getDoctorLimitation, 1)
                .eq(TenantOperate::getTenantCode, tenant));
        if (operate == null) {
            return 0;
        }
        if (doctor.getIsSuccess()) {
            Integer data = doctor.getData();
            if (data >= operate.getDoctorLimitationNumber()) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public void tenantOperationalEvents() {

        // 查询 限制项目时长的 运营配置。
        List<TenantOperate> tenantOperates = baseMapper.selectList(Wraps.<TenantOperate>lbQ()
                .select(SuperEntity::getId, TenantOperate::getTenantId, TenantOperate::getDaysRemaining)
                .eq(TenantOperate::getDurationLimitation, 1)
                .gt(TenantOperate::getDaysRemaining, 0));
        if (CollUtil.isEmpty(tenantOperates)) {
            return;
        }
        for (TenantOperate operate : tenantOperates) {
            Integer daysRemaining = operate.getDaysRemaining();
            if (daysRemaining == null) {
                continue;
            }
            Tenant one = tenantMapper.selectOne(Wraps.<Tenant>lbQ()
                    .select(SuperEntity::getId, Tenant::getStatus)
                    .eq(SuperEntity::getId, operate.getTenantId()));
            if (TenantStatusEnum.NORMAL.eq(one.getStatus())) {
                daysRemaining = daysRemaining - 1;
                if (daysRemaining == -1) {
                    operate.setDaysRemaining(0);
                } else {
                    operate.setDaysRemaining(daysRemaining);
                }
                Tenant tenant = new Tenant();
                tenant.setId(operate.getTenantId());
                tenant.setDaysRemaining(daysRemaining);
                tenantMapper.updateById(tenant);
                baseMapper.updateById(operate);
            }
        }
    }

    @Override
    public Integer createNursingNumberCheck() {


        R<Integer> countUser = nursingStaffApi.countUser();
        String tenant = BaseContextHandler.getTenant();
        TenantOperate operate = baseMapper.selectOne(Wraps.<TenantOperate>lbQ()
                .select(SuperEntity::getId, TenantOperate::getTenantCode, TenantOperate::getNursingLimitationNumber)
                .eq(TenantOperate::getNursingLimitation, 1)
                .eq(TenantOperate::getTenantCode, tenant));
        if (operate == null) {
            return 0;
        }
        if (countUser.getIsSuccess()) {
            Integer data = countUser.getData();
            if (data >= operate.getNursingLimitationNumber()) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public Integer createOrganNumberCheck() {

        String tenant = BaseContextHandler.getTenant();
        TenantOperate operate = baseMapper.selectOne(Wraps.<TenantOperate>lbQ()
                .select(SuperEntity::getId, TenantOperate::getTenantCode, TenantOperate::getOrganLimitationNumber)
                .eq(TenantOperate::getOrganLimitation, 1)
                .eq(TenantOperate::getTenantCode, tenant));
        if (operate == null) {
            return 0;
        }
        int count = orgService.count(Wraps.<Org>lbQ().eq(Org::getReadonly, false));
        if (count >= operate.getOrganLimitationNumber()) {
            return -1;
        }
        return 0;
    }

    /**
     * 查询开启时间限制的项目配置
     * @return
     */
    private TenantOperate getTenantOperate() {
        String tenantCode = BaseContextHandler.getTenant();
        return baseMapper.selectOne(Wraps.<TenantOperate>lbQ()
                .select(SuperEntity::getId, TenantOperate::getDaysRemaining)
                .eq(TenantOperate::getDurationLimitation, 1)
                .eq(TenantOperate::getTenantCode, tenantCode));
    }

    private Integer getDaysRemaining(TenantOperate tenantOperate) {
        if (Objects.isNull(tenantOperate)) {
            return -1;
        }
        Integer daysRemaining = tenantOperate.getDaysRemaining();
        if (remindDay().contains(daysRemaining)) {
            return daysRemaining;
        }
        return -1;
    }

    private Set<Integer> remindDay() {
        Set<Integer> days = new HashSet<>();
        days.add(30);
        days.add(15);
        days.add(7);
        days.add(3);
        days.add(2);
        days.add(1);
        days.add(0);
        return days;
    }


}
