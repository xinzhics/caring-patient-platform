package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.nursing.api.hystrix.PlanApiFallback;
import com.caring.sass.nursing.dto.form.CopyPlanDTO;
import com.caring.sass.nursing.entity.plan.Plan;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PlanApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:27
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = PlanApiFallback.class
        , path = "/plan", qualifier = "PlanApi")
public interface PlanApi {

    @ApiOperation("根据类型获取护理计划")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "planType", value = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）", dataType = "long", paramType = "query")
    )
    @GetMapping("getPlanByType")
    R<Plan> getPlanByType(@RequestParam("planType") @NotNull Integer planType);

    @GetMapping("initPlan")
    R<Boolean> createProjectInitPlan();

    /**
     * 复制护理计划
     */
    @PostMapping("copyPlan")
    R<Map<Long, Long>> copyPlan(@RequestBody CopyPlanDTO copyPlanDTO);


    @ApiOperation("查询文件夹的分享链接被用在那个项目")
    @GetMapping({"/checkFolderShareUrlExist"})
    R<List<String>> checkFolderShareUrlExist(@RequestParam("url") String url);


    @ApiOperation("患者端监测计划列表")
    @GetMapping("/PatientMonitoringDataPlan")
    R<List<Plan>> getPatientMonitoringDataPlan();
}
