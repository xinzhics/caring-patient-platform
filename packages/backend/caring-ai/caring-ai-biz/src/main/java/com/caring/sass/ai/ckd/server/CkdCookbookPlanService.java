package com.caring.sass.ai.ckd.server;

import com.caring.sass.ai.entity.CookPlanMealType;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.ckd.CkdCookbookPlan;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 用户食谱计划
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
public interface CkdCookbookPlanService extends SuperService<CkdCookbookPlan> {

    List<CkdCookbookPlan> findCookBookPlansInLastThreeDays(String openId);


    CkdCookbookPlan changeCookBookPlans(String openId, Long planId, CookPlanMealType type);
}
