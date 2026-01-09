package com.caring.sass.cms.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.caring.sass.cms.dao.SiteJumpInformationMapper;
import com.caring.sass.cms.entity.SiteJumpInformation;
import com.caring.sass.cms.service.SiteJumpInformationService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 建站组件元素跳转信息
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Service

public class SiteJumpInformationServiceImpl extends SuperServiceImpl<SiteJumpInformationMapper, SiteJumpInformation> implements SiteJumpInformationService {


    @Override
    public void saveOrUpdate(Long folderId, Long pageId, Long moduleId, SiteJumpInformation jumpInformation, Snowflake snowflake) {

        if (Objects.isNull(jumpInformation)) {
            return;
        }
        long jumpId = snowflake.nextId();

        jumpInformation.setFolderId(folderId);
        jumpInformation.setPageId(pageId);
        jumpInformation.setModuleId(moduleId);
        jumpInformation.setId(jumpId);
        baseMapper.insert(jumpInformation);
    }

}
