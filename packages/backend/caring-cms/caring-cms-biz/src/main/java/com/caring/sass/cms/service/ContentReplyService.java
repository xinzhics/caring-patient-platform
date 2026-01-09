package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.ContentReply;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
public interface ContentReplyService extends SuperService<ContentReply> {

    /**
     * 查看留言列表
     *
     * @param contentId 内容id
     * @param userId    当前用户id
     * @return 留言列表
     */
    List<ContentReply> listReplyWithLike(Long contentId, Long userId, String roleType);

    /**
     * 查看留言列表
     *
     * @param contentId 内容id
     * @return 留言列表
     */
    List<ContentReply> listReplyWithLike(Long contentId);
}
