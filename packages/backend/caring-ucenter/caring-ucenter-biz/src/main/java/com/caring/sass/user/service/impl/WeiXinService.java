package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.paramtericText.ParametricTextManager;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.api.FormResultApi;
import com.caring.sass.nursing.api.FunctionConfigurationApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.dto.FollowUpChatDto;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.entity.Referral;
import com.caring.sass.user.util.I18nUtils;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.ConfigAdditionalApi;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.constant.WeiXinConstants;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.dto.config.SendKefuMsgForm;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.config.UploadTemporaryMatrialForm;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.entity.guide.RegGuide;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @ClassName WeixinService
 * @Description 处理 该服务中 使用 weixin 服务的业务
 * @Author yangShuai
 * @Date 2020/9/27 10:34
 * @Version 1.0
 */
@Slf4j
@Component
public class WeiXinService {

    @Autowired
    TenantApi tenantApi;

    @Autowired
    GuideApi guideApi;

    @Autowired
    ChannelContentApi channelContentApi;

    @Autowired
    FormResultApi formResultApi;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    ConfigAdditionalApi configAdditionalApi;

    @Autowired
    ParametricTextManager parametricTextManager;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    FunctionConfigurationApi functionConfigurationApi;

    @Autowired
    MsgPatientSystemMessageApi patientSystemMessageApi;

    @Autowired
    DictionaryItemService dictionaryItemService;

    @Autowired
    BusinessReminderLogControllerApi reminderLogControllerApi;

