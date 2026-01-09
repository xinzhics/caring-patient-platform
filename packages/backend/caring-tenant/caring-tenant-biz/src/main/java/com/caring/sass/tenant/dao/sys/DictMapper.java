package com.caring.sass.tenant.dao.sys;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.sys.Dict;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 系统字典类型
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DictMapper extends SuperMapper<Dict> {

}
