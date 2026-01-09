package com.caring.sass.nursing.service.information.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.information.CompletionInformationStatisticsMapper;
import com.caring.sass.nursing.entity.information.CompletenessInformation;
import com.caring.sass.nursing.entity.information.CompletionInformationStatistics;
import com.caring.sass.nursing.entity.information.CompletionInformationStatisticsDetail;
import com.caring.sass.nursing.entity.information.InformationMonitoringInterval;
import com.caring.sass.nursing.service.information.CompletenessInformationService;
import com.caring.sass.nursing.service.information.CompletionInformationStatisticsDetailService;
import com.caring.sass.nursing.service.information.CompletionInformationStatisticsService;
import com.caring.sass.nursing.service.information.InformationMonitoringIntervalService;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.NursingStaffPageDTO;
import com.caring.sass.user.entity.NursingStaff;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 信息完整度统计
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service
@AllArgsConstructor
public class CompletionInformationStatisticsServiceImpl
        extends SuperServiceImpl<CompletionInformationStatisticsMapper, CompletionInformationStatistics> implements CompletionInformationStatisticsService {

    @Autowired
    private final InformationMonitoringIntervalService intervalService;

    @Autowired
    private final CompletenessInformationService completenessInformationService;

    @Autowired
    private final CompletionInformationStatisticsDetailService statisticsDetailService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    NursingStaffApi nursingStaffApi;



    public CompletionInformationStatistics get(LocalDate localDate, Long nursingId) {
        if (localDate.equals(LocalDate.now())) {
            // 是今天。
            // 去统计一下
            LbqWrapper<CompletenessInformation> apply = Wraps.<CompletenessInformation>lbQ()
                    .apply(" patient_id in (" + " SELECT id from u_user_patient where service_advisor_id = '" + nursingId + "')");
            int count = completenessInformationService.count(apply);
            CompletionInformationStatistics statistics = new CompletionInformationStatistics();
            statistics.setNursingId(nursingId);
            statistics.setPatientTotal(count);
            statistics.setStatisticsDate(localDate);
            LbqWrapper<InformationMonitoringInterval> lbqWrapper = new LbqWrapper<>();
            lbqWrapper.orderByAsc(InformationMonitoringInterval::getMinValue);
            List<InformationMonitoringInterval> informationMonitoringIntervals = intervalService.list(lbqWrapper);
            if (CollUtil.isEmpty(informationMonitoringIntervals)) {
                return statistics;
            }
            List<CompletionInformationStatisticsDetail> statisticsDetails = new ArrayList<>(informationMonitoringIntervals.size());
            Long statisticsId = statistics.getId();
            for (InformationMonitoringInterval interval : informationMonitoringIntervals) {
                apply = Wraps.<CompletenessInformation>lbQ()
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
                    int detailCount = completenessInformationService.count(apply);
                    CompletionInformationStatisticsDetail statisticsDetail = CompletionInformationStatisticsDetail.builder()
                            .statisticsId(statisticsId)
                            .intervalId(interval.getId())
                            .intervalName(interval.getName())
                            .intervalMaxValue(maxValue)
                            .intervalMinValue(minValue)
                            .patientNumber(detailCount)
                            .build();
                    if (count == 0 || detailCount == 0) {
                        statisticsDetail.setIntervalProportion(0);
                    } else {
                        BigDecimal totalDecimal = new BigDecimal(count);
                        BigDecimal chartDecimal = new BigDecimal(detailCount);
                        statisticsDetail.setIntervalProportion(BigDecimalUtils.proportion(chartDecimal, totalDecimal));
                    }
                    statisticsDetails.add(statisticsDetail);
                }
            }
            statistics.setStatisticsDetailList(statisticsDetails);
            return statistics;
        } else {
            LbqWrapper<CompletionInformationStatistics> wrapper = Wraps.<CompletionInformationStatistics>lbQ()
                    .eq(CompletionInformationStatistics::getStatisticsDate, localDate)
                    .eq(CompletionInformationStatistics::getNursingId, nursingId)
                    .last(" limit 0,1 ");
            List<CompletionInformationStatistics> statistics = baseMapper.selectList(wrapper);
            if (CollUtil.isNotEmpty(statistics)) {
                CompletionInformationStatistics informationStatistics = statistics.get(0);
                Long statisticsId = informationStatistics.getId();
                LbqWrapper<CompletionInformationStatisticsDetail> lbqWrapper = Wraps.<CompletionInformationStatisticsDetail>lbQ()
                        .orderByAsc(CompletionInformationStatisticsDetail::getIntervalMinValue)
                        .eq(CompletionInformationStatisticsDetail::getStatisticsId, statisticsId);
                List<CompletionInformationStatisticsDetail> details = statisticsDetailService.list(lbqWrapper);
                informationStatistics.setStatisticsDetailList(details);
                return informationStatistics;
            }
        }
        return null;

    }


    /**
     * 统计项目下每个专员。他的患者信息完整度情况
     * @param tenantCode
     * @param informationMonitoringIntervals
     */
    private void statisticsNursingCompletenessInformation(String tenantCode, LocalDate statisticsDate,
                                                    List<InformationMonitoringInterval> informationMonitoringIntervals) {
        BaseContextHandler.setTenant(tenantCode);

        // 分页查询 项目下的专员。
        int current = 1;
        PageParams<NursingStaffPageDTO> pageParams = new PageParams<>();
        pageParams.setSize(50);
        while (true) {
            pageParams.setCurrent(current);
            R<IPage<NursingStaff>> selectPage = nursingStaffApi.getNursingStaffIds(pageParams);
            current++;
            if (!selectPage.getIsSuccess()) {
                break;
            }
            IPage<NursingStaff> data = selectPage.getData();
            List<NursingStaff> records = data.getRecords();
            statisticsNursingCompletenessInformation(records, statisticsDate, informationMonitoringIntervals);
            // 退出循环
            if (CollUtil.isEmpty(records)) {
                break;
            }
            if (current > data.getPages()) {
                break;
            }
        }

    }

    /**
     * 统计专员 每个区间的患者人数
     * @param nursingStaffs
     * @param informationMonitoringIntervals
     */
    private void statisticsNursingCompletenessInformation(List<NursingStaff> nursingStaffs, LocalDate statisticsDate,
                                                          List<InformationMonitoringInterval> informationMonitoringIntervals ) {
        // 统计 专员下 患者的总人数

        if (CollUtil.isEmpty(nursingStaffs)) {
            return;
        }
        for (NursingStaff nursingStaff : nursingStaffs) {
            Long nursingStaffId = nursingStaff.getId();
            if (Objects.isNull(nursingStaffId)) {
                continue;
            }
            // 统计此时 医助下的信息完整度有多少个。
            LbqWrapper<CompletenessInformation> apply = Wraps.<CompletenessInformation>lbQ()
                    .apply(" patient_id in (" + " SELECT id from u_user_patient where service_advisor_id = '" + nursingStaffId + "')");
            int count = completenessInformationService.count(apply);
            CompletionInformationStatistics statistics = new CompletionInformationStatistics();
            statistics.setNursingId(nursingStaffId);
            statistics.setPatientTotal(count);
            statistics.setStatisticsDate(statisticsDate);
            baseMapper.insert(statistics);
            if (CollUtil.isEmpty(informationMonitoringIntervals)) {
                continue;
            }
            List<CompletionInformationStatisticsDetail> statisticsDetails = new ArrayList<>(informationMonitoringIntervals.size());
            Long statisticsId = statistics.getId();
            BigDecimal zero = new BigDecimal(0);
            for (InformationMonitoringInterval interval : informationMonitoringIntervals) {
                apply = Wraps.<CompletenessInformation>lbQ()
                        .apply(" patient_id in (" + " SELECT id from u_user_patient where service_advisor_id = '" + nursingStaffId + "')");
                if (Objects.nonNull(interval.getMaxValue()) && Objects.nonNull(interval.getMinValue())) {
                    Double minValue = interval.getMinValue();
                    Double maxValue = interval.getMaxValue();
                    if (Objects.nonNull(minValue)) {
                        apply.ge(CompletenessInformation::getCompletion, minValue);
                    }
                    if (Objects.nonNull(maxValue)) {
                        apply.le(CompletenessInformation::getCompletion, maxValue);
                    }
                    int detailCount = completenessInformationService.count(apply);
                    CompletionInformationStatisticsDetail statisticsDetail = CompletionInformationStatisticsDetail.builder()
                            .statisticsId(statisticsId)
                            .intervalId(interval.getId())
                            .intervalName(interval.getName())
                            .intervalMaxValue(maxValue)
                            .intervalMinValue(minValue)
                            .patientNumber(detailCount)
                            .build();
                    if (count == 0 || detailCount == 0) {
                        statisticsDetail.setIntervalProportion(0);
                    } else {
                        BigDecimal totalDecimal = new BigDecimal(count);
                        BigDecimal chartDecimal = new BigDecimal(detailCount);
                        statisticsDetail.setIntervalProportion(BigDecimalUtils.proportion(chartDecimal, totalDecimal));
                    }
                    statisticsDetails.add(statisticsDetail);
                }
            }
            if (CollUtil.isNotEmpty(statisticsDetails)) {
                statisticsDetailService.saveBatchSomeColumn(statisticsDetails);
            }
        }


    }

    /**
     * 项目下 医助管理的患者信息完整度统计
     */
    @Override
    public void statisticsCompletenessInformationTask(LocalDate statisticsDate) {

        R<List<Tenant>> normalTenant = tenantApi.getAllNormalTenant();
        if (normalTenant.getIsSuccess()) {
            List<Tenant> tenantList = normalTenant.getData();
            if (CollUtil.isEmpty(tenantList)) {
                return;
            }
            for (Tenant tenant : tenantList) {
                String code = tenant.getCode();
                BaseContextHandler.setTenant(code);

                LbqWrapper<InformationMonitoringInterval> lbqWrapper = new LbqWrapper<>();
                List<InformationMonitoringInterval> informationMonitoringIntervals = intervalService.list(lbqWrapper);
                SaasGlobalThreadPool.execute( () -> statisticsNursingCompletenessInformation(code, statisticsDate, informationMonitoringIntervals));
            }
        }


    }

}
