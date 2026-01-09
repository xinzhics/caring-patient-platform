package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.user.entity.StatisticsDoctor;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 医生统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface StatisticsDoctorMapper extends SuperMapper<StatisticsDoctor> {

    /**
     * 通过数据库聚合函数查询统计数据
     *
     * @return 统计列表
     */
    List<StatisticsDoctor> queryDb();

}
