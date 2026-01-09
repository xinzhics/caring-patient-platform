package com.caring.sass.nursing.service.information;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.information.InformationMonitoringInterval;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 信息完整度区间设置
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
public interface InformationMonitoringIntervalService extends SuperService<InformationMonitoringInterval> {

    /**
     * 查询项目的区间，并设置医助下患者完整度人数
     * @param nursingId
     * @return
     */
    List<InformationMonitoringInterval> nursingIntervalStatistics(Long nursingId);

}
