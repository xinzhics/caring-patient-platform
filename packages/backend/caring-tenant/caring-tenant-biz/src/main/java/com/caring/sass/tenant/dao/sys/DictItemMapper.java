package com.caring.sass.tenant.dao.sys;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.sys.DictItem;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 字典项
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DictItemMapper extends SuperMapper<DictItem> {

}
