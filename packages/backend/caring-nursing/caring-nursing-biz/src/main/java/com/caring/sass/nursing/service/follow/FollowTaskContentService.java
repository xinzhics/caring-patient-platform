package com.caring.sass.nursing.service.follow;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import com.caring.sass.nursing.entity.plan.Plan;

/**
 * <p>
 * 业务接口
 * 随访任务内容列表
 * </p>
 *
 * @author 杨帅
 * @date 2023-01-04
 */
public interface FollowTaskContentService extends SuperService<FollowTaskContent> {

    void initAllTenantPlan();


    /**
     * 创建护理计划，监测计划时，同步创建一个随访任务
     * @param plan
     */
    void createOneByPlan(Plan plan);

    /**
     * 更新学习计划随访任务的 计划状态
     * @param status 0， 1
     */
    void updateLearnPlanStatus(Integer status);

    void updateByPlan(Plan plan);

    /**
     * 修改计划的状态，
     * @param planId
     * @param status
     */
    void updatePlanStatusByPlan(Long planId, Integer status);
}
