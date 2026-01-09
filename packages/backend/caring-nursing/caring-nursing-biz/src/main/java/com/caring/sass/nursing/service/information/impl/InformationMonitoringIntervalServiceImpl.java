package com.caring.sass.nursing.service.information.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.information.CompletenessInformationMapper;
import com.caring.sass.nursing.dao.information.InformationMonitoringIntervalMapper;
import com.caring.sass.nursing.entity.information.CompletenessInformation;
import com.caring.sass.nursing.entity.information.InformationMonitoringInterval;
import com.caring.sass.nursing.service.information.InformationMonitoringIntervalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 信息完整度区间设置
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service

public class InformationMonitoringIntervalServiceImpl extends SuperServiceImpl<InformationMonitoringIntervalMapper, InformationMonitoringInterval> implements InformationMonitoringIntervalService {

    @Autowired
    CompletenessInformationMapper completenessInformationMapper;


    @Override
    public List<InformationMonitoringInterval> nursingIntervalStatistics(Long nursingId) {

        List<InformationMonitoringInterval> monitoringIntervals = new ArrayList<>();
        InformationMonitoringInterval build = InformationMonitoringInterval.builder()
                .name("全部")
                .build();
        monitoringIntervals.add(build);
        List<InformationMonitoringInterval> intervalList = baseMapper.selectList(Wraps.<InformationMonitoringInterval>lbQ()
                .orderByAsc(InformationMonitoringInterval::getMinValue));

        LbqWrapper<CompletenessInformation> apply = Wraps.<CompletenessInformation>lbQ()
                .apply(" patient_id in (" + " SELECT id from u_user_patient where service_advisor_id = '" + nursingId + "')");
        int count = completenessInformationMapper.selectCount(apply);
        build.setPatientNumber(count);
        if (CollUtil.isNotEmpty(intervalList)) {
            for (InformationMonitoringInterval interval : intervalList) {

                apply = Wraps.<CompletenessInformation>lbQ()
                        .eq(CompletenessInformation::getComplete, 0)
                        .apply(" patient_id in (" + " SELECT id from u_user_patient where service_advisor_id = '" + nursingId + "')");
                if (Objects.nonNull(interval.getMaxValue()) && Objects.nonNull(interval.getMinValue())) {
                    Double minValue = interval.getMinValue();
                    Double maxValue = interval.getMaxValue();
                    if (Objects.nonNull(minValue)) {
                        apply.ge(CompletenessInformation::getCompletion, minValue);
                    }
                    if (Objects.nonNull(maxValue)) {
                        apply.le(CompletenessInformation::getCompletion, maxValue);
                    }
                    int intervalCount = completenessInformationMapper.selectCount(apply);
                    interval.setPatientNumber(intervalCount);
                } else {
                    interval.setPatientNumber(0);
                }
                monitoringIntervals.add(interval);
            }
//            monitoringIntervals.addAll(intervalList);
        }
        return monitoringIntervals;
    }
}
