package com.caring.sass.nursing.service.statistics.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.nursing.dao.statistics.*;
import com.caring.sass.nursing.dto.statistics.*;
import com.caring.sass.nursing.entity.statistics.*;
import com.caring.sass.nursing.service.form.PatientFormFieldReferenceService;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import com.caring.sass.nursing.service.statistics.StatisticsBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName StatisticsBizServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/5/16 16:28
 * @Version 1.0
 */
@Slf4j
@Service
public class StatisticsBizServiceImpl implements StatisticsBizService {

    @Autowired
    StatisticsMasterMapper statisticsMasterMapper;

    @Autowired
    StatisticsComplianceRateMapper statisticsComplianceRateMapper;

    @Autowired
    StatisticsBaselineValueMapper statisticsBaselineValueMapper;

    @Autowired
    StatisticsTaskMapper statisticsTaskMapper;

    @Autowired
    StatisticsIntervalMapper statisticsIntervalMapper;

    @Autowired
    PatientFormFieldReferenceService patientFormFieldReferenceService;

    @Autowired
    ReminderLogService reminderLogService;

    @Autowired
    StatisticsDataMapper statisticsDataMapper;

    @Autowired
    StatisticsDataMonthMapper statisticsDataMonthMapper;

    @Autowired
    StatisticsDataNewMapper statisticsDataNewMapper;
    /**
     * 项目统计图显示的顺序
     * @return
     */
    @Override
    public List<TenantStatisticsChartList> tenantStatisticsChartLists() {

        // 查询配置好的统计任务
        List<StatisticsTask> taskList = statisticsTaskMapper.selectList(Wraps.<StatisticsTask>lbQ().eq(StatisticsTask::getStep, StatisticsStatus.STEP_FINISH));
        List<Long> taskIds = taskList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        taskIds.add(0L);
        List<StatisticsMaster> masters = statisticsMasterMapper.selectList(Wraps.<StatisticsMaster>lbQ()
                .in(StatisticsMaster::getStatisticsTaskId, taskIds)
                .eq(StatisticsMaster::getShowOrHide, StatisticsStatus.SHOW)
                .notIn(StatisticsMaster::getBelongType, StatisticsStatus.USER_PROFILE, StatisticsStatus.DIAGNOSIS_TYPE));
        List<StatisticsComplianceRate> complianceRates = statisticsComplianceRateMapper.selectList(Wraps.<StatisticsComplianceRate>lbQ()
                .eq(StatisticsComplianceRate::getShowOrHide, StatisticsStatus.SHOW)
                .in(StatisticsComplianceRate::getStatisticsTaskId, taskIds));
        List<StatisticsBaselineValue> baselineValues = statisticsBaselineValueMapper.selectList(Wraps.<StatisticsBaselineValue>lbQ()
                .eq(StatisticsBaselineValue::getShowOrHide, StatisticsStatus.SHOW)
                .in(StatisticsBaselineValue::getStatisticsTaskId, taskIds));
        List<TenantStatisticsChartList> statisticsChartLists = new ArrayList<>(40);
        Map<Long, StatisticsTask> taskMap = taskList.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
        StatisticsTask task = null;
        if (CollUtil.isNotEmpty(masters)) {
            for (StatisticsMaster master : masters) {
                Long statisticsTaskId = master.getStatisticsTaskId();
                if (Objects.nonNull(statisticsTaskId)) {
                    task = taskMap.get(statisticsTaskId);
                }
                statisticsChartLists.add(TenantStatisticsChartList
                        .builder()
                        .chartType(master.getChartType())
                        .createTime(master.getCreateTime())
                        .chartWidth(master.getChartWidth())
                        .chartOrder(master.getChartOrder())
                        .showPercentage(Objects.nonNull(task) ? task.getShowPercentage() : true)
                        .showNumber(Objects.nonNull(task) ? task.getShowNumber() : true)
                        .statisticsTaskId(statisticsTaskId)
                        .formFieldLabel(master.getFormFieldLabel())
                        .statisticsDataType(Objects.equals(master.getBelongType(), StatisticsStatus.RETURN_RATE) ? StatisticsStatus.RETURN_RATE : "master_chart")
                        .build());
            }
        }

        if (CollUtil.isNotEmpty(complianceRates)) {
            for (StatisticsComplianceRate complianceRate : complianceRates) {
                Long statisticsTaskId = complianceRate.getStatisticsTaskId();
                task = taskMap.get(statisticsTaskId);
                statisticsChartLists.add(TenantStatisticsChartList
                        .builder()
                        .chartType("lineChart")
                        .createTime(complianceRate.getCreateTime())
                        .showPercentage(Objects.nonNull(task) ? task.getShowPercentage() : true)
                        .showNumber(Objects.nonNull(task) ? task.getShowNumber() : true)
                        .chartWidth(complianceRate.getChartWidth())
                        .chartOrder(complianceRate.getChartOrder())
                        .statisticsTaskId(statisticsTaskId)
                        .formFieldLabel(complianceRate.getFormFieldLabel())
                        .statisticsDataType("compliance_rate")
                        .build());
            }
        }

        if (CollUtil.isNotEmpty(baselineValues)) {
            for (StatisticsBaselineValue baselineValue : baselineValues) {
                Long statisticsTaskId = baselineValue.getStatisticsTaskId();
                task = taskMap.get(statisticsTaskId);
                statisticsChartLists.add(TenantStatisticsChartList
                        .builder()
                        .chartType("fanChart")
                        .createTime(baselineValue.getCreateTime())
                        .showPercentage(Objects.nonNull(task) ? task.getShowPercentage() : true)
                        .showNumber(Objects.nonNull(task) ? task.getShowNumber() : true)
                        .chartWidth(baselineValue.getChartWidth())
                        .chartOrder(baselineValue.getChartOrder())
                        .statisticsTaskId(baselineValue.getStatisticsTaskId())
                        .formFieldLabel(baselineValue.getFormFieldLabel())
                        .statisticsDataType("base_line_value")
                        .build());
            }
        }

        statisticsChartLists.sort(Comparator.comparingInt(TenantStatisticsChartList::getChartOrder));

        return statisticsChartLists;

    }

