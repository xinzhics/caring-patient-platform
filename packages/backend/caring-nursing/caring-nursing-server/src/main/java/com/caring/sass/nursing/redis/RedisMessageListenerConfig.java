package com.caring.sass.nursing.redis;

import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.nursing.service.redis.DoctorChangeNursingMessageListener;
import com.caring.sass.nursing.service.redis.NursingChangeMessageListener;
import com.caring.sass.nursing.service.redis.PatientChangeDoctorMessageListener;
import com.caring.sass.nursing.service.redis.PatientDeleteMessageListener;
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
    MessageListenerAdapter doctorChangeNursingMessageListener() {
        log.info("redis message listener config start");
        return new MessageListenerAdapter(new DoctorChangeNursingMessageListener());
    }

    @Bean
    MessageListenerAdapter nursingChangeMessageListener() {
        log.info("redis message listener config start");
        return new MessageListenerAdapter(new NursingChangeMessageListener());
    }

    @Bean
    MessageListenerAdapter patientChangeDoctorMessageListener() {
        log.info("redis message listener config start");
        return new MessageListenerAdapter(new PatientChangeDoctorMessageListener());
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        // 患者删除事件
        log.info("redisContainer channel topic start");
        container.addMessageListener(messageListener(), new ChannelTopic(SaasRedisBusinessKey.getPatientDeleteKey()));
        container.addMessageListener(doctorChangeNursingMessageListener(), new ChannelTopic(SaasRedisBusinessKey.DOCTOR_CHANGE_NURSING));
        container.addMessageListener(nursingChangeMessageListener(), new ChannelTopic(SaasRedisBusinessKey.NURSING_CHANGE));
        container.addMessageListener(patientChangeDoctorMessageListener(), new ChannelTopic(SaasRedisBusinessKey.PATIENT_CHANGE_DOCTOR));
        return container;
    }

}
