package com.caring.sass.nursing.controller.follow;

import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.constant.FollowBloodPressureOrSugarSetting;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dto.follow.FunctionConfigurationPushSettingsDTO;
import com.caring.sass.nursing.dto.follow.FunctionConfigurationUpdateDTO;
import com.caring.sass.nursing.dto.follow.PlanTemplateUpdateDTO;
import com.caring.sass.nursing.dto.follow.PushTemplateUpdateDTO;
import com.caring.sass.nursing.entity.follow.FunctionConfiguration;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/functionConfiguration")
@Api(value = "FunctionConfiguration", tags = "功能配置")
public class FunctionConfigurationController {

    @Autowired
    FunctionConfigurationService functionConfigurationService;

    @ApiOperation("查询血压或者血糖的推送设置")
    @GetMapping("queryBloodPressureOrBloodSugar/setup")
    public R<List<FollowBloodPressureOrSugarSetting>> queryBloodPressureOrBloodSugar(@RequestParam String tenantCode, @RequestParam Long planId) {

        BaseContextHandler.setTenant(tenantCode);
        List<FollowBloodPressureOrSugarSetting>  settings = functionConfigurationService.queryBloodPressureOrBloodSugar(planId);

        return R.success(settings);
    }

    @ApiOperation("更新血压血糖的微信模板ID")
    @GetMapping("updateBloodPressureOrSugarTemplateId")
    public R<TemplateMsg> updateBloodPressureOrSugarTemplateFields(@RequestParam String tenantCode,
                                                            @RequestParam Long planDetailTimeId,
                                                            @RequestParam String weiXinTemplateId) {

        TemplateMsg templateMsg = functionConfigurationService.updateBloodPressureOrBloodSugar(tenantCode, planDetailTimeId, weiXinTemplateId);
        return R.success(templateMsg);
    }


    @ApiOperation("更新血压血糖的微信模板配置，返回模板ID")
    @PutMapping("updateBloodPressureOrSugarTemplateFields")
    public R<TemplateMsg> updateBloodPressureOrSugarTemplateFields(@RequestParam String tenantCode,
                                                            @RequestParam Long planDetailTimeId,
                                                            @RequestBody TemplateMsgUpdateDTO templateMsgUpdateDTO) {

        TemplateMsg templateMsg = functionConfigurationService.updateBloodPressureOrBloodSugar(tenantCode, planDetailTimeId, templateMsgUpdateDTO);
        return R.success(templateMsg);
    }


    @ApiOperation("查询项目的功能配置")
    @GetMapping("findList")
    public R<List<FunctionConfiguration>> findList(@RequestParam String tenantCode) {
        List<FunctionConfiguration> functionConfigurationList = functionConfigurationService.findList(tenantCode);
        return R.success(functionConfigurationList);
    }

    @ApiOperation("更新功能配置的状态")
    @PutMapping("updateFunctionStatus")
    public R<Void> updateFunctionStatus(@RequestBody FunctionConfigurationUpdateDTO configurationUpdateStatusDTO) {

        functionConfigurationService.updateFunctionStatus(configurationUpdateStatusDTO);
        return R.success(null);
    }

    @ApiOperation("快捷修改用药管理，复查提醒，健康日志，转诊管理的微信模板ID")
    @PutMapping("updateWeiXinTemplateId")
    public R<Void> updateWeiXinTemplateId(@RequestBody FunctionConfigurationUpdateDTO configurationUpdateStatusDTO) {
        functionConfigurationService.updateWeiXinTemplateId(configurationUpdateStatusDTO);
        return R.success(null);
    }

    @ApiOperation("查询用药管理，复查管理，健康日志，转诊管理的微信模板设置")
    @GetMapping("queryTemplateMsg/{tenantCode}/{functionConfigurationId}")
    public R<TemplateMsgDto> queryTemplateMsg(@PathVariable("tenantCode") String tenantCode, @PathVariable("functionConfigurationId") Long functionConfigurationId) {
        BaseContextHandler.setTenant(tenantCode);
        TemplateMsgDto templateMsgDto = functionConfigurationService.queryTemplateMsg(functionConfigurationId);
        return R.success(templateMsgDto);
    }

