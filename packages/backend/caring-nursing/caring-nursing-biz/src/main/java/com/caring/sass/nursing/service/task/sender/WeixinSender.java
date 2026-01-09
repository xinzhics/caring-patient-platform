package com.caring.sass.nursing.service.task.sender;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.form.FormResultService;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.service.task.NursingPlanPushParam;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.nursing.util.PlanDict;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 微信消息发送
 *
 * @author xinzh
 */
@Slf4j
@Component
public class WeixinSender {

    private final WeiXinApi weiXinApi;

    private final TemplateMsgApi templateMsgApi;

    private final FormResultService formResultService;

    private final ReminderLogService reminderLogService;

    private final MsgPatientSystemMessageApi patientSystemMessageApi;

    private final RedisTemplate<String, String> redisTemplate;

    public WeixinSender(WeiXinApi weiXinApi, MsgPatientSystemMessageApi patientSystemMessageApi,
                        TemplateMsgApi templateMsgApi,
                        FormResultService formResultService,
                        ReminderLogService reminderLogService,
                        RedisTemplate<String, String> redisTemplate) {
        this.weiXinApi = weiXinApi;
        this.templateMsgApi = templateMsgApi;
        this.patientSystemMessageApi = patientSystemMessageApi;
        this.formResultService = formResultService;
        this.reminderLogService = reminderLogService;
        this.redisTemplate = redisTemplate;
    }


    /**
     * 给医生推送学习计划
     * @param wxAppId
     * @param openId
     * @param detailDTOTemplateMessageId
     * @param cmsTitle
     * @param url
     * @return
     */
    public boolean sendDoctorLearnPlanWeiXinMessage(String wxAppId, String doctorName, String planName, String openId, String detailDTOTemplateMessageId, String cmsTitle, String url) {

        R<TemplateMsgDto> templateMsgDtoR;
        if (StrUtil.isEmpty(detailDTOTemplateMessageId)) {
            templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(BaseContextHandler.getTenant(), null, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
        } else {
            templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(BaseContextHandler.getTenant(), Long.parseLong(detailDTOTemplateMessageId), null);
        }
        TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
        if (templateMsgDtoR.getIsError() || Objects.isNull(msgDtoRData)) {
            return false;
        }
        WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        List<WxMpTemplateData> data = new ArrayList();
        if (msgDtoRData.getCommonCategory()) {
            data = CommonTemplateServiceWorkModel.buildWxMpTemplateData(doctorName, planName);
            templateMessage.setData(data);
        } else {
            List<TemplateMsgFields> msgFieldsList = msgDtoRData.getFields();
            for (TemplateMsgFields fields : msgFieldsList) {
                if (fields.getType() != null && 0 != fields.getType()) {
                    if (fields.getType() == 2) {
                        // 模板为 时间时，
                        data.add(new WxMpTemplateData(fields.getAttr(), DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM), fields.getColor()));
                    }
                } else {
                    data.add(new WxMpTemplateData(fields.getAttr(), fields.getValue(), fields.getColor()));
                }
            }
            if (Objects.nonNull(cmsTitle)) {
                // 检查。当模板消息配置中。有keyword3 并且要求值 为前后端约定的 CmsContent.title 时。将需要推送的Cms标题 填充
                for (WxMpTemplateData datum : data) {
                    if ("keyword3".equals(datum.getName())) {
                        if ("CmsContent.title".equals(datum.getValue())) {
                            datum.setValue(cmsTitle);
                        }
                        break;
                    }
                }
            }
        }
        SendTemplateMessageForm form = new SendTemplateMessageForm();
        templateMessage.setTemplateId(msgDtoRData.getTemplateId());
        templateMessage.setToUser(openId);
        templateMessage.setData(data);
        templateMessage.setUrl(url);
        form.setTemplateMessage(templateMessage);
        form.setWxAppId(wxAppId);
        R<String> message = weiXinApi.sendTemplateMessage(form);
        if (message.getIsSuccess() != null && message.getIsSuccess()) {
            return true;
        }
        return false;
    }

