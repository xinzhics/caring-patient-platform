package com.caring.sass.nursing.service.information;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.information.InformationIntegrityMonitoringTaskDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.information.InformationIntegrityMonitoring;
import com.caring.sass.user.entity.Patient;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 信息完整度监控指标
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
public interface InformationIntegrityMonitoringService extends SuperService<InformationIntegrityMonitoring> {

    /**
     * 信息完整度同步任务 （信息完整度监控指标录入结束触发）
     * @param model
     * @return
     */
    Boolean synInformationIntegrityMonitoringTask(InformationIntegrityMonitoringTaskDTO model);

    /**
     * 执行同步任务信息完整度的逻辑(指定患者触发)
     * @param tenantCode  租户编码
     */
    void doSingleSynInformationIntegrityMonitoringTask(String tenantCode, List<Patient> patients);


    void doSingleSynInformationIntegrityMonitoringTask(String tenantCode, Patient patient, Long formId, Long formResultId);

}
