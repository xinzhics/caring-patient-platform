package com.caring.sass.nursing.service.information.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.SendType;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.information.CompletenessInformationMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.information.CompletenessInformationPatientEditDto;
import com.caring.sass.nursing.dto.information.PatientInformationFieldPatientEditDto;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.information.*;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.qo.CompletenessInformationQo;
import com.caring.sass.nursing.qo.IntervalQo;
import com.caring.sass.nursing.qo.ReminderQo;
import com.caring.sass.nursing.service.form.FormResultService;
import com.caring.sass.nursing.service.information.*;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.nursing.vo.NotificaVo;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者信息完整度概览表
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service
@AllArgsConstructor
public class CompletenessInformationServiceImpl
        extends SuperServiceImpl<CompletenessInformationMapper, CompletenessInformation> implements CompletenessInformationService {

    @Autowired
    WeiXinApi weiXinApi;
    @Autowired
    PatientApi patientApi;
    @Autowired
    TemplateMsgApi templateApi;
    @Autowired
    ManagementHistoryService managementHistoryService;
    @Autowired
    ManagementHistoryDetailService managementHistoryDetailService;
    @Autowired
    InformationMonitoringIntervalService intervalService;
    @Autowired
    PatientInformationFieldService patientInformationFieldService;

    @Autowired
    NursingStaffApi nursingStaffApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    FormResultService formResultService;

    @Autowired
    FormMapper formMapper;

    @Autowired
    MsgPatientSystemMessageApi patientSystemMessageApi;

    @Autowired
    BusinessReminderLogControllerApi businessReminderLogControllerApi;

    @Override
    public Integer selectCount(Long nursingId) {

        LbqWrapper<CompletenessInformation> wrapper = Wraps.<CompletenessInformation>lbQ()
                .eq(CompletenessInformation::getComplete, 0)
                .apply(" patient_id in ( " + " SELECT id from u_user_patient where service_advisor_id = '" + nursingId + "' " + " ) ");
        Integer integer = baseMapper.selectCount(wrapper);
        if (integer == null) {
            return 0;
        }
        return integer;

    }

    @Override
    public IPage<CompletenessInformation> selectList(PageParams<CompletenessInformationQo> params) {
        IPage<CompletenessInformation> iPage = params.buildPage();
        CompletenessInformationQo model = params.getModel();
        LbqWrapper<CompletenessInformation> wrapper = Wraps.<CompletenessInformation>lbQ();
        if (Objects.nonNull(model.getIntervalId())) {
            InformationMonitoringInterval interval = intervalService.getById(model.getIntervalId());
            if (Objects.nonNull(interval)) {
                Double minValue = interval.getMinValue();
                Double maxValue = interval.getMaxValue();
                if (Objects.nonNull(minValue)) {
                    wrapper.ge(CompletenessInformation::getCompletion, minValue.intValue());
                }
                if (Objects.nonNull(maxValue)) {
                    wrapper.le(CompletenessInformation::getCompletion, maxValue.intValue());
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean applySql = false;
        if (Objects.nonNull(model.getNursingId())) {
            applySql = true;
            stringBuilder.append(" SELECT id from u_user_patient where service_advisor_id = '").append(model.getNursingId()).append("' ");
        }
        if (StrUtil.isNotEmpty(model.getName())) {
            if (applySql) {
                stringBuilder.append(" and name like '%").append(model.getName()).append("%'");
            } else {
                applySql = true;
                stringBuilder.append(" SELECT id from u_user_patient where name like '%").append(model.getName()).append("%'");
            }
        }
        if (applySql) {
            wrapper.apply(" patient_id in ( " + stringBuilder.toString() + " ) ", (Object) null);
        }
        wrapper.orderByAsc(CompletenessInformation::getCompletion);
        baseMapper.selectPage(iPage, wrapper);
        List<CompletenessInformation> records = iPage.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> patientIds = records.stream().map(CompletenessInformation::getPatientId).collect(Collectors.toList());
            R<List<Patient>> patientIdsList = patientApi.listByIds(patientIds);
            if (patientIdsList.getIsSuccess()) {
                List<Patient> patients = patientIdsList.getData();
                if (CollUtil.isNotEmpty(patients)) {
                    Map<Long, Patient> patientMap = patients.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
                    for (CompletenessInformation record : records) {
                        Patient patient = patientMap.get(record.getPatientId());
                        if (StrUtil.isNotEmpty(patient.getRemark())) {
                            record.setPatientName(patient.getName() + " (" + patient.getRemark()+ ")");
                        } else {
                            record.setPatientName(patient.getName());
                        }
                        record.setAvatar(patient.getAvatar());
                        record.setDoctorId(patient.getDoctorId());
                        record.setDoctorName(patient.getDoctorName());
                    }
                }
            }
        }
        return iPage;
    }

    /**
     * 患者信息完整度信息详情
     *
     * @param id 完整度概率ID
     * @return
     */
    @Override
    public CompletenessInformation getById(Serializable id) {
        CompletenessInformation information = this.baseMapper.selectById(id);
        if (null != information) {
            LbqWrapper<PatientInformationField> lbqWrapper = Wraps.<PatientInformationField>lbQ().eq(PatientInformationField::getInformationId, id)
                    .orderByAsc(PatientInformationField::getFieldSort);
            List<PatientInformationField> fieldList = patientInformationFieldService.list(lbqWrapper);
            if (!CollectionUtils.isEmpty(fieldList)) {
                information.setPatientInformationFields(fieldList);
            }
            R<Patient> patientR = patientApi.get(information.getPatientId());
            if (patientR.getIsSuccess()) {
                Patient patient = patientR.getData();
                information.setPatientName(patient.getName());
                information.setAvatar(patient.getAvatar());
                information.setDoctorId(patient.getDoctorId());
                information.setDoctorName(patient.getDoctorName());
            }
        }
        return information;
    }


    /**
     * 患者删除时。 删除患者的 信息完整度
     *
     * @param patientId
     */
    @Override
    public void removeByPatientId(long patientId) {

        List<CompletenessInformation> informationList = baseMapper.selectList(Wraps.<CompletenessInformation>lbQ()
                .eq(CompletenessInformation::getPatientId, patientId));
        if (CollUtil.isNotEmpty(informationList)) {
            List<Long> collect = informationList.stream().map(SuperEntity::getId).collect(Collectors.toList());
            patientInformationFieldService.remove(Wraps.<PatientInformationField>lbQ()
                    .in(PatientInformationField::getInformationId, collect));
            baseMapper.deleteBatchIds(collect);
        }
    }

    @Override
    public CompletenessInformation findIncompleteInformation(Long patientId, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<CompletenessInformation> lbqInformationWrapper = new LbqWrapper();
        lbqInformationWrapper.eq(CompletenessInformation::getPatientId,patientId);
        CompletenessInformation information = this.baseMapper.selectOne(lbqInformationWrapper);
        if (information==null){
            log.error("患者【"+patientId+"】信息完整度不存在！");
            return information;
        }
        LbqWrapper<PatientInformationField> lbqWrapper = Wraps.<PatientInformationField>lbQ()
                .eq(PatientInformationField::getInformationId, information.getId())
                .eq(PatientInformationField::getFieldWrite, 0)
                .orderByAsc(PatientInformationField::getFieldSort);
        List<PatientInformationField> fieldList = patientInformationFieldService.list(lbqWrapper);
        if (!CollectionUtils.isEmpty(fieldList)) {
            information.setIncompleteInformationFields(fieldList);
        }
        R<Patient> patientR = patientApi.get(information.getPatientId());
        if (patientR.getIsSuccess() && !ObjectUtils.isEmpty(patientR.getData())) {
            information.setPatientName(patientR.getData().getName());
            information.setAvatar(patientR.getData().getAvatar());
            information.setDoctorId(patientR.getData().getDoctorId());
            information.setDoctorName(patientR.getData().getDoctorName());
        }
        return information;
    }

    /**
     * 汇总所有需要发送的患者 。进行发送
     *
     * @param nursingId
     * @param managementHistory
     * @param intervals
     * @param msgDto
     */
    public void sendMessageByInterval(Long nursingId,
                                      ManagementHistory managementHistory,
                                      List<InformationMonitoringInterval> intervals,
                                      TemplateMsgDto msgDto) {


        String tenantCode = BaseContextHandler.getTenant();
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        final String weixinAppId = tenant.getWxAppId();
        LbqWrapper<CompletenessInformation> wrapper;
        Long managementHistoryId = managementHistory.getId();
        if (CollUtil.isNotEmpty(intervals)) {
            for (InformationMonitoringInterval interval : intervals) {
                wrapper = Wraps.<CompletenessInformation>lbQ();
                wrapper.apply(" patient_id in (  SELECT id from u_user_patient where service_advisor_id = '" + nursingId + "') ");
                wrapper.eq(CompletenessInformation::getComplete, 0);
                Double minValue = interval.getMinValue();
                Double maxValue = interval.getMaxValue();
                if (Objects.nonNull(minValue)) {
                    wrapper.ge(CompletenessInformation::getCompletion, minValue);
                }
                if (Objects.nonNull(maxValue)) {
                    wrapper.le(CompletenessInformation::getCompletion, maxValue);
                }
                moreSend(wrapper, managementHistory, msgDto, weixinAppId, tenant);
            }
        } else {
            wrapper = Wraps.<CompletenessInformation>lbQ();
            wrapper.eq(CompletenessInformation::getComplete, 0);
            wrapper.apply(" patient_id in (  SELECT id from u_user_patient where service_advisor_id = '" + nursingId + "') ");
            moreSend(wrapper, managementHistory, msgDto, weixinAppId, tenant);
        }

        //将患者数量更新到 管理历史中去
        Object o = redisTemplate.boundHashOps(SaasRedisBusinessKey.PATIENT_INFO_MANAGEMENT_HISTORY).get(managementHistoryId.toString());
        redisTemplate.boundHashOps(SaasRedisBusinessKey.PATIENT_INFO_MANAGEMENT_HISTORY).delete(managementHistoryId.toString());
        if (Objects.nonNull(o)) {
            try {
                managementHistory.setPatientNumber(Integer.parseInt(o.toString()));
            } catch (Exception e) {
                log.info(" history send message number parse error ");
                e.printStackTrace();
            }
        }
        managementHistoryService.updateById(managementHistory);
    }

    /**
     * 群发
     *
     * @param wrapper
     * @param managementHistory
     * @param msgDto
     * @param weixinAppId
     */
    public void moreSend(LbqWrapper<CompletenessInformation> wrapper, ManagementHistory managementHistory, TemplateMsgDto msgDto, String weixinAppId, Tenant tenant) {
        IPage<CompletenessInformation> page;
        int current = 1;
        Long managementHistoryId = managementHistory.getId();
        final String finalDomainName = tenant.getDomainName();
        final Boolean thirdAuthorization = Objects.nonNull(tenant.getWxBindTime());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = now.plusDays(-1);
        Locale locale = LocaleContextHolder.getLocale();
        String roleName = dictionaryItemService.findDictionaryItemName(DictionaryItemService.DOCTOR);
        while (true) {
            page = new Page<>(current, 100L);
            IPage<CompletenessInformation> informationIPage = this.page(page, wrapper);
            current++;
            List<CompletenessInformation> records = informationIPage.getRecords();
            // 退出循环
            boolean lastPage = false;
            if (CollUtil.isEmpty(records)) {
                lastPage = true;
            }
            if (current > informationIPage.getPages()) {
                lastPage = true;
            }
            // 通过患者ID和创建时间。过滤掉24小时内刚提示过的用户
            List<Long> patientIds = records.stream().map(CompletenessInformation::getPatientId).collect(Collectors.toList());
            List<ManagementHistoryDetail> list = managementHistoryDetailService.list(Wraps.<ManagementHistoryDetail>lbQ()
                    .select(SuperEntity::getId, ManagementHistoryDetail::getPatientId)
                    .in(ManagementHistoryDetail::getPatientId, patientIds)
                    .ge(SuperEntity::getCreateTime, dateTime));
            Set<Long> remindedWithin24Hours = list.stream().map(ManagementHistoryDetail::getPatientId).collect(Collectors.toSet());

            List<CompletenessInformation> sendMessagePatient = new ArrayList<>(70);
            for (CompletenessInformation record : records) {
                if (remindedWithin24Hours.contains(record.getPatientId())) {
                    continue;
                }
                sendMessagePatient.add(record);
            }
            if (!sendMessagePatient.isEmpty()) {
                // 记录一下发送给多少人
                redisTemplate.boundHashOps(SaasRedisBusinessKey.PATIENT_INFO_MANAGEMENT_HISTORY)
                        .increment(managementHistoryId.toString(), sendMessagePatient.size());
                SaasGlobalThreadPool.execute(() -> sendMessage(tenant, finalDomainName, sendMessagePatient, msgDto, managementHistory, thirdAuthorization, weixinAppId, locale, roleName));
            }
            if (lastPage) {
                break;
            }
        }
    }

    /**
     * 群发消息。并记录
     * @param finalDomainName
     * @param records
     * @param msgDto
     * @param managementHistory
     * @param appId
     */
    public void sendMessage(Tenant tenant,
                            String finalDomainName, List<CompletenessInformation> records,
                            TemplateMsgDto msgDto,
                            ManagementHistory managementHistory,
                            Boolean thirdAuthorization,
                            String appId,
                            Locale locale, String roleName) {
        BaseContextHandler.setTenant(tenant.getCode());
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<Long> longList = records.stream().map(CompletenessInformation::getPatientId).collect(Collectors.toList());
        List<ManagementHistoryDetail> details = new ArrayList<>(records.size());
        Long historyId = managementHistory.getId();
        if (CollUtil.isNotEmpty(longList)) {
            NursingPlanPatientDTO patientDTO = new NursingPlanPatientDTO(longList, tenant.getCode());
            R<List<NursingPlanPatientBaseInfoDTO>> patientApiBaseInfoForNursingPlan = patientApi.getBaseInfoForNursingPlan(patientDTO);
            if (patientApiBaseInfoForNursingPlan.getIsSuccess()) {
                List<NursingPlanPatientBaseInfoDTO> baseInfoForNursingPlanData = patientApiBaseInfoForNursingPlan.getData();
                if (CollUtil.isNotEmpty(baseInfoForNursingPlanData)) {
                    if (tenant.isCertificationServiceNumber()) {
                        for (NursingPlanPatientBaseInfoDTO baseInfoDTO : baseInfoForNursingPlanData) {
                            SendTemplateMessageForm messageForm = buildTemplate(msgDto, finalDomainName, baseInfoDTO.getId(), BaseContextHandler.getTenant(),
                                    thirdAuthorization, baseInfoDTO.getOpenId(), appId, baseInfoDTO.getName(), baseInfoDTO.getDoctorName(), locale);
                            // 由于业务中没设计是否要记录发送成功问题。
                            try {
                                weiXinApi.sendTemplateMessage(messageForm);
                            } catch (Exception e) {
                                log.info("weixin send Template Message error");
                            }
                            details.add(ManagementHistoryDetail.builder().historyId(historyId).doctorId(baseInfoDTO.getDoctorId()).patientId(baseInfoDTO.getId()).build());
                        }
                    } else {
                        for (NursingPlanPatientBaseInfoDTO baseInfoDTO : baseInfoForNursingPlanData) {
                            sendSms(baseInfoDTO.getMobile(), baseInfoDTO.getId(), tenant.getCode(), tenant.getDiseasesType(), tenant.getName(), tenant.getDomainName(), baseInfoDTO.getDoctorName(), roleName);
                            details.add(ManagementHistoryDetail.builder().historyId(historyId).doctorId(baseInfoDTO.getDoctorId()).patientId(baseInfoDTO.getId()).build());
                        }
                    }

                }
            }
        }
        if (CollUtil.isNotEmpty(details)) {
            managementHistoryDetailService.saveBatch(details);
        }
    }


    @Override
    public R<String> sendMessageReminder(ReminderQo reminder) {
        // 先查询到信息未填写完整的患者，然后判断是否需要发送，分页查询
        List<Long> intervalIds = reminder.getIntervalId();
        //
        R<NursingStaff> nursingStaffR = nursingStaffApi.get(reminder.getNursingId());
        NursingStaff data = nursingStaffR.getData();
        if (Objects.isNull(data)) {
            return R.fail("医助不存在");
        }
        // 新建一条管理历史。
        ManagementHistory managementHistory = ManagementHistory.builder()
                .historyType("信息完整度")
                .nursingId(reminder.getNursingId())
                .orgId(data.getOrganId())
                .sendType(SendType.MASS_MESSAGE.getType())
                .patientNumber(0)
                .build();
        managementHistoryService.save(managementHistory);

        // 记录一共给多少个患者发送了信息
        redisTemplate.boundHashOps(SaasRedisBusinessKey.PATIENT_INFO_MANAGEMENT_HISTORY)
                .increment(managementHistory.getId().toString(), 0);

        // 准备一下发送的 消息模板
        R<TemplateMsgDto> result = templateApi.getCommonCategoryServiceWorkOrderMsg(BaseContextHandler.getTenant(), null, TemplateMessageIndefiner.COMPLETENESS_INFORMATION);
        TemplateMsgDto msgDto = result.getData();
        if (result.getIsSuccess() == null || msgDto == null) {
            return R.success("error");
        }

        if (CollUtil.isEmpty(intervalIds)) {
            // 说明是对专员下所有的这个 患者进行发送
            sendMessageByInterval(reminder.getNursingId(), managementHistory, null, msgDto);
        } else {
            // 说明只发送 区间内的
            List<InformationMonitoringInterval> list = intervalService.listByIds(intervalIds);
            if (CollUtil.isNotEmpty(list)) {
                sendMessageByInterval(reminder.getNursingId(), managementHistory, list, msgDto);
            }
        }
        return R.success("success");
    }

    @Override
    public R<String> sendOne(Long patientId) {
        LbqWrapper<CompletenessInformation> wrapper = Wraps.<CompletenessInformation>lbQ()
                .eq(CompletenessInformation::getPatientId, patientId)
                .last(" limit 0,1 ");
        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        String domainName = "";
        boolean thirdAuthorization = false;
        boolean certificationServiceNumber = true;
        Tenant tenant;
        if (tenantR.getIsSuccess()) {
            tenant = tenantR.getData();
            if (Objects.nonNull(tenant)) {
                domainName = tenant.getDomainName();
                thirdAuthorization = Objects.nonNull(tenant.getWxBindTime());
                certificationServiceNumber = tenant.isCertificationServiceNumber();
            }
        } else {
            return R.fail("服务繁忙");
        }
        // 通过患者ID和创建时间。过滤掉24小时内刚提示过的用户
        LocalDateTime dateTime = LocalDateTime.now().plusDays(-1);
        int count = managementHistoryDetailService.count(Wraps.<ManagementHistoryDetail>lbQ()
                .eq(ManagementHistoryDetail::getPatientId, patientId)
                .ge(SuperEntity::getCreateTime, dateTime));
        if (count > 0) {
            throw new BizException(I18nUtils.getMessage("REMINDER_TIME_OUT"));
        }
        Locale locale = LocaleContextHolder.getLocale();
        CompletenessInformation information = this.getOne(wrapper);
        String itemName = dictionaryItemService.findDictionaryItemName(DictionaryItemService.DOCTOR);
        if (information != null) {
            R<Patient> result = patientApi.get(information.getPatientId());
            if (result.getIsSuccess()) {
                if (result.getData() != null) {
                    R<String> message;
                    Patient patient = result.getData();
                    if (certificationServiceNumber) {
                        SendTemplateMessageForm form = getTemplate(patient.getOpenId(), domainName, patient.getId(), BaseContextHandler.getTenant(),
                                thirdAuthorization, patient.getWxAppId(), patient.getName(), patient.getDoctorName(), locale);
                        if (form == null) {
                            log.error("No relevant template found, cancel template message sending");
                            return R.fail("发送失败");
                        }
                        message = weiXinApi.sendTemplateMessage(form);
                    } else {
                        message = sendSms(patient.getMobile(), patient.getId(), tenant.getCode(), tenant.getDiseasesType(), tenant.getName(), domainName, patient.getDoctorName(), itemName);
                    }
                    ManagementHistory history = buildHistory(patient);
                    try {
                        // 保持历史
                        managementHistoryService.add(history);
                    } catch (Exception e) {
                        log.error("保持信息完整度出现错误");
                    }
                    return message;
                }
            }
        }
        return R.fail("发送失败");
    }

    @Autowired
    DictionaryItemService dictionaryItemService;


    /**
     * 立即发送一条短信
     * @param patientMobile
     * @param patientId
     * @param tenantCode
     * @param diseasesType
     * @param tenantName
     * @param domainName
     * @param doctorName
     * @param role
     * @return
     */
    private R<String> sendSms(String patientMobile, Long patientId, String tenantCode, TenantDiseasesTypeEnum diseasesType, String tenantName, String domainName, String doctorName, String role) {
        String wxPatientBizUrl = ApplicationDomainUtil.wxPatientBizUrl(domainName, true, H5Router.PATIENT_INTEGRITY);

        String smsParams = BusinessReminderType.getPatientIntegrity(tenantName, doctorName, role);
        // 创建一条 今日待办消息 的推送任务
        BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                .mobile(patientMobile)
                .wechatRedirectUrl(wxPatientBizUrl)
                .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                .type(BusinessReminderType.PATIENT_INTEGRITY)
                .tenantCode(tenantCode)
                .queryParams(smsParams)
                .patientId(patientId)
                .status(0)
                .openThisMessage(0)
                .finishThisCheckIn(0)
                .build();

        R<Boolean> booleanR = businessReminderLogControllerApi.sendNoticeSms(logSaveDTO);
        if (booleanR.getIsSuccess()) {
            return R.success("");
        } else {
            return R.fail("发送失败");
        }

    }



    private void sendSystemMessageToPatient(String jumpUrl,
                                            Long patientId,
                                            LocalDateTime pushTime,
                                            String pushPerson,
                                            String tenantCode) {
        MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(TemplateMessageIndefiner.COMPLETENESS_INFORMATION,
                null,
                jumpUrl,
                patientId,
                pushTime,
                pushPerson,
                tenantCode);

        systemMessageSaveDTO.createPushContent( null, null, null);
        patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);

    }


    private ManagementHistory buildHistory(Patient patient) {
        ManagementHistory history = new ManagementHistory();
        List<ManagementHistoryDetail> details = new ArrayList<>(16);
        ManagementHistoryDetail detail = new ManagementHistoryDetail();
        detail.setDoctorId(patient.getDoctorId());
        detail.setPatientId(patient.getId());
        details.add(detail);

        history.setOrgId(patient.getOrganId());
        history.setPatientNumber(1);
        history.setSendType(SendType.SINGLE_SHOT.getType());
        history.setNursingId(patient.getServiceAdvisorId());
        history.setHistoryType("信息完整度");
        history.setHistoryDetails(details);
        return history;
    }

    @Override
    public R<NotificaVo> notification(IntervalQo Interval) {
        if (Interval != null) {
            QueryWrap<CompletenessInformation> wrap = Wraps.q();
            wrap.eq("complete", 0).ge("completion",
                    Interval.getMin()).le("completion", Interval.getMax() == 100 ? 99 : Interval.getMax());
            Integer count = this.baseMapper.selectCount(wrap);
            NotificaVo notificaVo = new NotificaVo();
            notificaVo.setNum(count);
            return R.success(notificaVo);
        }
        return null;
    }



    private SendTemplateMessageForm getTemplate(String openId, String finalDomainName, Long patientId, String code,
                                                Boolean thirdAuthorization, String appId, String patientName, String doctorName, Locale locale) {
        R<TemplateMsgDto> result = templateApi.getCommonCategoryServiceWorkOrderMsg(code, null, TemplateMessageIndefiner.COMPLETENESS_INFORMATION);
        if (result.getIsSuccess() && result.getData() != null) {
            TemplateMsgDto templateMsgDto = result.getData();
            return buildTemplate(templateMsgDto, finalDomainName, patientId, code, thirdAuthorization, openId, appId, patientName, doctorName, locale);
        }
        return null;
    }



    private SendTemplateMessageForm buildTemplate(TemplateMsgDto templateMsgDto, String finalDomainName, Long patientId, String code, Boolean thirdAuthorization,
                                                  String openId, String appId, String patientName, String doctorName, Locale locale) {
        // 线程中有的话。就可以不传了
        SendTemplateMessageForm messageForm = new SendTemplateMessageForm();
        messageForm.setWxAppId(appId);
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        message.setTemplateId(templateMsgDto.getTemplateId());
        message.setToUser(openId);
        String url = ApplicationDomainUtil.wxPatientBizUrl(finalDomainName, thirdAuthorization, H5Router.PATIENT_INTEGRITY);
        message.setUrl(url);
        List<TemplateMsgFields> msgFieldsList = templateMsgDto.getFields();
        List<WxMpTemplateData> data = new ArrayList();
        if (templateMsgDto.getCommonCategory()) {
            data = CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientName, I18nUtils.getMessage(CommonTemplateServiceWorkModel.INFORMATION_IMPROVEMENT, locale));
        } else {
            boolean needForm = false;
            for (TemplateMsgFields field : msgFieldsList) {
                if (field.getType() == 1) {
                    needForm = true;
                    break;
                }
            }
            List<FormFieldDto> allFields = new ArrayList<>(50);
            if (needForm) {
                List<FormResult> list = formResultService.getBasicAndLastHealthFormResult(patientId);

                // 查询 患者填写的 基本消息和疾病信息 表单。
                if (!org.apache.commons.collections.CollectionUtils.isEmpty(list)) {
                    for (FormResult formResult : list) {
                        List<FormFieldDto> formFields = JSON.parseArray(formResult.getJsonContent(), FormFieldDto.class);
                        allFields.addAll(formFields);
                    }
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
                                sb.append(values);
                            }
                        }
                        // 添加一条
                        data.add(new WxMpTemplateData(fields.getAttr(), sb.toString(), fields.getColor()));
                    } else if (fields.getType() == 2) {
                        // 模板为 时间时，
                        data.add(new WxMpTemplateData(fields.getAttr(), DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM), fields.getColor()));
                    }
                } else {
                    data.add(new WxMpTemplateData(fields.getAttr(), fields.getValue(), fields.getColor()));
                }
            }
        }
        message.setData(data);
        messageForm.setTemplateMessage(message);

        sendSystemMessageToPatient(url, patientId, LocalDateTime.now(), doctorName, code);
        return messageForm;
    }


    /**
     * 查询患者信息完整度字段 和字段对应的表单的字段
     *
     * @param patientId
     * @return
     */
    @Override
    public CompletenessInformationPatientEditDto findCompletenessFormField(Long patientId) {
        LbqWrapper<CompletenessInformation> wrapper = Wraps.<CompletenessInformation>lbQ()
                .eq(CompletenessInformation::getPatientId, patientId)
                .last(" limit 0,1 ");
        CompletenessInformation information = baseMapper.selectOne(wrapper);
        if (Objects.isNull(information)) {
            return null;
        }
        CompletenessInformationPatientEditDto editDto = new CompletenessInformationPatientEditDto();
        R<Patient> patientR = patientApi.getBaseInfoForStatisticsData(patientId);
        Patient patient = patientR.getData();
        if (patientR.getIsSuccess() && Objects.nonNull(patient)) {
            information.setDoctorId(patient.getDoctorId());
            information.setNursingId(patient.getServiceAdvisorId());
            information.setDoctorName(patient.getDoctorName());
        }
        BeanUtils.copyProperties(information, editDto);
        editDto.setId(information.getId());
        List<PatientInformationField> fields = patientInformationFieldService.list(Wraps.<PatientInformationField>lbQ()
                .eq(PatientInformationField::getInformationId, information.getId())
                .orderByAsc(PatientInformationField::getFieldSort));
        List<PatientInformationFieldPatientEditDto> patientEditFields = new ArrayList<>(fields.size());
        editDto.setPatientEditFields(patientEditFields);
        if (CollUtil.isNotEmpty(fields)) {
            PatientInformationFieldPatientEditDto patientEditDto;
            Set<Long> formIds = fields.stream().map(PatientInformationField::getFormId).collect(Collectors.toSet());
            for (PatientInformationField field : fields) {
                patientEditDto = new PatientInformationFieldPatientEditDto();
                BeanUtils.copyProperties(field, patientEditDto);
                patientEditDto.setId(field.getId());
                patientEditFields.add(patientEditDto);
            }
            Map<Long, List<PatientInformationFieldPatientEditDto>> informationFields = patientEditFields.stream()
                    .collect(Collectors.groupingBy(PatientInformationFieldPatientEditDto::getFormId));
            for (Long formId : formIds) {
                FormResult formResult = formResultService.getOne(Wraps.<FormResult>lbQ()
                        .eq(FormResult::getFormId, formId)
                        .eq(FormResult::getUserId, patientId)
                        .orderByDesc(SuperEntity::getCreateTime)
                        .last(" limit 0,1 ")
                );
                List<FormField> fieldList = null;
                if (Objects.isNull(formResult)) {
                    Form form = formMapper.selectById(formId);
                    if (Objects.nonNull(form)) {
                        String fieldsJson = form.getFieldsJson();
                        fieldList = JSON.parseArray(fieldsJson, FormField.class);
                    }
                } else {
                    String jsonContent = formResult.getJsonContent();
                    fieldList = JSON.parseArray(jsonContent, FormField.class);
                }
                if (CollUtil.isEmpty(fieldList)) {
                    continue;
                }
                Map<String, FormField> formFieldMap = fieldList.stream().collect(Collectors.toMap(FormField::getId, item -> item, (o1, o2) -> o2));
                List<PatientInformationFieldPatientEditDto> fieldPatientEditDtos = informationFields.get(formId);
                for (PatientInformationFieldPatientEditDto dto : fieldPatientEditDtos) {
                    String fieldId = dto.getFieldId();
                    FormField formField = formFieldMap.get(fieldId);
                    // 如果信息完整度中有值。 则直接将信息完整度的值填充到字段中
                    if (dto.getFieldWrite().equals(1)) {
                        formField.setValue(dto.getFieldValue());
                        formField.setValues(dto.getFieldValues());
                        formField.setOtherValue(dto.getOtherValue());
                    }
                    dto.setFormField(JSON.toJSONString(formField));
                }
            }

        }
        return editDto;
    }

    /**
     * 创建一个表单结果
     * @param form
     * @param patientId
     * @param formId
     * @return
     */
    public FormResult createFormResultFromForm(Form form, Long patientId, Long formId) {
        FormResult formResult = new FormResult();
        formResult.setCategory(form.getCategory());
        formResult.setUserId(patientId);
        formResult.setFormId(formId);
        formResult.setJsonContent(form.getFieldsJson());
        formResult.setName(form.getName());
        formResult.setOneQuestionPage(form.getOneQuestionPage());
        formResult.setShowTrend(form.getShowTrend());
        formResult.setBusinessId(form.getBusinessId());
        return formResult;
    }

    /**
     * 根据患者信息完整度的编辑结果。更新表单结果中的值
     * @param formResult
     * @param patientEditDtoMap
     */
    public void saveOrUpdateFormResultForInformation( FormResult formResult, Map<String, PatientInformationFieldPatientEditDto> patientEditDtoMap ) {

        String jsonContent = formResult.getJsonContent();
        List<FormField> fieldList = JSON.parseArray(jsonContent, FormField.class);
        boolean updateFormResult = false;
        for (FormField field : fieldList) {
            PatientInformationFieldPatientEditDto patientEditDto = patientEditDtoMap.get(field.getId());
            if (Objects.nonNull(patientEditDto) && Objects.nonNull(patientEditDto.getFormField())) {
                FormField dtoFormField = JSON.parseObject(patientEditDto.getFormField(), FormField.class);
                field.setValues(dtoFormField.getValues());
                field.setValue(dtoFormField.getValue());
                field.setOtherValue(dtoFormField.getOtherValue());
                int write = InformationIntegrityMonitoringServiceImpl.judgeValuesWrite(field.getWidgetType(), field.getValues(), field.getValue());
                if (write == 1) {
                    updateFormResult = true;
                }
            }
        }
        formResult.setJsonContent(JSON.toJSONString(fieldList));
        if (updateFormResult) {
            formResult.setNoUpdatePatientInformation(true);
            if (formResult.getId() == null) {
                formResultService.save(formResult);
            } else {
                formResultService.updateById(formResult);
            }
        }
    }

    /**
     * 患者编辑信息完整度。更新或者创建新的表单结果。
     * 更新患者的信息完整度
     * @param editDto
     */
    @Override
    public void updateIncompleteInformationField(CompletenessInformationPatientEditDto editDto) {
        Long patientId = editDto.getPatientId();
        List<PatientInformationFieldPatientEditDto> patientEditFields = editDto.getPatientEditFields();
        // 只处理 待完善的字段
        Map<Long, List<PatientInformationFieldPatientEditDto>> patientFields = patientEditFields.stream()
                .filter(item -> item.getFieldWrite().equals(0))
                .collect(Collectors.groupingBy(PatientInformationFieldPatientEditDto::getFormId));
        // 更新患者信息完整度中的 待完善字段
        List<PatientInformationField> informationFields = patientInformationFieldService.list(Wraps.<PatientInformationField>lbQ()
                .eq(PatientInformationField::getInformationId, editDto.getId())
                .eq(PatientInformationField::getFieldWrite, 0));
        // 更新表单结果或 生成新的表单
        if (CollUtil.isNotEmpty(patientFields)) {
            for (Map.Entry<Long, List<PatientInformationFieldPatientEditDto>> listEntry : patientFields.entrySet()) {
                Long formId = listEntry.getKey();
                List<PatientInformationFieldPatientEditDto> formFields = listEntry.getValue();
                Map<String, PatientInformationFieldPatientEditDto> patientEditDtoMap = formFields.stream()
                        .collect(Collectors.toMap(PatientInformationFieldPatientEditDto::getFieldId, item -> item, (o1, o2) -> o2));
                PatientInformationFieldPatientEditDto fieldPatientEditDto = formFields.get(0);
                if (fieldPatientEditDto.getBusinessType().equals(FormEnum.BASE_INFO.getDesc())) {
                    // 如果是基本信息，
                    FormResult formResult = formResultService.getOne(Wraps.<FormResult>lbQ()
                            .eq(FormResult::getUserId, patientId)
                            .eq(FormResult::getFormId, formId)
                            .last(" limit 0, 1 "));
                    if (Objects.isNull(formResult)) {
                        // 查询表单。给他生成新数据
                        Form form = formMapper.selectById(formId);
                        formResult = createFormResultFromForm(form, patientId, formId);
                    }

                    saveOrUpdateFormResultForInformation(formResult, patientEditDtoMap);
                } else if (fieldPatientEditDto.getBusinessType().equals(FormEnum.HEALTH_RECORD.getDesc())) {
                    // 疾病信息。
                    // 查询最新的 未删除的疾病信息
                    FormResult formResult = formResultService.getOne(Wraps.<FormResult>lbQ()
                            .eq(FormResult::getUserId, patientId)
                            .eq(FormResult::getFormId, formId)
                            .eq(FormResult::getDeleteMark, 0)
                            .orderByDesc(FormResult::getCreateTime)
                            .last(" limit 0, 1 "));
                    if (Objects.isNull(formResult)) {
                        // 查询表单。给他生成新数据
                        Form form = formMapper.selectById(formId);
                        formResult = createFormResultFromForm(form, patientId, formId);
                    }

                    saveOrUpdateFormResultForInformation(formResult, patientEditDtoMap);
                } else {
                    // 直接生成一条新的 表单结果
                    Form form = formMapper.selectById(formId);
                    if (Objects.nonNull(form)) {
                        FormResult formResult = createFormResultFromForm(form, patientId, formId);
                        saveOrUpdateFormResultForInformation(formResult, patientEditDtoMap);
                    }
                }
            }

        }

        // 更新患者信息完整度中的字段
        for (PatientInformationField informationField : informationFields) {
            Long fieldFormId = informationField.getFormId();
            List<PatientInformationFieldPatientEditDto> patientEditDtos = patientFields.get(fieldFormId);
            for (PatientInformationFieldPatientEditDto patientEditDto : patientEditDtos) {
                if (informationField.getId().equals(patientEditDto.getId())) {
                    FormField formField = JSON.parseObject(patientEditDto.getFormField(), FormField.class);
                    if (Objects.isNull(formField)) {
                        continue;
                    }
                    informationField.setFieldValue(formField.getValue());
                    informationField.setFieldValues(formField.getValues());
                    informationField.setOtherValue(formField.getOtherValue());
                    int write = InformationIntegrityMonitoringServiceImpl.judgeValuesWrite(informationField.getFieldWidgetType(), informationField.getFieldValues(), informationField.getFieldValue());
                    informationField.setFieldWrite(write);
                }
            }
        }
        patientInformationFieldService.updateBatchById(informationFields);

        Set<Long> informationIds = new HashSet<>();
        informationIds.add(editDto.getId());
        // 统计一下看看 信息完整度是多少了
        calculateCompletion(informationIds);

    }

    /**
     * 重新计算信息完整度
     * @param informationIds
     */
    @Override
    public void calculateCompletion(Set<Long> informationIds) {
        for (Long informationId : informationIds) {
            CompletenessInformation information = baseMapper.selectById(informationId);
            information.setLastWriteTime(LocalDateTime.now());
            int count = patientInformationFieldService.count(Wraps.<PatientInformationField>lbQ()
                    .eq(PatientInformationField::getInformationId, informationId)
                    .eq(PatientInformationField::getFieldWrite, 1));
            int total = patientInformationFieldService.count(Wraps.<PatientInformationField>lbQ()
                    .eq(PatientInformationField::getInformationId, informationId));
            information.setCompletion(getCompletenessInformationComplete(total, count));
            if (100 == information.getCompletion()) {
                information.setComplete(1);
            } else {
                information.setComplete(0);
            }
            baseMapper.updateById(information);
        }

    }

    /**
     * 获取信息完整度百分比
     *
     * @param monitoringFieldCount
     * @param writeFieldCount
     * @return
     */
    private Integer getCompletenessInformationComplete(Integer monitoringFieldCount, Integer writeFieldCount) {
        if (monitoringFieldCount == 0) {
            return 100;
        }
        BigDecimal totalDecimal = new BigDecimal(monitoringFieldCount);
        BigDecimal chartDecimal = new BigDecimal(writeFieldCount);
        return BigDecimalUtils.proportion(chartDecimal, totalDecimal);
    }
}
