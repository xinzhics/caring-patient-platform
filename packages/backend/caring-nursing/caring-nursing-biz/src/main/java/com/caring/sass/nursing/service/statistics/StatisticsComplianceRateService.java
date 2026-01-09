package com.caring.sass.nursing.service.statistics;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.statistics.StatisticsComplianceRate;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;

import java.io.Serializable;

/**
 * 达标率 Mapper
 *
 */
public interface StatisticsComplianceRateService extends SuperService<StatisticsComplianceRate> {

    /**
     * 创建 或者 删除达标率 图
     * @param complianceRateChart
     * @param statisticsTask
     */
    void createOrDeleteComplianceRate(boolean complianceRateChart,  StatisticsTask statisticsTask);

    /**
     * 根据统计任务ID移除
     */
    void removeByTaskId(Serializable taskId, String tenant);
}
