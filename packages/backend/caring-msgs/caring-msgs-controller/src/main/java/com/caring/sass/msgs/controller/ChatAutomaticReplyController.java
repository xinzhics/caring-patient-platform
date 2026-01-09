package com.caring.sass.msgs.controller;

import com.caring.sass.msgs.entity.ChatAutomaticReply;
import com.caring.sass.msgs.dto.ChatAutomaticReplySaveDTO;
import com.caring.sass.msgs.dto.ChatAutomaticReplyUpdateDTO;
import com.caring.sass.msgs.dto.ChatAutomaticReplyPageDTO;
import com.caring.sass.msgs.service.ChatAutomaticReplyService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/chatAutomaticReply")
@Api(value = "ChatAutomaticReply", tags = "聊天消息超时自动回复")
public class ChatAutomaticReplyController extends SuperController<ChatAutomaticReplyService, Long, ChatAutomaticReply, ChatAutomaticReplyPageDTO, ChatAutomaticReplySaveDTO, ChatAutomaticReplyUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<ChatAutomaticReply> chatAutomaticReplyList = list.stream().map((map) -> {
            ChatAutomaticReply chatAutomaticReply = ChatAutomaticReply.builder().build();
            //TODO 请在这里完成转换
            return chatAutomaticReply;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(chatAutomaticReplyList));
    }






}
