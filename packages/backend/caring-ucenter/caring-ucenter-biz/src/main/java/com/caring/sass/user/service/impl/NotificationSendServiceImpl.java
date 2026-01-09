package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.user.constant.NotificationJumpType;
import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.user.dao.BulkNotificationMapper;
import com.caring.sass.user.dao.BulkNotificationSendRecordMapper;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.util.NotificationTemplateMsgUtil;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName NotificationSendServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 15:07
 * @Version 1.0
 */
@Slf4j
@Service
public class NotificationSendServiceImpl {

    @Autowired
    BulkNotificationMapper bulkNotificationMapper;

    @Autowired
    BulkNotificationSendRecordMapper sendRecordMapper;

    @Autowired
    PatientMapper patientMapper;

    @Autowired
    DoctorMapper doctorMapper;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    TemplateMsgApi templateMsgApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    NotificationSendRunnable notificationSendRunnable;

    /**
     * 没有名字的人员 。
     */
    public static String noTargetName = "-";

    /**
     * 创建游客的推送记录
     * @param bulkNotificationId
     * @param tenantCode
     */
    public void  buildTouristsTag(Long bulkNotificationId, String tenantCode) {
        R<String> wxTagUser = weiXinApi.queryWxTagUser(TagsEnum.TOURISTS_TAG);
        if (wxTagUser.getIsSuccess()) {
            String redisKey = wxTagUser.getData();
            BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(redisKey);
            List<String> openIds = new ArrayList<>();
            List<BulkNotificationSendRecord> sendRecords;
            while (true) {
                // 从redis 中取出来一个。
                String openId = boundSetOps.pop();
                if (StrUtil.isNotEmpty(openId)) {
                    openIds.add(openId);
                }
                sendRecords = new ArrayList<>(100);
                if (openIds.size() == 100 || StrUtil.isEmpty(openId)) {
                    if (CollUtil.isNotEmpty(openIds)) {
                        List<String> existOpenIds = getNoExistOpenIds(openIds, bulkNotificationId);
                        for (String wxOpenId : existOpenIds) {
                            BulkNotificationSendRecord sendRecord = BulkNotificationSendRecord.builder()
                                    .notificationId(bulkNotificationId)
                                    .openId(wxOpenId)
                                    .tenantCode(tenantCode)
                                    .notificationTarget(NotificationTarget.TOURISTS_TAG)
                                    .notificationSendStatus(NotificationSendStatus.WAIT_SEND)
                                    .build();
                            sendRecord.setTargetName(noTargetName);
                            sendRecords.add(sendRecord);
                        }
                        sendRecordMapper.insertBatchSomeColumn(sendRecords);
                    }
                }
                if (StrUtil.isEmpty(openId)) {
                    break;
                }
            }
            redisTemplate.delete(redisKey);
        }

    }


    private List<String> getNoExistOpenIds(List<String> openIds, Long bulkNotificationId) {
        if (CollUtil.isEmpty(openIds)) {
            return openIds;
        }
        // 查询openId有多少是已经被保存进到记录中去的
        List<BulkNotificationSendRecord> sendRecords = sendRecordMapper.selectList(Wraps.<BulkNotificationSendRecord>lbQ()
                .eq(BulkNotificationSendRecord::getNotificationId, bulkNotificationId)
                .in(BulkNotificationSendRecord::getOpenId, openIds)
                .select(SuperEntity::getId, BulkNotificationSendRecord::getOpenId));
        if (CollUtil.isEmpty(sendRecords)) {
            return openIds;
        }
        // 拿到已经保存到数据库中的openId记录
        List<String> collectOpenIds = sendRecords.stream().map(BulkNotificationSendRecord::getOpenId).collect(Collectors.toList());
        // 将没有保存的openId 过滤出来。返回
        return openIds.stream().filter(item -> !collectOpenIds.contains(item)).collect(Collectors.toList());
    }

    public void  buildNoTag(Long bulkNotificationId, String tenantCode) {
        R<String> wxTagUser = weiXinApi.queryWxTagUser(TagsEnum.NO_TAG);
        if (wxTagUser.getIsSuccess()) {
            String redisKey = wxTagUser.getData();
            BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(redisKey);
            List<String> openIds = new ArrayList<>();
            List<BulkNotificationSendRecord> sendRecords;
            while (true) {
                // 从redis 中取出来一个。
                String openId = boundSetOps.pop();
                if (StrUtil.isNotEmpty(openId)) {
                    openIds.add(openId);
                }
                sendRecords = new ArrayList<>(100);
                if (openIds.size() == 100 || StrUtil.isEmpty(openId)) {
                    if (CollUtil.isNotEmpty(openIds)) {
                        List<String> existOpenIds = getNoExistOpenIds(openIds, bulkNotificationId);
                        for (String wxOpenId : existOpenIds) {
                            BulkNotificationSendRecord sendRecord = BulkNotificationSendRecord.builder()
                                    .notificationId(bulkNotificationId)
                                    .openId(wxOpenId)
                                    .tenantCode(tenantCode)
                                    .notificationTarget(NotificationTarget.NO_TAG)
                                    .notificationSendStatus(NotificationSendStatus.WAIT_SEND)
                                    .build();
                            sendRecord.setTargetName(noTargetName);
                            sendRecords.add(sendRecord);
                        }
                        sendRecordMapper.insertBatchSomeColumn(sendRecords);
                    }
                }
                if (StrUtil.isEmpty(openId)) {
                    break;
                }
            }
            redisTemplate.delete(redisKey);
        }

    }

