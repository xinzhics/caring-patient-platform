package com.caring.sass.cms.service.impl;


import com.caring.sass.cms.dao.ContentReplyMapper;
import com.caring.sass.cms.entity.ContentReply;
import com.caring.sass.cms.service.ContentReplyService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.cms.service.ReplyLikeLogService;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Slf4j
@Service

public class ContentReplyServiceImpl extends SuperServiceImpl<ContentReplyMapper, ContentReply> implements ContentReplyService {

    private final ReplyLikeLogService replyLikeLogService;

    public ContentReplyServiceImpl(ReplyLikeLogService replyLikeLogService) {
        this.replyLikeLogService = replyLikeLogService;
    }

    @Override
    public List<ContentReply> listReplyWithLike(Long contentId, Long userId, String roleType) {
        List<ContentReply> replyList = baseMapper.selectList(Wraps.<ContentReply>lbQ()
                .eq(ContentReply::getContentId, contentId)
                .orderByDesc(ContentReply::getCreateTime));
        // 设置是否已经点赞信息
        if (Objects.nonNull(userId)) {
            for (ContentReply contentReply : replyList) {
                contentReply.setHasLike(replyLikeLogService.hasLike(contentReply.getId(), userId, roleType));
            }
        }
        return replyList;
    }


    @Override
    public List<ContentReply> listReplyWithLike(Long contentId) {
        return baseMapper.selectList(Wraps.<ContentReply>lbQ()
                .eq(ContentReply::getContentId, contentId));
    }

}