    /**
     * 发送学习计划的模板消息
     * @param tenant
     * @param patient
     * @param linkUrl
     * @return
     */
    public String sendLearnPlanWeiXinMessage(Tenant tenant,
                                              NursingPlanPatientBaseInfoDTO patient,
                                              String cmsTitle,
                                              String planName,
                                              String linkUrl,
                                              LocalDateTime pushTime,
                                              Long planId,
                                              TemplateMsgDto templateMsgDto,
                                              Long reminderLogId) {
        String tenantCode = tenant.getCode();
        WxMpTemplateMessage wxMpTemplateMessage = null;

        PlanFunctionTypeEnum functionTypeEnum = PlanFunctionTypeEnum.getEnumByPlanType(PlanEnum.LEARN_PLAN.getCode(), null);
        if (functionTypeEnum != null) {
            MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(functionTypeEnum.getCode(),
                    planId,
                    wxMpTemplateMessage.getUrl(),
                    patient.getId(),
                    pushTime,
                    patient.getDoctorName(),
                    tenant.getCode());
            systemMessageSaveDTO.createPushContent( null, null, null);
            patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);
        }
        // 个人服务号不推送模版消息
        if (tenant.isPersonalServiceNumber()) {
            return null;
        }

        wxMpTemplateMessage = getTemplateMsgData(patient, templateMsgDto, planName, tenantCode);
        // 获取模板封装好的微信模板配置
        if (Objects.isNull(wxMpTemplateMessage)) {
            log.error("sendWeiXinMessage error 获取对应的微信模板失败");
            return "获取对应的微信模板失败";
        }

