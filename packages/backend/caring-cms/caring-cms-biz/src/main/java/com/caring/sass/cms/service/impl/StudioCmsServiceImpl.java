package com.caring.sass.cms.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.cms.constant.StudioCmsType;
import com.caring.sass.cms.dao.StudioCmsMapper;
import com.caring.sass.cms.dao.StudioCollectMapper;
import com.caring.sass.cms.dao.StudioContentReplyMapper;
import com.caring.sass.cms.entity.StudioCms;
import com.caring.sass.cms.entity.StudioCollect;
import com.caring.sass.cms.entity.StudioContentReply;
import com.caring.sass.cms.service.StudioCmsService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 医生cms详情表
 * </p>
 *
 * @author 杨帅
 * @date 2025-11-11
 */
@Slf4j
@Service

public class StudioCmsServiceImpl extends SuperServiceImpl<StudioCmsMapper, StudioCms> implements StudioCmsService {


    @Autowired
    StudioCollectMapper studioCollectMapper;

    @Autowired
    StudioContentReplyMapper studioContentReplyMapper;



    @Override
    public void pinToTop(Long cmsId, Long doctorId) {
        // 取消其他文章的置顶状态
        baseMapper.update(null, Wrappers.lambdaUpdate(StudioCms.class)
                .set(StudioCms::getPinToTop, 0)
                .eq(StudioCms::getDoctorId, doctorId));
        StudioCms cms = new StudioCms();
        cms.setId(cmsId);
        cms.setPinToTop(1);
        cms.setPinToTopSort(System.currentTimeMillis() + "");
        baseMapper.updateById(cms);
    }

    @Override
    public void release(Long cmsId) {
        LambdaQueryWrapper<StudioCms> lqw = Wrappers.lambdaQuery();
        lqw.eq(StudioCms::getId, cmsId);
        lqw.eq(StudioCms::getReleaseStatus, 1);
        Integer count = baseMapper.selectCount(lqw);

        StudioCms cms = new StudioCms();
        cms.setId(cmsId);
        if (count > 0) {
            cms.setReleaseStatus(0);
        } else {
            cms.setReleaseStatus(1);
        }
        baseMapper.updateById(cms);
    }


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        studioCollectMapper.delete(Wrappers.<StudioCollect>lambdaQuery()
                .eq(StudioCollect::getCmsId, id));
        studioContentReplyMapper.delete(Wrappers.<StudioContentReply>lambdaQuery()
                .eq(StudioContentReply::getCCmsId, id));
        return super.removeById(id);
    }

    @Override
    public void deleteArticleData(Long articleDataId, StudioCmsType studioCmsType) {

        List<StudioCms> studioCms = baseMapper.selectList(Wrappers.<StudioCms>lambdaQuery()
                .eq(StudioCms::getCmsType, studioCmsType)
                .eq(StudioCms::getArticleDataId, articleDataId));
        for (StudioCms studioCm : studioCms) {
            studioCollectMapper.delete(Wrappers.<StudioCollect>lambdaQuery()
                    .eq(StudioCollect::getCmsId, studioCm.getId()));
            studioContentReplyMapper.delete(Wrappers.<StudioContentReply>lambdaQuery()
                    .eq(StudioContentReply::getCCmsId, studioCm.getId()));
            baseMapper.deleteById(studioCm.getId());
        }
    }
}
