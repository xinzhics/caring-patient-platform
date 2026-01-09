package com.caring.sass.nursing.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.StatisticsTaskApi;
import org.springframework.stereotype.Component;

/**
 * @ClassName PlanApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:28
 * @Version 1.0
 */
@Component
public class StatisticsTaskApiFallback implements StatisticsTaskApi {

    @Override
    public R<String> initTenantDefaultMaster() {
        return R.timeout();
    }

    @Override
    public R<String> copyTask(String fromTenantCode, String toTenantCode) {
        return R.timeout();
    }
}
