package com.caring.sass.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.dto.ChatDTO;
import com.caring.sass.ai.service.MinzhiChatService;
import com.caring.sass.ai.service.MinzhiSearchService;
import com.caring.sass.base.R;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * 敏智聊天接口
 *
 * @author leizhi
 */
@Api(value = "/minzhi/chat", tags = "敏智AI对话接口")
@RestController
@RequestMapping("/minzhi/chat")
public class MinzhiChatController {

    private final MinzhiChatService minzhiChatService;
    private final MinzhiSearchService searchService;

    public MinzhiChatController(MinzhiChatService minzhiChatService, MinzhiSearchService searchService) {
        this.minzhiChatService = minzhiChatService;
        this.searchService = searchService;
    }

    @ApiOperation("创建sse链接")
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestHeader Map<String, String> headers) {
        String uid = headers.get("uid");
        if (StrUtil.isBlank(uid)) {
            throw new BizException("参数错误");
        }
        return searchService.createSse(uid);
    }


    @ApiOperation("通过sse返回数据")
    @PostMapping("/sseChat")
    public R<Boolean> sseChat(@RequestBody @Validated ChatDTO chatDTO) {
        searchService.sseChat(chatDTO);
        return R.success();
    }
}
