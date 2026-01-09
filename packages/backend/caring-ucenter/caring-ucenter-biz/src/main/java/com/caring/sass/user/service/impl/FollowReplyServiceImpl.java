package com.caring.sass.user.service.impl;


import com.alibaba.fastjson.JSON;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.dao.FollowReplyMapper;
import com.caring.sass.user.entity.FollowReply;
import com.caring.sass.user.redis.FollowReplyRedisDTO;
import com.caring.sass.user.redis.UcenterRedisKeyConstant;
import com.caring.sass.user.service.FollowReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 关注后回复和关注未注册回复
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Service

public class FollowReplyServiceImpl extends SuperServiceImpl<FollowReplyMapper, FollowReply> implements FollowReplyService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean save(FollowReply model) {
        baseMapper.delete(Wraps.<FollowReply>lbQ().eq(FollowReply::getReplyCategory, model.getReplyCategory()));
        super.save(model);
        updateFollowReplyRedis(model);
        return true;
    }


    @Override
    public boolean updateById(FollowReply model) {
        FollowReply followReply = baseMapper.selectById(model.getId());
        BeanUtils.copyProperties(model, followReply);
        super.updateById(followReply);
        updateFollowReplyRedis(model);
        return true;
    }


    /**
     * 缓存回复规则内容
     */
    private void updateFollowReplyRedis(FollowReply model) {

        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordProjectFollowReply);
        String tenant = BaseContextHandler.getTenant();
        FollowReplyRedisDTO replyRedisDTO = new FollowReplyRedisDTO();
        BeanUtils.copyProperties(model, replyRedisDTO);
        hashOperations.put(tenant + model.getReplyCategory(), JSON.toJSONString(replyRedisDTO));

    }

    /**
     * 获取 项目的 配置的记录
     */
    @Override
    public FollowReplyRedisDTO getFollowReplyRedis(String replyCategory) {

        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordProjectFollowReply);
        String tenant = BaseContextHandler.getTenant();
        FollowReplyRedisDTO replyRedisDTO = new FollowReplyRedisDTO();
        Object o = hashOperations.get(tenant + replyCategory);
        if (Objects.isNull(o)) {
            FollowReply followReply = baseMapper.selectOne(Wraps.<FollowReply>lbQ()
                    .orderByDesc(Entity::getUpdateTime)
                    .eq(FollowReply::getReplyCategory, replyCategory)
                    .last(" limit 0, 1 "));
            if (Objects.isNull(followReply)) {
                return null;
            } else {
                updateFollowReplyRedis(followReply);
                BeanUtils.copyProperties(followReply, replyRedisDTO);
            }
        } else {
            replyRedisDTO = JSON.parseObject(o.toString(), FollowReplyRedisDTO.class);
        }
        return replyRedisDTO;

    }


    @Override
    public List<FollowReply> getFollowReply(KeyWordEnum unregistered_reply, String localTime, KeyWordEnum open) {

        return baseMapper.selectAllTenantCode(unregistered_reply.toString(), localTime, open.toString());
    }
}
