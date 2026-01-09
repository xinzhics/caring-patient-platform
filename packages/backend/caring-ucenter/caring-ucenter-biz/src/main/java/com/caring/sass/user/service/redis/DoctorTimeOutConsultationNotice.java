package com.caring.sass.user.service.redis;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dao.NursingStaffMapper;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.service.impl.WeiXinService;
import com.caring.sass.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DoctorTimeOutConsultationNotice {

    @Autowired
    TenantApi tenantApi;

    @Autowired
    WeiXinService weiXinService;

    @Autowired
    DoctorMapper doctorMapper;


    @Autowired
    NursingStaffMapper nursingStaffMapper;

    @Autowired
    DictionaryItemService dictionaryItemService;

    @Autowired
    BusinessReminderLogControllerApi businessReminderLogControllerApi;

    /**
     * 查询 个人服务号项目 里面。医生是否存在 患者未读消息。存在时，给医生发送短信提醒
     * @param now
     */
    public void nursingHandlePatientMessage(LocalDateTime now) {

        R<List<Tenant>> tenantList = tenantApi.queryTenantList(TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER);
        List<Tenant> tenants = tenantList.getData();
        if (tenants.isEmpty()) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        ImApi imApi = SpringUtils.getBean(ImApi.class);
        for (Tenant tenant : tenants) {
            String code = tenant.getCode();
            BaseContextHandler.setTenant(code);
            Boolean weiXinTemplate = weiXinService.noSendWeiXinTemplate(code, PlanFunctionTypeEnum.ONLINE_CONSULTATION);
            if (weiXinTemplate) {
                continue;
            }
            String role = dictionaryItemService.findDictionaryItemName(DictionaryItemService.PATIENT);
            String pathUrl = ApplicationDomainUtil.wxAssistantBaseDomain(tenant.getDomainName(), true);
            pathUrl += "/#/pages/message/index";

            List<NursingStaff> nursingStaffs = nursingStaffMapper.selectList(Wraps.<NursingStaff>lbQ()
                    .isNotNull(NursingStaff::getMobile)
                    .and(wrapper ->
                            wrapper.eq(NursingStaff::getImWxTemplateStatus, CommonStatus.YES)
                            .or()
                            .isNull(NursingStaff::getImWxTemplateStatus)));
            if (nursingStaffs.isEmpty()) {
                continue;
            }
            TenantDiseasesTypeEnum diseasesType = tenant.getDiseasesType();
            for (NursingStaff nursingStaff : nursingStaffs) {
                if (StrUtil.isEmpty(nursingStaff.getMobile())) {
                    return;
                }
                R<Integer> patientNumber = imApi.countPatientNumber(nursingStaff.getId(), UserType.UCENTER_NURSING_STAFF, code, formattedDateTime);
                Integer data = patientNumber.getData();
                if (data == null || data == 0) {
                    continue;
                }

                String smsParams = BusinessReminderType.getDoctorTimeOutConsultationNotice(tenant.getName(),
                        data,
                        role);

                // 创建一条 今日待办消息 的推送任务
                BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                        .mobile(nursingStaff.getMobile())
                        .wechatRedirectUrl(pathUrl)
                        .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                        .type(BusinessReminderType.DOCTOR_TIME_OUT_CONSULTATION_NOTICE)
                        .tenantCode(code)
                        .queryParams(smsParams)
                        .nursingId(nursingStaff.getId())
                        .status(0)
                        .openThisMessage(0)
                        .finishThisCheckIn(0)
                        .build();
                businessReminderLogControllerApi.sendNoticeSms(logSaveDTO);
            }
        }

    }

    /**
     * 查询 个人服务号项目 里面。医生是否存在 患者未读消息。存在时，给医生发送短信提醒
     * @param now
     */
    public void handlePatientMessage(LocalDateTime now) {

        R<List<Tenant>> tenantList = tenantApi.queryTenantList(TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER);
        List<Tenant> tenants = tenantList.getData();
        if (tenants.isEmpty()) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        ImApi imApi = SpringUtils.getBean(ImApi.class);
        for (Tenant tenant : tenants) {
            String code = tenant.getCode();
            BaseContextHandler.setTenant(code);
            String role = dictionaryItemService.findDictionaryItemName(DictionaryItemService.PATIENT);
            String doctorBizUrl = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(), true, H5Router.DOCTOR_CHAT);
            Boolean weiXinTemplate = weiXinService.noSendWeiXinTemplate(code, PlanFunctionTypeEnum.ONLINE_CONSULTATION);
            if (weiXinTemplate) {
                continue;
            }
            List<Doctor> doctors = doctorMapper.selectList(Wraps.<Doctor>lbQ()
                    .eq(Doctor::getBuildIn, 0)
                    .eq(Doctor::getImWxTemplateStatus, CommonStatus.YES));
            if (doctors.isEmpty()) {
                continue;
            }
            TenantDiseasesTypeEnum diseasesType = tenant.getDiseasesType();
            for (Doctor doctor : doctors) {
                if (StrUtil.isEmpty(doctor.getMobile())) {
                    return;
                }
                R<Integer> patientNumber = imApi.countPatientNumber(doctor.getId(), UserType.UCENTER_DOCTOR, code, formattedDateTime);
                Integer data = patientNumber.getData();
                if (data == null || data == 0) {
                    continue;
                }

                String smsParams = BusinessReminderType.getDoctorTimeOutConsultationNotice(tenant.getName(),
                        data,
                        role);

                // 创建一条 今日待办消息 的推送任务
                BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                        .mobile(doctor.getMobile())
                        .wechatRedirectUrl(doctorBizUrl)
                        .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                        .type(BusinessReminderType.DOCTOR_TIME_OUT_CONSULTATION_NOTICE)
                        .tenantCode(code)
                        .queryParams(smsParams)
                        .doctorId(doctor.getId())
                        .status(0)
                        .openThisMessage(0)
                        .finishThisCheckIn(0)
                        .build();
                businessReminderLogControllerApi.sendNoticeSms(logSaveDTO);
            }
        }



    }
}
