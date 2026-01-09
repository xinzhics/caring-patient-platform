package com.caring.sass.msgs.controller;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.utils.SassDateUtis;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.dto.DoctorSubscribeKeyWordReply;
import com.caring.sass.msgs.dto.GptDoctorChatSaveDTO;
import com.caring.sass.msgs.entity.GptDoctorChat;
import com.caring.sass.msgs.service.GptDoctorChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 前端控制器
 * GPT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2023-02-10
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/gptDoctorChat")
@Api(value = "GptDoctorChat", tags = "GPT医生聊天记录")
public class GptDoctorChatController {

    @Autowired
    GptDoctorChatService gptDoctorChatService;

    @ApiOperation("医生发送消息给GPT")
    @PostMapping("sendMsg")
    public R<GptDoctorChat> sendMsg(@RequestBody @Validated GptDoctorChatSaveDTO chatSaveDTO) {
        GptDoctorChat doctorChat = gptDoctorChatService.sendMsg(chatSaveDTO);
        return R.success(doctorChat);

    }

    @ApiOperation("创建sse链接")
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestHeader Map<String, String> headers) {
        String uid = headers.get("uid");
        if (StrUtil.isBlank(uid)) {
            throw new BizException("参数错误");
        }
        return gptDoctorChatService.createSse(uid);
    }

    @ApiOperation("通过sse返回数据")
    @PostMapping("/sseChat")
    public R<GptDoctorChat> sseChat(@RequestBody @Validated GptDoctorChatSaveDTO chatSaveDTO) {
        GptDoctorChat openAiChatResponse = gptDoctorChatService.sseChat(chatSaveDTO);
        return R.success(openAiChatResponse);
    }


    @ApiOperation("检查医生是否接收过敏智介绍")
    @GetMapping("checkSendAiIntroduction")
    public R<Boolean> checkSendAiIntroduction(@RequestParam("doctorId") Long doctorId, @RequestParam("imAccount") String imAccount) {
        gptDoctorChatService.checkSendAiIntroduction(doctorId, imAccount);
        return R.success(true);
    }

    @ApiOperation("接收医生订阅关键字后的文字消息，并返回一段取消订阅的描述")
    @PutMapping("doctorSubscribeReply")
    public R<Boolean> doctorSubscribeReply(@RequestBody DoctorSubscribeKeyWordReply subscribeKeyWordReply) {
        gptDoctorChatService.doctorSubscribeReply(subscribeKeyWordReply);
        return R.success(true);
    }


    @ApiOperation("当文章关键词触发到医生时，给医生推送文章资讯")
    @PutMapping("sendCms")
    public R<Boolean> sendCms(@RequestBody GptDoctorChatSaveDTO chatSaveDTO) {
        String tenantCode = chatSaveDTO.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        gptDoctorChatService.sendCms(chatSaveDTO);
        return R.success(true);
    }


    // 接收 文章的ID。 医生的ids
    // 医生id查询IM账号
    // 封装消息对象 推送到IM

    @ApiOperation("获取医生和GPT聊天列表最新20数据")
    @GetMapping({"chatListPage"})
    public R<List<GptDoctorChat>> chatListPage(@RequestParam(value = "doctorId") Long doctorId,
                          @RequestParam(value = "createTimeString") String createTimeString) {
        LocalDateTime createTime = SassDateUtis.getMessageCreateTime(createTimeString);
        List<GptDoctorChat> doctorChats = gptDoctorChatService.chatListPage(doctorId, createTime);
        return R.success(doctorChats);
    }


    @ApiOperation("获取医生和GPT最新的消息")
    @GetMapping({"lastNewMessage"})
    public R<GptDoctorChat> lastNewMessage(@RequestParam(value = "doctorId") Long doctorId) {
        GptDoctorChat doctorChats = gptDoctorChatService.lastNewMessage(doctorId);
        return R.success(doctorChats);
    }


    @ApiOperation("医生是否可以继续发送消息 true 标识可以继续发送")
    @GetMapping({"doctorSendMsgStatus"})
    public R<Boolean> doctorSendMsgStatus(@RequestParam(value = "doctorId") Long doctorId) {
       boolean status = gptDoctorChatService.doctorSendMsgStatus(doctorId);
       return R.success(status);
    }





}
