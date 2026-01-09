package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.ReplyLikeLog;

/**
 * <p>
 * 业务接口
 * 点赞记录
 * </p>
 *
 * @author leizhi
 * @date 2021-03-03
 */
public interface ReplyLikeLogService extends SuperService<ReplyLikeLog> {

    /**
     * 是否点赞
     *
     * @param replyId 评论id
     * @param userId  评论者id
     * @return true 已经点赞，false未点赞
     */
    boolean hasLike(Long replyId, Long userId, String roleType);
}
