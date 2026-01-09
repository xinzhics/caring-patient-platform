package com.caring.sass.msgs.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dto.ChatDoctorSharedMsgDTO;
import com.caring.sass.msgs.dto.ChatUserNewMsgPageDTO;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatAtRecord;
import com.caring.sass.msgs.entity.ChatUserNewMsg;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ChatUserNewMsgService
 * @Description
 * @Author yangShuai
 * @Date 2020/12/23 17:06
 * @Version 1.0
 **/
public interface ChatUserNewMsgService {


    <E extends IPage<ChatUserNewMsg>> E page(E page, Wrapper<ChatUserNewMsg> queryWrapper);

    ChatUserNewMsg findOne(Long id);

    void updateMsg(Chat chat, Long doctorId, Long nursingId, String receiverImAccount, String tenantCode, List<ChatAtRecord> chatAtRecords);

    void deleteByPatientImAccount(String patientImAccount);

    /**
     * 根据 医生的未读记录。生成医生的 待办消息列表
     * @param buildPage
     * @param doctorIds
     * @param paramsModel
     * @return
     */
    IPage<ChatUserNewMsg> doctorDealtWith(IPage<ChatUserNewMsg> buildPage, List<String> doctorIds, ChatDoctorSharedMsgDTO paramsModel);

    /**
     * 医生端
     * 添加了at后的消息列表查询
     * @return
     */
    IPage<ChatUserNewMsg> page(IPage<ChatUserNewMsg> buildPage, Map<String, LocalDateTime> data, List<String> doctorIds, ChatDoctorSharedMsgDTO paramsModel, List<Long> doctorExitChatPatientIds);


    /**
     * app端
     *
     * @param buildPage
     * @param paramsModel
     * @return
     */
    IPage<ChatUserNewMsg> page(IPage<ChatUserNewMsg> buildPage, ChatUserNewMsgPageDTO paramsModel);

    /**
     * 使用群组的im账号，查询群组和当前角色的消息记录
     * @param receiverImAccount
     * @return
     */
    ChatUserNewMsg findChatUserNewMsg(String receiverImAccount);

    /**
     * 移除消息列表中 医助 或 医生和患者的记录
     * @param receiverImAccount
     * @param requestId
     * @param requestRoleType
     * @return
     */
    Boolean removeChatUserNewMsg(String receiverImAccount, String requestId, String requestRoleType);
}
