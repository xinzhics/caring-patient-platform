package com.caring.sass.wx.service.config.wxPay;

import cn.hutool.core.util.StrUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.util.IOUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 微信商户 ckd营养小程序 配置
 */
@Slf4j
@Component
public class CKDMerchantConfig {


    /** 商户号 */
    @Getter
    public String merchantId = "1709710980";

    /** 商户API私钥路径 */
    public static String privateKeyName = "apiclient_ckd_key3.pem";

    /** 商户证书序列号  2024/07/03至2029/07/02*/
    public static String merchantSerialNumber = "21A8664D743D432C3695930DF908656B263E87AC";

    /** 商户APIV3密钥 */
    public static String apiV3Key = "2ac9754a0ae54bbc8cc41deb64281521";

    /**
     * 微信支付的回调
     */
    @Getter
    public String notifyUrl = "/api/wx/pay/anno/callback/ckd";

    @Getter
    public String newNotifyUrl = "/api/wx/pay/anno/callback/all/1709710980";

    @Getter
    public String refundsNotifyUrl = "/api/wx/refund/anno/callback/1709710980";


    @Getter
    public Config config = null;

    @Getter
    public NotificationConfig notificationConfig = null;

    public CKDMerchantConfig() {
        initConfig();
    }


    protected void initConfig() {
        String privateKey = readPrivateKey();
        if (StrUtil.isEmpty(privateKey)) {
            return;
        }
        config = new RSAAutoCertificateConfig.Builder()
                .merchantId(merchantId)
                .privateKey(privateKey)
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();

        notificationConfig = new RSAAutoCertificateConfig.Builder()
                .merchantId(merchantId)
                .privateKey(privateKey)
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();
    }

    protected String readPrivateKey() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String privateKey = null;
        try (InputStream inputStream = classLoader.getResourceAsStream(privateKeyName)) {
            privateKey = IOUtil.toString(inputStream);

        } catch (Exception e) {
            log.error(" load apiclient_key error, file read error ");
            e.printStackTrace();
        }
        return privateKey;
    }







}
