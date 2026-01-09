package com.caring.sass.nursing.controller.plan;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;
import com.caring.sass.nursing.dto.plan.PlanDetailTimeSaveDTO;
import com.caring.sass.nursing.dto.plan.PlanDetailTimeUpdateDTO;
import com.caring.sass.nursing.dto.plan.PlanDetailTimePageDTO;
import com.caring.sass.nursing.service.plan.PlanDetailTimeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 护理计划详情
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/planDetailTime")
@Api(value = "PlanDetailTime", tags = "护理计划详情")
@PreAuth(replace = "planDetailTime:")
public class PlanDetailTimeController extends SuperController<PlanDetailTimeService, Long, PlanDetailTime, PlanDetailTimePageDTO, PlanDetailTimeSaveDTO, PlanDetailTimeUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<PlanDetailTime> planDetailTimeList = list.stream().map((map) -> {
            PlanDetailTime planDetailTime = PlanDetailTime.builder().build();
            //TODO 请在这里完成转换
            return planDetailTime;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(planDetailTimeList));
    }

    @ApiOperation("删除护理计划一个推送时间设置")
    @DeleteMapping("deletePLanDetailTime/{tenantCode}/{id}")
    public R<Boolean> deletePLanDetailTime(@PathVariable("tenantCode") String tenantCode, @PathVariable("id") Long id) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.removeById(id);
        return R.success();
    }

}
