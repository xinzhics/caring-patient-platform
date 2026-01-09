package com.caring.sass.tenant.api;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.hystrix.H5CoreFunctionApiFallback;
import com.caring.sass.tenant.api.hystrix.H5RouterApiFallback;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${caring.feign.authority-server:caring-tenant-server}", fallback = H5CoreFunctionApiFallback.class
        , path = "/h5CoreFunctions", qualifier = "H5CoreFunctionApi")
public interface H5CoreFunctionApi {

    @GetMapping("findOneByCode")
    @ApiOperation("查询项目患者核心功能配置")
    R<H5CoreFunctions> findOneByCode();

}
