package com.caring.sass.nursing.service.statistics;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.statistics.StatisticsData;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;

/**
 * 统计的业务数据 Mapper
 *
 */
public interface StatisticsDataService extends SuperService<StatisticsData> {

    /**
     * 解析任务。重置 任务相关的 数据。
     * @param statisticsTask
     * @param tenant
     */
    void parseTaskResetData(StatisticsTask statisticsTask, String tenant);

    /**
     * 用户提交或者修改这个表单时。
     * @param formResult
     * @param tenantCode
     */
    void formResultChange(FormResult formResult, String tenantCode);
}
