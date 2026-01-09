package com.caring.sass.cms.service.impl;


import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.ReplyLikeLogMapper;
import com.caring.sass.cms.entity.ReplyLikeLog;
import com.caring.sass.cms.service.ReplyLikeLogService;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 点赞记录
 * </p>
 *
 * @author leizhi
 * @date 2021-03-03
 */
@Slf4j
@Service
public class ReplyLikeLogServiceImpl extends SuperServiceImpl<ReplyLikeLogMapper, ReplyLikeLog> implements ReplyLikeLogService {

    @Override
    public boolean hasLike(Long replyId, Long userId, String roleType) {
        Integer count = baseMapper.selectCount(Wraps.<ReplyLikeLog>lbQ()
                .eq(ReplyLikeLog::getUserId, userId)
                .eq(ReplyLikeLog::getHasCancel, 1)
                .eq(ReplyLikeLog::getReplyId, replyId)
                .eq(ReplyLikeLog::getRoleType, roleType));
        // 不为0，则已点赞
        return count != 0;
    }
}
