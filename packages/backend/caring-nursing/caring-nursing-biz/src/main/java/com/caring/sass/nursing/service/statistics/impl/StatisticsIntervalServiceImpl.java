package com.caring.sass.nursing.service.statistics.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.statistics.StatisticsIntervalMapper;
import com.caring.sass.nursing.dto.statistics.StatisticsIntervalSaveDTO;
import com.caring.sass.nursing.entity.statistics.StatisticsInterval;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;
import com.caring.sass.nursing.service.statistics.StatisticsIntervalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 区间值 Mapper
 *
 */

@Slf4j
@Service
public class StatisticsIntervalServiceImpl extends
        SuperServiceImpl<StatisticsIntervalMapper, StatisticsInterval>
        implements StatisticsIntervalService {


    @Override
    public void createStatisticsInterval(List<StatisticsIntervalSaveDTO> saveDTOList, StatisticsTask statisticsTask) {

        if (CollUtil.isNotEmpty(saveDTOList)) {
            baseMapper.delete(Wraps.<StatisticsInterval>lbQ().eq(StatisticsInterval::getStatisticsTaskId, statisticsTask.getId()));
            List<StatisticsInterval> list = new ArrayList<>(saveDTOList.size());
            StatisticsInterval statisticsInterval;
            for (StatisticsIntervalSaveDTO intervalSaveDTO : saveDTOList) {
                statisticsInterval = new StatisticsInterval();
                BeanUtils.copyProperties(intervalSaveDTO, statisticsInterval);
                statisticsInterval.setStatisticsTaskId(statisticsTask.getId());
                list.add(statisticsInterval);
            }
            baseMapper.insertBatchSomeColumn(list);
        }

    }
}
