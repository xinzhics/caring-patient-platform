package com.caring.sass.nursing.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.PatientNursingPlanApi;
import org.springframework.stereotype.Component;

/**
 * @ClassName PlanApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:28
 * @Version 1.0
 */
@Component
public class PatientNursingPlanApiFallback implements PatientNursingPlanApi {

    @Override
    public R<Boolean> patientFirstSubscribePlanAll(Long patientId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> queryRegisterCompletePlanAndPush(Long patientId) {
        return R.timeout();
    }
}
