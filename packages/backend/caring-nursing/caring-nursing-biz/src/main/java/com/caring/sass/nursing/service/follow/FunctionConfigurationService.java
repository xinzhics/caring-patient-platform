package com.caring.sass.nursing.service.follow;

import com.caring.sass.nursing.constant.FollowBloodPressureOrSugarSetting;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dto.follow.FunctionConfigurationPushSettingsDTO;
import com.caring.sass.nursing.dto.follow.FunctionConfigurationUpdateDTO;
import com.caring.sass.nursing.dto.follow.PlanTemplateUpdateDTO;
import com.caring.sass.nursing.dto.follow.PushTemplateUpdateDTO;
import com.caring.sass.nursing.entity.follow.FunctionConfiguration;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsg;

import java.util.List;

public interface FunctionConfigurationService {


    /**
     * 查询血糖 血压的配置
     *
     * @return
     */
    List<FollowBloodPressureOrSugarSetting> queryBloodPressureOrBloodSugar(Long planId);




    /**
     * 查询项目的 功能配置
     * @param tenantCode
     * @return
     */
    List<FunctionConfiguration> findList(String tenantCode);


    /**
     * 获取项目 功能配置的状态
     * 适用于 微信模板推送之前， 获取功能配置开关状态
     * @param tenantCode
     * @param functionTypeEnum
     * @return 1 为开启， 0 为关闭
     */
    Integer getFunctionStatus(String tenantCode, PlanFunctionTypeEnum functionTypeEnum);

    /**
     * 更新功能配置的状态
     * 同步更新redis的状态
     * @param configurationUpdateStatusDTO
     */
    void updateFunctionStatus(FunctionConfigurationUpdateDTO configurationUpdateStatusDTO);

    /**
     * 查询用药管理，复查管理，健康日志，转诊管理的微信模板设置
     *
     * @param functionConfigurationId
     * @return
     */
    TemplateMsgDto queryTemplateMsg(Long functionConfigurationId);

    /**
     * 快捷修改用药管理，复查提醒，健康日志，转诊管理的微信模板ID
     * @param configurationUpdateStatusDTO
     */
    void updateWeiXinTemplateId(FunctionConfigurationUpdateDTO configurationUpdateStatusDTO);


    /**
     * 查询学习计划。
     * @param learnPlanRole 学习计划的角色
     * @return
     */
    List<Plan> queryLearnPlan(String learnPlanRole);

    /**
     * 修改 血压血糖 的 微信模板ID
     * @param tenantCode
     * @param planDetailTimeId
     * @param weiXinTemplateId
     * @return
     */
    TemplateMsg updateBloodPressureOrBloodSugar(String tenantCode, Long planDetailTimeId, String weiXinTemplateId);


    /**
     * 创建或者更新血压血糖的微信模板配置
     * @param tenantCode
     * @param planDetailTimeId
     * @param updateDTO
     */
    TemplateMsg updateBloodPressureOrBloodSugar(String tenantCode, Long planDetailTimeId, TemplateMsgUpdateDTO updateDTO);

    /**
     * 修改用药管理，复查提醒，健康日志，转诊管理的微信模板配置
     * @param tenantCode
     * @param functionConfigurationId
     * @param templateMsgUpdateDTO
     * @return
     */
    TemplateMsg updateWeiXinTemplateFields(String tenantCode, Long functionConfigurationId, TemplateMsgUpdateDTO templateMsgUpdateDTO);


    /**
     * 查询 学习计划的 微信模板配置
     * @param tenantCode
     * @param planId
     * @return
     */
    TemplateMsgDto queryPlanWeiXinTemplate(String tenantCode, Long planId);

    /**
     * 更新学习计划的 微信模板配置
     * @param tenantCode
     * @param learnPlanId
     * @param templateMsgUpdateDTO
     * @return
     */
    TemplateMsg updateWeiXinTemplateField(String tenantCode, Long learnPlanId, TemplateMsgUpdateDTO templateMsgUpdateDTO);

    /**
     * 初始化单个项目的 功能配置列表
     * @param tenantCode
     */
    List<FunctionConfiguration> initFunctionConfiguration(String tenantCode);

    /**
     * 初始化 系统中各项目的功能配置
     */
    void initFunctionConfiguration();

    /**
     * 复制功能配置
     * @param fromTenantCode
     * @param toTenantCode
     */
    void copyFunctionConfiguration(String fromTenantCode, String toTenantCode);

    /**
     * 查询 functionType 类型对应的 模板配置列表
     * @param functionType
     * @return
     */
    List<FunctionConfigurationPushSettingsDTO> queryFunctionConfigurationTempItem(String functionType);


    /**
     * 在线咨询，病例讨论，预约
     * 更新模板配置中的微信的模板ID
     *
     * @param pushTemplateUpdateDTO
     */
    void updatePushTemplate(PushTemplateUpdateDTO pushTemplateUpdateDTO);

    /**
     * 更新指标监测，自定义护理计划 的模板ID
     *
     * @param planTemplateUpdateDTO
     */
    void updatePlanTemplate(PlanTemplateUpdateDTO planTemplateUpdateDTO);

    /**
     * 获取 指标监测 自定义随访 中还开启的护理计划ID
     * @return
     */
    List<Long> getRemovePlanButOpenId();

}
