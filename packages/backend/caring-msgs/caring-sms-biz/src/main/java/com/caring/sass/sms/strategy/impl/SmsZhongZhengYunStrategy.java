package com.caring.sass.sms.strategy.impl;

import com.caring.sass.sms.config.SmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName SmsChuangLanStrategy
 * @Description 中正云的
 * @Author yangShuai
 * @Date 2020/10/13 14:47
 * @Version 1.0
 */
@Component("ChuangLan")
@Slf4j
public class SmsZhongZhengYunStrategy {
    
    @Autowired
    private SmsConfig smsConfig;

    public String sendMessage(String phoneNums, String content) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("uid=" + smsConfig.getSmsUser());
        sb.append("&pwd=" + smsConfig.getPassword());
        sb.append("&tos=" + phoneNums);
        sb.append("&msg=").append(URLEncoder.encode(content, StandardCharsets.UTF_8.toString()));
        sb.append("&otime=");
        return post(smsConfig.getRestServiceUrl(), sb.toString());
    }



    public String post(String URL, String Data) throws Exception {
        BufferedReader In = null;
        PrintWriter Out = null;
        HttpURLConnection HttpConn = null;

        try {
            java.net.URL url = new URL(URL);
            HttpConn = (HttpURLConnection)url.openConnection();
            HttpConn.setRequestMethod("POST");
            HttpConn.setDoInput(true);
            HttpConn.setDoOutput(true);
            Out = new PrintWriter(HttpConn.getOutputStream());
            Out.println(Data);
            Out.flush();
            if (HttpConn.getResponseCode() != 200) {
                throw new Exception("HTTP_POST_ERROR_RETURN_STATUS：" + HttpConn.getResponseCode());
            }

            StringBuffer content = new StringBuffer();
            String tempStr = "";
            In = new BufferedReader(new InputStreamReader(HttpConn.getInputStream()));

            while((tempStr = In.readLine()) != null) {
                content.append(tempStr);
            }

            In.close();
            return content.toString();
        } catch (IOException var12) {
            var12.printStackTrace();
        } finally {
            Out.close();
            HttpConn.disconnect();
        }

        return null;
    }
}
