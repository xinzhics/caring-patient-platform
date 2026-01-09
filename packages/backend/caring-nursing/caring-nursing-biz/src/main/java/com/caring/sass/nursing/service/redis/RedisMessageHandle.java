package com.caring.sass.nursing.service.redis;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.common.redis.RedisMessageDoctorChangeNursing;
import com.caring.sass.common.redis.RedisMessageDto;
import com.caring.sass.common.redis.RedisMessageNursingChange;
import com.caring.sass.common.redis.RedisMessagePatientChangeDoctor;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.constant.AppointmentStatusEnum;
import com.caring.sass.nursing.dao.appointment.AppointmentMapper;
import com.caring.sass.nursing.dao.drugs.PatientDayDrugsMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsHistoryLogMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeMapper;
import com.caring.sass.nursing.dao.form.FormResultBackUpMapper;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.form.SugarFormResultMapper;
import com.caring.sass.nursing.dao.plan.PatientNursingPlanMapper;
import com.caring.sass.nursing.dao.plan.PlanCmsReminderLogMapper;
import com.caring.sass.nursing.dao.plan.ReminderLogMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataMonthMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataNewMapper;
import com.caring.sass.nursing.dao.tag.AssociationMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoFormResultLastPushTimeMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultFieldAbnormalMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultMapper;
import com.caring.sass.nursing.dao.unfinished.UnfinishedPatientRecordMapper;
import com.caring.sass.nursing.entity.appointment.AppointConfig;
import com.caring.sass.nursing.entity.appointment.Appointment;
import com.caring.sass.nursing.entity.drugs.*;
import com.caring.sass.nursing.entity.form.*;
import com.caring.sass.nursing.entity.plan.PatientNursingPlan;
import com.caring.sass.nursing.entity.plan.PlanCmsReminderLog;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import com.caring.sass.nursing.entity.statistics.StatisticsData;
import com.caring.sass.nursing.entity.statistics.StatisticsDataMonth;
import com.caring.sass.nursing.entity.statistics.StatisticsDataNew;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFormResultLastPushTime;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResult;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResultFieldAbnormal;
import com.caring.sass.nursing.entity.unfinished.UnfinishedPatientRecord;
import com.caring.sass.nursing.service.appointment.AppointConfigService;
import com.caring.sass.nursing.service.drugs.DrugsResultHandleHisService;
import com.caring.sass.nursing.service.drugs.DrugsResultInformationService;
import com.caring.sass.nursing.service.form.IndicatorsResultInformationService;
import com.caring.sass.nursing.service.form.PatientFormFieldReferenceService;
import com.caring.sass.nursing.service.information.CompletenessInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisMessageHandle
 * @Description
 * @Author yangShuai
 * @Date 2022/4/26 16:00
 * @Version 1.0
 */
@Slf4j
@Component
public class RedisMessageHandle {

    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    AssociationMapper associationMapper;

    @Autowired
    StatisticsDataMapper statisticsDataMapper;

    @Autowired
    PatientNursingPlanMapper patientNursingPlanMapper;

    @Autowired
    SugarFormResultMapper sugarFormResultMapper;

    @Autowired
    FormResultBackUpMapper formResultBackUpMapper;

    @Autowired
    ReminderLogMapper reminderLogMapper;

    @Autowired
    PlanCmsReminderLogMapper planCmsReminderLogMapper;

    @Autowired
    PatientDrugsMapper patientDrugsMapper;

    @Autowired
    PatientDrugsHistoryLogMapper patientDrugsHistoryLogMapper;

    @Autowired
    PatientDrugsTimeMapper patientDrugsTimeMapper;

    @Autowired
    PatientDayDrugsMapper patientDayDrugsMapper;

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    CompletenessInformationService completenessInformationService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    AppointConfigService appointConfigService;

    @Autowired
    PatientFormFieldReferenceService patientFormFieldReferenceService;

    @Autowired
    IndicatorsResultInformationService indicatorsResultInformationService;

    @Autowired
    DrugsResultHandleHisService drugsResultHandleHisService;

    @Autowired
    DrugsResultInformationService drugsResultInformationService;

    @Autowired
    TraceIntoFormResultLastPushTimeMapper traceIntoFormResultLastPushTimeMapper;

    @Autowired
    TraceIntoResultMapper traceIntoResultMapper;

