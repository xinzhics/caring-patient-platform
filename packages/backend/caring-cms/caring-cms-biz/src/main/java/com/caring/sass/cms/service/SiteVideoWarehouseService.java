package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.dto.site.SiteVideoWarehouseUpdateDTO;
import com.caring.sass.cms.entity.SiteVideoWarehouse;

/**
 * <p>
 * 业务接口
 * 建站视频库
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
public interface SiteVideoWarehouseService extends SuperService<SiteVideoWarehouse> {

    /**
     * 同步使用了视频视频的板块
     * @param warehouse
     */
    void synchronizeInformation(SiteVideoWarehouseUpdateDTO warehouse);



    boolean checkVideoUse(Long videoId);
}
