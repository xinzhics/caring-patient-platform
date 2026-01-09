package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import com.caring.sass.cms.dao.SiteModuleTitleStyleMapper;
import com.caring.sass.cms.entity.SiteModuleTitleStyle;
import com.caring.sass.cms.service.SiteModuleTitleStyleService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 建站组件主题样式表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Service

public class SiteModuleTitleStyleServiceImpl extends SuperServiceImpl<SiteModuleTitleStyleMapper, SiteModuleTitleStyle> implements SiteModuleTitleStyleService {




    @Override
    public void saveOrUpdate(Long folderId, Long pageId, Long moduleId, List<SiteModuleTitleStyle> moduleTitleStyles, Snowflake snowflake) {
        if (CollUtil.isEmpty(moduleTitleStyles)) {
            return;
        }
        for (SiteModuleTitleStyle titleStyle : moduleTitleStyles) {
            long styleId = snowflake.nextId();
            titleStyle.setFolderId(folderId);
            titleStyle.setPageId(pageId);
            titleStyle.setModuleId(moduleId);
            titleStyle.setId(styleId);
        }
        baseMapper.insertBatchSomeColumn(moduleTitleStyles);

    }
}
