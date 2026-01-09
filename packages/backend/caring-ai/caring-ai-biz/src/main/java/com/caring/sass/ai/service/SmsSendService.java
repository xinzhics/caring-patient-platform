package com.caring.sass.ai.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.caring.sass.ai.config.SmsSignatureConfig;
import com.caring.sass.ai.entity.sms.AiSmsTask;
import com.caring.sass.ai.sms.dao.AiSmsTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SmsSendService {

    @Autowired
    SmsSignatureConfig smsSignatureConfig;

    @Autowired
    AiSmsTaskMapper smsTaskMapper;

    @Autowired
    AliYunAccessKey aliYunAccessKey;

    /**
     * 调用短信服务
     * @param phone
     * @param smsCode
     * @return
     */
    public String sendSMS(String phone, String smsCode) {

        String aiSign = smsSignatureConfig.getName();
        String content = "【" + aiSign + "】" + "您的验证码是" + smsCode + ",请于5分钟内正确输入!";


        AiSmsTask smsTask = new AiSmsTask();
        smsTask.setSendTime(LocalDateTime.now());
        smsTask.setReceiver(phone);
        smsTask.setContent(content);
        smsTask.setTemplateParams(smsCode);
        try {
            SendSmsResponseBody body = sendSms(phone, aiSign, smsSignatureConfig.getTemplateCode(), smsCode);
            String code = body.getCode();
            if (StrUtil.equals("OK", code)) {
                smsTask.setStatus("SUCCESS");
                smsTask.setBizId(body.getBizId());
            } else {
                smsTask.setStatus(code);
            }
        } catch (Exception e) {
            smsTask.setStatus("-1");
        }
        smsTaskMapper.insert(smsTask);
        if (smsTask.getStatus().equals("SUCCESS")) {
            return smsTask.getStatus();
        } else {
            return null;
        }

    }


    /**
     * 调用阿里云的 短信服务。直接发送AI方面的短信
     */
    public SendSmsResponseBody sendSms(String phoneNumber, String aiSign, String templateCode, String code) throws Exception {

        Config config = new Config()
                // 配置 AccessKey ID，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(aliYunAccessKey.getAccessKeyId())
                // 配置 AccessKey Secret，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(aliYunAccessKey.getSecret());

        // 配置 Endpoint
        config.endpoint = "dysmsapi.aliyuncs.com";

        Client client = new Client(config);

        JSONObject object = new JSONObject();
        object.put("code", code);
        // 构造请求对象，请填入请求参数值
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumber)
                .setSignName(aiSign)
                .setTemplateCode(templateCode)
                .setTemplateParam(object.toJSONString());

        // 获取响应对象
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        return sendSmsResponse.getBody();
    }



}
