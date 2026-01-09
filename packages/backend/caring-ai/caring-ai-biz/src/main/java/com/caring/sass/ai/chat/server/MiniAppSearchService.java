package com.caring.sass.ai.chat.server;

import com.caring.sass.ai.entity.chat.MiniAppUserChat;
import com.caring.sass.ai.entity.dto.MiniAppUserChatSaveDTO;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @className: MiniAppSearchService
 * @author: 杨帅
 * @date: 2024/4/9
 */
public interface MiniAppSearchService extends SuperService<MiniAppUserChat>{


    SseEmitter createSse(String uid);

    MiniAppUserChat sseChat(MiniAppUserChatSaveDTO miniAppUserChatSaveDTO);

    void submitAuditing(String appId);

    void cancelAuditing(String appId);

    Boolean auditing(String appId);

}
