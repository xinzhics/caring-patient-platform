package com.caring.sass.tenant.dao;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.TenantConfigurationSchedule;

import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 项目配置进度表
 * </p>
 *
 * @author 杨帅
 * @date 2023-07-18
 */
@Repository
public interface TenantConfigurationScheduleMapper extends SuperMapper<TenantConfigurationSchedule> {

    void copy(String fromTenantCode, String templateIdMaps);
}
