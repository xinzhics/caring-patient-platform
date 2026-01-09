package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.hystrix.FunctionConfigurationApiFallback;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName PlanApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:27
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = FunctionConfigurationApiFallback.class
        , path = "/functionConfiguration", qualifier = "FunctionConfigurationApi")
public interface FunctionConfigurationApi {

    @ApiOperation("获取功能配置的开关状态")
    @PutMapping("getFunctionStatus")
    R<Integer> getFunctionStatus(@RequestParam("tenantCode") String tenantCode,
                                        @RequestParam("functionTypeEnum") PlanFunctionTypeEnum functionTypeEnum);

}
