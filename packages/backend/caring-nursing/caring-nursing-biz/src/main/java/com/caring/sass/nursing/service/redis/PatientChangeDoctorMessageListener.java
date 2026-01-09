package com.caring.sass.nursing.service.redis;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.common.redis.RedisMessagePatientChangeDoctor;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * 患者更换医生后的消息事件
 */
@Slf4j
@Component
public class PatientChangeDoctorMessageListener implements MessageListener {

    RedisMessageHandle redisMessageHandle;


    @Override
    public void onMessage(Message message, byte[] pattern) {

        StringRedisSerializer serializer = new StringRedisSerializer();
        serializer.deserialize(message.getBody());
        String messageString = message.toString();
        String channel = new String(message.getChannel());

        if (StrUtil.isEmpty(messageString) || StrUtil.isEmpty(channel)) {
            return;
        }
        if (SaasRedisBusinessKey.PATIENT_CHANGE_DOCTOR.equals(channel)) {
            RedisMessagePatientChangeDoctor messageDto = JSON.parseObject(messageString, RedisMessagePatientChangeDoctor.class);
            String handleKey = SaasRedisBusinessKey.PATIENT_CHANGE_DOCTOR + "_nursing_" + messageDto.getPatientId();
            handleMessage(messageDto, handleKey);
        }

    }

    protected void handleMessage(RedisMessagePatientChangeDoctor message, String handleKey) {
        RedisMessageHandle messageHandle = getRedisMessageHandle();
        if (messageHandle == null) {
            log.info("RedisMessageHandle not find");
        } else {
            messageHandle.handlePatientChangeDoctorMessage(message, handleKey);
        }
    }


    public RedisMessageHandle getRedisMessageHandle() {
        if (redisMessageHandle == null) {
            redisMessageHandle = SpringUtils.getBean(RedisMessageHandle.class);
        }
        return redisMessageHandle;
    }


}
