package com.caring.sass.nursing.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.InformationIntegrityMonitoringApi;
import org.springframework.stereotype.Component;

/**
 * @ClassName InformationIntegrityMonitoringApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:28
 * @Version 1.0
 */
@Component
public class InformationIntegrityMonitoringApiFallback implements InformationIntegrityMonitoringApi {

    @Override
    public R singleSynInformationIntegrityMonitoringTask(String tenantCode, Long patientId) {
        return R.timeout();
    }

}
