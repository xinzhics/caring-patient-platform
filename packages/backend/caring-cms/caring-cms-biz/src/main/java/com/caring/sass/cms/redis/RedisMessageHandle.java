package com.caring.sass.cms.redis;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.cms.dao.ContentReplyMapper;
import com.caring.sass.cms.dao.ReplyLikeLogMapper;
import com.caring.sass.cms.entity.ContentReply;
import com.caring.sass.cms.entity.ReplyLikeLog;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.RedisMessageDto;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisMessageHandle
 * @Description
 * @Author yangShuai
 * @Date 2022/4/26 16:04
 * @Version 1.0
 */
@Slf4j
@Component
public class RedisMessageHandle {

    @Autowired
    ReplyLikeLogMapper replyLikeLogMapper;

    @Autowired
    ContentReplyMapper contentReplyMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 患者删除 事件 处理
     * @param messageDto
     */
    public void handleMessage(RedisMessageDto messageDto) {

        String tenantCode = messageDto.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        String businessId = messageDto.getBusinessId();
        if (StrUtil.isEmpty(businessId)) {
            return;
        }
        long patientId = Long.parseLong(businessId);
        replyLikeLogMapper.delete(Wraps.<ReplyLikeLog>lbQ()
                .eq(ReplyLikeLog::getUserId, patientId)
                .eq(ReplyLikeLog::getRoleType, UserType.UCENTER_PATIENT));

        contentReplyMapper.delete(Wraps.<ContentReply>lbQ()
                .eq(ContentReply::getReplierId, patientId)
                .eq(ContentReply::getRoleType, UserType.UCENTER_PATIENT));

    }


    public void handleMessage(RedisMessageDto message, String handleKey) {
        if (redisTemplate == null) {
            handleMessage(message, handleKey);
        } else {
            Boolean cms = redisTemplate.opsForValue().setIfAbsent(handleKey, "1");
            if (cms != null && cms) {
                redisTemplate.expire(handleKey, 13, TimeUnit.SECONDS);
                SaasGlobalThreadPool.execute(() -> handleMessage(message));
            }
        }
    }
}
