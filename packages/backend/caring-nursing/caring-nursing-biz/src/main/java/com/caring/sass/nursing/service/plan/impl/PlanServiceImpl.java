package com.caring.sass.nursing.service.plan.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dto.form.CopyPlanDTO;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.plan.*;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;
import com.caring.sass.nursing.enumeration.AdminTemplateEnum;
import com.caring.sass.nursing.enumeration.FollowUpPlanTypeEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.follow.FollowTaskContentService;
import com.caring.sass.nursing.service.follow.FollowTaskService;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import com.caring.sass.nursing.service.form.FormService;
import com.caring.sass.nursing.service.plan.*;
import com.caring.sass.nursing.service.statistics.StatisticsTaskService;
import com.caring.sass.nursing.util.PlanDict;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 护理计划（随访服务）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service
public class PlanServiceImpl extends SuperServiceImpl<PlanMapper, Plan> implements PlanService {


    private final PlanDetailService planDetailService;

    private final PlanDetailTimeService planDetailTimeService;

    private final PlanTagService planTagService;

    private final AimService aimService;

    private final DatabaseProperties databaseProperties;

    private final FormService formService;

    @Autowired
    PlanLearnDetailService planLearnDetailService;

    ReminderLogService reminderLogService;

    @Autowired
    StatisticsTaskService statisticsTaskService;

    @Autowired
    H5RouterApi h5RouterApi;

    @Autowired
    FollowTaskService followTaskService;

    @Autowired
    FollowTaskContentService followTaskContentService;

    FunctionConfigurationService functionConfigurationService;

    @Autowired
    MsgPatientSystemMessageApi patientSystemMessageApi;

    PatientNursingPlanService patientNursingPlanService;


    public PlanServiceImpl(DatabaseProperties databaseProperties, PlanDetailService planDetailService,
                           PlanDetailTimeService planDetailTimeService, PlanTagService planTagService, AimService aimService
                            ,FormService formService) {
        this.databaseProperties = databaseProperties;
        this.planDetailService = planDetailService;
        this.planDetailTimeService = planDetailTimeService;
        this.planTagService = planTagService;
        this.aimService = aimService;
        this.formService = formService;
    }

    public ReminderLogService getReminderLogService() {
        if (reminderLogService == null) {
            reminderLogService = SpringUtils.getBean(ReminderLogService.class);
        }
        return reminderLogService;
    }

    public FunctionConfigurationService getFunctionConfigurationService() {
        if (functionConfigurationService == null) {
            functionConfigurationService = SpringUtils.getBean(FunctionConfigurationService.class);
        }
        return functionConfigurationService;
    }

    public PatientNursingPlanService getPatientNursingPlanService() {
        if (patientNursingPlanService == null) {
            patientNursingPlanService = SpringUtils.getBean(PatientNursingPlanService.class);
        }
        return patientNursingPlanService;
    }

