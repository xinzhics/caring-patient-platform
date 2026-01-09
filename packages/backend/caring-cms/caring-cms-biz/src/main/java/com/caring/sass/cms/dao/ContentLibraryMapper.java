package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.ContentLibrary;
import org.springframework.stereotype.Repository;

/**
 * 内容库
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ContentLibraryMapper extends SuperMapper<ContentLibrary> {

}
