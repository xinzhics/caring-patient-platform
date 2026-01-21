package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.dto.ChatClearHistory;
import com.caring.sass.msgs.dto.IMUserDto;
import com.caring.sass.msgs.dto.SendAssistanceNoticeDto;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatGroupSend;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import io.swagger.models.auth.In;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName UserOtherService
 * @Description 护理医助，医生，患者 使用的 其他相关的服务聚合。
 * @Author yangShuai
 * @Date 2020/9/27 10:24
 * @Version 1.0
 */
@Component
public class ImService {

    @Autowired
    ImApi imApi;


    /**
     * 发送给医助一个系统消息
     * @param sendAssistanceNoticeDto
     * @return
     */
    public boolean sendAssistanceNotice(SendAssistanceNoticeDto sendAssistanceNoticeDto) {
        R<String> assistanceNotice = imApi.sendAssistanceNotice(sendAssistanceNoticeDto);
        if (assistanceNotice.getIsSuccess() && assistanceNotice.getData().equals("success")) {
            return true;
        } else {
            return false;
        }
    }


    public void refreshMsgStatus(Long receiverId) {
        imApi.refreshMsgStatus(receiverId);
    }


    /**
     * 清除非医生患者的未读消息记录
     * @param userId
     * @param groupIds
     */
    public void refreshDoctorMsgStatus(Long userId, List<String> groupIds ) {
        if (CollUtil.isEmpty(groupIds)) {
            // 医生没有患者。
            imApi.refreshDoctorNoReadMsgStatus(userId);
            return;
        }
        ChatClearHistory chatClearHistory = new ChatClearHistory();
        chatClearHistory.setGroupIds(groupIds);
        chatClearHistory.setRoleType(UserType.UCENTER_DOCTOR);
        chatClearHistory.setUserId(userId);
        try {
            imApi.clearChatNoReadHistoryForNoMyPatient(chatClearHistory);
        } catch (Exception e) {

        }
    }

    /**
     * @Author yangShuai
     * @Description 创建一条群发记录
     * @Date 2020/11/18 9:58
     *
     * @return void
     */
    public void createGroupChat(ChatGroupSend chatGroupSend) {
        imApi.sendGroup(chatGroupSend);
    }

    public Boolean online(String imAccount) {

        R<Boolean> booleanR = imApi.imOnline(imAccount);
        if (booleanR.getIsSuccess().equals(true)) {
            return booleanR.getData();
        } else {
            return false;
        }
    }

    public Chat queryLastMsg(Long patientId, @Length(max = 255, message = "环信账号长度不能超过255") String imAccount) {

        R<Chat> chatR = imApi.queryLastMsg(patientId, UserType.UCENTER_PATIENT, imAccount);
        return chatR.getData();

    }


    public interface ImAccountKey {
        String PATIENT = "saas_patient";
        String DOCTOR = "saas_doctor";
        String NURSING_STAFF = "saas_nursing_Staff";
    }

    /**
     * @Author yangShuai
     * @Description 注册 Im 账号
     * @Date 2020/9/27 10:28
     *
     * @return void
     */
    public String registerAccount(String imAccountKey,  Object userId) {
        String account = imAccountKey + userId;
        IMUserDto dto = new IMUserDto();
        dto.setUsername(account);
        imApi.registerAccount(dto);
        return account;
    }

    /**
     * @Author yangShuai
     * @Description 移除账号
     * @Date 2020/10/16 16:14
     *
     * @return void
     */
    public void removeAccount(String imAccount) {
        try {
            imApi.cleanChatAll(imAccount);
        } catch (Exception e) {

        }
    }

    /**
     * @Author yangShuai
     * @Description 直接发送消息
     * @Date 2020/10/16 16:51
     *
     * @return void
     */
    public Chat sendChat(Chat chat) {
        chat.setTenantCode(BaseContextHandler.getTenant());
        R<Chat> sendChat = imApi.sendChat(chat);
        chat = sendChat.getData();
        return chat;
    }

    /**
     * 设置患者其他冗余信息
     * @param chat
     * @param patient
     */
    public void setOtherMessage(Chat chat, Patient patient) {
        chat.setId(null);
        chat.setGroupId(patient.getGroupId());
        chat.setDoctorId(patient.getDoctorId());
        chat.setNursingId(patient.getServiceAdvisorId());
        chat.setDiagnosisName(patient.getDiagnosisName());
        chat.setRemark(patient.getRemark());
        chat.setPatientName(patient.getName());
        chat.setPatientId(patient.getId().toString());
        chat.setPatientAvatar(patient.getAvatar());
        chat.setDoctorId(patient.getDoctorId());
        // 消息所属的 IM小组
        chat.setReceiverImAccount(patient.getImAccount());
        chat.setReceiverId(patient.getId().toString());

    }


    /**
     * 患者是发送人时
     * @param chat
     * @param patient
     */
    public void setSenderMessage(Chat chat, Patient patient) {

        chat.setSenderAvatar(patient.getAvatar());
        chat.setSenderImAccount(patient.getImAccount());
        chat.setSenderRoleType(UserType.UCENTER_PATIENT);
        chat.setSenderId(patient.getId().toString());
        chat.setSenderName(patient.getName());

    }


