package com.caring.sass.user.controller;

import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.constant.SmsType;
import com.caring.sass.user.service.impl.SMSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Api(value = "SMS", tags = "短信")
@RestController
@RequestMapping({"/sms/anno"})
public class SMSController {


    @Autowired
    TenantApi tenantApi;

    @Autowired
    SMSService smsService;

    @ApiOperation("发送注册，找回密码，医生登录的短信验证码")
    @RequestMapping(value = {"/{type}/sendCode/{phone}"}, method = {RequestMethod.GET})
    public R sendCode(@PathVariable("phone") String phone, @PathVariable("type") SmsType type) throws Exception {
        String tenantCode = BaseContextHandler.getTenant();
        R<Tenant> apiTenant = tenantApi.getByCode(tenantCode);
        if (apiTenant.getIsSuccess() && Objects.nonNull(apiTenant.getData())) {
            Tenant tenant = apiTenant.getData();
            boolean sendSms = smsService.sendSms(tenant, phone, type);
            return R.success(sendSms);
        } else {
            return R.fail("项目不存在");
        }
    }

    @RequestMapping(
            value = {"checkCode"},
            method = {RequestMethod.GET}
    )
    @ApiOperation("校验短信验证码")
    public R checkCode(@ApiParam(value = "接受验证码的手机",required = true) @RequestParam("phone") String phone,
                       @RequestParam("code") String code,
                       @RequestParam("type") SmsType type) {
        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(code)) {
            smsService.checkSMS(phone, type, code);
        } else {
            return R.fail("手机号或验证码不能为空!");
        }
        return R.success();
    }


}
