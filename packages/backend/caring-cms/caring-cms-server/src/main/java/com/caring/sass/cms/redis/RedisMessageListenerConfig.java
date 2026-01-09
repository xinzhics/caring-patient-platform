package com.caring.sass.cms.redis;

import com.caring.sass.common.redis.SaasRedisBusinessKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * @ClassName ReidsMessageListener
 * @Description
 * @Author yangShuai
 * @Date 2022/4/26 14:25
 * @Version 1.0
 */
@Slf4j
@Configuration
@Component
public class RedisMessageListenerConfig {

    @Bean
    MessageListenerAdapter messageListener() {
        log.info("redis message listener config start");
        return new MessageListenerAdapter(new PatientDeleteMessageListener());
    }


    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        // 患者删除事件
        log.info("redisContainer channel topic start");
        container.addMessageListener(messageListener(), new ChannelTopic(SaasRedisBusinessKey.getPatientDeleteKey()));
        return container;
    }

}
