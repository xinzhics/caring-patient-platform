package com.caring.sass.nursing.service.statistics;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.statistics.StatisticsBaselineValue;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;

import java.io.Serializable;

/**
 * 基准值 Mapper
 *
 */
public interface StatisticsBaselineValueService extends SuperService<StatisticsBaselineValue> {



    /**
     * 创建或者删除基准值统计图
     * @param hasBaseLineValue
     * @param statisticsTask
     */
    void createOrDeleteBaseLineValue(boolean hasBaseLineValue,  StatisticsTask statisticsTask);

    /**
     * 根据统计任务ID移除
     */
    void removeByTaskId(Serializable taskId, String tenant);
}
