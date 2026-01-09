package com.caring.sass.cms.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.SiteVideoClickRemarkMapper;
import com.caring.sass.cms.dao.SiteVideoWarehouseMapper;
import com.caring.sass.cms.entity.SiteVideoClickRemark;
import com.caring.sass.cms.entity.SiteVideoWarehouse;
import com.caring.sass.cms.service.SiteVideoClickRemarkService;
import com.caring.sass.cms.service.SiteVideoWarehouseService;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 视频点击播放
 * </p>
 *
 * @date 2023-11-07
 */
@Slf4j
@Service

public class SiteVideoClickRemarkServiceImpl extends SuperServiceImpl<SiteVideoClickRemarkMapper, SiteVideoClickRemark> implements SiteVideoClickRemarkService {


    @Autowired
    SiteVideoWarehouseMapper siteVideoWarehouseMapper;

    @Override
    public boolean save(SiteVideoClickRemark model) {
        String openId = model.getOpenId();
        Long videoId = model.getVideoId();
        Integer count = baseMapper.selectCount(Wraps.<SiteVideoClickRemark>lbQ().eq(SiteVideoClickRemark::getOpenId, openId).eq(SiteVideoClickRemark::getVideoId, videoId));
        if (count != null && count > 0) {
            return true;
        }
        baseMapper.insert(model);
        siteVideoWarehouseMapper.updateNumberViews(model.getVideoId());
        return true;
    }
}
