package com.caring.sass.nursing.service.follow.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.FollowBloodPressureOrSugarSetting;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.follow.FunctionConfigurationMapper;
import com.caring.sass.nursing.dao.plan.PlanDetailMapper;
import com.caring.sass.nursing.dao.plan.PlanDetailTimeMapper;
import com.caring.sass.nursing.dto.follow.FunctionConfigurationPushSettingsDTO;
import com.caring.sass.nursing.dto.follow.FunctionConfigurationUpdateDTO;
import com.caring.sass.nursing.dto.follow.PlanTemplateUpdateDTO;
import com.caring.sass.nursing.dto.follow.PushTemplateUpdateDTO;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.entity.follow.FunctionConfiguration;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.plan.PlanDetail;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;
import com.caring.sass.nursing.enumeration.FollowUpPlanTypeEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.follow.FollowTaskContentService;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsUpdateDTO;
import com.caring.sass.wx.dto.template.TemplateMsgUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsg;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能配置相关的业务接口。
 * 护理计划，监测计划，模板配置等聚合业务
 */
@Slf4j
@Service
public class FunctionConfigurationServiceImpl implements FunctionConfigurationService {

    @Autowired
    PlanService planService;

    @Autowired
    PlanDetailTimeMapper planDetailTimeMapper;

    @Autowired
    PlanDetailMapper planDetailMapper;

    @Autowired
    TemplateMsgApi templateMsgApi;

    @Autowired
    H5RouterApi h5RouterApi;

    @Autowired
    FunctionConfigurationMapper baseMapper;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 缓存项目的功能配置开关状态到redis
     */
    private static String FUNCTION_CONFIGURATION_REDIS_HASH_KEY = "FUNCTION_CONFIGURATION";

