package com.caring.sass.nursing.service.statistics.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataMonthMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataNewMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsTaskMapper;
import com.caring.sass.nursing.dto.statistics.ChartInfo;
import com.caring.sass.nursing.dto.statistics.StatisticsIntervalSaveDTO;
import com.caring.sass.nursing.dto.statistics.StatisticsStatus;
import com.caring.sass.nursing.dto.statistics.StatisticsTaskListDto;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.statistics.*;
import com.caring.sass.nursing.service.statistics.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计任务 Mapper
 *
 */

@Slf4j
@Service
public class StatisticsTaskServiceImpl extends
        SuperServiceImpl<StatisticsTaskMapper, StatisticsTask>
        implements StatisticsTaskService {

    @Autowired
    StatisticsIntervalService statisticsIntervalService;

    @Autowired
    StatisticsComplianceRateService statisticsComplianceRateService;

    @Autowired
    StatisticsBaselineValueService statisticsBaselineValueService;

    @Autowired
    StatisticsMasterService statisticsMasterService;

    @Autowired
    StatisticsDataService statisticsDataService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    StatisticsDataMonthMapper statisticsDataMonthMapper;

    @Autowired
    StatisticsDataNewMapper statisticsDataNewMapper;

    @Autowired
    FormMapper formMapper;


    private Long redisTaskSortIncrement(String tenant) {

        return ChartOrderRedisUtil.redisTaskSortIncrement(redisTemplate, tenant);
    }

    /**
     * 更新 达标率。 基准值， 区间设置
     * @param statisticsTask
     * @param intervalSaveDTOList
     */
    public void handlerSaveOrUpdate(StatisticsTask statisticsTask, List<StatisticsIntervalSaveDTO> intervalSaveDTOList) {
        // 判断 达标率统计图怎么处理
        Boolean complianceRateChart = statisticsTask.getComplianceRateChart();
        if (complianceRateChart != null) {
            statisticsComplianceRateService.createOrDeleteComplianceRate(complianceRateChart, statisticsTask);
        }

        // 判断 基准值统计图怎么处理
        Boolean baselineValueChart = statisticsTask.getBaselineValueChart();
        if (baselineValueChart != null) {
            statisticsBaselineValueService.createOrDeleteBaseLineValue(baselineValueChart, statisticsTask);
        }

        // 更新统计任务设置的区间
        statisticsIntervalService.createStatisticsInterval(intervalSaveDTOList, statisticsTask);

    }


    /**
     * 保存 统计任务
     * @param statisticsTask
     * @param intervalSaveDTOList
     */
    @Transactional
    @Override
    public void save(StatisticsTask statisticsTask, List<StatisticsIntervalSaveDTO> intervalSaveDTOList) {
        statisticsTask.setInitData(false);
        baseMapper.insert(statisticsTask);

        String tenant = BaseContextHandler.getTenant();
        Long increment =  redisTaskSortIncrement(tenant);

        // 创建 或者更新 主统计图
        statisticsMasterService.createOrUpdateStatisticsMaster(increment, statisticsTask);

    }

    /**
     * 更新统计任务
     * @param statisticsTask
     * @param intervalSaveDTOList
     */
    @Transactional
    @Override
    public void update(StatisticsTask statisticsTask, List<StatisticsIntervalSaveDTO> intervalSaveDTOList) {
        StatisticsMaster statisticsMaster = statisticsTask.getStatisticsMaster();
        statisticsTask.setInitData(null);
        super.updateAllById(statisticsTask);

        if (Objects.nonNull(statisticsMaster)) {
            statisticsMasterService.updateById(statisticsMaster);
        }

        handlerSaveOrUpdate(statisticsTask, intervalSaveDTOList);

        if (StatisticsStatus.STEP_FINISH.equals(statisticsTask.getStep())) {
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() -> statisticsDataService.parseTaskResetData(statisticsTask, tenant));
        }

    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }


    @Override
    public boolean removeById(Serializable id) {

        String tenant = BaseContextHandler.getTenant();
        // 移除 统计任务相关的数据
        statisticsDataService.remove(Wraps.<StatisticsData>lbQ().eq(StatisticsData::getStatisticsTaskId, id));
        statisticsDataMonthMapper.delete(Wraps.<StatisticsDataMonth>lbQ().eq(StatisticsDataMonth::getStatisticsTaskId, id));
        statisticsDataNewMapper.delete(Wraps.< StatisticsDataNew>lbQ().eq( StatisticsDataNew::getStatisticsTaskId, id));
        // 删除各 统计图。和统计图的排序站位
        statisticsComplianceRateService.removeByTaskId(id, tenant);
        statisticsBaselineValueService.removeByTaskId(id, tenant);
        statisticsIntervalService.remove(Wraps.<StatisticsInterval>lbQ().eq(StatisticsInterval::getStatisticsTaskId, id));
        statisticsMasterService.removeByTaskId(id, tenant);
        baseMapper.deleteById(id);
        return true;
    }

    /**
     * 获取一个统计任务的详细
     * @param taskId
     * @return
     */
    @Override
    public StatisticsTask getStatisticsInfo(String taskId) {

        StatisticsTask statisticsTask = baseMapper.selectById(taskId);
        if (Objects.nonNull(statisticsTask)) {
            StatisticsMaster master = statisticsMasterService.getOne(Wraps.<StatisticsMaster>lbQ()
                    .eq(StatisticsMaster::getStatisticsTaskId, statisticsTask.getId())
                    .last(" limit 0, 1 "));
            statisticsTask.setStatisticsMaster(master);
        }
        List<StatisticsInterval> intervals = statisticsIntervalService.list(Wraps.<StatisticsInterval>lbQ()
                .eq(StatisticsInterval::getStatisticsTaskId, statisticsTask.getId()));
        statisticsTask.setStatisticsIntervals(intervals);
        return statisticsTask;
    }

    /**
     * 复制 一个项目的 统计配置。到新的项目中去
     * @param fromTenantCode
     * @param toTenantCode
     */
    @Override
    public void copyTask(String fromTenantCode, String toTenantCode) {
        BaseContextHandler.setTenant(fromTenantCode);
        List<StatisticsTask> statisticsTasks = baseMapper.selectList(Wraps.lbQ());
        List<Form> formList = null;
        if (CollUtil.isNotEmpty(statisticsTasks)) {
            // 考虑这个任务也不会超过10个。 所以这里循环保存
            Set<Long> formIdSet = statisticsTasks.stream().map(StatisticsTask::getFormId).collect(Collectors.toSet());
            BaseContextHandler.setTenant(toTenantCode);
            formList = formMapper.selectList(Wraps.<Form>lbQ().in(Form::getSourceFormId, formIdSet)
                    .select(SuperEntity::getId, Form::getSourceFormId));
            BaseContextHandler.setTenant(fromTenantCode);
        }


        List<StatisticsMaster> statisticsMasters = statisticsMasterService.list(Wraps.lbQ());
        List<StatisticsBaselineValue> baselineValues = statisticsBaselineValueService.list();
        List<StatisticsInterval> intervals = statisticsIntervalService.list();
        List<StatisticsComplianceRate> complianceRates = statisticsComplianceRateService.list();

        BaseContextHandler.setTenant(toTenantCode);
        Long newFormId;
        Long oldFormId;
        Map<Long, Long> taskIdMap = new HashMap<>(statisticsTasks.size());
        if (CollUtil.isNotEmpty(statisticsTasks) && CollUtil.isNotEmpty(formList)) {
            Map<Long, Long> newFormIdMap = formList.stream().collect(Collectors.toMap(Form::getSourceFormId, SuperEntity::getId));
            for (StatisticsTask task : statisticsTasks) {
                newFormId = newFormIdMap.get(task.getFormId());
                oldFormId = task.getId();
                task.setId(null);
                task.setFormId(newFormId);
                baseMapper.insert(task);
                taskIdMap.put(oldFormId, task.getId());
            }
        }

        int maxChartOrder = 1;
        Long newTaskId;
        if (CollUtil.isNotEmpty(statisticsMasters)) {
            for (StatisticsMaster master : statisticsMasters) {
                master.setId(null);
                newTaskId = taskIdMap.get(master.getStatisticsTaskId());
                if (newTaskId == null) {
                    master.setStatisticsTaskId(0L);
                } else {
                    master.setStatisticsTaskId(newTaskId);
                }
                Integer chartOrder = master.getChartOrder();
                if (chartOrder != null) {
                    maxChartOrder = Math.max(maxChartOrder, chartOrder);
                }
            }
            statisticsMasterService.saveBatchSomeColumn(statisticsMasters);
            for (StatisticsMaster master : statisticsMasters) {
                if (master.getChartOrder() != null) {
                    ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, toTenantCode, master.getChartOrder(), master.getId());
                }
            }
        }
        if (CollUtil.isNotEmpty(baselineValues)) {
            for (StatisticsBaselineValue baselineValue : baselineValues) {
                baselineValue.setId(null);
                newTaskId = taskIdMap.get(baselineValue.getStatisticsTaskId());
                if (newTaskId == null) {
                    baselineValue.setStatisticsTaskId(0L);
                } else {
                    baselineValue.setStatisticsTaskId(newTaskId);
                }
                Integer chartOrder = baselineValue.getChartOrder();
                if (chartOrder != null) {
                    maxChartOrder = Math.max(maxChartOrder, chartOrder);
                }
            }
            statisticsBaselineValueService.saveBatchSomeColumn(baselineValues);
            for (StatisticsBaselineValue baselineValue : baselineValues) {
                if (baselineValue.getChartOrder() != null) {
                    ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, toTenantCode, baselineValue.getChartOrder(), baselineValue.getId());
                }
            }
        }
        if (CollUtil.isNotEmpty(intervals)) {
            for (StatisticsInterval interval : intervals) {
                interval.setId(null);
                newTaskId = taskIdMap.get(interval.getStatisticsTaskId());
                if (newTaskId == null) {
                    interval.setStatisticsTaskId(0L);
                } else {
                    interval.setStatisticsTaskId(newTaskId);
                }
            }
            statisticsIntervalService.saveBatchSomeColumn(intervals);
        }
        if (CollUtil.isNotEmpty(complianceRates)) {
            for (StatisticsComplianceRate rate : complianceRates) {
                rate.setId(null);
                newTaskId = taskIdMap.get(rate.getStatisticsTaskId());
                if (newTaskId == null) {
                    rate.setStatisticsTaskId(0L);
                } else {
                    rate.setStatisticsTaskId(newTaskId);
                }
                Integer chartOrder = rate.getChartOrder();
                if (chartOrder != null) {
                    maxChartOrder = Math.max(maxChartOrder, chartOrder);
                }
            }
            statisticsComplianceRateService.saveBatchSomeColumn(complianceRates);
            for (StatisticsComplianceRate rate : complianceRates) {
                if (rate.getChartOrder() != null) {
                    ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, toTenantCode, rate.getChartOrder(), rate.getId());
                }
            }
        }
        ChartOrderRedisUtil.setChartOrderInitValue(redisTemplate, toTenantCode, maxChartOrder);
        BaseContextHandler.setTenant(fromTenantCode);

    }

    /**
     * 修改统计图表的隐藏和展示
     * @param chartId
     * @param chartType
     * @param showOrHide
     */
    @Override
    public void updateChartShowOrHide(Long chartId, String chartType, String showOrHide) {

        if (!StatisticsStatus.SHOW.equals(showOrHide) && !StatisticsStatus.HIDE.equals(showOrHide)) {
            throw new BizException("显示隐藏状态不正确");
        }

        if (StatisticsStatus.MASTER_CHART.equals(chartType) || StatisticsStatus.RETURN_RATE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsMaster master = new StatisticsMaster();
            master.setId(chartId);
            master.setShowOrHide(showOrHide);
            statisticsMasterService.updateById(master);

        } else if (StatisticsStatus.BASE_LINE_VALUE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsBaselineValue baselineValue = new StatisticsBaselineValue();
            baselineValue.setId(chartId);
            baselineValue.setShowOrHide(showOrHide);
            statisticsBaselineValueService.updateById(baselineValue);
        } else if (StatisticsStatus.COMPLIANCE_RATE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsComplianceRate complianceRate = new StatisticsComplianceRate();
            complianceRate.setId(chartId);
            complianceRate.setShowOrHide(showOrHide);
            statisticsComplianceRateService.updateById(complianceRate);
        }

    }

    /**
     * 修改统计图 的宽度
     * @param chartId
     * @param chartType
     * @param chartWidth
     */
    @Override
    public void updateChartChartWidth(Long chartId, String chartType, Integer chartWidth) {

        if (chartWidth ==null) {
            return;
        }
        if (!chartWidth.equals(100) && !chartWidth.equals(50)) {
            throw new BizException("宽度只能是100或者50");
        }

        if (StatisticsStatus.MASTER_CHART.equals(chartType)  || StatisticsStatus.RETURN_RATE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsMaster master = new StatisticsMaster();
            master.setId(chartId);
            master.setChartWidth(chartWidth);
            statisticsMasterService.updateById(master);

        } else if (StatisticsStatus.BASE_LINE_VALUE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsBaselineValue baselineValue = new StatisticsBaselineValue();
            baselineValue.setId(chartId);
            baselineValue.setChartWidth(chartWidth);
            statisticsBaselineValueService.updateById(baselineValue);
        } else if (StatisticsStatus.COMPLIANCE_RATE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsComplianceRate complianceRate = new StatisticsComplianceRate();
            complianceRate.setId(chartId);
            complianceRate.setChartWidth(chartWidth);
            statisticsComplianceRateService.updateById(complianceRate);
        }


    }

    /**
     * 修改统计图的排序
     * @param chartId
     * @param chartType
     * @param fromChartOrder
     * @param toChartOrder
     */
    @Override
    public void updateChartChartOrder(Long chartId, String chartType, Integer fromChartOrder, Integer toChartOrder) {
        // 先判断 这个 chartOrder 有没有给设置到其他统计图上去
        String tenant = BaseContextHandler.getTenant();
        if (toChartOrder == null || toChartOrder.equals(0)) {
            return;
        }
        if (!StatisticsStatus.MASTER_CHART.equals(chartType)
                && !StatisticsStatus.RETURN_RATE.equals(chartType)
                && !StatisticsStatus.BASE_LINE_VALUE.equals(chartType)
                && !StatisticsStatus.COMPLIANCE_RATE.equals(chartType)) {
            return;
        }
        String orderChartId = ChartOrderRedisUtil.getTaskChartOrder(redisTemplate, tenant, toChartOrder);
        if (StrUtil.isEmpty(orderChartId)) {
            ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, tenant, toChartOrder, chartId);
        } else {
            if (orderChartId.equals(chartId.toString())) {
                ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, tenant, toChartOrder, chartId);
            } else {
                throw new BizException("序号已经被占用");
            }
        }
        ChartOrderRedisUtil.delTaskChartOrder(redisTemplate, tenant, fromChartOrder);
        ChartOrderRedisUtil.redisTaskSortMaxIncrement(redisTemplate, tenant, toChartOrder);

        if (StatisticsStatus.MASTER_CHART.equals(chartType) || StatisticsStatus.RETURN_RATE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsMaster master = new StatisticsMaster();
            master.setId(chartId);
            master.setChartOrder(toChartOrder);
            statisticsMasterService.updateById(master);

        } else if (StatisticsStatus.BASE_LINE_VALUE.equals(chartType)) {
            // 修改主统计图表的显示隐藏
            StatisticsBaselineValue baselineValue = new StatisticsBaselineValue();
            baselineValue.setId(chartId);
            baselineValue.setChartOrder(toChartOrder);
            statisticsBaselineValueService.updateById(baselineValue);
        } else {
            // 修改主统计图表的显示隐藏
            StatisticsComplianceRate complianceRate = new StatisticsComplianceRate();
            complianceRate.setId(chartId);
            complianceRate.setChartOrder(toChartOrder);
            statisticsComplianceRateService.updateById(complianceRate);
        }


    }

    @Override
    public List<StatisticsTaskListDto> statisticsTaskPage() {

        List<StatisticsTaskListDto> statisticsTaskListDtos = new ArrayList<>(15);

        // 查询 项目的 用户概要， 诊断类型， 复诊率
        queryTenantMaster(statisticsTaskListDtos);

        // 查询项目的 统计任务
        List<StatisticsTask> taskList = baseMapper.selectList(Wraps.lbQ());
        if (CollUtil.isNotEmpty(taskList)) {
            Map<Long, StatisticsMaster> masterMap = null;
            Map<Long, StatisticsBaselineValue> baselineValueMap = null;
            Map<Long, StatisticsComplianceRate> complianceRateMap = null;
            List<Long> taskIds = taskList.stream().map(SuperEntity::getId).collect(Collectors.toList());
            List<StatisticsMaster> masters = statisticsMasterService.list(Wraps.<StatisticsMaster>lbQ()
                    .in(StatisticsMaster::getStatisticsTaskId, taskIds));
            if (CollUtil.isNotEmpty(masters)) {
                masterMap = masters.stream().collect(Collectors.toMap(StatisticsMaster::getStatisticsTaskId, item -> item, (o1, o2) -> o1));
            }
            List<StatisticsBaselineValue> baselineValues = statisticsBaselineValueService.list(Wraps.<StatisticsBaselineValue>lbQ()
                    .in(StatisticsBaselineValue::getStatisticsTaskId, taskIds));
            if (CollUtil.isNotEmpty(baselineValues)) {
                baselineValueMap = baselineValues.stream().collect(Collectors.toMap(StatisticsBaselineValue::getStatisticsTaskId, item -> item, (o1, o2) -> o1));
            }
            List<StatisticsComplianceRate> complianceRates = statisticsComplianceRateService.list(Wraps.<StatisticsComplianceRate>lbQ()
                    .in(StatisticsComplianceRate::getStatisticsTaskId, taskIds));
            if (CollUtil.isNotEmpty(complianceRates)) {
                complianceRateMap = complianceRates.stream().collect(Collectors.toMap(StatisticsComplianceRate::getStatisticsTaskId, item -> item, (o1, o2) -> o1));
            }
            Long statisticsTaskId;
            for (StatisticsTask statisticsTask : taskList) {
                statisticsTaskId = statisticsTask.getId();
                statisticsTaskListDtos.add(StatisticsTaskListDto.builder()
                        .statisticsName(statisticsTask.getStatisticsName())
                        .belongType(StatisticsStatus.CUSTOMIZE)
                        .id(statisticsTaskId)
                        .chartInfoList(chartInfos(masterMap, baselineValueMap, complianceRateMap, statisticsTaskId))
                        .build());
            }

        }

        return statisticsTaskListDtos;
    }

    /**
     * 这是统计任务的 三个 统计图
     * @param masterMap
     * @param baselineValueMap
     * @param complianceRateMap
     * @return
     */
    private List<ChartInfo> chartInfos (Map<Long, StatisticsMaster> masterMap, Map<Long, StatisticsBaselineValue> baselineValueMap,
                                        Map<Long, StatisticsComplianceRate> complianceRateMap, Long taskId) {

        List<ChartInfo> chartInfos = new ArrayList<>();
        if (masterMap != null) {
            StatisticsMaster master = masterMap.get(taskId);
            if (Objects.nonNull(master)) {
                chartInfos.add(ChartInfo.builder()
                        .id(master.getId())
                        .formFieldLabel(master.getFormFieldLabel())
                        .type(StatisticsStatus.MASTER_CHART)
                        .showOrHide(master.getShowOrHide())
                        .chartOrder(master.getChartOrder())
                        .chartWidth(master.getChartWidth())
                        .build());
            }
        }
        if (complianceRateMap != null) {
            StatisticsComplianceRate complianceRate = complianceRateMap.get(taskId);
            if (Objects.nonNull(complianceRate)) {
                chartInfos.add(ChartInfo.builder()
                        .id(complianceRate.getId())
                        .formFieldLabel(complianceRate.getFormFieldLabel())
                        .type(StatisticsStatus.COMPLIANCE_RATE)
                        .showOrHide(complianceRate.getShowOrHide())
                        .chartOrder(complianceRate.getChartOrder())
                        .chartWidth(complianceRate.getChartWidth())
                        .build());
            }
        }

        if (baselineValueMap != null) {
            StatisticsBaselineValue baselineValue = baselineValueMap.get(taskId);
            if (Objects.nonNull(baselineValue)) {
                chartInfos.add(ChartInfo.builder()
                        .id(baselineValue.getId())
                        .formFieldLabel(baselineValue.getFormFieldLabel())
                        .type(StatisticsStatus.BASE_LINE_VALUE)
                        .showOrHide(baselineValue.getShowOrHide())
                        .chartOrder(baselineValue.getChartOrder())
                        .chartWidth(baselineValue.getChartWidth())
                        .build());
            }
        }
        return chartInfos;

    }


    /**
     * 查询 项目的 用户概要， 诊断类型， 复诊率
     * @param statisticsTaskListDtos
     */
    private void queryTenantMaster(List<StatisticsTaskListDto> statisticsTaskListDtos) {

        List<StatisticsMaster> masterList = statisticsMasterService.list(Wraps.<StatisticsMaster>lbQ()
                .in(StatisticsMaster::getBelongType, StatisticsStatus.USER_PROFILE, StatisticsStatus.DIAGNOSIS_TYPE, StatisticsStatus.RETURN_RATE));

        if (CollUtil.isNotEmpty(masterList)) {
            for (StatisticsMaster master : masterList) {
                statisticsTaskListDtos.add(StatisticsTaskListDto.builder()
                        .belongType(master.getBelongType())
                        .showOrHide(master.getShowOrHide())
                        .chartOrder(master.getChartOrder())
                        .chartWidth(master.getChartWidth())
                        .statisticsName(master.getFormFieldLabel())
                        .id(master.getId())
                        .build());
            }
        }

    }

}

