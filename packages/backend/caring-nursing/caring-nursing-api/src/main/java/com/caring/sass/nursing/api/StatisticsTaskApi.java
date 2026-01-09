package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.hystrix.StatisticsTaskApiFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName StatisticsTaskApi
 * @Description
 * @Author yangShuai
 * @Date 2022/4/20 15:27
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = StatisticsTaskApiFallback.class
        , path = "/statisticsTask", qualifier = "StatisticsTaskApi")
public interface StatisticsTaskApi {


    @ApiOperation("创建项目时，初始化统计任务")
    @GetMapping("initTenantDefaultMaster")
    R<String> initTenantDefaultMaster();

    @ApiOperation("复制项目的统计")
    @GetMapping("copyTask")
    R<String> copyTask(@RequestParam("fromTenantCode") String fromTenantCode,
                       @RequestParam("toTenantCode") String toTenantCode);
}
