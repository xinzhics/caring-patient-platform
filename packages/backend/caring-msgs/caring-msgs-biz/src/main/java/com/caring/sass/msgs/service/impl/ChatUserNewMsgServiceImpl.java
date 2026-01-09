package com.caring.sass.msgs.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.dao.ChatAtRecordMapper;
import com.caring.sass.msgs.dao.ChatMapper;
import com.caring.sass.msgs.dao.ChatSendReadMapper;
import com.caring.sass.msgs.dao.ChatUserNewMsgMapper;
import com.caring.sass.msgs.dto.ChatDoctorSharedMsgDTO;
import com.caring.sass.msgs.dto.ChatUserNewMsgPageDTO;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatAtRecord;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.msgs.entity.ChatUserNewMsg;
import com.caring.sass.msgs.service.ChatUserNewMsgService;
import com.caring.sass.tenant.api.TenantApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ChatUserNewMsgServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2020/12/23 17:06
 * @Version 1.0
 */
@Slf4j
@Service
public class ChatUserNewMsgServiceImpl  extends SuperServiceImpl<ChatUserNewMsgMapper, ChatUserNewMsg> implements ChatUserNewMsgService {

    @Autowired
    ChatUserNewMsgMapper chatUserNewMsgMapper;

    @Autowired
    ChatSendReadMapper chatSendReadMapper;

    @Autowired
    ChatMapper chatMapper;

    @Autowired
    IMService imService;

    @Autowired
    ChatAtRecordMapper chatAtRecordMapper;

    @Autowired
    TenantApi tenantApi;

    @Override
    public <E extends IPage<ChatUserNewMsg>> E page(E page, Wrapper<ChatUserNewMsg> queryWrapper) {

        E selectPage = chatUserNewMsgMapper.selectPage(page, queryWrapper);
        List<ChatUserNewMsg> records = selectPage.getRecords();
        records.forEach(chatUserNewMsg -> {
                    chatUserNewMsg.setChat(chatMapper.selectById(chatUserNewMsg.getChatId()));
                chatUserNewMsg.setNoReadTotal(countDoctorNoRead(chatUserNewMsg));
        });
        selectPage.setRecords(records);
        return selectPage;
    }

    @Override
    public ChatUserNewMsg findOne(Long id) {
        ChatUserNewMsg userNewMsg = chatUserNewMsgMapper.selectById(id);
        if (Objects.nonNull(userNewMsg.getChatId())) {
            Chat chat = chatMapper.selectById(userNewMsg.getChatId());
            userNewMsg.setChat(chat);
            userNewMsg.setNoReadTotal(countDoctorNoRead(userNewMsg));
        }
        return userNewMsg;
    }

