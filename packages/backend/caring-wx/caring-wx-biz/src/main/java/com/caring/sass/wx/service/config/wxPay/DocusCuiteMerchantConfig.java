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
 * 微信商户 哆咔叽DocusCuite 配置
 *
 * 哆咔叽  操作密码 857625
 * 敏瑞      操作密码 857625
 *
 */
@Slf4j
@Component
public class DocusCuiteMerchantConfig {


    /** 商户号 */
    @Getter
    public String merchantId = "1680458756";

    /** 商户API私钥路径 */
    public static String privateKeyName = "apiclient_docusduite_key.pem";

    /** 商户证书序列号  2024/07/03至2029/07/02*/
    public static String merchantSerialNumber = "77093ABD5DAC3B30B99FB625D196154802B349BA";

    /** 商户APIV3密钥 */
    public static String apiV3Key = "2ac9754a0ae54bbc8cc41deb64281521";

    /**
     * 微信支付的回调
     */
    @Getter
    public String notifyUrl = "/api/wx/pay/anno/callback";

    @Getter
    public String newNotifyUrl = "/api/wx/pay/anno/callback/all/1680458756";

    @Getter
    public String refundsNotifyUrl = "/api/wx/refund/anno/callback/1680458756";


    @Getter
    public Config config = null;

    @Getter
    public NotificationConfig notificationConfig = null;

    public DocusCuiteMerchantConfig() {
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
