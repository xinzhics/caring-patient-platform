package com.caring.sass.nursing.service.statistics;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.statistics.StatisticsMaster;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;

import java.io.Serializable;

/**
 * 主统计图 Mapper
 *
 */
public interface StatisticsMasterService extends SuperService<StatisticsMaster> {

    /**
     * 保存 主统计图
     * @param increment
     * @param statisticsTask
     */
    void createOrUpdateStatisticsMaster(Long increment, StatisticsTask statisticsTask);



    /**
     * 初始化一个项目的 统计图表
     */
    void initTenantDefaultChart();

    /**
     * 根据统计任务ID移除
     */
    void removeByTaskId(Serializable taskId, String tenant);

}
