package com.caring.sass.msgs.controller;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.utils.SassDateUtis;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.dto.BaiduBotDoctorChatPageDTO;
import com.caring.sass.msgs.dto.BaiduBotDoctorChatSaveDTO;
import com.caring.sass.msgs.dto.BaiduBotDoctorChatUpdateDTO;
import com.caring.sass.msgs.entity.BaiduBotDoctorChat;
import com.caring.sass.msgs.service.BaiduBotDoctorChatService;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * 百度灵医BOT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-02-29
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/baiduBotDoctorChat")
@Api(value = "BaiduBotDoctorChat", tags = "百度灵医BOT医生聊天记录")
@PreAuth(replace = "baiduBotDoctorChat:")
public class BaiduBotDoctorChatController extends SuperController<BaiduBotDoctorChatService, Long, BaiduBotDoctorChat, BaiduBotDoctorChatPageDTO, BaiduBotDoctorChatSaveDTO, BaiduBotDoctorChatUpdateDTO> {


    @ApiOperation("创建sse链接")
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestParam String uid) {
        if (StrUtil.isBlank(uid)) {
            throw new BizException("参数错误");
        }
        return baseService.createSse(uid);
    }

    @ApiOperation("通过sse返回数据")
    @PostMapping("/sseChat")
    public R<BaiduBotDoctorChat> sseChat(@RequestBody @Validated BaiduBotDoctorChatSaveDTO doctorChatSaveDTO) {
        BaiduBotDoctorChat openAiChatResponse = baseService.sseChat(doctorChatSaveDTO);
        return R.success(openAiChatResponse);
    }


    @ApiOperation("获取医生聊天列表最新20数据")
    @GetMapping({"chatListPage"})
    public R<List<BaiduBotDoctorChat>> chatListPage(@RequestParam(value = "doctorId") Long doctorId,
                                               @RequestParam(value = "createTimeString") String createTimeString) {
        LocalDateTime createTime = SassDateUtis.getMessageCreateTime(createTimeString);
        List<BaiduBotDoctorChat> doctorChats = baseService.chatListPage(doctorId, createTime);
        return R.success(doctorChats);
    }


    @ApiOperation("获取医生最新的消息")
    @GetMapping({"lastNewMessage"})
    public R<BaiduBotDoctorChat> lastNewMessage(@RequestParam(value = "doctorId") Long doctorId) {
        BaiduBotDoctorChat doctorChats = baseService.lastNewMessage(doctorId);
        return R.success(doctorChats);
    }


}