    /**
     * 创建医生的 推送记录
     * @param bulkNotificationId
     * @param tenantCode
     */
    public void buildDoctorMessage(Long bulkNotificationId, String tenantCode) {
        R<String> wxTagUser = weiXinApi.queryWxTagUser(TagsEnum.DOCTOR_TAGS);
        if (wxTagUser.getIsSuccess()) {
            String redisKey = wxTagUser.getData();
            BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(redisKey);
            List<String> openIds = new ArrayList<>();
            List<BulkNotificationSendRecord> sendRecords;
            while (true) {
                // 从redis 中取出来一个。
                String openId = boundSetOps.pop();
                if (StrUtil.isNotEmpty(openId)) {
                    openIds.add(openId);
                }
                sendRecords = new ArrayList<>(100);
                if (openIds.size() == 100 || StrUtil.isEmpty(openId)) {
                    if (CollUtil.isNotEmpty(openIds)) {
                        List<String> existOpenIds = getNoExistOpenIds(openIds, bulkNotificationId);
                        List<Doctor> doctors = doctorMapper.selectList(Wraps.<Doctor>lbQ().in(Doctor::getOpenId, existOpenIds)
                                .select(SuperEntity::getId, Doctor::getName, Doctor::getOpenId)
                                .last(" limit " + existOpenIds.size() + " " ));
                        Map<String, Doctor> doctorMap = new HashMap<>();
                        if (CollUtil.isNotEmpty(doctors)) {
                            doctorMap= doctors.stream().collect(Collectors.toMap(Doctor::getOpenId, doctor -> doctor, (o1 ,o2) -> o2));
                        }
                        for (String wxOpenId : existOpenIds) {
                            Doctor doctor = doctorMap.get(wxOpenId);
                            BulkNotificationSendRecord sendRecord = BulkNotificationSendRecord.builder()
                                    .notificationId(bulkNotificationId)
                                    .openId(wxOpenId)
                                    .tenantCode(tenantCode)
                                    .notificationTarget(NotificationTarget.DOCTOR)
                                    .notificationSendStatus(NotificationSendStatus.WAIT_SEND)
                                    .build();
                            if (Objects.nonNull(doctor)) {
                                sendRecord.setTargetName(doctor.getName());
                                sendRecord.setTargetId(doctor.getId());
                            } else {
                                sendRecord.setTargetName(noTargetName);
                            }
                            sendRecords.add(sendRecord);
                        }
                        sendRecordMapper.insertBatchSomeColumn(sendRecords);



                    }
                }
                if (StrUtil.isEmpty(openId)) {
                    break;
                }
            }
            redisTemplate.delete(redisKey);
        }
    }

    /**
     * 将 患者的 要发送的记录维护一下
     * @param bulkNotificationId
     * @param tenantCode
     */
    public void buildPatientMessage(Long bulkNotificationId, String tenantCode) {
        R<String> wxTagUser = weiXinApi.queryWxTagUser(TagsEnum.PATIENT_TAG);
        if (wxTagUser.getIsSuccess()) {
            String redisKey = wxTagUser.getData();
            BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(redisKey);
            List<String> openIds = new ArrayList<>();
            List<BulkNotificationSendRecord> sendRecords;
            while (true) {
                // 从redis 中取出来一个。
                String openId = boundSetOps.pop();
                if (StrUtil.isNotEmpty(openId)) {
                    openIds.add(openId);
                }
                sendRecords = new ArrayList<>(100);
                if (openIds.size() == 100 || StrUtil.isEmpty(openId)) {
                    if (CollUtil.isNotEmpty(openIds)) {
                        List<String> existOpenIds = getNoExistOpenIds(openIds, bulkNotificationId);
                        List<Patient> patients = patientMapper.selectList(Wraps.<Patient>lbQ().in(Patient::getOpenId, existOpenIds)
                                .select(SuperEntity::getId, Patient::getName, Patient::getOpenId)
                                .last(" limit " + existOpenIds.size() + " " ));
                        Map<String, Patient> patientMap = new HashMap<>();
                        if (CollUtil.isNotEmpty(patients)) {
                            patientMap = patients.stream().collect(Collectors.toMap(Patient::getOpenId, patient -> patient, (o1 ,o2) -> o2));
                        }
                        for (String wxOpenId : existOpenIds) {
                            Patient patient = patientMap.get(wxOpenId);
                            BulkNotificationSendRecord sendRecord = BulkNotificationSendRecord.builder()
                                    .notificationId(bulkNotificationId)
                                    .openId(wxOpenId)
                                    .tenantCode(tenantCode)
                                    .notificationTarget(NotificationTarget.PATIENT)
                                    .notificationSendStatus(NotificationSendStatus.WAIT_SEND)
                                    .build();
                            if (Objects.nonNull(patient)) {
                                sendRecord.setTargetName(patient.getName());
                                sendRecord.setTargetId(patient.getId());
                            } else {
                                sendRecord.setTargetName(noTargetName);
                            }
                            sendRecords.add(sendRecord);
                        }
                        sendRecordMapper.insertBatchSomeColumn(sendRecords);
                    }
                }
                if (StrUtil.isEmpty(openId)) {
                  break;
                }
            }
            redisTemplate.delete(redisKey);
        }

    }

