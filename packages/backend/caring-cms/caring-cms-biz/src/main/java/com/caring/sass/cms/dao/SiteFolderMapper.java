package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.SiteFolder;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 建站文件夹表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Repository
public interface SiteFolderMapper extends SuperMapper<SiteFolder> {


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<SiteFolder> queryDeleteFolder();



}
