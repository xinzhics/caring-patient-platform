package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.ContentCollect;

/**
 * <p>
 * 业务接口
 * 收藏
 * </p>
 */
public interface ContentCollectService extends SuperService<ContentCollect> {


    void cancelContent(Long contentId, Long userId);

    boolean hasCollect(Long id, Long userId, String roleType);
}
