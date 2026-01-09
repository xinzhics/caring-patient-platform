package com.caring.sass.msgs.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.msgs.entity.ChatAutomaticReply;

/**
 * <p>
 * 业务接口
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-22
 */
public interface ChatAutomaticReplyService extends SuperService<ChatAutomaticReply> {

    /**
     * 自动回复消息的任务逻辑
     */
    void replyPatientMessage();
}
