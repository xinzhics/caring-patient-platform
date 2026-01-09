package com.caring.sass.user.service.redis;


import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 个人服务号业务
 * 将本该公众号模版推送的消息提醒。替换成短信推送
 *
 * 医生医助给患者发送消息后。
 * 设置患者最晚回复时间。
 * 当最晚回复到达时，患者依然没有回复。那么给患者发送短信提醒
 * 使用redis hash表和 set集合 实现
 *
 * hash 存储患者的最晚回复时间。 当患者发送消息后，则清除记录， 并根据最晚回复时间，将他从set中清除
 *
 * hash： key：patient_no_read_log, field: patientId, value: last_reply_time
 * 约定最晚回复时间 的分钟数都 为 00： 10： 20： 30： 40：50 降低消息处理任务的出发频率
 *
 * set集合的key 设置2小时过期
 * set: key： last_reply_time， values： patientIds;
 *
 */
@Slf4j
@Component
public class PatientMsgsNoReadCenter {


    private static final String PATIENT_NO_READ_LOG = "patient_no_read_log";

    private static final String PATIENT_NO_READ_DATA_INFO = "patient_no_read_data_info";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    BusinessReminderLogControllerApi reminderLogControllerApi;

    @Autowired
    PatientMapper patientMapper;


    private static ExecutorService executor;

    public PatientMsgsNoReadCenter() {
        NamedThreadFactory threadFactory = new NamedThreadFactory("patient_msgs_no_read_center_", false);
        executor = new ThreadPoolExecutor(0, 2, 0L,
                TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(1000),
                threadFactory);
    }

    private String handleValue(Long patientId, String tenantCode) {
        return patientId.toString() + "," + tenantCode;
    }

    /**
     * 当个人服务号医生医助给患者发了消息之后。
     * 在redis中存储 患者最晚回复消息的提醒时间
     * @param patientId
     */
    public void savePatientMsgsNoRead(Long patientId, String tenantCode, String domain, String tenantName, String msgSendUserName, String msgSendUserRole) {
        String value = handleValue(patientId, tenantCode);
        try {
            Boolean hassedKey = redisTemplate.boundHashOps(PATIENT_NO_READ_LOG).hasKey(value);
            if (hassedKey != null && hassedKey) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            int minute = now.getMinute();
            minute = minute / 10 * 10;
            LocalDateTime dateTime = now.withMinute(minute).plusMinutes(30).withSecond(0).withNano(0);
            String last_reply_time = dateTime.toString();
            redisTemplate.opsForHash().put(PATIENT_NO_READ_LOG, value, last_reply_time);
            redisTemplate.opsForHash().put(PATIENT_NO_READ_DATA_INFO, value, domain+ "," + tenantName + "," + msgSendUserName + "," + msgSendUserRole);
            redisTemplate.opsForSet().add(last_reply_time, value);
        } catch (Exception e) {
            log.error("Error saving patient message no read: {}", e.getMessage());
        }
    }


    /**
     * 患者发送了消息之后。
     * 将患者的最晚回复时间，删除
     * @param patientId
     */
    public void removePatientMsgsNoRead(Long patientId, String tenantCode) {
        String value = handleValue(patientId, tenantCode);
        try {
            Boolean hassedKey = redisTemplate.boundHashOps(PATIENT_NO_READ_LOG).hasKey(value);
            if (hassedKey != null && hassedKey) {
                Object lastReplyTimeObj = redisTemplate.opsForHash().get(PATIENT_NO_READ_LOG, value);
                if (lastReplyTimeObj != null) {
                    redisTemplate.opsForHash().delete(PATIENT_NO_READ_LOG, value);
                    redisTemplate.opsForHash().delete(PATIENT_NO_READ_DATA_INFO, value);
                    redisTemplate.opsForSet().remove(lastReplyTimeObj.toString(), value);
                }
            }
        } catch (Exception e) {
            log.error("Error removing patient message no read: {}", e.getMessage());
        }
    }


    /**
     * 定时拉取 患者最晚回复时间。
     * 如果患者最晚回复时间等于当前时间，则发送短信提醒
     */
    public void scheduledPushPatientMsgsNoRead() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = now.withSecond(0).withNano(0);
        String timeString = dateTime.toString();
        while (true) {
            String popped;
            try {
                popped = redisTemplate.opsForSet().pop(timeString);
            } catch (Exception e) {
                log.error("Error popping from Redis set: {}", e.getMessage());
                break;
            }
            if (StrUtil.isEmpty(popped)) {
                break;
            }

            try {
                redisTemplate.opsForHash().delete(PATIENT_NO_READ_LOG, popped);
            } catch (Exception e) {
                log.error("Error deleting from Redis hash: {}", e.getMessage());
            }
            try {
                executor.execute(() -> pushMessage(popped));
            } catch (Exception e) {
                log.error("Error executing pushMessage task: {}", e.getMessage());
            }
        }
    }


    /**
     * 给患者推送 短信提醒
     * @param params = patientId, tenantCode, domain
     */
    public void pushMessage(String params) {
        if (params == null || params.isEmpty()) {
            log.warn("Invalid params: {}", params);
            return;
        }
        String[] split = params.split(",");
        if (split.length < 2) {
            log.warn("Invalid params format: {}", params);
            return;
        }
        String patientId = split[0];
        String tenantCode = split[1];
        if (StrUtil.isEmpty(tenantCode) || StrUtil.isEmpty(patientId)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        String domain = "";
        String tenantName = "";
        String msgSendUserName;
        String msgSendUserRole;
        Object sendUserInfo = redisTemplate.opsForHash().get(PATIENT_NO_READ_DATA_INFO, params);
        if (sendUserInfo == null) {
            msgSendUserName = "医生";
            msgSendUserRole = "";
        } else {
            String[] strings = sendUserInfo.toString().split(",");
            if (strings.length < 4) {
                log.warn("Invalid sendUserInfo format: {}", sendUserInfo);
                return;
            }
            domain = strings[0];
            tenantName = strings[1];
            msgSendUserName = strings[2];
            msgSendUserRole = strings[3];
        }

        try {
            Patient patient = patientMapper.selectOne(Wraps.<Patient>lbQ().eq(SuperEntity::getId, Long.parseLong(patientId))
                    .select(SuperEntity::getId, Patient::getName, Patient::getMobile));
            if (patient == null) {
                log.warn("Patient not found for ID: {}", patientId);
                return;
            }
            String smsParams = BusinessReminderType.getPatientTimeOutConsultationNotice(tenantName,
                    msgSendUserName, msgSendUserRole);
            String wxPatientBizUrl = ApplicationDomainUtil.wxPatientBizUrl(domain, true, H5Router.CHAT);
            // 创建一条 今日待办消息 的推送任务
            BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                    .mobile(patient.getMobile())
                    .wechatRedirectUrl(wxPatientBizUrl)
                    .diseasesType("")
                    .type(BusinessReminderType.PATIENT_TIME_OUT_CONSULTATION_NOTICE)
                    .tenantCode(tenantCode)
                    .queryParams(smsParams)
                    .patientId(patient.getId())
                    .status(0)
                    .openThisMessage(0)
                    .finishThisCheckIn(0)
                    .build();

            R<Boolean> booleanR = reminderLogControllerApi.sendNoticeSms(logSaveDTO);
            log.info("SMS sent: {}", booleanR.getData());
        } catch (Exception e) {
            log.error("Error pushing message to patient: {}", e.getMessage());
        }
    }



}