    /**
     * 根据前端请求的参数。
     * 统计相关的数据进行返回
     * @param param
     * @return
     */
    @Override
    public TenantStatisticsResult tenantDataStatisticsOrg(TenantDataStatisticsParam param, List<Long> orgIds) {
        return tenantDataStatistics(param, orgIds, null, null);
    }

    /**
     * 医生的患者数据统计
     * @param param
     * @param doctorId
     * @return
     */
    @Override
    public TenantStatisticsResult tenantDataStatisticsDoctor(TenantDataStatisticsParam param, Long doctorId) {
        return tenantDataStatistics(param, null, doctorId, null);
    }

    /**
     * 医助的患者数据统计
     * @param param
     * @param nursing
     * @return
     */
    @Override
    public TenantStatisticsResult tenantDataStatisticsNursing(TenantDataStatisticsParam param, Long nursing) {
        return tenantDataStatistics(param, null, null, nursing);
    }

    /**
     * 统计数据
     * @param param
     * @param orgIds
     * @param doctorId
     * @param nursingId
     * @return
     */
    private TenantStatisticsResult tenantDataStatistics(TenantDataStatisticsParam param,
                                                        List<Long> orgIds,
                                                        Long doctorId, Long nursingId) {

        String statisticsDataType = param.getStatisticsDataType();
        Long statisticsTaskId = param.getStatisticsTaskId();
        LocalDate startTime = param.getStartTime();
        LocalDate endTime = param.getEndTime();
        Period period = param.getPeriod();

        // 复诊率的统计 ( 推送人数 ， 提交人数， )
        if (StatisticsStatus.RETURN_RATE.equals( statisticsDataType)) {
            return reminderLogService.statisticsData(startTime, endTime, period, orgIds, doctorId, nursingId);
        }

        StatisticsTask statisticsTask = statisticsTaskMapper.selectById(statisticsTaskId);
        if (statisticsTask == null) {
            return null;
        }

        // 基线值统计
        if (StatisticsStatus.BASE_LINE_VALUE.equals( statisticsDataType)) {
            List<StatisticsInterval> intervals = statisticsIntervalMapper.selectList(Wraps.<StatisticsInterval>lbQ()
                    .eq(StatisticsInterval::getStatisticsTaskId, statisticsTaskId));
            return patientFormFieldReferenceService.statisticsData(statisticsTask.getFormId(),
                    statisticsTask.getFormFieldId(), intervals,
                    orgIds, doctorId, nursingId);
        }

        // 达标率 统计
        if (StatisticsStatus.COMPLIANCE_RATE.equals( statisticsDataType)) {

            return statisticsData(startTime, endTime, period, statisticsTask, orgIds, doctorId, nursingId);
        }

        // 主统计图是根据 统计任务设置的区间进行统计
        if (StatisticsStatus.MASTER_CHART.equals( statisticsDataType)) {
            List<StatisticsInterval> intervals = statisticsIntervalMapper.selectList(Wraps.<StatisticsInterval>lbQ()
                    .eq(StatisticsInterval::getStatisticsTaskId, statisticsTaskId));
            return statisticsMasterData(statisticsTask, intervals, orgIds, doctorId, nursingId);
        }
        return null;

    }

