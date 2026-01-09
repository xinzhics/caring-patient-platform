package com.caring.sass.msgs.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dao.*;
import com.caring.sass.msgs.entity.*;
import com.caring.sass.msgs.service.ChatBizService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.ImGroupDetail;
import com.caring.sass.user.dto.ImGroupMember;
import com.caring.sass.user.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ChatBizServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/12/3 10:31
 * @Version 1.0
 */
@Service
public class ChatBizServiceImpl implements ChatBizService {

    @Autowired
    ChatMapper chatMapper;

    @Autowired
    ChatUserNewMsgMapper chatUserNewMsgMapper;

    @Autowired
    ConsultationChatMapper consultationChatMapper;

    @Autowired
    PatientApi patientApi;

    @Autowired
    ChatAtRecordMapper chatAtRecordMapper;

    @Autowired
    ChatSendReadMapper chatSendReadMapper;


    @Override
    public void batchUpdatePatientForAllTable(JSONObject patient) {

        Object id = patient.get("id");
        Object name = patient.get("name");
        Object avatar = patient.get("avatar");
        Object remark = patient.get("remark");
        Object doctorRemark = patient.get("doctorRemark");
        final String patientId = "patient_id";
        if (remark != null && remark.toString().length() > 0) {
            // 修改 app对患者的备注
            UpdateWrapper<ChatUserNewMsg> appMsgNewRemark = new UpdateWrapper<>();
            appMsgNewRemark.eq(patientId, id.toString());
            appMsgNewRemark.eq("request_role_type", UserType.UCENTER_NURSING_STAFF);
            appMsgNewRemark.set("patient_remark", remark.toString());
            chatUserNewMsgMapper.update(new ChatUserNewMsg(), appMsgNewRemark);
        }
        if (doctorRemark != null && doctorRemark.toString().length() > 0) {
            // 修改 医生对患者的备注
            UpdateWrapper<ChatUserNewMsg> appMsgNewRemark = new UpdateWrapper<>();
            appMsgNewRemark.eq(patientId, id.toString());
            appMsgNewRemark.eq("request_role_type", UserType.UCENTER_DOCTOR);
            appMsgNewRemark.set("patient_remark", doctorRemark.toString());
            chatUserNewMsgMapper.update(new ChatUserNewMsg(), appMsgNewRemark);
        }

        if (Objects.isNull(id)) {
            return;
        }
        // 更新聊天记录
        UpdateWrapper<Chat> chatUpdateWrapper = new UpdateWrapper<>();
        UpdateWrapper<ChatUserNewMsg> newMsgUpdateWrapper = new UpdateWrapper<>();
        UpdateWrapper<ConsultationChat> consultationChatUpdateWrapper = new UpdateWrapper<>();

        if (Objects.nonNull(name)) {
            chatUpdateWrapper.set("sender_name", name.toString());
            newMsgUpdateWrapper.set("patient_name", name.toString());
            consultationChatUpdateWrapper.set("sender_name", name.toString());
        }

        if (Objects.nonNull(avatar)) {
            chatUpdateWrapper.set("sender_avatar", avatar.toString());
            newMsgUpdateWrapper.set("patient_avatar", avatar.toString());
            consultationChatUpdateWrapper.set("sender_avatar", avatar.toString());
        }
        chatUpdateWrapper.eq("sender_id", id.toString())
                        .eq("sender_role_type", UserType.UCENTER_PATIENT);

        newMsgUpdateWrapper.eq(patientId, id.toString());

        consultationChatUpdateWrapper.eq("sender_id", id.toString())
            .eq("sender_role_type", UserType.UCENTER_PATIENT);

        chatMapper.update(new Chat(), chatUpdateWrapper);
        chatUserNewMsgMapper.update(new ChatUserNewMsg(), newMsgUpdateWrapper);

        consultationChatMapper.update(new ConsultationChat(), consultationChatUpdateWrapper);
    }

    @Autowired
    IMService imService;

    /**
     * 撤回消息
     * @param patientId
     * @param chatId
     * @return
     */
    @Override
    public Boolean withdrawChat(Long patientId, Long chatId) {

        Chat chat = chatMapper.selectById(chatId);
        chat.setWithdrawChatStatus(1);
        chat.setWithdrawChatUserId(Long.parseLong(chat.getSenderId()));
        R<ImGroupDetail> groupDetail = patientApi.getPatientGroupDetail(patientId);
        ImGroupDetail data = groupDetail.getData();
        Patient patient = data.getPatient();
        List<ImGroupMember> groupMembers = data.getGroupMembers();
        List<String> imAccount = new ArrayList<>();
        imAccount.add(patient.getImAccount());
        for (ImGroupMember groupMember : groupMembers) {
            imAccount.add(groupMember.getImAccount());
        }
        // 更新消息为 撤回。
        chatMapper.updateById(chat);
        // 清除这条消息的未读记录。和@记录
        chatAtRecordMapper.delete(Wraps.<ChatAtRecord>lbQ().eq(ChatAtRecord::getChatId, chatId));
        chatSendReadMapper.delete(Wraps.<ChatSendRead>lbQ().eq(ChatSendRead::getChatId, chatId));
        Long userId = BaseContextHandler.getUserId();
        String userType = BaseContextHandler.getUserType();
        updateCharUserNew(userId, userType, chatId, true);
        imService.sendWithdrawChat(imAccount, chatId, chat.getSenderImAccount(), chat.getSenderId(),
                chat.getSenderName(), chat.getSenderRoleType(), chat.getSenderAvatar(), chat.getReceiverImAccount());
        return true;
    }

