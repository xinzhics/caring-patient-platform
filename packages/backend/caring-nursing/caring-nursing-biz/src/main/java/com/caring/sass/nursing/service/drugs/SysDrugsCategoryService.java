package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 药品类别
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface SysDrugsCategoryService extends SuperService<SysDrugsCategory> {

    /**
     * 递归删除
     *
     * @param ids
     * @return
     */
    boolean recursively(List<Long> ids);


    /**
     * 查询子类id
     *
     * @param parentId 父类id
     */
    List<Long> childIds(Long parentId);


}