    /**
     * 查询功能配置
     * @param tenantCode
     * @return
     */
    public Boolean noSendWeiXinTemplate(String tenantCode, PlanFunctionTypeEnum planFunctionTypeEnum) {
        R<Integer> functionStatus = functionConfigurationApi.getFunctionStatus(tenantCode, planFunctionTypeEnum);
        if (functionStatus.getIsSuccess()) {
            Integer data = functionStatus.getData();
            if (data != null && data == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 发送 入组成功提示。
     * @Date 2020/9/27 16:11
     */
    public void sendJoinGroupSuccess(Patient patient) {
        if (Objects.isNull(patient)) {
            return;
        }

        Tenant tenant = tenantApi.getTenantByWxAppId(patient.getWxAppId()).getData();
        if (Objects.isNull(tenant)) {
            return;
        }
        RegGuide guide = guideApi.getGuide().getData();
        if (Objects.isNull(guide)) {
            return;
        }
        Integer successMsgType = guide.getSuccessMsgType();
        if (Objects.isNull(successMsgType)) {
            return;
        }

        if (Objects.equals(guide.getSuccessMsgType(), 1)) {
            sendTextKefuMessage(patient.getWxAppId(), patient.getOpenId(), guide.getSuccessMsg());
            return;
        }

        if (Objects.equals(guide.getSuccessMsgType(), 0)) {
            R<ChannelContent> content = channelContentApi.queryContentById(Convert.toLong(guide.getSuccessMsg()));
            if (content.getIsError() || Objects.isNull(content.getData())) {
                return;
            }
            sendMaterialMessage(tenant, patient.getOpenId(), content.getData(), content.getData().getLink());
        }


    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 发送入组引导消息
     * @Date 2020/9/27 16:11
     */
    @Async
    public void sendGuideMessage(Patient patient, Tenant tenant, String openId, boolean sendFirstFollowUpEvent ) {
        BaseContextHandler.setTenant(tenant.getCode());
        RegGuide guide = this.guideApi.getGuide().getData();
        WxMpKefuMessage.WxArticle article1 = new WxMpKefuMessage.WxArticle();
        GeneralForm generalForm = new GeneralForm();
        generalForm.setWxAppId(tenant.getWxAppId());
        // R<ConfigAdditional> additional = configAdditionalApi.getConfigAdditionalByWxAppId(generalForm);
        String articleUrl = ApplicationDomainUtil.wxPatientBaseDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()));
        article1.setUrl(articleUrl);
        article1.setPicUrl(StringUtils.isEmpty(guide.getIcon()) ? tenant.getLogo() : guide.getIcon());
        article1.setDescription(guide.getDescribe());
        parametricTextManager.init();
        parametricTextManager.addParameter("project", tenant);
        parametricTextManager.addParameter("patient", patient);
        String convertedGuide = parametricTextManager.format(guide.getGuide());
        article1.setTitle(StringUtils.isEmpty(convertedGuide) ? guide.getGuide() : convertedGuide);
        WxMpKefuMessage msg = (WxMpKefuMessage.NEWS().toUser(openId)).addArticle(article1).build();

        SendKefuMsgForm sendKefuMsgForm = new SendKefuMsgForm();
        sendKefuMsgForm.setWxAppId(tenant.getWxAppId());
        sendKefuMsgForm.setMessage(msg);
        weiXinApi.sendKefuMsg(sendKefuMsgForm);

        if (sendFirstFollowUpEvent) {
            // 发出患者关注事件。
            FollowUpChatDto followUpChatDto = new FollowUpChatDto();
            followUpChatDto.setPatientId(patient.getId());
            followUpChatDto.setTenantCode(BaseContextHandler.getTenant());
            redisTemplate.opsForList().leftPush(SaasRedisBusinessKey.PATIENT_FOLLOW_UP_EVENT, JSON.toJSONString(followUpChatDto));
        }
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 发送微信 客服消息
     * @Date 2020/9/27 15:55
     */
    @Async
    public void sendTextKefuMessage(String appId, String targetOpenId, String content) {
        WxMpKefuMessage msg = new WxMpKefuMessage();
        msg.setContent(content);
        msg.setMsgType("text");
        msg.setToUser(targetOpenId);

        SendKefuMsgForm sendKefuMsgForm = new SendKefuMsgForm();
        sendKefuMsgForm.setWxAppId(appId);
        sendKefuMsgForm.setMessage(msg);
        weiXinApi.sendKefuMsg(sendKefuMsgForm);

    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 发送微信 图文消息
     * @Date 2020/9/27 15:55
     */
    @Async
    public void sendMaterialMessage(Tenant tenant, String targetOpenId, ChannelContent content, String linkUrl) {
        BaseContextHandler.setTenant(tenant.getCode());
        WxMpKefuMessage.WxArticle article1 = new WxMpKefuMessage.WxArticle();

        GeneralForm generalForm = new GeneralForm();
        generalForm.setWxAppId(tenant.getWxAppId());

        R<ConfigAdditional> additional = configAdditionalApi.getConfigAdditionalByWxAppId(generalForm);
        String wxJsSecurityUrl = ApplicationDomainUtil.wxPatientBaseDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()));
        String cmsUrl = wxJsSecurityUrl;
        if (additional.getIsSuccess() && additional.getData() != null) {
            cmsUrl = ApplicationDomainUtil.wxPatientBizUrl(wxJsSecurityUrl, Objects.nonNull(tenant.getWxBindTime()), additional.getData().getCmsUrl());
        }

        article1.setUrl(cmsUrl);
        article1.setTitle(content.getTitle());
        article1.setPicUrl(content.getIcon());
        article1.setDescription(content.getSummary());
        JSONObject jo = new JSONObject();
        WxMpKefuMessage msg = (WxMpKefuMessage.NEWS().toUser(targetOpenId)).addArticle(article1).build();

        SendKefuMsgForm sendKefuMsgForm = new SendKefuMsgForm();
        sendKefuMsgForm.setMessage(msg);
        sendKefuMsgForm.setWxAppId(tenant.getWxAppId());
        weiXinApi.sendKefuMsg(sendKefuMsgForm);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 发送图片 客服消息
     * @Date 2020/9/27 15:56
     */
    @Async
    public void sendImgKefuMessage(String appId, String targetOpenId, String content) {
        WxMpKefuMessage msg = new WxMpKefuMessage();
        msg.setContent(content);
        msg.setMsgType("image");
        msg.setToUser(targetOpenId);
        UploadTemporaryMatrialForm uploadTemporaryMatrialForm = new UploadTemporaryMatrialForm();
        uploadTemporaryMatrialForm.setWxAppId(appId);
        uploadTemporaryMatrialForm.setFileType(WeiXinConstants.FileType.jpeg);
        uploadTemporaryMatrialForm.setMediaType(WeiXinConstants.MediaType.image);
        uploadTemporaryMatrialForm.setUrl(content);
        uploadTemporaryMatrialForm.setFileName(UUID.randomUUID().toString());
        R<WxMediaUploadResult> temporaryMatrial = this.weiXinApi.uploadTemporaryMatrial(uploadTemporaryMatrialForm);
        String mediaId = (temporaryMatrial.getData()).getMediaId();
        msg.setMediaId(mediaId);

        SendKefuMsgForm sendKefuMsgForm = new SendKefuMsgForm();
        sendKefuMsgForm.setWxAppId(appId);
        sendKefuMsgForm.setMessage(msg);
        weiXinApi.sendKefuMsg(sendKefuMsgForm);

    }


    /**
     * @return void
     * @Author yangShuai
     * @Description 发送 关键字回复
     * @Date 2020/10/17 16:14
     */
    @Async
    public void sendKeyWordKefuMessage(String appId, String targetOpenId, String mediaId, String imgUrl) {
        WxMpKefuMessage msg = new WxMpKefuMessage();
        msg.setContent(imgUrl);
        msg.setMsgType("image");
        msg.setToUser(targetOpenId);
        msg.setMediaId(mediaId);

        SendKefuMsgForm sendKefuMsgForm = new SendKefuMsgForm();
        sendKefuMsgForm.setWxAppId(appId);
        sendKefuMsgForm.setMessage(msg);
        weiXinApi.sendKefuMsg(sendKefuMsgForm);
    }


    /**
     * 发送医生审核通过的模板消息
     * @param appId
     * @param templateMessageIndefiner
     * @param targetOpenId
     */
    public void sendDoctorAuditTemplateMessage(String appId, String doctorName, String templateMessageIndefiner, String targetOpenId) {
        TemplateMsgDto templateMsgDto = getTemplateMsgDto(templateMessageIndefiner);
        if (templateMsgDto != null && !StringUtils.isEmpty(targetOpenId)) {
            List<WxMpTemplateData> mpTemplateData;
            if (templateMsgDto.getCommonCategory()) {
                mpTemplateData = CommonTemplateServiceWorkModel.buildWxMpTemplateData(doctorName, "申请账号成功");
            } else {
                mpTemplateData = DoctorAuditMessageTemplate.consultationForReaction
                        .get(TemplateMessageIndefiner.DOCTOR_AUDIT_NOTICE)
                        .init();
            }
            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
            wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
            wxMpTemplateMessage.setData(mpTemplateData);
            SendTemplateMessageForm form = new SendTemplateMessageForm();
            form.setWxAppId(appId);
            wxMpTemplateMessage.setToUser(targetOpenId);
            form.setTemplateMessage(wxMpTemplateMessage);
            this.weiXinApi.sendTemplateMessage(form);
        }
    }

    /**
     * 发送会诊消息
     *
     * @param appId
     * @param consultationName
     * @param templateMessageIndefiner
     * @param targetOpenId
     * @param url
     */
    public void sendConsultationTemplateMessage(String appId, String consultationName, String templateMessageIndefiner, String targetOpenId, String url, String userName) {
        TemplateMsgDto templateMsgDto = getTemplateMsgDto(templateMessageIndefiner);
        if (templateMsgDto != null && !StringUtils.isEmpty(targetOpenId)) {
            Boolean commonCategory = templateMsgDto.getCommonCategory();
            List<WxMpTemplateData> mpTemplateData = ConsultationMessageTemplate.consultationForReaction.get(templateMessageIndefiner).init(consultationName, userName, commonCategory);
            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
            wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
            wxMpTemplateMessage.setData(mpTemplateData);
            SendTemplateMessageForm form = new SendTemplateMessageForm();
            form.setWxAppId(appId);
            wxMpTemplateMessage.setToUser(targetOpenId);
            wxMpTemplateMessage.setUrl(url);
            form.setTemplateMessage(wxMpTemplateMessage);
            this.weiXinApi.sendTemplateMessage(form);
        }
    }


    /**
     * 发送会诊消息
     *
     * @param appId
     * @param consultationName
     * @param templateMessageIndefiner
     * @param targetOpenId
     * @param url
     */
    public void sendConsultationTemplateInviteMessage(String appId, String consultationName, String templateMessageIndefiner,
                                                      String targetOpenId, String url, String userName) {
        TemplateMsgDto templateMsgDto = getTemplateMsgDto(templateMessageIndefiner);
        if (templateMsgDto != null && !StringUtils.isEmpty(targetOpenId)) {
            Boolean commonCategory = templateMsgDto.getCommonCategory();
            List<WxMpTemplateData> mpTemplateData = new ConsultationMessageTemplate.ConsultationInviteParams().init(consultationName, userName, commonCategory);
            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
            wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
            wxMpTemplateMessage.setData(mpTemplateData);
            SendTemplateMessageForm form = new SendTemplateMessageForm();
            form.setWxAppId(appId);
            wxMpTemplateMessage.setUrl(url);
            form.setTemplateMessage(wxMpTemplateMessage);
            wxMpTemplateMessage.setToUser(targetOpenId);
            this.weiXinApi.sendTemplateMessage(form);
        }
    }


    /**
     * 获取字典的翻译
     * @return
     */
    private Map<String, String> getDictItem() {
        List<DictionaryItem> dictionaryItems = dictionaryItemService.list(Wraps.lbQ());
        Map<String, String> dictionaryMap = new HashMap<>();
        for (DictionaryItem item : dictionaryItems) {
            dictionaryMap.put(item.getCode(), item.getName());
        }
        return dictionaryMap;
    }

    private static final String DOCTOR = "doctor";

    /**
     * 发送转诊卡
     *
     * @param referralCategory 转诊性质
     * @param launchDoctorName 推荐医生姓名
     * @param referral        转诊信息
     */
    public void sendReferral(String referralCategory, String launchDoctorName, Referral referral, String openId, String patientName, Long patientId, String acceptDoctorDepartment, String mobile) {
        String tenantCode = BaseContextHandler.getTenant();
        R<Integer> functionStatus = functionConfigurationApi.getFunctionStatus(tenantCode, PlanFunctionTypeEnum.REFERRAL_SERVICE);
        if (functionStatus.getIsSuccess()) {
            Integer data = functionStatus.getData();
            if (data != null && data == 0) {
                log.info("send referral message，but {} referral manager is close", tenantCode);
                return;
            }
        }
        R<Tenant> tR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tR.getData();
        boolean b = tR.getIsError() || Objects.isNull(tenant);
        if (b) {
            log.error("查询项目详细失败");
            return;
        }
        String url = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), String.format(H5Router.REFERRAL_CARD_URL, referral.getId()));
        MsgPatientSystemMessageSaveDTO messageSaveDTO = new MsgPatientSystemMessageSaveDTO(PlanFunctionTypeEnum.REFERRAL_SERVICE.getCode(),
                referral.getId(),
                url, patientId, LocalDateTime.now(), launchDoctorName, tenantCode);
        messageSaveDTO.createPushContent(null, null, null);
        patientSystemMessageApi.saveSystemMessage(messageSaveDTO);

        TenantDiseasesTypeEnum diseasesType = tenant.getDiseasesType();
        if (tenant.isCertificationServiceNumber()) {
            TemplateMsgDto t = getTemplateMsgDto(TemplateMessageIndefiner.REFERRAL_CARD);
            if (Objects.isNull(t)) {
                log.error("微信未配置转诊卡模板消息");
                return;
            }
            List<WxMpTemplateData> templateData = new ArrayList<>();
            if (t.getCommonCategory()) {
                templateData = CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientName, I18nUtils.getMessage(CommonTemplateServiceWorkModel.REFERRAL_CARD));
            } else {
                // first 字段配置颜色属性 微信推送不生效
                templateData.add(new WxMpTemplateData("keyword1", referralCategory));
                templateData.add(new WxMpTemplateData("keyword2", DateUtil.now()));
            }
            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage(
                    openId, t.getTemplateId(), url, null, null, templateData
            );

            SendTemplateMessageForm to = new SendTemplateMessageForm();
            to.setWxAppId(tenant.getWxAppId());
            to.setTemplateMessage(wxMpTemplateMessage);
            weiXinApi.sendTemplateMessage(to);
        } else {
            Map<String, String> dictItem = getDictItem();
            String role = dictItem.get(DOCTOR);
            // 个人服务号。给患者发送一条通知短信
            String smsParams = BusinessReminderType.getPatientReferralNotice(tenant.getName(), referral.getLaunchDoctorName(), role, acceptDoctorDepartment, referral.getAcceptDoctorName(), role);
            if (StrUtil.isEmpty(mobile)) {
                return;
            }
            BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                    .mobile(mobile)
                    .wechatRedirectUrl(url)
                    .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                    .type(BusinessReminderType.PATIENT_REFERRAL_NOTICE)
                    .tenantCode(tenantCode)
                    .queryParams(smsParams)
                    .patientId(patientId)
                    .status(0)
                    .openThisMessage(0)
                    .finishThisCheckIn(0)
                    .build();

             reminderLogControllerApi.sendNoticeSms(logSaveDTO);

        }


    }

