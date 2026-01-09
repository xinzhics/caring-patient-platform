package com.caring.sass.msgs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dto.DoctorGroupSendPageDTO;
import com.caring.sass.msgs.entity.ChatGroupSend;

import java.util.List;

/**
 * @ClassName ChatService
 * @Description
 * @Author yangShuai
 * @Date 2020/10/16 11:02
 * @Version 1.0
 */
public interface ChatGroupSendService {


    boolean save(ChatGroupSend chatGroupSendDto);


    IPage<ChatGroupSend> page(IPage<ChatGroupSend> page, LbqWrapper<ChatGroupSend> lbqWrapper);


    void deleteGroupSend(Long id);


    void saveChatGroupSend(ChatGroupSend chatGroupSend);

    void chatGroupTiming();


    List<ChatGroupSend> page(DoctorGroupSendPageDTO groupSendPageDTO);


}
