package com.caring.sass.sms.service.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.sms.SmsSignatureConfig;
import com.caring.sass.sms.dao.BusinessReminderLogMapper;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.entity.BusinessReminderLog;
import com.caring.sass.sms.service.BusinessReminderLogService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * <p>
 * 业务实现类
 * 短信模版中的连接统一使用
 * // 由于参数的长度要求。
 *  https://sms.caringsaas.cn/notice/vMcMx2nw
 *
 *
 *
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-17
 */
@Slf4j
@Service

public class BusinessReminderLogServiceImpl extends SuperServiceImpl<BusinessReminderLogMapper, BusinessReminderLog> implements BusinessReminderLogService {


    @Autowired
    SmsSignatureConfig smsSignatureConfig;


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int RANDOM_STRING_LENGTH = 7;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomString(String systemEnvironment) {
        StringBuilder stringBuilder = new StringBuilder(RANDOM_STRING_LENGTH);
        for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        systemEnvironment += stringBuilder.toString();
        return systemEnvironment;
    }

    /**
     * 创建一个通知短信 并发送
     * @param businessReminderLogSaveDTO
     */
    @Override
    public void createBusinessReminderLog(BusinessReminderLogSaveDTO businessReminderLogSaveDTO) {

        BusinessReminderLog reminderLog = new BusinessReminderLog();
        BeanUtils.copyProperties(businessReminderLogSaveDTO, reminderLog);
        String queryParams = businessReminderLogSaveDTO.getQueryParams();
        JSONObject jsonObject = JSON.parseObject(queryParams);
        String templateCode = smsSignatureConfig.getTemplateCodeByReminderType(businessReminderLogSaveDTO.getType());
        String systemEnvironment = smsSignatureConfig.getSystemEnvironment();
        reminderLog.setTemplateId(templateCode);
        reminderLog.setTenantCode(businessReminderLogSaveDTO.getTenantCode());
        String string;
        int i = 0;
        while (true) {
            string = generateRandomString(systemEnvironment);
            Integer count = baseMapper.selectCount(Wraps.<BusinessReminderLog>lbQ().eq(BusinessReminderLog::getSmsParamId,  string));
            if (count == 0) {
                break;
            }
            if (i > 10) {
                baseMapper.delete(Wraps.<BusinessReminderLog>lbQ().eq(BusinessReminderLog::getSmsParamId, string));
                break;
            }
            i++;
        }
        reminderLog.setSmsParamId( string);
        baseMapper.insert(reminderLog);
        jsonObject.put("smsId", string);

        String mobile = businessReminderLogSaveDTO.getMobile();

        // 暂时只考虑 卡柠科技一个签名
        String signName = smsSignatureConfig.getName();
        try {
            SendSmsResponseBody body = sendSms(mobile, signName, templateCode, jsonObject.toJSONString());
            String code = body.getCode();
            if (StrUtil.equals("OK", code)) {
                reminderLog.setStatus(1);
            } else {
                log.error("createBusinessReminderLog sms send error: {}", JSON.toJSONString(body));
                reminderLog.setStatus(2);
                reminderLog.setErrorMessage(body.getMessage());
            }
        } catch (Exception e) {
            log.error("短信发送失败", e);
        }
        baseMapper.updateById(reminderLog);

    }

    /**
     * 调用阿里云的 短信服务。直接发送AI方面的短信
     */
    public SendSmsResponseBody sendSms(String phoneNumber, String aiSign, String templateCode, String templateParam) throws Exception {

        Config config = new Config()
                // 配置 AccessKey ID，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(System.getenv().getOrDefault("ALIBABA_CLOUD_ACCESS_KEY_ID", ""))
                // 配置 AccessKey Secret，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(System.getenv().getOrDefault("ALIBABA_CLOUD_ACCESS_KEY_SECRET", ""));

        // 配置 Endpoint
        config.endpoint = "dysmsapi.aliyuncs.com";

        Client client;
        client = new Client(config);

        // 构造请求对象，请填入请求参数值
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumber)
                .setSignName(aiSign)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam);

        // 获取响应对象
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        return sendSmsResponse.getBody();
    }



}
