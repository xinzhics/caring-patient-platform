package com.caring.sass.nursing.service.plan.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.plan.PlanTagMapper;
import com.caring.sass.nursing.entity.plan.PlanTag;
import com.caring.sass.nursing.service.plan.PlanTagService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service

public class PlanTagServiceImpl extends SuperServiceImpl<PlanTagMapper, PlanTag> implements PlanTagService {


    @Override
    public PlanTag getByPlanId(Long planId) {

        LbqWrapper<PlanTag> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(PlanTag::getNursingPlanId, planId);
        return baseMapper.selectOne(queryWrapper);

    }


    @Override
    public boolean deleteByPlanId(Long planId) {
        LbqWrapper<PlanTag> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(PlanTag::getNursingPlanId, planId);
        baseMapper.delete(queryWrapper);
        return true;
    }
}
