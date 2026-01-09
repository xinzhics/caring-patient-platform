package com.caring.sass.nursing.service.drugs.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.drugs.SysDrugsImageMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugsImage;
import com.caring.sass.nursing.service.drugs.SysDrugsImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 药品
 */
@Slf4j
@Service

public class SysDrugsImageServiceImpl extends SuperServiceImpl<SysDrugsImageMapper, SysDrugsImage> implements SysDrugsImageService {


    /**
     * @Author yangShuai
     * @Description 清除药品的图片。并重新保存
     * @Date 2021/3/22 14:52
     *
     * @param model
     * @param drugId
     * @return boolean
     */
    @Override
    public boolean save(List<SysDrugsImage> model, Long drugId) {

        LbqWrapper<SysDrugsImage> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(SysDrugsImage::getDrugsId, drugId);
        baseMapper.delete(queryWrapper);
        if (!CollectionUtils.isEmpty(model)) {
            for (SysDrugsImage image : model) {
                image.setId(null);
                image.setDrugsId(drugId);
            }
            return super.saveBatch(model);
        }
        return true;
    }

    @Override
    public void deleteByDrugsIds(List<Long> longs) {
        if (CollectionUtils.isNotEmpty(longs))
            baseMapper.delete(Wraps.<SysDrugsImage>lbQ().in(SysDrugsImage::getDrugsId, longs));
    }
}
