package com.caring.sass.msgs.redis;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.common.redis.RedisMessageDoctorChangeNursing;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @ClassName
 * @Description 医生更换医助事件
 * @Author yangShuai
 * @Date 2022/4/26 14:27
 * @Version 1.0
 */
@Slf4j
@Component
public class DoctorChangeNursingMessageListener implements MessageListener {

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
        if (SaasRedisBusinessKey.DOCTOR_CHANGE_NURSING.equals(channel)) {
            RedisMessageDoctorChangeNursing messageDto = JSON.parseObject(messageString, RedisMessageDoctorChangeNursing.class);
            handleMessage(messageDto);
        }

    }

    protected void handleMessage(RedisMessageDoctorChangeNursing message) {
        RedisMessageHandle messageHandle = getRedisMessageHandle();
        if (messageHandle == null) {
            log.info("RedisMessageHandle not find");
        } else {
            messageHandle.handleDoctorChangeNursingMessage(message);
        }
    }


    public RedisMessageHandle getRedisMessageHandle() {
        if (redisMessageHandle == null) {
            redisMessageHandle = SpringUtils.getBean(RedisMessageHandle.class);
        }
        return redisMessageHandle;
    }


}
