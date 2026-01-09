package com.caring.sass.nursing.controller.statistics;

import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.statistics.TenantDataStatisticsParam;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsChartList;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsResult;
import com.caring.sass.nursing.entity.statistics.StatisticsData;
import com.caring.sass.nursing.service.statistics.StatisticsBizService;
import com.caring.sass.nursing.service.statistics.StatisticsDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName StatisticsDataController
 * @Description
 * @Author yangShuai
 * @Date 2022/4/18 17:05
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/statisticsData")
@Api(value = "StatisticsData", tags = "统计图数据")
public class StatisticsDataController extends SuperController<StatisticsDataService, Long, StatisticsData, Object,
        Object, Object> {

    @Autowired
    StatisticsBizService statisticsBizService;

    @Autowired
    UserService userService;

    @ApiOperation("项目配置统计图的顺序类型宽度")
    @GetMapping("tenantStatisticsChartList")
    public R<List<TenantStatisticsChartList>> tenantStatisticsChartList() {

        List<TenantStatisticsChartList> chartLists = statisticsBizService.tenantStatisticsChartLists();
        return R.success(chartLists);

    }


    /**
     * 统计用的复诊率
     * 推送人数
     * 提交人数
     * 计算复诊率
     */
    @ApiOperation("项目端根据统计要求统计数据")
    @PostMapping("tenantDataStatistics/{userId}")
    public R<TenantStatisticsResult> tenantDataStatistics(
            @PathVariable("userId") Long userId,
            @RequestBody TenantDataStatisticsParam tenantDataStatisticsParam) {

        Map<String, Object> m = userService.getDataScopeById(userId);
        List<Long> orgIds = (List<Long>) m.get("orgIds");
        TenantStatisticsResult result = statisticsBizService.tenantDataStatisticsOrg(tenantDataStatisticsParam, orgIds);
        return R.success(result);

    }


    /**
     * 统计用的复诊率
     * 推送人数
     * 提交人数
     * 计算复诊率
     */
    @ApiOperation("医生端根据统计要求统计数据")
    @PostMapping("tenantDataStatistics/doctor/{userId}")
    public R<TenantStatisticsResult> doctorDataStatistics(
            @PathVariable("userId") Long userId,
            @RequestBody TenantDataStatisticsParam tenantDataStatisticsParam) {
        TenantStatisticsResult result = statisticsBizService.tenantDataStatisticsDoctor(tenantDataStatisticsParam, userId);
        return R.success(result);

    }

    /**
     * 统计用的复诊率
     * 推送人数
     * 提交人数
     * 计算复诊率
     */
    @ApiOperation("app根据统计要求统计数据")
    @PostMapping("tenantDataStatistics/nursing/{userId}")
    public R<TenantStatisticsResult> nursingDataStatistics(
            @PathVariable("userId") Long userId,
            @RequestBody TenantDataStatisticsParam tenantDataStatisticsParam) {

        TenantStatisticsResult result = statisticsBizService.tenantDataStatisticsNursing(tenantDataStatisticsParam, userId);
        return R.success(result);

    }
}
