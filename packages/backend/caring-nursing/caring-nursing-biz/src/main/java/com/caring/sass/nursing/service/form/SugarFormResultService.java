package com.caring.sass.nursing.service.form;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.blood.BloodSugarTrendResult;
import com.caring.sass.nursing.dto.blood.SugarDTO;
import com.caring.sass.nursing.entity.form.SugarFormResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 血糖表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface SugarFormResultService extends SuperService<SugarFormResult> {

    List<Map<String, Object>> loadPatientBloodSugarTrendData(Integer type, Integer week, Long patientId);

    List<SugarDTO> findSugarByTime(String patientId, Long startTime, Long endTime);

    JSONObject list(Integer week, Long patientId);
}
