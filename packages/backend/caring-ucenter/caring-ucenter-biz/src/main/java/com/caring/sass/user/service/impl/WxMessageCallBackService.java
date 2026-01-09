package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.tenant.api.AppConfigApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.wx.message.recv.ImageMessage;
import com.caring.sass.user.dto.wx.message.recv.TextMessage;
import com.caring.sass.user.dto.wx.message.recv.VideoMessage;
import com.caring.sass.user.dto.wx.message.recv.VoiceMessage;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.wx.KeyWordApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName WxMessageCallBackService
 * @Description
 * @Author yangShuai
 * @Date 2020/9/27 16:58
 * @Version 1.0
 */
@Component
public class WxMessageCallBackService {

    @Autowired
    DoctorService doctorService;

    @Autowired
    PatientService patientService;

    @Autowired
    NursingStaffService nursingStaffService;

    @Autowired
    AppConfigApi appConfigApi;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    ImService imService;

    @Autowired
    KeyWordApi keyWordApi;

    @Autowired
    WeiXinService weiXinService;

    @Async
    public void onReceiveImageMessageEvent(ImageMessage message) {
        R<Tenant> tenant = tenantApi.getTenantByWxAppId(message.getToUserName());
        Tenant data = tenant.getData();
        if (tenant.getIsSuccess() && Objects.nonNull(data) ) {
            BaseContextHandler.setTenant(data.getCode());
        } else {
            return;
        }
        TenantOfficialAccountType accountType = data.getOfficialAccountType();
        Patient patient = patientService.findByOpenId(message.getFromUserName());
        if (patient != null) {
            NursingStaff nursingStaff = nursingStaffService.getById(patient.getServiceAdvisorId());
            Doctor doctor = doctorService.getById(patient.getDoctorId());
            if (nursingStaff != null) {
                Chat chat = new Chat();
                imService.setSenderMessage(chat, patient);

                List<ChatSendRead> chatSendReads = new ArrayList<>();
                ChatSendRead read = imService.createNursingStaffSendRead(patient.getImAccount(), nursingStaff, patient.getNursingExitChat());
                chatSendReads.add(read);
                if (doctor != null) {
                    ChatSendRead read1 = imService.createDoctorSendRead(accountType.toString(), patient.getImAccount(), doctor, patient.getDoctorExitChatListJson());
                    chatSendReads.add(read1);
                }
                chat.setSendReads(chatSendReads);
                imService.setOtherMessage(chat, patient);

                chat.setCreateTime(LocalDateTime.now());
                chat.setType(MediaType.image);
                chat.setContent(message.getPicUrl());
                chat.setHistoryVisible(1);
                imService.sendChat(chat);
            }
        }
    }

    /**
     * 用户取关
     * @param openId
     */
    public void unsubscribe(String openId) {
        Patient patient = patientService.findByOpenId(openId);
        if (Objects.nonNull(patient)) {
            // 患者取消关注
            patientService.unsubscribe(patient);
        } else {
            Doctor doctor = doctorService.findByOpenId(openId);
            if (Objects.nonNull(doctor)) {
                doctor.setWxStatus(2);
                doctorService.updateById(doctor);
            }
        }
        List<NursingStaff> nursingStaffs = nursingStaffService.list(Wraps.<NursingStaff>lbQ().eq(NursingStaff::getOpenId, openId));
        if (CollUtil.isNotEmpty(nursingStaffs)) {
            for (NursingStaff staff : nursingStaffs) {
                NursingStaff staff1 = new NursingStaff();
                staff1.setId(staff.getId());
                staff1.setWxStatus(2);
                nursingStaffService.updateById(staff1);
            }
        }
    }


    public void onReceiveTextMessageEvent(TextMessage textMessage) {
        Patient patient = patientService.findByOpenId(textMessage.getFromUserName());
        if (patient == null) {
            return;
        }

        NursingStaff nursingStaff = nursingStaffService.getById(patient.getServiceAdvisorId());
        Doctor doctor = doctorService.getById(patient.getDoctorId());
        if (nursingStaff == null) {
            return;
        }
        Chat chat = new Chat();
        imService.setOtherMessage(chat, patient);
        imService.setSenderMessage(chat, patient);
        R<String> accountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        String accountTypeData = accountType.getData();
        chat.setType(MediaType.text);
        chat.setCreateTime(LocalDateTime.now());
        chat.setContent(textMessage.getContent());
        chat.setHistoryVisible(1);
        List<ChatSendRead> sendReads = new ArrayList<>(2);
        sendReads.add(imService.createNursingStaffSendRead(patient.getImAccount(), nursingStaff, patient.getNursingExitChat()));
        if (doctor != null) {
            ChatSendRead doctorSendRead = imService.createDoctorSendRead(accountTypeData, patient.getImAccount(), doctor, patient.getDoctorExitChatListJson());
            sendReads.add(doctorSendRead);
        }
        chat.setSendReads(sendReads);
        imService.sendChat(chat);
        imService.refreshMsgStatus(patient.getId());
    }

    @Async
    public void onReceiveVideoMessageEvent(VideoMessage message) {
    }

    @Async
    public void onReceiveVoiceMessageEvent(VoiceMessage message) {

    }
}