    @Override
    public List<Plan> getPlan(Integer isAdminTemplate,  String followUpPlanType) {
        LbqWrapper<Plan> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Plan::getIsAdminTemplate, isAdminTemplate);
        lbqWrapper.eq(Plan::getFollowUpPlanType, followUpPlanType);
        return baseMapper.selectList(lbqWrapper);
    }

    @Override
    public List<Plan> getPlan(Integer isAdminTemplate, Integer status, String followUpPlanType) {
        LbqWrapper<Plan> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Plan::getIsAdminTemplate, isAdminTemplate);
        lbqWrapper.eq(Plan::getStatus, status);
        lbqWrapper.eq(Plan::getFollowUpPlanType, followUpPlanType);
        return baseMapper.selectList(lbqWrapper);
    }


    /**
     * @return com.caring.sass.nursing.entity.plan.Plan
     * @Author yangShuai
     * @Description 获取一个护理计划。包括其他所有的设置
     * @Date 2020/10/24 17:10
     */
    @Override
    public Plan getOne(Long planId) {
        Plan plan = baseMapper.selectById(planId);
        PlanTag planTag = planTagService.getByPlanId(planId);
        if (Objects.nonNull(planTag)) {
            plan.setPlanTag(planTag);
            plan.setTagId(planTag.getTagId());
        }
        List<PlanDetailDTO> detailDTOS = planDetailService.findDetailWithTimeById(plan.getId());
        plan.setPlanDetailList(detailDTOS);
        return plan;
    }

    @Override
    public Long getPLanIdByPlanType(Integer planType) {
        Plan one = baseMapper.selectOne(Wraps.<Plan>lbQ()
                .eq(Plan::getPlanType, planType).last(" limit 0,1 ")
                .select(SuperEntity::getId, Plan::getName));
        if (Objects.isNull(one)) {
            return null;
        }
        return one.getId();
    }


    @Override
    public Plan getPlanAndDetails(Long planId) {
        Plan plan = baseMapper.selectById(planId);
        if (Objects.nonNull(plan)) {
            List<PlanDetailDTO> detailDTOS = planDetailService.findDetailWithTimeByIdOrderByTime(plan.getId());
            plan.setPlanDetailList(detailDTOS);
        }
        return plan;
    }

    @Override
    public List<Plan> getPlanAndDetails(List<Long> planIds) {
        if (CollUtil.isEmpty(planIds)) {
            return new ArrayList<>();
        }
        List<Plan> planList = baseMapper.selectBatchIds(planIds);
        if (CollUtil.isEmpty(planList)) {
            return new ArrayList<>();
        }
        setPlanDetail(planList, planIds);
        return planList;
    }

    private void setPlanDetail(List<Plan> planList, List<Long> planIds) {
        List<PlanDetail> planDetails = planDetailService.list(Wraps.<PlanDetail>lbQ().in(PlanDetail::getNursingPlanId, planIds));
        if (CollUtil.isEmpty(planDetails)) {
            return;
        }
        Set<Long> planDetailIds = planDetails.stream().map(SuperEntity::getId).collect(Collectors.toSet());
        List<PlanDetailTime> detailTimes = planDetailTimeService.list(Wraps.<PlanDetailTime>lbQ()
                .in(PlanDetailTime::getNursingPlanDetailId, planDetailIds)
                .orderByAsc(PlanDetailTime::getPreTime)
                .orderByAsc(PlanDetailTime::getTime));
        if (CollUtil.isEmpty(detailTimes)) {
            return;
        }
        Map<Long, List<PlanDetailTime>> detailTimeMaps = new HashMap<>();
        for (PlanDetailTime time : detailTimes) {
            List<PlanDetailTime> timeList = detailTimeMaps.get(time.getNursingPlanDetailId());
            if (timeList == null) {
                timeList = new ArrayList<>();
            }
            timeList.add(time);
            detailTimeMaps.put(time.getNursingPlanDetailId(), timeList);
        }
        PlanDetailDTO detailDTO;
        List<PlanDetailTime> timeList;
        Map<Long, List<PlanDetailDTO>> planDetailsMap = new HashMap<>();
        for (PlanDetail detail : planDetails) {
            List<PlanDetailDTO> detailDTOS = planDetailsMap.get(detail.getNursingPlanId());
            if (CollUtil.isEmpty(detailDTOS)) {
                detailDTOS = new ArrayList<>();
            }
            detailDTO = new PlanDetailDTO();
            BeanUtils.copyProperties(detail, detailDTO);
            detailDTO.setId(detail.getId());
            timeList = detailTimeMaps.get(detail.getId());
            detailDTO.setPlanDetailTimes(timeList);
            detailDTOS.add(detailDTO);
            planDetailsMap.put(detail.getNursingPlanId(), detailDTOS);
        }
        for (Plan plan : planList) {
            List<PlanDetailDTO> details = planDetailsMap.get(plan.getId());
            if (CollUtil.isEmpty(details)) {
                continue;
            }
            plan.setPlanDetailList(details);
        }
    }

    @Override
    public List<Plan> getPlanAndDetailsByPlanType(Integer planType, String userRole, Integer status) {
        return getPlanAndDetailsByPlanType(planType, userRole, status, null);
    }

    /**
     * 查询计划列表，
     * @param planType
     * @param userRole
     * @param status
     * @return
     */
    @Override
    public List<Plan> getPlanAndDetailsByPlanType(Integer planType, String userRole, Integer status, List<Long> planIds) {
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ();
        if (Objects.nonNull(planType)) {
            wrapper.eq(Plan::getPlanType, planType);
        }
        if (Objects.nonNull(userRole)) {
            wrapper.eq(Plan::getLearnPlanRole, userRole);
        }
        if (Objects.nonNull(status)) {
            wrapper.eq(Plan::getStatus, status);
        }
        if (CollUtil.isNotEmpty(planIds)) {
            wrapper.in(SuperEntity::getId, planIds);
        }
        wrapper.orderByDesc(SuperEntity::getCreateTime);
        List<Plan> planList = baseMapper.selectList(wrapper);
        if (CollUtil.isNotEmpty(planList)) {
            planIds = planList.stream().map(SuperEntity::getId).collect(Collectors.toList());
            setPlanDetail(planList, planIds);
        }
        return planList;
    }

    /**
     * @return java.lang.Boolean
     * @Author yangShuai
     * @Description 重置护理计划
     * @Date 2020/10/24 16:58
     */
    @Transactional
    @Override
    public Boolean resetPlan(Long planId) {
        Plan plan = baseMapper.selectById(planId);
        plan = createPlan(plan.getPlanType(), plan);
        planTagService.deleteByPlanId(planId);
        resetPlantDetail(planId, plan);
        return true;
    }

    /**
     * 修改计划的状态
     * @param planId
     * @param status
     * @return
     */
    @Override
    public boolean updateStatus(Long planId, Integer status) {
        UpdateWrapper<Plan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", planId);
        updateWrapper.set("status_", status);
        baseMapper.update(new Plan(), updateWrapper);
        followTaskContentService.updatePlanStatusByPlan(planId, status);

        PatientNursingPlanService patientNursingPlanService1 = getPatientNursingPlanService();
        if (CommonStatus.YES.equals(status)) {
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() -> patientNursingPlanService1.patientBindPlan(tenant, planId));
        }

        return true;
    }


    @Autowired
    TenantApi tenantApi;

    // 上线功能后，运行一次。 给没有订阅护理计划的患者自动订阅上
    public void queryAllTenantNoBindPlanPatient() {

//        R<List<Tenant>> tenantR = tenantApi.getAllTenant();
//        List<Tenant> tenants = tenantR.getData();
//        PatientNursingPlanService patientNursingPlanService1 = getPatientNursingPlanService();
//        for (Tenant tenant : tenants) {
//
//            String code = tenant.getCode();
//            BaseContextHandler.setTenant(code);
//            List<Plan> planList = baseMapper.selectList(Wraps.<Plan>lbQ());
//            for (Plan plan : planList) {
//                patientNursingPlanService1.patientBindPlan(code, plan.getId());
//            }
//        }
    }




    /**
     * @return java.lang.Boolean
     * @Author yangShuai
     * @Description 更新护理计划，详情，标签，触发时间
     * @Date 2020/10/24 17:18
     */
    @Transactional
    @Override
    public Boolean updatePlan(Plan plan) {
        Long planTagId = plan.getTagId();
        boolean created = false;
        List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
        if (plan.getId() == null) {
            created = true;
            baseMapper.insert(plan);
        }
        planTagService.deleteByPlanId(plan.getId());
        if (planTagId != null) {
            PlanTag planTag = new PlanTag();
            planTag.setNursingPlanId(plan.getId());
            planTag.setTagId(planTagId);
            planTagService.save(planTag);
        }

        if (!CollectionUtils.isEmpty(planDetailList)) {
            for (PlanDetailDTO detailDTO : planDetailList) {
                List<PlanDetailTime> detailTimeList = detailDTO.getPlanDetailTimes();
                PlanDetail planDetail = new PlanDetail();
                if (detailDTO.getId() == null) {
                    BeanUtils.copyProperties(detailDTO, planDetail);
                    planDetail.setNursingPlanId(plan.getId());
                    planDetailService.save(planDetail);
                } else {
                    BeanUtils.copyProperties(detailDTO, planDetail);
                    planDetail.setNursingPlanId(plan.getId());
                    planDetail.setId(detailDTO.getId());
                    planDetailService.updateById(planDetail);
                }
                if (!CollectionUtils.isEmpty(detailTimeList)) {
                    List<PlanDetailTime> detailTimes = planDetailTimeService.findByDetailId(planDetail.getId());
                    List<Long> timeIds = detailTimes.stream().map(SuperEntity::getId).collect(Collectors.toList());
                    for (PlanDetailTime detailTime : detailTimeList) {
                        detailTime.setNursingPlanDetailId(planDetail.getId());
                        if (detailTime.getId() != null) {
                            timeIds.remove(detailTime.getId());
                            planDetailTimeService.updateById(detailTime);
                        } else {
                            planDetailTimeService.save(detailTime);
                        }
                    }
                    if (!timeIds.isEmpty()) {
                        planDetailTimeService.removeByIds(timeIds);
                    }
                }
            }
        }
        if (created) {
            followTaskContentService.createOneByPlan(plan);
        } else {
            baseMapper.updateById(plan);
            formService.updateFormName(plan.getId().toString(), plan.getName());
            followTaskContentService.updateByPlan(plan);
        }
        return true;
    }

    /**
     * 只有在初始化时可以使用
     * @param planType
     * @param plan
     * @return
     */
    private Plan createPlan(Integer planType, Plan plan) {
        Plan newPlan = PlanDict.getPlan(planType);
        if (newPlan != null) {
            newPlan.setId(plan.getId());
            newPlan.setCreateTime(plan.getCreateTime());
            newPlan.setCreateUser(plan.getCreateUser());
        } else {
            throw new BizException("护理计划类型不存在");
        }
        return newPlan;
    }

    /**
     * 创建项目时。初始化护理计划
     *
     * @return
     */
    @Override
    public boolean initPlan() {
        Plan plan = createPlan(1, new Plan());
        Plan plan1 = createPlan(2, new Plan());
        Plan plan2 = createPlan(3, new Plan());
        Plan plan3 = createPlan(4, new Plan());
        Plan plan4 = createPlan(5, new Plan());
        Plan plan5 = createPlan(6, new Plan());
        List<Plan> plans;
        plans = ListUtil.list(false, plan, plan1, plan2, plan3, plan4, plan5);
        saveBatchSomeColumn(plans);
        String tenant = BaseContextHandler.getTenant();
        asyncInit(plans, tenant);
        return false;
    }

    @Async
    public void asyncInit(List<Plan> plans, String tenant) {
        // 异步项目信息会丢失，故加上
        BaseContextHandler.setTenant(tenant);
        for (Plan plan : plans) {
            resetPlantDetail(plan.getId(), plan);
        }
        followTaskService.initOneTenantFollow();
        getFunctionConfigurationService().initFunctionConfiguration(tenant);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 重置护理计划详情
     * @Date 2020/10/24 17:01
     */
    public void resetPlantDetail(Long plantId, Plan n) {
        if (plantId == null) {
            return;
        }
        Integer planType = n.getPlanType();
        planDetailService.deleteByPlanId(plantId);

        if (1 == planType) {
            n.setId(plantId);
            initXYJC(n, plantId);
        }

        if (2 == planType) {
            n.setId(plantId);
            initXTJC(n, plantId);
        }

        if (3 == planType) {
            n.setId(plantId);
            initFCTX(n, plantId);
        }

        if (4 == planType) {
            n.setId(plantId);
            initYYTX(n, plantId);
        }

        if (5 == planType) {
            n.setId(plantId);
            initJKRZ(n, plantId);
        }

        if (6 == planType) {
            n.setId(plantId);
            initXXJH(n, plantId);
        }

    }


    /**
     * @return void
     * @Author yangShuai
     * @Description 初始化 血压监测
     * @Date 2020/9/21 13:49
     */
    @Transactional
    public void initXYJC(Plan n, Long plantId) {
        n.setExecute(7);
        n.setEffectiveTime(0);
        n.setFollowUpPlanType("monitoring_data");
        n.setSystemTemplate(0);
        n.setIsAdminTemplate(0);
        baseMapper.updateById(n);
        plantId = plantId == null ? n.getId() : plantId;

        PlanDetail planDetail1 = new PlanDetail();
        planDetail1.setNursingPlanId(plantId);
        planDetail1.setType(1);
        planDetailService.save(planDetail1);
        PlanDetailTime planDetailTime2 = new PlanDetailTime();
        planDetailTime2.setFrequency(7);
        planDetailTime2.setTime(LocalTime.of(6, 30));
        planDetailTime2.setPreTime(0);
        planDetailTime2.setNursingPlanDetailId(planDetail1.getId());
        planDetailTimeService.save(planDetailTime2);

        PlanDetailTime planDetailTime3 = new PlanDetailTime();
        planDetailTime3.setFrequency(7);
        planDetailTime3.setTime(LocalTime.of(21, 0));
        planDetailTime3.setPreTime(0);
        planDetailTime3.setNursingPlanDetailId(planDetail1.getId());
        planDetailTimeService.save(planDetailTime3);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 初始化 血糖监测
     * @Date 2020/9/21 14:02
     */
    public void initXTJC(Plan n, Long plantId) {
        n.setExecute(10);
        n.setEffectiveTime(0);
        n.setFollowUpPlanType("monitoring_data");
        n.setSystemTemplate(0);
        n.setIsAdminTemplate(0);
        baseMapper.updateById(n);

        // 模板消息护理详情
        PlanDetail nursingPlanDetail2 = new PlanDetail();
        nursingPlanDetail2.setNursingPlanId(plantId);
        nursingPlanDetail2.setType(1);
        planDetailService.save(nursingPlanDetail2);
        Long nursingPlanDetail2Id = nursingPlanDetail2.getId();

        List<PlanDetailTime> planDetailTimes = ListUtil.list(false,
                PlanDetailTime.builder().frequency(7).time(LocalTime.of(6, 30)).preTime(0).nursingPlanDetailId(nursingPlanDetail2Id).build(),
                PlanDetailTime.builder().frequency(7).time(LocalTime.of(9, 0)).preTime(0).nursingPlanDetailId(nursingPlanDetail2Id).build(),
                PlanDetailTime.builder().frequency(7).time(LocalTime.of(11, 30)).preTime(0).nursingPlanDetailId(nursingPlanDetail2Id).build(),
                PlanDetailTime.builder().frequency(7).time(LocalTime.of(14, 0)).preTime(0).nursingPlanDetailId(nursingPlanDetail2Id).build(),
                PlanDetailTime.builder().frequency(7).time(LocalTime.of(17, 30)).preTime(0).nursingPlanDetailId(nursingPlanDetail2Id).build(),
                PlanDetailTime.builder().frequency(7).time(LocalTime.of(20, 0)).preTime(0).nursingPlanDetailId(nursingPlanDetail2Id).build(),
                PlanDetailTime.builder().frequency(7).time(LocalTime.of(22, 0)).preTime(0).nursingPlanDetailId(nursingPlanDetail2Id).build()
        );
        planDetailTimeService.saveBatch(planDetailTimes);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 复查提醒
     * @Date 2020/9/21 14:10
     */
    public void initFCTX(Plan n, Long plantId) {
        n.setExecute(30);
        n.setEffectiveTime(0);
        n.setFollowUpPlanType("care_plan");
        n.setSystemTemplate(0);
        n.setIsAdminTemplate(0);
        n.setStatus(0);
        baseMapper.updateById(n);
        PlanDetail nursingPlanDetail = new PlanDetail();
        if (null == plantId) {
            nursingPlanDetail.setNursingPlanId(n.getId());
        } else {
            nursingPlanDetail.setNursingPlanId(plantId);
        }

        nursingPlanDetail.setType(1);
        planDetailService.save(nursingPlanDetail);
        PlanDetailTime planDetailTime = new PlanDetailTime();
        planDetailTime.setFrequency(90);
        planDetailTime.setTime(LocalTime.of(9, 0));
        planDetailTime.setPreTime(0);
        planDetailTime.setNursingPlanDetailId(nursingPlanDetail.getId());
        planDetailTimeService.save(planDetailTime);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 用药提醒
     * @Date 2020/9/21 14:13
     */
    public void initYYTX(Plan n, Long plantId) {
        PlanDetail nursingPlanDetail = new PlanDetail();
        n.setStatus(1);
        baseMapper.updateById(n);
        if (null == plantId) {
            nursingPlanDetail.setNursingPlanId(n.getId());
        } else {
            nursingPlanDetail.setNursingPlanId(plantId);
        }

        nursingPlanDetail.setType(0);
        planDetailService.save(nursingPlanDetail);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 初始化健康日志
     * @Date 2020/9/21 14:16
     */
    public void initJKRZ(Plan n, Long plantId) {
        n.setStatus(0);
        n.setExecute(7);
        n.setEffectiveTime(0);
        n.setFollowUpPlanType("care_plan");
        n.setSystemTemplate(0);
        n.setIsAdminTemplate(0);
        baseMapper.updateById(n);
        PlanDetail nursingPlanDetail = new PlanDetail();
        if (null == plantId) {
            nursingPlanDetail.setNursingPlanId(n.getId());
        } else {
            nursingPlanDetail.setNursingPlanId(plantId);
        }

        nursingPlanDetail.setType(1);
        planDetailService.save(nursingPlanDetail);
        PlanDetailTime planDetailTime = new PlanDetailTime();
        planDetailTime.setFrequency(7);
        planDetailTime.setTime(LocalTime.of(21, 0));
        planDetailTime.setPreTime(0);
        planDetailTime.setNursingPlanDetailId(nursingPlanDetail.getId());
        planDetailTimeService.save(planDetailTime);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 初始化学习计划
     * @Date 2020/9/21 14:18
     */
    public void initXXJH(Plan n, Long plantId) {
        n.setExecute(1);
        n.setStatus(0);
        n.setEffectiveTime(0);
        n.setFollowUpPlanType("care_plan");
        n.setLearnPlanRole(UserType.UCENTER_PATIENT);
        n.setSystemTemplate(0);
        n.setIsAdminTemplate(0);
        baseMapper.updateById(n);

        PlanDetail nursingPlanDetail = new PlanDetail();
        plantId = plantId == null ? n.getId() : plantId;
        nursingPlanDetail.setType(1);
        nursingPlanDetail.setNursingPlanId(plantId);
        planDetailService.save(nursingPlanDetail);

        Long planDetailId = nursingPlanDetail.getId();
        List<PlanDetailTime> planDetailTimes = ListUtil.list(false,
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(1).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(2).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(3).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(4).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(5).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(6).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(7).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(14).nursingPlanDetailId(planDetailId).build(),
                PlanDetailTime.builder().frequency(0).time(LocalTime.of(21, 0)).preTime(28).nursingPlanDetailId(planDetailId).build()
        );
        planDetailTimeService.saveBatch(planDetailTimes);
    }



    @Override
    public boolean removeById(Serializable id) {
        // 判断护理计划是否已经被加入到患者菜单去了
        R<Integer> count = h5RouterApi.countByPath("custom/follow/" + id);
        if (count.getIsSuccess()) {
            Integer data = count.getData();
            if (data != null && data > 0) {
                throw new BizException("该护理计划正在使用中，不可删除");
            }
        } else {
            throw new BizException("校验护理计划是否正在使用失败");
        }
        Plan plan = baseMapper.selectById(id);
        // 删除护理计划
        baseMapper.deleteById(id);
        // 删除护理计划详情
        planDetailService.deleteByPlanId(Long.parseLong(id.toString()));
        // 删除护理计划标签
        planTagService.remove(Wraps.<PlanTag>lbQ().eq(PlanTag::getNursingPlanId, id));

        // 清除护理计划下表单相关的所有业务
        formService.removePlanForm(id);

        // 删除 学习计划下的推送
        planLearnDetailService.remove(Wraps.<PlanLearnDetail>lbQ().eq(PlanLearnDetail::getLearnPlanId, id));

        // 移除 护理计划的推送记录
        getReminderLogService().remove(Wraps.<ReminderLog>lbQ().eq(ReminderLog::getPlanId, id));

        // 删除 护理计划 关联的 统计任务
        List<StatisticsTask> statisticsTaskList = statisticsTaskService.list(Wraps.<StatisticsTask>lbQ().eq(StatisticsTask::getPlanId, id));
        for (StatisticsTask task : statisticsTaskList) {
            statisticsTaskService.removeById(task.getId());
        }
        Integer planType = plan.getPlanType();
        PlanFunctionTypeEnum functionTypeEnum = PlanFunctionTypeEnum.getEnumByPlanType(planType, plan.getFollowUpPlanType());
        if (functionTypeEnum != null) {
            patientSystemMessageApi.deleteByBusinessId(functionTypeEnum.getCode(), plan.getId());
        }

        if (planType != null && PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
            // 如果删除的是学习计划，则不删除随访内容
            return true;
        }
        followTaskContentService.remove(Wraps.<FollowTaskContent>lbQ().eq(FollowTaskContent::getPlanId, id));


        return true;
    }


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    /**
     * 根据项目信息删除护理计划
     */
    @Override
    public Boolean deleteByTenant() {
        String tenant = BaseContextHandler.getTenant();
        if (StrUtil.isBlank(tenant)) {
            return true;
        }
        List<Long> ids = baseMapper.selectList(Wraps.<Plan>lbQ().select(Plan::getId))
                .stream().map(Plan::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(ids)) {
            return true;
        }
        return this.removeById((Serializable) ids);
    }

    /**
     * 根据 系统模板plan 创建一个项目计划模板
     * @param plan
     */
    @Override
    public void copySystemTemplatePlan(Plan plan) {

        plan.setId(null);
        List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
        baseMapper.insert(plan);
        if (CollUtil.isNotEmpty(planDetailList)) {
            for (PlanDetailDTO detailDTO : planDetailList) {
                detailDTO.setId(null);
                PlanDetail planDetail = new PlanDetail();
                BeanUtils.copyProperties(detailDTO, planDetail);
                List<PlanDetailTime> detailTimeList = detailDTO.getPlanDetailTimes();
                planDetail.setNursingPlanId(plan.getId());
                Integer type = detailDTO.getType();
                planDetailService.save(planDetail);
                if (!CollectionUtils.isEmpty(detailTimeList)) {
                    for (PlanDetailTime detailTime : detailTimeList) {
                        detailTime.setNursingPlanDetailId(planDetail.getId());
                        if (Objects.nonNull(type) && type.equals(1)) {
                            detailTime.setTemplateMessageId(null);
                        }
                        detailTime.setId(null);
                        planDetailTimeService.save(detailTime);
                    }
                }
            }
        }
        followTaskContentService.createOneByPlan(plan);
    }

    @Override
    public void saveSystemTemplatePlan(Plan plan) {
        copySystemTemplatePlan(plan);
    }



    @Override
    public Map<Long, Long> copyPlan(CopyPlanDTO copyPlanDTO) {
        String fromTenantCode = copyPlanDTO.getFromTenantCode();
        String toTenantCode = copyPlanDTO.getToTenantCode();
        Map<Long, Long> templateIdMaps = copyPlanDTO.getTemplateIdMaps();

        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        // 复制护理计划 、计划详情、详情时间
        String currentTenant = BaseContextHandler.getTenant();

        // 查找出需要复制的项目数据
        BaseContextHandler.setTenant(fromTenantCode);
        List<Plan> plans = baseMapper.selectList(Wrappers.emptyWrapper());
        List<PlanDetail> planDetails = planDetailService.list();
        List<PlanDetailTime> planDetailTimes = planDetailTimeService.list();
        List<Aim> aims = aimService.list();

        // 修改为要复制的数据
        Map<Long, Long> planIdMaps = new HashMap<>();
        Map<Long, Long> planDetailIdMaps = new HashMap<>();
        plans.forEach(p -> planIdMaps.put(p.getId(), snowflake.nextId()));
        planDetails.forEach(d -> planDetailIdMaps.put(d.getId(), snowflake.nextId()));

        List<Plan> toSavePlans = plans.stream().peek(p -> p.setId(planIdMaps.get(p.getId()))).collect(Collectors.toList());
        List<PlanDetail> toSavePlanDetails = planDetails.stream().peek(d -> {
            d.setId(planDetailIdMaps.get(d.getId()));
            d.setNursingPlanId(planIdMaps.get(d.getNursingPlanId()));
            // 当 从模板集合中拿到数据时，表明关联的是模板。否则可能是一篇文章，是不需要修改的。
            if (!StringUtils.isEmpty(d.getTemplateMessageId()) && templateIdMaps.get(Convert.toLong(d.getTemplateMessageId())) != null) {
                d.setTemplateMessageId(Convert.toStr(templateIdMaps.get(Convert.toLong(d.getTemplateMessageId()))));
            }
        }).collect(Collectors.toList());
        List<PlanDetailTime> toSavePlanDetailTimes = planDetailTimes.stream().peek(t -> {
            t.setId(null);
            t.setNursingPlanDetailId(planDetailIdMaps.get(t.getNursingPlanDetailId()));

            // 当 从模板集合中拿到数据时，表明关联的是模板。否则可能是一篇文章，是不需要修改的。
            if (!StringUtils.isEmpty(t.getTemplateMessageId()) && templateIdMaps.get(Convert.toLong(t.getTemplateMessageId())) != null) {
                t.setTemplateMessageId(Convert.toStr(templateIdMaps.get(Convert.toLong(t.getTemplateMessageId()))));
            }
        }).collect(Collectors.toList());
        List<Aim> toSaveAims = aims.stream().peek(a -> a.setId(null)).collect(Collectors.toList());

        // 保存数据
        BaseContextHandler.setTenant(toTenantCode);
        saveBatchSomeColumn(toSavePlans);
        planDetailService.saveBatch(toSavePlanDetails);
        planDetailTimeService.saveBatch(toSavePlanDetailTimes);
        aimService.saveBatch(toSaveAims);
        followTaskService.initOneTenantFollow();
        getFunctionConfigurationService().copyFunctionConfiguration(fromTenantCode, toTenantCode);
        BaseContextHandler.setTenant(currentTenant);
        return planIdMaps;
    }



    @Override
    public void checkPlanAll(Plan plan) {

        List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
        if (CollUtil.isEmpty(planDetailList)) {
            return;
        }
        for (PlanDetailDTO detailDTO : planDetailList) {
            if (detailDTO.getType() == null) {
                throw new BizException("推送类型不能为空");
            }
            List<PlanDetailTime> detailTimes = detailDTO.getPlanDetailTimes();
            if (CollUtil.isEmpty(detailTimes)) {
                continue;
            }
            for (PlanDetailTime detailTime : detailTimes) {
                if (detailTime.getTime() == null) {
                    throw new BizException("推送时间不能为空");
                }
                if (detailTime.getFrequency() == null) {
                    throw new BizException("推送间隔不能为空");
                }
            }
        }

    }

    @Override
    public List<Plan> exportFollowPlan() {
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ()
                .in(Plan::getPlanType, PlanEnum.REVIEW_REMIND.getCode(),
                        PlanEnum.HEALTH_LOG.getCode())
                .orderByAsc(Plan::getPlanType);
                //获取自定义护理计划
        List<Plan> plans = baseMapper.selectList(wrapper);

        // 自定义护理计划
        List<Plan> planList = baseMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.CARE_PLAN.operateType)
                .eq(Plan::getIsAdminTemplate, AdminTemplateEnum.OTHER_PLAN.getCode()));
        if (CollUtil.isNotEmpty(planList)) {
            plans.addAll(planList);
        }

        // 自定义监测计划
        planList = baseMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.MONITORING_DATA.operateType)
                .eq(Plan::getIsAdminTemplate, AdminTemplateEnum.OTHER_PLAN.getCode()));
        if (CollUtil.isNotEmpty(planList)) {
            plans.addAll(planList);
        }
        if (CollUtil.isEmpty(plans)) {
            return new ArrayList<>();
        }
        List<String> planIds = new ArrayList<>();
        for (Plan plan : plans) {
            planIds.add(plan.getId().toString());
        }
        FormMapper formMapper = SpringUtils.getBean(FormMapper.class);
        List<Form> formList = formMapper.selectList(Wraps.<Form>lbQ().in(Form::getBusinessId, planIds).select(Form::getBusinessId));
        Set<String> formBusinessId = formList.stream().map(Form::getBusinessId).collect(Collectors.toSet());
        List<Plan> returnPlan = new ArrayList<>(planList.size());
        for (Plan plan : plans) {
            if (formBusinessId.contains(plan.getId().toString())) {
                returnPlan.add(plan);
            }
        }
        return returnPlan;
    }


    /**
     * 查询文件夹的分享链接被用在那个项目
     * @param url
     * @return
     */
    @Override
    public List<String> checkFolderShareUrlExist(String url) {
        return planDetailService.checkFolderShareUrlExist(url);
    }
}
