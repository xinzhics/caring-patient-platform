package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.BatchBuildTaskChild;
import org.springframework.stereotype.Repository;


/**
 * 批量任务的子任务dao
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface BatchBuildTaskChildMapper extends SuperMapper<BatchBuildTaskChild> {

}