        if (Objects.nonNull(cmsTitle)) {
            // 检查。当模板消息配置中。有keyword3 并且要求值 为前后端约定的 CmsContent.title 时。将需要推送的Cms标题 填充
            List<WxMpTemplateData> data = wxMpTemplateMessage.getData();
            for (WxMpTemplateData datum : data) {
                if ("keyword3".equals(datum.getName())) {
                    if ("CmsContent.title".equals(datum.getValue())) {
                        datum.setValue(cmsTitle);
                    }
                    break;
                }
            }
        }
        // 封装成微信需要的格式
        SendTemplateMessageForm form = new SendTemplateMessageForm();
        wxMpTemplateMessage.setToUser(patient.getOpenId());
        wxMpTemplateMessage.setUrl(linkUrl);
        form.setWxAppId(tenant.getWxAppId());
        form.setTemplateMessage(wxMpTemplateMessage);
        form.setReminderLogPush(reminderLogId.toString());
        form.setTenantCode(tenantCode);
        String jsonString = JSON.toJSONString(form);
        redisTemplate.opsForList().leftPush(SaasRedisBusinessKey.PUSH_TEMPLATE_MESSAGE_TASK, jsonString);
        return null;
    }

    /**
     * 发送预约相关的模板
     * @param wxAppId
     * @param tenantCode
     * @param templateMessageIndefine
     * @param openId
     * @param mpTemplateData
     * @param url
     */
    public void sendAppointmentWeiXinMessage(String wxAppId,
                                             String tenantCode,
                                             String templateMessageIndefine,
                                             String openId,
                                             List<WxMpTemplateData> mpTemplateData,
                                             String templateId,
                                             String url) {

        SendTemplateMessageForm form = new SendTemplateMessageForm();
        form.setWxAppId(wxAppId);
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        wxMpTemplateMessage.setTemplateId(templateId);
        wxMpTemplateMessage.setToUser(openId);
        wxMpTemplateMessage.setData(mpTemplateData);
        wxMpTemplateMessage.setUrl(url);
        form.setTemplateMessage(wxMpTemplateMessage);
        R<String> message = weiXinApi.sendTemplateMessage(form);
        Boolean isSuccess = message.getIsSuccess();
        if (!isSuccess) {
            log.error("预约通知发送失败");
        }
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 发送微信消息 模板消息
     * @Date 2020/10/27 14:57
     */
    public String sendWeiXinMessage(Tenant tenant, Boolean cmsLink, String followUpPlanType, Long planId, NursingPlanPatientBaseInfoDTO patient,
                                     PlanEnum planType,
                                     ChannelContent channelContent, String linkUrl, String planName, LocalDateTime pushTime, String customServiceName,
                                     String customLink, TemplateMsgDto templateMsgDto, ReminderLog reminderLog) {
        // 获取模板封装好的微信模板配置
        String pageTitle = planName;
        if (PlanDict.MONITORING_DATA.equals(followUpPlanType)) {
            planName = I18nUtils.getMessageByTenantDefault(CommonTemplateServiceWorkModel.MONITORING_PLAN, tenant.getDefaultLanguage(), planName);
        } else if (PlanDict.CARE_PLAN.equals(followUpPlanType)) {
            if (PlanEnum.CUSTOM_PLAN.equals(planType) || PlanEnum.HEALTH_LOG.equals(planType)) {
                planName = I18nUtils.getMessageByTenantDefault(CommonTemplateServiceWorkModel.FOLLOW_UP_REMINDER, tenant.getDefaultLanguage(), planName);
            }
        }
        if (StrUtil.isEmpty(customServiceName)) {
            customServiceName = planName;
        }
        // 满足 不填写表单。也不需要跳转链接的情况
        String url = null;
        // cms的链接跳转
        if (cmsLink || StrUtil.isNotEmpty(customLink)) {
            // 跳转的可能是个文章链接，也可能是个超市提醒的连接
            if (StringUtils.isNotEmpty(linkUrl) || StrUtil.isNotEmpty(customLink)) {
                try {
                    String encodeUrl = "";
                    if (StrUtil.isNotEmpty(customLink)) {
                        encodeUrl = customLink;
                    } else if (StringUtils.isNotEmpty(linkUrl)) {
                        encodeUrl = linkUrl;
                    }
                    url = ApplicationDomainUtil.externalLinksShowUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                            "messageId=" + reminderLog.getId(), "cmsUrl=" + URLEncoder.encode(encodeUrl, "utf-8"), "tenantCode=" + tenant.getCode() );
                } catch (UnsupportedEncodingException e) {
                    log.error("linkUrl encode error {}", linkUrl);
                    return "linkUrl encode error：" + linkUrl;
                }
            }
        } else if (StringUtils.isEmpty(linkUrl)) {
            // 用户没有设置指定的跳转目标。根据计划生成一个跳转链接
            url = H5Router.getRouterByPlanEnum(planType, followUpPlanType);
            if (StrUtil.isNotEmpty(url)) {
                // 是自定义计划。
                if (PlanEnum.CUSTOM_PLAN.equals(planType)) {
                    // 监测计划。跳转到监测
                    if (PlanDict.MONITORING_DATA.equals(followUpPlanType)) {
                        url = ApplicationDomainUtil.wxPatientBaseDomain(tenant.getDomainName(),
                                Objects.nonNull(tenant.getWxBindTime())) + "/" + String.format(url, planId, reminderLog.getId(), pageTitle);
                    } else {
                        url = ApplicationDomainUtil.wxPatientBaseDomain(tenant.getDomainName(),
                                Objects.nonNull(tenant.getWxBindTime())) + "/" + String.format(url, planId, reminderLog.getId());
                    }
                } else {
                    url = ApplicationDomainUtil.wxPatientBaseDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime())) + "/" + url + "?messageId=" + reminderLog.getId();
                }
            }
        } else {
            try {
                url = ApplicationDomainUtil.externalLinksShowUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                        "messageId=" + reminderLog.getId(), "cmsUrl=" + URLEncoder.encode(linkUrl, "utf-8"), "tenantCode=" + tenant.getCode() );
            } catch (UnsupportedEncodingException e) {
                log.error("linkUrl encode error {}", linkUrl);
                return "linkUrl encode error：" + linkUrl;
            }
        }

        PlanFunctionTypeEnum functionTypeEnum = PlanFunctionTypeEnum.getEnumByPlanType(planType.getCode(), followUpPlanType);
        if (functionTypeEnum != null) {
            MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(functionTypeEnum.getCode(),
                    planId,
                    url,
                    patient.getId(),
                    pushTime,
                    patient.getDoctorName(),
                    tenant.getCode());
            systemMessageSaveDTO.setPlanName(pageTitle);
            systemMessageSaveDTO.createPushContent( pageTitle, null, null);
            patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);
        }
        // 个人服务号不需要执行后续的发送公众号模版消息逻辑
        if (tenant.isPersonalServiceNumber()) {
            return null;
        }
        WxMpTemplateMessage wxMpTemplateMessage = getTemplateMsgData(patient, templateMsgDto, customServiceName, tenant.getCode());
        if (Objects.isNull(wxMpTemplateMessage)) {
            log.error("sendWeiXinMessage error 获取对应的微信模板失败");
            return "获取对应的微信模板失败";
        }

        if (Objects.nonNull(channelContent)) {
            // 检查。当模板消息配置中。有keyword3 并且要求值 为前后端约定的 CmsContent.title 时。将需要推送的Cms标题 填充
            List<WxMpTemplateData> data = wxMpTemplateMessage.getData();
            for (WxMpTemplateData datum : data) {
                if ("keyword3".equals(datum.getName())) {
                    if ("CmsContent.title".equals(datum.getValue())) {
                        datum.setValue(channelContent.getTitle());
                    }
                    break;
                }
            }
        }
        // 封装成微信需要的格式
        SendTemplateMessageForm form = new SendTemplateMessageForm();
        wxMpTemplateMessage.setToUser(patient.getOpenId());
        wxMpTemplateMessage.setUrl(url);
        form.setWxAppId(tenant.getWxAppId());
        form.setTemplateMessage(wxMpTemplateMessage);
        form.setReminderLogPush(reminderLog.getId().toString());;
        form.setTenantCode(tenant.getCode());
        String jsonString = JSON.toJSONString(form);
        redisTemplate.opsForList().leftPush(SaasRedisBusinessKey.PUSH_TEMPLATE_MESSAGE_TASK, jsonString);
        return null;
    }


    /**
     * @return me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage
     * @Author yangShuai
     * @Description 获取微信推送的模板。并填充模板中的属性
     * @Date 2020/10/28 10:01
     */
    public WxMpTemplateMessage getTemplateMsgData(NursingPlanPatientBaseInfoDTO patient, TemplateMsgDto templateMsgDto, String planName, String tenantCode) {
        // 拉取配置的 微信模板
        if (Objects.isNull(templateMsgDto)) {
            return null;
        }
        WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        templateMessage.setTemplateId(templateMsgDto.getTemplateId());
        templateMessage.setToUser(patient.getOpenId());
        List<WxMpTemplateData> data = new ArrayList();
        if (templateMsgDto.getCommonCategory()) {
            data = CommonTemplateServiceWorkModel.buildWxMpTemplateData(patient.getName(), planName);
            templateMessage.setData(data);
            return templateMessage;
        }
        List<TemplateMsgFields> msgFieldsList = templateMsgDto.getFields();

        List<FormResult> list = formResultService.getBasicAndLastHealthFormResult(patient.getId());
        // 查询 患者填写的 基本消息和疾病信息 表单。
        List<FormFieldDto> allFields = new ArrayList();
        if (!CollectionUtils.isEmpty(list)) {
            for (FormResult formResult : list) {
                List<FormFieldDto> formFields = JSON.parseArray(formResult.getJsonContent(), FormFieldDto.class);
                allFields.addAll(formFields);
            }
        }

        for (TemplateMsgFields fields : msgFieldsList) {
            if (fields.getType() != null && 0 != fields.getType()) {
                if (fields.getType() == 1) {
                    StringBuilder sb = new StringBuilder();
                    // 将 模板中 关联表单属性的 数据 从表单中拉取要填充的结果。
                    for (FormFieldDto allField : allFields) {
                        if (allField.getId() != null && allField.getId().equals(fields.getValue())) {
                            // 从表单中获取用户填写的结果。 并设置到推送模板的值中
                            String values = allField.parseResultValues();
                            if (StringUtils.isEmpty(values)) {
                                String widgetType = allField.getWidgetType();
                                // 如果是患者姓名组件。直接从患者中拿去
                                if (null != widgetType && widgetType.equals(FormWidgetType.FULL_NAME)) {
                                    sb.append(patient.getName());
                                }
                            } else {
                                sb.append(values);
                            }
                        }
                    }
                    // 添加一条
                    data.add(new WxMpTemplateData(fields.getAttr(), sb.toString(), fields.getColor()));
                } else if (fields.getType() == 2) {
                    // 如果项目的code 是敏宁的0401 。按敏宁要求，随访时间上不添加时分秒
                    if (StrUtil.equals(tenantCode, "0401")) {
                        data.add(new WxMpTemplateData(fields.getAttr(), DateUtils.date2Str(new Date(), DateUtils.Y_M_D), fields.getColor()));
                    } else {
                        data.add(new WxMpTemplateData(fields.getAttr(), DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM), fields.getColor()));
                    }
                }
            } else {
                data.add(new WxMpTemplateData(fields.getAttr(), fields.getValue(), fields.getColor()));
            }
        }

        templateMessage.setData(data);
        return templateMessage;
    }


    /**
     * @return java.lang.Long
     * @Author yangShuai
     * @Description 为护理计划创建一个 推送记录
     * @Date 2020/10/27 14:43
     */
    public ReminderLog createReminderLog(NursingPlanPatientBaseInfoDTO patient, PlanEnum planType, String workId, Long planId) {
        cn.hutool.json.JSONArray jsonArray = new cn.hutool.json.JSONArray();
        jsonArray.add(workId);
        if (null == planType) {
            return reminderLogService.createReminderLog(patient.getId(), "未知的护理计划类型",
                    JSONUtil.toJsonStr(jsonArray), 0, planId, patient.getOrganId(), patient.getClassCode(), patient.getDoctorId(), patient.getServiceAdvisorId());
        }
        return reminderLogService.createReminderLog(patient.getId(), planType.getDesc(),
                JSONUtil.toJsonStr(jsonArray), planType.getCode(), planId, patient.getOrganId(), patient.getClassCode(), patient.getDoctorId(), patient.getServiceAdvisorId());
    }


}