    @ApiOperation("修改用药管理，复查提醒，健康日志，转诊管理的微信模板配置")
    @PutMapping("updateWeiXinTemplateFields/{tenantCode}/{functionConfigurationId}")
    public R<TemplateMsg> updateWeiXinTemplateFields(@PathVariable("tenantCode") String tenantCode,
                                              @PathVariable("functionConfigurationId") Long functionConfigurationId,
                                              @RequestBody TemplateMsgUpdateDTO templateMsgUpdateDTO) {
        TemplateMsg templateMsg = functionConfigurationService.updateWeiXinTemplateFields(tenantCode, functionConfigurationId, templateMsgUpdateDTO);
        return R.success(templateMsg);
    }

    @ApiOperation("按角色查询学习计划")
    @GetMapping("queryLearnPlan/{tenantCode}/{learnPlanRole}")
    public R<List<Plan>> queryLearnPlan(@PathVariable("tenantCode") String tenantCode, @PathVariable("learnPlanRole") String learnPlanRole) {
        BaseContextHandler.setTenant(tenantCode);
        List<Plan> planList = functionConfigurationService.queryLearnPlan(learnPlanRole);
        return R.success(planList);
    }

    @ApiOperation("查询计划的微信模板配置")
    @GetMapping("queryPlanWeiXinTemplate/{tenantCode}/{planId}")
    public R<TemplateMsgDto> queryPlanWeiXinTemplate(@PathVariable("tenantCode") String tenantCode,
                                                          @PathVariable("planId") Long planId) {

        TemplateMsgDto templateMsgDto = functionConfigurationService.queryPlanWeiXinTemplate(tenantCode, planId);
        return R.success(templateMsgDto);
    }


    @ApiOperation("更新单个计划(学习计划，指标监测，自定义护理计划) 的模板配置")
    @PutMapping("updateWeiXinTemplateField/{tenantCode}/{planId}")
    public R<TemplateMsg> updateWeiXinTemplateField(@PathVariable("tenantCode") String tenantCode,
                                                            @PathVariable("planId") Long planId,
                                                            @RequestBody TemplateMsgUpdateDTO templateMsgUpdateDTO) {
        BaseContextHandler.setTenant(tenantCode);
        TemplateMsg templateMsg = functionConfigurationService.updateWeiXinTemplateField(tenantCode, planId, templateMsgUpdateDTO);
        return R.success(templateMsg);
    }


    @ApiOperation("查询病例讨论，预约，在线咨询的推送列表")
    @GetMapping("queryFunctionConfigurationTempItem/{tenantCode}/{functionTypeCode}")
    public R<List<FunctionConfigurationPushSettingsDTO>> queryFunctionConfigurationTempItem(@PathVariable String tenantCode,
                                                                                            @PathVariable String functionTypeCode) {
        BaseContextHandler.setTenant(tenantCode);
        List<FunctionConfigurationPushSettingsDTO> functionConfigurationPushSettingsDTOS = functionConfigurationService.queryFunctionConfigurationTempItem(functionTypeCode);
        return R.success(functionConfigurationPushSettingsDTOS);
    }


    @ApiOperation("快捷修改在线咨询，预约，病例讨论的微信模板ID")
    @PutMapping("updatePushTemplate")
    public R<Void> updatePushTemplate(@RequestBody PushTemplateUpdateDTO pushTemplateUpdateDTO) {
        functionConfigurationService.updatePushTemplate(pushTemplateUpdateDTO);
        return R.success(null);
    }

    @ApiOperation("快捷修改指标监测或自定义护理计划的微信模板ID")
    @PutMapping("updatePlanTemplate")
    public R<Void> updatePlanTemplate(@RequestBody PlanTemplateUpdateDTO planTemplateUpdateDTO) {
        functionConfigurationService.updatePlanTemplate(planTemplateUpdateDTO);
        return R.success(null);
    }

    @ApiOperation("获取功能配置的开关状态")
    @PutMapping("getFunctionStatus")
    public R<Integer> getFunctionStatus(@RequestParam("tenantCode") String tenantCode,
                                        @RequestParam("functionTypeEnum") PlanFunctionTypeEnum functionTypeEnum) {

        Integer status = functionConfigurationService.getFunctionStatus(tenantCode, functionTypeEnum);
        return R.success(status);
    }


}
