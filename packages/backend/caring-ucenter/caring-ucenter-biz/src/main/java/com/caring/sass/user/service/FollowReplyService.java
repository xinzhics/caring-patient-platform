package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.entity.FollowReply;
import com.caring.sass.user.redis.FollowReplyRedisDTO;

import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 关注后回复和关注未注册回复
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
public interface FollowReplyService extends SuperService<FollowReply> {

    FollowReplyRedisDTO getFollowReplyRedis(String replyCategory);

    List<FollowReply> getFollowReply(KeyWordEnum unregistered_reply, String localTimeStr, KeyWordEnum open);


}
