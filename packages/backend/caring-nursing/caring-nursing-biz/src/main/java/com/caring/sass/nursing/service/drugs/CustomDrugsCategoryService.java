package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.drugs.CustomDrugsCategory;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 项目药品类别
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface CustomDrugsCategoryService extends SuperService<CustomDrugsCategory> {

    /**
     * 查询自定义药品分类的类别id
     *
     * @return 类别id
     */
    List<Long> selectCategoryIds();

    /**
     * 复制项目药品分类
     *
     * @param fromTenantCode 待复制的项目编码
     * @param toTenantCode   目标项目编码
     */
    Boolean copyDrugsCategory(String fromTenantCode, String toTenantCode);
}
