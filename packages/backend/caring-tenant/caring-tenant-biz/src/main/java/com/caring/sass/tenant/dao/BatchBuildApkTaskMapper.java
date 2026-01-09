package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.BatchBuildApkTask;
import org.springframework.stereotype.Repository;


@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface BatchBuildApkTaskMapper extends SuperMapper<BatchBuildApkTask> {

}
