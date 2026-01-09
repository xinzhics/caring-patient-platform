package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.hystrix.PatientNursingPlanApiFallback;
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
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = PatientNursingPlanApiFallback.class
        , path = "/patientNursingPlan", qualifier = "PatientNursingPlanApi")
public interface PatientNursingPlanApi {

    @ApiOperation("患者入组后首次默认绑定全部随访")
    @PutMapping("patientFirstSubscribePlanAll")
    R<Boolean> patientFirstSubscribePlanAll(@RequestParam("patientId") Long patientId);

    @ApiOperation("查询要求注册完成时推送的护理计划。 并直接进行推送")
    @PutMapping("queryRegisterCompletePlanAndPush")
    R<Boolean> queryRegisterCompletePlanAndPush(@RequestParam("patientId") Long patientId);

}
