package com.caring.sass.sms.strategy.impl;

import cn.hutool.http.HttpUtil;
import com.caring.sass.sms.enumeration.ProviderType;
import com.caring.sass.sms.strategy.domain.SmsDO;
import com.caring.sass.sms.strategy.domain.SmsResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 吉信通短信发送
 *
 * @author xinzh
 */
@Component("JXT")
@Slf4j
public class SmsJxtStrategy extends AbstractSmsStrategy {

    private static String smsUser = "caringsaas";
    private static String password = "password";
    private static String restServiceUrl = "http://service2.winic.org/Service.asmx/SendMessages";
    @Override
    protected SmsResult send(SmsDO smsDO) {
        String r = "";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("uid=").append(smsUser);
            sb.append("&pwd=").append(password);
            sb.append("&tos=").append(smsDO.getPhone());
            sb.append("&msg=").append(URLEncoder.encode(smsDO.getSignName() + smsDO.getContent(), StandardCharsets.UTF_8.toString()));
            sb.append("&otime=");
            String param = sb.toString();
            log.info("发送吉信通短信，参数为{}", param);
            r = HttpUtil.post(restServiceUrl, param);
            log.info("吉信通短信结果{}", r);
        } catch (Exception e) {
            log.error(e.getMessage());
            return SmsResult.fail(e.getMessage());
        }
        return SmsResult.build(ProviderType.JXT, "000",
                "", r, "", 0);
    }



}
