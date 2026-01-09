package com.caring.sass.ai.config;

import com.caring.sass.exception.BizException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * @author xinzh
 */
@Component
@Data
@ConfigurationProperties(prefix = CozeConfig.PREFIX)
public class CozeConfig {
    public static final String PREFIX = "coze.config";

    public static String API = "/open_api/v2/chat";

    @ApiModelProperty(value = "人脸融合的botId")
    private String faceBotId;

    /**
     * 旷视face的appKey
     */
    @ApiModelProperty(value = "客户端请求的访问令牌")
    private String authorization;

    @ApiModelProperty(value = "code域名")
    private String domain;

    @Autowired
    RedisTemplate<String, String>  redisTemplate;



    /**
     * 验证本秒是否已经超过2次的限制
     * @param secondKey
     */
    public void secondCheck(String secondKey, String token) {
        String count = redisTemplate.opsForValue().get(secondKey);
        if (count == null) {
            redisTemplate.opsForValue().set(secondKey, (Long.MAX_VALUE-1)+ "", 5L, TimeUnit.SECONDS);
        } else {
            try {
                redisTemplate.opsForValue().increment(secondKey);
            } catch (Exception e) {
                try {
                    Thread.sleep(1000L);
                    LocalTime second = LocalTime.now().withNano(0);
                    secondKey = "coze_count" + second.toString();
                    secondCheck(secondKey, token);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /**
     * 验证本分钟是否已经超过60次的限制
     * @param minuteKey
     */
    public void minuteCheck(String minuteKey) {
        String count = redisTemplate.opsForValue().get(minuteKey);
        if (count == null) {
            redisTemplate.opsForValue().set(minuteKey, (Long.MAX_VALUE-59)+ "", 2, TimeUnit.MINUTES);
        } else {
            try {
                redisTemplate.opsForValue().increment(minuteKey);
            } catch (Exception e) {
                throw new BizException("真的很抱歉，产品太火爆啦，本分钟的额度用完啦，请稍后再试哟！");
            }
        }
    }


    /**
     * 验证本天是否已经超过3000次的限制
     * @param dayKey
     */
    public void dayCheck(String dayKey) {
        String count = redisTemplate.opsForValue().get(dayKey);
        if (count == null) {
            redisTemplate.opsForValue().set(dayKey, (Long.MAX_VALUE-2999)+ "", 24, TimeUnit.HOURS);
        } else {
            try {
                redisTemplate.opsForValue().increment(dayKey);
            } catch (Exception e) {
                throw new BizException("真的很抱歉，产品太火爆啦，今天的额度用完啦，请明天再试吧！");
            }
        }
    }


    public synchronized void checkCanQueryCoze(String token) {
        LocalTime time = LocalTime.now();
        LocalTime second = time.withNano(0);
        LocalTime minute = time.withNano(0).withSecond(0);
        LocalDate localDate = LocalDate.now(); // 天
        // 验证本分钟是否已经超过60次的限制

        // 验证本天是否已经超过3000次的限制

        // 获取 redis 中存储的 coze 的访问次数
        String secondKey = "coze_count" + second.toString() + ":" + token;
        String minuteKey = "coze_count" + minute.toString() + ":" + token;
        String dayKey = "coze_count" + localDate + ":" + token;
        dayCheck(dayKey);
        minuteCheck(minuteKey);
        secondCheck(secondKey, token);

    }


}
