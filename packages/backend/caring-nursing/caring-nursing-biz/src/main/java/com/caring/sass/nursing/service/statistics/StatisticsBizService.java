package com.caring.sass.nursing.service.statistics;

import com.caring.sass.nursing.dto.statistics.TenantDataStatisticsParam;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsChartList;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsResult;

import java.util.List;

/**
 * @ClassName StatisticsBizService
 * @Description
 * @Author yangShuai
 * @Date 2022/5/16 16:24
 * @Version 1.0
 */
public interface StatisticsBizService {


    /**
     * 项目统计图显示的顺序
     * @return
     */
    List<TenantStatisticsChartList> tenantStatisticsChartLists();


    /**
     * 项目统计的结果
     * @param param
     * @return
     */
    TenantStatisticsResult tenantDataStatisticsOrg(TenantDataStatisticsParam param, List<Long> orgIds);

    /**
     * 医生的患者数据统计
     * @param param
     * @param doctorId
     * @return
     */
    TenantStatisticsResult tenantDataStatisticsDoctor(TenantDataStatisticsParam param, Long doctorId);

    /**
     * 医助的患者数据统计
     * @param param
     * @param nursing
     * @return
     */
    TenantStatisticsResult tenantDataStatisticsNursing(TenantDataStatisticsParam param, Long nursing);
}
