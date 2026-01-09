package com.caring.sass.msgs.api;

import com.caring.sass.base.R;
import com.caring.sass.msgs.api.fallback.GroupImFallback;
import com.caring.sass.msgs.dto.GptDoctorChatSaveDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName GroupImApi
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 16:29
 * @Version 1.0
 **/
@Component
@FeignClient(name = "${caring.feign.msgs-server:caring-msgs-server}",qualifier = "GptDoctorChatApi",path = "gptDoctorChat")
public interface GptDoctorChatApi {

    @ApiOperation("检查医生是否接收过敏智介绍")
    @GetMapping("checkSendAiIntroduction")
    R<Boolean> checkSendAiIntroduction(@RequestParam("doctorId") Long doctorId, @RequestParam("imAccount") String imAccount);


    @ApiOperation("当文章关键词触发到医生时，给医生推送文章资讯")
    @PutMapping("sendCms")
    R<Boolean> sendCms(@RequestBody GptDoctorChatSaveDTO chatSaveDTO);

}
