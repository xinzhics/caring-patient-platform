package com.caring.sass.nursing.api.hystrix;

import com.caring.sass.nursing.api.PlanApi;
import com.caring.sass.base.R;
import com.caring.sass.nursing.dto.form.CopyPlanDTO;
import com.caring.sass.nursing.entity.plan.Plan;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PlanApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:28
 * @Version 1.0
 */
@Component
public class PlanApiFallback implements PlanApi {


    @Override
    public R<Plan> getPlanByType(@NotNull Integer planType) {
        return R.timeout();
    }

    @Override
    public R<Boolean> createProjectInitPlan() {
        return R.timeout();
    }

    @Override
    public R<Map<Long, Long>> copyPlan(CopyPlanDTO copyPlanDTO) {
        return R.timeout();
    }

    @Override
    public R<List<String>> checkFolderShareUrlExist(String url) {
        return R.timeout();
    }

    @Override
    public R<List<Plan>> getPatientMonitoringDataPlan() {
        return R.timeout();
    }
}