    /**
     * 系统触发关键字回复时 设置 发送人的信息
     * @param chat
     * @param keywordReplyForm
     * @param nursingStaff
     */
    public void setSenderMessage(Chat chat, String keywordReplyForm, NursingStaff nursingStaff) {
        if (KeyWordEnum.medical_assistance.toString().equals(keywordReplyForm)) {
            setSenderMessage(chat, nursingStaff);
        } else {
            chat.setSenderRoleType(UserType.UCENTER_SYSTEM_IM);
            chat.setSenderName(" ");
            chat.setSenderAvatar("https://caring.obs.cn-north-4.myhuaweicloud.com/image/saasAiAvatar.png");
            //TODO: 头像 名字。需要设置
        }

    }
    /**
     * 医助是发送人时
     * @param chat
     * @param nursingStaff
     */
    public void setSenderMessage(Chat chat, NursingStaff nursingStaff) {

        chat.setSenderAvatar(nursingStaff.getAvatar());
        chat.setSenderImAccount(nursingStaff.getImAccount());
        chat.setSenderRoleType(UserType.UCENTER_NURSING_STAFF);
        chat.setSenderId(nursingStaff.getId().toString());
        chat.setSenderName(nursingStaff.getName());

    }

    /**
     * 医生是发送人时
     * @param chat
     * @param doctor
     */
    public void setSenderMessage(Chat chat, Doctor doctor) {

        chat.setSenderAvatar(doctor.getAvatar());
        chat.setSenderImAccount(doctor.getImAccount());
        chat.setSenderRoleType(UserType.UCENTER_DOCTOR);
        chat.setSenderId(doctor.getId().toString());
        chat.setSenderName(doctor.getName());

    }

    /**
     * 创建一个 医助的接收记录
     * @param groupId
     * @param nursingStaff
     */
    public ChatSendRead createNursingStaffSendRead(String groupId, NursingStaff nursingStaff, Integer nursingExitChat) {
        ChatSendRead send = new ChatSendRead();
        send.setGroupId(groupId);
        send.setRoleType(UserType.UCENTER_NURSING_STAFF);
        send.setUserId(nursingStaff.getId());
        if (nursingExitChat == null || nursingExitChat.equals(0)) {
            send.setImAccount(nursingStaff.getImAccount());
            send.setNeedSmsNotice(true);
        }
        return send;
    }

    /**
     * 创建一个 医生的接收记录
     * accountTypeData: 项目账号类型
     * @param groupId
     * @param doctor
     */
    public ChatSendRead createDoctorSendRead(String accountTypeData, String groupId, Doctor doctor, String doctorExitChatList) {
        ChatSendRead send = new ChatSendRead();
        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(accountTypeData)) {
            // 个人服务号
            if (doctor.getImMsgStatus() != null && doctor.getImMsgStatus() == 1) {
                if (StrUtil.isEmpty(doctorExitChatList) || !doctorExitChatList.contains(doctor.getId().toString())) {
                    send.setImAccount(doctor.getImAccount());
                }
            }
        } else {
            // 企业服务号 医生没有openId, 就不生成他的未读记录
            if (!StringUtils.isEmpty(doctor.getOpenId()) &&
                    doctor.getImMsgStatus() != null && doctor.getImMsgStatus() == 1) {
                if (StrUtil.isEmpty(doctorExitChatList) || !doctorExitChatList.contains(doctor.getId().toString())) {
                    send.setImAccount(doctor.getImAccount());
                }
            }
        }

        send.setGroupId(groupId);
        send.setNeedSmsNotice(true);
        send.setRoleType(UserType.UCENTER_DOCTOR);
        send.setUserId(doctor.getId());
        return send;
    }


    /**
     * 创建多个 医生的接收记录
     * @param groupId
     * @param doctorList
     */
    public List<ChatSendRead> createDoctorSendRead(String accountTypeData, String groupId, List<Doctor> doctorList, Long noDoctorId, String doctorExitChatListJson) {
        if (CollectionUtils.isEmpty(doctorList) || StringUtils.isEmpty(groupId)) {
            return new ArrayList<>();
        }
        List<ChatSendRead> sendReadList = new ArrayList<>();
        // 约定非患者的主治医生，不需要短信提醒未读消息
        for (Doctor doctor : doctorList) {
            if (noDoctorId != null && doctor.getId().equals(noDoctorId)) {
                continue;
            }
            ChatSendRead doctorSendRead = createDoctorSendRead(accountTypeData, groupId, doctor, doctorExitChatListJson);
            doctorSendRead.setNeedSmsNotice(false);
            sendReadList.add(doctorSendRead);
        }
        return sendReadList;
    }


    /**
     * 创建一个 患者的接收记录
     * @param groupId
     * @param patient
     */
    public ChatSendRead createPatientSendRead(String groupId, Patient patient) {
        ChatSendRead send = new ChatSendRead();
        send.setGroupId(groupId);
        send.setRoleType(UserType.UCENTER_PATIENT);
        send.setUserId(patient.getId());
        send.setImAccount(patient.getImAccount());
        return send;
    }

    /**
     * 异步处理 患者相关的冗余信息
     * @param tenantCode
     * @param patient
     */
    public void batchUpdatePatientForAllTable(String tenantCode, Patient patient) {
        if (patient == null) {
            return;
        }
        if (StringUtils.isEmpty(tenantCode)) {
            return;
        }
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(patient));
        imApi.batchUpdatePatientForAllTable(tenantCode, jsonObject);
    }


}
