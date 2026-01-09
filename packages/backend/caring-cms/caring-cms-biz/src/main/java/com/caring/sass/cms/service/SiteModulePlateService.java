package com.caring.sass.cms.service;

import cn.hutool.core.lang.Snowflake;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.SiteModulePlate;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 建站组件的板块表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
public interface SiteModulePlateService extends SuperService<SiteModulePlate> {

    void saveList(Long folderId, Long pageId, Long moduleId, List<SiteModulePlate> labelPlates, Snowflake snowflake);

    void setPlateJump(List<SiteModulePlate> siteModulePlates);

    void setPlatesAllInfo(List<SiteModulePlate> siteModulePlates);

    /**
     * 当文章更新时，更新已经被使用的文章冗余
     * @param model
     * @param tenant
     */
    void updateCmsInfo(ChannelContent model, String tenant);
}
