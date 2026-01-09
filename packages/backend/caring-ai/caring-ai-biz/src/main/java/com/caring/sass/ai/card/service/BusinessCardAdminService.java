package com.caring.sass.ai.card.service;

import com.caring.sass.ai.entity.card.BusinessCardAdmin;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 科普名片管理员
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
public interface BusinessCardAdminService extends SuperService<BusinessCardAdmin> {

    void updateCardOrgan( Long id,
                         Long organId);

    void clearUserId(Long id);
}
