package com.caring.sass.nursing.service.statistics;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.statistics.StatisticsIntervalSaveDTO;
import com.caring.sass.nursing.dto.statistics.StatisticsTaskListDto;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;

import java.util.List;

/**
 * 统计任务 Mapper
 *
 */

public interface StatisticsTaskService extends SuperService<StatisticsTask> {


    /**
     * 保存统计 任务
     * @param statisticsTask
     * @param intervalSaveDTOS
     */
    void save(StatisticsTask statisticsTask, List<StatisticsIntervalSaveDTO> intervalSaveDTOS);


    /**
     * 修改 统计任务设置
     * @param statisticsTask
     * @param intervalSaveDTOS
     */
    void update(StatisticsTask statisticsTask, List<StatisticsIntervalSaveDTO> intervalSaveDTOS);


    /**
     * 超管端 统计任务的列表
     * @return
     */
    List<StatisticsTaskListDto> statisticsTaskPage();

    /**
     * 获取一个统计任务的详细
     * @param taskId
     * @return
     */
    StatisticsTask getStatisticsInfo(String taskId);

    /**
     * 复制项目 的 统计任务
     * @param fromTenantCode
     * @param toTenantCode
     */
    void copyTask(String fromTenantCode, String toTenantCode);

    /**
     * 修改统计图是否显示
     * @param chartId
     * @param chartType
     * @param showOrHide
     */
    void updateChartShowOrHide(Long chartId, String chartType, String showOrHide);

    /**
     * 修改统计图的宽度
     * @param chartId
     * @param chartType
     * @param chartWidth
     */
    void updateChartChartWidth(Long chartId, String chartType, Integer chartWidth);

    /**
     * 修改统计图的排序
     * @param chartId
     * @param chartType
     * @param fromChartOrder
     * @param toChartOrder
     */
    void updateChartChartOrder(Long chartId, String chartType, Integer fromChartOrder, Integer toChartOrder);

}
