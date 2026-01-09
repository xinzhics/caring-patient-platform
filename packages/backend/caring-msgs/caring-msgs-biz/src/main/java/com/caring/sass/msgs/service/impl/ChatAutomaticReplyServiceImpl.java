package com.caring.sass.msgs.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dao.ChatAutomaticReplyMapper;
import com.caring.sass.msgs.dao.ChatMapper;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatAutomaticReply;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.msgs.service.ChatAutomaticReplyService;
import com.caring.sass.msgs.service.ChatService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-22
 */
@Slf4j
@Service

public class ChatAutomaticReplyServiceImpl extends SuperServiceImpl<ChatAutomaticReplyMapper, ChatAutomaticReply> implements ChatAutomaticReplyService {

    @Autowired
    ChatService chatService;

    @Autowired
    IMService imService;

    @Autowired
    PatientApi patientApi;

    @Override
    public boolean save(ChatAutomaticReply model) {
        model.setTenantCode(BaseContextHandler.getTenant());
        ChatAutomaticReply reply = baseMapper.selectOne(Wraps.<ChatAutomaticReply>lbQ().eq(ChatAutomaticReply::getTenantCode, BaseContextHandler.getTenant()).last(" limit 0,1 "));
        if (Objects.isNull(reply)) {
            return super.save(model);
        } else {
            model.setId(reply.getId());
            return super.updateById(model);
        }
    }

    public List<ChatAutomaticReply> queryOpenReplyTenant() {

        List<ChatAutomaticReply> replies = baseMapper.selectList(Wraps.<ChatAutomaticReply>lbQ()
                .select(ChatAutomaticReply::getTenantCode, ChatAutomaticReply::getTimeOut, ChatAutomaticReply::getContent, ChatAutomaticReply::getStatus)
                .eq(ChatAutomaticReply::getStatus, CommonStatus.YES));

        if (CollUtil.isEmpty(replies)) {
            return new ArrayList<>();
        }
        return replies;
    }

    /**
     * 自动回复消息的任务逻辑
     */
    @Override
    public void replyPatientMessage() {

        List<ChatAutomaticReply> replies = queryOpenReplyTenant();

        for (ChatAutomaticReply reply : replies) {

            String tenantCode = reply.getTenantCode();
            BaseContextHandler.setTenant(tenantCode);
            Integer timeOut = reply.getTimeOut();
            String content = reply.getContent();
            if (Objects.isNull(timeOut) || StrUtil.isEmpty(content)) {
                continue;
            }
            List<Chat> chatList = chatService.queryTimeOutChat(timeOut);
            if (CollUtil.isEmpty(chatList)) {
                continue;
            }
            Set<String> imAccount = chatList.stream().map(Chat::getReceiverImAccount).collect(Collectors.toSet());

            List<String> patientImAccounts = new ArrayList<>(imAccount);

            SaasGlobalThreadPool.execute(() -> replyPatient(patientImAccounts, content, tenantCode));
        }
    }


    /**
     * 回复给患者内容
     * @param patientImAccount
     * @param content
     * @param tenantCode
     */
    public void replyPatient(List<String> patientImAccount, String content, String tenantCode) {

        // 批量查询患者信息
        BaseContextHandler.setTenant(tenantCode);
        Chat chat;
        List<ChatSendRead> sendReads;
        List<List<String>> listList = ListUtils.subList(patientImAccount, 100);
        for (List<String> imAccounts : listList) {
            R<List<Patient>> patientByImAccounts = patientApi.findPatientByImAccounts(imAccounts);
            if (patientByImAccounts.getIsSuccess()) {
                List<Patient> patients = patientByImAccounts.getData();
                if (CollUtil.isEmpty(patients)) {
                    continue;
                }
                for (Patient patient : patients) {
                    chat = new Chat();
                    sendReads = new ArrayList<>();
                    chat.setType(MediaType.text);
                    chat.setContent(content);

                    // 设置发送人的信息
                    chat.setSenderRoleType(UserType.UCENTER_SYSTEM_IM);
                    chat.setSenderName(" ");
                    chat.setSenderAvatar("https://caring.obs.cn-north-4.myhuaweicloud.com/image/saasAiAvatar.png");

                    chat.setSystemMessage(CommonStatus.YES);
                    chat.setHistoryVisible(1);

                    // 设置患者其他冗余信息
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

                    // 创建一个患者的接受记录
                    ChatSendRead send = new ChatSendRead();
                    send.setGroupId(patient.getImAccount());
                    send.setRoleType(UserType.UCENTER_PATIENT);
                    send.setUserId(patient.getId());
                    send.setImAccount(patient.getImAccount());
                    sendReads.add(send);
                    chat.setSendReads(sendReads);
                    chat.setCreateTime(LocalDateTime.now());
                    chat.setWithdrawChatStatus(0);
                    chatService.save(chat);
                    try {
                        imService.sendImMessage(chat);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    chatService.updateStatus(patient.getImAccount());
                }
            }
        }
    }



}
