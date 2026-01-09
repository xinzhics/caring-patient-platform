package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.hystrix.AppointmentApiFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @className: AppointmentApi
 * @author: 杨帅
 * @date: 2024/1/8
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = AppointmentApiFallback.class
        , path = "/appointment", qualifier = "AppointmentApi")
public interface AppointmentApi {

    @GetMapping("doctor/statistics/approvalNumber")
    @ApiOperation("【精准预约】 医生统计待审核预约数量")
    R<Integer> doctorStatisticsApprovalNumber(@RequestParam(name = "doctorId") Long doctorId);


}
