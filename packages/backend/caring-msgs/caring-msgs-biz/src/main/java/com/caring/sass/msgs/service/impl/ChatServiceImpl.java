package com.caring.sass.msgs.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.Direction;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dao.ChatAtRecordMapper;
import com.caring.sass.msgs.dao.ChatMapper;
import com.caring.sass.msgs.dao.ChatSendReadMapper;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatAtRecord;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.msgs.service.ChatService;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.user.entity.NursingStaff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChatServiceImpl extends SuperServiceImpl<ChatMapper, Chat> implements ChatService {

    @Autowired
    ChatSendReadMapper chatSendReadMapper;

    @Autowired
    ChatAtRecordMapper chatAtRecordMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    NursingStaffApi nursingStaffApi;

    @Autowired
    TenantApi tenantApi;

    // 活跃时间内的患者IM账号
    private static String PATIENT_IM_ACTIVE_TIME = "patient_im_active_time:";

    /**
     * 保存聊天记录
     * 并对每一个接收人生成一条消息未读记录
     * @param model
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Chat model) {
        model.setWithdrawChatStatus(0);
        JSONArray jsonArray = new JSONArray();
        model.setDeleteThisMessageUserIdJsonArrayString(jsonArray.toJSONString());
        int insert = baseMapper.insert(model);
        if (model.getSystemMessage() != null && model.getSystemMessage().equals(1)) {
            return insert > 0;
        }
        List<ChatSendRead> sendReads = model.getSendReads();
        List<ChatSendRead> needSaveReads = new ArrayList<>();
        for (ChatSendRead sendRead : sendReads) {
            // 要求不产生未读记录时， 取消未读记录生成
            if (sendRead.getNoCreateReadLog() != null && sendRead.getNoCreateReadLog().equals(true)) {
                continue;
            }
            // 没有IM账号, 表示不接收消息，故:不生成未读记录
            if (!StringUtils.isEmpty(sendRead.getImAccount())) {
                // 关注和取关的消息。不生产未读记录。
                sendRead.setCreateTime(model.getCreateTime());
                sendRead.setChatId(model.getId());
                sendRead.setIs_delete(CommonStatus.NO);
                sendRead.setSendUserRole(model.getSenderRoleType());
                needSaveReads.add(sendRead);
            }
        }
        if (CollUtil.isNotEmpty(needSaveReads)) {
            chatSendReadMapper.insertBatchSomeColumn(needSaveReads);
        }

        // 清除发送人在群组中的未读
        Integer groupMessage = model.getGroupMessage();
        if (Objects.isNull(groupMessage) || groupMessage.equals(CommonStatus.NO)) {
            setChatRead(model.getReceiverImAccount(), Long.parseLong(model.getSenderId()), model.getSenderRoleType(), null);
        }
        // 只要发送人不是患者。那么就认为 这个群组中的患者问题被回复了。
        if (!model.getSenderRoleType().equals(UserType.UCENTER_PATIENT)) {
            UpdateWrapper<Chat> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status", CommonStatus.YES);
            updateWrapper.eq("receiver_im_account", model.getReceiverImAccount());
            updateWrapper.eq("status", CommonStatus.NO);
            baseMapper.update(new Chat(), updateWrapper);
        }
//        updatePatientImActiveTime(model.getReceiverImAccount());
        return insert > 0;
    }

    @Override
    public void updateStatus(String receiverImAccount) {
        UpdateWrapper<Chat> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", CommonStatus.YES);
        updateWrapper.eq("receiver_im_account", receiverImAccount);
        updateWrapper.eq("status", CommonStatus.NO);
        baseMapper.update(new Chat(), updateWrapper);
    }

    /**
     * 设置群组中聊天记录 为已读
     * @param groupId
     * @param userId
     * @param roleType
     */
    public void setChatRead(String groupId, Long userId, String roleType, List<String> groupIds) {
        UpdateWrapper<ChatSendRead> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_delete", CommonStatus.YES);
        if (StrUtil.isNotEmpty(groupId)) {
            updateWrapper.eq("group_id", groupId);
        } else if (CollUtil.isNotEmpty(groupIds)) {
            updateWrapper.in("group_id", groupIds);
        }
        updateWrapper.eq("is_delete", CommonStatus.NO);
        updateWrapper.eq("user_id", userId);
        updateWrapper.eq("role_type", roleType);
        chatSendReadMapper.update(new ChatSendRead(), updateWrapper);
    }


    @Override
    public Chat updateImRemind(Long chatId) {

        Chat chat = baseMapper.selectById(chatId);
        String functionParams = chat.getRecommendationFunctionParams();
        if (StrUtil.isNotEmpty(functionParams)) {
            JSONObject object = JSON.parseObject(functionParams);
            Object fillOut = object.get("fillOut");
            // 如果已经设置过已填。 则不推送消息
            if (Objects.nonNull(fillOut) && fillOut.toString().equals("1")) {
                return null;
            }
            object.put("fillOut", 1);
            chat.setRecommendationFunctionParams(object.toJSONString());
            baseMapper.updateById(chat);
            return chat;
        } else {
            JSONObject object = new JSONObject();
            object.put("fillOut", 1);
            chat.setRecommendationFunctionParams(object.toJSONString());
            baseMapper.updateById(chat);
            return chat;
        }
    }

    /**
     * 查询患者超时没有被医生或医助回复的消息
     * @param timeOut
     * @return
     */
    @Override
    public List<Chat> queryTimeOutChat(Integer timeOut) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = simpleDateFormat.format(new Date());
        LbqWrapper<Chat> lbqWrapper = Wraps.<Chat>lbQ().select(Chat::getReceiverImAccount)
                .eq(Chat::getStatus, CommonStatus.NO)
                .eq(Chat::getHistoryVisible, CommonStatus.YES)
                .eq(Chat::getSenderRoleType, UserType.UCENTER_PATIENT)
                .apply(true, "TIMESTAMPDIFF(MINUTE, create_time, '" + format + "' ) =  " + timeOut + " ")
                .groupBy(Chat::getReceiverImAccount);
        return baseMapper.selectList(lbqWrapper);
    }


    @Override
    public void desensitization(List<Chat> records) {

        if (records.isEmpty()) {
            return;
        }

        String userType = BaseContextHandler.getUserType();
        if (UserType.ADMIN.equals(userType)) {
            return;
        }

        String tenant = BaseContextHandler.getTenant();
        R<Boolean> securitySettings = tenantApi.queryDataSecuritySettings(tenant);
        if (securitySettings.getIsSuccess()) {
            Boolean data = securitySettings.getData();
            if (data == null || !data) {
                return;
            }
        } else {
            return;
        }

        for (Chat record : records) {
            // 对 换着的 姓名。 联系方式 脱敏
            record.setSenderName(SensitiveInfoUtils.desensitizeName(record.getSenderName()));
            record.setPatientName(SensitiveInfoUtils.desensitizeName(record.getPatientName()));
        }

    }

    /**
     * 更新患者 环信IM 活跃时间。
     * @param receiverImAccount
     */
    public void updatePatientImActiveTime(String receiverImAccount) {
        try {
            Boolean hasKey = redisTemplate.hasKey(PATIENT_IM_ACTIVE_TIME + receiverImAccount);
            if (hasKey == null || !hasKey) {
                redisTemplate.opsForSet().add(PATIENT_IM_ACTIVE_TIME + receiverImAccount);
            }
            redisTemplate.expire(PATIENT_IM_ACTIVE_TIME + receiverImAccount, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("updatePatientImActiveTime receiverImAccount: {}", receiverImAccount);
        }

    }

    public static void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(-1);
        System.out.println(dateTime);
    }

    /**
     * 检查 患者24小时内 是否im活跃
     * @param tenant
     * @param receiverImAccount
     */
    @Override
    public void checkSendPatientRemind(String tenant, String receiverImAccount) {

        Boolean hasKey = redisTemplate.hasKey(PATIENT_IM_ACTIVE_TIME + receiverImAccount);
        if (hasKey != null && hasKey) {
            return;
        }
        BaseContextHandler.setTenant(tenant);
        LocalDateTime dateTime = LocalDateTime.now().plusDays(-1);
        Integer count = baseMapper.selectCount(Wraps.<Chat>lbQ()
                .eq(Chat::getReceiverImAccount, receiverImAccount)
                .gt(Chat::getCreateTime, dateTime));
        if (count != null && count > 0) {
            return;
        } else {
            nursingStaffApi.sendPatientImRemind(tenant, receiverImAccount);
        }
    }

    /**
     * 已经五分钟没有读的消息
     * @param receiverRoleType
     * @return
     */
    @Override
    public List<ChatSendRead> chatFirstByRoleType(String receiverRoleType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = simpleDateFormat.format(new Date());
        if ("doctor".equals(receiverRoleType)) {
            return chatSendReadMapper.selectList(Wraps.<ChatSendRead>lbQ().select(ChatSendRead::getUserId)
                    .eq(ChatSendRead::getRoleType, receiverRoleType)
                    .eq(ChatSendRead::getIs_delete, CommonStatus.NO)
                    .groupBy(ChatSendRead::getUserId));
        } else {
            // 查询患者 已经5分钟没有阅读的消息
            return chatSendReadMapper.selectList(Wraps.<ChatSendRead>lbQ().select(ChatSendRead::getUserId)
                    .eq(ChatSendRead::getIs_delete, CommonStatus.NO)
                    .apply(true, "role_type = '"+ receiverRoleType + "'")
                    .apply(true, "TIMESTAMPDIFF(MINUTE, create_time, '"+ format+ "' ) = 4")
                .groupBy(ChatSendRead::getUserId));
        }
    }

    /**
     * 获取 聊天群组中的消息记录
     * 不考虑 历史记录是否可见问题
     * @param patientImAccount
     * @param dateTime
     * @return
     */
    @Override
    public List<Chat> getchatGroupHistory(String patientImAccount, LocalDateTime dateTime) {
        IPage<Chat> buildPage = new Page(1, 10);
        LbqWrapper<Chat> lbqWrapper = Wraps.<Chat>lbQ().eq(Chat::getReceiverImAccount, patientImAccount)
                .lt(Chat::getCreateTime, dateTime)
                .orderByDesc(Chat::getCreateTime);
        IPage<Chat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<Chat> records = selectPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records;
    }

    /**
     * 患者查询历史记录，只能查看  HistoryVisible 为 1 的
     * XXX 关注
     * XXX 取关  、 重新关注 等消息患者不可见
     * @param patientImAccount
     * @param patient
     * @param localDateTime
     * @return
     */
    @Override
    public List<Chat> getchatGroupHistory(String patientImAccount, String patient, LocalDateTime localDateTime, Integer size) {
        IPage<Chat> buildPage = new Page(1, size);
        LbqWrapper<Chat> lbqWrapper = Wraps.<Chat>lbQ().eq(Chat::getReceiverImAccount, patientImAccount)
                .lt(Chat::getCreateTime, localDateTime)
                .eq(Chat::getHistoryVisible, 1)
                .orderByDesc(Chat::getCreateTime);
        IPage<Chat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<Chat> records = selectPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records;
    }

    /**
     * 获取医生 在 患者这个聊天群组中可见的历史消息
     * @param patientImAccount
     * @param localDateTime
     * @param groupTime
     * @param size
     * @return
     */
    @Override
    public List<Chat> getDoctorChatGroupHistory(String patientImAccount, LocalDateTime localDateTime, LocalDateTime groupTime, Integer size) {
        IPage<Chat> buildPage = new Page(1, size);
        LbqWrapper<Chat> lbqWrapper = Wraps.<Chat>lbQ().eq(Chat::getReceiverImAccount, patientImAccount)
                .lt(Chat::getCreateTime, localDateTime)
                .eq(Chat::getHistoryVisible, 1)
                .orderByDesc(Chat::getCreateTime);


        // 看到的消息记录，需要晚于 医生的入组时间
        if (groupTime != null) {
            lbqWrapper.gt(SuperEntity::getCreateTime, groupTime);
        }
        IPage<Chat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<Chat> records = selectPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records;
    }

    @Override
    public Chat getChatImage(String patientImAccount, LocalDateTime localDateTime, LocalDateTime groupTime, Direction direction) {
        IPage<Chat> buildPage = new Page(1, 1);
        Long userId = BaseContextHandler.getUserId();
        LbqWrapper<Chat> lbqWrapper = Wraps.<Chat>lbQ()
                .eq(Chat::getReceiverImAccount, patientImAccount)
                .eq(Chat::getWithdrawChatStatus, 0)
                .eq(Chat::getType, MediaType.image);
        if (userId != null) {
            lbqWrapper.notLike(Chat::getDeleteThisMessageUserIdJsonArrayString, userId.toString());
        }
        if (Direction.Next.equals(direction)) {
            lbqWrapper.gt(Chat::getCreateTime, localDateTime);
            lbqWrapper.orderByAsc(Chat::getCreateTime);
        }
        if (Direction.Previous.equals(direction)) {
            lbqWrapper.lt(Chat::getCreateTime, localDateTime);
            lbqWrapper.orderByDesc(Chat::getCreateTime);
        }
        // 看到的消息记录，需要晚于 医生的入组时间
        if (groupTime != null) {
            lbqWrapper.gt(SuperEntity::getCreateTime, groupTime);
        }

        IPage<Chat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<Chat> records = selectPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.get(0);
    }

    /**
     * @Author yangShuai
     * @Description 刷新发送给医生读取的消息的状态为已读
     * @Date 2020/11/27 13:31
     *
     * @return void
     */
    @Override
    public void refreshMsgStatus(Long userId, String roleType) {

        setChatRead(null, userId,  roleType, null);

        // 清除这个群组中 @ 患者的消息
        if (UserType.UCENTER_PATIENT.equals(roleType)) {
            chatAtRecordMapper.delete(Wraps.<ChatAtRecord>lbQ()
                    .eq(ChatAtRecord::getAtUserId, userId)
                    .eq(ChatAtRecord::getUserType, roleType)
            );
        }
    }

    /**
     * 清除 用户在 1 个群组中的历史消息记录
     * @param userId
     * @param roleType
     * @param groupId
     */
    @Override
    public void refreshMsgStatus(Long userId, String roleType, String groupId) {
        // 设置未读记录为已读
        setChatRead(groupId, userId,  roleType, null);


        // 清除这个群组中 @ 用户的消息
        chatAtRecordMapper.delete(Wraps.<ChatAtRecord>lbQ()
                .eq(ChatAtRecord::getAtUserId, userId)
                .eq(ChatAtRecord::getUserType, roleType)
                .eq(ChatAtRecord::getReceiverImAccount, groupId));
    }

    /**
     * 清除 用户在 N 个群组中的历史消息记录
     * @param userId
     * @param roleType
     * @param groupIds
     */
    @Override
    public void refreshMsgStatus(Long userId, String roleType, List<String> groupIds) {

        setChatRead(null, userId,  roleType, groupIds);

    }

    /**
     * 清除用户不在的群组的未读消息记录
     * @param userId
     * @param roleType
     * @param groupIds
     */
    @Override
    public void refreshMsgStatusNotInGroupIds(Long userId, String roleType, List<String> groupIds) {
        chatSendReadMapper.delete(Wraps.<ChatSendRead>lbQ()
                .eq(ChatSendRead::getUserId, userId)
                .eq(ChatSendRead::getRoleType, roleType)
                .notIn(ChatSendRead::getGroupId, groupIds)
        );
    }

    /**
     * 统计某角色的未读消息数量
     * @param doctorId
     * @param roleType
     * @return
     */
    @Override
    public Integer countMsgNumber(Long doctorId, String roleType) {

        return chatSendReadMapper.selectCount(Wraps.<ChatSendRead>lbQ()
                .eq(ChatSendRead::getUserId, doctorId)
                .eq(ChatSendRead::getIs_delete, CommonStatus.NO)
                .eq(ChatSendRead::getRoleType, roleType));
    }


    /**
     * 查询 用户有多少患者的未读消息。只有患者发了没有读的才算
     * @param userId
     * @param userType
     * @param now
     * @return
     */
    @Override
    public Integer countPatientNumber(Long userId, String userType, LocalDateTime now) {

        return chatSendReadMapper.countPatientNumber(userId, userType, now);
    }


    @Override
    public Chat queryLastMsg(Long senderId, String userType, String receiverImAccount) {

        Chat chat = baseMapper.selectOne(Wrappers.<Chat>lambdaQuery()
                .eq(Chat::getSenderId, senderId)
                .eq(Chat::getSenderRoleType, userType)
                .eq(Chat::getReceiverImAccount, receiverImAccount)
                .eq(Chat::getHistoryVisible, 1)
                .eq(Chat::getWithdrawChatStatus, 0L)
                .last(" limit 0, 1 ")
                .orderByDesc(Chat::getCreateTime));
        return chat;
    }

    @Override
    public IPage<Chat> page(IPage<Chat> page, LbqWrapper<Chat> lbqWrapper) {

        return baseMapper.selectPage(page, lbqWrapper);
    }
}
