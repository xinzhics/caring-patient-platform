package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.SiteFolderPageModule;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 建站文件夹中的页面表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Repository
public interface SiteFolderPageModuleMapper extends SuperMapper<SiteFolderPageModule> {


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<SiteFolderPageModule> selectAllNoTenantCode();


}
