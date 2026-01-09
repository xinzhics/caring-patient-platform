package com.caring.sass.nursing.service.task.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.drugs.PatientDrugsMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeSettingMapper;
import com.caring.sass.nursing.dao.plan.PatientNursingPlanMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.entity.drugs.PatientDayDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTime;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;
import com.caring.sass.nursing.entity.plan.PatientNursingPlan;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.drugs.PatientDayDrugsService;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import com.caring.sass.nursing.service.task.SuperTask;
import com.caring.sass.nursing.service.task.TemplateMessageHelper;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.nursing.util.PatientDrugsNextReminderDateUtil;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 推送用药提醒
 *
 * @author leizhi
 */
@Slf4j
@Service
public class PushDrugsTask extends SuperTask {

    private final PatientDrugsTimeMapper patientDrugsTimeMapper;

    private final PatientDayDrugsService patientDayDrugsService;

    private final PatientDrugsMapper patientDrugsMapper;

    private final WeiXinApi weiXinApi;

    private final ReminderLogService reminderLogService;

    private final PatientApi patientApi;

    private final PlanMapper planMapper;

    private final PatientNursingPlanMapper patientNursingPlanMapper;

    private final PatientDrugsTimeSettingMapper patientDrugsTimeSettingMapper;

    private final MsgPatientSystemMessageApi patientSystemMessageApi;

    FunctionConfigurationService functionConfigurationService;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PUSH_DRUGS_TASK_KEY = "PUSH_DRUGS_TASK_KEY";
    NamedThreadFactory threadFactory = new NamedThreadFactory("push-drugs-", false);
    private final AtomicInteger maximumPoolSize = new AtomicInteger(0);
    private final ExecutorService executor = new ThreadPoolExecutor(1, 4, 0L,
            TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(180),
            threadFactory);



    public PushDrugsTask(PatientDrugsTimeMapper patientDrugsTimeMapper,
                         PatientDayDrugsService patientDayDrugsService,
                         WeiXinApi weiXinApi,
                         ReminderLogService reminderLogService,
                         PlanMapper planMapper,
                         PatientNursingPlanMapper patientNursingPlanMapper,
                         PatientDrugsMapper patientDrugsMapper,
                         PatientDrugsTimeSettingMapper patientDrugsTimeSettingMapper,
                         MsgPatientSystemMessageApi patientSystemMessageApi,
                         RedisTemplate<String, String> redisTemplate,
                         PatientApi patientApi) {
        this.patientDrugsTimeMapper = patientDrugsTimeMapper;
        this.patientDayDrugsService = patientDayDrugsService;
        this.weiXinApi = weiXinApi;
        this.reminderLogService = reminderLogService;
        this.patientApi = patientApi;
        this.patientNursingPlanMapper = patientNursingPlanMapper;
        this.patientDrugsMapper = patientDrugsMapper;
        this.planMapper = planMapper;
        this.patientDrugsTimeSettingMapper = patientDrugsTimeSettingMapper;
        this.patientSystemMessageApi = patientSystemMessageApi;
        this.redisTemplate = redisTemplate;
    }


    public FunctionConfigurationService getFunctionConfigurationService() {
        if (functionConfigurationService == null) {
            functionConfigurationService = SpringUtils.getBean(FunctionConfigurationService.class);
        }
        return functionConfigurationService;
    }

