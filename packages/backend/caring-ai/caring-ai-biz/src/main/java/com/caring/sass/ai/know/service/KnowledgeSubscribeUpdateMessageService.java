package com.caring.sass.ai.know.service;

import com.caring.sass.ai.dto.know.KnowledgeSubscribeUpdateMessageSaveDTO;
import com.caring.sass.ai.entity.know.KnowledgeSubscribeUpdateMessage;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 博主订阅设置修改记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-08-05
 */
public interface KnowledgeSubscribeUpdateMessageService extends SuperService<KnowledgeSubscribeUpdateMessage> {

    void updateSubscribeInfo(KnowledgeSubscribeUpdateMessageSaveDTO knowledgeUser);

}
