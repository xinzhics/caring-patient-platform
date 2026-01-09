package com.caring.sass.msgs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.common.constant.Direction;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatSendRead;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ChatService
 * @Description
 * @Author yangShuai
 * @Date 2020/10/16 11:02
 * @Version 1.0
 */
public interface ChatService {

    /***
     * 保存一条消息记录，并 生成接收人对应的未读记录 , 清除发送人的未读
     * @param chat
     * @return
     */
    boolean save(Chat chat);

    /**
     * 定时任务使用。 查询一段时间内用户是否有未读消息。
     * @param receiverRoleType
     * @return
     */
    List<ChatSendRead> chatFirstByRoleType(String receiverRoleType);

    /**
     * 清除用户的所有未读
     * @param userId
     * @param roleType
     */
    void refreshMsgStatus(Long userId, String roleType);

    /**
     * 清除用户在某些群组的未读
     * @param userId
     * @param roleType
     * @param groupIds
     */
    void refreshMsgStatus(Long userId, String roleType, List<String> groupIds);

    /**
     * 清除用户不在的群组的未读消息记录
     * @param userId
     * @param roleType
     * @param groupIds
     */
    void refreshMsgStatusNotInGroupIds(Long userId, String roleType, List<String> groupIds);

    /**
     * 清除用户在 一个 群组的未读
     * @param userId
     * @param roleType
     * @param groupId
     */
    void refreshMsgStatus(Long userId, String roleType, String groupId);

    /**
     * 统计用户所有的未读消息数量
     * @param userId
     * @param roleType
     * @return
     */
    Integer countMsgNumber(Long userId, String roleType);

    IPage<Chat> page(IPage<Chat> buildPage, LbqWrapper<Chat> lbqWrapper);

    /**
     * 获取 聊天群组中的消息记录
     * 不考虑 历史记录是否可见问题
     * @param patientImAccount
     * @param dateTime
     * @return
     */
    List<Chat> getchatGroupHistory(String patientImAccount, LocalDateTime dateTime);

    /**
     * 获取 患者 可见的 聊天群组中的消息记录
     * 考虑 historyVisible 要求为 1
     * @param patientImAccount
     * @param localDateTime
     * @return
     */
    List<Chat> getchatGroupHistory(String patientImAccount, String patient, LocalDateTime localDateTime, Integer size);

    /**
     * 获取医生 在 患者这个聊天群组中可见的历史消息
     * @param patientImAccount
     * @param localDateTime
     * @param groupTime
     * @param size
     * @return
     */
    List<Chat> getDoctorChatGroupHistory(String patientImAccount, LocalDateTime localDateTime, LocalDateTime groupTime, Integer size);


    Chat getChatImage(String patientImAccount, LocalDateTime localDateTime, LocalDateTime groupTime, Direction direction);

    /**
     * 异步检查是否需要发送 欢迎患者的 话术: 您好，我是医生助理，为了让医生了解到您具体的需求，给到您对应的健康指导，请将您的症状或者需要咨询的内容直接发送过来！
     * @param tenant
     * @param receiverImAccount
     */
    void checkSendPatientRemind(String tenant, String receiverImAccount);


    void updateStatus(String receiverImAccount);

    /**
     * 更新消息的im提醒状态是 已填写
     * @param chatId
     */
    Chat updateImRemind(Long chatId);


    List<Chat> queryTimeOutChat(Integer timeOut);


    /**
     * 对其中的敏感数据加密
     * @param records
     */
    void desensitization(List<Chat> records);

    /**
     * 统计用户有多少患者给他发送了消息，未读
     * @param userId
     * @param userType
     * @param now
     * @return
     */
    Integer countPatientNumber(Long userId, String userType, LocalDateTime now);

    /**
     * 查询用户最新的一条消息
     * @param senderId
     * @param userType
     * @param receiverImAccount
     * @return
     */
    Chat queryLastMsg(Long senderId, String userType, String receiverImAccount);
}
