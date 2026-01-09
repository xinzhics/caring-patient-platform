package com.caring.sass.nursing.service.statistics.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.statistics.StatisticsMasterMapper;
import com.caring.sass.nursing.dto.statistics.StatisticsStatus;
import com.caring.sass.nursing.entity.statistics.StatisticsMaster;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;
import com.caring.sass.nursing.service.statistics.StatisticsMasterService;
import com.caring.sass.tenant.api.TenantApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 主统计图 Mapper
 *
 */

@Slf4j
@Service
public class StatisticsMasterServiceImpl extends
        SuperServiceImpl<StatisticsMasterMapper, StatisticsMaster>
        implements StatisticsMasterService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public StatisticsMaster getByTaskId(Serializable taskId) {
        List<StatisticsMaster> selectList = baseMapper.selectList(Wraps.<StatisticsMaster>lbQ()
                .eq(StatisticsMaster::getStatisticsTaskId, taskId)
                .last(" limit 0, 1"));
        if (CollUtil.isNotEmpty(selectList)) {
            return selectList.get(0);
        }
        return null;
    }


    /**
     * 保存 主统计图
     * @param increment
     * @param statisticsTask
     */
    @Override
    public void createOrUpdateStatisticsMaster( Long increment, StatisticsTask statisticsTask) {

        StatisticsMaster statisticsMaster = getByTaskId(statisticsTask.getId());
        if (Objects.isNull(statisticsMaster)) {
            statisticsMaster = new StatisticsMaster();
            statisticsMaster.setChartWidth(100);
            statisticsMaster.setBelongType(StatisticsStatus.CUSTOMIZE);
            statisticsMaster.setShowOrHide(StatisticsStatus.SHOW);
            if (Objects.nonNull(increment)) {
                statisticsMaster.setChartOrder(increment.intValue());
            }
        }
        statisticsMaster.setFormFieldLabel(statisticsTask.getFormFieldLabel());
        statisticsMaster.setStatisticsTaskId(statisticsTask.getId());
        if (statisticsMaster.getId() == null) {
            baseMapper.insert(statisticsMaster);
        } else {
            baseMapper.updateById(statisticsMaster);
        }
        String tenant = BaseContextHandler.getTenant();
        ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, tenant, statisticsMaster.getChartOrder(),  statisticsMaster.getId());
        statisticsTask.setStatisticsMaster(statisticsMaster);
    }


    @Override
    public void removeByTaskId(Serializable taskId, String tenant) {
        StatisticsMaster master = getByTaskId(taskId);
        if (Objects.nonNull(master)) {
            ChartOrderRedisUtil.delTaskChartOrder(redisTemplate, tenant, master.getChartOrder());
            baseMapper.deleteById(master);
        }
    }


    /**
     * 初始化 项目默认的 统计图表
     */
    @Transactional
    @Override
    public void initTenantDefaultChart() {

        initUserProfile();

        initDiagnosisType();

        initReturnRate();

    }

    /**
     * 初始化 用户概要
     */
    private void initUserProfile() {

        StatisticsMaster master = StatisticsMaster.builder()
                .statisticsTaskId(0L)
                .belongType(StatisticsStatus.USER_PROFILE)
                .chartWidth(null)
                .formFieldLabel("用户概要")
                .showOrHide(StatisticsStatus.SHOW)
                .chartType(null)
                .chartOrder(null).build();
        String tenant = BaseContextHandler.getTenant();
        if (StringUtils.isNotEmptyString(tenant)) {
            baseMapper.delete(Wraps.<StatisticsMaster>lbQ().eq(StatisticsMaster::getBelongType, StatisticsStatus.USER_PROFILE));
        }
        baseMapper.insert(master);

    }

    /**
     * 初始化诊断类型
     */
    private void initDiagnosisType() {

        StatisticsMaster master = StatisticsMaster.builder()
                .statisticsTaskId(0L)
                .belongType(StatisticsStatus.DIAGNOSIS_TYPE)
                .chartWidth(null)
                .formFieldLabel("诊断类型")
                .showOrHide(StatisticsStatus.SHOW)
                .chartType(null)
                .chartOrder(null).build();
        String tenant = BaseContextHandler.getTenant();
        if (StringUtils.isNotEmptyString(tenant)) {
            baseMapper.delete(Wraps.<StatisticsMaster>lbQ().eq(StatisticsMaster::getBelongType, StatisticsStatus.DIAGNOSIS_TYPE));
        }
        baseMapper.insert(master);

    }

    /**
     * 初始化复诊率  统计图
     */
    private void initReturnRate() {

        StatisticsMaster master = StatisticsMaster.builder()
                .statisticsTaskId(0L)
                .belongType(StatisticsStatus.RETURN_RATE)
                .chartWidth(100)
                .formFieldLabel("复诊率")
                .showOrHide(StatisticsStatus.SHOW)
                .chartType(null)
                .chartOrder(1)
                .build();
        String tenant = BaseContextHandler.getTenant();
        if (StringUtils.isNotEmptyString(tenant)) {
            baseMapper.delete(Wraps.<StatisticsMaster>lbQ().eq(StatisticsMaster::getBelongType, StatisticsStatus.RETURN_RATE));
        }
        baseMapper.insert(master);
        ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, tenant, 1, master.getId());

    }




}
