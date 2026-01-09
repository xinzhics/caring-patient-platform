package com.caring.sass.nursing.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}"
        , path = "/reminderLog", qualifier = "PlanReminderLogApi")
public interface PlanReminderLogApi {



    @ApiOperation("微信推送完模板消息后回调")
    @GetMapping("weixinMessageCallback")
    void weixinMessageCallback(@RequestParam("messageId") Long messageId,
                                      @RequestParam("status") Integer status,
                                      @RequestParam("tenantCode") String tenantCode);



}
