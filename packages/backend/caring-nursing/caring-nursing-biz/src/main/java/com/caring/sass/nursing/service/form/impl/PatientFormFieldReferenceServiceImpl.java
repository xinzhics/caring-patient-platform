package com.caring.sass.nursing.service.form.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.form.PatientFormFieldReferenceMapper;
import com.caring.sass.nursing.dto.statistics.FanChartData;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsResult;
import com.caring.sass.nursing.entity.form.PatientFormFieldReference;
import com.caring.sass.nursing.entity.statistics.StatisticsInterval;
import com.caring.sass.nursing.service.form.PatientFormFieldReferenceService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName PatientFormFieldReferencServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/5/19 13:32
 * @Version 1.0
 */
@Slf4j
@Service
public class PatientFormFieldReferenceServiceImpl extends SuperServiceImpl<PatientFormFieldReferenceMapper, PatientFormFieldReference> implements PatientFormFieldReferenceService {

    @Autowired
    PatientApi patientApi;


    @Override
    public boolean save(PatientFormFieldReference model) {

        R<Patient> patientR = patientApi.getBaseInfoForStatisticsData(model.getPatientId());
        if (patientR.getIsSuccess() != null && patientR.getIsSuccess()) {
            Patient data = patientR.getData();
            if (Objects.nonNull(data)) {
                model.setDoctorId(data.getDoctorId());
                model.setNursingId(data.getServiceAdvisorId());
                model.setOrgId(data.getOrganId());
            }
        }
        return super.save(model);
    }

    /**
     * 基线值统计
     * @param formId
     * @param formFieldId
     * @param intervals
     * @param orgIds
     * @param doctorId
     * @param nursingId
     * @return
     */
    @Override
    public TenantStatisticsResult statisticsData(Long formId,
                                                 String formFieldId,
                                                 List<StatisticsInterval> intervals,
                                                 List<Long> orgIds, Long doctorId, Long nursingId) {

        // 区间设置不存在。 不统计
        if (CollUtil.isEmpty(intervals)) {
            return null;
        }

//        if (CollUtil.isEmpty(orgIds) && Objects.isNull(doctorId) && Objects.isNull(nursingId)) {
//            return null;
//        }
        List<FanChartData> fanCharts = new ArrayList<>(intervals.size());
        FanChartData fanChartData;
        int total = 0;
        for (StatisticsInterval interval : intervals) {
            fanChartData = new FanChartData();
            LbqWrapper<PatientFormFieldReference> wrapper = Wraps.<PatientFormFieldReference>lbQ()
                    .eq(PatientFormFieldReference::getFieldId, formFieldId)
                    .ge(PatientFormFieldReference::getReferenceValue, interval.getMinValue())
                    .le(PatientFormFieldReference::getReferenceValue, interval.getMaxValue());
            if (CollUtil.isNotEmpty(orgIds)) {
                wrapper.in(PatientFormFieldReference::getOrgId, orgIds);
            }
            if (Objects.nonNull(doctorId)) {
                wrapper.in(PatientFormFieldReference::getDoctorId, doctorId);
            }
            if (Objects.nonNull(nursingId)) {
                wrapper.in(PatientFormFieldReference::getNursingId, nursingId);
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count == null) {
                fanChartData.setTotal(0);
            } else {
                fanChartData.setTotal(count);
                total = total + count;
            }
            fanChartData.setName(interval.getStatisticsIntervalName());
            fanChartData.setMaxValue(interval.getMaxValue());
            fanChartData.setMinValue(interval.getMinValue());
            fanChartData.setColor(interval.getColor());
            fanCharts.add(fanChartData);
        }

        for (FanChartData fanChart : fanCharts) {
            if (total == 0) {
                fanChart.setProportion(0);
            } else if (fanChart.getTotal() == 0) {
                fanChart.setProportion(0);
            } else {
                BigDecimal totalDecimal = new BigDecimal(total);
                BigDecimal chartDecimal = new BigDecimal(fanChart.getTotal());
                fanChart.setProportion(BigDecimalUtils.proportion(chartDecimal, totalDecimal));
            }
        }

        return TenantStatisticsResult.builder().fanCharts(fanCharts).build();
    }
}
