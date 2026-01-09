package com.caring.sass.msgs.bot;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.bot.baiduModel.Content;
import com.caring.sass.msgs.bot.baiduModel.Message;
import com.caring.sass.msgs.bot.baiduModel.MessageBean;
import com.caring.sass.msgs.entity.BaiduBotDoctorChat;
import com.unfbx.chatgpt.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @className: BaiduThirdCommonV1DiagnoseBot
 * @author: 杨帅
 * @date: 2024/2/29
 */
@Slf4j
@Component
public class BaiduThirdCommonV1DiagnoseBot {

    private static String host = "https://01bot.baidu.com";

    @Autowired
    CacheRepository cacheRepository;

    public static String getMd5(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    private static String string2Unicode(String string) {
        if (string.equals("")) {
            return null;
        }
        char[] bytes = string.toCharArray();
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            char c = bytes[i];

            // 标准ASCII范围内的字符，直接输出
            if (c >= 0 && c <= 127) {
                unicode.append(c);
                continue;
            }
            String hexString = Integer.toHexString(bytes[i]);

            unicode.append("\\u");

            // 不够四位进行补0操作
            if (hexString.length() < 4) {
                unicode.append("0000".substring(hexString.length(), 4));
            }
            unicode.append(hexString);
        }
        return unicode.toString();
    }

    public static String hmacSha256(String secret, String message) throws NoSuchAlgorithmException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }


    /**
     * 使用 baiduBotDoctorChats 记录。生产消息体
     * @param baiduBotDoctorChats
     * @return
     */
    public static ArrayList<Message> getMessages(List<BaiduBotDoctorChat> baiduBotDoctorChats) {
        ArrayList<Message> messages = new ArrayList<>();
        Message message;
        for (BaiduBotDoctorChat doctorChat : baiduBotDoctorChats) {
            message = new Message();
            message.setVersion("api-v2");
            LocalDateTime createTime = doctorChat.getCreateTime();
            createTime = createTime.withNano(0);
            long seconds = createTime.toEpochSecond(ZoneOffset.of("+8"));
            message.setRole(doctorChat.getSenderRoleType());
            message.setCreated(seconds);
            Content content = new Content();
            content.setType("text");
            content.setBody(doctorChat.getContent());
            ArrayList<Content> contents = new ArrayList<>();
            contents.add(content);
            message.setContent(contents);
            messages.add(message);
        }
        return messages;
    }


    public void sendBaiduBot(String uid, List<BaiduBotDoctorChat> baiduBotDoctorChats) throws Exception {
        String ak = ApplicationProperties.getBaiduBotAK();
//        String ak = "dd3f1b0ce88b6df86216766980ac6e547d19c75495bfcf108f78b7c012c13d13acb4f58a4552c5fa7f0c65b96dd00b02e4f428eff935e17aa5434bc8ffa823ef1e01cd97e134253b32b96e4f77076e7b";
        String sk = ApplicationProperties.getBaiduBotSK();
//        String sk = "939e384850e9915c45db5b27f45dc3314e416df5";
        MessageBean messageBean = new MessageBean();
        messageBean.setModel("third-common-v1-diagnose");
        messageBean.setStream(true);

        ArrayList<Message> messages = getMessages(baiduBotDoctorChats);
        messageBean.setMessages(messages);
        String messageJson = JSONObject.toJSONString(messageBean);
        System.out.println(messageJson);
        messageJson = string2Unicode(messageJson);
        String md5 = "";
        try {
            md5 = getMd5(messageJson);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot get content md5");
            System.exit(0);
        }
        DateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        String trimester = sdf.format(new Date());
        String authStringPrefix = "ihcloud/" + ak + "/" + trimester + "/300";
        String signingKey = "";
        try {
            signingKey = hmacSha256(sk, authStringPrefix);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot get content md5");
            System.exit(0);
        }
        String path = "/api/01bot/sse-gateway/stream";
        String url = host + path;
        String canonicalRequest = String.join("\n", "POST", path, "content-md5:" + md5);
        String signature = "";
        try {
            signature = hmacSha256(signingKey, canonicalRequest);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("gen signature failed");
            System.exit(0);
        }
        // 使用 okHttp3 sse 方式请求接口
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        EventSource.Factory factory = EventSources.createFactory(client);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, messageJson);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-IHU-Authorization-V2", authStringPrefix + "/" + signature)
                .post(body)
                .build();
        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BaseException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }
        BaiduBotSSEEventSourceListener sourceListener = new BaiduBotSSEEventSourceListener(sseEmitter, cacheRepository, uid, BaseContextHandler.getTenant());
//        BaiduBotSSEEventSourceListener sourceListener = new BaiduBotSSEEventSourceListener(sseEmitter, null, uid, "0112");
        factory.newEventSource(request, sourceListener);

    }

    public static void main(String[] args) {
        BaiduBotDoctorChat botDoctorChat = new BaiduBotDoctorChat();
        botDoctorChat.setContent("你好。喉咙里有好多粘性的痰，但是我不咳嗽不感冒，咽口水时明显有痰卡着不舒服，使劲吐痰就像绳一样一直抽的出来");
        botDoctorChat.setCreateTime(LocalDateTime.now());
        botDoctorChat.setSenderRoleType(BaiduBotDoctorChat.User);
        List<BaiduBotDoctorChat> objects = new ArrayList<>();
        objects.add(botDoctorChat);
        BaiduThirdCommonV1DiagnoseBot baiduThirdCommonV1DiagnoseBot = new BaiduThirdCommonV1DiagnoseBot();

        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        long time = new Date().getTime();
        String uid = "uid" + time;
        SseEmitterSession.put("uid" + time, sseEmitter);
        try {
            baiduThirdCommonV1DiagnoseBot.sendBaiduBot(uid, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
