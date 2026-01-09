package com.caring.sass.tenant.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.AppConfigApi;
import com.caring.sass.tenant.entity.AppConfig;
import org.springframework.stereotype.Component;

@Component
public class AppConfigApiFallback implements AppConfigApi {
    @Override
    public R<AppConfig> getByTenant() {
        return R.timeout();
    }

    @Override
    public R<String> getAppointmentDoctorScope(String tenantCode) {
        return R.timeout();
    }
}
