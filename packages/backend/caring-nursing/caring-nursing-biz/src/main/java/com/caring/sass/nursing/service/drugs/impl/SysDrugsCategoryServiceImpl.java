package com.caring.sass.nursing.service.drugs.impl;


import cn.hutool.core.convert.Convert;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.dao.drugs.SysDrugsCategoryMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.service.drugs.SysDrugsCategoryService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 药品类别
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class SysDrugsCategoryServiceImpl extends SuperServiceImpl<SysDrugsCategoryMapper, SysDrugsCategory> implements SysDrugsCategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recursively(List<Long> ids) {
        boolean removeFlag = removeByIds(ids);
        delete(ids);
        return removeFlag;
    }

    private void delete(List<Long> ids) {
        // 查询子节点
        List<Long> childIds = super.listObjs(Wraps.<SysDrugsCategory>lbQ().select(SysDrugsCategory::getId).in(SysDrugsCategory::getParentId, ids), Convert::toLong);
        if (!childIds.isEmpty()) {
            removeByIds(childIds);
            delete(childIds);
        }
        log.debug("退出药品分类数据递归");
    }

    @Override
    public List<Long> childIds(Long parentId) {
        // 查询子类，这里只有两级，偷个懒
        return super.listObjs(Wraps.<SysDrugsCategory>lbQ().select(SysDrugsCategory::getId).eq(SysDrugsCategory::getParentId, parentId), Convert::toLong);
    }

    @Override
    public boolean save(SysDrugsCategory model) {

        LbqWrapper<SysDrugsCategory> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(SysDrugsCategory::getLabel, model.getLabel());
        Integer integer = baseMapper.selectCount(lbqWrapper);
        if (integer > 0) {
            throw new BizException("药品类别名称已经被占用");
        }
        return super.save(model);
    }

    @Override
    public boolean updateById(SysDrugsCategory model) {
        LbqWrapper<SysDrugsCategory> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(SysDrugsCategory::getLabel, model.getLabel());
        List<SysDrugsCategory> categoryList = baseMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(categoryList)) {
            return super.updateById(model);
        } else {
            for (SysDrugsCategory drugsCategory : categoryList) {
                if (model.getId().equals(drugsCategory.getId())) {
                    return super.updateById(model);
                } else {
                    throw new BizException("药品类别名称已经被占用");
                }
            }
            return false;
        }

    }



}
