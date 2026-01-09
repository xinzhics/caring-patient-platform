package com.caring.sass.cms.dao;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.SiteVideoWarehouse;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 建站视频库
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Repository
public interface SiteVideoWarehouseMapper extends SuperMapper<SiteVideoWarehouse> {


    /**
     * 视频点击播放次数加一
     * @param id
     */
    void updateNumberViews(@Param("id") Long id);

}
