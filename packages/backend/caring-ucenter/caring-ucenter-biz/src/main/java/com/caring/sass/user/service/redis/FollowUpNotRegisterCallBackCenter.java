package com.caring.sass.user.service.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.entity.FollowReply;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.FollowReplyService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.impl.ImService;
import com.caring.sass.user.service.impl.WeiXinService;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 关注未注册
 */
@Slf4j
@Component
public class FollowUpNotRegisterCallBackCenter {

    private final WeiXinService weiXinService;

    private final PatientService patientService;

    private final FollowReplyService followReplyService;

    private final ImService imService;

    private final NursingStaffService nursingStaffService;

    private final ChannelContentApi channelContentApi;

    @Autowired
    TenantApi tenantApi;

    public FollowUpNotRegisterCallBackCenter(WeiXinService weiXinService,
                                             PatientService patientService,
                                             FollowReplyService followReplyService,
                                             ImService imService,
                                             NursingStaffService nursingStaffService,
                                             ChannelContentApi channelContentApi) {
        this.weiXinService = weiXinService;
        this.patientService = patientService;
        this.followReplyService = followReplyService;
        this.imService = imService;
        this.nursingStaffService = nursingStaffService;
        this.channelContentApi = channelContentApi;
    }

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(-3);
        System.out.println(localDate);
    }

    public void handleTask() {

        LocalTime localTime = LocalTime.of(LocalTime.now().getHour(), 0, 0);
        String localTimeString = localTime.toString();
        List<FollowReply> followReply = followReplyService.getFollowReply(KeyWordEnum.unregistered_reply, localTimeString, KeyWordEnum.open);
        if (CollUtil.isEmpty(followReply)) {
            return;
        }
        for (FollowReply reply : followReply) {
            String tenantCode = reply.getTenantCode();
            R<String> accountType = tenantApi.queryOfficialAccountType(tenantCode);
            String accountTypeData = accountType.getData();
            if (TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER.toString().equals(accountTypeData)) {
                SaasGlobalThreadPool.execute(() -> handleTask(reply));
            }
        }


    }


    public void handleTask(FollowReply reply) {

        String tenantCode = reply.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        Integer triggeringConditions = reply.getTriggeringConditions();
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(-triggeringConditions);
        LocalDateTime startDateTime = LocalDateTime.of(localDate, LocalTime.of(0,0,0,0));
        LocalDateTime endDateTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59,999));
        String keywordReplyForm = reply.getReplyForm();
        if (StrUtil.isEmpty(keywordReplyForm)) {
            log.info("自动回复使用的身份未知。不进行发布");
            return;
        }
        List<Patient> patients = patientService.list(Wraps.<Patient>lbQ()
                .eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE)
                .ge(SuperEntity::getCreateTime, startDateTime)
                .le(SuperEntity::getCreateTime, endDateTime));
        if (CollUtil.isEmpty(patients)) {
            return;
        }
        ChannelContent channelContent = null;
        String firstTitle = "注册提醒";
        String keyword1Title = null;
        if (KeyWordEnum.text.toString().equals(reply.getReplyType())) {
            keyword1Title = reply.getReplyContent();
        } else {
            // 查询文件内容
            R<ChannelContent> baseContent = channelContentApi.getBaseContent(Long.parseLong(reply.getReplyContent()));
            if (!baseContent.getIsSuccess()) {
                return;
            }
            channelContent = baseContent.getData();
            if (channelContent == null) {
                return;
            }
            keyword1Title = channelContent.getTitle();
        }
        List<ChatSendRead> sendReads;
        Chat chat;
        for (Patient patient : patients) {
            NursingStaff nursingStaff = null;
            if (KeyWordEnum.medical_assistance.toString().equals(keywordReplyForm)) {
                // 查询这个 患者的医助
                nursingStaff = nursingStaffService.getBaseNursingStaffById(patient.getServiceAdvisorId());
            }
            chat = new Chat();
            sendReads = new ArrayList<>();
            if (KeyWordEnum.text.toString().equals(reply.getReplyType())) {
                chat.setType(MediaType.text);
                chat.setContent(reply.getReplyContent());
            } else {
                // 是一篇文章
                chat.setType(MediaType.cms);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", channelContent.getId().toString());
                jsonObject.put("icon", channelContent.getIcon());
                jsonObject.put("title", channelContent.getTitle());
                jsonObject.put("summary", channelContent.getSummary());
                chat.setContent(jsonObject.toJSONString());
            }
            chat.setSystemMessage(1);
            chat.setHistoryVisible(1);
            // 设置im 发送人的信息
            imService.setSenderMessage(chat, keywordReplyForm, nursingStaff);

            imService.setOtherMessage(chat, patient);
            // 设置消息接收人的信息
            ChatSendRead sendRead = imService.createPatientSendRead(patient.getImAccount(), patient);
            sendReads.add(sendRead);
            chat.setSendReads(sendReads);

            imService.sendChat(chat);
            weiXinService.sendFollowUpMessage(patient.getOpenId(), tenantCode, firstTitle, keyword1Title, CommonTemplateServiceWorkModel.REGISTRATION_REMINDER, patient.getId(), KeyWordEnum.unregistered_reply);

        }

    }


}
