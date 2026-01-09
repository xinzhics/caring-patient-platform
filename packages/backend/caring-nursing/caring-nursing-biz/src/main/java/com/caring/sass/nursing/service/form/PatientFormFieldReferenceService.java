package com.caring.sass.nursing.service.form;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsResult;
import com.caring.sass.nursing.entity.form.PatientFormFieldReference;
import com.caring.sass.nursing.entity.statistics.StatisticsInterval;

import java.util.List;

/**
 * @ClassName PatientFormFieldReferenceService
 * @Description
 * @Author yangShuai
 * @Date 2022/5/19 13:31
 * @Version 1.0
 */
public interface PatientFormFieldReferenceService  extends SuperService<PatientFormFieldReference> {

    /**
     * 统计 字段的基线值
     * @param formId
     * @param formFieldId
     * @param intervals
     * @param orgIds
     * @param doctorId
     * @param nursingId
     * @return
     */
    TenantStatisticsResult statisticsData(Long formId,
                                          String formFieldId,
                                          List<StatisticsInterval> intervals,
                                          List<Long> orgIds, Long doctorId, Long nursingId);
}
