package com.caring.sass.nursing.service.follow.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.follow.FollowTaskContentMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.entity.follow.FollowContentShow;
import com.caring.sass.nursing.entity.follow.FollowContentTimeFrame;
import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.follow.FollowTaskContentService;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 随访任务内容列表
 * </p>
 *
 * @author 杨帅
 * @date 2023-01-04
 */
@Slf4j
@Service

public class FollowTaskContentServiceImpl extends SuperServiceImpl<FollowTaskContentMapper, FollowTaskContent> implements FollowTaskContentService {

    @Autowired
    PlanMapper planMapper;

    @Override
    public void initAllTenantPlan() {
        List<Plan> plans = planMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getSystemTemplate, 0));
        List<FollowTaskContent> followTaskContents = new ArrayList<>();
        int contentSort = 0;
        if (CollUtil.isEmpty(plans)) {
            return;
        }
        int learnPlanStatus = 0;
        for (Plan plan : plans) {
            if (!PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType()) && !PlanEnum.MEDICATION_REMIND.getCode().equals(plan.getPlanType())) {
                Integer effectiveTime = plan.getEffectiveTime();
                followTaskContents.add(FollowTaskContent.builder()
                        .contentSort(++contentSort)
                        .planId(plan.getId())
                        .planName(plan.getName())
                        .planType(plan.getPlanType())
                        .timeFrame(effectiveTime != null && effectiveTime > 0 ? FollowContentTimeFrame.NOT_NEED_SET : FollowContentTimeFrame.NOT_SET)
                        .showName(plan.getName())
                        .showContent(FollowContentShow.FOLLOW_CONTENT_SHOW)
                        .planStatus(plan.getStatus())
                        .build());
            } else if (PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
                if (plan.getStatus() == 1) {
                    learnPlanStatus = plan.getStatus();
                }
            }
        }
        followTaskContents.add(FollowTaskContent.builder()
                .contentSort(++contentSort)
                .planName(PlanEnum.LEARN_PLAN.getDesc())
                .planType(PlanEnum.LEARN_PLAN.getCode())
                .timeFrame(FollowContentTimeFrame.NOT_SET)
                .showName(PlanEnum.LEARN_PLAN.getDesc())
                .showContent(FollowContentShow.FOLLOW_CONTENT_SHOW)
                .planStatus(learnPlanStatus)
                .build());
        if (CollUtil.isNotEmpty(followTaskContents)) {
            baseMapper.insertBatchSomeColumn(followTaskContents);
        }

    }


    /**
     * 创建护理计划，监测计划时，创建随访任务内容
     *
     * @param plan
     */
    @Override
    public void createOneByPlan(Plan plan) {
        if (plan.getPlanType() != null && PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
            // 如果是又添加的学习计划，则不创建随访内容
            return;
        }
        FollowTaskContent followTaskContent = baseMapper.selectOne(Wraps.<FollowTaskContent>lbQ()
                .orderByDesc(FollowTaskContent::getContentSort)
                .select(FollowTaskContent::getContentSort, SuperEntity::getId)
                .last(" limit 0,1 "));
        Integer contentSort;
        if (Objects.nonNull(followTaskContent)) {
            contentSort = followTaskContent.getContentSort();
        } else {
            contentSort = 0;
        }
        Integer effectiveTime = plan.getEffectiveTime();
        FollowTaskContent taskContent = FollowTaskContent.builder()
                .contentSort(++contentSort)
                .planId(plan.getId())
                .planName(plan.getName())
                .planType(plan.getPlanType())
                .timeFrame(FollowContentTimeFrame.NOT_SET)
                .showName(plan.getName())
                .showContent(FollowContentShow.FOLLOW_CONTENT_SHOW)
                .planStatus(plan.getStatus())
                .build();
        if (effectiveTime != null && effectiveTime > 0) {
            taskContent.setTimeFrame(FollowContentTimeFrame.NOT_NEED_SET);
        }

        baseMapper.insert(taskContent);
    }

    /**
     * 护理计划的状态被修改时， 随访中的任务内容也会被修改。
     * @param status
     */
    @Override
    public void updatePlanStatusByPlan(Long planId, Integer status) {
        // 计划被开启了。
        FollowTaskContent taskContent = baseMapper.selectOne(Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanId, planId));
        if (Objects.isNull(taskContent)) {
            return;
        }
        if (status == 1) {
            // 设置随访的任务 的排序为最大排序、
            FollowTaskContent followTaskContent = baseMapper.selectOne(Wraps.<FollowTaskContent>lbQ()
                    .orderByDesc(FollowTaskContent::getContentSort)
                    .select(FollowTaskContent::getContentSort, SuperEntity::getId)
                    .last(" limit 0,1 "));
            Integer contentSort = followTaskContent.getContentSort();
            taskContent.setContentSort(++contentSort);
            taskContent.setPlanStatus(1);
            baseMapper.updateById(taskContent);
        } else {
            taskContent.setPlanStatus(0);
            baseMapper.updateById(taskContent);
        }
    }

    /**
     * 更新学习计划随访任务的 计划状态
     * @param status 0， 1
     */
    @Override
    public void updateLearnPlanStatus(Integer status) {
        // 计划被开启了。
        FollowTaskContent taskContent = baseMapper.selectOne(Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanType, PlanEnum.LEARN_PLAN.getCode()).last(" limit 0, 1 "));
        if (Objects.isNull(taskContent)) {
            return;
        }
        if (status == 1) {
            // 设置随访的任务 的排序为最大排序、
            FollowTaskContent followTaskContent = baseMapper.selectOne(Wraps.<FollowTaskContent>lbQ()
                    .orderByDesc(FollowTaskContent::getContentSort)
                    .select(FollowTaskContent::getContentSort, SuperEntity::getId)
                    .last(" limit 0,1 "));
            Integer contentSort = followTaskContent.getContentSort();
            taskContent.setContentSort(++contentSort);
            taskContent.setPlanStatus(1);
            baseMapper.updateById(taskContent);
        } else {
            taskContent.setPlanStatus(0);
            baseMapper.updateById(taskContent);
        }
    }

    @Autowired
    FunctionConfigurationService functionConfigurationService;

    /**
     * 护理计划修改之后，修改这个随访
     * @param plan
     */
    @Override
    public void updateByPlan(Plan plan) {
        // 如果计划关闭，
        Integer planType = plan.getPlanType();
        FollowTaskContent followTaskContent;
        if (planType != null && planType.equals(PlanEnum.LEARN_PLAN.getCode())) {
            // 学习计划
            followTaskContent = baseMapper.selectOne(Wraps.<FollowTaskContent>lbQ()
                    .eq(FollowTaskContent::getPlanType, PlanEnum.LEARN_PLAN.getCode())
                    .last(" limit 0, 1 "));
        } else {
            followTaskContent = baseMapper.selectOne(Wraps.<FollowTaskContent>lbQ()
                    .eq(FollowTaskContent::getPlanId, plan.getId())
                    .last(" limit 0, 1 "));
        }
        if (Objects.nonNull(followTaskContent)) {
            if (!PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
                followTaskContent.setPlanName(plan.getName());
            }
            // 计划被关闭了。 而且还是学习计划被关闭了。
            if (planType != null && planType.equals(PlanEnum.LEARN_PLAN.getCode())) {
                // 检查是不是所有的 患者学习计划都关闭。
                Integer functionStatus = functionConfigurationService.getFunctionStatus(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.LEARNING_PLAN);
                if (functionStatus == 1) {
                    Integer integer = planMapper.selectCount(Wraps.<Plan>lbQ()
                            .eq(Plan::getLearnPlanRole, UserType.UCENTER_PATIENT)
                            .eq(Plan::getPlanType, PlanEnum.LEARN_PLAN.getCode()).eq(Plan::getStatus, 1));
                    // 还有学习计划没有关闭。
                    if (integer != null && integer > 0) {
                        followTaskContent.setPlanStatus(1);
                    } else {
                        followTaskContent.setPlanStatus(0);
                    }
                } else {
                    followTaskContent.setPlanStatus(0);
                }
            } else {
                followTaskContent.setPlanStatus(plan.getStatus());
            }

            // 随访计划的是有期限的
            Integer effectiveTime = plan.getEffectiveTime();
            if (planType != null && planType.equals(PlanEnum.LEARN_PLAN.getCode())) {
                followTaskContent.setTimeFrame(FollowContentTimeFrame.NOT_NEED_SET);
            } else if (effectiveTime != null && effectiveTime > 0) {
                if (!FollowContentTimeFrame.NOT_NEED_SET.equals(followTaskContent.getTimeFrame())) {
                    followTaskContent.setTimeFrame(FollowContentTimeFrame.NOT_NEED_SET);
                }
            } else {
                if (FollowContentTimeFrame.NOT_NEED_SET.equals(followTaskContent.getTimeFrame())) {
                    followTaskContent.setTimeFrame(FollowContentTimeFrame.NOT_SET);
                }
            }
            baseMapper.updateById(followTaskContent);
        }
    }
}
