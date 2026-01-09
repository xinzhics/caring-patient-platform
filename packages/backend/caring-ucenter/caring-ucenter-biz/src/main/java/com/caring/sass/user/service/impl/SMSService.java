package com.caring.sass.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.caring.sass.base.R;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.VerificationCodeApi;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.sms.enumeration.VerificationCodeType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.constant.SmsType;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.util.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SMSService {

    @Autowired
    NursingStaffService nursingStaffService;

    @Autowired
    VerificationCodeApi verificationCodeApi;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    public boolean sendSms(Tenant tenant, String phone, SmsType type) {
        if (type.getCode().equals(SmsType.Register.getCode())) {
            NursingStaff nursingStaff = nursingStaffService.getByMobile(phone);
            if (Objects.nonNull(nursingStaff)) {
                String mobile_number_registered = I18nUtils.getMessage("UCENTER_MOBILE_NUMBER_REGISTERED");
                throw new BizException(mobile_number_registered);
            };
        }
        if (type.getCode().equals(SmsType.ResetPwd.getCode())) {
            NursingStaff nursingStaff = nursingStaffService.getByMobile(phone);
            if (Objects.isNull(nursingStaff)) {
                String mobile_not_exist = I18nUtils.getMessage("UCENTER_MOBILE_NOT_EXIST");
                throw new BizException(mobile_not_exist);
            }
        }

        String key = "nursing_sms_" + type.getCode() + "_" + phone;
        String string = redisTemplate.opsForValue().get(key);
        if (string != null && Integer.parseInt(string) > 10) {
            throw new BizException(I18nUtils.getMessage("SMS_SEND_CODE_MORE"));
        }
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey == null || !hasKey) {
            redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().increment(key, 1);
        }

        String smsCode = RandomUtil.randomNumbers(6);
        Boolean aBoolean = this.sendSMSMessage(phone, smsCode);
        if (!aBoolean) {
            throw new BizException("短信验证码发送失败!");
        }
        key = "sms_" + type.getCode() + phone;
        BoundSetOperations<String, String> operations = redisTemplate.boundSetOps(key);
        operations.add(smsCode);

        redisTemplate.boundSetOps(key).expire(1, TimeUnit.HOURS);

        return true;
    }

    public Boolean sendSMSMessage(String phone, String smsCode) {
        VerificationCodeDTO codeDTO = new VerificationCodeDTO();
        codeDTO.setCode(smsCode);
        codeDTO.setMobile(phone);
        codeDTO.setType(VerificationCodeType.REGISTER_USER);
        R<Boolean> send = verificationCodeApi.send(codeDTO);
        if (send.getIsSuccess()) {
            return send.getData();
        }
        return null;
    }

    public void checkSMS(String phone, SmsType type, String code) {
        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(code)) {
            String key = "sms_" + type.getCode() + phone;
            Boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey == null || !hasKey) {
                throw new BizException("手机验证码不存在或已过期，请重新获取!");
            }
            BoundSetOperations<String, String> operations = redisTemplate.boundSetOps(key);
            Boolean member = operations.isMember(code);
            if (member == null || !member) {
                throw new BizException("手机验证码不存在或已过期，请重新获取!");
            }
            redisTemplate.delete(key);
        } else {
            throw new BizException("手机号或手机验证码不能为空!");
        }
    }

    public boolean equals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

}
