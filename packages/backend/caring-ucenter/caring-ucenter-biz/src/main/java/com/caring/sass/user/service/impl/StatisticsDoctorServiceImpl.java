package com.caring.sass.user.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.user.dao.StatisticsDoctorMapper;
import com.caring.sass.user.entity.StatisticsDoctor;
import com.caring.sass.user.service.StatisticsDoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 医生统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Slf4j
@Service

public class StatisticsDoctorServiceImpl extends SuperServiceImpl<StatisticsDoctorMapper, StatisticsDoctor> implements StatisticsDoctorService {

    /**
     * 从数据库中同步统计信息
     */
    @Override
    public void syncFromDb() {
        // 清空数据表
        baseMapper.delete(Wrappers.emptyWrapper());
        // 重新查找统计数据
        List<StatisticsDoctor> statisticsDoctors = baseMapper.queryDb();
        // 持久化
        baseMapper.insertBatchSomeColumn(statisticsDoctors);
    }
}
