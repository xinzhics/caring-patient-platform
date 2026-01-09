package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.hystrix.FormApiFallback;
import com.caring.sass.nursing.api.hystrix.InformationIntegrityMonitoringApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName PlanApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:27
 * @Version 1.0
 **/
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = InformationIntegrityMonitoringApiFallback.class
        , path = "/informationIntegrityMonitoring", qualifier = "InformationIntegrityMonitoringApi")
public interface InformationIntegrityMonitoringApi {

    /**
     * 执行同步任务信息完整度的逻辑(指定患者触发)
     *
     * @return 实体
     */
    @GetMapping(value = "singleSynInformationIntegrityMonitoringTask/{tenantCode}")
    R singleSynInformationIntegrityMonitoringTask(@PathVariable("tenantCode") String tenantCode, @RequestParam("patientId") Long patientId);

}
