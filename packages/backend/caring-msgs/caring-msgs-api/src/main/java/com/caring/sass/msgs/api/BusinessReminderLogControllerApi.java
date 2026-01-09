package com.caring.sass.msgs.api;

import com.caring.sass.base.R;
import com.caring.sass.msgs.api.fallback.BusinessReminderLogApiFallback;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "${caring.feign.msgs-server:caring-msgs-server}", path = "/businessReminderLog",
        fallback = BusinessReminderLogApiFallback.class, qualifier = "BusinessReminderLogControllerApi")
public interface BusinessReminderLogControllerApi {


    @ApiOperation("保存短信并发送")
    @PostMapping("/send")
    R<Boolean> sendNoticeSms(@RequestBody BusinessReminderLogSaveDTO businessReminderLogSaveDTO);

}
