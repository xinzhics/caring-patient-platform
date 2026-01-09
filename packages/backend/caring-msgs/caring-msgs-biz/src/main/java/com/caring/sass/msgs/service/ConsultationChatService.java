package com.caring.sass.msgs.service;

import com.caring.sass.msgs.entity.ConsultationChat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConsultationChatService
 * @Description
 * @Author yangShuai
 * @Date 2021/6/3 14:44
 * @Version 1.0
 **/
public interface ConsultationChatService {


    void saveConsultationChat(ConsultationChat consultationChat);


    void appReadMessage(Long groupId, String userType, Long receiverId);

    Map<Long, Integer> countNoReadMessage(List<Long> groupIds, String userRole, Long receiverId);


    List<ConsultationChat> getGroupImMessage(Long groupId, LocalDateTime localDateTime);


    void deleteMessage(Long groupId);
}
