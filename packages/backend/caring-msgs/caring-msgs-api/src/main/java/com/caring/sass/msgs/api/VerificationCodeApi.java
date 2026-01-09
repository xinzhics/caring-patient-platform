package com.caring.sass.msgs.api;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.CacheKey;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.sms.entity.SmsTask;
import com.caring.sass.sms.enumeration.SourceType;
import com.caring.sass.sms.enumeration.TemplateCodeType;
import com.caring.sass.utils.BizAssert;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 验证码接口
 *
 * @author xinzh
 */
@Component
@FeignClient(name = "${caring.feign.msgs-server:caring-msgs-server}", path = "/verification", qualifier = "VerificationCodeApi")
public interface VerificationCodeApi {

    /**
     * 通用的发送验证码功能
     * <p>
     * 一个系统可能有很多地方需要发送验证码（注册、找回密码等），每增加一个场景，VerificationCodeType 就需要增加一个枚举值
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "发送验证码", notes = "发送验证码")
    @PostMapping(value = "/send")
    R<Boolean> send(@RequestBody VerificationCodeDTO data);

    /**
     * 对某种类型的验证码进行校验
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "验证验证码", notes = "验证验证码")
    @PostMapping
    R<Boolean> verification(@RequestBody VerificationCodeDTO data);
}
