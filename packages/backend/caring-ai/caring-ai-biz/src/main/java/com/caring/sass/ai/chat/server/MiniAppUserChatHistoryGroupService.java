package com.caring.sass.ai.chat.server;

import com.caring.sass.ai.entity.chat.MiniAppUserChatHistoryGroup;
import com.caring.sass.base.service.SuperService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 敏智用户历史对话
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-28
 */
public interface MiniAppUserChatHistoryGroupService extends SuperService<MiniAppUserChatHistoryGroup> {

    /**
     * 给历史计划做时间的分组
     * @param records
     * @return
     */
    Map<String, List<MiniAppUserChatHistoryGroup>> customPage(List<MiniAppUserChatHistoryGroup> records);

}
