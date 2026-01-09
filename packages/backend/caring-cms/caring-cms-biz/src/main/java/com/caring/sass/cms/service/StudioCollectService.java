package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.StudioCollect;

/**
 * <p>
 * 业务接口
 * 医生CMS收藏记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-11-11
 */
public interface StudioCollectService extends SuperService<StudioCollect> {

    boolean hasCollect(Long id, Long userId);
}
