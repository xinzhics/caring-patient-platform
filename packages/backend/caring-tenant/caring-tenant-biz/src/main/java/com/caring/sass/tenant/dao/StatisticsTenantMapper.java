package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.StatisticsTenant;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 项目统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface StatisticsTenantMapper extends SuperMapper<StatisticsTenant> {

    /**
     * 通过查询数据库获取项目信息
     */
    List<StatisticsTenant> queryDb();
}
