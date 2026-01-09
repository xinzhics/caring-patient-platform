package com.caring.sass.nursing.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.AppointmentApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @className: AppointmentApi
 * @author: 杨帅
 * @date: 2024/1/8
 */
@Component
public class AppointmentApiFallback implements AppointmentApi {
    @Override
    public R<Integer> doctorStatisticsApprovalNumber(Long doctorId) {
        return R.timeout();
    }
}
