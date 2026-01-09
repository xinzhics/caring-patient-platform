package com.caring.sass.wx.service.config.wxPay;

import cn.hutool.core.util.StrUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.RSAPublicKeyNotificationConfig;
import com.wechat.pay.java.core.util.IOUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 凯琳。 科普创作商户
 */
@Slf4j
@Component
public class CaringArticleMerchantConfig {


    /** 商户号 */
    @Getter
    public String merchantId = "1724249213";

    /** 商户API私钥路径 */
    public static String privateKeyName = "apiclient_article_key.pem";

    /** 商户证书序列号  2024/07/03至2029/07/02*/
    public static String merchantSerialNumber = "22D6374D1FCC20A020DC2D64BB37AB16AF4F3821";

    /** 商户APIV3密钥 */
    public static String apiV3Key = "2ac9754a0ae54bbc8cc41deb64281521";

    public static String publicKeyPath = "pub_article_key.pem";

    public static String publicKeyId = "PUB_KEY_ID_0117242492132025080500212066002004";

    @Getter
    public String newNotifyUrl = "/api/wx/pay/anno/callback/all/1724249213";

    @Getter
    public String refundsNotifyUrl = "/api/wx/refund/anno/callback/1724249213";

    @Getter
    public Config config = null;

    @Getter
    public NotificationConfig notificationConfig = null;

    public CaringArticleMerchantConfig() {
        initConfig();
    }


    protected void initConfig() {
        String privateKey = readPrivateKey();
        String publicKey = readPublicKey();
        if (StrUtil.isEmpty(privateKey)) {
            return;
        }
        config = new RSAPublicKeyConfig.Builder()
                .merchantId(merchantId)
                .privateKey(privateKey)
                .publicKey(publicKey)
                .publicKeyId(publicKeyId)
                .merchantSerialNumber(merchantSerialNumber)
                .apiV3Key(apiV3Key)
                .build();

        notificationConfig = new RSAPublicKeyNotificationConfig.Builder()
                .publicKey(publicKey)
                .publicKeyId(publicKeyId)
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

    protected String readPublicKey() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String publicKey = null;
        try (InputStream inputStream = classLoader.getResourceAsStream(publicKeyPath)) {
            publicKey = IOUtil.toString(inputStream);

        } catch (Exception e) {
            log.error(" load apiclient_key error, file read error ");
            e.printStackTrace();
        }
        return publicKey;
    }







}
