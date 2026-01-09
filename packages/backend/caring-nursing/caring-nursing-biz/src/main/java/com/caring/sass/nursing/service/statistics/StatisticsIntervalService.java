package com.caring.sass.nursing.service.statistics;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.statistics.StatisticsIntervalSaveDTO;
import com.caring.sass.nursing.entity.statistics.StatisticsInterval;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;

import java.util.List;

/**
 * 区间值 Mapper
 *
 */
public interface StatisticsIntervalService extends SuperService<StatisticsInterval> {

    /**
     * 创建统计任务的区间设置
     * @param saveDTOList
     * @param statisticsTask
     */
    void createStatisticsInterval(List<StatisticsIntervalSaveDTO> saveDTOList, StatisticsTask statisticsTask);
}