    /**
     * 删除或撤回之后，处理改消息在最新消息列表上的状态
     * @param userId 操作人的ID
     * @param userType 操作人的角色
     * @param chatId 被操作的消息ID
     * @param all 是否和消息相关的都处理
     */
    public void updateCharUserNew(Long userId, String userType, Long chatId, boolean all) {
        List<ChatUserNewMsg> chatUserNewMsgs;
        if (all) {
            LbqWrapper<ChatUserNewMsg> lbqWrapper = Wraps.<ChatUserNewMsg>lbQ().eq(ChatUserNewMsg::getChatId, chatId);
            chatUserNewMsgs = chatUserNewMsgMapper.selectList(lbqWrapper);
        } else {
            chatUserNewMsgs = new ArrayList<>();
            if (UserType.NURSING_STAFF.equals(userType)) {
                // 更换医助列表列表中 最新消息
                ChatUserNewMsg chatUserNewMsg = chatUserNewMsgMapper.selectOne(Wraps.<ChatUserNewMsg>lbQ()
                        .eq(ChatUserNewMsg::getChatId, chatId)
                        .eq(ChatUserNewMsg::getRequestId, userId.toString())
                        .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_NURSING_STAFF)
                        .last(" limit 0, 1 "));
                if (Objects.nonNull(chatUserNewMsg)) {
                    chatUserNewMsgs.add(chatUserNewMsg);
                }
            } else if (UserType.DOCTOR.equals(userType)) {
                // 更换医助列表列表中 最新消息
                ChatUserNewMsg chatUserNewMsg = chatUserNewMsgMapper.selectOne(Wraps.<ChatUserNewMsg>lbQ()
                        .eq(ChatUserNewMsg::getChatId, chatId)
                        .eq(ChatUserNewMsg::getRequestId, userId.toString())
                        .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_DOCTOR)
                        .last(" limit 0, 1 "));
                if (Objects.nonNull(chatUserNewMsg)) {
                    chatUserNewMsgs.add(chatUserNewMsg);
                }
            }
        }
        // 切换这个用户的 最新消息列表中的消息
        if (CollUtil.isNotEmpty(chatUserNewMsgs)) {
            for (ChatUserNewMsg userNewMsg : chatUserNewMsgs) {
                String receiverImAccount = userNewMsg.getReceiverImAccount();
                Chat chat = chatMapper.selectOne(Wraps.<Chat>lbQ()
                        .eq(Chat::getReceiverImAccount, receiverImAccount)
                        .eq(Chat::getWithdrawChatStatus, 0)
                        .ne(Chat::getId, chatId)
                        .notLike(Chat::getDeleteThisMessageUserIdJsonArrayString, userId)
                        .orderByDesc(Chat::getCreateTime)
                        .last(" limit 0, 1"));
                if (Objects.nonNull(chat)) {
                    userNewMsg.setChatId(chat.getId());
                    chatUserNewMsgMapper.updateById(userNewMsg);
                } else {
                    userNewMsg.setChatId(null);
                    chatUserNewMsgMapper.updateAllById(userNewMsg);
                }
            }
        }
    }

    /**
     * 更新这条消息为 删除
     * @param userId
     * @param chatId
     * @return
     */
    @Override
    public Boolean updateDeleteStatusChat(Long userId, Long chatId) {
        Chat chat = chatMapper.selectById(chatId);
        String arrayString = chat.getDeleteThisMessageUserIdJsonArrayString();
        JSONArray jsonArray;
        if (StrUtil.isEmpty(arrayString)) {
            jsonArray = new JSONArray();
        } else {
            jsonArray = JSONArray.parseArray(arrayString);
        }
        if (!jsonArray.contains(userId.toString())) {
            jsonArray.add(userId.toString());
        }
        chat.setDeleteThisMessageUserIdJsonArrayString(jsonArray.toJSONString());
        chatMapper.updateById(chat);
        String userType = BaseContextHandler.getUserType();
        // 切换这个用户的 最新消息列表中的消息
        updateCharUserNew(userId, userType, chatId, false);
        return true;
    }

    /**
     * 删除某个会话
     * @param chatNewMsgId
     */
    @Override
    public void deleteChatMsg(Long chatNewMsgId) {
        chatUserNewMsgMapper.deleteById(chatNewMsgId);
    }

}
