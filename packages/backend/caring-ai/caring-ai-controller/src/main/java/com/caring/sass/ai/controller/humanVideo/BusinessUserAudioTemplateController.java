package com.caring.sass.ai.controller.humanVideo;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.humanVideo.service.BusinessUserAudioTemplateService;
import com.caring.sass.base.R;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * <p>
 * 前端控制器
 * 用户提交的录音素材
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessUserAudioTemplate")
@Api(value = "BusinessUserAudioTemplate", tags = "科普名片用户提交的录音素材")
public class BusinessUserAudioTemplateController {


    @Autowired
    BusinessUserAudioTemplateService audioTemplateService;


    @ApiOperation("创建sse链接")
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestParam(name = "uid", required = true) String uid) {
        if (StrUtil.isBlank(uid)) {
            throw new BizException("会话ID不存在");
        }
        return audioTemplateService.createSse(uid);
    }


    @ApiOperation("获取对话全部内容仅限一次")
    @GetMapping("/getAllResult")
    public R<String> getAllResult(@RequestParam(name = "uid", required = true) String uid) {
        if (StrUtil.isBlank(uid)) {
            throw new BizException("会话ID不存在");
        }
        String result = audioTemplateService.getAllResult(uid);
        return R.success(result);
    }





    @ApiOperation("获取对话内容")
    @PostMapping("/getText")
    public R<Boolean> getText(@RequestParam Long userId, String uid, @RequestBody BusinessCard businessCard) {

        audioTemplateService.getText(userId, uid, businessCard);

        return R.success(true);
    }



}
