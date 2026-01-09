package com.caring.sass.nursing.service.statistics.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.statistics.StatisticsBaselineValueMapper;
import com.caring.sass.nursing.dto.statistics.StatisticsStatus;
import com.caring.sass.nursing.entity.statistics.StatisticsBaselineValue;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;
import com.caring.sass.nursing.service.statistics.StatisticsBaselineValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 基准值 Mapper
 *
 */
@Slf4j
@Service
public class StatisticsBaselineValueServiceImpl extends
        SuperServiceImpl<StatisticsBaselineValueMapper, StatisticsBaselineValue>
        implements StatisticsBaselineValueService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public StatisticsBaselineValue getByTaskId(Serializable taskId) {
        List<StatisticsBaselineValue> selectList = baseMapper.selectList(Wraps.<StatisticsBaselineValue>lbQ()
                .eq(StatisticsBaselineValue::getStatisticsTaskId, taskId)
                .last(" limit 0, 1"));
        if (CollUtil.isNotEmpty(selectList)) {
            return selectList.get(0);
        }
        return null;
    }

    /**
     * 创建或者删除基准值统计图
     * @param hasBaseLineValue
     * @param statisticsTask
     */
    @Override
    public void createOrDeleteBaseLineValue(boolean hasBaseLineValue,  StatisticsTask statisticsTask) {
        String tenant = BaseContextHandler.getTenant();
        StatisticsBaselineValue baseLineValue = null;
        if (hasBaseLineValue) {
            baseLineValue = getByTaskId(statisticsTask.getId());
            if (Objects.isNull(baseLineValue)) {
                baseLineValue = new StatisticsBaselineValue();
                baseLineValue.setStatisticsTaskId(statisticsTask.getId());
                baseLineValue.setChartWidth(50);
                baseLineValue.setShowOrHide(StatisticsStatus.SHOW);
                Long increment = ChartOrderRedisUtil.redisTaskSortIncrement(redisTemplate, tenant);
                if (Objects.nonNull(increment)) {
                    baseLineValue.setChartOrder(increment.intValue());
                } else {
                    baseLineValue.setChartOrder(0);
                }
            }
            baseLineValue.setFormFieldLabel(statisticsTask.getFormFieldLabel());
            if (baseLineValue.getId() == null) {
                baseMapper.insert(baseLineValue);
            } else {
                baseMapper.updateById(baseLineValue);
            }
            ChartOrderRedisUtil.saveTaskChartOrder(redisTemplate, tenant, baseLineValue.getChartOrder(), baseLineValue.getId());
        } else {
            removeByTaskId(statisticsTask.getId(), tenant);
        }

    }


    @Override
    public void removeByTaskId(Serializable taskId, String tenant) {
        StatisticsBaselineValue baseLineValue = getByTaskId(taskId);
        if (Objects.nonNull(baseLineValue)) {
            ChartOrderRedisUtil.delTaskChartOrder(redisTemplate, tenant, baseLineValue.getChartOrder());
            baseMapper.deleteById(baseLineValue);
        }
    }
}
