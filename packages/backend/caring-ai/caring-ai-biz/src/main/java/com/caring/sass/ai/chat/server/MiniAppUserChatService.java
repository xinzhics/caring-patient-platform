package com.caring.sass.ai.chat.server;

import com.caring.sass.ai.entity.chat.MiniAppUserChat;
import com.caring.sass.ai.entity.dto.MiniAppUserChatSaveDTO;
import com.caring.sass.base.service.SuperService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 敏智用户聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-28
 */
public interface MiniAppUserChatService extends SuperService<MiniAppUserChat> {


    /**
     * 提交一个问题
     * @param miniAppUserChatSaveDTO
     * @return
     */
    MiniAppUserChat submitChat(MiniAppUserChatSaveDTO miniAppUserChatSaveDTO);

    /**
     * 创建一个sse链接
     * @param uid
     * @return
     */
    SseEmitter createSse(String uid);


    /**
     * 分页查询10条最新的消息
     * @param doctorId
     * @param createTime
     * @return
     */
    List<MiniAppUserChat> chatListPage(Long doctorId, Long historyId, LocalDateTime createTime);

}
