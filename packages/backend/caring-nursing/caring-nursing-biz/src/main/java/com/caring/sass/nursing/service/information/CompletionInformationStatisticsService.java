package com.caring.sass.nursing.service.information;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.information.CompletionInformationStatistics;

import java.time.LocalDate;

/**
 * <p>
 * 业务接口
 * 信息完整度统计
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
public interface CompletionInformationStatisticsService extends SuperService<CompletionInformationStatistics> {

    /**
     * 查询今天患者信息完整度
     *
     * @param localDate  通过时间查询
     * @param nursingId 医助id
     * @return
     */
    CompletionInformationStatistics get(LocalDate localDate, Long nursingId);

//    /**
//     * 此接口提供给定时任务
//     * @param nursingId
//     * @param informationList
//     * @param map
//     * @return
//     */
//    CompletionInformationStatistics build(Long nursingId,
//                                          List<CompletenessInformation> informationList, Map<Long, Patient> map);

    /**
     * 定时任务方法
     */
    void statisticsCompletenessInformationTask(LocalDate localDate);
}