    /**
     * 随访计划处理中心.
     * 实时从redis中获取到租户，然后处理。
     * 定时任务触发。 每分钟调用一次
     */
    public void handleMessage() {
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent("push_drugs_task_handle_message_if_absent", "1", 5, TimeUnit.MINUTES);
        if (lockAcquired == null || !lockAcquired) {
            log.warn("Another instance is running this task.");
            return;
        }
        try {
            while (true) {
                if (maximumPoolSize.get() >= 175) {
                    break;
                }
                try {
                    String rightPop = redisTemplate.opsForList().rightPop(PUSH_DRUGS_TASK_KEY);
                    if (StrUtil.isNotEmpty(rightPop)) {
                        // 处理此项目的消息
                        maximumPoolSize.incrementAndGet();
                        executor.execute(() -> handleMessage(rightPop));
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    log.error("Error push drug while handling message", e);
                }
            }
        } finally {
            redisTemplate.delete("push_drugs_task_handle_message_if_absent");
        }


    }


    /**
     * 处理项目的消息
     * @param message
     */
    public void handleMessage(String message) {
        try {
            log.error("pushDrugsTask handleMessage:{}", message);
            PushDrugsTaskMessageContent messageContent = new PushDrugsTaskMessageContent();
            messageContent = messageContent.fromJSONString(message);
            String tenantCode = messageContent.getTenantCode();
            LocalDateTime date = messageContent.getStartTime();
            LocalDateTime addDate = messageContent.getEndTime();
            R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
            Tenant tenant = tenantR.getData();
            if (Objects.isNull(tenant)) {
                return;
            }
            String wxAppId = tenant.getWxAppId();
            if (StrUtil.isEmpty(wxAppId)) {
                return;
            }
            BaseContextHandler.setTenant(tenantCode);

            LbqWrapper<PatientDrugs> lbqWrapper = new LbqWrapper<>();
            lbqWrapper.ge(PatientDrugs::getNextReminderDate, date);
            lbqWrapper.eq(PatientDrugs::getStatus, 0);
            lbqWrapper.lt(PatientDrugs::getNextReminderDate, addDate);
            List<PatientDrugs> patientDrugs = patientDrugsMapper.selectList(lbqWrapper);
            if (CollectionUtils.isEmpty(patientDrugs)) {
                return;
            }
            Map<Long, List<PatientDrugs>> patientDrugsTimeMap = patientDrugs.stream().collect(Collectors.groupingBy(PatientDrugs::getPatientId));
            Set<Long> patientIds = patientDrugs.stream().map(PatientDrugs::getPatientId).collect(Collectors.toSet());
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
            Plan plan = planMapper.selectOne(Wraps.<Plan>lbQ().eq(Plan::getPlanType, 4));
            // 查询患者 对 护理计划的订阅状态
            LbqWrapper<PatientNursingPlan> planLbqWrapper = Wraps.<PatientNursingPlan>lbQ()
                    .eq(PatientNursingPlan::getNursingPlantId, plan.getId())
                    .in(PatientNursingPlan::getPatientId, patientIds)
                    .select(SuperEntity::getId, PatientNursingPlan::getPatientId,
                            PatientNursingPlan::getNursingPlantId , PatientNursingPlan::getIsSubscribe);
            List<PatientNursingPlan> nursingPlans = patientNursingPlanMapper.selectList(planLbqWrapper);
            Map<Long, Integer> planSubscribeStatus = new HashMap<>();
            if (CollUtil.isNotEmpty(nursingPlans)) {
                planSubscribeStatus = nursingPlans.stream()
                        .collect(Collectors.toMap(PatientNursingPlan::getPatientId, PatientNursingPlan::getIsSubscribe));
            }
            Set<Map.Entry<Long, List<PatientDrugs>>> entries = patientDrugsTimeMap.entrySet();



            // 调整模版。 将模版操作。增加redis缓存
            TemplateMsgDto templateMsgDto = null;
            if (tenant.isCertificationServiceNumber()) {
                templateMsgDto = TemplateMessageHelper.getTemplateMsgDto(TemplateMessageIndefiner.MEDICINE_TAKE_ALERT_TEMPLATE_MESSAGE);
            }
            Integer functionStatus = getFunctionConfigurationService().getFunctionStatus(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.MEDICATION);


            for (Map.Entry<Long, List<PatientDrugs>> entry : entries) {
                Long patientId = entry.getKey();
                NursingPlanPatientBaseInfoDTO patientBaseInfo = patientMap.get(patientId);
                if (Objects.isNull(patientBaseInfo)) {
                    return;
                }
                PatientDayDrugs patientDayDrugs = patientDayDrugsService.createPatientDayDrugs(patientBaseInfo);
                List<PatientDrugs> value = entry.getValue();
                JSONArray jsonArray = new JSONArray();
                List<PatientDrugsTime> patientDrugsTimes = new ArrayList<>();
                for (PatientDrugs drugs : value) {
                    // 是否需要给 患者推送打卡提醒
                    Integer needPush = planSubscribeStatus.get(drugs.getPatientId());
                    PatientDrugsTime patientDrugsTime = PatientDrugsTime
                            .builder()
                            .drugsTime(date)
                            .drugsId(drugs.getDrugsId())
                            .patientDrugsId(drugs.getId())
                            .status(2)
                            .drugsDose(drugs.getDose())
                            .patientId(drugs.getPatientId())
                            .medicineIcon(drugs.getMedicineIcon())
                            .medicineName(drugs.getMedicineName())
                            .unit(drugs.getUnit()).build();
                    if (needPush != null && needPush == 1) {
                        patientDrugsTime.setNeedPush(1);
                    } else {
                        patientDrugsTime.setNeedPush(0);
                    }
                    if (patientDayDrugs.getCheckinedNumber() == 0) {
                        patientDayDrugs.setStatus(0);
                    } else {
                        patientDayDrugs.setStatus(1);
                    }
                    patientDrugsTimes.add(patientDrugsTime);

                    // 计算下次推送时间
                    List<PatientDrugsTimeSetting> timeSettings = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                            .eq(PatientDrugsTimeSetting::getPatientDrugsId, drugs.getId())
                            .orderByAsc(PatientDrugsTimeSetting::getDayOfTheCycle)
                            .orderByAsc(PatientDrugsTimeSetting::getTriggerTimeOfTheDay));
                    drugs.setNextReminderDate(PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(drugs, timeSettings));
                    patientDrugsMapper.updateById(drugs);
                }
                patientDrugsTimeMapper.insertBatchSomeColumn(patientDrugsTimes);
                patientDayDrugs.setCheckinNumberTotal(patientDayDrugs.getCheckinNumberTotal() + 1);
                patientDayDrugsService.updateById(patientDayDrugs);
                for (PatientDrugsTime drugsTime : patientDrugsTimes) {
                    if (drugsTime.getNeedPush() != null && drugsTime.getNeedPush() == 1) {
                        jsonArray.add(drugsTime.getId());
                    }
                }
                // 没有需要推送提醒的药品。就不提醒了
                if (CollUtil.isEmpty(jsonArray)) {
                    continue;
                }
                ReminderLog reminderLog = reminderLogService.createReminderLog(patientId, "用药打卡", JSONUtil.toJsonStr(jsonArray),
                        PlanEnum.MEDICATION_REMIND.getCode(), null,
                        patientBaseInfo.getOrganId(), patientBaseInfo.getClassCode(), patientBaseInfo.getDoctorId(), patientBaseInfo.getServiceAdvisorId());
                if (functionStatus != 1) {
                    continue;
                }
                SendTemplateMessageForm form = new SendTemplateMessageForm();
                form.setWxAppId(wxAppId);
                Map<String, Object> params = new HashMap<>();
                params.put("keyword2", String.format("共%d款药品（查看详情）", jsonArray.size()));
                WxMpTemplateMessage wxMpTemplateMessage = null;

                long seconds = date.toEpochSecond(ZoneOffset.of("+8"));
                String url = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CALENDAR_INDEX, "drugsTimestamp=" + seconds);
                MsgPatientSystemMessageSaveDTO messageSaveDTO = new MsgPatientSystemMessageSaveDTO(PlanFunctionTypeEnum.MEDICATION.getCode(),
                        null,
                        url, patientBaseInfo.getId(), date, patientBaseInfo.getDoctorName(), tenant.getCode());

                messageSaveDTO.createPushContent(null, null, null);
                patientSystemMessageApi.saveSystemMessage(messageSaveDTO);

                // 个人服务号不需要去处理模版消息的发送
                if (tenant.isPersonalServiceNumber()) {
                    continue;
                }
                if (Objects.nonNull(templateMsgDto)) {
                    if (templateMsgDto.getCommonCategory()) {
                        wxMpTemplateMessage = new WxMpTemplateMessage();
                        List<WxMpTemplateData> mpTemplateData = CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientBaseInfo.getName(),
                                I18nUtils.getMessageByTenantDefault(CommonTemplateServiceWorkModel.MEDICATION_REMINDER, tenant.getDefaultLanguage()));
                        wxMpTemplateMessage.setData(mpTemplateData);
                    } else {
                        wxMpTemplateMessage = TemplateMessageHelper.setField(templateMsgDto, params);
                    }
                }
                if (Objects.isNull(wxMpTemplateMessage)) {
                    continue;
                }

                wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
                wxMpTemplateMessage.setToUser(patientBaseInfo.getOpenId());
                GeneralForm generalForm = new GeneralForm();
                generalForm.setWxAppId(wxAppId);
                wxMpTemplateMessage.setUrl(url);
                form.setTemplateMessage(wxMpTemplateMessage);
                R<String> templateMessage = weiXinApi.sendTemplateMessage(form);
                if (templateMessage.getIsSuccess()) {
                    reminderLogService.sendSuccess(reminderLog.getId());
                }
            }

        } finally {
            maximumPoolSize.decrementAndGet();
        }

    }

    @Data
    class PushDrugsTaskMessageContent {

        String tenantCode;

        LocalDateTime startTime;

        LocalDateTime endTime;

        private String toJSONString() {
            return JSONUtil.toJsonStr(this);
        }

        public PushDrugsTaskMessageContent fromJSONString(String json) {
            return JSONUtil.toBean(json, PushDrugsTaskMessageContent.class);
        }
    }


    /**
     * 只查询这个时间段有 用药推送的租户。
     * @param now
     */
    public void execute(LocalDateTime now) {
        LocalDateTime date = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), 0,0);
        LocalDateTime addDate = date.plusMinutes(15);
        LbqWrapper<PatientDrugs> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.ge(PatientDrugs::getNextReminderDate, date);
        lbqWrapper.eq(PatientDrugs::getStatus, 0);
        lbqWrapper.lt(PatientDrugs::getNextReminderDate, addDate);
        List<String> tenantCodes = patientDrugsMapper.selectWaitPushTenantCode(date, addDate);
        if (CollUtil.isNotEmpty(tenantCodes)) {
            for (String tenantCode : tenantCodes) {
                PushDrugsTaskMessageContent messageContent = new PushDrugsTaskMessageContent();
                messageContent.setTenantCode(tenantCode);
                messageContent.setStartTime(date);
                messageContent.setEndTime(addDate);

                // 将需要推送的 项目。 推送进入redis消息队列。
                // 分发给nursing 所有的实例。多线程 执行任务 给患者推送
                redisTemplate.opsForList().leftPush(PUSH_DRUGS_TASK_KEY, messageContent.toJSONString());
            }
        }
    }

}
