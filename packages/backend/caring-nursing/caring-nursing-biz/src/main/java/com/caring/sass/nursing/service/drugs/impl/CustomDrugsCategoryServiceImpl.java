package com.caring.sass.nursing.service.drugs.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.drugs.CustomDrugsCategoryMapper;
import com.caring.sass.nursing.entity.drugs.CustomDrugsCategory;
import com.caring.sass.nursing.service.drugs.CustomDrugsCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 项目药品类别
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class CustomDrugsCategoryServiceImpl extends SuperServiceImpl<CustomDrugsCategoryMapper, CustomDrugsCategory> implements CustomDrugsCategoryService {


    /**
     * 查询自定义药品分类的类别id
     *
     * @return 类别id
     */
    @Override
    public List<Long> selectCategoryIds() {
        List<CustomDrugsCategory> list = baseMapper.selectList(Wraps.<CustomDrugsCategory>lbQ()
                .select(CustomDrugsCategory::getCategoryId));
        return list.stream().map(CustomDrugsCategory::getCategoryId).collect(Collectors.toList());
    }

    @Override
    public Boolean copyDrugsCategory(String fromTenantCode, String toTenantCode) {
        String currentTenant = BaseContextHandler.getTenant();

        BaseContextHandler.setTenant(fromTenantCode);
        List<CustomDrugsCategory> customDrugsCategories = baseMapper.selectList(Wrappers.emptyWrapper());

        // 修改数据
        List<CustomDrugsCategory> toSaveFroms = customDrugsCategories.stream().peek(f -> f.setId(null)).collect(Collectors.toList());
        BaseContextHandler.setTenant(toTenantCode);
        saveBatchSomeColumn(toSaveFroms);
        BaseContextHandler.setTenant(currentTenant);
        return true;
    }
}
