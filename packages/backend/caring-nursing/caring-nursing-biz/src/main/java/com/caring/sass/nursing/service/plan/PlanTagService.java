package com.caring.sass.nursing.service.plan;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.plan.PlanTag;

/**
 * <p>
 * 业务接口
 * 
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
public interface PlanTagService extends SuperService<PlanTag> {

    PlanTag getByPlanId(Long aLong);

    boolean deleteByPlanId(Long planId);
}
