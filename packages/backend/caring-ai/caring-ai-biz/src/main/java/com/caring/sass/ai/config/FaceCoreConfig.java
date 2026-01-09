package com.caring.sass.ai.config;

import com.caring.sass.ai.dto.CozeToken;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Component
public class FaceCoreConfig {

    private final Object lock = new Object();
    /**
     * 游标
     */
    public AtomicInteger ruler = new AtomicInteger(0);

    /**
     * 最大游标 默认是 0 。每添加一个token，增1
     */
    public AtomicInteger maxRuler = new AtomicInteger(0);

    /**
     * token的列表
     */
    private List<String> tokens;

    /**
     * token对应的botId
     */
    private Map<String, String> tokenBotId;


    public FaceCoreConfig() {
        // 默认的token 和 botId - 使用环境变量
        String defaultToken = "Bearer " + System.getenv().getOrDefault("COZE_FACE_TOKEN", "");
        String defaultBotId = System.getenv().getOrDefault("COZE_FACE_BOT_ID", "7386940646856704038");
        tokenBotId = new HashMap<>();
        tokens = new ArrayList<>();
        if (!defaultToken.replace("Bearer ", "").isEmpty()) {
            tokenBotId.put(defaultToken, defaultBotId);
            tokens.add(defaultToken);
        }
    }


    /**
     * 添加扣子 人脸合成的 token 和botId
     * @param token
     * @param botId
     */
    public void addToken(String token, String botId) {
        // 添加token和botId的逻辑
        synchronized(lock) {
            tokens.add(token);
            tokenBotId.put(token, botId);
            maxRuler.incrementAndGet();
            System.out.println(Thread.currentThread().getId() + " add token:" + token);
        }

    }

    /**
     * 移除扣子token
     * @param token
     */
    public void removeToken(String token) {
        // 添加token和botId的逻辑
        synchronized(lock) {
            tokens.remove(token);
            tokenBotId.remove(token);
            maxRuler.decrementAndGet();
            System.out.println(Thread.currentThread().getId() + " remove token:" + token);
        }
    }


    /**
     * 获取一个token
     * @return
     */
    private CozeToken getToken() {
        CozeToken cozeToken = null;
        synchronized(lock) {
            int index = ruler.get();
            if (index <= maxRuler.get()) {
                index = ruler.getAndIncrement();
                String token = tokens.get(index);
                String botId = tokenBotId.get(token);
                cozeToken = new CozeToken(token, botId);
            } else {
                // 重置ruler，如果需要的话
                ruler.set(0);
            }
        }
        // 返回token
        return cozeToken;
    }

    public CozeToken getTokenThenNoNull() {
        CozeToken cozeToken = null;
        while (cozeToken == null) {
            cozeToken = getToken();
        }
        return cozeToken;
    }

//    public static void main(String[] args) {
//        FaceCoreConfig faceCoreConfig = new FaceCoreConfig();
//        faceCoreConfig.addToken("token1", "botId1");
//        faceCoreConfig.addToken("token2", "botId2");
//        faceCoreConfig.addToken("token3", "botId3");
//
//        new Thread(() -> {
//            for (int i=0;i<3;i++) {
//                CozeToken cozeToken = faceCoreConfig.getTokenThenNoNull();
//                long id = Thread.currentThread().getId();
//                System.out.println( id + ":" + cozeToken);
//            }
//        }).start();
//
//        new Thread(() -> {
//            faceCoreConfig.addToken("token4", "botId4");
//            faceCoreConfig.removeToken("token1");
//            faceCoreConfig.addToken("token5", "botId5");
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//
//        new Thread(() -> {
//            for (int i=0;i<3;i++) {
//                CozeToken cozeToken = faceCoreConfig.getTokenThenNoNull();
//                long id = Thread.currentThread().getId();
//                System.out.println( id + ":" + cozeToken);
//            }
//        }).start();
//
//
//
//        new Thread(() -> {
//            for (int i=0;i<3;i++) {
//                CozeToken cozeToken = faceCoreConfig.getTokenThenNoNull();
//                long id = Thread.currentThread().getId();
//                System.out.println( id + ":" + cozeToken);
//            }
//        }).start();
//
//    }



}
