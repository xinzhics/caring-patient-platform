package com.caring.sass.msgs.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName ChatBizService
 * @Description
 * @Author yangShuai
 * @Date 2021/12/3 10:31
 * @Version 1.0
 */
public interface ChatBizService {

    void batchUpdatePatientForAllTable(JSONObject patient);


    /**
     * 撤回消息，并通知群组中的成员
     * @param patientId
     * @param chatId
     * @return
     */
    Boolean withdrawChat(Long patientId, Long chatId);

    /**
     * 更新一个删除状态。
     * @param userId
     * @param chatId
     * @return
     */
    Boolean updateDeleteStatusChat(Long userId, Long chatId);

    /**
     * 删除消息会话
     * @param chatNewMsgId
     */
    void deleteChatMsg(Long chatNewMsgId);
}
