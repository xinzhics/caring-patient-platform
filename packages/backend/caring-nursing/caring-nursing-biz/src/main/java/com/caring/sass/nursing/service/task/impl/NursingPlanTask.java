package com.caring.sass.nursing.service.task.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.plan.PatientCustomPlanTimeMapper;
import com.caring.sass.nursing.dao.plan.PlanTagMapper;
import com.caring.sass.nursing.dto.NursingTaskExecuteParams;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.entity.plan.*;
import com.caring.sass.nursing.enumeration.FollowUpPlanTypeEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import com.caring.sass.nursing.service.plan.*;
import com.caring.sass.nursing.service.tag.AssociationService;
import com.caring.sass.nursing.service.task.NursingPlanPushParam;
import com.caring.sass.nursing.service.task.PushAlgorithm;
import com.caring.sass.nursing.service.task.SuperTask;
import com.caring.sass.nursing.service.task.sender.WeixinSender;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.user.dto.NursingPlanUpdatePatientDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 初步优化任务执行策略。
 * 首先由job任务中心发起任务。
 * job 任务调用jvm其中一个nursing实例。
 * 实例查询所有的合适的租户。
 *
 * 将每个待执行的租户 push 进入redis的消息队列。
 *
 * 实例从redis中获取到待执行的租户。
 * 将任务丢入线程池中， 一个jvm实例 最多3个子线程 处理租户任务。每处理完成一个，就重新从redis中拉取
 *
 * 直到redis中的任务被执行完毕
 *
 */
@Slf4j
@Service
public class NursingPlanTask extends SuperTask {

    private PlanService planService;

    private final PlanTagMapper planTagMapper;

    private final PlanDetailService planDetailService;

    private final PlanDetailTimeService planDetailTimeService;

    private final PatientNursingPlanService patientNursingPlanService;

    private final PatientApi patientApi;

    private final DoctorApi doctorApi;

    private final TemplateMsgApi templateMsgApi;

    private final ChannelContentApi channelContentApi;

    private final AssociationService associationService;

    private final PatientCustomPlanTimeMapper patientCustomPlanTimeMapper;

    private final WeixinSender weixinSender;

    private final PlanCmsReminderLogService planCmsReminderLogService;

    private ReminderLogService reminderLogService;

    FunctionConfigurationService functionConfigurationService;

    private final H5RouterApi h5RouterApi;

    private final RedisTemplate<String, String> redisTemplate;

    private final AtomicInteger maximumPoolSize = new AtomicInteger(0);
    private static ExecutorService executor;


