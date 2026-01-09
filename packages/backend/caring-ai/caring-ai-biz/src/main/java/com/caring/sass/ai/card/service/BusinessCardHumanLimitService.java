package com.caring.sass.ai.card.service;

import com.caring.sass.ai.entity.card.BusinessCardHumanLimit;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 用户数字人额度
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-14
 */
public interface BusinessCardHumanLimitService extends SuperService<BusinessCardHumanLimit> {



    boolean deductLimit(Long userId);
}