    @Autowired
    TraceIntoResultFieldAbnormalMapper traceIntoResultFieldAbnormalMapper;

    @Autowired
    StatisticsDataMonthMapper statisticsDataMonthMapper;

    @Autowired
    StatisticsDataNewMapper statisticsDataNewMapper;

    @Autowired
    UnfinishedPatientRecordMapper  unfinishedPatientRecordMapper;

    public void handleMessage(RedisMessageDto messageDto) {

        String tenantCode = messageDto.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        String businessId = messageDto.getBusinessId();
        if (StrUtil.isEmpty(businessId)) {
            return;
        }
        long patientId = Long.parseLong(businessId);

        formResultMapper.delete(Wraps.<FormResult>lbQ().eq(FormResult::getUserId, patientId));
        associationMapper.delete(Wraps.<Association>lbQ().eq(Association::getAssociationId, businessId));
        statisticsDataMapper.delete(Wraps.<StatisticsData>lbQ().eq(StatisticsData::getPatientId, patientId));
        statisticsDataMonthMapper.delete(Wraps.<StatisticsDataMonth>lbQ().eq(StatisticsDataMonth::getPatientId, patientId));
        statisticsDataNewMapper.delete(Wraps.<StatisticsDataNew>lbQ().eq(StatisticsDataNew::getPatientId, patientId));
        patientNursingPlanMapper.delete(Wraps.<PatientNursingPlan>lbQ().eq(PatientNursingPlan::getPatientId, patientId));
        sugarFormResultMapper.delete(Wraps.<SugarFormResult>lbQ().eq(SugarFormResult::getPatientId, patientId));
        formResultBackUpMapper.delete(Wraps.<FormResultBackUp>lbQ().eq(FormResultBackUp::getPatientId, patientId));

        reminderLogMapper.delete(Wraps.<ReminderLog>lbQ().eq(ReminderLog::getPatientId, patientId));
        planCmsReminderLogMapper.delete(Wraps.<PlanCmsReminderLog>lbQ().eq(PlanCmsReminderLog::getUserId, patientId));

        patientDrugsMapper.delete(Wraps.<PatientDrugs>lbQ().eq(PatientDrugs::getPatientId, patientId));
        patientDrugsHistoryLogMapper.delete(Wraps.<PatientDrugsHistoryLog>lbQ().eq(PatientDrugsHistoryLog::getPatientId, patientId));
        patientDrugsTimeMapper.delete(Wraps.<PatientDrugsTime>lbQ().eq(PatientDrugsTime::getPatientId, patientId));
        patientDayDrugsMapper.delete(Wraps.<PatientDayDrugs>lbQ().eq(PatientDayDrugs::getPatientId, patientId));
        patientFormFieldReferenceService.remove(Wraps.<PatientFormFieldReference>lbQ().eq(PatientFormFieldReference::getPatientId, patientId));
        appointmentMapper.delete(Wraps.<Appointment>lbQ()
                .ne(Appointment::getStatus, AppointmentStatusEnum.VISITED.getCode())
                .eq(Appointment::getPatientId, patientId));

        // 信息完整度
        completenessInformationService.removeByPatientId(patientId);
        // 患者监测数据结果及处理表
        indicatorsResultInformationService.remove(Wraps.<IndicatorsResultInformation>lbQ().eq(IndicatorsResultInformation::getPatientId, patientId));
        // 用药预警-预警处理历史表
        drugsResultHandleHisService.remove(Wraps.<DrugsResultHandleHis>lbQ().eq(DrugsResultHandleHis::getPatientId, patientId));
        // 用药预警-预警结果表
        drugsResultInformationService.remove(Wraps.<DrugsResultInformation>lbQ().eq(DrugsResultInformation::getPatientId, patientId));

        traceIntoFormResultLastPushTimeMapper.delete(Wraps.<TraceIntoFormResultLastPushTime>lbQ().eq(TraceIntoFormResultLastPushTime::getPatientId, patientId));
        traceIntoResultMapper.delete(Wraps.<TraceIntoResult>lbQ().eq(TraceIntoResult::getPatientId, patientId));
        traceIntoResultFieldAbnormalMapper.delete(Wraps.<TraceIntoResultFieldAbnormal>lbQ().eq(TraceIntoResultFieldAbnormal::getPatientId, patientId));

        unfinishedPatientRecordMapper.delete(Wraps.<UnfinishedPatientRecord>lbQ().eq(UnfinishedPatientRecord::getPatientId, patientId));
    }

