package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.entity.StatisticsNursingStaff;

import java.util.Map;

/**
 * <p>
 * 业务接口
 * 护理医助统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
public interface StatisticsNursingStaffService extends SuperService<StatisticsNursingStaff> {

    /**
     * 通过数据库聚合函数查询统计数据
     */
    void syncFromDb();

    Map<String, Long> queryMemberNum();
}
