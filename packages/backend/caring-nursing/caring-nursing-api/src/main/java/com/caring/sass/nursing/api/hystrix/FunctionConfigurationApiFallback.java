package com.caring.sass.nursing.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.FunctionConfigurationApi;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 功能配置的api
 */
@Component
public class FunctionConfigurationApiFallback implements FunctionConfigurationApi {

    @Override
    public R<Integer> getFunctionStatus(String tenantCode, PlanFunctionTypeEnum functionTypeEnum) {
        return R.success(1);
    }
}