    public void handleMessage(RedisMessageDto messageDto, String handleKey) {
        Boolean nursing = false;
        if (redisTemplate == null) {
            log.info("PatientDeleteMessageListener redisTemplate is null， {}", handleKey);
            SaasGlobalThreadPool.execute(() -> handleMessage(messageDto));
        } else {
            try {
                nursing = redisTemplate.opsForValue().setIfAbsent(handleKey, "1");
            } catch (Exception e) {
                log.info("PatientDeleteMessageListener 锁失败了， {}", handleKey);
            }
            if (nursing != null && nursing) {
                this.redisTemplate.expire(handleKey, 10, TimeUnit.SECONDS);
                SaasGlobalThreadPool.execute(() -> handleMessage(messageDto));
            }
        }

    }


    private void handleDoctorChangeNursingMessage(RedisMessageDoctorChangeNursing message) {
        String tenantCode = message.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);

        // 修改 医生的预约配置
        UpdateWrapper<AppointConfig> wrapper = new UpdateWrapper<>();
        wrapper.set("organization_id", message.getOrgId());
        wrapper.eq("doctor_id", message.getDoctorId());
        appointConfigService.update(new AppointConfig(), wrapper);

        // 修改用药预警，  医助为医生新的医助
        UpdateWrapper<DrugsResultHandleHis> drugsResultHandleHisWrapper = new UpdateWrapper<>();
        drugsResultHandleHisWrapper.set("nursing_id", message.getTargetNursingId());
        drugsResultHandleHisWrapper.eq("doctor_id", message.getDoctorId());
        drugsResultHandleHisService.update(new DrugsResultHandleHis(), drugsResultHandleHisWrapper);

        UpdateWrapper<DrugsResultInformation> drugsResultInformationWrapper = new UpdateWrapper<>();
        drugsResultInformationWrapper.set("nursing_id", message.getTargetNursingId());
        drugsResultInformationWrapper.eq("doctor_id", message.getDoctorId());
        drugsResultInformationService.update(new DrugsResultInformation(), drugsResultInformationWrapper);

        // 信息完整度 没有冗余字段 不需要处理

        // 监测数据
        UpdateWrapper<IndicatorsResultInformation> informationWrapper = new UpdateWrapper<>();
        informationWrapper.set("nursing_id", message.getTargetNursingId());
        informationWrapper.eq("doctor_id", message.getDoctorId());
        indicatorsResultInformationService.update(new IndicatorsResultInformation(), informationWrapper);

        // 患者的 基准值 目标值
        UpdateWrapper<PatientFormFieldReference> referenceWrapper = new UpdateWrapper<>();
        referenceWrapper.set("nursing_id", message.getTargetNursingId());
        referenceWrapper.set("org_id", message.getOrgId());
        referenceWrapper.eq("doctor_id", message.getDoctorId());
        patientFormFieldReferenceService.update(new PatientFormFieldReference(), referenceWrapper);

        // 修改 护理计划推送记录 中 医生对应的医助为新的医助
        UpdateWrapper<ReminderLog> reminderLogWrapper = new UpdateWrapper<>();
        reminderLogWrapper.set("nursing_id", message.getTargetNursingId());
        reminderLogWrapper.set("org_id", message.getOrgId());
        reminderLogWrapper.set("class_code", message.getClassCode());
        reminderLogWrapper.eq("doctor_id", message.getDoctorId());
        reminderLogMapper.update(new ReminderLog(),reminderLogWrapper);

        UpdateWrapper<TraceIntoFormResultLastPushTime> lastPushTimeUpdateWrapper = new UpdateWrapper<>();
        lastPushTimeUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        lastPushTimeUpdateWrapper.eq("doctor_id", message.getDoctorId());
        traceIntoFormResultLastPushTimeMapper.update(new TraceIntoFormResultLastPushTime(), lastPushTimeUpdateWrapper);


        UpdateWrapper<TraceIntoResult> resultUpdateWrapper = new UpdateWrapper<>();
        resultUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        resultUpdateWrapper.eq("doctor_id", message.getDoctorId());
        traceIntoResultMapper.update(new TraceIntoResult(), resultUpdateWrapper);

