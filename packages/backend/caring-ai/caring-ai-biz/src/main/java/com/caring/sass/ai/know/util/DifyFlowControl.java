package com.caring.sass.ai.know.util;

import com.caring.sass.ai.know.config.DifyApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DifyFlowControl {

    @Autowired
    DifyApiConfig difyApiConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public static String DIFY_FLOW_MINUTE_FLOW_CONTROL = "DIFY_FLOW_MINUTE_FLOW_CONTROL:";


    /**
     * 查询redis的计数器。
     * 检查当前这分钟，是否还有调用dify的接口次数。
     * 如果没有，则线程睡眠到下一分钟，再请求。
     * @return
     */
    private synchronized String getRedisKey() {

        LocalDateTime now = LocalDateTime.now();
        now.withNano(0);
        now.withSecond(0);
        String key = DIFY_FLOW_MINUTE_FLOW_CONTROL + now;
        redisTemplate.opsForValue().setIfAbsent(key, (Long.MAX_VALUE - difyApiConfig.getFlowControl()) + "", 70, TimeUnit.SECONDS);
        return key;
    }

    /**
     * 每2秒去尝试获取一个请求dify接口的机会。
     * 由于 dify 并发为1000 。1分钟只能有1000个请求。
     * 使用redis。先根据当前时间的 时分秒 去获取一个key
     * 然后使用key 对 value + 1
     * 由于value 最大为Long.MAX_VALUE。
     * 超过最大值，则表示当前秒已经没有可用的请求了。
     * 等待下一秒在请求。
     */
    public void whenRedisValueIncrSuccess() {
        String redisKey = getRedisKey();
        try {
            redisTemplate.opsForValue().increment(redisKey);
        } catch (Exception e) {
            try {
                Thread.sleep(2000);
                whenRedisValueIncrSuccess();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }



}