    /**
     * 主 统计图 统计
     * @param task
     * @param intervals
     * @param orgIds
     * @param doctorId
     * @param nursingId
     * @return
     */
    private TenantStatisticsResult  statisticsMasterData(StatisticsTask task,
                                                         List<StatisticsInterval> intervals,
                                                         List<Long> orgIds,
                                                         Long doctorId,
                                                         Long nursingId) {
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
            LbqWrapper<StatisticsDataNew> wrapper = Wraps.<StatisticsDataNew>lbQ()
                    .eq(StatisticsDataNew::getStatisticsTaskId, task.getId())
                    .eq(StatisticsDataNew::getFormId, task.getFormId())
                    .eq(StatisticsDataNew::getFormFieldId, task.getFormFieldId())
                    .ge(StatisticsDataNew::getSubmitValue, interval.getMinValue())
                    .le(StatisticsDataNew::getSubmitValue, interval.getMaxValue());
            if (CollUtil.isNotEmpty(orgIds)) {
                wrapper.in(StatisticsDataNew::getOrganId, orgIds);
            }
            if (Objects.nonNull(doctorId)) {
                wrapper.in(StatisticsDataNew::getDoctorId, doctorId);
            }
            if (Objects.nonNull(nursingId)) {
                wrapper.in(StatisticsDataNew::getNursingId, nursingId);
            }
            Integer count = statisticsDataNewMapper.selectCount(wrapper);
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


    /**
     * 统计达标率
     * @param startTime
     * @param endTime
     * @param period
     * @param orgIds
     * @param doctorId
     * @param nursingId
     * @return
     */
    private TenantStatisticsResult statisticsData(LocalDate startTime,
                                                  LocalDate endTime,
                                                  Period period,
                                                  StatisticsTask task,
                                                  List<Long> orgIds,
                                                  Long doctorId,
                                                  Long nursingId) {
        boolean showInYear = period.getMonths() > 1 || period.getYears() > 0;
        List<LocalDate> xData = new ArrayList<>(31);
        String formFieldId = task.getFormFieldId();
        Long formId = task.getFormId();
        Long statisticsTaskId = task.getId();
        int year = startTime.getYear();
        int dayOfMonth = startTime.getMonthValue();
        List<Map<String, Object>> allPush;
        List<Map<String, Object>> standardPush;
        Map<String, Object> allPushTmpMap = new HashMap<>();
        Map<String, Object> standardPushTmpMap = new HashMap<>();
        if (showInYear) {
            // 统计月份。  提交人数
            QueryWrap<StatisticsDataMonth> wrapAllPush = Wraps.<StatisticsDataMonth>q()
                    .select("submit_month as submitMonth", "count(id) countNum")
                    .eq("statistics_task_id", statisticsTaskId)
                    .eq("form_field_id", formFieldId)
                    .eq("form_id", formId)
                    .eq("submit_year", year)
                    .groupBy("submit_month");


            // 统计月份 达标人数
            QueryWrap<StatisticsDataMonth> wrapAllPushStandard = Wraps.<StatisticsDataMonth>q()
                    .select("submit_month as submitMonth", "count(id) countNum")
                    .eq("statistics_task_id", statisticsTaskId)
                    .eq("form_field_id", formFieldId)
                    .eq("form_id", formId)
                    .eq("submit_year", year)
                    .eq("reach_the_standard", 1)
                    .groupBy("submit_month");

            if (CollUtil.isNotEmpty(orgIds)) {
                wrapAllPush.in("organ_id", orgIds);
                wrapAllPushStandard.in("organ_id", orgIds);
            }
            if (Objects.nonNull(doctorId)) {
                wrapAllPush.in("doctor_id", doctorId);
                wrapAllPushStandard.in("doctor_id", doctorId);
            }
            if (Objects.nonNull(nursingId)) {
                wrapAllPush.in("nursing_id", nursingId);
                wrapAllPushStandard.in("nursing_id", nursingId);
            }
            allPush = statisticsDataMonthMapper.selectMaps(wrapAllPush);
            standardPush = statisticsDataMonthMapper.selectMaps(wrapAllPushStandard);
            for (Map<String, Object> t : allPush) {
                allPushTmpMap.put(Convert.toStr(t.get("submitMonth")), t.get("countNum"));
            }
            for (Map<String, Object> t : standardPush) {
                standardPushTmpMap.put(Convert.toStr(t.get("submitMonth")), t.get("countNum"));
            }
        } else {

            // 统计日期 提交人数
            QueryWrap<StatisticsData> wrapAllPush = Wraps.<StatisticsData>q()
                    .select("submit_date as submitDate", "count(id) countNum")
                    .eq("statistics_task_id", statisticsTaskId)
                    .eq("form_field_id", formFieldId)
                    .eq("form_id", formId)
                    .eq("submit_year", year)
                    .eq("submit_month", dayOfMonth)
                    .groupBy("submit_date");

            // 达标人数
            QueryWrap<StatisticsData> wrapAllPushStandard = Wraps.<StatisticsData>q()
                    .select("submit_date as submitDate", "count(id) countNum")
                    .eq("statistics_task_id", statisticsTaskId)
                    .eq("form_field_id", formFieldId)
                    .eq("form_id", formId)
                    .eq("submit_year", year)
                    .eq("submit_month", dayOfMonth)
                    .eq("reach_the_standard", 1)
                    .groupBy("submit_date");
            if (CollUtil.isNotEmpty(orgIds)) {
                wrapAllPush.in("organ_id", orgIds);
                wrapAllPushStandard.in("organ_id", orgIds);
            }
            if (Objects.nonNull(doctorId)) {
                wrapAllPush.in("doctor_id", doctorId);
                wrapAllPushStandard.in("doctor_id", doctorId);
            }
            if (Objects.nonNull(nursingId)) {
                wrapAllPush.in("nursing_id", nursingId);
                wrapAllPushStandard.in("nursing_id", nursingId);
            }
            allPush = statisticsDataMapper.selectMaps(wrapAllPush);
            standardPush = statisticsDataMapper.selectMaps(wrapAllPushStandard);
            for (Map<String, Object> t : allPush) {
                allPushTmpMap.put(Convert.toStr(t.get("submitDate")).substring(0, 10), t.get("countNum"));
            }
            for (Map<String, Object> t : standardPush) {
                standardPushTmpMap.put(Convert.toStr(t.get("submitDate")).substring(0, 10), t.get("countNum"));
            }
        }

        List<TenantStatisticsYData> yData;
        // 按年份统计
        if (showInYear) {
            long distance = ChronoUnit.MONTHS.between(startTime, endTime);
            Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> xData.add(f));
            yData = showInYearStatistics(startTime, endTime, allPushTmpMap, standardPushTmpMap);
        } else {
            long distance = ChronoUnit.DAYS.between(startTime, endTime);
            Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> xData.add(f));
            yData = showInMonthStatistics(startTime, endTime, allPushTmpMap, standardPushTmpMap);
        }
        TenantStatisticsResult result = new TenantStatisticsResult();
        result.setXData(xData);
        result.setYData(yData);
        return result;
    }


    /**
     * 按照年统计
     * @param startTime
     * @param endTime
     * @param allPushTmpMap
     * @param standardPushTmpMap
     * @return
     */
    private List<TenantStatisticsYData> showInYearStatistics(LocalDate startTime, LocalDate endTime,
                                                             Map<String, Object> allPushTmpMap,
                                                             Map<String, Object> standardPushTmpMap) {
        // 计算相差的月份
        List<TenantStatisticsYData> yData = new ArrayList<>(3);
        // 推送数量
        List<Integer> allPushYData;
        // 比率
        List<Integer> ratio;

        TenantStatisticsYData statisticsYData;

        long distance = ChronoUnit.MONTHS.between(startTime, endTime);
        allPushYData = new ArrayList<>(allPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
            String thisMonth = Convert.toStr(f.getMonthValue());
            allPushYData.add(allPushTmpMap.get(thisMonth) == null ? 0 : Integer.parseInt(allPushTmpMap.get(thisMonth).toString()));
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(allPushYData);
        statisticsYData.setName("提交人数");
        yData.add(statisticsYData);

        // 达标率
        ratio = new ArrayList<>(standardPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
            String thisMonth = Convert.toStr(f.getMonthValue());
            // 达标数量
            Object receiptMember = standardPushTmpMap.get(thisMonth);
            // 推送数量
            Object pushMember = allPushTmpMap.get(thisMonth);
            if (receiptMember == null || Integer.parseInt(receiptMember.toString()) == 0) {
                ratio.add(0);
            } else if (pushMember == null || Integer.parseInt(pushMember.toString()) == 0) {
                ratio.add(0);
            } else {
                // 计算两个值的商
                BigDecimal receiptMemberDecimal = new BigDecimal(receiptMember.toString());
                BigDecimal pushMemberDecimal = new BigDecimal(pushMember.toString());
                ratio.add(BigDecimalUtils.proportion(receiptMemberDecimal, pushMemberDecimal));
            }
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(ratio);
        statisticsYData.setName("达标率");
        yData.add(statisticsYData);
        return yData;
    }

    /**
     * 按照月份统计
     * @param startTime
     * @param endTime
     * @param allPushTmpMap
     * @param standardPushTmpMap
     * @return
     */
    private List<TenantStatisticsYData> showInMonthStatistics(LocalDate startTime, LocalDate endTime,
                                                              Map<String, Object> allPushTmpMap,
                                                              Map<String, Object> standardPushTmpMap) {
        // 计算相差的月份
        List<TenantStatisticsYData> yData = new ArrayList<>(3);
        // 推送数量
        List<Integer> allPushYData;
        // 比率
        List<Integer> ratio;

        TenantStatisticsYData statisticsYData;

        long distance = ChronoUnit.DAYS.between(startTime, endTime);
        allPushYData = new ArrayList<>(allPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            allPushYData.add(allPushTmpMap.get(thisDay) == null ? 0 : Integer.parseInt(allPushTmpMap.get(thisDay).toString()));
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(allPushYData);
        statisticsYData.setName("提交人数");
        yData.add(statisticsYData);

        // 达标率
        ratio = new ArrayList<>(standardPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            // 提交数量
            Object receiptMember = standardPushTmpMap.get(thisDay);
            // 推送数量
            Object pushMember = allPushTmpMap.get(thisDay);
            if (receiptMember == null || Integer.parseInt(receiptMember.toString()) == 0) {
                ratio.add(0);
            } else if (pushMember == null || Integer.parseInt(pushMember.toString()) == 0) {
                ratio.add(0);
            } else {
                // 计算两个值的商
                int proportion = BigDecimalUtils.proportion(new BigDecimal(receiptMember.toString()), new BigDecimal(pushMember.toString()));
                ratio.add(proportion);
            }
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(ratio);
        statisticsYData.setName("达标率");
        yData.add(statisticsYData);
        return yData;
    }

}
