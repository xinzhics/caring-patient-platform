package com.caring.sass.authority.api;

import com.caring.sass.authority.entity.common.Hospital;
import com.caring.sass.base.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "${caring.feign.authority-server:caring-authority-server}"
        , path = "/hospital", qualifier = "HospitalApi")
public interface HospitalApi {
    @ApiOperation("获取医院基本信息")
    @GetMapping("getHospital/{id}")
    R<Hospital> getHospital(@PathVariable(name = "id") Long id);
}
