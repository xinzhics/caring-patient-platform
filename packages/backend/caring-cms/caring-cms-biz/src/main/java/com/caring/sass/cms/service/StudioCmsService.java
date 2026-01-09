package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.constant.StudioCmsType;
import com.caring.sass.cms.entity.StudioCms;

/**
 * <p>
 * 业务接口
 * 医生cms详情表
 * </p>
 *
 * @author 杨帅
 * @date 2025-11-11
 */
public interface StudioCmsService extends SuperService<StudioCms> {

    void pinToTop(Long cmsId, Long doctorId);

    void release(Long cmsId);

    void deleteArticleData(Long articleDataId, StudioCmsType studioCmsType);
}