    /**
     * 查询 血压或者血糖的 推送配置
     * @param planId
     * @return
     */
    @Override
    public List<FollowBloodPressureOrSugarSetting> queryBloodPressureOrBloodSugar(Long planId) {

        String tenant = BaseContextHandler.getTenant();
        Plan plan = planService.getPlanAndDetails(planId);
        List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
        if (CollUtil.isEmpty(planDetailList)) {
            return new ArrayList<>();
        }
        List<FollowBloodPressureOrSugarSetting> settings = new ArrayList<>();
        for (PlanDetailDTO planDetailDTO : planDetailList) {
            Integer type = planDetailDTO.getType();
            if (type != null && type == 1) {
                List<PlanDetailTime> planDetailTimes = planDetailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailList)) {
                    continue;
                }
                for (int i = 0; i < planDetailTimes.size(); i++) {
                    PlanDetailTime detailTime = planDetailTimes.get(i);
                    String templateMessageId = detailTime.getTemplateMessageId();
                    Long detailTimeId = detailTime.getId();
                    FollowBloodPressureOrSugarSetting followBloodPressureOrSugarSetting = new FollowBloodPressureOrSugarSetting();
                    followBloodPressureOrSugarSetting.setPlanId(planId);
                    followBloodPressureOrSugarSetting.setPlanDetailTimeId(detailTimeId);
                    String pushName = getPushName(plan.getPlanType(), i);
                    followBloodPressureOrSugarSetting.setPushName(pushName);
                    if (StringUtils.isNotEmptyString(templateMessageId)) {
                        long messageId = Long.parseLong(templateMessageId);
                        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getTenantOneById(tenant, messageId);
                        TemplateMsgDto data = templateMsgDtoR.getData();
                        if (templateMsgDtoR.getIsSuccess() && Objects.nonNull(data)) {
                            String weiXinTemplateId = data.getTemplateId();
                            followBloodPressureOrSugarSetting.setTemplateId(messageId);
                            followBloodPressureOrSugarSetting.setWeiXinTemplateId(weiXinTemplateId);
                        }
                    }
                    settings.add(followBloodPressureOrSugarSetting);
                }
            }
        }
        return settings;
    }

    /**
     * 创建或者更新血压血糖的 模板配置
     * @param tenantCode
     * @param planDetailTimeId
     * @param updateDTO
     * @return
     */
    @Override
    public TemplateMsg updateBloodPressureOrBloodSugar(String tenantCode, Long planDetailTimeId, TemplateMsgUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(tenantCode);
        PlanDetailTime planDetailTime = planDetailTimeMapper.selectById(planDetailTimeId);
        String templateMessageId = planDetailTime.getTemplateMessageId();
        R<TemplateMsg> templateMsgR = templateMsgApi.updateTemplateMsgAndFields(updateDTO, tenantCode);
        TemplateMsg msgRData = null;
        if (templateMsgR.getIsSuccess() && templateMsgR.getData() != null) {
            msgRData = templateMsgR.getData();
        } else {
            throw new BizException("更新微信模板失败");
        }
        if (msgRData != null && StringUtils.isEmpty(templateMessageId)) {
            planDetailTime.setTemplateMessageId(msgRData.getId().toString());
            planDetailTimeMapper.updateById(planDetailTime);
        }
        return msgRData;

    }


    /**
     * 更新 用药， 复查提醒，健康日志， 转诊管理 的微信模板配置
     * @param tenantCode
     * @param functionConfigurationId
     * @param templateMsgUpdateDTO
     * @return
     */
    @Override
    public TemplateMsg updateWeiXinTemplateFields(String tenantCode, Long functionConfigurationId, TemplateMsgUpdateDTO templateMsgUpdateDTO) {

        BaseContextHandler.setTenant(tenantCode);
        FunctionConfiguration configuration = baseMapper.selectById(functionConfigurationId);
        PlanFunctionTypeEnum functionType = configuration.getFunctionType();
        if (PlanFunctionTypeEnum.MEDICATION.eq(functionType)) {
            templateMsgUpdateDTO.setIndefiner(TemplateMessageIndefiner.MEDICINE_TAKE_ALERT_TEMPLATE_MESSAGE);
        }
        if (PlanFunctionTypeEnum.REFERRAL_SERVICE.eq(functionType)) {
            templateMsgUpdateDTO.setIndefiner(TemplateMessageIndefiner.REFERRAL_CARD);
        }
        if (PlanFunctionTypeEnum.HEALTH_LOG.eq(functionType) || PlanFunctionTypeEnum.REVIEW_MANAGE.eq(functionType)) {
            templateMsgUpdateDTO.setIndefiner(TemplateMessageIndefiner.NURSING_PLAN);
        }
        R<TemplateMsg> templateMsgR = templateMsgApi.updateTemplateMsgAndFields(templateMsgUpdateDTO, tenantCode);
        if (templateMsgR.getIsSuccess() && templateMsgR.getData() != null) {
            TemplateMsg msgRData = templateMsgR.getData();
            configuration.setCaringTemplateId(msgRData.getId());
            configuration.setWeiXinTemplateId(msgRData.getTemplateId());
            baseMapper.updateById(configuration);
            if (PlanFunctionTypeEnum.REVIEW_MANAGE.eq(functionType) || PlanFunctionTypeEnum.HEALTH_LOG.eq(functionType)) {
                // 查询复查计划，健康日志, 更新计划中的微信模板ID
                updatePlanDetailTemplateId(configuration.getPlanId(),  configuration.getCaringTemplateId());
            }
            return msgRData;
        } else {
            throw new BizException("");
        }
    }

    /**
     * 创建或者更新 血压血糖 微信模板 中的 模板ID
     * @param tenantCode
     * @param planDetailTimeId
     * @param weiXinTemplateId
     * @return
     */
    @Override
    public TemplateMsg updateBloodPressureOrBloodSugar(String tenantCode, Long planDetailTimeId, String weiXinTemplateId) {
        BaseContextHandler.setTenant(tenantCode);
        PlanDetailTime planDetailTime = planDetailTimeMapper.selectById(planDetailTimeId);
        String templateMessageId = planDetailTime.getTemplateMessageId();
        if (StringUtils.isEmpty(templateMessageId)) {
            // 说明之前没有关联 sass 的模板
            // 创建一个新的模板。并将模板和计划设置关联上
            TemplateMsgUpdateDTO templateMsgUpdateDTO = new TemplateMsgUpdateDTO();
            templateMsgUpdateDTO.setFields(new ArrayList<>());
            templateMsgUpdateDTO.setBusinessId(planDetailTimeId.toString());
            templateMsgUpdateDTO.setTemplateId(weiXinTemplateId);
            R<TemplateMsg> templateMsgR = templateMsgApi.updateTemplateMsgAndFields(templateMsgUpdateDTO, tenantCode);
            TemplateMsg data = templateMsgR.getData();
            if (templateMsgR.getIsSuccess() && data != null) {
                Long id = data.getId();
                planDetailTime.setTemplateMessageId(id.toString());
                planDetailTimeMapper.updateById(planDetailTime);
                return data;
            } else {
                throw new BizException("创建微信模板失败");
            }
        } else {
            R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getTenantOneById(tenantCode, Long.parseLong(templateMessageId));
            if (templateMsgDtoR.getIsSuccess()) {
                TemplateMsgDto dtoRData = templateMsgDtoR.getData();
                TemplateMsgUpdateDTO updateDTO = initTemplateMsgUpdateDTO(dtoRData, weiXinTemplateId);
                R<TemplateMsg> templateMsgR = templateMsgApi.updateTemplateMsgAndFields(updateDTO, tenantCode);
                if (templateMsgR.getIsSuccess()) {
                    return templateMsgR.getData();
                } else {
                    throw new BizException("更新微信模板失败");
                }
            } else {
                throw new BizException("查看微信模板失败");
            }
        }
    }


    /**
     * 查询学习计划的微信模板配置
     * @param tenantCode
     * @param planId
     * @return
     */
    @Override
    public TemplateMsgDto queryPlanWeiXinTemplate(String tenantCode, Long planId) {
        BaseContextHandler.setTenant(tenantCode);
        Plan planAndDetails = planService.getPlanAndDetails(planId);
        List<PlanDetailDTO> planDetailList = planAndDetails.getPlanDetailList();
        if (CollUtil.isEmpty(planDetailList)) {
            // 学习计划新建的，还没生产推送配置。这里给初始化一个
            PlanDetail planDetail = new PlanDetail();
            planDetail.setNursingPlanId(planId);
            planDetail.setType(1);
            planDetailMapper.insert(planDetail);
            // 微信模板自然没有关联。所以返回空数据
            return null;
        } else {
            PlanDetailDTO detailDTO = planDetailList.get(0);
            List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
            if (CollUtil.isEmpty(planDetailTimes)) {
                PlanDetailTime planDetailTime = new PlanDetailTime();
                planDetailTime.setNursingPlanDetailId(detailDTO.getId());
                planDetailTimeMapper.insert(planDetailTime);
                return null;
            } else {
                String templateMessageId;
                if (planAndDetails.getPlanType() != null && PlanEnum.LEARN_PLAN.getCode().equals(planAndDetails.getPlanType())) {
                    templateMessageId = detailDTO.getTemplateMessageId();
                } else {
                    // 除了学习计划。其他的计划模板都在这里关联
                    PlanDetailTime detailTime = planDetailTimes.get(0);
                    templateMessageId = detailTime.getTemplateMessageId();
                }
                if (StrUtil.isNotEmpty(templateMessageId)) {
                    try {
                        long parseLong = Long.parseLong(templateMessageId);
                        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getTenantOneById(tenantCode, parseLong);
                        if (templateMsgDtoR.getIsSuccess()) {
                            return templateMsgDtoR.getData();
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
            }

        }
        return null;
    }

    /**
     * 更新学习计划的 微信配置
     * @param tenantCode
     * @param planId
     * @param templateMsgUpdateDTO
     * @return
     */
    @Override
    public TemplateMsg updateWeiXinTemplateField(String tenantCode, Long planId, TemplateMsgUpdateDTO templateMsgUpdateDTO) {

        BaseContextHandler.setTenant(tenantCode);
        R<TemplateMsg> templateMsgR = templateMsgApi.updateTemplateMsgAndFields(templateMsgUpdateDTO, tenantCode);
        TemplateMsg msgRData;
        if (templateMsgR.getIsSuccess() && templateMsgR.getData() != null) {
            msgRData = templateMsgR.getData();
        } else {
            throw new BizException("更新微信模板配置失败");
        }

        // 更新 完微信模板后。处理护理计划中的模板ID
        updatePlanDetailTemplateId(planId,  msgRData.getId());

        return msgRData;
    }

    /**
     * 获取血压血糖每次推送的名称
     * @param planType
     * @param index
     * @return
     */
    private static String getPushName(Integer planType, int index) {
        if (PlanEnum.BLOOD_PRESSURE.getCode().equals(planType)) {
            if (index == 0) {
                return "血压测量提醒(第一次)";
            } else if (index == 1) {
                return "血压测量提醒(第二次)";
            }
        } else if (PlanEnum.BLOOD_SUGAR.getCode().equals(planType)) {
            if (index == 0) {
                return "第一次血糖测量提醒(06:30)";
            } else if (index == 1) {
                return "第二次血糖测量提醒(09:00)";
            } else if (index == 2) {
                return "第三次血糖测量提醒(11:30)";
            } else if (index == 3) {
                return "第四次血糖测量提醒(14:00)";
            } else if (index == 4) {
                return "第五次血糖测量提醒(17:30)";
            } else if (index == 5) {
                return "第六次血糖测量提醒(20:00)";
            } else if (index == 6) {
                return "第七次血糖测量提醒(22:00)";
            }
        }
        return null;
    }


    @Override
    public List<FunctionConfigurationPushSettingsDTO> queryFunctionConfigurationTempItem(String functionType) {
        if (StrUtil.isEmpty(functionType)) {
            return new ArrayList<>();
        }
        // 预约管理
        // 预约提醒通知
        // 预约修改通知
        List<String> indefinerList = new ArrayList<>();
        Map<String, FunctionConfigurationPushSettingsDTO> settingsDTOMap = new HashMap<>();
        List<FunctionConfigurationPushSettingsDTO> list = new ArrayList<>();
        FunctionConfigurationPushSettingsDTO build;
        if (PlanFunctionTypeEnum.BOOKING_MANAGEMENT.getCode().equals(functionType)) {
            indefinerList.add(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE);
            indefinerList.add(TemplateMessageIndefiner.RESERVATION_UPDATE_TEMPLATE_MESSAGE);
            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("预约结果通知")
                    .templateMessageIndefiner(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE, build);
            list.add(build);

            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("待审核预约")
                    .templateMessageIndefiner(TemplateMessageIndefiner.RESERVATION_UPDATE_TEMPLATE_MESSAGE)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.RESERVATION_UPDATE_TEMPLATE_MESSAGE, build);
            list.add(build);

        }
        // 病例讨论
        // 病例讨论开始通知
        // 病例讨论消息通知
        // 病例讨论结束通知
        if (PlanFunctionTypeEnum.CASE_DISCUSSION.getCode().equals(functionType)) {

            indefinerList.add(TemplateMessageIndefiner.CONSULTATION_NOTICE);
            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("病例讨论开始通知")
                    .templateMessageIndefiner(TemplateMessageIndefiner.CONSULTATION_NOTICE)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.CONSULTATION_NOTICE, build);
            list.add(build);

            indefinerList.add(TemplateMessageIndefiner.CONSULTATION_PROCESSING);
            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("病例讨论消息通知")
                    .templateMessageIndefiner(TemplateMessageIndefiner.CONSULTATION_PROCESSING)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.CONSULTATION_PROCESSING, build);
            list.add(build);

            indefinerList.add(TemplateMessageIndefiner.CONSULTATION_END);
            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("病例讨论结束通知")
                    .templateMessageIndefiner(TemplateMessageIndefiner.CONSULTATION_END)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.CONSULTATION_END, build);
            list.add(build);
        }
        // 在线咨询
        // 咨询回复
        // 聊天提醒(患者)
        // 聊天提醒(医助)
        if (PlanFunctionTypeEnum.ONLINE_CONSULTATION.getCode().equals(functionType)) {
            indefinerList.add(TemplateMessageIndefiner.CONSULTATION_RESPONSE);
            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("咨询回复")
                    .templateMessageIndefiner(TemplateMessageIndefiner.CONSULTATION_RESPONSE)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.CONSULTATION_RESPONSE, build);
            list.add(build);

            indefinerList.add(TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT);
            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("聊天提醒（患者）")
                    .templateMessageIndefiner(TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT, build);
            list.add(build);

            indefinerList.add(TemplateMessageIndefiner.CONSULTATION_RESPONSE_NURSING);
            build = FunctionConfigurationPushSettingsDTO.builder()
                    .name("聊天提醒（医助）")
                    .templateMessageIndefiner(TemplateMessageIndefiner.CONSULTATION_RESPONSE_NURSING)
                    .build();
            settingsDTOMap.put(TemplateMessageIndefiner.CONSULTATION_RESPONSE_NURSING, build);
            list.add(build);
        }

        if (CollUtil.isEmpty(indefinerList)) {
            return new ArrayList<>();
        }
        String tenant = BaseContextHandler.getTenant();
        R<List<TemplateMsg>> msgApiByIndefinerList = templateMsgApi.findByIndefinerList(tenant, indefinerList);
        if (msgApiByIndefinerList.getIsSuccess()) {
            List<TemplateMsg> templateMsgs = msgApiByIndefinerList.getData();
            if (CollUtil.isNotEmpty(templateMsgs)) {
                for (TemplateMsg templateMsg : templateMsgs) {
                    String indefiner = templateMsg.getIndefiner();
                    if (StrUtil.isNotEmpty(indefiner)) {
                        FunctionConfigurationPushSettingsDTO pushSettingsDTO = settingsDTOMap.get(indefiner);
                        if (pushSettingsDTO != null) {
                            pushSettingsDTO.setCaringTemplateId(templateMsg.getId());
                            pushSettingsDTO.setWeiXinTemplateId(templateMsg.getTemplateId());
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查询 项目的功能配置
     * @param tenantCode
     * @return
     */
    @Override
    public List<FunctionConfiguration> findList(String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        return baseMapper.selectList(Wraps.lbQ());

    }



    /**
     * 查询用药管理，复查管理，健康日志，转诊管理的微信模板设置
     *
     * @param functionConfigurationId
     * @return
     */
    @Override
    public TemplateMsgDto queryTemplateMsg(Long functionConfigurationId) {
        FunctionConfiguration configuration = baseMapper.selectById(functionConfigurationId);
        PlanFunctionTypeEnum functionType = configuration.getFunctionType();
        String tenant = BaseContextHandler.getTenant();
        R<TemplateMsgDto> templateMsgDtoR = null;
        // 用药
        // 健康日志
        // 复查提醒
        // 转诊管理
        if (PlanFunctionTypeEnum.HEALTH_LOG.eq(functionType)
                || PlanFunctionTypeEnum.REVIEW_MANAGE.eq(functionType)
                || PlanFunctionTypeEnum.REFERRAL_SERVICE.eq(functionType)
                || PlanFunctionTypeEnum.MEDICATION.eq(functionType)) {
            if (Objects.nonNull(configuration.getCaringTemplateId())) {
                templateMsgDtoR = templateMsgApi.getTenantOneById(tenant, configuration.getCaringTemplateId());
            }
        }
        if (templateMsgDtoR != null && templateMsgDtoR.getIsSuccess()) {
            return templateMsgDtoR.getData();
        }
        return null;
    }

    /**
     * 获取项目 功能配置的状态
     * @param tenantCode
     * @param functionTypeEnum
     * @return
     */
    @Override
    public Integer getFunctionStatus(String tenantCode, PlanFunctionTypeEnum functionTypeEnum) {

        // 状态默认为开启
        int status = 1;
        BaseContextHandler.setTenant(tenantCode);
        BoundHashOperations<String, Object, Object> boundHashOperations = redisTemplate.boundHashOps(FUNCTION_CONFIGURATION_REDIS_HASH_KEY);
        String redisHashFieldName = getRedisHashFieldName(tenantCode, functionTypeEnum);
        Object o = boundHashOperations.get(redisHashFieldName);
        if (Objects.isNull(o)) {
            FunctionConfiguration configuration = baseMapper.selectOne(Wraps.<FunctionConfiguration>lbQ()
                    .eq(FunctionConfiguration::getFunctionType, functionTypeEnum)
                    .last(" limit 0,1 "));
            if (Objects.isNull(configuration)) {
                return status;
            } else {
                status = configuration.getFunctionStatus();
                boundHashOperations.put(redisHashFieldName, status + "");
            }
        } else {
            status = Integer.parseInt(o.toString());
        }
        return status;
    }

    /**
     * 更新功能配置的状态
     * 并清除redis中功能配置状态
     * @param configurationUpdateStatusDTO
     */
    @Override
    public void updateFunctionStatus(FunctionConfigurationUpdateDTO configurationUpdateStatusDTO) {
        @NotEmpty String tenantCode = configurationUpdateStatusDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        FunctionConfiguration configuration = baseMapper.selectById(configurationUpdateStatusDTO.getFunctionConfigurationId());
        configuration.setFunctionStatus(configurationUpdateStatusDTO.getFunctionStatus());
        baseMapper.updateById(configuration);
        PlanFunctionTypeEnum functionType = configuration.getFunctionType();

        // 如果是用药， 复查管理， 健康日志 需要同步切换 用药，复查 健康日志  计划的状态
        if (PlanFunctionTypeEnum.REVIEW_MANAGE.eq(functionType)
                || PlanFunctionTypeEnum.MEDICATION.eq(functionType)
                || PlanFunctionTypeEnum.HEALTH_LOG.eq(functionType)) {
            if (configuration.getPlanId() != null) {
                planService.updateStatus(configuration.getPlanId(), configurationUpdateStatusDTO.getFunctionStatus());
            } else {
                log.info("用药， 复查管理， 健康日志 的 护理计划ID不能为空");
            }
        } else if (PlanFunctionTypeEnum.LEARNING_PLAN.eq(functionType)) {
            FollowTaskContentService followTaskContentService = SpringUtils.getBean(FollowTaskContentService.class);
            followTaskContentService.updateLearnPlanStatus(configurationUpdateStatusDTO.getFunctionStatus());
        }

        // 清除缓存中的 状态
        redisTemplate.boundHashOps(FUNCTION_CONFIGURATION_REDIS_HASH_KEY).delete(getRedisHashFieldName(tenantCode, functionType));
    }



    /**
     * 快捷修改 用药管理，复查管理，健康日志， 转诊管理 关联的微信模板ID
     * @param configurationUpdateDTO
     */
    @Override
    public void updateWeiXinTemplateId(FunctionConfigurationUpdateDTO configurationUpdateDTO) {
        @NotEmpty String tenantCode = configurationUpdateDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        FunctionConfiguration configuration = baseMapper.selectById(configurationUpdateDTO.getFunctionConfigurationId());
        configuration.setWeiXinTemplateId(configurationUpdateDTO.getWeiXinTemplateId());
        PlanFunctionTypeEnum functionType = configuration.getFunctionType();
        R<TemplateMsgDto> templateMsgDtoR = null;
        boolean noHasTemplate = false;
        if (PlanFunctionTypeEnum.REVIEW_MANAGE.eq(functionType)
                || PlanFunctionTypeEnum.HEALTH_LOG.eq(functionType)
                || PlanFunctionTypeEnum.REFERRAL_SERVICE.eq(functionType)
                || PlanFunctionTypeEnum.MEDICATION.eq(functionType)) {
            if (configuration.getCaringTemplateId() != null) {
                templateMsgDtoR = templateMsgApi.getTenantOneById(tenantCode, configuration.getCaringTemplateId());
            } else {
                noHasTemplate = true;
            }
        }
        if (templateMsgDtoR != null && templateMsgDtoR.getIsSuccess() || noHasTemplate) {
            TemplateMsgDto data = null;
            if (templateMsgDtoR != null) {
                data = templateMsgDtoR.getData();
            }
            if (Objects.isNull(data)) {
                // 还没有微信
                TemplateMsgUpdateDTO msgSaveDTO = TemplateMsgUpdateDTO.builder()
                        .fields(new ArrayList<>())
                        .templateId(configurationUpdateDTO.getWeiXinTemplateId())
                        .title(configuration.getFunctionName())
                        .build();
                if (PlanFunctionTypeEnum.MEDICATION.eq(functionType)) {
                    msgSaveDTO.setIndefiner(TemplateMessageIndefiner.MEDICINE_TAKE_ALERT_TEMPLATE_MESSAGE);
                }
                if (PlanFunctionTypeEnum.REFERRAL_SERVICE.eq(functionType)) {
                    msgSaveDTO.setIndefiner(TemplateMessageIndefiner.REFERRAL_CARD);
                }
                R<TemplateMsg> msgAndFields = templateMsgApi.updateTemplateMsgAndFields(msgSaveDTO, tenantCode);
                if (msgAndFields.getIsSuccess()) {
                    TemplateMsg templateMsg = msgAndFields.getData();
                    if (Objects.nonNull(templateMsg)) {
                        Long templateMsgId = templateMsg.getId();
                        configuration.setCaringTemplateId(templateMsgId);
                        configuration.setWeiXinTemplateId(configurationUpdateDTO.getWeiXinTemplateId());
                        baseMapper.updateById(configuration);
                    } else {
                        throw new BizException("保存微信模板失败");
                    }
                } else {
                    throw new BizException("保存微信模板失败");
                }
            } else {
                TemplateMsgUpdateDTO msgUpdateDTO = initTemplateMsgUpdateDTO(data, configuration.getWeiXinTemplateId());
                templateMsgApi.updateTemplateMsgAndFields(msgUpdateDTO, tenantCode);
                baseMapper.updateById(configuration);
            }
        }

        // 如果是复查提醒 或者健康日志。则快捷切换微信模板ID之后。检查更新 护理计划中模板的关联ID
        if (PlanFunctionTypeEnum.HEALTH_LOG.eq(functionType) || PlanFunctionTypeEnum.REFERRAL_SERVICE.eq(functionType)) {
            updatePlanDetailTemplateId(configuration.getPlanId(),  configuration.getCaringTemplateId());
        }
    }

    /**
     * 更新护理计划里面的 保存的 系统模板ID
     */
    public void updatePlanDetailTemplateId(Long planId, Long caringTemplateId) {
        Plan plan;
        if (planId != null) {
            plan = planService.getPlanAndDetails(planId);
        } else {
            return;
        }
        if (PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            PlanDetail planDetail = new PlanDetail();
            if (CollUtil.isEmpty(planDetailList)) {
                planDetail.setNursingPlanId(plan.getId());
                planDetail.setType(1);
                planDetail.setTemplateMessageId(caringTemplateId.toString());
                planDetailMapper.insert(planDetail);
            } else {
                PlanDetailDTO detailDTO = planDetailList.get(0);
                planDetail.setId(detailDTO.getId());
                planDetail.setTemplateMessageId(caringTemplateId.toString());
                planDetailMapper.updateById(planDetail);
            }
        } else {
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            PlanDetail planDetail = new PlanDetail();
            if (CollUtil.isEmpty(planDetailList)) {
                planDetail.setNursingPlanId(plan.getId());
                planDetail.setPushType(0);
                planDetail.setType(1);
                planDetailMapper.insert(planDetail);
                PlanDetailTime planDetailTime = new PlanDetailTime();
                planDetailTime.setNursingPlanDetailId(planDetail.getId());
                planDetailTime.setTemplateMessageId(caringTemplateId.toString());
                planDetailTimeMapper.insert(planDetailTime);
            } else {
                PlanDetailDTO detailDTO = planDetailList.get(0);
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailTimes)) {
                    PlanDetailTime planDetailTime = new PlanDetailTime();
                    planDetailTime.setNursingPlanDetailId(planDetail.getId());
                    planDetailTime.setTemplateMessageId(caringTemplateId.toString());
                    planDetailTimeMapper.insert(planDetailTime);
                } else {
                    PlanDetailTime detailTime = planDetailTimes.get(0);
                    detailTime.setTemplateMessageId(caringTemplateId.toString());
                    planDetailTimeMapper.updateById(detailTime);
                }
            }
        }

    }

    /**
     * 快速修改 指标监测， 自定义护理计划 关联的微信模板ID
     * @param planTemplateUpdateDTO
     */
    @Override
    public void updatePlanTemplate(PlanTemplateUpdateDTO planTemplateUpdateDTO) {
        @NotEmpty String tenantCode = planTemplateUpdateDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        Long caringTemplateId = planTemplateUpdateDTO.getCaringTemplateId();
        Long planId = planTemplateUpdateDTO.getPlanId();
        String weiXinTemplateId = planTemplateUpdateDTO.getWeiXinTemplateId();

        R<TemplateMsg> templateMsgR = null;
        if (Objects.nonNull(caringTemplateId)) {
            R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getTenantOneById(tenantCode, caringTemplateId);
            if (templateMsgDtoR.getIsSuccess()) {
                TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
                TemplateMsgUpdateDTO updateDTO = initTemplateMsgUpdateDTO(msgDtoRData, weiXinTemplateId);
                updateDTO.setTitle(planTemplateUpdateDTO.getWeixinTemplateTitle());
                templateMsgR = templateMsgApi.updateTemplateMsgAndFields(updateDTO, tenantCode);
            }
        } else {
            // 之前没有模板。需要新增一个。
            TemplateMsgUpdateDTO updateDTO = new TemplateMsgUpdateDTO();
            updateDTO.setIndefiner(TemplateMessageIndefiner.NURSING_PLAN);
            updateDTO.setTemplateId(weiXinTemplateId);
            templateMsgR = templateMsgApi.updateTemplateMsgAndFields(updateDTO, tenantCode);
        }
        if (templateMsgR != null && templateMsgR.getIsSuccess()) {
            TemplateMsg data = templateMsgR.getData();
            if (Objects.nonNull(data)) {
                updatePlanDetailTemplateId(planId,  data.getId());
            }
        }
    }

    /**
     * 初始化快捷修改微信模板ID时，提交的参数
     * 判断是否已经关联过 weiXinTemplateId
     * 如果之前已经关联过，则通过不设置fields 方式，清除原先的fields
     * @param dtoRData
     * @param weiXinTemplateId
     * @return
     */
    public TemplateMsgUpdateDTO initTemplateMsgUpdateDTO(TemplateMsgDto dtoRData, String weiXinTemplateId) {
        TemplateMsgUpdateDTO templateMsgUpdateDTO = new TemplateMsgUpdateDTO();
        BeanUtils.copyProperties(dtoRData, templateMsgUpdateDTO);
        templateMsgUpdateDTO.setFields(new ArrayList<>());
        List<TemplateMsgFields> fields = dtoRData.getFields();
        // 如果之前没有关联微信模板ID， 则保留文案配置，如果之前关联过，则不传递fields ，fields 将会清除
        if (StrUtil.isEmpty(dtoRData.getTemplateId())) {
            if (CollUtil.isNotEmpty(fields)) {
                TemplateMsgFieldsUpdateDTO updateDTO;
                List<TemplateMsgFieldsUpdateDTO> updateFields = new ArrayList<>();
                for (TemplateMsgFields field : fields) {
                    updateDTO = new TemplateMsgFieldsUpdateDTO();
                    BeanUtils.copyProperties(field, updateDTO);
                    updateFields.add(updateDTO);
                }
                templateMsgUpdateDTO.setFields(updateFields);
            }
        }
        templateMsgUpdateDTO.setTemplateId(weiXinTemplateId);
        return templateMsgUpdateDTO;
    }

    /**
     * 在线咨询，病例讨论，预约
     * 更新模板配置中的微信的模板ID
     *
     * @param pushTemplateUpdateDTO
     */
    @Override
    public void updatePushTemplate(PushTemplateUpdateDTO pushTemplateUpdateDTO) {

        @NotEmpty String tenantCode = pushTemplateUpdateDTO.getTenantCode();
        Long caringTemplateId = pushTemplateUpdateDTO.getCaringTemplateId();
        String templateMessageIndefiner = pushTemplateUpdateDTO.getTemplateMessageIndefiner();
        String weiXinTemplateId = pushTemplateUpdateDTO.getWeiXinTemplateId();
        BaseContextHandler.setTenant(tenantCode);
        R<TemplateMsg> templateMsgR = null;
        if (Objects.nonNull(caringTemplateId)) {
            R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getTenantOneById(tenantCode, caringTemplateId);
            if (templateMsgDtoR.getIsSuccess()) {
                TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
                TemplateMsgUpdateDTO updateDTO = initTemplateMsgUpdateDTO(msgDtoRData, weiXinTemplateId);
                templateMsgR = templateMsgApi.updateTemplateMsgAndFields(updateDTO, tenantCode);
            }
        } else {
            // 之前没有模板。需要新增一个。
            TemplateMsgUpdateDTO updateDTO = new TemplateMsgUpdateDTO();
            updateDTO.setIndefiner(templateMessageIndefiner);
            updateDTO.setTemplateId(weiXinTemplateId);
            templateMsgR = templateMsgApi.updateTemplateMsgAndFields(updateDTO, tenantCode);
        }
        if (templateMsgR == null || !templateMsgR.getIsSuccess()) {
            throw new BizException("更新微信模板失败");
        }

    }

    /**
     * 分角色查询 学习计划列表
     * @param learnPlanRole
     * @return
     */
    @Override
    public List<Plan> queryLearnPlan(String learnPlanRole) {
        List<Plan> planList = planService.list(Wraps.<Plan>lbQ()
                .eq(Plan::getPlanType, PlanEnum.LEARN_PLAN.getCode())
                .eq(Plan::getLearnPlanRole, learnPlanRole)
                .orderByDesc(SuperEntity::getCreateTime));
        // 统计计划下的planDetailTime的数量
        if (CollUtil.isNotEmpty(planList)) {
            List<Long> planIds = planList.stream().map(SuperEntity::getId).collect(Collectors.toList());
            List<PlanDetail> planDetails = planDetailMapper.selectList(Wraps.<PlanDetail>lbQ().in(PlanDetail::getNursingPlanId, planIds)
                    .select(SuperEntity::getId, PlanDetail::getNursingPlanId));
            if (CollUtil.isNotEmpty(planDetails)) {
                Map<Long, Long> planDetailIdMaps = planDetails.stream().collect(Collectors.toMap(PlanDetail::getNursingPlanId, SuperEntity::getId));
                Collection<Long> planDetailIds = planDetailIdMaps.values();
                if (CollUtil.isNotEmpty(planDetailIds)) {
                    QueryWrap<PlanDetailTime> count = Wraps.<PlanDetailTime>q()
                            .in("nursing_plan_detail_id", planDetailIds)
                            .groupBy("nursing_plan_detail_id")
                            .select("nursing_plan_detail_id, count(id) as countNum");
                    List<Map<String, Object>> mapList = planDetailTimeMapper.selectMaps(count);
                    Map<Long, Object> planTimeCount = new HashMap<>();
                    for (Map<String, Object> t : mapList) {
                        planTimeCount.put(Convert.toLong(t.get("nursing_plan_detail_id")), t.get("countNum"));
                    }
                    for (Plan plan : planList) {
                        Long planDetailId = planDetailIdMaps.get(plan.getId());
                        Object o = planTimeCount.get(planDetailId);
                        if (Objects.nonNull(o)) {
                            plan.setPlanDetailTimeNumber(Integer.parseInt(o.toString()));
                        }
                    }
                }
            }
        }
        return planList;
    }

    /**
     * 获取 redis的中的hash表 的field 的 name
     * @param tenantCode
     * @param functionTypeEnum
     * @return
     */
    private static String getRedisHashFieldName(String tenantCode, PlanFunctionTypeEnum functionTypeEnum) {
        return tenantCode + functionTypeEnum.toString();
    }

    /**
     * 复制项目时。把原项目的功能配置状态复制过来
     * @param fromTenantCode
     * @param toTenantCode
     */
    @Override
    public void copyFunctionConfiguration(String fromTenantCode, String toTenantCode) {
        BaseContextHandler.setTenant(fromTenantCode);
        List<FunctionConfiguration> formFunctionConfigurations = baseMapper.selectList(Wraps.<FunctionConfiguration>lbQ());
        BaseContextHandler.setTenant(toTenantCode);
        List<FunctionConfiguration> functionConfigurations = initFunctionConfiguration(toTenantCode);
        if (CollUtil.isEmpty(formFunctionConfigurations)) {
            return;
        }
        Map<PlanFunctionTypeEnum, List<FunctionConfiguration>> formFunctionConfigMap = formFunctionConfigurations.stream().collect(Collectors.groupingBy(FunctionConfiguration::getFunctionType));

        FunctionConfiguration tempConfig;
        for (FunctionConfiguration configuration : functionConfigurations) {
            PlanFunctionTypeEnum functionType = configuration.getFunctionType();
            List<FunctionConfiguration> list = formFunctionConfigMap.get(functionType);
            if (CollUtil.isEmpty(list)) {
                continue;
            }
            tempConfig = list.get(0);
            configuration.setFunctionStatus(tempConfig.getFunctionStatus());
            baseMapper.updateById(configuration);
        }

    }

    /**
     * 初始化某个项目的功能配置
     * @param tenantCode
     */
    @Transactional
    @Override
    public List<FunctionConfiguration> initFunctionConfiguration(String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        baseMapper.delete(Wraps.lbQ());
        // 查询用药，复查提醒，健康日志 的护理计划。 根据护理计划生产对应的功能配置
        List<Plan> planList = planService.list(Wraps.<Plan>lbQ()
                .in(Plan::getPlanType, PlanEnum.MEDICATION_REMIND.getCode(),
                        PlanEnum.REVIEW_REMIND.getCode(), PlanEnum.HEALTH_LOG.getCode()));
        Map<Integer, Plan> planMap = new HashMap<>();
        if (CollUtil.isNotEmpty(planList)) {
            planMap = planList.stream().collect(Collectors.toMap(Plan::getPlanType, plan -> plan, (o1, o2) -> o2));
        }
        List<FunctionConfiguration> functionConfigurations = new ArrayList<>();
        // 初始化 用药
        Plan plan = planMap.get(PlanEnum.MEDICATION_REMIND.getCode());
        FunctionConfiguration configuration;
        if (Objects.nonNull(plan)) {
            plan.setStatus(1);
            configuration = initByPlan(plan,  PlanFunctionTypeEnum.MEDICATION);
            // 查询用药的微信模板配置
            R<TemplateMsgDto> indefiner = templateMsgApi.findByIndefiner(tenantCode, TemplateMessageIndefiner.MEDICINE_TAKE_ALERT_TEMPLATE_MESSAGE);
            if (indefiner.getIsSuccess()) {
                TemplateMsgDto data = indefiner.getData();
                if (Objects.nonNull(data)) {
                    String templateId = data.getTemplateId();
                    configuration.setWeiXinTemplateId(templateId);
                    configuration.setCaringTemplateId(data.getId());
                }
            }
            configuration.setHasWeiXinTemplate(1);
            functionConfigurations.add(configuration);
        }

        // 初始化 复查提醒
        plan = planMap.get(PlanEnum.REVIEW_REMIND.getCode());
        TemplateMsgDto templateMsgDto;
        if (Objects.nonNull(plan)) {
            configuration = initByPlan(plan, PlanFunctionTypeEnum.REVIEW_MANAGE);
            templateMsgDto = getReviewManageAndHealthLogWeiXinTemplateId(plan, tenantCode);
            if (Objects.nonNull(templateMsgDto)) {
                configuration.setWeiXinTemplateId(templateMsgDto.getTemplateId());
                configuration.setCaringTemplateId(templateMsgDto.getId());
            }
            configuration.setHasWeiXinTemplate(1);
            functionConfigurations.add(configuration);
        }

        // 初始化 健康日志
        plan = planMap.get(PlanEnum.HEALTH_LOG.getCode());
        if (Objects.nonNull(plan)) {
            configuration = initByPlan(plan, PlanFunctionTypeEnum.HEALTH_LOG);
            templateMsgDto = getReviewManageAndHealthLogWeiXinTemplateId(plan, tenantCode);
            if (Objects.nonNull(templateMsgDto)) {
                configuration.setWeiXinTemplateId(templateMsgDto.getTemplateId());
                configuration.setCaringTemplateId(templateMsgDto.getId());
            }
            configuration.setHasWeiXinTemplate(1);
            functionConfigurations.add(configuration);
        }

        // 初始化 转诊管理
        configuration = initByPlanFunctionTypeEnum(PlanFunctionTypeEnum.REFERRAL_SERVICE,1, 0);
        R<TemplateMsgDto> indefiner = templateMsgApi.findByIndefiner(tenantCode, TemplateMessageIndefiner.REFERRAL_CARD);
        if (indefiner.getIsSuccess()) {
            TemplateMsgDto data = indefiner.getData();
            if (Objects.nonNull(data)) {
                String templateId = data.getTemplateId();
                configuration.setWeiXinTemplateId(templateId);
                configuration.setCaringTemplateId(data.getId());
            }
        }
        configuration.setHasWeiXinTemplate(1);
        functionConfigurations.add(configuration);

        // 初始化 学习计划
        configuration = initByPlanFunctionTypeEnum(PlanFunctionTypeEnum.LEARNING_PLAN ,0, 1);
        configuration.setFunctionStatus(0);
        functionConfigurations.add(configuration);


        // 初始化 预约管理
        configuration = initByPlanFunctionTypeEnum(PlanFunctionTypeEnum.BOOKING_MANAGEMENT,1, 0);
        functionConfigurations.add(configuration);

        // 初始化 病例讨论
        configuration = initByPlanFunctionTypeEnum(PlanFunctionTypeEnum.CASE_DISCUSSION,1, 0);
        functionConfigurations.add(configuration);

        // 初始化 在线咨询
        configuration = initByPlanFunctionTypeEnum(PlanFunctionTypeEnum.ONLINE_CONSULTATION,1, 0);
        functionConfigurations.add(configuration);

        // 初始化 指标监测
        configuration = initByPlanFunctionTypeEnum(PlanFunctionTypeEnum.INDICATOR_MONITORING,0, 1);
        configuration.setFunctionStatus(0);
        functionConfigurations.add(configuration);

        // 初始化 自定义随访
        configuration = initByPlanFunctionTypeEnum(PlanFunctionTypeEnum.CUSTOM_FOLLOW_UP,0, 1);
        configuration.setFunctionStatus(0);
        functionConfigurations.add(configuration);

        if (CollUtil.isNotEmpty(functionConfigurations)) {
            baseMapper.insertBatchSomeColumn(functionConfigurations);
        }
        return functionConfigurations;
    }

    /**
     * 获取复查提醒， 健康日志的微信模板ID
     * @param plan
     * @param tenantCode
     * @return
     */
    private TemplateMsgDto getReviewManageAndHealthLogWeiXinTemplateId(Plan plan, String tenantCode) {
        Plan planAndDetails = planService.getPlanAndDetails(plan.getId());
        List<PlanDetailDTO> planDetailList = planAndDetails.getPlanDetailList();
        if (CollUtil.isNotEmpty(planDetailList)) {
            List<PlanDetailTime> planDetailTimes = planDetailList.get(0).getPlanDetailTimes();
            if (CollUtil.isNotEmpty(planDetailTimes)) {
                PlanDetailTime detailTime = planDetailTimes.get(0);
                if (Objects.nonNull(detailTime)) {
                    String templateMessageId = detailTime.getTemplateMessageId();
                    if (StringUtils.isNotEmptyString(templateMessageId)) {
                        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getTenantOneById(tenantCode, Long.parseLong(templateMessageId));
                        if (templateMsgDtoR.getIsSuccess()) {
                            TemplateMsgDto data = templateMsgDtoR.getData();
                            if (Objects.nonNull(data)) {
                                return data;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * 初始化
     * @param functionTypeEnum
     * @param hasPushConfig
     * @param hasFunctionConfig
     * @return
     */
    private FunctionConfiguration initByPlanFunctionTypeEnum(PlanFunctionTypeEnum functionTypeEnum,
                                                             Integer hasPushConfig,
                                                             Integer hasFunctionConfig) {

        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setFunctionType(functionTypeEnum);
        functionConfiguration.setFunctionStatus(1);
        functionConfiguration.setFunctionName(functionTypeEnum.getDesc());
        functionConfiguration.setHasFunctionConfig(hasFunctionConfig);
        functionConfiguration.setHasPushConfig(hasPushConfig);
        return functionConfiguration;
    }

    /**
     * 根据 计划初始化 功能配置
     * 用药，复查提醒 健康日志
     * @param plan
     * @param functionType
     * @return
     */
    private FunctionConfiguration initByPlan(Plan plan, PlanFunctionTypeEnum functionType) {
        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setFunctionStatus(plan.getStatus());
        configuration.setFunctionName(functionType.getDesc());
        configuration.setPlanId(plan.getId());
        configuration.setPlanType(plan.getPlanType());
        configuration.setFunctionType(functionType);
        configuration.setHasPushConfig(1);
        configuration.setHasFunctionConfig(1);
        return configuration;
    }

    /**
     * 初始化 系统中各项目的功能配置
     */
    @Deprecated
    @Override
    public void initFunctionConfiguration() {

        R<List<Object>> allTenantCode = tenantApi.getAllTenantCode();
        if (allTenantCode.getIsSuccess()) {
            List<Object> codeDatas = allTenantCode.getData();
            if (CollUtil.isNotEmpty(codeDatas)) {
                for (Object codeData : codeDatas) {
                    initFunctionConfiguration(codeData.toString());
                }
                return;
            }
        }
        throw new BizException("初始化功能配置失败");
    }



    /**
     * 获取监测指标 自定义随访 状态关闭时，
     * 不需要在随访任务列表显示，现在状态为正常显示的计划ID
     * @return
     */
    @Override
    public List<Long> getRemovePlanButOpenId() {
        // 获取 指标监测 自定义随访的 功能配置的状态
        String tenant = BaseContextHandler.getTenant();
        Integer indicatorMonitoring = this.getFunctionStatus(tenant, PlanFunctionTypeEnum.INDICATOR_MONITORING);
        // 指标监测关闭。 查询指标监测中开启的护理计划
        List<Long> removePlanId = new ArrayList<>();
        if (indicatorMonitoring.equals(0)) {
            List<Plan> plans = planService.list(Wraps.<Plan>lbQ()
                    .select(SuperEntity::getId, Plan::getName)
                    .eq(Plan::getStatus, 1)
                    .eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.MONITORING_DATA.operateType));
            if (CollUtil.isNotEmpty(plans)) {
                removePlanId.addAll(plans.stream().map(SuperEntity::getId).collect(Collectors.toList()));
            }
        }
        Integer customFollowUp = this.getFunctionStatus(tenant, PlanFunctionTypeEnum.CUSTOM_FOLLOW_UP);
        // 自定义随访关闭。 查询自定义随访中开启的护理计划
        if (customFollowUp.equals(0)) {
            List<Plan> plans = planService.list(Wraps.<Plan>lbQ()
                    .select(SuperEntity::getId, Plan::getName)
                    .eq(Plan::getStatus, 1)
                    .eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.CARE_PLAN.operateType)
                    .eq(Plan::getIsAdminTemplate, 1));
            if (CollUtil.isNotEmpty(plans)) {
                removePlanId.addAll(plans.stream().map(SuperEntity::getId).collect(Collectors.toList()));
            }
        }
        return removePlanId;

    }

}
