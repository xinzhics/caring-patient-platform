package com.caring.sass.nursing.service.plan.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.AppointmentStatusEnum;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.appointment.AppointmentMapper;
import com.caring.sass.nursing.dao.plan.PatientCustomPlanTimeMapper;
import com.caring.sass.nursing.dao.plan.PatientNursingPlanMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dto.follow.*;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.dto.plan.SubscribeDTO;
import com.caring.sass.nursing.entity.appointment.Appointment;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.plan.*;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.enumeration.FollowUpPlanTypeEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.drugs.PatientDrugsService;
import com.caring.sass.nursing.service.drugs.PatientDrugsTimeService;
import com.caring.sass.nursing.service.follow.FollowTaskContentService;
import com.caring.sass.nursing.service.follow.FollowTaskService;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import com.caring.sass.nursing.service.plan.*;
import com.caring.sass.nursing.service.tag.AssociationService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.service.task.NursingPlanPushParam;
import com.caring.sass.nursing.service.task.sender.WeixinSender;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.nursing.util.PlanDict;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.tenant.api.H5CoreFunctionApi;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.user.dto.PatientPageDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 会员订阅护理计划
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service

public class PatientNursingPlanServiceImpl extends SuperServiceImpl<PatientNursingPlanMapper, PatientNursingPlan> implements PatientNursingPlanService {

    @Autowired
    private PatientApi patientApi;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    AssociationService associationService;

    @Autowired
    PlanTagService planTagService;

    @Autowired
    AimService aimService;

    @Autowired
    PatientDrugsService patientDrugsService;

    @Autowired
    PlanDetailService planDetailService;

    @Autowired
    PatientCustomPlanTimeMapper patientCustomPlanTimeMapper;

    @Autowired
    GuideApi guideApi;

    @Autowired
    FunctionConfigurationService functionConfigurationService;

    @Autowired
    H5CoreFunctionApi h5CoreFunctionApi;

    @Autowired
    H5RouterApi h5RouterApi;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    FollowTaskContentService followTaskContentService;

    @Autowired
    AppointmentMapper appointmentMapper;

    ReminderLogService reminderLogService;

