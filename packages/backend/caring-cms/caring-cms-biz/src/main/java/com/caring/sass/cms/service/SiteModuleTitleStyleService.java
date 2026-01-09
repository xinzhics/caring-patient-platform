package com.caring.sass.cms.service;

import cn.hutool.core.lang.Snowflake;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.SiteModuleTitleStyle;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 建站组件主题样式表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
public interface SiteModuleTitleStyleService extends SuperService<SiteModuleTitleStyle> {

    void saveOrUpdate(Long folderId, Long pageId, Long moduleId, List<SiteModuleTitleStyle> moduleTitleStyles, Snowflake snowflake);


}
