package com.caring.sass.nursing.controller.plan;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.entity.plan.PlanDetail;
import com.caring.sass.nursing.dto.plan.PlanDetailSaveDTO;
import com.caring.sass.nursing.dto.plan.PlanDetailUpdateDTO;
import com.caring.sass.nursing.dto.plan.PlanDetailPageDTO;
import com.caring.sass.nursing.service.plan.PlanDetailService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;


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
@RequestMapping("/planDetail")
@Api(value = "PlanDetail", tags = "护理计划详情")
@PreAuth(replace = "planDetail:")
public class PlanDetailController extends SuperController<PlanDetailService, Long, PlanDetail, PlanDetailPageDTO, PlanDetailSaveDTO, PlanDetailUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<PlanDetail> planDetailList = list.stream().map((map) -> {
            PlanDetail planDetail = PlanDetail.builder().build();
            //TODO 请在这里完成转换
            return planDetail;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(planDetailList));
    }


    @ApiOperation("删除护理计划一个推送详情")
    @DeleteMapping("deletePLanDetail/{tenantCode}/{id}")
    public R<Boolean> deletePLanDetail(@PathVariable("tenantCode") String tenantCode, @PathVariable("id") Long id) {
        BaseContextHandler.setTenant(tenantCode);
        ArrayList<Long> list = new ArrayList<>();
        list.add(id);
        baseService.removeByIds(list);
        return R.success();
    }


    @ApiOperation("获取护理计划下的推送详情")
    @GetMapping("getPlanDetail/{planId}")
    public R<PlanDetail> getPlanDetail(@PathVariable("planId") Long planId) {
        PlanDetail detail = baseService.getOne(Wraps.<PlanDetail>lbQ().eq(PlanDetail::getNursingPlanId, planId).last("limit 0,1"));
        if (Objects.nonNull(detail)) {
            return R.success(detail);
        }
        return R.success(null);
    }


}
