package com.caring.sass.nursing.dao.drugs;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.entity.drugs.SysDrugsImage;
import org.springframework.stereotype.Repository;


@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface SysDrugsImageMapper extends SuperMapper<SysDrugsImage> {

}
