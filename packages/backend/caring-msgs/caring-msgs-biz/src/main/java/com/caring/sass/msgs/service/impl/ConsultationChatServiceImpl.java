package com.caring.sass.msgs.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dao.ConsultationChatMapper;
import com.caring.sass.msgs.dao.ConsultationMessageStatusMapper;
import com.caring.sass.msgs.entity.ConsultationChat;
import com.caring.sass.msgs.entity.ConsultationMessageStatus;
import com.caring.sass.msgs.service.ConsultationChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @ClassName ConsultationChatServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/6/3 14:45
 * @Version 1.0
 */
@Service
@Slf4j
public class ConsultationChatServiceImpl extends SuperServiceImpl<ConsultationChatMapper, ConsultationChat> implements ConsultationChatService {

    @Autowired
    ConsultationMessageStatusMapper consultationMessageStatusMapper;


    /**
     * 保存群组消息记录
     * @param consultationChat
     */
    @Override
    public void saveConsultationChat(ConsultationChat consultationChat) {
        consultationChat.setCreateTime(LocalDateTime.now());
        // 要产生的新的未读记录
        List<ConsultationMessageStatus> messageStatuses = consultationChat.getMessageStatuses();

        baseMapper.insert(consultationChat);

        // 异步处理保存消息后的问题
        syncHandle(consultationChat, messageStatuses, BaseContextHandler.getTenant());

    }

    /**
     * 未读消息改为已读
     * @param groupId
     * @param receiverId
     */
    @Override
    public void appReadMessage(Long groupId, String userType, Long receiverId) {

        LbqWrapper<ConsultationMessageStatus> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationMessageStatus::getGroupId, groupId)
                .eq(ConsultationMessageStatus::getReceiverId, receiverId)
                .eq(ConsultationMessageStatus::getReceiverRoleType, userType)
                .eq(ConsultationMessageStatus::getStatus, 2);
        consultationMessageStatusMapper.delete(lbqWrapper);
    }

    /**
     * 统计app的会诊小组的未读消息数量
     * @param groupIds
     * @param userRole
     * @param receiverId
     * @return
     */
    @Override
    public Map<Long, Integer> countNoReadMessage(List<Long> groupIds, String userRole, Long receiverId) {

        QueryWrapper<ConsultationMessageStatus> queryWrapper = Wrappers.query();
        queryWrapper.select("group_id as groupId", "count(*) as total");
        if (Objects.nonNull(receiverId)) {
            queryWrapper.eq("receiver_id", receiverId);
            queryWrapper.eq("receiver_role_type", userRole);
        }
        queryWrapper.in("group_id", groupIds)
                .eq("status", 2)
                .groupBy("group_id");

        Map<Long, Integer> map = new HashMap<>();
        List<Map<String, Object>> mapList = consultationMessageStatusMapper.selectMaps(queryWrapper);
        if (CollectionUtils.isEmpty(mapList)) {
            return null;
        }
        for (Long groupId : groupIds) {
            map.put(groupId, getCountResult(mapList, "groupId", "total", groupId));
        }
        return map;
    }

    private Integer getCountResult(List<Map<String, Object>> mapList, String labelKey, String valueLabel, Long valueKey) {
        if (CollectionUtils.isEmpty(mapList)) {
            return 0;
        }
        for (Map<String, Object> stringObjectMap : mapList) {
            if (stringObjectMap == null) {
                continue;
            }
            Object o = stringObjectMap.get(labelKey);
            if (Objects.nonNull(o) && o.toString().equals(valueKey.toString())) {
                Object count = stringObjectMap.get(valueLabel);
                if (Objects.isNull(count)) {
                    return 0;
                }
                return Integer.parseInt(count.toString());
            }
        }
        return 0;
    }

    /**
     * 新建一条app未读消息记录，或者修改未读消息为已读
     * @param consultationChat
     * @param tenant
     */
    @Async
    public void syncHandle(ConsultationChat consultationChat,  List<ConsultationMessageStatus> messageStatuses, String tenant){
        // cleanUnread 和 createMessageRecord 一般不会同时为 true
        // 创建一条app 的未读记录
        BaseContextHandler.setTenant(tenant);
        if (CollUtil.isNotEmpty(messageStatuses)) {
            for (ConsultationMessageStatus messageStatus : messageStatuses) {
                messageStatus.setConsultationChatId(consultationChat.getId());

            }
            consultationMessageStatusMapper.insertBatchSomeColumn(messageStatuses);
        }
        // 删除已读消息
        LbqWrapper<ConsultationMessageStatus> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationMessageStatus::getGroupId, consultationChat.getGroupId())
                .eq(ConsultationMessageStatus::getReceiverId, consultationChat.getSenderId())
                .eq(ConsultationMessageStatus::getStatus, 2);
        consultationMessageStatusMapper.delete(lbqWrapper);
    }

    @Override
    public List<ConsultationChat> getGroupImMessage(Long groupId, LocalDateTime localDateTime) {

        IPage<ConsultationChat> buildPage = new Page(1, 20);
        LbqWrapper<ConsultationChat> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationChat::getGroupId, groupId);
        lbqWrapper.lt(ConsultationChat::getCreateTime, localDateTime);
        lbqWrapper.orderByDesc(ConsultationChat::getCreateTime);
        IPage<ConsultationChat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<ConsultationChat> records = selectPage.getRecords();
        Collections.reverse(records);
        return records;
    }

    @Transactional
    @Override
    public void deleteMessage(Long groupId) {
        LbqWrapper<ConsultationChat> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationChat::getGroupId, groupId);
        baseMapper.delete(lbqWrapper);

        LbqWrapper<ConsultationMessageStatus> lbqWrapper1 = new LbqWrapper<>();
        lbqWrapper1.eq(ConsultationMessageStatus::getGroupId, groupId);
        consultationMessageStatusMapper.delete(lbqWrapper1);
    }
}
