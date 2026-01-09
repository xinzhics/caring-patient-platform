package com.caring.sass.tenant.api;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.hystrix.AppConfigApiFallback;
import com.caring.sass.tenant.entity.AppConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${caring.feign.authority-server:caring-tenant-server}", fallback = AppConfigApiFallback.class
        , path = "/appConfig", qualifier = "appConfigApi")
public interface AppConfigApi {


    @GetMapping("getByTenant")
    R<AppConfig> getByTenant();


    @ApiOperation("获取app配置医生二维码")
    @GetMapping("getAppointmentDoctorScope/{tenantCode}")
    R<String> getAppointmentDoctorScope(@PathVariable("tenantCode") String tenantCode);

}
