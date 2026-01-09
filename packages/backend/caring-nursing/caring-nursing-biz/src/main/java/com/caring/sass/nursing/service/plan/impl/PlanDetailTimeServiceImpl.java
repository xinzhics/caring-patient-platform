package com.caring.sass.nursing.service.plan.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.plan.PlanDetailTimeMapper;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;
import com.caring.sass.nursing.service.plan.PlanDetailTimeService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 护理计划详情
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service

public class PlanDetailTimeServiceImpl extends SuperServiceImpl<PlanDetailTimeMapper, PlanDetailTime> implements PlanDetailTimeService {



    @Override
    public void deleteByDetailIds(Collection<? extends Serializable> collect) {
        LbqWrapper<PlanDetailTime> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.in(PlanDetailTime::getNursingPlanDetailId, collect);
        int delete = baseMapper.delete(lbqWrapper);
    }

    @Override
    public List<PlanDetailTime> findByDetailId(Long id) {
        LbqWrapper<PlanDetailTime> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(PlanDetailTime::getNursingPlanDetailId, id);
        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public List<PlanDetailTime> findByDetailIdOrderByTime(Long detailId) {
        LbqWrapper<PlanDetailTime> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(PlanDetailTime::getNursingPlanDetailId, detailId);
        queryWrapper.orderByAsc(PlanDetailTime::getPreTime)
                .orderByAsc(PlanDetailTime::getTime);
        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public List<PlanDetailTime> checkFolderShareUrlExist(String url) {
        List<PlanDetailTime> detailTimes = baseMapper.selectMenuLikeUrl("%" + url + "%");
        return detailTimes;
    }
}
