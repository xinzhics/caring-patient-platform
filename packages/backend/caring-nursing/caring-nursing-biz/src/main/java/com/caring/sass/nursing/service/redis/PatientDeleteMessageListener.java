package com.caring.sass.nursing.service.redis;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.common.redis.RedisMessageDto;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName
 * @Description 患者删除事件 处理
 * @Author yangShuai
 * @Date 2022/4/26 14:27
 * @Version 1.0
 */
@Slf4j
@Component
public class PatientDeleteMessageListener implements MessageListener {

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
        if (SaasRedisBusinessKey.getPatientDeleteKey().equals(channel)) {
            RedisMessageDto messageDto = JSON.parseObject(messageString, RedisMessageDto.class);
            String handleKey = SaasRedisBusinessKey.getPatientDeleteHandleKey("nursing", messageDto.getBusinessId());
            handleMessage(messageDto, handleKey);
        }

    }

    protected void handleMessage(RedisMessageDto message, String handleKey) {
        RedisMessageHandle messageHandle = getRedisMessageHandle();
        if (messageHandle == null) {
            log.info("RedisMessageHandle not find");
        } else {
            messageHandle.handleMessage(message, handleKey);
        }
    }


    public RedisMessageHandle getRedisMessageHandle() {
        if (redisMessageHandle == null) {
            redisMessageHandle = SpringUtils.getBean(RedisMessageHandle.class);
        }
        return redisMessageHandle;
    }


}
