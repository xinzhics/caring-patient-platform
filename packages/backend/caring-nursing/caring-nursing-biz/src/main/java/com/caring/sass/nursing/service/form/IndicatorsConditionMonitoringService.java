package com.caring.sass.nursing.service.form;


import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.IndicatorsConditionMonitoring;
import com.caring.sass.nursing.vo.IndicatorsConditionMonitoringVO;
import com.caring.sass.nursing.vo.IndicatorsPlanVo;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者管理-监测数据-监控指标条件表
 * </p>
 *
 * @author lixiang
 * @date 2022-06-14
 */
public interface IndicatorsConditionMonitoringService extends SuperService<IndicatorsConditionMonitoring> {

    /**
     * 查询患者管理-监测数据-监测计划
     * @param tenantCode
     * @return
     */
    List<IndicatorsPlanVo> getIndicatorsPlan(String tenantCode);

    /**
     * 查询患者管理-监测数据-监测计划-监测指标及条件
     * @param tenantCode
     * @return
     */
    List<IndicatorsConditionMonitoringVO> getIndicatorsConditionMonitoring(String tenantCode, Long planId);

    /**
     * 监测数据-监测计划-监测指标 -立即生效
     * @param tenantCode
     * @return
     */
    Boolean synIndicatorsConditionMonitoringTask(String tenantCode);

    /**
     * 监测数据-监测计划-监测指标 -监控表单触发
     * @param model
     * @param tenantCode
     * @return
     */
    Boolean synIndicatorsConditionMonitoringTaskByFormChange(FormResult model,String tenantCode);

}
