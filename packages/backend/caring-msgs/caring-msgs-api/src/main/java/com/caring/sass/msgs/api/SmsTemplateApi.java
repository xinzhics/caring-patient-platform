package com.caring.sass.msgs.api;

import com.caring.sass.base.api.SaveApi;
import com.caring.sass.sms.dto.SmsTemplateSaveDTO;
import com.caring.sass.sms.entity.SmsTemplate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * 短信模板接口
 *
 * @author xinzh
 */
@Component
@FeignClient(name = "${caring.feign.msgs-server:caring-msgs-server}", path = "/smsTemplate", qualifier = "SmsTemplateApi")
public interface SmsTemplateApi extends SaveApi<SmsTemplate, SmsTemplateSaveDTO> {

}