    @Override
    public ChatUserNewMsg findChatUserNewMsg(String receiverImAccount) {
        String userType = BaseContextHandler.getUserType();
        Long userId = BaseContextHandler.getUserId();
        ChatUserNewMsg chatUserNewMsg;
        // 登录人是医助
        if (UserType.NURSING_STAFF.equals(userType)) {
            chatUserNewMsg = chatUserNewMsgMapper.selectOne(Wraps.<ChatUserNewMsg>lbQ()
                    .eq(ChatUserNewMsg::getReceiverImAccount, receiverImAccount)
                    .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_NURSING_STAFF)
                    .eq(ChatUserNewMsg::getRequestId, userId.toString())
                    .last(" limit 0, 1"));
            // 当前登陆人是医生
        } else if (UserType.DOCTOR.equals(userType)) {
            chatUserNewMsg = chatUserNewMsgMapper.selectOne(Wraps.<ChatUserNewMsg>lbQ()
                    .eq(ChatUserNewMsg::getReceiverImAccount, receiverImAccount)
                    .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_DOCTOR)
                    .eq(ChatUserNewMsg::getRequestId, userId.toString())
                    .last(" limit 0, 1"));
        } else {
            throw new BizException("当前登陆人角色未找到。");
        }
        if (Objects.nonNull(chatUserNewMsg)) {
            if (Objects.nonNull(chatUserNewMsg.getChatId())) {
                Chat chat = chatMapper.selectById(chatUserNewMsg.getChatId());
                chatUserNewMsg.setChat(chat);
                chatUserNewMsg.setNoReadTotal(countDoctorNoRead(chatUserNewMsg));
            }
        }
        return chatUserNewMsg;
    }

    /**
     * 移除消息列表中 医助 或 医生和患者的记录
     * @param receiverImAccount
     * @param requestId
     * @param requestRoleType
     * @return
     */
    @Override
    public Boolean removeChatUserNewMsg(String receiverImAccount, String requestId, String requestRoleType) {
        LbqWrapper<ChatUserNewMsg> wrapper = Wraps.<ChatUserNewMsg>lbQ().eq(ChatUserNewMsg::getReceiverImAccount, receiverImAccount)
                .eq(ChatUserNewMsg::getRequestId, requestId)
                .eq(ChatUserNewMsg::getRequestRoleType, requestRoleType);
        chatUserNewMsgMapper.delete(wrapper);
        return true;
    }

    /**
     * 统计有多少条未读消息
     * @param chatUserNewMsg
     * @return
     */
    public int countDoctorNoRead(ChatUserNewMsg chatUserNewMsg) {
        LbqWrapper<ChatSendRead> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChatSendRead::getGroupId, chatUserNewMsg.getReceiverImAccount());
        lbqWrapper.eq(ChatSendRead::getUserId, Long.parseLong(chatUserNewMsg.getRequestId()));
        lbqWrapper.eq(ChatSendRead::getRoleType, chatUserNewMsg.getRequestRoleType());
        lbqWrapper.eq(ChatSendRead::getIs_delete, CommonStatus.NO);
        Integer integer = chatSendReadMapper.selectCount(lbqWrapper);
        if (integer == null) {
            return 0;
        }
        return integer;
    }

    /**
     * 创建护理专员的一条新消息记录
     * @param chat
     * @param nursingId
     * @param receiverImAccount
     */
    public ChatUserNewMsg createNursingChatNewMsg(Chat chat, Long nursingId, String receiverImAccount) {
        ChatUserNewMsg chatUserNewMsg = new ChatUserNewMsg();
        chatUserNewMsg.setRequestId(nursingId.toString());
        chatUserNewMsg.setRequestRoleType(UserType.UCENTER_NURSING_STAFF);
        chatUserNewMsg.setChatId(chat.getId());
        chatUserNewMsg.setPatientName(chat.getPatientName());
        chatUserNewMsg.setReceiverImAccount(receiverImAccount);
        chatUserNewMsg.setPatientId(Long.parseLong(chat.getPatientId()));
        chatUserNewMsg.setPatientAvatar(chat.getPatientAvatar());
        chatUserNewMsg.setPatientRemark(chat.getRemark());
        chatUserNewMsg.setUpdateTime(chat.getCreateTime());
        chatUserNewMsg.setCreateTime(chat.getCreateTime());
        chatUserNewMsgMapper.insert(chatUserNewMsg);
        return chatUserNewMsg;
    }

    /**
     * 创建 医生 的一条新消息记录
     * @param chat
     * @param doctorId
     * @param receiverImAccount
     */
    public ChatUserNewMsg createDoctorChatNewMsg(Chat chat, Long doctorId, String receiverImAccount) {
        ChatUserNewMsg chatUserNewMsg = new ChatUserNewMsg();
        chatUserNewMsg.setRequestId(doctorId.toString());
        chatUserNewMsg.setRequestRoleType(UserType.UCENTER_DOCTOR);
        chatUserNewMsg.setChatId(chat.getId());
        chatUserNewMsg.setPatientName(chat.getPatientName());
        chatUserNewMsg.setReceiverImAccount(receiverImAccount);
        chatUserNewMsg.setPatientId(Long.parseLong(chat.getPatientId()));
        chatUserNewMsg.setPatientAvatar(chat.getPatientAvatar());
        chatUserNewMsg.setPatientRemark(chat.getDoctorRemark());
        chatUserNewMsg.setUpdateTime(chat.getCreateTime());
        chatUserNewMsg.setCreateTime(chat.getCreateTime());
        chatUserNewMsgMapper.insert(chatUserNewMsg);
        return chatUserNewMsg;
    }

    /**
     * 更新 医生和专员的 患者聊天列表最新消息
     * @param chat
     * @param doctorId
     * @param nursingId
     * @param receiverImAccount
     * @param tenantCode
     * @param chatAtRecords
     */
    @Override
    public void updateMsg(Chat chat, Long doctorId, Long nursingId, String receiverImAccount,
                          String tenantCode, List<ChatAtRecord> chatAtRecords) {
        try {
            if (doctorId == null && nursingId == null) {
                return;
            }
            BaseContextHandler.setTenant(tenantCode);
            ChatUserNewMsg doctorNewMsg = null;
            ChatUserNewMsg nursingStaffNewMsg = null;
            LbqWrapper<ChatUserNewMsg> lbqWrapper = new LbqWrapper<>();
            // 查找 receiverImAccount 账号对应的最新消息记录 (可能是多条， 一条是医生的，一条是专员的)
            lbqWrapper.eq(ChatUserNewMsg::getReceiverImAccount, receiverImAccount);
            List<ChatUserNewMsg> chatUserNewMsgs = chatUserNewMsgMapper.selectList(lbqWrapper);
            if (CollectionUtils.isEmpty(chatUserNewMsgs)) {
                chatUserNewMsgs = new ArrayList<>();
            }
            boolean nursing = true;
            boolean doctor = true;
            for (ChatUserNewMsg newMsg : chatUserNewMsgs) {
                newMsg.setCreateTime(chat.getCreateTime());
                newMsg.setUpdateTime(chat.getCreateTime());
                newMsg.setPatientAvatar(chat.getPatientAvatar());
                newMsg.setPatientRemark(chat.getRemark());
                newMsg.setPatientName(chat.getPatientName());
                newMsg.setChatId(chat.getId());
                if (UserType.UCENTER_NURSING_STAFF.equals(newMsg.getRequestRoleType())) {
                    if (nursingId != null) {
                        newMsg.setRequestId(nursingId.toString());
                    }
                    nursingStaffNewMsg = newMsg;
                    nursing = false;
                } else if (UserType.UCENTER_DOCTOR.equals(newMsg.getRequestRoleType())) {
                    if (doctorId != null) {
                        newMsg.setRequestId(doctorId.toString());
                    }
                    doctorNewMsg = newMsg;
                    doctor = false;
                }
                chatUserNewMsgMapper.updateById(newMsg);
            }
            if (nursing && nursingId != null) {
                nursingStaffNewMsg = createNursingChatNewMsg(chat, nursingId, receiverImAccount);
            }
            if (doctor && doctorId != null) {
                doctorNewMsg = createDoctorChatNewMsg(chat, doctorId, receiverImAccount);
            }
            if (CollUtil.isNotEmpty(chatAtRecords)) {
                for (ChatAtRecord atRecord : chatAtRecords) {
                    atRecord.setChatId(chat.getId());
                    atRecord.setReceiverImAccount(receiverImAccount);
                }
                Map<String, List<ChatAtRecord>> groupByUserType = chatAtRecords.stream().collect(Collectors.groupingBy(ChatAtRecord::getUserType));
                // 处理 @ 医生的记录
                if (Objects.nonNull(doctorNewMsg)) {
                    List<ChatAtRecord> recordList = groupByUserType.get(UserType.UCENTER_DOCTOR);
                    if (CollUtil.isNotEmpty(recordList)) {
                        for (ChatAtRecord atRecord : recordList) {
                            atRecord.setChatUserNewMsgId(doctorNewMsg.getId());
                        }
                    }
                }

                // 处理 @ 专员的记录
                if (Objects.nonNull(nursingStaffNewMsg)) {
                    List<ChatAtRecord> atRecordList = groupByUserType.get(UserType.UCENTER_NURSING_STAFF);
                    if (CollUtil.isNotEmpty(atRecordList)) {
                        for (ChatAtRecord atRecord : atRecordList) {
                            atRecord.setChatUserNewMsgId(nursingStaffNewMsg.getId());
                        }
                    }
                }
                chatAtRecordMapper.insertBatchSomeColumn(chatAtRecords);
            }
        } catch (Exception e) {
        }
    }

    /**
     * @Author yangShuai
     * @Description 清空患者的 聊天记录，im账号
     * @Date 2021/1/5 9:58
     *
     * @return void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByPatientImAccount(String patientImAccount) {
        try {
            imService.delete(patientImAccount);
        } catch (Exception e) {

        }
        LbqWrapper<ChatUserNewMsg> wrapper = new LbqWrapper<> ();
        wrapper.eq(ChatUserNewMsg::getReceiverImAccount, patientImAccount);
        chatUserNewMsgMapper.delete(wrapper);

        LbqWrapper<Chat> chatLbqWrapper = new LbqWrapper<> ();
        chatLbqWrapper.eq(Chat::getReceiverImAccount, patientImAccount);
        chatMapper.delete(chatLbqWrapper);

        LbqWrapper<ChatSendRead> chatSendReadLbqWrapper = new LbqWrapper<> ();
        chatSendReadLbqWrapper.eq(ChatSendRead::getGroupId, patientImAccount);
        chatSendReadMapper.delete(chatSendReadLbqWrapper);
    }


    /**
     * 根据 医生的未读记录。生成医生的 待办消息列表
     * @param buildPage
     * @param doctorIds
     * @param paramsModel
     * @return
     */
    @Override
    public IPage<ChatUserNewMsg> doctorDealtWith(IPage<ChatUserNewMsg> buildPage, List<String> doctorIds, ChatDoctorSharedMsgDTO paramsModel) {

        String idsJoin = ListUtils.getSqlIdsJoin(doctorIds);
        long current = buildPage.getCurrent();
        long size = buildPage.getSize();
        List<String> groupIds = chatSendReadMapper.distinctGroupId(idsJoin, UserType.UCENTER_DOCTOR, " limit " + (current - 1) * size + "," + current * size);

        List<ChatUserNewMsg> userNewMsgs = chatUserNewMsgMapper.selectList(Wraps.<ChatUserNewMsg>lbQ()
                .eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_DOCTOR)
                .in(ChatUserNewMsg::getRequestId, doctorIds)
                .in(ChatUserNewMsg::getReceiverImAccount, groupIds));
        if (CollUtil.isEmpty(userNewMsgs)) {
            return buildPage;
        }
        // 根据ID列表对数据排序
        Map<String, ChatUserNewMsg> receiverImAccountMap = userNewMsgs.stream().collect(Collectors.toMap(ChatUserNewMsg::getReceiverImAccount, item -> item));
        Map<Long, ChatUserNewMsg> newMsgMap = userNewMsgs.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
        List<ChatUserNewMsg> resultNewMsgs = new ArrayList<>(userNewMsgs.size());
        List<Long> msgIds = new ArrayList<>();
        for (String groupId : groupIds) {
            ChatUserNewMsg newMsg = receiverImAccountMap.get(groupId);
            if (Objects.isNull(newMsg)) {
                continue;
            }
            resultNewMsgs.add(newMsg);
            msgIds.add(newMsg.getId());
        }
        // 设置哪些 列表里面有被 @ 的消息
        setAtChatMsg(msgIds, newMsgMap, paramsModel.getRequestId(), UserType.UCENTER_DOCTOR);

        // 设置未读统计等
        setNoReadTotal(resultNewMsgs, paramsModel.getRequestId());

        buildPage.setRecords(resultNewMsgs);
        return buildPage;
    }


    /**
     * 添加了at后的消息列表查询
     * @return
     */
    @Override
    public IPage<ChatUserNewMsg> page(IPage<ChatUserNewMsg> buildPage, Map<String, LocalDateTime> data,
                                      List<String> doctorIds, ChatDoctorSharedMsgDTO paramsModel, List<Long> doctorExitChatPatientIds) {



        // 先查询 符合条件的 ChatUserNewMsg id 列表。
        StringBuilder sql = new StringBuilder();
        StringBuilder pageSql = new StringBuilder();
        LbqWrapper<ChatUserNewMsg> wrapper = Wraps.<ChatUserNewMsg>lbQ()
                .eq(ChatUserNewMsg::getRequestRoleType, "doctor");

        sql.append(" un.request_role_type = 'doctor' ");
        if (CollUtil.isNotEmpty(doctorIds)) {
            String idsJoin = ListUtils.getSqlIdsJoin(doctorIds);
            wrapper.in(ChatUserNewMsg::getRequestId, doctorIds);
            sql.append(" and un.request_id in (").append(idsJoin).append(")");
        }
        String patientName = paramsModel.getPatientName();
        if (StringUtils.isNotEmptyString(patientName)) {
            wrapper.like(ChatUserNewMsg::getPatientName, paramsModel);
            sql.append(" and un.patient_name like '%").append(patientName).append("%'");
        }
        if (CollUtil.isNotEmpty(doctorExitChatPatientIds)) {
            String idsJoin = ListUtils.getSqlIdsJoin(doctorExitChatPatientIds);
            sql.append(" and un.patient_id not in (").append(idsJoin).append(")");
        }
        sql.append(" ORDER BY unread.create_time desc, ar.create_time desc, un.create_time desc");

        long current = buildPage.getCurrent();
        long size = buildPage.getSize();
        if (current == 0) {
            current = 1;
        }
        if (size == 0) {
            size = 20;
        }
        pageSql.append(" limit ").append((current - 1) * size ).append(",").append(size);
        Integer count = baseMapper.selectCount(wrapper);
        List<Long> msgIds = baseMapper.distinctIdForChatMsg(paramsModel.getRequestId(), UserType.UCENTER_DOCTOR ,sql.toString(), pageSql.toString());
        buildPage.setTotal(count);
        buildPage.setSize(size);
        buildPage.setCurrent(current);
        if (CollUtil.isEmpty(msgIds)) {
            return buildPage;
        }
        // 根据ID 列表查询 数据详细。
        List<ChatUserNewMsg> userNewMsgs = chatUserNewMsgMapper.selectList(Wraps.<ChatUserNewMsg>lbQ().in(SuperEntity::getId, msgIds));
        if (CollUtil.isEmpty(userNewMsgs)) {
            return buildPage;
        }
        // 根据ID列表对数据排序
        Map<Long, ChatUserNewMsg> newMsgMap = userNewMsgs.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
        List<ChatUserNewMsg> resultNewMsgs = new ArrayList<>(userNewMsgs.size());
        for (Long msgId : msgIds) {
            if (Objects.isNull(newMsgMap.get(msgId))) {
                continue;
            }
            resultNewMsgs.add(newMsgMap.get(msgId));
        }
        // 设置哪些 列表里面有被 @ 的消息
        setAtChatMsg(msgIds, newMsgMap, paramsModel.getRequestId(), UserType.UCENTER_DOCTOR);

        // 设置未读统计等
        setNewChatAndTotalNoRead(resultNewMsgs, data, paramsModel.getRequestId());
        buildPage.setRecords(resultNewMsgs);
        return buildPage;
    }

    @Override
    public IPage<ChatUserNewMsg> page(IPage<ChatUserNewMsg> buildPage, ChatUserNewMsgPageDTO paramsModel) {

        StringBuilder sql = new StringBuilder();
        String requestId = paramsModel.getRequestId();
        LbqWrapper<ChatUserNewMsg> lbqWrapper = Wraps.<ChatUserNewMsg>lbQ().eq(ChatUserNewMsg::getRequestId, requestId);
        String userType = UserType.UCENTER_NURSING_STAFF;
        sql.append(" un.request_id = '").append(requestId).append("' ");
        if (StringUtils.isEmpty(paramsModel.getRequestRoleType())) {
            lbqWrapper.eq(ChatUserNewMsg::getRequestRoleType, userType);
            sql.append(" and un.request_role_type = '").append(userType).append("' ");
        } else {
            userType = paramsModel.getRequestRoleType();
            sql.append(" and un.request_role_type = '").append(paramsModel.getRequestRoleType()).append("' ");
            lbqWrapper.eq(ChatUserNewMsg::getRequestRoleType, paramsModel.getRequestRoleType());
        }
        long current = buildPage.getCurrent();
        long size = buildPage.getSize();
        if (current == 0) {
            current = 1;
        }
        if (size == 0) {
            size = 20;
        }
        String tenant = BaseContextHandler.getTenant();
        String keyWord = paramsModel.getPatientName();
        if (StringUtils.isNotEmptyString(keyWord)) {
            // 根据患者名称。扩展搜索。
            // 查询医助下患者的IM账号并且聊天内容中包含关键词词的聊天记录的群组ID
            List<String> receiverImAccounts = chatMapper.selectReceiverImAccount(tenant, requestId, "%"+ keyWord +"%");
            // 根据条件没有查询到 患者IM账号
            if (receiverImAccounts.isEmpty()) {
                buildPage.setTotal(0);
                buildPage.setSize(size);
                buildPage.setCurrent(current);
                return buildPage;
            }
            String sqlIdsJoin = ListUtils.getSqlIdsJoin(receiverImAccounts);
            // 列表数据中查询拼接群组ID查询条件
            sql.append(" and un.receiver_im_account in (").append(sqlIdsJoin).append(")");

            // 统计语句中拼接IM群组id查询条件
            lbqWrapper.in(ChatUserNewMsg::getReceiverImAccount,receiverImAccounts);
        }
        if (Objects.nonNull(paramsModel.getDoctorId())) {
            sql.append(" and un.patient_id in (select id from u_user_patient where doctor_id = ").append(paramsModel.getDoctorId()).append(")");
            lbqWrapper.apply(" patient_id in (select id from u_user_patient where doctor_id = " + paramsModel.getDoctorId() + ")");
        } else if (Objects.nonNull(paramsModel.getGroupId())) {
            sql.append(" and un.patient_id in (select id from u_user_patient where doctor_id in (select doctor_id from u_user_doctor_group where group_id = ").append(paramsModel.getGroupId()).append("))");
            lbqWrapper.apply(" patient_id in (select id from u_user_patient where doctor_id in (select doctor_id from u_user_doctor_group where group_id =" + paramsModel.getGroupId() + "))");
        }
        sql.append(" ORDER BY unread.create_time desc, ar.create_time desc, un.create_time desc");

        sql.append(" limit ").append((current - 1) * size ).append(",").append(size);
        Integer count = baseMapper.selectCount(lbqWrapper);
        if (count != null && count == 0) {
            buildPage.setTotal(0);
            buildPage.setSize(size);
            buildPage.setCurrent(current);
            return buildPage;
        }
        List<Long> msgIds = baseMapper.distinctIdForChatMsg(Long.parseLong(requestId), UserType.UCENTER_NURSING_STAFF, sql.toString(), "");
        buildPage.setTotal(count);
        buildPage.setSize(size);
        buildPage.setCurrent(current);
        if (CollUtil.isEmpty(msgIds)) {
            return buildPage;
        }
        // 根据ID 列表查询 数据详细。
        List<ChatUserNewMsg> userNewMsgs = chatUserNewMsgMapper.selectList(Wraps.<ChatUserNewMsg>lbQ().in(SuperEntity::getId, msgIds));
        if (CollUtil.isEmpty(userNewMsgs)) {
            return buildPage;
        }
        // 根据ID列表对数据排序
        Map<Long, ChatUserNewMsg> newMsgMap = userNewMsgs.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
        List<ChatUserNewMsg> resultNewMsgs = new ArrayList<>(userNewMsgs.size());
        for (Long msgId : msgIds) {
            if (newMsgMap.get(msgId) == null) {
                continue;
            }
            resultNewMsgs.add(newMsgMap.get(msgId));
        }

        // 设置哪些 列表里面有被 @ 的消息
        setAtChatMsg(msgIds, newMsgMap, Long.parseLong(requestId), userType);

        resultNewMsgs.forEach(chatUserNewMsg -> {
            if (chatUserNewMsg.getChatId() != null) {
                chatUserNewMsg.setChat(chatMapper.selectById(chatUserNewMsg.getChatId()));
            }
            chatUserNewMsg.setNoReadTotal(countDoctorNoRead(chatUserNewMsg));
        });
        buildPage.setRecords(resultNewMsgs);
        return buildPage;
    }


    /**
     * 检查哪些消息列表中 存在被 at的记录
     * @param chatUserNewMsgIds
     * @param newMsgMap
     * @param atUserId
     * @param userType
     */
    private void setAtChatMsg(List<Long> chatUserNewMsgIds, Map<Long, ChatUserNewMsg> newMsgMap, Long atUserId, String userType) {

        LbqWrapper<ChatAtRecord> wrapper = Wraps.<ChatAtRecord>lbQ().in(ChatAtRecord::getChatUserNewMsgId, chatUserNewMsgIds)
                .eq(ChatAtRecord::getUserType, userType)
                .eq(ChatAtRecord::getAtUserId, atUserId)
                .orderByDesc(ChatAtRecord::getCreateTime);
        List<ChatAtRecord> recordList = chatAtRecordMapper.selectList(wrapper);
        if (CollUtil.isEmpty(recordList)) {
            return;
        }
        ChatUserNewMsg userNewMsg;
        for (ChatAtRecord atRecord : recordList) {
            userNewMsg = newMsgMap.get(atRecord.getChatUserNewMsgId());
            userNewMsg.addChatAtRecords(atRecord);
            newMsgMap.put(atRecord.getChatUserNewMsgId(), userNewMsg);
        }


    }


    /**
     * 给医生待办的消息列表添加未读消息数量和 最新消息内容
     * @param records
     * @param doctorId
     */
    private void setNoReadTotal(List<ChatUserNewMsg> records, Long doctorId) {

        // 确定 这些 消息列表都属于 哪些群组 receiverImAccount， 然后便于统计 每个群组医生的未读消息数量
        List<String> imGorupIds = new ArrayList<>(records.size());

        // 确定 医生的入组时间是否在 消息列表的早更新时间之前。 否则医生不可查看消息列表关联的最新消息
        // 需要查询的 消息ids
        List<Long> allChatIds = new ArrayList<>(records.size());
        for (ChatUserNewMsg record : records) {
            String receiverImAccount = record.getReceiverImAccount();
            imGorupIds.add(receiverImAccount);
            allChatIds.add(record.getChatId());
        }

        // 查看 chat 并将医生不见的 content 修改为 null
        Map<Long, Chat> chatMap = new HashMap<>(records.size());
        if ( CollectionUtils.isNotEmpty(allChatIds)) {
            List<Chat> chatList = chatMapper.selectList(Wraps.<Chat>lbQ().in(SuperEntity::getId, allChatIds)
                    .select(Chat::getDiagnosisName, Chat::getContent, Chat::getType, SuperEntity::getId));
            if (CollectionUtils.isNotEmpty(chatList)) {
                chatMap = chatList.stream().collect(Collectors.toMap(Chat::getId, item -> item));
            }
        }

        // 统计 医生在 imGorupIds 这些群组中的未读记录
        Map<String, Integer> countNoReadNumber = new HashMap<>(records.size());
        List<Map<String, Object>> imGroupIdCount = chatSendReadMapper.selectMaps(Wrappers.<ChatSendRead>query()
                .select("group_id as groupId", "count(*) as total")
                .groupBy("group_id")
                .in("group_id", imGorupIds)
                .eq("user_id", doctorId)
                .eq("is_delete", CommonStatus.NO)
                .eq("role_type", UserType.UCENTER_DOCTOR));
        if (CollectionUtils.isNotEmpty(imGroupIdCount)) {
            for (Map<String, Object> objectMap : imGroupIdCount) {
                Object groupId = objectMap.get("groupId");
                Object total = objectMap.get("total");
                if (total != null && groupId != null) {
                    countNoReadNumber.put(groupId.toString(), Convert.toInt(total));
                }
            }
        }
        for (ChatUserNewMsg record : records) {
            record.setChat(chatMap.get(record.getChatId()));
            record.setNoReadTotal(countNoReadNumber.get(record.getReceiverImAccount()));
        }
    }

    /**
     * 查询最新的可见消息记录
     * 统计群组中的未读数量
     */
    private void setNewChatAndTotalNoRead(List<ChatUserNewMsg> records, Map<String, LocalDateTime> data, Long doctorId) {
        // 确定 这些 消息列表都属于 哪些群组 receiverImAccount， 然后便于统计 每个群组医生的未读消息数量
        List<String> imGorupIds = new ArrayList<>(records.size());

        // 确定 医生的入组时间是否在 消息列表的早更新时间之前。 否则医生不可查看消息列表关联的最新消息

        // 医生可以看到content的消息ids
        Set<Long> readChatIds = new HashSet<>(records.size());
        // 需要查询的 消息ids
        List<Long> allChatIds = new ArrayList<>(records.size());
        for (ChatUserNewMsg record : records) {
            String receiverImAccount = record.getReceiverImAccount();
            imGorupIds.add(receiverImAccount);
            allChatIds.add(record.getChatId());
            if (CollUtil.isNotEmpty(data)) {
                if (StringUtils.isNotEmptyString(record.getRequestId())) {
                    LocalDateTime dateTime = data.get(record.getRequestId());
                    if (Objects.nonNull(dateTime)) {
                        // 入组时间在 最新消息创建之前
                        if (dateTime.isBefore(record.getUpdateTime())) {
                            readChatIds.add(record.getChatId());
                        }
                    }
                }
            }
        }

        // 查看 chat 并将医生不见的 content 修改为 null
        Map<Long, Chat> chatMap = new HashMap<>(records.size());
        if ( CollectionUtils.isNotEmpty(allChatIds)) {
            List<Chat> chatList = chatMapper.selectList(Wraps.<Chat>lbQ().in(SuperEntity::getId, allChatIds)
                    .select(Chat::getDiagnosisName, Chat::getContent, Chat::getType, SuperEntity::getId));
            if (CollectionUtils.isNotEmpty(chatList)) {
                for (Chat chat : chatList) {
                    // 消息内容医生是否可见
                    if (CollUtil.isNotEmpty(data) && !readChatIds.contains(chat.getId())) {
                        chat.setContent(null);
                    }
                    chatMap.put(chat.getId(), chat);
                }
            }
        }

        // 统计 医生在 imGorupIds 这些群组中的未读记录
        Map<String, Integer> countNoReadNumber = new HashMap<>(records.size());
        List<Map<String, Object>> imGroupIdCount = chatSendReadMapper.selectMaps(Wrappers.<ChatSendRead>query()
                .select("group_id as groupId", "count(*) as total")
                .groupBy("group_id")
                .in("group_id", imGorupIds)
                .eq("user_id", doctorId)
                .eq("is_delete", CommonStatus.NO)
                .eq("role_type", UserType.UCENTER_DOCTOR));
        if (CollectionUtils.isNotEmpty(imGroupIdCount)) {
            for (Map<String, Object> objectMap : imGroupIdCount) {
                Object groupId = objectMap.get("groupId");
                Object total = objectMap.get("total");
                if (total != null && groupId != null) {
                    countNoReadNumber.put(groupId.toString(), Convert.toInt(total));
                }
            }
        }
        for (ChatUserNewMsg record : records) {
            record.setChat(chatMap.get(record.getChatId()));
            record.setNoReadTotal(countNoReadNumber.get(record.getReceiverImAccount()));
        }
    }

}
