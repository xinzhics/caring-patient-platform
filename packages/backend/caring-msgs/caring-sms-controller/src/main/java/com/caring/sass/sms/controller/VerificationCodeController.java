package com.caring.sass.sms.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.common.constant.CacheKey;
import com.caring.sass.exception.BizException;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.sms.entity.SmsTask;
import com.caring.sass.sms.enumeration.SourceType;
import com.caring.sass.sms.enumeration.TemplateCodeType;
import com.caring.sass.sms.service.SmsTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 通用验证码
 *
 * @author caring
 * @date 2019/08/06
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/verification")
@Api(value = "VerificationCode", tags = "验证码")
public class VerificationCodeController {

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Autowired
    private SmsTaskService smsTaskService;

    private static final Long DEF_TIMEOUT_2M = 120L;

    @ApiOperation(value = "发送验证码", notes = "发送验证码")
    @PostMapping(value = "anno/send")
    public R<Boolean> annoSend(@Validated @RequestBody VerificationCodeDTO data) {
        return send(data);
    }

    /**
     * 通用的发送验证码功能
     * <p>
     * 一个系统可能有很多地方需要发送验证码（注册、找回密码等），每增加一个场景，VerificationCodeType 就需要增加一个枚举值
     * TODO：医生登录发送验证码
     * @param data
     * @return
     */
    @ApiOperation(value = "发送验证码", notes = "发送验证码")
    @PostMapping(value = "/send")
    public R<Boolean> send(@Validated @RequestBody VerificationCodeDTO data) {

        String code = data.getCode();
        if (StrUtil.isBlank(code) || code.equals("1")) {
            code = RandomUtil.randomNumbers(6);
        }
        SmsTask smsTask = SmsTask.builder().build();
        smsTask.setSourceType(SourceType.SERVICE);
        JSONObject param = new JSONObject();
        param.put("code", code);
        smsTask.setTemplateParams(param.toString());
        smsTask.setReceiver(data.getMobile());

        // 调用此方法的 业务 包括 超管登录， 医助注册 找回密码， 医生登录。 验证码次数由业务自己校验。
//        String key = "user_login_sms_" + data.getMobile();
//        String string = redisTemplate.opsForValue().get(key);
//        if (string != null && Integer.parseInt(string) > 5) {
//            throw new BizException("验证码发送次数过多。请稍后再试");
//        }
//        Boolean hasKey = redisTemplate.hasKey(key);
//        if (hasKey == null || !hasKey) {
//            redisTemplate.opsForValue().increment(key, 1);
//            redisTemplate.expire(key, 1, TimeUnit.HOURS);
//        } else {
//            redisTemplate.opsForValue().increment(key, 1);
//        }

        smsTask.setDraft(false);
        String key = CacheKey.buildTenantKey(CacheKey.REGISTER_USER, data.getType().name(), data.getMobile());
        smsTaskService.saveTask(smsTask, TemplateCodeType.COMMON_SMS);
        redisTemplate.boundSetOps(key).add(code);
        redisTemplate.boundSetOps(key).expire(10, TimeUnit.MINUTES);
        return R.success();
    }

    /**
     * 对某种类型的验证码进行校验
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "验证验证码", notes = "验证验证码")
    @PostMapping
    public R<Boolean> verification(@Validated(SuperEntity.Update.class) @RequestBody VerificationCodeDTO data) {
        String key = CacheKey.buildTenantKey(CacheKey.REGISTER_USER, data.getType().name(), data.getMobile());
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey == null || !hasKey) {
            return R.success(false);
        }
        Boolean member = redisTemplate.boundSetOps(key).isMember(data.getCode());
        if (member == null || !member) {
            return R.success(false);
        }
        Boolean clearKey = data.getClearKey();
        if (clearKey) {
            redisTemplate.delete(key);
        }
        return R.success(true);
    }



    @ApiOperation(value = "验证验证码", notes = "验证验证码")
    @PostMapping("anno")
    public R<Boolean> annoVerification(@RequestBody VerificationCodeDTO data) {
        return verification(data);
    }

}