    public ReminderLogService getReminderLogService() {
        if (reminderLogService == null) {
            reminderLogService = SpringUtils.getBean(ReminderLogService.class);
        }
        return reminderLogService;
    }
    /**
     * 1.自定义护理计划有设置带标签的触发对象，但患者未打上该标签时，在患者端智能提醒处不显示该自定义护理计划提醒的开关；
     * 2.自定义护理计划未设置触发对象时，默认所有患者都能收到该护理计划，且所有患者的智能提醒界面都显示该自定义护理计划的开关；
     * 3.患者标签修改后，原标签对应的护理计划推送提醒不显示，仅显示当前标签对应的护理计划的智能提醒
     * 4.自定义护理计划关闭后，智能提醒界面不显示该护理计划智能提醒开关
     *
     * @param patientId
     * @return
     */
    public SubscribeDTO getSubscribeList(Long patientId) {
        R<Patient> patientR = patientApi.get(patientId);
        if (!patientR.getIsSuccess() || Objects.isNull(patientR.getData())) {
            throw new BizException("获取用户数据异常");
        }
        // 最终患者可见的护理计划
        List<PatientNursingPlan> resultSubscribes = new ArrayList<>();

        // 患者关注的护理计划
        LbqWrapper<PatientNursingPlan> lbqWrapper = Wraps.<PatientNursingPlan>lbQ()
                .eq(PatientNursingPlan::getPatientId, patientId);
        List<PatientNursingPlan> subscribes = baseMapper.selectList(lbqWrapper);
        Map<Long, PatientNursingPlan> patientNursingPlanMap = new HashMap<>();
        if (CollUtil.isNotEmpty(subscribes)) {
            patientNursingPlanMap = subscribes.stream()
                    .collect(Collectors.toMap(PatientNursingPlan::getNursingPlantId, item -> item, (o1, o2) -> o2));
        }

        SubscribeDTO subscribeVo = new SubscribeDTO();
        subscribeVo.setSubscribeList(resultSubscribes);
        subscribeVo.setPatientId(patientId);
        Aim aim = aimService.getOne(Wraps.<Aim>lbQ().eq(Aim::getIsDefault, 1));
        subscribeVo.setImgUrl(aim != null && StringUtils.isNotEmptyString(aim.getUrl()) ? aim.getUrl() : "");
        // 查询 系统开启的护理计划
        List<Plan> planList = planMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getStatus, 1)
                .in(Plan::getFollowUpPlanType, PlanDict.CARE_PLAN, PlanDict.MONITORING_DATA)
                .apply(" (learn_plan_role is null or learn_plan_role = 'patient') ")
                .orderByAsc(Plan::getCreateTime));

        // 查询 护理计划设置的标签
        Map<Long, Long> planTagMap = new HashMap<>();
        if (CollUtil.isNotEmpty(planList)) {
            Set<Long> planIdSet = planList.stream().map(SuperEntity::getId).collect(Collectors.toSet());
            List<PlanTag>  planTags = planTagService.list(Wraps.<PlanTag>lbQ().in(PlanTag::getNursingPlanId, planIdSet));
            if (CollUtil.isNotEmpty(planTags)) {
                planTagMap = planTags.stream().collect(Collectors.toMap(PlanTag::getNursingPlanId, PlanTag::getTagId, (o1, o2) -> o2));
            }
        }
        // 查询患者 拥有的标签
        LbqWrapper<Association> associationLbqWrapper = Wraps.<Association>lbQ()
                .eq(Association::getAssociationId, Convert.toStr(patientId));
        List<Association> associationList = associationService.list(associationLbqWrapper);
        Set<Long> patientTagIds = new HashSet<>();
        if (CollUtil.isNotEmpty(associationList)) {
            patientTagIds = associationList.stream().map(Association::getTagId).collect(Collectors.toSet());
        }

        Patient patient = patientR.getData();
        if (patient.getNursingTime() == null) {
            LocalDateTime time = LocalDateTime.now();
            LocalDateTime dateTime = time.plusDays(1);
            patient.setNursingTime(time);
            subscribeVo.setStartDate(dateTime);
            subscribeVo.setCompleteEnterGroupTime(time);
        } else {
            subscribeVo.setStartDate(patient.getNursingTime());
            subscribeVo.setCompleteEnterGroupTime(patient.getCompleteEnterGroupTime());
        }

        // 查询 指标监测，自定义随访 关闭后， 其中还处于打开状态的 护理计划
        List<Long> removePlanButOpenId = functionConfigurationService.getRemovePlanButOpenId();

        // 如果学习计划关闭。还要过滤掉学习计划
        Integer functionStatus = functionConfigurationService.getFunctionStatus(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.LEARNING_PLAN);
        if (functionStatus.equals(0)) {
            List<Plan> plans = planMapper.selectList(Wraps.<Plan>lbQ()
                    .eq(Plan::getLearnPlanRole, UserType.UCENTER_PATIENT)
                    .eq(Plan::getPlanType, PlanEnum.LEARN_PLAN.getCode())
                    .eq(Plan::getStatus, 1));
            if (CollUtil.isNotEmpty(plans)) {
                plans.forEach(item -> removePlanButOpenId.add(item.getId()));
            }
        }

        // 合并得出 患者可见的 护理计划列表
        // 循环系统的护理计划。根据各种条件得出 患者可见的 护理计划
        for (Plan plan : planList) {
            if (CollUtil.isNotEmpty(removePlanButOpenId) && removePlanButOpenId.contains(plan.getId())) {
                continue;
            }
            // 计划的标签
            boolean canSet = false;
            Long tagId = planTagMap.get(plan.getId());
            if (Objects.isNull(tagId)) {
                // 说明这个计划可以直接让患者看到。
                canSet = true;
            } else {
                // 验证一下患者有没有这个标签
                if (patientTagIds.contains(tagId)) {
                    canSet = true;
                }
            }
            // 计划可以给患者看到。
            if (canSet) {
                // 看看患者之前有没有添加到自己的护理计划中去
                PatientNursingPlan patientNursingPlan = patientNursingPlanMap.get(plan.getId());
                if (Objects.nonNull(patientNursingPlan)) {
                    // 给患者重新设置一下。
                    resultSubscribes.add(patientNursingPlan);
                } else {
                    patientNursingPlan = new PatientNursingPlan();
                    patientNursingPlan.setPatientId(patientId);
                    patientNursingPlan.setNursingPlantId(plan.getId());
                    patientNursingPlan.setStartDate(LocalDate.now());
                    if (PlanEnum.MEDICATION_REMIND.getCode().equals(plan.getPlanType())) {
                        // 护理计划是 用药提醒。
                        // 患者之前又没有这个计划。则查询系统的 用药配置是否开启
                        // 是否在注册时填写用药信息：0填写，1不填写
                        R<Integer> fillDrugs = guideApi.hasFillDrugs(BaseContextHandler.getTenant());
                        if (fillDrugs.getIsSuccess()) {
                            Integer drugsData = fillDrugs.getData();
                            if (Objects.nonNull(drugsData) && drugsData.equals(1)) {
                                patientNursingPlan.setIsSubscribe(0);
                            } else {
                                patientNursingPlan.setIsSubscribe(1);
                            }
                        }
                    } else {
                        patientNursingPlan.setIsSubscribe(1);
                    }
                    resultSubscribes.add(patientNursingPlan);
                }
                patientNursingPlan.setDisableSubscribe(0);
                patientNursingPlan.setPlanType(plan.getPlanType());
                patientNursingPlan.setNursingPlanName(plan.getName());

            } else {
                // 说明患者不需要拥有这个护理计划了。
//                PatientNursingPlan patientNursingPlan = patientNursingPlanMap.get(plan.getId());
//                if (Objects.nonNull(patientNursingPlan)) {
//                    baseMapper.deleteById(patientNursingPlan.getId());
//                }
            }
        }
        return subscribeVo;

    }


    /**
     * 患者首次入组后。默认开启的随访都订阅
     * @param patientId
     */
    @Override
    public void patientFirstSubscribePlanAll(Long patientId) {
        // 查询 系统开启的护理计划
        List<Plan> planList = planMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getStatus, 1)
                .in(Plan::getFollowUpPlanType, PlanDict.CARE_PLAN, PlanDict.MONITORING_DATA)
                .apply(" (learn_plan_role is null or learn_plan_role = 'patient') ")
                .orderByAsc(Plan::getCreateTime));
        // 查询 指标监测，自定义随访 关闭后， 其中还处于打开状态的 护理计划
        List<Long> removePlanButOpenId = functionConfigurationService.getRemovePlanButOpenId();

        // 如果学习计划关闭。还要过滤掉学习计划
        Integer functionStatus = functionConfigurationService.getFunctionStatus(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.LEARNING_PLAN);
        if (functionStatus.equals(0)) {
            List<Plan> plans = planMapper.selectList(Wraps.<Plan>lbQ()
                    .eq(Plan::getLearnPlanRole, UserType.UCENTER_PATIENT)
                    .eq(Plan::getPlanType, PlanEnum.LEARN_PLAN.getCode())
                    .eq(Plan::getStatus, 1));
            if (CollUtil.isNotEmpty(plans)) {
                plans.forEach(item -> removePlanButOpenId.add(item.getId()));
            }
        }
        List<PatientNursingPlan> plans = new ArrayList<>();
        PatientNursingPlan nursingPlan;
        for (Plan plan : planList) {
            if (CollUtil.isNotEmpty(removePlanButOpenId) && removePlanButOpenId.contains(plan.getId())) {
                continue;
            }
            nursingPlan = PatientNursingPlan.builder().nursingPlantId(plan.getId()).startDate(LocalDate.now()).patientId(patientId).isSubscribe(1).build();
            plans.add(nursingPlan);
        }
        if (CollUtil.isNotEmpty(plans)) {
            baseMapper.insertBatchSomeColumn(plans);
        }
    }

    /**
     * 查询患者订阅的护理计划
     * @param patientId
     * @return
     */
    @Override
    public List<PatientNursingPlan> patientSubscribeList(Long patientId) {

        List<PatientNursingPlan> nursingPlans = baseMapper.selectList(Wraps.<PatientNursingPlan>lbQ()
                .select(SuperEntity::getId, PatientNursingPlan::getNursingPlantId)
                .eq(PatientNursingPlan::getPatientId, patientId)
                .eq(PatientNursingPlan::getIsSubscribe, 1));
        if (CollUtil.isEmpty(nursingPlans)) {
            return new ArrayList<>();
        }
        return nursingPlans;
    }

    /**
     * 取消掉 患者关注完智能提醒才算是入组成功。
     * @param patientId
     * @param subscribeDTO
     */
    @Override
    public void updateMyNursingPlans(Long patientId, SubscribeDTO subscribeDTO) {
        List<PatientNursingPlan> subscribeList = subscribeDTO.getSubscribeList();
        for (PatientNursingPlan patientNursingPlan : subscribeList) {
            if (null != patientNursingPlan) {
                patientNursingPlan.setPatientId(patientId);
                patientNursingPlan.setStartDate(subscribeDTO.getStartDate().toLocalDate());
                if (PlanEnum.MEDICATION_REMIND.getCode().equals(patientNursingPlan.getPlanType()) && patientNursingPlan.getId() != null) {
                    // 查询一下 患者之前是否有 用药提醒 的设置
                    PatientNursingPlan medicationRemindPlan = baseMapper.selectById(patientNursingPlan.getId());
                    // 用药提醒已经开启了。
                    if (medicationRemindPlan.getIsSubscribe() != null && 1 ==  medicationRemindPlan.getIsSubscribe()) {
                        // 患者要关闭用药提醒
                        if (patientNursingPlan.getIsSubscribe() != null && patientNursingPlan.getIsSubscribe() == 0) {
                            patientNursingPlan.setPatientCancelSubscribe(1);
                        }
                    }
                }
                super.saveOrUpdate(patientNursingPlan);
            }
        }
        String tenant = BaseContextHandler.getTenant();
        SaasGlobalThreadPool.execute(() -> updatePatientCustomPlanTime(subscribeList, tenant, patientCustomPlanTimeMapper));

    }


    /**
     * 患者单独 订阅某个护理计划
     * @param patientId
     * @param planId
     */
    @Override
    public void subscribePlan(Long patientId, Long planId) {
        List<PatientNursingPlan> nursingPlanList = baseMapper.selectList(Wraps.<PatientNursingPlan>lbQ()
                .eq(PatientNursingPlan::getNursingPlantId, planId)
                .eq(PatientNursingPlan::getPatientId, patientId));
        if (CollUtil.isEmpty(nursingPlanList)) {
            PatientNursingPlan nursingPlan = new PatientNursingPlan();
            nursingPlan.setIsSubscribe(1);
            nursingPlan.setPatientId(patientId);
            nursingPlan.setNursingPlantId(planId);
            nursingPlan.setStartDate(LocalDate.now());
            baseMapper.insert(nursingPlan);
        } else {
            PatientNursingPlan nursingPlan = nursingPlanList.get(0);
            nursingPlan.setIsSubscribe(1);
            baseMapper.updateById(nursingPlan);
            if (nursingPlanList.size() > 1) {
                for (int i = 1; i < nursingPlanList.size(); i++) {
                    baseMapper.deleteById(nursingPlan.getId());
                }
            }
        }

    }

    @Override
    public PatientCustomPlanTime getPatientCustomPlan(Long patientId, Long planId, Long detailId, Long detailTimeId) {

        List<PatientCustomPlanTime> planTimeList = patientCustomPlanTimeMapper.selectList(Wraps.<PatientCustomPlanTime>lbQ()
                .eq(PatientCustomPlanTime::getPatientId, patientId)
                .eq(PatientCustomPlanTime::getNursingPlantId, planId)
                .eq(PatientCustomPlanTime::getNursingPlanDetailId, detailId)
                .eq(PatientCustomPlanTime::getNursingPlanDetailTimeId, detailTimeId)
                .eq(PatientCustomPlanTime::getCustomizeStatus, 1));
        if (CollUtil.isNotEmpty(planTimeList)) {
            return planTimeList.get(0);
        }
        return null;


    }


    /**
     * 要求计划只有一个推送时间
     * 注射模式的计划。 用户提交表单后。
     * 根据用户的提交时间和 计划设置的下次提醒间隔，超时提醒时间
     * 生成用户的自定义推送提醒设置。
     * @param plan
     * @param formResult
     */
    @Override
    public void handleInjectionForm(Plan plan, FormResult formResult) {

        if (Objects.isNull(plan) || Objects.isNull(formResult)) {
            return;
        }
        // 查询用户是否存在已经设置的 自定义提醒
        PatientCustomPlanTime customPlanTime = patientCustomPlanTimeMapper.selectOne(Wraps.<PatientCustomPlanTime>lbQ()
                .eq(PatientCustomPlanTime::getPatientId, formResult.getUserId())
                .eq(PatientCustomPlanTime::getNursingPlantId, plan.getId())
                .last(" limit 0,1 "));

        // 查询护理计划下的详细设置。这里的护理计划目前 只追对 自定义随访
        List<PlanDetailDTO> detailDTOS = planDetailService.findDetailWithTimeById(plan.getId());
        PlanDetailDTO detailDTO = null;
        PlanDetailTime detailTime = null;
        // 如果推送时间没设置。默认是9点
        LocalTime localTime = LocalTime.of(9,0,0,0);
        if (CollUtil.isNotEmpty(detailDTOS)) {
            detailDTO = detailDTOS.get(0);

            List<PlanDetailTime> detailTimes = detailDTO.getPlanDetailTimes();
            if (CollUtil.isNotEmpty(detailTimes)) {
                detailTime = detailTimes.get(0);
                localTime = detailTime.getTime();
            }
        }
        // 如果之前不存在自定义推送。则初始化其中的参数。 用户，护理计划，
        if (customPlanTime == null) {
            customPlanTime = new PatientCustomPlanTime();
            if (Objects.nonNull(detailDTO)) {
                customPlanTime.setNursingPlanDetailId(detailDTO.getId());
            }
            if (Objects.nonNull(detailTime)) {
                customPlanTime.setNursingPlanDetailTimeId(detailTime.getId());
            }
            customPlanTime.setNursingPlantId(plan.getId());
            customPlanTime.setFrequency(0);
            customPlanTime.setPlanType(plan.getPlanType());
            customPlanTime.setPatientId(formResult.getUserId());
        }
        customPlanTime.setCustomizeStatus(CommonStatus.YES);
        LocalDateTime createTime = formResult.getCreateTime();
        if (plan.getNextRemind() == null) {
            return;
        }
        if (plan.getNextRemind() == 0) {
            customPlanTime.setCustomizeStatus(2);
        } else {

            // 使用表单的提交时间和 计划设置下次提醒时间。生成患者自定义随访提醒
            LocalDateTime dateTime = createTime.plusDays(plan.getNextRemind());
            LocalDate localDate = dateTime.toLocalDate();
            customPlanTime.setNextRemindTime(LocalDateTime.of(localDate, localTime));
        }
        // 判断是否要生成超时提醒时间。
        if (plan.getNextRemind() > 0 && plan.getTimeOutRemind() != null && plan.getTimeOutRemind() > 0) {
            LocalDateTime nextRemindTime = customPlanTime.getNextRemindTime();
            customPlanTime.setRemindTimeOut(nextRemindTime.plusDays(plan.getTimeOutRemind()));
        } else {
            customPlanTime.setRemindTimeOut(null);
        }
        if (customPlanTime.getId() == null) {
            patientCustomPlanTimeMapper.insert(customPlanTime);
        } else {
            patientCustomPlanTimeMapper.updateAllById(customPlanTime);
        }
    }

    /**
     * 更新患者的自定义护理计划推送时间
     * @param subscribeList
     * @param tenantCode
     * @param patientCustomPlanTimeMapper
     */
    public void updatePatientCustomPlanTime(List<PatientNursingPlan> subscribeList, String tenantCode, PatientCustomPlanTimeMapper patientCustomPlanTimeMapper) {
        BaseContextHandler.setTenant(tenantCode);
        for (PatientNursingPlan nursingPlan : subscribeList) {
            if (nursingPlan.getIsSubscribe() != null && nursingPlan.getIsSubscribe() == 1) {
                List<PatientCustomPlanTime> planTimes = nursingPlan.getCustomPlanTimes();
                if (CollectionUtils.isEmpty(planTimes)) {
                    continue;
                }
                for (PatientCustomPlanTime planTime : planTimes) {
                    if (planTime.getId() == null && planTime.getCustomizeStatus() != null && planTime.getCustomizeStatus() == 1) {
                        if (planTime.getPatientId() != null && planTime.getNursingPlantId() != null &&
                                planTime.getNursingPlanDetailId() != null && planTime.getNursingPlanDetailTimeId() != null) {
                            patientCustomPlanTimeMapper.insert(planTime);
                        }
                    } else if (planTime.getId() != null && planTime.getCustomizeStatus() != null) {
                        // 关闭了自定义推送  删除
                        if (planTime.getCustomizeStatus() == 0) {
                            patientCustomPlanTimeMapper.deleteById(planTime.getId());
                        } else {
                            patientCustomPlanTimeMapper.updateById(planTime);
                        }
                    }
                }
            } else {
                List<PatientCustomPlanTime> planTimes = nursingPlan.getCustomPlanTimes();
                if (CollectionUtils.isNotEmpty(planTimes)) {
                    patientCustomPlanTimeMapper.delete(Wraps.<PatientCustomPlanTime>lbQ()
                            .eq(PatientCustomPlanTime::getNursingPlantId, nursingPlan.getNursingPlantId())
                            .eq(PatientCustomPlanTime::getCustomizeStatus, 1)
                            .eq(PatientCustomPlanTime::getPatientId, nursingPlan.getPatientId()));
                }
            }
        }
    }

    /**
     * 设置护理计划下次推送时间或查找用户设置的推送时间
     *
     * 目前只处理 复查提醒 的自定义设置。其他护理计划不处理
     * @param subscribeDTO
     */
    @Override
    public void setPatientCustomPlanTime(SubscribeDTO subscribeDTO) {
        List<PatientNursingPlan> subscribeList = subscribeDTO.getSubscribeList();
        LocalDateTime completeEnterGroupTime = subscribeDTO.getCompleteEnterGroupTime();
        Long patientId = subscribeDTO.getPatientId();
        for (PatientNursingPlan nursingPlan : subscribeList) {
            if (nursingPlan.getPlanType() != null &&
            nursingPlan.getPlanType().equals(PlanEnum.REVIEW_REMIND.getCode())) {
                // 获取护理计划 中每个 DetailTime 下一次给用户推送的时间。
                List<PatientCustomPlanTime> planTimes = calculateNextPushTime(patientId, completeEnterGroupTime, nursingPlan.getNursingPlantId());
                nursingPlan.setCustomPlanTimes(planTimes);
            }
        }
    }

    /**
     * 计算用户 下一次的推送时间
     * @param patientId
     * @param completeEnterGroupTime
     * @param nursingPlantId
     * @return
     */
    public List<PatientCustomPlanTime> calculateNextPushTime(Long patientId, LocalDateTime completeEnterGroupTime,
                                                             Long nursingPlantId) {
        if (completeEnterGroupTime == null) {
            completeEnterGroupTime = LocalDateTime.now();
        }

        Plan plan = planMapper.selectById(nursingPlantId);

        List<PatientCustomPlanTime> customPlanTimes = patientCustomPlanTimeMapper.selectList(Wraps.<PatientCustomPlanTime>lbQ()
                .eq(PatientCustomPlanTime::getNursingPlantId, nursingPlantId)
                .eq(PatientCustomPlanTime::getCustomizeStatus, 1)
                .eq(PatientCustomPlanTime::getPatientId, patientId));
        Map<Long, PatientCustomPlanTime> planTimeMap = customPlanTimes.stream()
                .collect(Collectors.toMap(PatientCustomPlanTime::getNursingPlanDetailTimeId, item -> item, (o1, o2) -> o2));
        List<PlanDetailDTO> planDetailDTOS = planDetailService.findDetailWithTimeById(nursingPlantId);
        // 判断护理计划是否还在设置的有效时长中
        if (Objects.isNull(plan) || CollectionUtils.isEmpty(planDetailDTOS)) {
            return new ArrayList<>();
        }
        Integer execute = plan.getExecute();
        Integer effectiveTime = plan.getEffectiveTime();
        // 推送日期
        LocalDate pushDay;
        // 计划有效期
        LocalDate planValidityPeriod = null;
        LocalDate now = LocalDate.now();
        List<PatientCustomPlanTime> planTimes = new ArrayList<>(20);
        if (effectiveTime != 0) {

            // 用户入组后这个计划可以生效的最后日期
            planValidityPeriod = completeEnterGroupTime.toLocalDate().plusDays(execute).plusDays(effectiveTime);
            if (now.isAfter(planValidityPeriod)) {
                for (PlanDetailDTO detailDTO : planDetailDTOS) {
                    if (CollectionUtils.isEmpty(detailDTO.getPlanDetailTimes())) {
                        continue;
                    }
                    for (PlanDetailTime detailTime : detailDTO.getPlanDetailTimes()) {
                        PatientCustomPlanTime planTime = planTimeMap.get(detailTime.getId());
                        if (Objects.nonNull(planTime)) {
                            planTimes.add(planTime);
                            continue;
                        } else {
                            Integer frequency = detailTime.getFrequency();
                            planTime = PatientCustomPlanTime.builder()
                                    .patientId(patientId)
                                    .nursingPlantId(plan.getId())
                                    .nursingPlanDetailId(detailDTO.getId())
                                    .nursingPlanDetailTimeId(detailTime.getId())
                                    .customizeStatus(0)
                                    .planType(plan.getPlanType())
                                    .frequency(frequency)
                                    .nextRemindTime(null)
                                    .build();
                        }
                        planTimes.add(planTime);
                    }
                }
                // 今天已经在计划最后生效日期之前。 不需要再推送此计划
                return planTimes;
            }
        }
        for (PlanDetailDTO detailDTO : planDetailDTOS) {
            if (CollectionUtils.isEmpty(detailDTO.getPlanDetailTimes())) {
                continue;
            }
            for (PlanDetailTime detailTime : detailDTO.getPlanDetailTimes()) {
                // 患者上次保存的 自定义时间
                PatientCustomPlanTime planTime = planTimeMap.get(detailTime.getId());
                if (Objects.nonNull(planTime)) {
                    planTimes.add(planTime);
                    continue;
                }
                // 推送间隔
                Integer frequency = detailTime.getFrequency();
                if (frequency == null) {
                    continue;
                }

                // 用户首次推送时间
                pushDay = completeEnterGroupTime.toLocalDate().plusDays(detailTime.getPreTime()).plusDays(execute);

                PatientCustomPlanTime.PatientCustomPlanTimeBuilder builder = PatientCustomPlanTime.builder()
                        .patientId(patientId)
                        .nursingPlantId(plan.getId())
                        .nursingPlanDetailId(detailDTO.getId())
                        .nursingPlanDetailTimeId(detailTime.getId())
                        .customizeStatus(0)
                        .planType(plan.getPlanType())
                        .frequency(frequency);
                LocalDateTime remindTime = getPatientNextRemindTime(completeEnterGroupTime, detailTime, execute, frequency, now, effectiveTime);
                builder.nextRemindTime(remindTime);

                planTimes.add(builder.build());
            }
        }
        return planTimes;

    }

    /**
     * 计算患者入组后。计划首次的执行日期
     * @param completeEnterGroupTime
     * @param detailTime
     * @param execute
     * @return
     */
    @Override
    public LocalDate getPatientFirstRemindDay(LocalDateTime completeEnterGroupTime, PlanDetailTime detailTime, Integer execute, Integer frequency) {
        if (Objects.isNull(completeEnterGroupTime)) {
            completeEnterGroupTime = LocalDateTime.now();
        }
        if (Objects.isNull(detailTime)) {
            throw new BizException("推送时间不能为空");
        }
        if (Objects.isNull(execute)) {
            throw new BizException("execute不能为空");
        }
        LocalDate localDate = completeEnterGroupTime.toLocalDate().plusDays(detailTime.getPreTime()).plusDays(execute);
        if (frequency == 0) {
            return localDate;
        }
        // 如果 首次推送时间比 入组时间还早。需要计算入组时间后的。推送日期
        if (LocalDateTime.of(localDate, detailTime.getTime()).isBefore(completeEnterGroupTime)) {
            // 今天 日期减去 首次推送时间日期，
            long days = DateUtils.getDays(completeEnterGroupTime.toLocalDate(), localDate);
            if (completeEnterGroupTime.toLocalTime().isAfter(detailTime.getTime())) {
                days +=1;
            }
            // 第一次推送完成了，计算最接近今日的未来推送
            // frequency = 1  每天一次推送
            // frequency = 2  每两天一次推送
            if (frequency == 1) {
                return localDate.plusDays(days);
            } else if (days >= frequency) {
                long i = days / frequency;
                return localDate.plusDays((i + 1) * frequency);
            } else {
                return completeEnterGroupTime.toLocalDate().plusDays(frequency);
            }
        }

        return localDate;
    }


    /**
     * 推算出 患者最近的下次执行时间是什么时候
     * @param completeEnterGroupTime
     * @param detailTime
     * @param execute
     * @param frequency
     * @param currentDay
     * @return
     */
    @Override
    public LocalDateTime getPatientNextRemindTime(LocalDateTime completeEnterGroupTime,
                                         PlanDetailTime detailTime,
                                         Integer execute,
                                         Integer frequency,
                                         LocalDate currentDay,Integer effectiveTime) {
        // 推送日期  用户首次推送时间
        LocalDate pushDay = getPatientFirstRemindDay(completeEnterGroupTime, detailTime, execute, frequency);
        // 今天 日期减去 首次推送时间日期，
        long days = DateUtils.getDays(currentDay, pushDay);
        LocalDate now = LocalDate.now();
        if (currentDay.isBefore(now)) {
            if (effectiveTime != 0 && days >= effectiveTime) {
                return null;
            }
        } else {
            long days1 = DateUtils.getDays(now, pushDay);
            if (effectiveTime != 0 && days1 >= effectiveTime) {
                return null;
            }
        }

        if (frequency.equals(0)) {
            // 单次推送。计算推送日期
            return LocalDateTime.of(pushDay, detailTime.getTime());
        } else {

            // 循环推送
            // 第一次推送还没有推 或者 第一次推送今天推
            if (days <= 0) {
                return LocalDateTime.of(pushDay, detailTime.getTime());
            } else {
                // 第一次推送完成了，计算最接近今日的未来推送
                // frequency = 1  每天一次推送
                // frequency = 2  每两天一次推送
                if (frequency == 1) {
                    return LocalDateTime.of(pushDay.plusDays(days), detailTime.getTime());
                } else if (days >= frequency) {
                    long i = days / frequency;
                    return LocalDateTime.of(pushDay.plusDays((i + 1) * frequency), detailTime.getTime());
                } else {
                    return LocalDateTime.of(currentDay.plusDays(frequency - days), detailTime.getTime());
                }
            }
        }

    }


    /**
     * 取消患者对护理计划的订阅
     * @param patientId
     * @param nursingPlantId
     */
    @Override
    public void cancelPatientNursingPlan(Long patientId, Long nursingPlantId) {

        if (Objects.isNull(patientId) || Objects.isNull(nursingPlantId)) {
            return;
        }
        UpdateWrapper<PatientNursingPlan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_subscribe", 0)
                .eq("nursing_plant_id", nursingPlantId)
                .eq("patient_id", patientId);
        baseMapper.update(new PatientNursingPlan(), updateWrapper);

    }

    @Autowired
    PatientDrugsTimeService patientDrugsTimeService;

    /**
     * 患者今日待办
     * @param patientId 患者ID
     * @param menuController 是否需要菜单控制
     * @return
     */
    @Override
    public List<PatientMenuFollowItem> patientMenuFollow(Long patientId, boolean menuController) {

        if (menuController) {
            R<H5CoreFunctions> oneByCode = h5CoreFunctionApi.findOneByCode();
            boolean showFollow = false;
            if (oneByCode.getIsSuccess()) {
                H5CoreFunctions coreFunctions = oneByCode.getData();
                if (Objects.nonNull(coreFunctions)) {
                    Integer imStatus = coreFunctions.getCalendarStatus();
                    if (imStatus.equals(1)) {
                        showFollow = true;
                    }
                }
            }
            if (!showFollow) {
                return new ArrayList<>();
            }
        }
        List<FollowAllPlanDetailDTO> followAllPlanDetailDTOS = null;
        FollowTaskService taskService = SpringUtils.getBean(FollowTaskService.class);
        FollowAllExecutionCycleDTO executionCycleDTO;
        List<PatientMenuFollowItem> tempFollowItems = new ArrayList<>();
        // 查询患者的 随访计划 的详细设置
        executionCycleDTO = taskService.patientQueryFollowPlanCalendarDayPlanDetail(patientId, null, null, LocalDate.now());
        followAllPlanDetailDTOS = executionCycleDTO.getPlanDetails();
        if (CollUtil.isNotEmpty(followAllPlanDetailDTOS)) {
            // 查询项目的随访任务设置。拿到各个计划对应的显示名称
            List<FollowTaskContent> taskContents = followTaskContentService.list(Wraps.<FollowTaskContent>lbQ());
            if (CollUtil.isNotEmpty(taskContents)) {
                Map<String, String> taskContentMap = new HashMap<>();
                for (FollowTaskContent content : taskContents) {
                    if (content.getPlanType() != null && PlanEnum.LEARN_PLAN.getCode().equals(content.getPlanType())) {
                        taskContentMap.put(PlanEnum.LEARN_PLAN.getCode().toString(), content.getShowName());
                    } else {
                        taskContentMap.put(content.getPlanId().toString(), content.getShowName());
                    }
                }
                for (FollowAllPlanDetailDTO followAllPlanDetailDTO : followAllPlanDetailDTOS) {
                    Integer planType = followAllPlanDetailDTO.getPlanType();
                    Long planId = followAllPlanDetailDTO.getPlanId();
                    String planName = followAllPlanDetailDTO.getFirstShowTitle();
                    String followUpPlanType = followAllPlanDetailDTO.getFollowUpPlanType();
                    // 根据 planType 和 followUpPlanType 组装待办列表的 提示文案。
                    List<FollowAllPlanDetailTimeDTO> planDetailTimeDTOS = followAllPlanDetailDTO.getPlanDetailTimeDTOS();
                    String showName = followAllPlanDetailDTO.getFirstShowTitle();
                    if (Objects.nonNull(planType) && PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
                        showName = taskContentMap.get(PlanEnum.LEARN_PLAN.getCode().toString());
                    } else {
                        if (followAllPlanDetailDTO.getPlanId() != null) {
                            showName = taskContentMap.get(followAllPlanDetailDTO.getPlanId().toString());
                        }
                    }
                    String nameByPlanType = PlanFunctionTypeEnum.getNameByPlanType(planType, followUpPlanType);
                    PlanFunctionTypeEnum functionTypeEnum = PlanFunctionTypeEnum.getEnumByPlanType(planType, followUpPlanType);
                    for (FollowAllPlanDetailTimeDTO detailTimeDTO : planDetailTimeDTOS) {
                        int cmsReadStatus = detailTimeDTO.getCmsReadStatus();
                        if (cmsReadStatus == 0) {
                            PatientMenuFollowItem build = PatientMenuFollowItem.builder()
                                    .functionTypeEnum(functionTypeEnum)
                                    .functionTypeName(nameByPlanType)
                                    .planName(planName)
                                    .cmsId(detailTimeDTO.getCmsId())
                                    .cmsContentUrl(detailTimeDTO.getHrefUrl())
                                    .cmsReadStatus(detailTimeDTO.getCmsReadStatus())
                                    .planDetailTimeId(detailTimeDTO.getPlanDetailTimeId())
                                    .followDateTime(detailTimeDTO.getPlanExecutionDate())
                                    .planId(planId)
                                    .messageId(detailTimeDTO.getMessageId())
                                    .name(showName)
                                    .build();
                            build.setRemindContent(getRemindContentByInfo(planType, followUpPlanType, showName, null, build.getFollowDateTime()));
                            tempFollowItems.add(build);
                        }
                    }
                }
            }
        }
        Integer functionStatus = functionConfigurationService.getFunctionStatus(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.MEDICATION);
        if (functionStatus == 1) {
            Map<LocalTime, Integer> drugsTimes = patientDrugsTimeService.queryTodayDrugsTimes(patientId);
            if (CollUtil.isNotEmpty(drugsTimes)) {
                Set<Map.Entry<LocalTime, Integer>> entries = drugsTimes.entrySet();
                for (Map.Entry<LocalTime, Integer> entry : entries) {
                    LocalTime key = entry.getKey();
                    Integer value = entry.getValue();
                    PatientMenuFollowItem build = PatientMenuFollowItem.builder()
                            .functionTypeEnum(PlanFunctionTypeEnum.MEDICATION)
                            .functionTypeName(PlanFunctionTypeEnum.MEDICATION.getDesc())
                            .planName(PlanFunctionTypeEnum.MEDICATION.getDesc())
                            .cmsReadStatus(value == null || value.equals(2) ? 0 : 1)
                            .followDateTime(key)
                            .name(PlanFunctionTypeEnum.MEDICATION.getDesc())
                            .build();
                    build.setRemindContent(getRemindContentByInfo(PlanEnum.MEDICATION_REMIND.getCode(), null, null, key, build.getFollowDateTime()));
                    tempFollowItems.add(build);
                }
            }
        }

        tempFollowItems.sort((o1, o2) -> o1.getFollowDateTime().isBefore(o2.getFollowDateTime()) ? -1 : 1);
        List<PatientMenuFollowItem> followItemsTimeOut0 = new ArrayList<>();
        List<PatientMenuFollowItem> followItemsTimeOut1 = new ArrayList<>();
        for (PatientMenuFollowItem tempFollowItem : tempFollowItems) {
            if (!tempFollowItem.getFollowDateTime().isBefore(LocalTime.now())) {
                tempFollowItem.setTimeOut(0);
                followItemsTimeOut0.add(tempFollowItem);
            } else {
                tempFollowItem.setTimeOut(1);
                followItemsTimeOut1.add(tempFollowItem);
            }
        }
        tempFollowItems = new ArrayList<>();
        if (CollUtil.isNotEmpty(followItemsTimeOut0)) {
            tempFollowItems.addAll(followItemsTimeOut0);
        }
        if (CollUtil.isNotEmpty(followItemsTimeOut1)) {
            tempFollowItems.addAll(followItemsTimeOut1);
        }

        return tempFollowItems;
    }

    public String getRemindContentByInfo(Integer planType, String followUpPlanType, String showName, LocalTime localTime, LocalTime followDateTime) {
        if (planType != null) {
            if (PlanEnum.REVIEW_REMIND.getCode().equals(planType)) {
                // "您将于"+ LocalDate.now() +"进行复查！"
                return I18nUtils.getMessage("RemindContentByInfo_REVIEW_REMIND", LocalDate.now()) ;
            } else if (PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
                // "您有一条科普消息待查看！"
                return I18nUtils.getMessage("RemindContentByInfo_LEARN_PLAN") ;
            } else if (PlanEnum.HEALTH_LOG.getCode().equals(planType)) {
                // "您有一条健康日志待查看！"
                return I18nUtils.getMessage("RemindContentByInfo_HEALTH_LOG", showName);
            } else if (PlanEnum.MEDICATION_REMIND.getCode().equals(planType)) {
                //  "您将在 "+ localTime.withSecond(0).withNano(0).toString() +" 该用药咯！"
                return I18nUtils.getMessage("RemindContentByInfo_MEDICATION_REMIND", localTime.withSecond(0).withNano(0).toString());
            }
        }
        if (FollowUpPlanTypeEnum.CARE_PLAN.operateType.equals(followUpPlanType) || FollowUpPlanTypeEnum.MONITORING_DATA.operateType.equals(followUpPlanType)) {
            // "您将于"+followDateTime.toString()+"进行"+showName+"！"
            return I18nUtils.getMessage("RemindContentByInfo_MONITORING_DATA", followDateTime.toString(), showName);
        }
        return "";
    }

    /**
     * 医生 医助 都将患者个人中心重做后。可以弃用
     * @param patientId
     * @param doctorId
     * @return
     */
    @Deprecated
    @Override
    public PatientMenuFollow patientMenuFollow(Long patientId, Long doctorId) {

        PatientMenuFollow menuFollow = new PatientMenuFollow();
        // 查询随访日历是否要展示
        R<H5CoreFunctions> oneByCode = h5CoreFunctionApi.findOneByCode();
        boolean showFollow = false;
        if (oneByCode.getIsSuccess()) {
            H5CoreFunctions coreFunctions = oneByCode.getData();
            if (Objects.nonNull(coreFunctions)) {
                Integer imStatus = coreFunctions.getImStatus();
                if (imStatus.equals(0)) {
                    showFollow = true;
                }
            }
        }
        int followNum = 0;  // 随访计划的数量
        List<FollowAllPlanDetailDTO> followAllPlanDetailDTOS = null;
        FollowTaskService taskService = SpringUtils.getBean(FollowTaskService.class);
        FollowAllExecutionCycleDTO executionCycleDTO;
        if (showFollow) {
            R<Doctor> doctorR = doctorApi.get(doctorId);
            if (doctorR.getIsSuccess()) {
                Doctor doctor = doctorR.getData();
                menuFollow.setDoctorAvatar(doctor.getAvatar());
            }
            int followCount = 0;   // 随访未推送的次数
            List<PatientMenuFollowItem> tempFollowItems = new ArrayList<>();
            List<PatientMenuFollowItem> resultFollowItems = new ArrayList<>();
            // 查询患者定于的 随访计划 的详细设置
            executionCycleDTO = taskService.patientQueryFollowPlanCalendarDayPlanDetail(patientId, null, null, LocalDate.now());
            followAllPlanDetailDTOS = executionCycleDTO.getPlanDetails();
            // 学习计划怎么算 ？ 展示学习计划的标题，还是什么呢
            if (CollUtil.isNotEmpty(followAllPlanDetailDTOS)) {
                // 查询项目的随访任务设置。拿到各个计划对应的显示名称
                List<FollowTaskContent> taskContents = followTaskContentService.list(Wraps.<FollowTaskContent>lbQ());
                if (CollUtil.isNotEmpty(taskContents)) {
                    Map<String, String> taskContentMap = new HashMap<>();
                    for (FollowTaskContent content : taskContents) {
                        if (content.getPlanType() != null && PlanEnum.LEARN_PLAN.getCode().equals(content.getPlanType())) {
                            taskContentMap.put(PlanEnum.LEARN_PLAN.getCode().toString(), content.getShowName());
                        } else {
                            taskContentMap.put(content.getPlanId().toString(), content.getShowName());
                        }
                    }
                    for (FollowAllPlanDetailDTO followAllPlanDetailDTO : followAllPlanDetailDTOS) {
                        Integer planType = followAllPlanDetailDTO.getPlanType();
                        List<FollowAllPlanDetailTimeDTO> planDetailTimeDTOS = followAllPlanDetailDTO.getPlanDetailTimeDTOS();
                        String showName = followAllPlanDetailDTO.getFirstShowTitle();
                        if (Objects.nonNull(planType) && PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
                            showName = taskContentMap.get(PlanEnum.LEARN_PLAN.getCode().toString());
                        } else {
                            if (followAllPlanDetailDTO.getPlanId() != null) {
                                showName = taskContentMap.get(followAllPlanDetailDTO.getPlanId().toString());
                            }
                        }
                        followNum++;
                        for (FollowAllPlanDetailTimeDTO detailTimeDTO : planDetailTimeDTOS) {
                            int cmsReadStatus = detailTimeDTO.getCmsReadStatus();
                            if (cmsReadStatus == 0) {
                                followCount++;
                                tempFollowItems.add(PatientMenuFollowItem.builder()
                                        .followDateTime(detailTimeDTO.getPlanExecutionDate())
                                        .name(showName)
                                        .build());
                            }
                        }
                    }
                }
            }
            for (PatientMenuFollowItem tempFollowItem : tempFollowItems) {
                if (!tempFollowItem.getFollowDateTime().isBefore(LocalTime.now())) {
                    tempFollowItem.setTimeOut(0);
                } else {
                    tempFollowItem.setTimeOut(1);
                }
            }
            tempFollowItems.sort((o1, o2) -> o1.getFollowDateTime().isBefore(o2.getFollowDateTime()) ? -1 : 1);
            menuFollow.setFollowCount(followCount);
            menuFollow.setFollowItems(tempFollowItems);
        }

        // 查询 患者  我的功能 中的菜单开启状态，然后计算开启菜单的 业务数据统计。
        String userType = BaseContextHandler.getUserType();
        if (userType == null) {
            userType = UserType.PATIENT;
        }
        R<List<H5Router>> router = h5RouterApi.getH5RouterByModuleType(RouterModuleTypeEnum.MY_FEATURES, userType);
        List<H5Router> h5RouterList = new ArrayList<>();
        if (router.getIsSuccess()) {
            List<H5Router> tempH5Routers = router.getData();
            for (H5Router h5Router : tempH5Routers) {
                // 菜单的 功能类型不能为空，菜单的功能类型属于 我的功能 分类。
                if (StrUtil.isNotEmpty(h5Router.getDictItemType()) && RouterModuleTypeEnum.MY_FEATURES.eq(RouterModuleTypeEnum.matchEnum(h5Router.getDictItemType()))) {
                    h5RouterList.add(h5Router);
                }
            }
        }

        if (CollUtil.isNotEmpty(h5RouterList)) {
            // 根据 h5RouterList 中的 DictItemType 统计相应的数据。
            for (H5Router h5Router : h5RouterList) {
                Long routerId = h5Router.getId();
                String dictItemType = h5Router.getDictItemType();
                if ("MEDICINE".equals(dictItemType)) {
                    // 统计患者正在使用的 药箱药品
                    int count = patientDrugsService.count(Wraps.<PatientDrugs>lbQ().eq(PatientDrugs::getPatientId, patientId).eq(PatientDrugs::getStatus, 0));
                    menuFollow.addMenuDataCountList(routerId, count);
                }
                // 统计患者的预约数据
                if ("RESERVATION_INDEX".equals(dictItemType)) {
                    Integer count = appointmentMapper.selectCount(Wraps.<Appointment>lbQ().eq(Appointment::getPatientId, patientId).in(Appointment::getStatus,
                            AppointmentStatusEnum.NO_VISIT, AppointmentStatusEnum.UNDER_REVIEW));
                    if (count == null) {
                        menuFollow.addMenuDataCountList(routerId, 0);
                    } else {
                        menuFollow.addMenuDataCountList(routerId, count);
                    }
                }
                // 随访
                if ("FOLLOW_UP".equals(dictItemType)) {
                    if (followAllPlanDetailDTOS == null) {
                        executionCycleDTO = taskService.patientQueryFollowPlanCalendarDayPlanDetail(patientId, null, null, LocalDate.now());
                        followAllPlanDetailDTOS = executionCycleDTO.getPlanDetails();
                        if (CollUtil.isNotEmpty(followAllPlanDetailDTOS)) {
                            followNum = followAllPlanDetailDTOS.size();
                        }
                    }
                    menuFollow.addMenuDataCountList(routerId, followNum);
                }
                // 智能提醒
                if ("REMIND".equals(dictItemType)) {
                    // 不包含 用药。
                    List<Plan> patientSeePlan = taskService.getPatientSeePlan(patientId);
                    List<PatientNursingPlan> nursingPlans = patientSubscribeList(patientId);
                    List<Long> patientNursingPLanIds = nursingPlans.stream()
                            .map(PatientNursingPlan::getNursingPlantId).collect(Collectors.toList());
                    int count = 0;
                    Set<Long> planIds = new HashSet<>();
                    // 查询项目的用药计划设置，如果开启。
                    Plan medication = planMapper.selectOne(Wraps.<Plan>lbQ()
                            .eq(Plan::getStatus, 1)
                            .eq(Plan::getPlanType, PlanEnum.MEDICATION_REMIND.getCode()).last(" limit 0,1 "));
                    if (Objects.nonNull(medication)) {
                        patientSeePlan.add(medication);
                    }
                    if (CollUtil.isNotEmpty(patientSeePlan) && CollUtil.isNotEmpty(patientNursingPLanIds)) {
                        for (Plan plan : patientSeePlan) {
                            if (patientNursingPLanIds.contains(plan.getId())) {
                                if (!planIds.contains(plan.getId())) {
                                    count++;
                                    planIds.add(plan.getId());
                                }
                            }
                        }
                    }
                    menuFollow.addMenuDataCountList(routerId, count);
                }
            }
        }

        return menuFollow;
    }

    @Autowired
    PlanService planService;

    @Autowired
    WeixinSender weixinSender;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    PlanCmsReminderLogService planCmsReminderLogService;

    @Autowired
    TemplateMsgApi templateMsgApi;

    /**
     * 查询注册完成时的 护理计划。并进行推送
     * // TODO: 需要处理个人服务号的短信推送
     *
     * @param tenantCode
     * @param patientId
     */
    @Override
    public void queryRegisterCompletePlanAndPush(String tenantCode, Long patientId) {

        BaseContextHandler.setTenant(tenantCode);
        List<Plan> planList = planService.list(Wraps.<Plan>lbQ().eq(Plan::getPlanModel, 2).eq(Plan::getStatus, 1));
        if (CollUtil.isEmpty(planList)) {
            return;
        }
        NursingPlanPatientDTO patientDTO = new NursingPlanPatientDTO();
        patientDTO.setTenantCode(tenantCode);
        patientDTO.setIds(Collections.singletonList(patientId));
        R<List<NursingPlanPatientBaseInfoDTO>> nursingPlan = patientApi.getBaseInfoForNursingPlan(patientDTO);
        if (!nursingPlan.getIsSuccess()) {
            return;
        }
        List<NursingPlanPatientBaseInfoDTO> patientBaseInfoDTOS = nursingPlan.getData();
        if (CollUtil.isEmpty(patientBaseInfoDTOS)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        NursingPlanPatientBaseInfoDTO patientBaseInfoDTO = patientBaseInfoDTOS.get(0);

        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        if (!tenantR.getIsSuccess()) {
            return;
        }
        Tenant tenant = tenantR.getData();

        R<TemplateMsgDto> templateMsgR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
        TemplateMsgDto templateMsgDto = templateMsgR.getData();
        Map<String, TemplateMsgDto> templateMsgDtoMap = new HashMap<>();
        NursingPlanPushParam nursingPlanPushParam = new NursingPlanPushParam();
        nursingPlanPushParam.setTemplateMsgDto(templateMsgDto);
        nursingPlanPushParam.setTemplateMsgDtoMap(templateMsgDtoMap);

        for (Plan plan : planList) {
            List<PlanDetailDTO> planDetailDTOS = planDetailService.findDetailWithTimeById(plan.getId());
            if (CollUtil.isEmpty(planDetailDTOS)) {
                continue;
            }
            for (PlanDetailDTO detailDTO : planDetailDTOS) {
                List<PlanDetailTime> detailTimes = detailDTO.getPlanDetailTimes();
                if (detailTimes.isEmpty()) {
                    break;
                }
                for (PlanDetailTime detailTime : detailTimes) {
                    // 与前端约定 -9999 代表注册完成时进行推送
                    if (detailTime.getPreTime().equals(-9999)) {
                        String url = null;
                        boolean cmsLink = false;
                        if (detailDTO.getPushType() != null && detailDTO.getPushType().equals(2)) {
                            // 此处说明，推送的是 一个模板，模板链接是文章
                            cmsLink = true;
                            if (org.apache.commons.lang.StringUtils.isNotEmpty(detailDTO.getContent())) {
                                url = detailDTO.getContent();
                            }
                        }

                        String templateMessageId = null;
                        if (!org.apache.commons.lang.StringUtils.isEmpty(detailTime.getTemplateMessageId())) {
                            // 推送微信模板消息
                            templateMessageId = detailTime.getTemplateMessageId();
                        } else if (!org.apache.commons.lang.StringUtils.isEmpty(detailDTO.getTemplateMessageId())) {
                            // 推送微信模板消息
                            templateMessageId = detailDTO.getTemplateMessageId();
                        }
                        if (templateMessageId != null && StrUtil.isNotBlank(templateMessageId) && templateMsgDtoMap.get(templateMessageId) == null) {
                            R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, Long.parseLong(templateMessageId), null);
                            TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
                            if (Objects.nonNull(msgDtoRData)) {
                                templateMsgDtoMap.put(templateMessageId, msgDtoRData);
                            }
                        }
                        TemplateMsgDto useTemplateMsg = null;
                        if (templateMessageId != null) {
                            useTemplateMsg = templateMsgDtoMap.get(templateMessageId);
                        } else {
                            useTemplateMsg = templateMsgDto;
                        }
                        // 创建一条推送记录
                        ReminderLog reminderLog = createReminderLog(patientBaseInfoDTO, PlanEnum.getPlanEnum(plan.getPlanType()), detailTime.getId().toString(), plan.getId());
                        String errorMessage = weixinSender.sendWeiXinMessage(
                                   tenant, cmsLink,
                                   plan.getFollowUpPlanType(), plan.getId(),
                                   patientBaseInfoDTO,
                                    PlanEnum.getPlanEnum(plan.getPlanType()),
                                    null, url, plan.getName(), now,  plan.getRemindTemplateTitle(), null, useTemplateMsg, reminderLog);

                        if (errorMessage != null) {
                            reminderLog.setErrorMessage(errorMessage);
                            reminderLog.setStatus(-3);
                            getReminderLogService().updateById(reminderLog);
                        } else {
                            reminderLog.setStatus(-2);
                            getReminderLogService().updateById(reminderLog);
                        }

                        if (cmsLink && StrUtil.isNotEmpty(url)) {
                            PlanCmsReminderLog planCmsReminderLog = new PlanCmsReminderLog();
                            planCmsReminderLog.setCmsLink(url);
                            planCmsReminderLog.setSendTime(now);
                            planCmsReminderLog.setUserId(patientBaseInfoDTO.getId());
                            planCmsReminderLogService.submitSyncCmsLinkSave(planCmsReminderLog, tenantCode);
                        }
                    }
                }

            }

        }

    }



    /**
     * @return java.lang.Long
     * @Author yangShuai
     * @Description 为护理计划创建一个 推送记录
     * @Date 2020/10/27 14:43
     */
    public ReminderLog createReminderLog(NursingPlanPatientBaseInfoDTO patient, PlanEnum planType, String workId, Long planId) {
        cn.hutool.json.JSONArray jsonArray = new cn.hutool.json.JSONArray();
        jsonArray.add(workId);
        if (null == planType) {
            return getReminderLogService().createReminderLog(patient.getId(), "监测计划或自定义随访",
                    JSONUtil.toJsonStr(jsonArray), 0, planId, patient.getOrganId(), patient.getClassCode(), patient.getDoctorId(), patient.getServiceAdvisorId());
        }
        return getReminderLogService().createReminderLog(patient.getId(), planType.getDesc(),
                JSONUtil.toJsonStr(jsonArray), planType.getCode(), planId, patient.getOrganId(), patient.getClassCode(), patient.getDoctorId(), patient.getServiceAdvisorId());
    }

    /**
     * 查询项目中未关注该计划的患者。自动订阅上
     * @param tenant
     * @param planId
     */
    @Override
    public void patientBindPlan(String tenant, Long planId) {

        BaseContextHandler.setTenant(tenant);

        PageParams<PatientPageDTO> params = new PageParams<>();
        PatientPageDTO pageDTO = new PatientPageDTO();
        pageDTO.setTenantCode(tenant);
        pageDTO.setCurrentUserType(UserType.ADMIN);
        params.setModel(pageDTO);
        int current = 1;
        while (true) {
            params.setCurrent(current);
            params.setSize(200);
            // 查询 患者列表
            R<IPage<Patient>> pageR = patientApi.exportPageWithScope(params);
            current++;
            IPage<Patient> scopeData = pageR.getData();

            List<Patient> records = scopeData.getRecords();
            List<Long> patientIds = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
            List<PatientNursingPlan> patientNursingPlans = baseMapper.selectList(Wraps.<PatientNursingPlan>lbQ()
                    .select(PatientNursingPlan::getPatientId, SuperEntity::getId)
                    .in(PatientNursingPlan::getPatientId, patientIds)
                    .eq(PatientNursingPlan::getNursingPlantId, planId));
            List<Long> existPatientIds = patientNursingPlans.stream().map(PatientNursingPlan::getPatientId).collect(Collectors.toList());
            patientIds.removeAll(existPatientIds);

            if (!patientIds.isEmpty()) {
                List<PatientNursingPlan> planArrayList = new ArrayList<>(patientIds.size());
                PatientNursingPlan patientNursingPlan = new PatientNursingPlan();
                for (Long patientId : patientIds) {
                    patientNursingPlan = new PatientNursingPlan();
                    patientNursingPlan.setPatientId(patientId);
                    patientNursingPlan.setNursingPlantId(planId);
                    patientNursingPlan.setStartDate(LocalDate.now());
                    patientNursingPlan.setIsSubscribe(1);
                    patientNursingPlan.setPatientCancelSubscribe(0);
                    planArrayList.add(patientNursingPlan);
                }
                baseMapper.insertBatchSomeColumn(planArrayList);
            }
            if (current > scopeData.getPages()) {
                break;
            }
        }



    }
}