    /**
     * 患者关注后发送提醒
     * @param targetOpenId
     * @param tenantCode
     * @param firstTitle
     * @param keyword1Title
     */
    public void sendFollowUpMessage(String targetOpenId, String tenantCode, String firstTitle, String keyword1Title, String serviceType, Long patientId, KeyWordEnum keyWordEnum) {
        // TODO: 待处理
        TemplateMsgDto templateMsgDto = getTemplateMsgDto(TemplateMessageIndefiner.CONSULTATION_RESPONSE);
        if (templateMsgDto != null) {
            R<Tenant> tenantByCode = tenantApi.getByCode(tenantCode);
            if (tenantByCode.getIsSuccess()) {
                Tenant tenant = tenantByCode.getData();
                if (Objects.nonNull(tenant)) {
                    String wxAppId = tenant.getWxAppId();
                    Boolean commonCategory = templateMsgDto.getCommonCategory();
                    List<WxMpTemplateData> dataList = new ArrayList();
                    if (commonCategory) {
                        dataList = CommonTemplateServiceWorkModel.buildWxMpTemplateData(I18nUtils.getMessageByTenantDefault("RespectedUser", tenant.getDefaultLanguage()), serviceType);
                    } else {
                        dataList.add(new WxMpTemplateData("keyword1", keyword1Title));
                        dataList.add(new WxMpTemplateData("keyword2", DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)));
                    }
                    String bizUrl = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CHAT);
                    String templateId = templateMsgDto.getTemplateId();
                    SendTemplateMessageForm form = new SendTemplateMessageForm();
                    form.setWxAppId(wxAppId);
                    WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
                    wxMpTemplateMessage.setToUser(targetOpenId);
                    wxMpTemplateMessage.setUrl(bizUrl);
                    wxMpTemplateMessage.setData(dataList);
                    wxMpTemplateMessage.setTemplateId(templateId);
                    form.setTemplateMessage(wxMpTemplateMessage);
                    MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(keyWordEnum.toString(), null, bizUrl, patientId, LocalDateTime.now(), "系统", tenant.getCode());
                    systemMessageSaveDTO.createPushContent(null, null, null);
                    patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);
                    this.weiXinApi.sendTemplateMessage(form);
                }
            }
        }

    }
    public void sendConsultationResponse(String appId, String targetOpenId, String imSendUserName, String url, String myName, String defaultLanguage) {
        TemplateMsgDto templateMsgDto = getTemplateMsgDto(TemplateMessageIndefiner.CONSULTATION_RESPONSE);
        if (templateMsgDto != null && !StringUtils.isEmpty(targetOpenId)) {
            Boolean commonCategory = templateMsgDto.getCommonCategory();
            WxMpTemplateMessage wxMpTemplateMessage;
            if (commonCategory) {
                wxMpTemplateMessage = new WxMpTemplateMessage();
                List<WxMpTemplateData> templateData = CommonTemplateServiceWorkModel.buildWxMpTemplateData(myName, I18nUtils.getMessageByTenantDefault("OA_YOU", defaultLanguage, imSendUserName)); // imSendUserName + "@了你"
                wxMpTemplateMessage.setData(templateData);
            } else {
                Map<String, Object> params = new HashMap<>();
                String first = "first";
                params.put(first, "");
                String keyword1 = "keyword1";
                params.put(keyword1, I18nUtils.getMessageByTenantDefault("OA_YOU", defaultLanguage, imSendUserName));
                wxMpTemplateMessage = setField(templateMsgDto, params, null);
            }

            SendTemplateMessageForm form = new SendTemplateMessageForm();
            form.setWxAppId(appId);
            wxMpTemplateMessage.setToUser(targetOpenId);
            wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
            wxMpTemplateMessage.setUrl(url);
            form.setTemplateMessage(wxMpTemplateMessage);
            this.weiXinApi.sendTemplateMessage(form);
        }

    }

    /**
     * 对于群发。 先吧使用的模版查询出来。
     * @param templateMessageIndefiner
     */
    public TemplateMsgDto getCurrentSendUseTemplate(Boolean isCertificationServiceNumber, String templateMessageIndefiner) {
        if (isCertificationServiceNumber) {
            TemplateMsgDto templateMsgDto = null;
            if (StrUtil.isNotEmpty(templateMessageIndefiner)) {
                templateMsgDto = getTemplateMsgDto(templateMessageIndefiner);
            }
            if (Objects.isNull(templateMsgDto) || templateMsgDto.getCommonCategory()) {
                templateMsgDto = getTemplateMsgDto(TemplateMessageIndefiner.CONSULTATION_RESPONSE);
            }
            return templateMsgDto;
        } else {
            return null;
        }

    }

    /**
     * 医助群发时。给患者推送微信公众号模版消息
     */
    public void sendConsultationResponseMore(String appId, String targetOpenId, MediaType chatType, String chatContent,
                                         String patientName, String url, Long patientId, TemplateMsgDto templateMsgDto, String defaultLanguage) {

        if (StringUtils.isEmpty(targetOpenId)) {
            return;
        }
        if (Objects.isNull(templateMsgDto)) {
            return;
        }
        String serviceType = I18nUtils.getMessageByTenantDefault("MESSAGE_REMINDER", defaultLanguage); // "消息提醒";
        // 使用服务工单
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        if (templateMsgDto.getCommonCategory()) {
            List<WxMpTemplateData> templateData = CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientName, serviceType);
            wxMpTemplateMessage.setData(templateData);
            wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
        } else {
            Map<String, Object> params = new HashMap<>();
            String keyword1 = "keyword1";
            params.put(keyword1, getImContentByType(chatType, chatContent));
            if (StrUtil.isNotEmpty(patientName)) {
                params.put("patientName", patientName);
            } else {
                params.put("patientName", getImContentByType(chatType, chatContent));
            }
            wxMpTemplateMessage = setField(templateMsgDto, params, patientId);
        }
        SendTemplateMessageForm form = new SendTemplateMessageForm();
        form.setWxAppId(appId);
        wxMpTemplateMessage.setToUser(targetOpenId);
        wxMpTemplateMessage.setUrl(url);
        form.setTemplateMessage(wxMpTemplateMessage);
        this.weiXinApi.sendTemplateMessage(form);
    }



    /**
     * 根据枚举类型。返回消息提醒时的提醒内容
     * @param chatType
     * @param content
     * @return
     */
    public static String getImContentByType(MediaType chatType, String content) {
        switch (chatType) {
            case text: {
                break;
            }
            case cms: {
                content = I18nUtils.getMessage("chatCms"); //"您收到一篇文章"
                break;
            }
            case remind: {
                content = I18nUtils.getMessage("chatRemind"); // "您收到一条提醒";
                break;
            }
            case file: {
                content = I18nUtils.getMessage("chatFile"); // "您收到一个文件";
                break;
            }
            case image: {
                content = I18nUtils.getMessage("chatImage"); // "您收到一张图片";
                break;
            }
            case voice: {
                content = I18nUtils.getMessage("chatVoice"); // "您收到一条语音";
                break;
            }
            case video: {
                content = I18nUtils.getMessage("chatVideo"); // "您收到一个视频消息";
                break;
            }
            default: {
                content = I18nUtils.getMessage("chatOther"); // "您收到一条消息";
            }
        }
        return content;
    }


    /**
     * @return
     * @Author yangShuai
     * @Description 发送咨询回复
     * @Date 2020/11/12 15:10
     */
    public void sendConsultationResponse(String appId, String targetOpenId, MediaType chatType, String chatContent,
                                         String patientName, String url, Long patientId, String templateMessageIndefiner, String tenantLanguage) {
        if (StringUtils.isEmpty(targetOpenId)) {
            return;
        }
        TemplateMsgDto templateMsgDto = null;
        String serviceType = I18nUtils.getMessageByTenantDefault("MESSAGE_REMINDER", tenantLanguage);
        if (StrUtil.isNotEmpty(templateMessageIndefiner)) {
            templateMsgDto = getTemplateMsgDto(templateMessageIndefiner);
            // templateMessageIndefiner 不为空。那接收人是医生。 需要判断是 患者发给医生，还是医助或其他医生发给医生的。 确定服务类型
            if (TemplateMessageIndefiner.CONSULTATION_RESPONSE_NURSING.equals(templateMessageIndefiner)) {
                serviceType = I18nUtils.getMessageByTenantDefault("MEDICAL_ASSISTANT_REPLY", tenantLanguage); // "医助回复提醒";
            } else if (TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT.equals(templateMessageIndefiner)) {
                serviceType = I18nUtils.getMessageByTenantDefault("PATIENT_MESSAGE_REMINDER", tenantLanguage); // "患者消息提醒";
            }
        }
        if (Objects.isNull(templateMsgDto) || templateMsgDto.getCommonCategory()) {
            templateMsgDto = getTemplateMsgDto(TemplateMessageIndefiner.CONSULTATION_RESPONSE);
        }
        if (Objects.isNull(templateMsgDto)) {
            return;
        }
        // 使用服务工单
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        if (templateMsgDto.getCommonCategory()) {
            List<WxMpTemplateData> templateData = CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientName, serviceType);
            wxMpTemplateMessage.setData(templateData);
            wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
        } else {
            Map<String, Object> params = new HashMap<>();
            String keyword1 = "keyword1";
            params.put(keyword1, getImContentByType(chatType, chatContent));
            if (StrUtil.isNotEmpty(patientName)) {
                params.put("patientName", patientName);
            } else {
                params.put("patientName", getImContentByType(chatType, chatContent));
            }
            wxMpTemplateMessage = setField(templateMsgDto, params, patientId);
        }
        SendTemplateMessageForm form = new SendTemplateMessageForm();
        form.setWxAppId(appId);
        wxMpTemplateMessage.setToUser(targetOpenId);
        wxMpTemplateMessage.setUrl(url);
        form.setTemplateMessage(wxMpTemplateMessage);
        this.weiXinApi.sendTemplateMessage(form);
    }

    public void getFormResultFields(List<FormFieldDto> fieldDtos, FormResult formResult) {
        // 查询 患者填写的 基本消息和疾病信息 表单。
        if (Objects.nonNull(formResult)) {
            String jsonContent = formResult.getJsonContent();
            if (StrUtil.isEmpty(jsonContent)) {
                return;
            }
            List<FormFieldDto> formFields = JSON.parseArray(formResult.getJsonContent(), FormFieldDto.class);
            if (CollUtil.isEmpty(formFields)) {
                return;
            }
            fieldDtos.addAll(formFields);
        }

    }

    public WxMpTemplateMessage setField(TemplateMsgDto templateMsgDto, Map<String, Object> params, Long patientId) {
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        if (templateMsgDto != null) {
            ParametricTextManager parametricTextManager = SpringUtils.getBean(ParametricTextManager.class);
            List<WxMpTemplateData> dataList = new ArrayList();
            List<TemplateMsgFields> msgDtoFields = templateMsgDto.getFields();
            for (Map.Entry entry : params.entrySet()) {
                parametricTextManager.addParameter((String) entry.getKey(), entry.getValue());
            }

            boolean needForm = false;
            for (TemplateMsgFields field : msgDtoFields) {
                if (field.getType() != null && field.getType() == 1) {
                    needForm = true;
                    break;
                }
            }
            List<FormFieldDto> allFields = new ArrayList<>(50);
            if (needForm && Objects.nonNull(patientId)) {
                R<FormResult> category = formResultApi.getFormResultByCategory(patientId, FormEnum.BASE_INFO);
                if (category.getIsSuccess()) {
                    getFormResultFields(allFields, category.getData());
                }
                category = formResultApi.getFormResultByCategory(patientId, FormEnum.HEALTH_RECORD);
                if (category.getIsSuccess()) {
                    getFormResultFields(allFields, category.getData());
                }
            }
            for (TemplateMsgFields fields : msgDtoFields) {
                if (fields.getType() != null && fields.getType() == 1) {
                    StringBuilder sb = new StringBuilder();
                    // 将 模板中 关联表单属性的 数据 从表单中拉取要填充的结果。
                    for (FormFieldDto allField : allFields) {
                        if (allField.getId() != null && allField.getId().equals(fields.getValue())) {
                            // 从表单中获取用户填写的结果。 并设置到推送模板的值中
                            String values = allField.parseResultValues();
                            sb.append(values);
                        }
                    }
                    // 添加一条
                    dataList.add(new WxMpTemplateData(fields.getAttr(), sb.toString(), fields.getColor()));
                } else if (fields.getType()!= null && fields.getType() == 2 && StrUtil.isEmpty(fields.getValue())) {
                    dataList.add(new WxMpTemplateData(fields.getAttr(), DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_TIME_FORMAT), fields.getColor()));
                } else if ( fields.getValue() != null && fields.getValue().contains("${")) {
                    String str = parametricTextManager.format(fields.getValue());
                    dataList.add(new WxMpTemplateData(fields.getAttr(), str, fields.getColor()));
                } else {
                    dataList.add(new WxMpTemplateData(fields.getAttr(), fields.getValue(), fields.getColor()));
                }
            }
            wxMpTemplateMessage.setData(dataList);
            wxMpTemplateMessage.setTemplateId(templateMsgDto.getTemplateId());
            ParametricTextManager.remove();
            return wxMpTemplateMessage;
        }
        return null;
    }

    /**
     * 需要处理他的模板内容
     * TODO
     * @param indefiner
     * @return
     */
    public static TemplateMsgDto getTemplateMsgDto(String indefiner) {
        TemplateMsgApi templateMsgApi = SpringUtils.getBean(TemplateMsgApi.class);
        R<TemplateMsgDto> msgApiByIndefiner = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(BaseContextHandler.getTenant(), null, indefiner);
        if (msgApiByIndefiner.getIsSuccess() != null && msgApiByIndefiner.getIsSuccess() && msgApiByIndefiner.getData() != null) {
            return msgApiByIndefiner.getData();
        }
        return null;
    }


}
