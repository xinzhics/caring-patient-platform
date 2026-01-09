package com.caring.sass.msgs.redis;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.RedisMessageDoctorChangeNursing;
import com.caring.sass.common.redis.RedisMessageDto;
import com.caring.sass.common.redis.RedisMessagePatientChangeDoctor;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.msgs.dao.*;
import com.caring.sass.msgs.entity.*;
import com.caring.sass.oauth.api.PatientApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
    ConsultationChatMapper consultationChatMapper;

    @Autowired
    ChatUserNewMsgMapper chatUserNewMsgMapper;

    @Autowired
    ChatMapper chatMapper;

    @Autowired
    ChatAtRecordMapper chatAtRecordMapper;

    @Autowired
    ChatSendReadMapper chatSendReadMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    PatientApi patientApi;

    @Autowired
    MsgPatientSystemMessageMapper msgPatientSystemMessageMapper;

    @Autowired
    PatientSystemMessageRemarkMapper patientSystemMessageRemarkMapper;

    /**
     * 患者删除 事件 处理
     * @param message
     */
    public void handleMessage(String message) {

        log.info("PatientDeleteMessageListener handleMessage : {} {}", SaasRedisBusinessKey.getPatientDeleteKey(),  message);

        RedisMessageDto messageDto = JSON.parseObject(message, RedisMessageDto.class);
        String tenantCode = messageDto.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        String businessId = messageDto.getBusinessId();
        String patientImAccount = messageDto.getPatientImAccount();
        if (StrUtil.isEmpty(businessId)) {
            return;
        }
        long patientId = Long.parseLong(businessId);

        consultationChatMapper.delete(Wraps.<ConsultationChat>lbQ().eq(ConsultationChat::getSenderId, patientId)
                .eq(ConsultationChat::getSenderRoleType, UserType.UCENTER_PATIENT));

        chatUserNewMsgMapper.delete(Wraps.<ChatUserNewMsg>lbQ().eq(ChatUserNewMsg::getPatientId, patientId));

        chatMapper.delete(Wraps.<Chat>lbQ().eq(Chat::getSenderId, businessId)
                .eq(Chat::getSenderRoleType, UserType.UCENTER_PATIENT));

        chatAtRecordMapper.delete(Wraps.<ChatAtRecord>lbQ().eq(ChatAtRecord::getAtUserId, patientId)
                .eq(ChatAtRecord::getUserType, UserType.UCENTER_PATIENT));

        chatSendReadMapper.delete(Wraps.<ChatSendRead>lbQ().eq(ChatSendRead::getGroupId, patientImAccount));

        msgPatientSystemMessageMapper.delete(Wraps.<MsgPatientSystemMessage>lbQ().eq(MsgPatientSystemMessage::getPatientId, patientId));

        patientSystemMessageRemarkMapper.delete(Wraps.<PatientSystemMessageRemark>lbQ().eq(PatientSystemMessageRemark::getPatientId, patientId));
    }


    public void handleMessage(String message, String handleKey) {

        if (redisTemplate == null) {
            SaasGlobalThreadPool.execute(() -> handleMessage(message));
        } else {
            Boolean msgs = redisTemplate.opsForValue().setIfAbsent(handleKey, "1");
            if (msgs != null && msgs) {
                redisTemplate.expire(handleKey, 15, TimeUnit.SECONDS);
                SaasGlobalThreadPool.execute(() -> handleMessage(message));
            }
        }

    }


    /**
     * 患者更换了医生。
     * @param message
     */
    public void handlePatientChangeDoctorMessage(RedisMessagePatientChangeDoctor message) {
        String tenantCode = message.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        String patientImAccount = message.getPatientImAccount();
        // 清除 患者 和 之前医生的 未读记录。
        // 清除 患者 和之前医生的 @记录。
        // 清除患者和医生之前的 消息列表
        Long oldDoctorId = message.getOldDoctorId();
        chatUserNewMsgMapper.delete(Wraps.<ChatUserNewMsg>lbQ()
                .eq(ChatUserNewMsg::getRequestId, oldDoctorId.toString())
                .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_DOCTOR)
                .eq(ChatUserNewMsg::getReceiverImAccount, patientImAccount));

        // 清除医生和 患者账号的 @记录
        chatAtRecordMapper.delete(Wraps.<ChatAtRecord>lbQ()
                .eq(ChatAtRecord::getReceiverImAccount, patientImAccount)
                .eq(ChatAtRecord::getAtUserId, oldDoctorId)
                .eq(ChatAtRecord::getUserType, UserType.UCENTER_DOCTOR));

        // 清除医生和 这些患者账号的 未读消息记录
        chatSendReadMapper.delete(Wraps.<ChatSendRead>lbQ()
                .eq(ChatSendRead::getGroupId, patientImAccount)
                .eq(ChatSendRead::getUserId, oldDoctorId)
                .eq(ChatSendRead::getRoleType, UserType.UCENTER_DOCTOR));

        Boolean changeNursing = message.getChangeNursing();
        if (changeNursing) {
            // 更换了医助
            // 清除患者和医助之前的 未读记录
            // 清除患者和医助之前的 @记录
            // 清除患者和医助的 消息列表
            Long oldNursingId = message.getOldNursingId();
            chatUserNewMsgMapper.delete(Wraps.<ChatUserNewMsg>lbQ()
                    .eq(ChatUserNewMsg::getRequestId, oldNursingId.toString())
                    .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_NURSING_STAFF)
                    .eq(ChatUserNewMsg::getReceiverImAccount, patientImAccount));

            // 清除医助和 患者账号的 @记录
            chatAtRecordMapper.delete(Wraps.<ChatAtRecord>lbQ()
                    .eq(ChatAtRecord::getReceiverImAccount, patientImAccount)
                    .eq(ChatAtRecord::getAtUserId, oldNursingId)
                    .eq(ChatAtRecord::getUserType, UserType.UCENTER_NURSING_STAFF));

            // 清除医助和 这些患者账号的 未读消息记录
            chatSendReadMapper.delete(Wraps.<ChatSendRead>lbQ()
                    .eq(ChatSendRead::getGroupId, patientImAccount)
                    .eq(ChatSendRead::getUserId, oldNursingId)
                    .eq(ChatSendRead::getRoleType, UserType.UCENTER_NURSING_STAFF));
        }


    }

    /**
     * 医生更换了医助。
     * 需要将原医助 和 这个医生的患者消息列表清除
     * @param message
     */
    public void handleDoctorChangeNursingMessage(RedisMessageDoctorChangeNursing message) {
        log.info("handleDoctorChangeNursingMessage {}", JSON.toJSONString(message));
        String tenantCode = message.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        Long doctorId = message.getDoctorId();
        R<List<Object>> doctorsPatientImAccount = patientApi.queryDoctorsPatientImAccount(doctorId);
        List<Object> imAccountData = doctorsPatientImAccount.getData();
        List<String> patientImAccount = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(imAccountData)) {
            imAccountData.forEach(item -> patientImAccount.add(item.toString()));
        } else {
            return;
        }
        Long primaryNursingId = message.getPrimaryNursingId();
        chatUserNewMsgMapper.delete(Wraps.<ChatUserNewMsg>lbQ()
                .eq(ChatUserNewMsg::getRequestId, primaryNursingId.toString())
                .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_NURSING_STAFF)
                .in(ChatUserNewMsg::getReceiverImAccount, patientImAccount));

        // 清除医助和 这些患者账号的 @记录
        chatAtRecordMapper.delete(Wraps.<ChatAtRecord>lbQ().in(ChatAtRecord::getReceiverImAccount, patientImAccount)
                .eq(ChatAtRecord::getAtUserId, primaryNursingId.toString())
                .eq(ChatAtRecord::getUserType, UserType.UCENTER_NURSING_STAFF));

        // 清除医助和 这些患者账号的 未读消息记录
        chatSendReadMapper.delete(Wraps.<ChatSendRead>lbQ()
                .in(ChatSendRead::getGroupId, patientImAccount)
                .eq(ChatSendRead::getUserId, primaryNursingId)
                .eq(ChatSendRead::getRoleType, UserType.UCENTER_NURSING_STAFF));

    }

}
