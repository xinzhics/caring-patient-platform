package com.caring.sass.nursing.dao.drugs;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 药品类别
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface SysDrugsCategoryMapper extends SuperMapper<SysDrugsCategory> {

}