        UpdateWrapper<TraceIntoResultFieldAbnormal> fieldAbnormalUpdateWrapper = new UpdateWrapper<>();
        fieldAbnormalUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        fieldAbnormalUpdateWrapper.eq("doctor_id", message.getDoctorId());
        traceIntoResultFieldAbnormalMapper.update(new TraceIntoResultFieldAbnormal(), fieldAbnormalUpdateWrapper);

        UpdateWrapper<UnfinishedPatientRecord> unfinishedPatientRecordUpdateWrapper = new UpdateWrapper<>();
        unfinishedPatientRecordUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        unfinishedPatientRecordUpdateWrapper.eq("doctor_id", message.getDoctorId());
        unfinishedPatientRecordMapper.update(new UnfinishedPatientRecord(), unfinishedPatientRecordUpdateWrapper);
    }

    /**
     * 医生变更医助后的消息处理
     * @param message
     * @param handleKey
     */
    public void handleDoctorChangeNursingMessage(RedisMessageDoctorChangeNursing message, String handleKey) {
        Boolean nursing = false;
        if (redisTemplate == null) {
            SaasGlobalThreadPool.execute(() -> handleDoctorChangeNursingMessage(message));
        } else {
            try {
                nursing = redisTemplate.opsForValue().setIfAbsent(handleKey, "1");
            } catch (Exception e) {
                log.info("PatientDeleteMessageListener 锁失败了， {}", handleKey);
            }
            if (nursing != null && nursing) {
                this.redisTemplate.expire(handleKey, 10, TimeUnit.SECONDS);
                SaasGlobalThreadPool.execute(() -> handleDoctorChangeNursingMessage(message));
            }
        }
    }

    /**
     * 患者变更医生后的消息处理
     * @param message
     * @param handleKey
     */
    public void handlePatientChangeDoctorMessage(RedisMessagePatientChangeDoctor message, String handleKey) {
        Boolean nursing = false;
        if (redisTemplate == null) {
            SaasGlobalThreadPool.execute(() -> handlePatientChangeDoctorMessage(message));
        } else {
            try {
                nursing = redisTemplate.opsForValue().setIfAbsent(handleKey, "1");
            } catch (Exception e) {
                log.info("PatientDeleteMessageListener 锁失败了， {}", handleKey);
            }
            if (nursing != null && nursing) {
                this.redisTemplate.expire(handleKey, 10, TimeUnit.SECONDS);
                SaasGlobalThreadPool.execute(() -> handlePatientChangeDoctorMessage(message));
            }
        }
    }

    /**
     * 患者变更医生后的消息处理
     * @param message
     */
    private void handlePatientChangeDoctorMessage(RedisMessagePatientChangeDoctor message) {
        String tenantCode = message.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        Boolean changeNursing = message.getChangeNursing();

        // 修改用药预警， 医助为医生新的医助
        UpdateWrapper<DrugsResultHandleHis> drugsResultHandleHisWrapper = new UpdateWrapper<>();
        drugsResultHandleHisWrapper.set("doctor_id", message.getTargetDoctorId());
        if (changeNursing) {
            drugsResultHandleHisWrapper.set("nursing_id", message.getTargetNursingId());
        }
        drugsResultHandleHisWrapper.set("doctor_name", message.getTargetDoctorName());
        drugsResultHandleHisWrapper.eq("patient_id", message.getPatientId());
        drugsResultHandleHisService.update(new DrugsResultHandleHis(), drugsResultHandleHisWrapper);

        UpdateWrapper<DrugsResultInformation> drugsResultInformationWrapper = new UpdateWrapper<>();
        drugsResultInformationWrapper.set("doctor_id", message.getTargetDoctorId());
        if (changeNursing) {
            drugsResultInformationWrapper.set("nursing_id", message.getTargetNursingId());
        }
        drugsResultInformationWrapper.set("doctor_name", message.getTargetDoctorName());
        drugsResultInformationWrapper.eq("patient_id", message.getPatientId());
        drugsResultInformationService.update(new DrugsResultInformation(), drugsResultInformationWrapper);

        // 信息完整度 没有冗余字段 不需要处理

        // 监测数据
        UpdateWrapper<IndicatorsResultInformation> informationWrapper = new UpdateWrapper<>();
        informationWrapper.set("doctor_id", message.getTargetDoctorId());
        if (changeNursing) {
            informationWrapper.set("nursing_id", message.getTargetNursingId());
        }
        informationWrapper.set("nursing_id", message.getTargetNursingId());
        informationWrapper.set("doctor_name", message.getTargetDoctorName());
        informationWrapper.eq("patient_id", message.getPatientId());
        indicatorsResultInformationService.update(new IndicatorsResultInformation(), informationWrapper);

        // 患者的 基准值 目标值
        UpdateWrapper<PatientFormFieldReference> referenceWrapper = new UpdateWrapper<>();
        if (changeNursing) {
            referenceWrapper.set("nursing_id", message.getTargetNursingId());
            referenceWrapper.set("org_id", message.getOrgId());
        }
        referenceWrapper.set("doctor_id", message.getTargetDoctorId());
        referenceWrapper.eq("patient_id", message.getPatientId());
        patientFormFieldReferenceService.update(new PatientFormFieldReference(), referenceWrapper);

        // 修改 护理计划推送记录 中 医生对应的医助为新的医助
        UpdateWrapper<ReminderLog> reminderLogWrapper = new UpdateWrapper<>();
        if (changeNursing) {
            reminderLogWrapper.set("nursing_id", message.getTargetNursingId());
            reminderLogWrapper.set("org_id", message.getOrgId());
            reminderLogWrapper.set("class_code", message.getClassCode());
        }
        reminderLogWrapper.set("doctor_id", message.getTargetDoctorId());
        reminderLogWrapper.eq("patient_id", message.getPatientId());
        reminderLogMapper.update(new ReminderLog(),reminderLogWrapper);

        UpdateWrapper<TraceIntoFormResultLastPushTime> lastPushTimeUpdateWrapper = new UpdateWrapper<>();
        lastPushTimeUpdateWrapper.set("doctor_id", message.getTargetDoctorId());
        if (changeNursing) {
            lastPushTimeUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        }
        lastPushTimeUpdateWrapper.eq("patient_id", message.getPatientId());
        traceIntoFormResultLastPushTimeMapper.update(new TraceIntoFormResultLastPushTime(), lastPushTimeUpdateWrapper);


        UpdateWrapper<TraceIntoResult> resultUpdateWrapper = new UpdateWrapper<>();
        resultUpdateWrapper.set("doctor_id", message.getTargetDoctorId());
        if (changeNursing) {
            resultUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        }
        resultUpdateWrapper.eq("patient_id", message.getPatientId());
        traceIntoResultMapper.update(new TraceIntoResult(), resultUpdateWrapper);

        UpdateWrapper<TraceIntoResultFieldAbnormal> fieldAbnormalUpdateWrapper = new UpdateWrapper<>();
        fieldAbnormalUpdateWrapper.set("doctor_id", message.getTargetDoctorId());
        if (changeNursing) {
            fieldAbnormalUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        }
        fieldAbnormalUpdateWrapper.eq("patient_id", message.getPatientId());
        traceIntoResultFieldAbnormalMapper.update(new TraceIntoResultFieldAbnormal(), fieldAbnormalUpdateWrapper);

        UpdateWrapper<UnfinishedPatientRecord> unfinishedPatientRecordUpdateWrapper = new UpdateWrapper<>();
        unfinishedPatientRecordUpdateWrapper.set("doctor_id", message.getTargetDoctorId());
        if (changeNursing) {
            unfinishedPatientRecordUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        }
        unfinishedPatientRecordUpdateWrapper.eq("patient_id", message.getPatientId());
        unfinishedPatientRecordMapper.update(new UnfinishedPatientRecord(), unfinishedPatientRecordUpdateWrapper);
    }


    private void handleNursingChangeMessage(RedisMessageNursingChange message) {
        String tenantCode = message.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);

        // 修改 医生的预约配置 考虑到 医生已经被转移到 新的医助下面来了。
        // 这里直接对 新医助下的医生做一次 机构的同步
        UpdateWrapper<AppointConfig> wrapper = new UpdateWrapper<>();
        wrapper.set("organization_id", message.getOrgId());
        wrapper.apply("doctor_id in (SELECT id FROM u_user_doctor where nursing_id = "+message.getTargetNursingId()+")");
        appointConfigService.update(new AppointConfig(), wrapper);



        // 修改用药预警，  医助为医生新的医助
        UpdateWrapper<DrugsResultHandleHis> drugsResultHandleHisWrapper = new UpdateWrapper<>();
        drugsResultHandleHisWrapper.set("nursing_id", message.getTargetNursingId());
        drugsResultHandleHisWrapper.eq("nursing_id", message.getNursingId());
        drugsResultHandleHisService.update(new DrugsResultHandleHis(), drugsResultHandleHisWrapper);

        UpdateWrapper<DrugsResultInformation> drugsResultInformationWrapper = new UpdateWrapper<>();
        drugsResultInformationWrapper.set("nursing_id", message.getTargetNursingId());
        drugsResultInformationWrapper.eq("nursing_id", message.getNursingId());
        drugsResultInformationService.update(new DrugsResultInformation(), drugsResultInformationWrapper);

        // 信息完整度 没有冗余字段 不需要处理

        // 监测数据
        UpdateWrapper<IndicatorsResultInformation> informationWrapper = new UpdateWrapper<>();
        informationWrapper.set("nursing_id", message.getTargetNursingId());
        informationWrapper.eq("nursing_id", message.getNursingId());
        indicatorsResultInformationService.update(new IndicatorsResultInformation(), informationWrapper);

        // 患者的 基准值 目标值
        UpdateWrapper<PatientFormFieldReference> referenceWrapper = new UpdateWrapper<>();
        referenceWrapper.set("nursing_id", message.getTargetNursingId());
        referenceWrapper.set("org_id", message.getOrgId());
        referenceWrapper.eq("nursing_id", message.getNursingId());
        patientFormFieldReferenceService.update(new PatientFormFieldReference(), referenceWrapper);

        // 修改 护理计划推送记录 中 医助为新的医助
        UpdateWrapper<ReminderLog> reminderLogWrapper = new UpdateWrapper<>();
        reminderLogWrapper.set("nursing_id", message.getTargetNursingId());
        reminderLogWrapper.set("org_id", message.getOrgId());
        reminderLogWrapper.set("class_code", message.getClassCode());
        reminderLogWrapper.eq("nursing_id", message.getNursingId());
        reminderLogMapper.update(new ReminderLog(),reminderLogWrapper);


        UpdateWrapper<TraceIntoFormResultLastPushTime> lastPushTimeUpdateWrapper = new UpdateWrapper<>();
        lastPushTimeUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        lastPushTimeUpdateWrapper.eq("nursing_id", message.getNursingId());
        traceIntoFormResultLastPushTimeMapper.update(new TraceIntoFormResultLastPushTime(), lastPushTimeUpdateWrapper);


        UpdateWrapper<TraceIntoResult> resultUpdateWrapper = new UpdateWrapper<>();
        resultUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        resultUpdateWrapper.eq("nursing_id", message.getNursingId());
        traceIntoResultMapper.update(new TraceIntoResult(), resultUpdateWrapper);

        UpdateWrapper<TraceIntoResultFieldAbnormal> fieldAbnormalUpdateWrapper = new UpdateWrapper<>();
        fieldAbnormalUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        fieldAbnormalUpdateWrapper.eq("nursing_id", message.getNursingId());
        traceIntoResultFieldAbnormalMapper.update(new TraceIntoResultFieldAbnormal(), fieldAbnormalUpdateWrapper);

        UpdateWrapper<UnfinishedPatientRecord> unfinishedPatientRecordUpdateWrapper = new UpdateWrapper<>();
        unfinishedPatientRecordUpdateWrapper.set("nursing_id", message.getTargetNursingId());
        unfinishedPatientRecordUpdateWrapper.eq("nursing_id", message.getNursingId());
        unfinishedPatientRecordMapper.update(new UnfinishedPatientRecord(), unfinishedPatientRecordUpdateWrapper);
    }

    /**
     * 医助转移数据到新医助
     * @param message
     * @param handleKey
     */
    public void handleNursingChangeMessage(RedisMessageNursingChange message, String handleKey) {
        Boolean nursing = false;
        if (redisTemplate == null) {
            SaasGlobalThreadPool.execute(() -> handleNursingChangeMessage(message));
        } else {
            try {
                nursing = redisTemplate.opsForValue().setIfAbsent(handleKey, "1");
            } catch (Exception e) {
                log.info("PatientDeleteMessageListener 锁失败了， {}", handleKey);
            }
            if (nursing != null && nursing) {
                this.redisTemplate.expire(handleKey, 10, TimeUnit.SECONDS);
                SaasGlobalThreadPool.execute(() -> handleNursingChangeMessage(message));
            }
        }
    }


}