    /**
     * 初始化 待发送的 消息记录
     * @param tenantCode
     * @param bulkNotification
     */
    public void initMessageSendRecord(String tenantCode, BulkNotification bulkNotification, String appId, TemplateMsgDto msgDtoData) {
        BaseContextHandler.setTenant(tenantCode);
        NotificationTarget notificationTarget = bulkNotification.getNotificationTarget();
        Long bulkNotificationId = bulkNotification.getId();

        // 将消息丢人队列中去。
        String url = null;
        if (NotificationJumpType.LINK.equals(bulkNotification.getNotificationJumpType())) {
            url =  bulkNotification.getJumpBusinessContent();
        }

        if (NotificationTarget.PATIENT.equals(notificationTarget)) {
            buildPatientMessage(bulkNotificationId, tenantCode);
        }
        if (NotificationTarget.DOCTOR.equals(notificationTarget)) {
             buildDoctorMessage(bulkNotificationId, tenantCode);
        }
        if (NotificationTarget.TOURISTS_TAG.equals(notificationTarget)) {
            buildTouristsTag(bulkNotificationId, tenantCode);
        }
        if (NotificationTarget.NO_TAG.equals(notificationTarget)) {
            buildNoTag(bulkNotificationId, tenantCode);
        }
        if (NotificationTarget.ALL.equals(notificationTarget)) {
            buildPatientMessage(bulkNotificationId, tenantCode);
            buildDoctorMessage(bulkNotificationId, tenantCode);
            buildTouristsTag(bulkNotificationId, tenantCode);
            buildNoTag(bulkNotificationId, tenantCode);
        }

        notificationSendRunnable.pushMessage(tenantCode, appId, url, msgDtoData, bulkNotification);

    }


    /**
     * 统计一下
     */
    public Map<Long, Integer> countSend(List<Long> collect) {

        Map<Long, Integer> map = new HashMap<>();
        if (CollUtil.isEmpty(collect)) {
            return map;
        }
        List<Map<String, Object>> mapList = sendRecordMapper.selectMaps(Wrappers.<BulkNotificationSendRecord>query()
                .select("notification_id as notificationId", "count(*) as total")
                .in("notification_id", collect)
                .eq("notification_send_status", NotificationSendStatus.FINISH)
                .groupBy("notification_id"));
        for (Map<String, Object> objectMap : mapList) {
            if (objectMap.get("total") != null) {
                Object notificationId = objectMap.get("notificationId");
                Object total = objectMap.get("total");
                map.put(Long.parseLong(notificationId.toString()), Integer.parseInt(total.toString()));
            }
        }
        return map;
    }

    /**
     * 扫码 并发送了 code 的人员，发送一条 群发推送
     * @param code
     * @param scanUser
     */
    public void sendTemplateMsg(String code, BulkNotificationScanUser scanUser) {

        List<BulkNotification> bulkNotifications = bulkNotificationMapper.selectList(Wraps.<BulkNotification>lbQ().eq(BulkNotification::getCode, code));
        if (CollUtil.isEmpty(bulkNotifications)) {
            return;
        }
        BulkNotification notification = bulkNotifications.get(0);

        // 查询推送的模板消息
        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getOneById(notification.getSassTemplateMsgId());
        if (templateMsgDtoR.getIsSuccess() == null || !templateMsgDtoR.getIsSuccess()) {
            throw new BizException("获取微信模板失败");
        }
        TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
        if (Objects.isNull(msgDtoRData)) {
            return;
        }
        String url = null;
        if (NotificationJumpType.LINK.equals(notification.getNotificationJumpType())) {
            url = notification.getJumpBusinessContent();
        }
        SendTemplateMessageForm messageForm = NotificationTemplateMsgUtil.create(scanUser.getWxAppId(), url,
                scanUser.getOpenId(), scanUser.getNickname(), msgDtoRData);
        weiXinApi.sendTemplateMessage(messageForm);

    }

    public void deleteAll(Long notificationId) {

        sendRecordMapper.delete(Wraps.<BulkNotificationSendRecord>lbQ().eq(BulkNotificationSendRecord::getNotificationId, notificationId));

    }
}
