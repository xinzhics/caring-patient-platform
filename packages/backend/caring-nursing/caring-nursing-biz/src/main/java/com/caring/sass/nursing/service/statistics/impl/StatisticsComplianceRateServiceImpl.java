package com.caring.sass.nursing.service.statistics.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.statistics.StatisticsComplianceRateMapper;
import com.caring.sass.nursing.dto.statistics.StatisticsStatus;
import com.caring.sass.nursing.entity.statistics.StatisticsComplianceRate;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;
import com.caring.sass.nursing.service.statistics.StatisticsComplianceRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 达标率 Mapper
 *
 * @author Dell
 */
@Slf4j
@Service
public class StatisticsComplianceRateServiceImpl  extends
        SuperServiceImpl<StatisticsComplianceRateMapper, StatisticsComplianceRate>
        implements StatisticsComplianceRateService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public StatisticsComplianceRate getByTaskId(Serializable taskId) {
        List<StatisticsComplianceRate> selectList = baseMapper.selectList(Wraps.<StatisticsComplianceRate>lbQ()
                .eq(StatisticsComplianceRate::getStatisticsTaskId, taskId)
                .last(" limit 0, 1"));
        if (CollUtil.isNotEmpty(selectList)) {
            return selectList.get(0);
        }
        return null;
    }

    /**
     * 创建 或者 删除达标率 图
     * @param complianceRateChart
     * @param statisticsTask
     */
    @Override
    public void createOrDeleteComplianceRate(boolean complianceRateChart, StatisticsTask statisticsTask) {
        String tenant = BaseContextHandler.getTenant();
        StatisticsComplianceRate complianceRate = null;
        if (complianceRateChart) {
            complianceRate = getByTaskId(statisticsTask.getId());
            if (Objects.isNull(complianceRate)) {
                complianceRate = new StatisticsComplianceRate();
                complianceRate.setChartWidth(50);
                complianceRate.setShowOrHide(StatisticsStatus.SHOW);
                Long increment = ChartOrderRedisUtil.redisTaskSortIncrement(redisTemplate, tenant);
                if (Objects.nonNull(increment)) {
                    complianceRate.setChartOrder(increment.intValue());
                } else {
                    complianceRate.setChartOrder(0);
                }
            }
            complianceRate.setStatisticsTaskId(statisticsTask.getId());
            complianceRate.setFormFieldLabel(statisticsTask.getFormFieldLabel());
            if (complianceRate.getId() == null) {
                baseMapper.insert(complianceRate);
            } else {
                baseMapper.updateById(complianceRate);
            }
            ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, tenant, complianceRate.getChartOrder(), complianceRate.getId());
        } else {
            removeByTaskId(statisticsTask.getId(), tenant);

        }
    }

    @Override
    public void removeByTaskId(Serializable taskId, String tenant) {
        StatisticsComplianceRate complianceRate = getByTaskId(taskId);
        if (Objects.nonNull(complianceRate)) {
            ChartOrderRedisUtil.delTaskChartOrder(redisTemplate, tenant, complianceRate.getChartOrder());
            baseMapper.deleteById(complianceRate);
        }
    }
}
