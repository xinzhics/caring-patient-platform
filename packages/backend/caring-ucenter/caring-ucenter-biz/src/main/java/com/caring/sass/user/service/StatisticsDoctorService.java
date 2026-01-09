package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.entity.StatisticsDoctor;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 医生统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
public interface StatisticsDoctorService extends SuperService<StatisticsDoctor> {

    /**
     * 通过数据库聚合函数查询统计数据
     */
    void syncFromDb();
}
