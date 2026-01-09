package com.caring.sass.cms.service;

import cn.hutool.core.lang.Snowflake;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.SiteJumpInformation;

/**
 * <p>
 * 业务接口
 * 建站组件元素跳转信息
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
public interface SiteJumpInformationService extends SuperService<SiteJumpInformation> {


    void saveOrUpdate(Long folderId, Long pageId, Long moduleId, SiteJumpInformation jumpInformation, Snowflake snowflake);

}
