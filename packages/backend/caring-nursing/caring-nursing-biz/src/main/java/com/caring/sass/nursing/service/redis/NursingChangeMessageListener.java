package com.caring.sass.nursing.service.redis;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.common.redis.RedisMessageDoctorChangeNursing;
import com.caring.sass.common.redis.RedisMessageNursingChange;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @ClassName
 * @Description 医助将数据转移到新医助下
 * @Author yangShuai
 * @Date 2022/4/26 14:27
 * @Version 1.0
 */
@Slf4j
@Component
public class NursingChangeMessageListener implements MessageListener {

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
        if (SaasRedisBusinessKey.NURSING_CHANGE.equals(channel)) {
            RedisMessageNursingChange messageDto = JSON.parseObject(messageString, RedisMessageNursingChange.class);
            String handleKey = SaasRedisBusinessKey.getNursingChangeHandleKey("nursing",
                    messageDto.getNursingId() , messageDto.getTargetNursingId());
            handleMessage(messageDto, handleKey);
        }

    }

    protected void handleMessage(RedisMessageNursingChange message, String handleKey) {
        RedisMessageHandle messageHandle = getRedisMessageHandle();
        if (messageHandle == null) {
            log.info("RedisMessageHandle not find");
        } else {
            messageHandle.handleNursingChangeMessage(message, handleKey);
        }
    }


    public RedisMessageHandle getRedisMessageHandle() {
        if (redisMessageHandle == null) {
            redisMessageHandle = SpringUtils.getBean(RedisMessageHandle.class);
        }
        return redisMessageHandle;
    }


}
