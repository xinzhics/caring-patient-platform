package com.caring.sass.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.chat.server.MiniAppSearchService;
import com.caring.sass.ai.chat.server.MiniAppUserChatService;
import com.caring.sass.ai.entity.chat.MiniAppUserChat;
import com.caring.sass.ai.entity.dto.MiniAppUserChatPageDTO;
import com.caring.sass.ai.entity.dto.MiniAppUserChatSaveDTO;
import com.caring.sass.ai.entity.dto.MiniAppUserChatUpdateDTO;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * 敏智用户聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-28
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/miniAppUserChat")
@Api(value = "MiniAppUserChat", tags = "敏智用户聊天记录")
public class MiniAppUserChatController extends SuperController<MiniAppUserChatService, Long, MiniAppUserChat, MiniAppUserChatPageDTO, MiniAppUserChatSaveDTO, MiniAppUserChatUpdateDTO> {

    @Autowired
    MiniAppSearchService miniAppSearchService;


    @ApiOperation("是否在审核")
    @GetMapping("anno/auditing{appId}")
    public R<Boolean> auditing(@PathVariable String appId) {
        Boolean auditing = miniAppSearchService.auditing(appId);
        return R.success(auditing);
    }

    @ApiOperation("小程序提交审核")
    @GetMapping("anno/submitAuditing{appId}")
    public R<Void> submitAuditing(@PathVariable String appId) {
        miniAppSearchService.submitAuditing(appId);
        return R.success(null);
    }

    @ApiOperation("小程序取消审核")
    @GetMapping("anno/cancelAuditing{appId}")
    public R<Void> cancelAuditing(@PathVariable String appId) {
        miniAppSearchService.cancelAuditing(appId);
        return R.success(null);
    }


    @ApiOperation("创建搜素问题答案的SSE")
    @GetMapping("/search/createSearchSse")
    public SseEmitter createSearchSse(@RequestParam String uid) {
        if (StrUtil.isBlank(uid)) {
            throw new BizException("参数错误");
        }
        return miniAppSearchService.createSse(uid);
    }

    @ApiOperation("搜索一个问题的答案")
    @PostMapping("/search/answer")
    public R<MiniAppUserChat> searchAnswer(@RequestBody @Validated MiniAppUserChatSaveDTO miniAppUserChatSaveDTO) {
        MiniAppUserChat openAiChatResponse = miniAppSearchService.sseChat(miniAppUserChatSaveDTO);
        return R.success(openAiChatResponse);
    }

    @ApiOperation("使用uid查询AI的回复")
    @GetMapping("/search/getAnswerDetailByUid")
    public R<MiniAppUserChat> getAnswerDetailByUid(@RequestParam String uid) {
        MiniAppUserChat userChat = baseService.getOne(Wraps.<MiniAppUserChat>lbQ().eq(MiniAppUserChat::getSessionId, uid).eq(MiniAppUserChat::getSenderRoleType, MiniAppUserChat.AiRole).last(" limit 0,1 "));
        return R.success(userChat);
    }


    @Deprecated
    @ApiOperation("创建sse链接")
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestParam String uid) {
        if (StrUtil.isBlank(uid)) {
            throw new BizException("参数错误");
        }
        return baseService.createSse(uid);
    }

    @Deprecated
    @ApiOperation("提交一个问题")
    @PostMapping("/submitChat")
    public R<MiniAppUserChat> submitChat(@RequestBody @Validated MiniAppUserChatSaveDTO miniAppUserChatSaveDTO) {
        MiniAppUserChat openAiChatResponse = baseService.submitChat(miniAppUserChatSaveDTO);
        return R.success(openAiChatResponse);
    }


    @ApiOperation("获取对话中的10条记录")
    @GetMapping({"chatListPage"})
    public R<List<MiniAppUserChat>> chatListPage(@RequestParam(value = "userId") Long userId,
                                                    @RequestParam(value = "historyId") Long historyId,
                                                    @RequestParam(value = "createTimeString") String createTimeString) {
        LocalDateTime createTime = getMessageCreateTime(createTimeString);
        List<MiniAppUserChat> doctorChats = baseService.chatListPage(userId, historyId, createTime);
        return R.success(doctorChats);
    }

    public static LocalDateTime getMessageCreateTime(String createTimeString) {
        if (StrUtil.isNotEmpty(createTimeString)) {
            DateTimeFormatter timeFormatter;
            if (createTimeString.length() == 23) {
                timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            } else {
                timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            }
            return LocalDateTime.parse(createTimeString, timeFormatter);
        }
        return LocalDateTime.now();
    }

    @ApiOperation("获取ID对应的聊天记录")
    @PostMapping({"anno/findChatListByIds"})
    public R<List<MiniAppUserChat>> findChatListByIds(@RequestBody List<Long> ids) {

        List<MiniAppUserChat> chatList = baseService.listByIds(ids);

        chatList.sort((o1, o2) -> {
            if (o1.getCreateTime().isBefore(o2.getCreateTime())) {
                return -1;
            } else {
                return 1;
            }
        });
        return R.success(chatList);

    }



}
