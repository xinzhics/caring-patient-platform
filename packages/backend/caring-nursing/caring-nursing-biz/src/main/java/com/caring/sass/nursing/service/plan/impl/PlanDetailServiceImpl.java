package com.caring.sass.nursing.service.plan.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.plan.PlanDetailMapper;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.entity.plan.PlanDetail;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;
import com.caring.sass.nursing.service.plan.PlanDetailService;
import com.caring.sass.nursing.service.plan.PlanDetailTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
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

public class PlanDetailServiceImpl extends SuperServiceImpl<PlanDetailMapper, PlanDetail> implements PlanDetailService {

    @Autowired
    PlanDetailTimeService planDetailTimeService;

    /**
     * @Author yangShuai
     * @Description 删除护理计划详情
     * @Date 2020/9/21 13:40
     *
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByPlanId(Long plantId) {
        LbqWrapper<PlanDetail> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(PlanDetail::getNursingPlanId, plantId);
        List<PlanDetail> planDetails = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(planDetails)) {
            List<Long> collect = planDetails.stream().map(PlanDetail::getId).collect(Collectors.toList());
            planDetailTimeService.deleteByDetailIds(collect);
            baseMapper.deleteBatchIds(collect);
        }
        return true;
    }

    @Override
    public boolean save(PlanDetail model) {
        if (model.getType() != null && model.getType() == 1) {
            if (model.getPushType() == null) {
                model.setPushType(0);
            }
        }
        return super.save(model);
    }

    /**
     * @Author yangShuai
     * @Description 删除护理计划详情时， 删除详情下的时间设置
     * @Date 2020/9/21 15:30
     *
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (!CollectionUtils.isEmpty(idList)) {
            baseMapper.deleteBatchIds(idList);
            planDetailTimeService.remove(Wraps.<PlanDetailTime>lbQ().in(PlanDetailTime::getNursingPlanDetailId, idList));
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        baseMapper.deleteById(id);
        // 删除护理计划详情时间
        planDetailTimeService.remove(Wraps.<PlanDetailTime>lbQ().eq(PlanDetailTime::getNursingPlanDetailId, id));
        return true;
    }

    @Override
    public List<PlanDetailDTO> findDetailWithTimeById(Long plantId) {
        List<PlanDetailDTO> planDetailDTOS = new ArrayList<>();
        LbqWrapper<PlanDetail> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(PlanDetail::getNursingPlanId, plantId);
        List<PlanDetail> planDetails = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(planDetails)) {
            for (PlanDetail planDetail : planDetails) {
                PlanDetailDTO planDetailDTO = new PlanDetailDTO();
                if (planDetail.getType() != null && planDetail.getType() == 1 && planDetail.getPushType() == null) {
                    planDetail.setPushType(0);
                }
                BeanUtils.copyProperties(planDetail, planDetailDTO);
                planDetailDTO.setId(planDetail.getId());
                List<PlanDetailTime> planDetailTimes = planDetailTimeService.findByDetailId(planDetail.getId());
                planDetailDTO.setPlanDetailTimes(planDetailTimes);
                planDetailDTOS.add(planDetailDTO);
            }
        }
        return planDetailDTOS;
    }


    @Override
    public List<PlanDetailDTO> findDetailWithTimeByIdOrderByTime(Long plantId) {
        List<PlanDetailDTO> planDetailDTOS = new ArrayList<>();
        LbqWrapper<PlanDetail> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(PlanDetail::getNursingPlanId, plantId);
        List<PlanDetail> planDetails = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(planDetails)) {
            for (PlanDetail planDetail : planDetails) {
                PlanDetailDTO planDetailDTO = new PlanDetailDTO();
                if (planDetail.getType() != null && planDetail.getType() == 1 && planDetail.getPushType() == null) {
                    planDetail.setPushType(0);
                }
                BeanUtils.copyProperties(planDetail, planDetailDTO);
                planDetailDTO.setId(planDetail.getId());
                List<PlanDetailTime> planDetailTimes = planDetailTimeService.findByDetailIdOrderByTime(planDetail.getId());
                planDetailDTO.setPlanDetailTimes(planDetailTimes);
                planDetailDTOS.add(planDetailDTO);
            }
        }
        return planDetailDTOS;
    }

    /**
     * 查询文件夹的分享链接被用在那个项目
     * @param url
     * @return
     */
    @Override
    public List<String> checkFolderShareUrlExist(String url) {
        List<String> arrayList = new ArrayList<>();
        List<PlanDetail> planDetails = baseMapper.selectMenuLikeUrl("%" + url + "%");
        if (CollUtil.isNotEmpty(planDetails)) {
            arrayList.addAll(planDetails.stream().map(PlanDetail::getTenantCode).collect(Collectors.toList()));
        }

        List<PlanDetailTime> planDetailTimes = planDetailTimeService.checkFolderShareUrlExist(url);
        if (CollUtil.isNotEmpty(planDetailTimes)) {
            arrayList.addAll(planDetailTimes.stream().map(PlanDetailTime::getTenantCode).collect(Collectors.toList()));
        }
        return arrayList;
    }
}
