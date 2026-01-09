package com.caring.sass.ai.controller.ckd;

import com.caring.sass.ai.ckd.server.CkdCookbookPlanService;
import com.caring.sass.ai.entity.CookPlanMealType;
import com.caring.sass.ai.entity.ckd.CkdCookbookPlan;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 用户食谱计划
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdCookbookPlan")
@Api(value = "CkdCookbookPlan", tags = "用户食谱计划")
public class CkdCookbookPlanController {

    @Autowired
    CkdCookbookPlanService ckdCookbookPlanService;

    @ApiOperation("查询菜谱方案列表")
    @GetMapping()
    public R<List<CkdCookbookPlan>> findCookBookPlans(@RequestParam String openId) {
        List<CkdCookbookPlan> ckdCookbookPlans = ckdCookbookPlanService.findCookBookPlansInLastThreeDays(openId);
        return R.success(ckdCookbookPlans);
    }

    @ApiOperation("刷新某个食谱")
    @PostMapping(value = "/change_plans")
    public R<CkdCookbookPlan> changeCookBookplans(@RequestParam String openId,
                                            @RequestParam Long planId,
                                            @RequestParam CookPlanMealType type) {
        CkdCookbookPlan cookbookPlan = ckdCookbookPlanService.changeCookBookPlans(openId, planId, type);
        return R.success(cookbookPlan);
    }


}
