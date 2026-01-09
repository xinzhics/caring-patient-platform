package com.caring.sass.msgs.service;

import com.caring.sass.msgs.dto.DoctorSubscribeKeyWordReply;
import com.caring.sass.msgs.dto.GptDoctorChatSaveDTO;
import com.caring.sass.msgs.entity.GptDoctorChat;
import com.caring.sass.msgs.entity.OpenAiChatRequest;
import com.caring.sass.msgs.entity.OpenAiChatResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务接口
 * GPT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2023-02-10
 */
public interface GptDoctorChatService {

    /**
     * 医生发送消息
     *
     * @param chatSaveDTO
     */
    GptDoctorChat sendMsg(GptDoctorChatSaveDTO chatSaveDTO);


    /**
     * 消息列表
     *
     * @param doctorId
     * @param createTime
     * @return
     */
    List<GptDoctorChat> chatListPage(Long doctorId, LocalDateTime createTime);

    /**
     * 医生是否可以继续发送消息
     *
     * @param doctorId
     * @return
     */
    boolean doctorSendMsgStatus(Long doctorId);


    /**
     * 检查是否发送过AI介绍。
     *
     * @param doctorId
     * @param imAccount
     */
    void checkSendAiIntroduction(Long doctorId, String imAccount);

    /**
     * 医生订阅回复
     *
     * @param subscribeKeyWordReply
     */
    void doctorSubscribeReply(DoctorSubscribeKeyWordReply subscribeKeyWordReply);


    void sendCms(GptDoctorChatSaveDTO chatSaveDTO);


    /**
     * 创建sse连接
     *
     * @param uid 连接唯一标识
     */
    SseEmitter createSse(String uid);

    /**
     * 关闭sse连接
     *
     * @param uid 连接唯一标识
     */
    void closeSse(String uid);

    /**
     * 开启一次对话
     *
     * @param chatRequest 对话参数
     * @return 本轮对话问题消耗tokens
     */
    GptDoctorChat sseChat(GptDoctorChatSaveDTO chatRequest);


    GptDoctorChat lastNewMessage(Long doctorId);

}
