package com.caring.sass.msgs.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.msgs.dto.BaiduBotDoctorChatSaveDTO;
import com.caring.sass.msgs.entity.BaiduBotDoctorChat;
import com.caring.sass.msgs.entity.GptDoctorChat;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 百度灵医BOT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-02-29
 */
public interface BaiduBotDoctorChatService extends SuperService<BaiduBotDoctorChat> {

    SseEmitter createSse(String uid);

    BaiduBotDoctorChat sseChat(BaiduBotDoctorChatSaveDTO baiduBotDoctorChatSaveDTO);

    void closeSse(String uid);

    List<BaiduBotDoctorChat> chatListPage(Long doctorId, LocalDateTime createTime);

    BaiduBotDoctorChat lastNewMessage(Long doctorId);

}
