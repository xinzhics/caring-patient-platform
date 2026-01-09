package com.caring.sass.cms.service.impl;


import com.caring.sass.cms.dao.StudioCollectMapper;
import com.caring.sass.cms.entity.StudioCollect;
import com.caring.sass.cms.service.StudioCollectService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 医生CMS收藏记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-11-11
 */
@Slf4j
@Service

public class StudioCollectServiceImpl extends SuperServiceImpl<StudioCollectMapper, StudioCollect> implements StudioCollectService {

    @Override
    public boolean save(StudioCollect model) {

        LbqWrapper<StudioCollect> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(StudioCollect::getUserId, model.getUserId());
        lbqWrapper.eq(StudioCollect::getCmsId, model.getCmsId());
        List<StudioCollect> contentCollects = baseMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(contentCollects)) {
            model.setCollectStatus(1);
            return super.save(model);
        } else {
            StudioCollect collect = contentCollects.get(0);
            if (collect.getCollectStatus() == 0) {
                collect.setCollectStatus(1);
            } else {
                collect.setCollectStatus(0);
            }
            baseMapper.updateById(collect);
        }
        return true;
    }


    @Override
    public boolean hasCollect(Long id, Long userId) {

        if (userId == null) {
            return false;
        }
        LbqWrapper<StudioCollect> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(StudioCollect::getUserId, userId);
        lbqWrapper.eq(StudioCollect::getCmsId, id);
        lbqWrapper.eq(StudioCollect::getCollectStatus, 1);
        Integer integer = baseMapper.selectCount(lbqWrapper);
        return integer > 0;
    }


}
