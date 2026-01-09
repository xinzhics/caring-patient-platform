package com.caring.sass.authority.dao.common;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.authority.entity.common.Province;
import com.caring.sass.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 字典类型
 * </p>
 *
 * @author caring
 * @date 2019-07-02
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ProvinceMapper extends SuperMapper<Province> {

}