    public NursingPlanTask(PatientApi patientApi,
                           TemplateMsgApi templateMsgApi,
                           PatientNursingPlanService patientNursingPlanService,
                           AssociationService associationService,
                           PlanDetailService planDetailService,
                           ChannelContentApi channelContentApi,
                           PlanDetailTimeService planDetailTimeService,
                           PlanTagMapper planTagMapper,
                           PatientCustomPlanTimeMapper patientCustomPlanTimeMapper,
                           PlanCmsReminderLogService planCmsReminderLogService,
                           WeixinSender weixinSender,
                           DoctorApi doctorApi,
                           RedisTemplate<String, String> redisTemplate,
                           H5RouterApi h5RouterApi) {
        this.patientNursingPlanService = patientNursingPlanService;
        this.templateMsgApi = templateMsgApi;
        this.patientApi = patientApi;
        this.channelContentApi = channelContentApi;
        this.planDetailService = planDetailService;
        this.associationService = associationService;
        this.planTagMapper = planTagMapper;
        this.weixinSender = weixinSender;
        this.patientCustomPlanTimeMapper = patientCustomPlanTimeMapper;
        this.planCmsReminderLogService = planCmsReminderLogService;
        this.planDetailTimeService = planDetailTimeService;
        this.doctorApi = doctorApi;
        this.redisTemplate = redisTemplate;
        this.h5RouterApi = h5RouterApi;

        NamedThreadFactory threadFactory = new NamedThreadFactory("nursing-task-", false);
        executor = new ThreadPoolExecutor(1, 4, 0L,
                TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(200),
                threadFactory);
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

    public PlanService getPlanService() {
        if (planService == null) {
            planService = SpringUtils.getBean(PlanService.class);
        }
        return planService;
    }

    /**
     * 本地任务执行队列
     */
    static String NURSING_TASK_EXECUTE = "nursing_task_execute";


    /**
     * 随访计划处理中心.
     * 实时从redis中获取到租户，然后处理。
     */
    public void handleMessage() {

        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent("nursing_plan_task_handle_message_if_absent", "1", 5, TimeUnit.MINUTES);
        if (lockAcquired == null || !lockAcquired) {
            log.warn("Another instance is running this task.");
            return;
        }
        try {
            while (true) {
                // 处理此项目的消息
                if (maximumPoolSize.get() >= 180) {
                    break;
                }
                try {
                    String value = redisTemplate.opsForList().rightPop(NURSING_TASK_EXECUTE);
                    if (StrUtil.isNotEmpty(value)) {
                        maximumPoolSize.incrementAndGet();
                        executor.execute(() -> handleMessage(value));
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 或者使用日志框架记录日志
                    log.error("Error occurred while handling message", e);
                }
            }
        } finally {
            redisTemplate.delete("nursing_plan_task_handle_message_if_absent");
        }


    }


    /**
     * 处理项目的任务
     * 根据项目租户，查询计划信息 用户信息。
     * 给用户生成今日 学习计划，监测计划，随访计划，血压血糖，复查提醒，健康日志 需要发送的推送任务。
     *
     * 用户自定义的护理计划推送和 用户的注射记录超时填写，属于到点发送计划。
     *
     * @param message
     */
    public void handleMessage(String message) {
        log.error("nursingplantask handleMessage: {}", message);
        try {
            NursingTaskExecuteParams messageContent = new NursingTaskExecuteParams();
            messageContent = messageContent.fromJSONString(message);
            String tenantCode = messageContent.getTenantCode();
            LocalDateTime date = messageContent.getStartDate();
            String taskType = messageContent.getTaskType();
            switch (taskType) {
                // 患者自定义护理计划
                case "customNursingPlanExecute":
                    customNursingPlanExecute(tenantCode, date);
                    break;
                // 患者的注射记录。填写超时。
                case "injectionFormTimeOutRemind":
                    injectionFormTimeOutRemind(tenantCode, date);
                    break;

                // 自定义计划 分为 自定义监测计划， 自定义随访计划。 只创建推送任务
                case "customPlanPushExecute":
                    customPlanPushExecute(tenantCode, date);
                    break;

                // 医生和患者的学习计划. 只创建推送任务
                case "learnPlanExecute":
                    learnPlanExecute(tenantCode, date);
                    break;

                // 复查提醒，血压 血糖，健康日志， 只创建推送任务
                case "nursingPlanPush":
                    nursingPlanPush(tenantCode, messageContent.getPlanEnum(), date);
                    break;

                case "handleReminderLogPush":
                    handleReminderLogPush(tenantCode, date);
            }
        } finally {
            maximumPoolSize.decrementAndGet();
        }

    }


    /**
     * @return boolean
     * @Author yangShuai
     * @Description 检验护理计划要求的标签。患者是否拥有。
     * @Date 2020/10/27 16:37
     */
    private boolean checkPatientTag(Long patientId, Long planId) {
        // 项目标签
        try {
            LbqWrapper<PlanTag> lbqWrapper = new LbqWrapper<>();
            lbqWrapper.eq(PlanTag::getNursingPlanId, planId);
            List<PlanTag> planTags = planTagMapper.selectList(lbqWrapper);
            if (CollectionUtils.isEmpty(planTags)) {
                return true;
            }
            // 患者是否有该标签
            for (PlanTag planTag : planTags) {
                Long tagId = planTag.getTagId();
                boolean existsTag = associationService.existsTag(tagId,  patientId.toString());
                if (existsTag) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("checkPatientTag error: patientId: {}, planId: {}", patientId, planId);
        }
        return false;
    }

    /**
     * 血压 血糖 随访计划 等执行
     * @param tenantCode
     * @param planList
     * @param planEnum
     * @param tenant
     * @param date
     */
    public void execute(String tenantCode, List<Plan> planList, PlanEnum planEnum, Tenant tenant, LocalDateTime date) {
        BaseContextHandler.setTenant(tenantCode);
        if (CollUtil.isEmpty(planList)) {
            return;
        }
        List<Long> planIds = planList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        planList = getPlanService().getPlanAndDetails(planIds);
        for (Plan plan : planList) {
            // 查询护理计划的详细
            List<PlanDetailDTO> planDetailDTOS = plan.getPlanDetailList();
            if (CollUtil.isEmpty(planDetailDTOS)) {
                continue;
            }
            LbqWrapper<PatientNursingPlan> lbqWrapper = Wraps.<PatientNursingPlan>lbQ()
                    .eq(PatientNursingPlan::getIsSubscribe, 1)
                    // 添加用户状态过滤
                    .apply(" patient_id in (select id from u_user_patient where status_ = 1 and is_complete_enter_group = 1 and tenant_code = '" + tenant.getCode() + "') ")
                    .eq(PatientNursingPlan::getNursingPlantId, plan.getId());
            List<PatientNursingPlan> patientNursingPlans = patientNursingPlanService.list(lbqWrapper);
            if (CollUtil.isEmpty(patientNursingPlans)) {
                continue;
            }
            Set<Long> personIds = patientNursingPlans.stream().map(PatientNursingPlan::getPatientId).collect(Collectors.toSet());
            List<Long> personIdsList = new ArrayList<>(personIds);
            List<List<Long>> subList = ListUtils.subList(personIdsList, 200);
            NursingPlanPatientDTO planPatientDTO;
            Map<Long, NursingPlanPatientBaseInfoDTO> patientMap = new HashMap<>(personIdsList.size());
            for (List<Long> longList : subList) {
                planPatientDTO = new NursingPlanPatientDTO(longList, tenantCode);
                R<List<NursingPlanPatientBaseInfoDTO>> infoForNursingPlan = patientApi.getBaseInfoForNursingPlan(planPatientDTO);
                if (infoForNursingPlan.getIsSuccess().equals(true)) {
                    for (NursingPlanPatientBaseInfoDTO planDatum : infoForNursingPlan.getData()) {
                        patientMap.put(planDatum.getId(), planDatum);
                    }
                }
            }
//            TemplateMsgDto templateMsgDto = null;
//            // 在这里吧使用的模版查询出来。避免后面的循环查询
//            Map<String, TemplateMsgDto> templateMsgDtoMap = new HashMap<>();
//            for (PlanDetailDTO plantDetail : planDetailDTOS) {
//                List<PlanDetailTime> detailTimes = plantDetail.getPlanDetailTimes();
//                for (PlanDetailTime detailTime : detailTimes) {
//                    String templateMessageId = null;
//                    if (!StringUtils.isEmpty(detailTime.getTemplateMessageId())) {
//                        // 推送微信模板消息
//                        templateMessageId = detailTime.getTemplateMessageId();
//                    } else if (!StringUtils.isEmpty(plantDetail.getTemplateMessageId())) {
//                        // 推送微信模板消息
//                        templateMessageId = plantDetail.getTemplateMessageId();
//                    }
//                    R<TemplateMsgDto> templateMsgDtoR;
//                    if (StrUtil.isEmpty(templateMessageId)) {
//                        templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
//                    } else {
//                        templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, Long.parseLong(templateMessageId), null);
//                    }
//                    if (StrUtil.isEmpty(templateMessageId)) {
//                        templateMsgDto = templateMsgDtoR.getData();
//                    } else if (templateMsgDtoR.getData() != null) {
//                        templateMsgDtoMap.put(templateMessageId, templateMsgDtoR.getData());
//                    }
//                }
//            }


            // 循环患者护理计划关联表。查询患者信息
            for (PatientNursingPlan nursingPlan : patientNursingPlans) {
                NursingPlanPatientBaseInfoDTO patient = patientMap.get(nursingPlan.getPatientId());
                if (Objects.isNull(patient)) {
                    continue;
                }
                // 校验 患者的标签 和 护理计划的标签是否匹配，
                if (checkPatientTag(patient.getId(), plan.getId())) {

                    // 初步检验通过。 将任务分发给对应的护理计划处理 方法
                    if (PlanEnum.LEARN_PLAN == planEnum) {
                        log.info("尝试推送学习计划");
                        studyPlan(planDetailDTOS, plan, patient, tenant, date);
                        continue;
                    }
                    NursingPlanPushParam pushParam = new NursingPlanPushParam(planDetailDTOS, plan, patient, tenant, date, planEnum, null, null);
                    try {
                        sendNursingPlant(pushParam);
                    } catch (Exception e) {
                        log.error("[execute] push error, pushParam: {}", JSON.toJSONString(pushParam));
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 自定义计划 分为 自定义监测计划， 自定义随访计划。
     * 生成 自定义监测计划， 自定义随访计划 的推送任务
     * @param date
     */
    public void customPlanPushExecute(String tenantCode, LocalDateTime date) {

        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        if (!tenantR.getIsSuccess()) {
            return;
        }
        Tenant tenant = tenantR.getData();
        if (Objects.isNull(tenant)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        List<Plan> planList = new ArrayList<>();

        // 查询 指标监测 的状态是否开启
        Integer functionStatus = getFunctionConfigurationService().getFunctionStatus(tenant.getCode(), PlanFunctionTypeEnum.INDICATOR_MONITORING);
        if (functionStatus.equals(1)) {
            // 查询自定义监测计划
            LbqWrapper<Plan> planLbqWrapper = new LbqWrapper<>();
            // type 是null 可以过滤掉血压血糖
            planLbqWrapper.isNull(Plan::getPlanType);
            planLbqWrapper.eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.MONITORING_DATA.operateType);
            planLbqWrapper.eq(Plan::getStatus, 1);
            planList.addAll(getPlanService().list(planLbqWrapper));
        }

        // 查询 自定义随访 的状态是否开启
        functionStatus = getFunctionConfigurationService().getFunctionStatus(tenant.getCode(), PlanFunctionTypeEnum.CUSTOM_FOLLOW_UP);
        if (functionStatus.equals(1)) {
            // 查询自定义监测计划
            LbqWrapper<Plan> planLbqWrapper = new LbqWrapper<>();
            planLbqWrapper.isNull(Plan::getPlanType);
            planLbqWrapper.eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.CARE_PLAN.operateType);
            planLbqWrapper.eq(Plan::getIsAdminTemplate, 1);
            planLbqWrapper.eq(Plan::getStatus, 1);
            planList.addAll(getPlanService().list(planLbqWrapper));
        }
        if (CollUtil.isEmpty(planList)) {
            return;
        }
        try {
            execute(tenant.getCode(), planList, PlanEnum.CUSTOM_PLAN, tenant, date);
        } catch (Exception e) {
            log.error("[execute] push error, tenantCode: {}, planList: {}", tenant.getCode(), JSON.toJSONString(planList));
            e.printStackTrace();
        }

    }

    /**
     * 自定义计划 分为 自定义监测计划， 自定义随访计划。
     * 分发任务
     * @param date
     */
    public void customPlanPushExecute(LocalDateTime date) {
        List<Tenant> normalTenant = getAllNormalTenant();
        for (Tenant tenant : normalTenant) {

            NursingTaskExecuteParams params = new NursingTaskExecuteParams();
            params.setTenantCode(tenant.getCode());
            params.setTaskType("customPlanPushExecute");
            params.setStartDate(date);
            redisTemplate.opsForList().leftPush(NURSING_TASK_EXECUTE, params.toJSONString());

        }
    }

    /**
     * 处理学习计划
     * @param tenantCode
     * @param date
     */
    public void learnPlanExecute(String tenantCode, LocalDateTime date) {

        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        if (!tenantR.getIsSuccess()) {
            return;
        }
        Tenant tenant = tenantR.getData();
        if (Objects.isNull(tenant)) {
            return;
        }
        // 查询学习计划 并按角色分组
        LbqWrapper<Plan> planLbqWrapper = new LbqWrapper<>();
        planLbqWrapper.eq(Plan::getPlanType, PlanEnum.LEARN_PLAN.getCode());
        planLbqWrapper.eq(Plan::getStatus, 1);

        List<Plan> planList = getPlanService().list(planLbqWrapper);
        if (CollUtil.isEmpty(planList)) {
            return;
        }
        Map<String, List<Plan>> listMap = planList.stream().collect(Collectors.groupingBy(Plan::getLearnPlanRole));
        // 查询患者的学习计划。
        List<Plan> patientPlans = listMap.get(UserType.UCENTER_PATIENT);

        // 执行患者的学习计划的推送
        execute(tenant.getCode(), patientPlans, PlanEnum.LEARN_PLAN, tenant, date);

        // 查询医生的学习计划。
        List<Plan> doctorPlans = listMap.get(UserType.UCENTER_DOCTOR);
        try {
            doctorLearnPlanPush(tenant, doctorPlans, date);
        } catch (Exception e) {
            log.error("[learnPlanExecute] push error, tenantCode: {} pushParam: {}", tenant.getCode(), JSON.toJSONString(doctorPlans));
            e.printStackTrace();
        }
    }

    /**
     * 学习计划的推送
     * 任务分发
     * @param localDateTime
     */
    public void learnPlanExecute(LocalDateTime localDateTime) {
        List<Tenant> normalTenant = getAllNormalTenant();
        for (Tenant tenant : normalTenant) {
            BaseContextHandler.setTenant(tenant.getCode());
            Integer functionStatus = getFunctionConfigurationService().getFunctionStatus(tenant.getCode(), PlanFunctionTypeEnum.LEARNING_PLAN);
            if (!functionStatus.equals(1)) {
                continue;
            }
            NursingTaskExecuteParams params = new NursingTaskExecuteParams();
            params.setTenantCode(tenant.getCode());
            params.setTaskType("learnPlanExecute");
            params.setStartDate(localDateTime);
            redisTemplate.opsForList().leftPush(NURSING_TASK_EXECUTE,  params.toJSONString());
        }
    }

    public void nursingPlanPush(String tenantCode, PlanEnum planEnum, LocalDateTime date) {

        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        if (!tenantR.getIsSuccess()) {
            return;
        }
        Tenant tenant = tenantR.getData();
        if (Objects.isNull(tenant)) {
            return;
        }
        BaseContextHandler.setTenant(tenant.getCode());
        LbqWrapper<Plan> planLbqWrapper = new LbqWrapper<>();
        if (planEnum.getCode() == null) {
            planLbqWrapper.isNull(Plan::getPlanType);
        } else {
            planLbqWrapper.eq(Plan::getPlanType, planEnum.getCode());
        }
        planLbqWrapper.eq(Plan::getStatus, 1);
        // 查询项目下的所有的护理计划
        List<Plan> planList = getPlanService().list(planLbqWrapper);
        if (CollUtil.isEmpty(planList)) {
            return;
        }

        // 如果是健康日志。 查询项目的健康日志 菜单的名字。 将菜单名称 设置到 基本名称上。
//        if (PlanEnum.HEALTH_LOG.equals(planEnum)) {
//            R<com.caring.sass.tenant.entity.router.H5Router> h5RouterR = h5RouterApi.getH5Router("HEALTH_CALENDAR", UserType.PATIENT);
//            if (h5RouterR.getIsSuccess()) {
//                com.caring.sass.tenant.entity.router.H5Router data = h5RouterR.getData();
//                if (Objects.nonNull(data)) {
//                    planList.forEach(item -> item.setName(data.getName()));
//                }
//            }
//        }
        try {
            execute(tenant.getCode(), planList, planEnum, tenant, date);
        } catch (Exception e) {
            log.error("execute error tenantCode: {}, planEnum: {}", tenant.getCode(), planEnum);
            e.printStackTrace();
        }

    }

    /**
     * 根据计划类型 执行任务
     * @param planEnum
     * @param date
     */
    public void execute(PlanEnum planEnum, LocalDateTime date) {

        List<Tenant> normalTenant = getAllNormalTenant();
        for (Tenant tenant : normalTenant) {


            NursingTaskExecuteParams params = new NursingTaskExecuteParams();
            params.setStartDate(date);
            params.setPlanEnum(planEnum);
            params.setTenantCode(tenant.getCode());
            params.setTaskType("nursingPlanPush");
            redisTemplate.opsForList().leftPush(NURSING_TASK_EXECUTE, params.toJSONString());
        }
    }

    /**
     * 医生学习计划的 推送逻辑
     * @param tenant
     * @param doctorPlans
     * @param date
     */
    public void doctorLearnPlanPush(Tenant tenant, List<Plan> doctorPlans, LocalDateTime date) {
        if (CollUtil.isEmpty(doctorPlans)) {
            return;
        }
        R<List<Doctor>> query = doctorApi.query(Doctor.builder().wxStatus(1).build());
        if (!query.getIsSuccess()) {
            return;
        }
        List<Doctor> doctorList = query.getData();
        if (CollUtil.isEmpty(doctorList)) {
            return;
        }
        int dateMinute = date.getMinute();
        int dateHour = date.getHour();
        for (Plan plan : doctorPlans) {
            // 查询护理计划的详细
            List<PlanDetailDTO> planDetailDTOS = planDetailService.findDetailWithTimeById(plan.getId());
            if (CollUtil.isEmpty(planDetailDTOS)) {
                continue;
            }

            for (PlanDetailDTO detailDTO : planDetailDTOS) {
                List<PlanDetailTime> detailTimes = detailDTO.getPlanDetailTimes();
                String detailDTOTemplateMessageId = detailDTO.getTemplateMessageId();
                if (CollUtil.isEmpty(detailTimes)) {
                    continue;
                }
                for (PlanDetailTime detailTime : detailTimes) {
                    LocalTime localTime = detailTime.getTime();
                    Integer detailType = detailDTO.getType();
                    if (detailType == null) {
                        continue;
                    }
                    boolean rightTime = dateHour == localTime.getHour() && dateMinute == localTime.getMinute();
                    if (!rightTime) {
                        continue;
                    }
                    Integer sendUrl = detailTime.getSendUrl();
                    String templateMessageId = detailTime.getTemplateMessageId();
                    if (StrUtil.isBlank(templateMessageId)) {
                        continue;
                    }
                    String cmsTitle = null;
                    ChannelContent channelContent = null;
                    if (sendUrl == null || 0 == sendUrl) {
                        Long msgId = Convert.toLong(templateMessageId);
                        if (msgId == null) {
                            continue;
                        }
                        R<ChannelContent> channelContentR = channelContentApi.getWithoutTenant(msgId);
                        channelContent = channelContentR.getData();
                        if (!channelContentR.getIsSuccess() || channelContent == null) {
                            continue;
                        }
                        cmsTitle = channelContent.getTitle();
                    } else {
                        cmsTitle = detailTime.getCmsTitle();
                    }
                    for (Doctor doctor : doctorList) {
                        LocalDateTime firstLoginTime = doctor.getFirstLoginTime();
                        Integer preTime = detailTime.getPreTime();
                        boolean whetherPush = PushAlgorithm.builder()
                                .completeEnterGroupTime(firstLoginTime)
                                .execute(plan.getExecute())
                                .preDays(preTime)
                                .frequency(0)
                                .effectiveTime(plan.getEffectiveTime())
                                .build().whetherPush();
                        if (whetherPush) {
                            boolean weiXinMessage;
                            String url = null;
                            ReminderLog reminderLog = null;
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.add(detailTime.getId().toString());

                            if (sendUrl == null || 0 == sendUrl) {
                                // 推送微信模板消息
                                reminderLog = getReminderLogService().createReminderLog(null, PlanEnum.LEARN_PLAN.getDesc(),
                                        JSONUtil.toJsonStr(jsonArray), 0, plan.getId(),
                                        doctor.getOrganId(), doctor.getClassCode(), doctor.getId(), doctor.getNursingId());

                                url = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(),
                                        Objects.nonNull(tenant.getWxBindTime()),
                                        H5Router.CMS_DETAIL,
                                        "id=" + Convert.toStr(channelContent.getId()) , "messageId=" +  reminderLog.getId());
                            } else {
                                // 推送的一遍链接
                                reminderLog = getReminderLogService().createReminderLog(null, PlanEnum.LEARN_PLAN.getDesc(),
                                        JSONUtil.toJsonStr(jsonArray), 0, plan.getId(),
                                        doctor.getOrganId(), doctor.getClassCode(), doctor.getId(), doctor.getNursingId());
                                try {
                                    url = ApplicationDomainUtil.externalLinksShowUrl(tenant.getDomainName(),
                                            Objects.nonNull(tenant.getWxBindTime()),
                                            "messageId=" + reminderLog.getId(), "cmsUrl=" + URLEncoder.encode(detailTime.getTemplateMessageId(), "utf-8"), "tenantCode=" + tenant.getCode() );
                                } catch (UnsupportedEncodingException e) {
                                    log.error(" create doctor learn plan cms link error {}  {} ", doctor.getId(),  reminderLog.getId());
                                    continue;
                                }
                            }
                            if (StrUtil.isEmpty(url)) {
                                continue;
                            }
                            if (tenant.isPersonalServiceNumber()) {
                                PlanCmsReminderLog planCmsReminderLog = new PlanCmsReminderLog();
                                planCmsReminderLog.setSendTime(LocalDateTime.now());
                                planCmsReminderLog.setCmsId(channelContent != null ? channelContent.getId() : null);
                                planCmsReminderLog.setCmsTitle(cmsTitle);
                                planCmsReminderLog.setCmsLink(url);
                                planCmsReminderLog.setUserId(doctor.getId());
                                planCmsReminderLog.setUserType(UserType.UCENTER_DOCTOR);
                                planCmsReminderLogService.submitSyncSave(planCmsReminderLog, tenant.getCode());
                                continue;
                            }
                            weiXinMessage = weixinSender.sendDoctorLearnPlanWeiXinMessage(tenant.getWxAppId(), doctor.getName(), plan.getName(),
                                    doctor.getOpenId(), detailDTOTemplateMessageId, cmsTitle, url);
                            if (weiXinMessage) {
                                getReminderLogService().sendSuccess(reminderLog.getId());
                                PlanCmsReminderLog planCmsReminderLog = new PlanCmsReminderLog();
                                planCmsReminderLog.setSendTime(LocalDateTime.now());
                                planCmsReminderLog.setCmsId(channelContent != null ? channelContent.getId() : null);
                                planCmsReminderLog.setCmsTitle(cmsTitle);
                                planCmsReminderLog.setCmsLink(url);
                                planCmsReminderLog.setUserId(doctor.getId());
                                planCmsReminderLog.setUserType(UserType.UCENTER_DOCTOR);
                                planCmsReminderLogService.submitSyncSave(planCmsReminderLog, tenant.getCode());
                            }

                        }
                    }
                }
            }
        }

    }

    /**
     * 清理掉 用户已经自定义 过的推送时间
     * @param patientId
     * @param planDetailTimes
     * @return
     */
    private List<PlanDetailTime> clearPlanDetailTime(Long patientId, Long planDetailId, List<PlanDetailTime> planDetailTimes) {

        if (CollectionUtils.isEmpty(planDetailTimes)) {
            return planDetailTimes;
        }

        LbqWrapper<PatientCustomPlanTime> wrapper = Wraps.<PatientCustomPlanTime>lbQ()
                .select(PatientCustomPlanTime::getNursingPlanDetailTimeId)
                .eq(PatientCustomPlanTime::getPatientId, patientId)
                .eq(PatientCustomPlanTime::getNursingPlanDetailId, planDetailId)
                .eq(PatientCustomPlanTime::getCustomizeStatus, 1);
        List<PatientCustomPlanTime> planTimes = patientCustomPlanTimeMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(planTimes)) {
            return planDetailTimes;
        }
        Map<Long, PlanDetailTime> detailTimeMap = planDetailTimes.stream().collect(Collectors.toMap(PlanDetailTime::getId, item -> item, (o1, o2) -> o2));
        for (PatientCustomPlanTime planTime : planTimes) {
            Long detailTimeId = planTime.getNursingPlanDetailTimeId();
            detailTimeMap.remove(detailTimeId);
        }
        return new ArrayList<>(detailTimeMap.values());
    }

    /**
     * @param nursingPlanPushParam
     * @return void
     * @Author yangShuai
     * @Description 发送护理计划， 学习计划除外
     * @Date 2020/10/27 15:48
     */
    private void sendNursingPlant(NursingPlanPushParam nursingPlanPushParam) {
        PlanEnum planEnum = nursingPlanPushParam.getPlanEnum();
        log.info("推送{}", planEnum.getDesc());
        Plan nursingPlan = nursingPlanPushParam.getNursingPlan();
        Integer execute = nursingPlan.getExecute();
        Tenant tenant = nursingPlanPushParam.getTenant();
        @Length(max = 20, message = "项目编码长度不能超过20") String tenantCode = tenant.getCode();
        int dateMinute = nursingPlanPushParam.getDate().getMinute();
        int dateHour = nursingPlanPushParam.getDate().getHour();
        // 使用患者的入组时间 或者 护理计划开始时间 作为患者触发时间的标准
        NursingPlanPatientBaseInfoDTO patient = nursingPlanPushParam.getPatient();
        LocalDateTime nursingTime = patient.getNursingTime();
        if (Objects.isNull(nursingTime)) {
            // 几率很低，暂时不做异步处理
            NursingPlanUpdatePatientDTO updatePatientDTO = new NursingPlanUpdatePatientDTO();
            updatePatientDTO.setId(patient.getId());
            updatePatientDTO.setCompleteEnterGroupTime(LocalDateTime.now());
            updatePatientDTO.setNursingTime(LocalDateTime.now());
            patientApi.updatePatientCompleteEnterGroupTime(updatePatientDTO);
        }
        Integer planType = nursingPlan.getPlanType();
        PlanEnum anEnum = PlanEnum.getPlanEnum(planType);
        for (PlanDetailDTO plantDetail : nursingPlanPushParam.getNursingPlantDetails()) {
            List<PlanDetailTime> planDetailTimes = plantDetail.getPlanDetailTimes();
            Long plantDetailId = plantDetail.getId();
            // 处理 推送时间， 将用户已经自定义的推送时间去掉。
            planDetailTimes = clearPlanDetailTime(patient.getId(), plantDetailId, planDetailTimes);
            if (CollectionUtils.isEmpty(planDetailTimes)) {
                continue;
            }

            for (PlanDetailTime detailTime : planDetailTimes) {
                Integer preDays = detailTime.getPreTime();
                if (preDays != null && preDays.equals(-9999)) {
                    continue;
                }
                boolean whetherPush = PushAlgorithm.builder()
                        .completeEnterGroupTime(nursingTime)
                        .execute(execute)
                        .preDays(preDays)
                        .frequency(detailTime.getFrequency())
                        .effectiveTime(nursingPlan.getEffectiveTime())
                        .build().whetherPush();
                if (whetherPush) {
                    LocalTime localTime = detailTime.getTime();
                    cn.hutool.json.JSONArray jsonArray = new cn.hutool.json.JSONArray();
                    jsonArray.add(detailTime.getId().toString());
                    // 不在验证是否达到时间。 在这里只生产推送任务
                    if (null == anEnum) {
                        getReminderLogService().createReminderLog(patient.getId(), "未知的护理计划类型",
                                JSONUtil.toJsonStr(jsonArray), 0, nursingPlan.getId(), patient.getOrganId(), patient.getClassCode(),
                                patient.getDoctorId(), patient.getServiceAdvisorId(), LocalDateTime.of(LocalDate.now(), localTime), plantDetailId);
                    } else {
                        getReminderLogService().createReminderLog(patient.getId(), anEnum.getDesc(),
                                JSONUtil.toJsonStr(jsonArray), 0, nursingPlan.getId(), patient.getOrganId(), patient.getClassCode(),
                                patient.getDoctorId(), patient.getServiceAdvisorId(), LocalDateTime.of(LocalDate.now(), localTime), plantDetailId);
                    }

                    // ~~~~~~~~~~~~~~~~ 后面的后通过发送任务去执行。生产任务这里不再执行
                }
            }
        }
    }


    /**
     * 学习计划 生产推送任务
     *
     * @return void
     * @Author yangShuai
     * @Description
     * @Date 2020/10/27 16:40
     */
    public void studyPlan(List<PlanDetailDTO> nursingPlantDetails, Plan nursingPlan, NursingPlanPatientBaseInfoDTO patient, Tenant tenant, LocalDateTime date) {
        LocalDateTime createTime = patient.getNursingTime();
        @Length(max = 20, message = "项目编码长度不能超过20") String tenantCode = tenant.getCode();
        R<TemplateMsgDto> templateMsgR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
        TemplateMsgDto templateMsgDto = templateMsgR.getData();
        Map<String, TemplateMsgDto> templateMsgDtoMap = new HashMap<>();
        NursingPlanPushParam nursingPlanPushParam = new NursingPlanPushParam();
        nursingPlanPushParam.setTemplateMsgDto(templateMsgDto);
        nursingPlanPushParam.setTemplateMsgDtoMap(templateMsgDtoMap);

        nursingPlantDetails.forEach((detail) -> {
            // 提前吧模版查询出来。
            List<PlanDetailTime> detailTimes = detail.getPlanDetailTimes();
            Long detailId = detail.getId();
            detailTimes = clearPlanDetailTime(patient.getId(), detailId, detailTimes);
            if (null != detailTimes && !detailTimes.isEmpty()) {
                for (PlanDetailTime line : detailTimes) {
                    Integer preTime = line.getPreTime();
                    LocalTime localTime = line.getTime();
                    Integer detailType = detail.getType();
                    if (detailType == null) {
                        continue;
                    }
                    // 学习计划 一个推送时间设置 都为单次
                    boolean whetherPush = PushAlgorithm.builder()
                            .completeEnterGroupTime(createTime)
                            .execute(nursingPlan.getExecute())
                            .preDays(preTime)
                            .frequency(0)
                            .effectiveTime(nursingPlan.getEffectiveTime())
                            .build().whetherPush();
                    if (whetherPush) {
                        cn.hutool.json.JSONArray jsonArray = new cn.hutool.json.JSONArray();
                        jsonArray.add(line.getId().toString());
                        getReminderLogService().createReminderLog(patient.getId(), PlanEnum.LEARN_PLAN.getDesc(),
                                JSONUtil.toJsonStr(jsonArray), PlanEnum.LEARN_PLAN.getCode(), nursingPlan.getId(),
                                patient.getOrganId(), patient.getClassCode(), patient.getDoctorId(), patient.getServiceAdvisorId(),
                                LocalDateTime.of(LocalDate.now(), localTime), detailId);
                    }
                }
            }
        });
    }


    // ~~~~~~~~~~~~~ 不需要调整的业务逻辑

    /**
     * 检查需要是否有 租户 在这个时间有推送记录需要推送
     *
     * @param localDateTime
     */
    public void checkReminderLog(LocalDateTime localDateTime) {
        localDateTime = localDateTime.withSecond(0).withNano(0);
        List<String> needSendLog = getReminderLogService().queryNeedSendLog(localDateTime);
        if (CollUtil.isEmpty(needSendLog)) {
            return;
        }

        for (String tenantCode : needSendLog) {
            NursingTaskExecuteParams params = new NursingTaskExecuteParams();
            params.setTenantCode(tenantCode);
            params.setStartDate(localDateTime);
            params.setTaskType("handleReminderLogPush");
            redisTemplate.opsForList().leftPush(NURSING_TASK_EXECUTE, params.toJSONString());
        }

    }

    /**
     * 查询租户下 等待执行的任务
     * 并进行推送
     * @param tenantCode
     * @param localDateTime
     */
    public void handleReminderLogPush(String tenantCode, LocalDateTime localDateTime) {

        BaseContextHandler.setTenant(tenantCode);
        IPage<ReminderLog> page = new Page<>();

        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (!tenant.getStatus().eq(TenantStatusEnum.NORMAL)) {
            return;
        }
        // 查询推送记录
        Map<String, Plan> planMap = new HashMap<>();
        Map<String, PlanDetail> planDetailDTOMap = new HashMap<>();
        Map<String, PlanDetailTime> planDetailTimeMap = new HashMap<>();
        // 服务工单推送模版
        R<TemplateMsgDto> categoryServiceWorkOrderMsg = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
        TemplateMsgDto templateMsgDto = categoryServiceWorkOrderMsg.getData();

        // 根据模版ID 存储模版。方便后续查找
        Map<String, TemplateMsgDto> templateMsgDtoMap = new HashMap<>();

        // 自定义监测计划模版推送开关
        Integer indicatorMonitoringStatus = getFunctionConfigurationService().getFunctionStatus(tenantCode, PlanFunctionTypeEnum.INDICATOR_MONITORING);

        // 查询 自定义随访 的状态是否开启
        Integer customFollowUpStatus = getFunctionConfigurationService().getFunctionStatus(tenantCode, PlanFunctionTypeEnum.CUSTOM_FOLLOW_UP);

        // 健康日志的菜单名称
        String healthCalendarName = null;
        R<com.caring.sass.tenant.entity.router.H5Router> h5RouterR = h5RouterApi.getH5Router("HEALTH_CALENDAR", UserType.PATIENT);
        if (h5RouterR.getIsSuccess()) {
            com.caring.sass.tenant.entity.router.H5Router data = h5RouterR.getData();
            if (Objects.nonNull(data)) {
                healthCalendarName = data.getName();
            }
        }


        try {
            while (true) {
                page.setCurrent(1);
                page.setSize(300);
                getReminderLogService().page(page, Wraps.<ReminderLog>lbQ()
                        .eq(ReminderLog::getStatus, -1)
                        .eq(ReminderLog::getWaitPushTime, localDateTime)
                        .orderByAsc(ReminderLog::getPatientId));

                List<ReminderLog> records = page.getRecords();
                if (records.isEmpty()) {
                    break;
                } else {
                    List<Long> ids = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
                    getReminderLogService().update(Wraps.<ReminderLog>lbU().in(SuperEntity::getId, ids)
                            .eq(ReminderLog::getStatus, -1)
                            .set(ReminderLog::getStatus, -4));
                }
                // 提取
                List<Long> patientIds = records.stream().map(ReminderLog::getPatientId).collect(Collectors.toList());
                R<List<NursingPlanPatientBaseInfoDTO>> baseInfoForNursingPlan = patientApi.getBaseInfoForNursingPlan(new NursingPlanPatientDTO(patientIds, tenantCode));
                List<NursingPlanPatientBaseInfoDTO> patientBaseInfoDTOS = null;
                if (baseInfoForNursingPlan.getIsSuccess()) {
                    patientBaseInfoDTOS = baseInfoForNursingPlan.getData();
                }
                if (patientBaseInfoDTOS == null) {
                    List<Long> ids = records.stream().map(ReminderLog::getId).collect(Collectors.toList());
                    getReminderLogService().update(Wraps.<ReminderLog>lbU()
                            .set(ReminderLog::getErrorMessage, "患者查询失败")
                            .set(ReminderLog::getStatus, -3)
                            .eq(SuperEntity::getId, ids));
                    continue;
                }
                Map<Long, NursingPlanPatientBaseInfoDTO> patientBaseInfoDTOMap = patientBaseInfoDTOS.stream()
                        .collect(Collectors.toMap(NursingPlanPatientBaseInfoDTO::getId, item -> item, (v1, v2) -> v1));

                for (ReminderLog record : records) {

                    try {
                        Long patientId = record.getPatientId();
                        Long planId = record.getPlanId();
                        Long planDetailId = record.getPlanDetailId();
                        String workId = record.getWorkId();

                        // 检查 护理计划是否还在开启状态
                        Plan plan = planMap.get(planId.toString());
                        if (plan == null) {
                            plan = getPlanService().getById(planId);
                            if (plan == null || plan.getStatus() == 0) {
                                updateReminderLogErrorMessage(record, "护理计划不存在或关闭");
                                continue;
                            }
                            // 如果是健康日志。 将健康日志的计划名称 同步菜单名称
                            if (PlanEnum.HEALTH_LOG.getCode().equals( plan.getPlanType())) {
                                if (healthCalendarName != null) {
                                    plan.setName(healthCalendarName);
                                }
                            }
                            planMap.put(planId.toString(), plan);
                        }
                        // 自定义监测 和 自定义随访需要判断模版消息开关是否依然开启
                        if (checkFollowUpStatus(plan, indicatorMonitoringStatus, customFollowUpStatus)) {
                            updateReminderLogErrorMessage(record, "功能开关关闭");
                            continue;
                        }

                        PlanDetail planDetail = planDetailDTOMap.get(planDetailId.toString());
                        if (planDetail == null) {
                            planDetail = planDetailService.getById(planDetailId);
                            if (planDetail == null) {
                                updateReminderLogErrorMessage(record, "护理计划详情不存在");
                                continue;
                            }
                            planDetailDTOMap.put(planDetailId.toString(), planDetail);
                        }
                        PlanDetailTime planDetailTime = planDetailTimeMap.get(workId);
                        if (planDetailTime == null) {
                            com.alibaba.fastjson.JSONArray objects = JSON.parseArray(workId);
                            if (objects.isEmpty()) {
                                updateReminderLogErrorMessage(record, "护理计划推送时间不存在");
                                continue;
                            }
                            String planDetailTimeId = objects.get(0).toString();
                            planDetailTime = planDetailTimeService.getById(Long.parseLong(planDetailTimeId));
                            if (planDetailTime == null) {
                                updateReminderLogErrorMessage(record, "护理计划推送时间不存在");
                                continue;
                            }
                            planDetailTimeMap.put(workId, planDetailTime);
                        }
                        // 查询患者信息
                        NursingPlanPatientBaseInfoDTO baseInfoDTO = patientBaseInfoDTOMap.get(patientId);
                        if (baseInfoDTO == null) {
                            updateReminderLogErrorMessage(record, "患者信息不存在");
                            continue;
                        }
                        // 查询模版信息
                        // 推送关联的计划。 指定要求的模版ID
                        String templateMessageId = null;
                        if (plan.getPlanType() != null && plan.getPlanType().equals(PlanEnum.LEARN_PLAN.getCode())) {
                            templateMessageId = planDetail.getTemplateMessageId();
                            if (templateMsgDtoMap.get(templateMessageId) == null) {
                                if (StrUtil.isNotBlank(templateMessageId)) {
                                    R<TemplateMsgDto> templateMs1gR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, Long.parseLong(templateMessageId), null);
                                    TemplateMsgDto msgRData = templateMs1gR.getData();
                                    if (Objects.nonNull(msgRData)) {
                                        templateMsgDtoMap.put(templateMessageId, msgRData);
                                    }
                                }
                            }
                        } else {
                            if (!StringUtils.isEmpty(planDetailTime.getTemplateMessageId())) {
                                // 推送微信模板消息
                                templateMessageId = planDetailTime.getTemplateMessageId();
                            } else if (!StringUtils.isEmpty(planDetail.getTemplateMessageId())) {
                                // 推送微信模板消息
                                templateMessageId = planDetail.getTemplateMessageId();
                            }
                            if (templateMessageId != null && StrUtil.isNotEmpty(templateMessageId)) {
                                if (templateMsgDtoMap.get(templateMessageId) == null) {
                                    R<TemplateMsgDto> templateMs1gR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, Long.parseLong(templateMessageId), null);
                                    TemplateMsgDto msgRData = templateMs1gR.getData();
                                    if (Objects.nonNull(msgRData)) {
                                        templateMsgDtoMap.put(templateMessageId, msgRData);
                                    }
                                }
                            }
                        }

                        // 组装推送任务参数

                        TemplateMsgDto useTemplateMsg = null;
                        if (templateMessageId != null) {
                            useTemplateMsg = templateMsgDtoMap.get(templateMessageId);
                        } else {
                            useTemplateMsg = templateMsgDto;
                        }
                        // 推送分为 护理计划推送 和 学习计划推送。
                        if (plan.getPlanType() != null && plan.getPlanType().equals(PlanEnum.LEARN_PLAN.getCode())) {

                            // 封装 学习计划推送消息。 提交给redis队列。
                            learnPlanPush(tenant, record, baseInfoDTO, plan, planDetailTime, useTemplateMsg, localDateTime);
                        } else {

                            nursingPlanPush(tenant, record, baseInfoDTO, plan, planDetail, planDetailTime, useTemplateMsg ,localDateTime);
                        }
                    } catch (Exception e) {
                        updateReminderLogErrorMessage(record, "推送逻辑异常");
                    }
                }
            }
        } catch (Exception e) {
            log.error("推送失败", e);
        }
    }


    /**
     * 封装 血压 血糖 健康日志， 复查提醒， 自定义监测 自定义护理计划 的推送消息。
     *
     * 提交给redis队列
     * @param tenant
     * @param record
     * @param baseInfoDTO
     * @param plan
     * @param planDetail
     * @param planDetailTime
     */
    public void nursingPlanPush(Tenant tenant,
                                ReminderLog record,
                                NursingPlanPatientBaseInfoDTO baseInfoDTO, Plan plan,
                                PlanDetail planDetail,
                                PlanDetailTime planDetailTime,
                                TemplateMsgDto templateMsgDto,
                                LocalDateTime localDateTime) {

        String url = null;
        boolean cmsLink = false;
        // 检查提前打卡： 验证患者是否已经对 detailTime.getId() 已经完成打卡
        if (getReminderLogService().checkReminderLogExits(tenant.getCode(), planDetailTime.getId(), null, baseInfoDTO.getId())) {
            return;
        }
        if (planDetail.getPushType() != null && planDetail.getPushType().equals(2)) {

            // 此处说明，推送的是 一个模板，模板链接是文章
            cmsLink = true;
            if (StringUtils.isNotEmpty(planDetail.getContent())) {
                url = planDetail.getContent();
            }
        }
        PlanEnum planEnum = PlanEnum.getPlanEnum(plan.getPlanType());
        try {
            String errorMessage = weixinSender.sendWeiXinMessage(tenant, cmsLink,
                    plan.getFollowUpPlanType(), plan.getId(),
                    baseInfoDTO,
                    planEnum,
                    null, url, plan.getName(), localDateTime,
                    plan.getRemindTemplateTitle(), null, templateMsgDto, record);
            if (errorMessage != null) {
                updateReminderLogErrorMessage(record, errorMessage);
            } else {
                updateReminderLogRedisPushSuccess(record);
            }
        } catch (Exception e) {
            updateReminderLogErrorMessage(record, "推送异常");
            e.printStackTrace();
        }
        if (cmsLink && StrUtil.isNotEmpty(url)) {
            PlanCmsReminderLog planCmsReminderLog = new PlanCmsReminderLog();
            planCmsReminderLog.setCmsLink(url);
            planCmsReminderLog.setSendTime(LocalDateTime.now());
            planCmsReminderLog.setUserId(baseInfoDTO.getId());
            planCmsReminderLogService.submitSyncCmsLinkSave(planCmsReminderLog, tenant.getCode());
        }


    }

    /**
     * 封装 学习计划推送消息。 提交给redis队列。
     * 由微信服务 去处理。
     *
     * @param tenant
     * @param record
     * @param baseInfoDTO
     * @param plan
     * @param planDetailTime
     */
    public void learnPlanPush(Tenant tenant,
                              ReminderLog record,
                              NursingPlanPatientBaseInfoDTO baseInfoDTO,
                              Plan plan,
                              PlanDetailTime planDetailTime,
                              TemplateMsgDto templateMsgDto,
                              LocalDateTime localDateTime) {

        String templateMessageId = planDetailTime.getTemplateMessageId();
        if (StrUtil.isBlank(templateMessageId)) {
            updateReminderLogErrorMessage(record, "学习计划没有配置学习资料");
            return;
        }
        String url = null;
        ChannelContent channelContent = null;
        Integer sendUrl = planDetailTime.getSendUrl();
        String cmsTitle = null;
        // 检查提前打卡： 发现要推送文章，提前检查 workId 对应此患者是否已经存在
        if (getReminderLogService().checkReminderLogExits(tenant.getCode(), planDetailTime.getId(), null, baseInfoDTO.getId())) {
            return;
        }
        if (sendUrl == null || 0 == sendUrl) {
            // { 代码待完善 }
            long cmsId = Long.parseLong(templateMessageId);
            R<ChannelContent> channelContentR = channelContentApi.getWithoutTenant(cmsId);
            channelContent = channelContentR.getData();
            if (channelContentR.getIsSuccess() && channelContent != null) {
                // 推送微信模板消息
                cmsTitle = channelContent.getTitle();
                url = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CMS_DETAIL,
                        "id=" + Convert.toStr(channelContent.getId()) , "messageId=" + record.getId());
                String errorMessage = weixinSender.sendLearnPlanWeiXinMessage(tenant,
                        baseInfoDTO, cmsTitle, plan.getName(), url, localDateTime, plan.getId(), templateMsgDto, record.getId());
                if (errorMessage != null) {
                    // 记录异常原因
                    updateReminderLogErrorMessage(record, errorMessage);
                } else {
                    // 设置提交消息队列成功
                    updateReminderLogRedisPushSuccess(record);
                }
            }
        } else {
            // 推送的一个链接
            try {
                url = ApplicationDomainUtil.externalLinksShowUrl(tenant.getDomainName(),
                        Objects.nonNull(tenant.getWxBindTime()),
                        "messageId=" + record.getId(), "cmsUrl=" + URLEncoder.encode(planDetailTime.getTemplateMessageId(), "utf-8"),  "tenantCode=" + tenant.getCode() );
            } catch (UnsupportedEncodingException e) {
                updateReminderLogErrorMessage(record, "学习计划链接异常");
                return;
            }
            if (StrUtil.isEmpty(url)) {
                updateReminderLogErrorMessage(record, "学习计划链接不存在");
                return;
            }
            cmsTitle = planDetailTime.getCmsTitle();
            String errorMessage = weixinSender.sendLearnPlanWeiXinMessage(tenant,
                    baseInfoDTO,  cmsTitle, plan.getName(), url, localDateTime,  plan.getId(), templateMsgDto, record.getId());
            if (errorMessage != null) {

                // 记录异常原因
                updateReminderLogErrorMessage(record, errorMessage);
            } else {

                // 设置提交消息队列成功
                updateReminderLogRedisPushSuccess(record);
            }
        }
        PlanCmsReminderLog planCmsReminderLog = new PlanCmsReminderLog();
        planCmsReminderLog.setCmsId(channelContent != null ? channelContent.getId() : null);
        planCmsReminderLog.setSendTime(LocalDateTime.now());
        planCmsReminderLog.setCmsTitle(cmsTitle);
        planCmsReminderLog.setIcon(channelContent != null ? channelContent.getIcon() : null);
        planCmsReminderLog.setCmsLink(url);
        planCmsReminderLog.setUserId(baseInfoDTO.getId());
        planCmsReminderLog.setUserType(UserType.UCENTER_PATIENT);
        planCmsReminderLogService.submitSyncSave(planCmsReminderLog, tenant.getCode());

    }

    /**
     * 推送推荐记录 提交消息队列 正常
     * @param record
     */
    public void updateReminderLogRedisPushSuccess(ReminderLog record) {

        record.setStatus(-2);
        getReminderLogService().updateById(record);

    }

    /**
     * 设置推送记录的异常消息
     * @param record
     * @param errorMessage
     */
    public void updateReminderLogErrorMessage(ReminderLog record, String errorMessage) {
        record.setErrorMessage(errorMessage);
        record.setStatus(-3);
        getReminderLogService().updateById(record);
    }

    /**
     * 检查状态是否不需要推送
     * @param plan
     * @param indicatorMonitoringStatus
     * @param customFollowUpStatus
     * @return true 不需要推送
     */
    public boolean checkFollowUpStatus(Plan plan, Integer indicatorMonitoringStatus, Integer customFollowUpStatus) {

        Integer planType = plan.getPlanType();
        String followUpPlanType = plan.getFollowUpPlanType();
        if (FollowUpPlanTypeEnum.MONITORING_DATA.operateType.equals(followUpPlanType)) {
            if (planType == null || planType.equals(1) || planType.equals(2)) {
                if (indicatorMonitoringStatus == 0) {
                    return true;
                }
            }
        }
        if (FollowUpPlanTypeEnum.CARE_PLAN.operateType.equals(followUpPlanType)) {
            if (planType == null); {
                if (customFollowUpStatus == 0) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * 自定义护理计划
     * @param date
     */
    public void customNursingPlanExecute(LocalDateTime date) {
        date = date.withSecond(0).withNano(0);
        LocalDateTime now = date;
        List<Tenant> tenantList = getAllNormalTenant();
        if (CollUtil.isEmpty(tenantList)) {
            return;
        }
        for (Tenant tenant : tenantList) {

            NursingTaskExecuteParams params = new NursingTaskExecuteParams();
            params.setTenantCode(tenant.getCode());
            params.setStartDate(now);
            params.setTaskType("customNursingPlanExecute");
            redisTemplate.opsForList().leftPush(NURSING_TASK_EXECUTE, params.toJSONString());
        }
    }


    /**
     * 查询数据库中到达推送时间的推送任务。
     * @param now
     * @param nextDate
     * @return
     */
    private List<PatientCustomPlanTime> getCustomPlanCanSend(LocalDateTime now ,  LocalDateTime nextDate) {
        LbqWrapper<PatientCustomPlanTime> wrapper = Wraps.<PatientCustomPlanTime>lbQ().select(SuperEntity::getId,
                        PatientCustomPlanTime::getPatientId,
                        PatientCustomPlanTime::getNursingPlantId,
                        PatientCustomPlanTime::getNursingPlanDetailId,
                        PatientCustomPlanTime::getNursingPlanDetailTimeId,
                        PatientCustomPlanTime::getNextRemindTime,
                        PatientCustomPlanTime::getRemindTimeOut,
                        PatientCustomPlanTime::getPlanType,
                        PatientCustomPlanTime::getFrequency)
                .eq(PatientCustomPlanTime::getCustomizeStatus, 1)
                .eq(PatientCustomPlanTime::getFrequency, 0)
                .ge(PatientCustomPlanTime::getNextRemindTime, now)
                .lt(PatientCustomPlanTime::getNextRemindTime, nextDate);
        return patientCustomPlanTimeMapper.selectList(wrapper);
    }


    /**
     * 患者的注射记录。填写超时。
     * 查询超时时间在当前的 自定义提醒。
     * @param date
     */
    public void injectionFormTimeOutRemind(LocalDateTime date) {
        date = date.withSecond(0).withNano(0);
        LocalDateTime now = date;
        List<Tenant> tenantList = getAllNormalTenant();
        if (CollUtil.isEmpty(tenantList)) {
            return;
        }
        for (Tenant tenant : tenantList) {

            NursingTaskExecuteParams params = new NursingTaskExecuteParams();
            params.setTaskType("injectionFormTimeOutRemind");
            params.setStartDate(now);
            params.setTenantCode(tenant.getCode());
            redisTemplate.opsForList().leftPush(NURSING_TASK_EXECUTE, params.toJSONString());

        }

    }

    /**
     * 患者自定义护理计划处理
     * @param tenantCode
     * @param date
     */
    public void customNursingPlanExecute(String tenantCode, LocalDateTime date) {
        LocalDateTime now = date;
        LocalDateTime nextDate = date.plusMinutes(15);
        try {
            R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
            Boolean success = tenantR.getIsSuccess();
            if (!success) {
                return;
            }
            Tenant tenant = tenantR.getData();
            if (Objects.isNull(tenant)) {
                return;
            }
            BaseContextHandler.setTenant(tenantCode);
            List<PatientCustomPlanTime> customPlanTimes = getCustomPlanCanSend(now, nextDate);
            if (CollectionUtils.isNotEmpty(customPlanTimes)) {
                customPlanCanSendWeiXin(tenant, customPlanTimes, date, false);
            }
        } catch (Exception e) {
            log.error("customNursingPlanExecute pull error tenantCode: {}", tenantCode);
            e.printStackTrace();
        }
    }


    /**
     * 患者的注射记录。填写超时。
     * 查询超时时间在当前的 自定义提醒。
     * @param tenantCode
     * @param data
     */
    public void injectionFormTimeOutRemind(String tenantCode, LocalDateTime data) {

        LocalDateTime now = data;
        LocalDateTime nextDate = data.plusMinutes(15);
        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        if (!tenantR.getIsSuccess()) {
            return;
        }
        Tenant tenant = tenantR.getData();
        if (Objects.isNull(tenant)) {
            return;
        }
        LbqWrapper<PatientCustomPlanTime> wrapper = Wraps.<PatientCustomPlanTime>lbQ().select(SuperEntity::getId,
                        PatientCustomPlanTime::getPatientId,
                        PatientCustomPlanTime::getNursingPlantId,
                        PatientCustomPlanTime::getNursingPlanDetailId,
                        PatientCustomPlanTime::getNursingPlanDetailTimeId,
                        PatientCustomPlanTime::getNextRemindTime,
                        PatientCustomPlanTime::getRemindTimeOut,
                        PatientCustomPlanTime::getPlanType,
                        PatientCustomPlanTime::getFrequency)
                .ge(PatientCustomPlanTime::getRemindTimeOut, now)
                .lt(PatientCustomPlanTime::getRemindTimeOut, nextDate);
        List<PatientCustomPlanTime> customPlanTimes = patientCustomPlanTimeMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(customPlanTimes)) {
            customPlanCanSendWeiXin(tenant, customPlanTimes, now,true);
        }
    }

    /**
     * 推送 自定义护理计划
     * 每次只查询达到推送时间的任务。进行推送。
     *
     * @param tenant
     * @param customPlanTimes
     */
    private void customPlanCanSendWeiXin(Tenant tenant, List<PatientCustomPlanTime> customPlanTimes, LocalDateTime pushTime, Boolean isTimeOutRemind) {

        if (CollectionUtils.isEmpty(customPlanTimes) || Objects.isNull(tenant)) {
            return;
        }
        BaseContextHandler.setTenant(tenant.getCode());
        Set<Long> patientIds = new HashSet<>(customPlanTimes.size());
        Set<Long> planDetailTimeSetIds = new HashSet<>(customPlanTimes.size());
        Set<Long> planDetailSetIds = new HashSet<>(customPlanTimes.size());
        Set<Long> planIds = new HashSet<>(customPlanTimes.size());

        customPlanTimes.forEach(item -> {
            if (item.getPatientId() != null && item.getNursingPlanDetailId() != null && item.getNursingPlanDetailTimeId() != null) {
                patientIds.add(item.getPatientId());
                planIds.add(item.getNursingPlantId());
                planDetailTimeSetIds.add(item.getNursingPlanDetailTimeId());
                planDetailSetIds.add(item.getNursingPlanDetailId());
            }
        });
        if (CollectionUtils.isEmpty(patientIds)) {
            return;
        }
        if (CollectionUtils.isEmpty(planDetailTimeSetIds)) {
            return;
        }
        if (CollectionUtils.isEmpty(planDetailSetIds)) {
            return;
        }
        NursingPlanPatientDTO planPatientDTO = new NursingPlanPatientDTO(new ArrayList<>(patientIds), tenant.getCode());
        R<List<NursingPlanPatientBaseInfoDTO>> baseInfoForNursingPlan = patientApi.getBaseInfoForNursingPlan(planPatientDTO);

        Map<Long, NursingPlanPatientBaseInfoDTO> patientMap = new HashMap<>(patientIds.size());
        if (baseInfoForNursingPlan.getIsSuccess().equals(true)) {
            List<NursingPlanPatientBaseInfoDTO> baseInfoDTOS = baseInfoForNursingPlan.getData();
            if (CollectionUtils.isNotEmpty(baseInfoDTOS)) {
                for (NursingPlanPatientBaseInfoDTO infoDTO : baseInfoDTOS) {
                    patientMap.put(infoDTO.getId(), infoDTO);
                }
            }
        }

        List<Long> planDetailTimeIds = new ArrayList<>(planDetailTimeSetIds);

        List<Long> planDetailIds = new ArrayList<>(planDetailSetIds);

        List<Plan> plans = getPlanService().list(Wraps.<Plan>lbQ()
                .select(SuperEntity::getId, Plan::getName, Plan::getPlanModel, Plan::getTimeoutRecovery, Plan::getTimeOutRemind, Plan::getTimeOutPlanId, Plan::getFollowUpPlanType,
                        Plan::getRemindTemplateTitle, Plan::getTimeOutRemindTemplateTitle, Plan::getTimeOutRecoveryRemindTemplateTitle)
                .in(SuperEntity::getId, planIds));

        if (CollUtil.isEmpty(plans)) {
            return;
        }

        List<PlanDetailTime> detailTimes = planDetailTimeService.list(Wraps.<PlanDetailTime>lbQ().select(PlanDetailTime::getTemplateMessageId,
                        SuperEntity::getId)
                .in(PlanDetailTime::getId, planDetailTimeIds));
        if (CollectionUtils.isEmpty(detailTimes)) {
            return;
        }

        List<PlanDetail> planDetails = planDetailService.list(Wraps.<PlanDetail>lbQ().select(SuperEntity::getId,PlanDetail::getContent, PlanDetail::getNursingPlanId,
                        PlanDetail::getPushType, PlanDetail::getType, PlanDetail::getTemplateMessageId)
                .in(PlanDetail::getId, planDetailIds));
        if (CollectionUtils.isEmpty(planDetails)) {
            return;
        }

        Map<Long, PlanDetailTime> detailTimeMap = detailTimes.stream().collect(Collectors.toMap(PlanDetailTime::getId, item -> item));
        Map<Long, PlanDetail> detailMap = planDetails.stream().collect(Collectors.toMap(PlanDetail::getId, item -> item));
        Map<Long, Plan> planMap = plans.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
        String tenantCode = tenant.getCode();
        Map<String, TemplateMsgDto> templateMsgMap = new HashMap<>();
        R<TemplateMsgDto> templateMsgR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
        TemplateMsgDto msgRData = templateMsgR.getData();
        for (PlanDetail planDetail : planDetails) {
            String templateMessageId = planDetail.getTemplateMessageId();
            if (StringUtils.isNotBlank(templateMessageId)) {
                R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, Long.parseLong(templateMessageId), null);
                TemplateMsgDto templateMsgDto = templateMsgDtoR.getData();
                if (Objects.nonNull(templateMsgDto)) {
                    templateMsgMap.put(templateMessageId, templateMsgDto);
                }
            }
        }
        for (PlanDetailTime detailTime : detailTimes) {
            String templateMessageId = detailTime.getTemplateMessageId();
            if (StringUtils.isNotBlank(templateMessageId) && templateMsgMap.get(templateMessageId) == null) {
                R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, Long.parseLong(templateMessageId), null);
                TemplateMsgDto templateMsgDto = templateMsgDtoR.getData();
                if (Objects.nonNull(templateMsgDto)) {
                    templateMsgMap.put(templateMessageId, templateMsgDto);
                }
            }
        }

        NursingPlanPushParam nursingPlanPushParam = new NursingPlanPushParam();
        nursingPlanPushParam.setTemplateMsgDto(msgRData);
        nursingPlanPushParam.setTemplateMsgDtoMap(templateMsgMap);


        NursingPlanPatientBaseInfoDTO baseInfoDTO;
        PlanDetail planDetail;
        PlanDetailTime detailTime;
        Plan plan;
        for (PatientCustomPlanTime customPlanTime : customPlanTimes) {
            baseInfoDTO = patientMap.get(customPlanTime.getPatientId());
            planDetail = detailMap.get(customPlanTime.getNursingPlanDetailId());
            detailTime = detailTimeMap.get(customPlanTime.getNursingPlanDetailTimeId());
            plan = planMap.get(customPlanTime.getNursingPlantId());
            if (Objects.isNull(plan) || Objects.isNull(planDetail) || Objects.isNull(detailTime)) {
                continue;
            }
            @Length(max = 100, message = "名称长度不能超过100") String planName = plan.getName();

            String remindTemplateTitle = plan.getRemindTemplateTitle();
            String timeOutRemindTemplateTitle = plan.getTimeOutRemindTemplateTitle();
            String timeOutRecoveryRemindTemplateTitle = plan.getTimeOutRecoveryRemindTemplateTitle();
            String customServiceName = null;
            Long timeOutPlanId = null;
            if (isTimeOutRemind) {
                customServiceName = timeOutRemindTemplateTitle;
                timeOutPlanId = plan.getTimeOutPlanId();
            } else if (customPlanTime.getNextRemindTime().isAfter(customPlanTime.getRemindTimeOut()) && Objects.nonNull(customPlanTime.getRemindTimeOut())) {
                // 当超时恢复提醒 推送的时候。设置这次提醒的超时详情时间
                customServiceName = timeOutRecoveryRemindTemplateTitle;

                // 再次设置 随访推送的超时提醒
                customPlanTime.setRemindTimeOut(pushTime.plusDays(plan.getTimeOutRemind()));
            } else {
                customServiceName = remindTemplateTitle;
            }
            try {
                customPlanSendWeiXin(tenant, baseInfoDTO, planDetail, detailTime, customPlanTime.getPlanType(), planName, pushTime,
                        customServiceName, timeOutPlanId, plan.getFollowUpPlanType(), nursingPlanPushParam);
            } catch (Exception e) {
                log.error("customPlanSendWeiXin pull error: tenantCode: {}, patientId: {}, timeId: {}", tenant.getCode(), baseInfoDTO.getId(), detailTime.getId());
                e.printStackTrace();
            }

            // 是超时提醒。 则推送后 要判断是否要在间隔几天后恢复推送。
            if (isTimeOutRemind && plan.getTimeoutRecovery() != null && plan.getTimeoutRecovery() > 0) {
                // 超时 恢复提醒的时间
                LocalDateTime dateTime = pushTime.plusDays(plan.getTimeoutRecovery());
                customPlanTime.setNextRemindTime(dateTime);
                customPlanTime.setCustomizeStatus(1);
            }
            patientCustomPlanTimeMapper.updateById(customPlanTime);
        }

    }


    /**
     * 患者自定义护理计划的推送时间
     * @param tenant
     * @param baseInfoDTO
     * @param planDetail
     * @param detailTime
     * @param planType
     * @return
     */
    private boolean customPlanSendWeiXin(Tenant tenant, NursingPlanPatientBaseInfoDTO baseInfoDTO, PlanDetail planDetail,
                                         PlanDetailTime detailTime, Integer planType, String planName, LocalDateTime pushTime,
                                         String customServiceName, Long timeOutPlanId, String followUpPlanType, NursingPlanPushParam nursingPlanPushParam) {
        Integer type = planDetail.getType();
        Integer pushType = planDetail.getPushType();
        if (type == null) {
            return false;
        }
        String url = null;
        boolean cmsLink = false;
        if (pushType != null && pushType.equals(2)) {
            // 此处说明，推送的是 一个模板，模板链接是文章
            cmsLink = true;
            if (StringUtils.isNotEmpty(planDetail.getContent().trim())) {
                url = planDetail.getContent();
            }
        }
        String templateMessageId = null;
        if (!StringUtils.isEmpty(detailTime.getTemplateMessageId())) {
            // 推送微信模板消息
            templateMessageId = detailTime.getTemplateMessageId();
        } else if (!StringUtils.isEmpty(planDetail.getTemplateMessageId())) {
            // 推送微信模板消息
            templateMessageId = planDetail.getTemplateMessageId();
        }
        String customLink = null;

        // 如果是超时提醒。需要检查这个超时提醒是否要跳转某计划表单。
        PlanEnum planEnum = PlanEnum.getPlanEnum(planType);
        if (Objects.nonNull(timeOutPlanId) && timeOutPlanId > 0) {
            customLink = H5Router.timeOutFormAddUrl();
            customLink = ApplicationDomainUtil.wxPatientBaseDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime())) + "/" + String.format(customLink, timeOutPlanId);
        }
        TemplateMsgDto templateMsgDto;
        if (templateMessageId == null) {
            templateMsgDto = nursingPlanPushParam.getTemplateMsgDto();
        } else {
            Map<String, TemplateMsgDto> templateMsgDtoMap = nursingPlanPushParam.getTemplateMsgDtoMap();
            templateMsgDto = templateMsgDtoMap.get(templateMessageId);
        }

        ReminderLog reminderLog = weixinSender.createReminderLog(baseInfoDTO, planEnum, detailTime.getId().toString(), planDetail.getNursingPlanId());
        String errorMessage = weixinSender.sendWeiXinMessage(tenant,
                cmsLink,
                followUpPlanType,
                planDetail.getNursingPlanId(),
                baseInfoDTO,
                planEnum,
                null,
                url,
                planName,
                pushTime,
                customServiceName,
                customLink,
                templateMsgDto,
                reminderLog);
        if (errorMessage == null) {
            updateReminderLogRedisPushSuccess(reminderLog);
        } else {
            updateReminderLogErrorMessage(reminderLog, errorMessage);
        }
        if (cmsLink && StrUtil.isNotEmpty(url)) {
            PlanCmsReminderLog planCmsReminderLog = new PlanCmsReminderLog();
            planCmsReminderLog.setCmsLink(url);
            planCmsReminderLog.setSendTime(LocalDateTime.now());
            planCmsReminderLog.setUserId(baseInfoDTO.getId());
            planCmsReminderLogService.submitSyncCmsLinkSave(planCmsReminderLog, tenant.getCode());
        }
        return true;
    }





















}
