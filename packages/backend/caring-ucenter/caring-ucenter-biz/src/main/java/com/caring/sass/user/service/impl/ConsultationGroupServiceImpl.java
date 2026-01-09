package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.msgs.api.GroupImApi;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.dao.*;
import com.caring.sass.user.dto.ConsultationGroupAcceptOrReject;
import com.caring.sass.user.dto.ConsultationGroupMemberAddSomeDTO;
import com.caring.sass.user.dto.ConsultationGroupPage;
import com.caring.sass.user.dto.SystemMsgType;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.ConsultationGroupService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.entity.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ConsultationGroupServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 13:59
 * @Version 1.0
 */
@Slf4j
@Service
public class ConsultationGroupServiceImpl  extends SuperServiceImpl<ConsultationGroupMapper, ConsultationGroup> implements ConsultationGroupService {

    @Autowired
    ConsultationGroupMemberMapper consultationGroupMemberMapper;

    @Autowired
    NursingStaffMapper nursingStaffMapper;

    @Autowired
    DoctorMapper doctorMapper;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    WeiXinService weiXinService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    ImService imService;

    @Autowired
    SystemMsgMapper systemMsgMapper;

    @Autowired
    GroupImApi groupImApi;

    PatientService patientService;

    @Autowired
    DictionaryItemService dictionaryItemService;

    @Autowired
    BusinessReminderLogControllerApi reminderLogControllerApi;

    private static final String PATIENT = "patient";

    private static final String DOCTOR = "doctor";

    private static final String ASSISTANT = "assistant";

    private PatientService getPatientService() {
        if (patientService == null) {
            patientService = SpringUtils.getBean(PatientService.class);
        }
        return patientService;
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

    @Autowired
    MsgPatientSystemMessageApi patientSystemMessageApi;

    /**
     * @Author yangShuai
     * @Description 保存会诊小组人员
     * @Date 2021/6/2 14:34
     *
     * @param consultationGroup
     * @param nursingStaff
     * @param patientIds
     * @return void
     */
    @Override
    public void appSaveMember(ConsultationGroup consultationGroup, NursingStaff nursingStaff, List<Long> patientIds, Tenant tenant) {
        List<ConsultationGroupMember> groupMembers = new ArrayList<>();
        Long consultationGroupId = consultationGroup.getId();
        Map<String, String> dictItem = getDictItem();
        ConsultationGroupMember groupMember = new ConsultationGroupMember(consultationGroupId, nursingStaff, dictItem.get(ASSISTANT));
        groupMembers.add(groupMember);
        Map<Long, String> patientDoctorNameMap = new HashMap<>();
        for (Long patientId : patientIds) {
            Patient patient = getPatientService().getBasePatientById(patientId);
            patientDoctorNameMap.put(patientId, patient.getDoctorName());
            groupMembers.add(new ConsultationGroupMember(consultationGroupId, patient, dictItem.get(PATIENT)));
        }
        consultationGroupMemberMapper.insertBatchSomeColumn(groupMembers);
        boolean certificationServiceNumber = tenant.isCertificationServiceNumber();
        TenantDiseasesTypeEnum diseasesType = tenant.getDiseasesType();
        String consultationNotice = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), String.format(H5Router.CONSULTATION_NOTICE, consultationGroupId));
        for (ConsultationGroupMember member : groupMembers) {
            if (member.getMemberRole() != null && member.getMemberRole().equals(UserType.UCENTER_PATIENT)) {
                if (certificationServiceNumber) {
                    weiXinService.sendConsultationTemplateMessage(tenant.getWxAppId(), consultationGroup.getGroupName(), TemplateMessageIndefiner.CONSULTATION_NOTICE,
                            member.getMemberOpenId(), consultationNotice, member.getMemberName());
                } else {
                    // 个人服务号发送短信 患者病历病例讨论开始邀请链接
                    String smsParams = BusinessReminderType.getPatientNoticeOfTheStartCaseDiscussion(tenant.getName(), nursingStaff.getName(), dictItem.get(ASSISTANT));
                    BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                            .mobile(member.getMobile())
                            .wechatRedirectUrl(consultationNotice)
                            .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                            .type(BusinessReminderType.PATIENT_NOTICE_OF_THE_START_CASE_DISCUSSION)
                            .tenantCode(tenant.getCode())
                            .patientId(member.getPatientId())
                            .queryParams(smsParams)
                            .status(0)
                            .openThisMessage(0)
                            .finishThisCheckIn(0)
                            .build();
                    reminderLogControllerApi.sendNoticeSms(logSaveDTO);
                }
                MsgPatientSystemMessageSaveDTO messageSaveDTO = new MsgPatientSystemMessageSaveDTO(PlanFunctionTypeEnum.CASE_DISCUSSION.getCode() + "_START",
                        consultationGroupId,
                        consultationNotice, member.getPatientId(), LocalDateTime.now(), patientDoctorNameMap.get(member.getPatientId()), tenant.getCode(), 0);
                messageSaveDTO.createPushContent( null,null, null);
                patientSystemMessageApi.saveSystemMessage(messageSaveDTO);
            }
        }
    }

    @Override
    public void doctorSaveMember(ConsultationGroup consultationGroup, Doctor doctor, List<Long> patientIds, Tenant tenant) {

        List<ConsultationGroupMember> groupMembers = new ArrayList<>();
        Long consultationGroupId = consultationGroup.getId();
        Map<String, String> dictItem = getDictItem();
        ConsultationGroupMember groupMember = new ConsultationGroupMember(consultationGroupId, doctor, dictItem.get(DOCTOR));
        groupMembers.add(groupMember);
        Map<Long, String> patientDoctorNameMap = new HashMap<>();
        for (Long patientId : patientIds) {
            Patient patient = getPatientService().getBasePatientById(patientId);
            patientDoctorNameMap.put(patientId, patient.getDoctorName());
            groupMembers.add(new ConsultationGroupMember(consultationGroupId, patient, dictItem.get(PATIENT)));
        }
        consultationGroupMemberMapper.insertBatchSomeColumn(groupMembers);
        String consultationNotice = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), String.format(H5Router.CONSULTATION_NOTICE, consultationGroupId));
        TenantDiseasesTypeEnum diseasesType = tenant.getDiseasesType();
        boolean certificationServiceNumber = tenant.isCertificationServiceNumber();
        for (ConsultationGroupMember member : groupMembers) {
            if (member.getMemberRole() != null && member.getMemberRole().equals(UserType.UCENTER_PATIENT)) {
                // 企业服务号发送模版
                if (certificationServiceNumber) {
                    weiXinService.sendConsultationTemplateMessage(tenant.getWxAppId(), consultationGroup.getGroupName(), TemplateMessageIndefiner.CONSULTATION_NOTICE,
                            member.getMemberOpenId(), consultationNotice, member.getMemberName());
                } else {
                    // 个人服务号发送短信 患者病历病例讨论开始邀请链接
                    String smsParams = BusinessReminderType.getPatientNoticeOfTheStartCaseDiscussion(tenant.getName(), doctor.getName(), dictItem.get(DOCTOR));
                    BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                            .mobile(member.getMobile())
                            .wechatRedirectUrl(consultationNotice)
                            .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                            .type(BusinessReminderType.PATIENT_NOTICE_OF_THE_START_CASE_DISCUSSION)
                            .tenantCode(tenant.getCode())
                            .patientId(member.getPatientId())
                            .queryParams(smsParams)
                            .status(0)
                            .openThisMessage(0)
                            .finishThisCheckIn(0)
                            .build();
                    reminderLogControllerApi.sendNoticeSms(logSaveDTO);
                }
                MsgPatientSystemMessageSaveDTO messageSaveDTO = new MsgPatientSystemMessageSaveDTO(PlanFunctionTypeEnum.CASE_DISCUSSION.getCode() + "_START",
                        consultationGroup.getId(),
                        consultationNotice, member.getPatientId(), LocalDateTime.now(), patientDoctorNameMap.get(member.getPatientId()), tenant.getCode(), 0);
                messageSaveDTO.createPushContent( null,null, null);
                patientSystemMessageApi.saveSystemMessage(messageSaveDTO);
            }
        }



    }

    /**
     * 统计 邀请信息
     * @param memberUserId 被邀请人员ID
     * @param memberRole 被邀请人角色
     * @return
     */
    @Override
    public Integer countInviteNumber(Long memberUserId, String memberRole) {

        return consultationGroupMemberMapper.selectCount(Wraps.<ConsultationGroupMember>lbQ()
                .eq(ConsultationGroupMember::getMemberUserId, memberUserId)
                .eq(ConsultationGroupMember::getMemberRole, memberRole));
    }

    /**
     * 给病例讨论组 邀请医生加入。
     * @param memberAddSomeDTO
     *
     */
    @Transactional
    @Override
    public void addMemberToGroup(ConsultationGroupMemberAddSomeDTO memberAddSomeDTO) {

        Long groupId = memberAddSomeDTO.getGroupId();
        String nursingImAccount = memberAddSomeDTO.getNursingIMAccount();
        List<String> doctorOpenId = memberAddSomeDTO.getDoctorOpenId();
        Set<String> doctorMobileId = memberAddSomeDTO.getDoctorMobileId();
        Long invitePeople = memberAddSomeDTO.getInvitePeople();
        String invitePeopleRole = memberAddSomeDTO.getInvitePeopleRole();
        List<ConsultationGroupMember> groupMembers = new ArrayList<>();
        List<ConsultationGroupMember> updateGroupMembers = new ArrayList<>();
        Map<String, String> dictItem = getDictItem();
        ConsultationGroup group = baseMapper.selectById(groupId);
        // 医助是否要加入到 环信群组中去
        boolean nursingJoin = false;
        ConsultationGroupMember nursingMember = null;
        if (StrUtil.isNotEmpty(nursingImAccount)) {
            // 判断医助是否在 讨论组，如果不在，则添加进来。
            nursingMember = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                    .eq(ConsultationGroupMember::getConsultationGroupId, groupId)
                    .eq(ConsultationGroupMember::getMemberImAccount, nursingImAccount)
                    .last(" limit 0,1 "));
            if (Objects.isNull(nursingMember)) {
                // 将 医助 进入到讨论组中来。
                NursingStaff nursingStaff = nursingStaffMapper.selectOne(Wraps.<NursingStaff>lbQ()
                        .eq(NursingStaff::getImAccount, nursingImAccount)
                        .select(SuperEntity::getId, NursingStaff::getName, NursingStaff::getAvatar, NursingStaff::getImAccount));
                nursingMember = new ConsultationGroupMember(groupId, nursingStaff, ConsultationGroupMember.JOINED, dictItem.get(ASSISTANT));
                nursingJoin = true;
            } else {
                // 如果之前医助是被移出状态。 则 重新加入他到环信裙子
                if (Objects.equals(ConsultationGroupMember.REMOVED, nursingMember.getMemberStatus())) {
                    nursingJoin = true;
                }
                nursingMember.setMemberStatus(ConsultationGroupMember.JOINED);
            }
        }
        // 需要发送邀请提醒的医生
        List<Doctor> doctorSendWxTemplate = new ArrayList<>();
        if (Objects.nonNull(doctorOpenId)) {
            doctorOpenId = doctorOpenId.stream().filter(StrUtil::isNotEmpty).collect(Collectors.toList());
        }
        if (CollUtil.isNotEmpty(doctorOpenId)) {
            List<Doctor> doctors = doctorMapper.selectList(Wraps.<Doctor>lbQ().in(Doctor::getOpenId, doctorOpenId)
                    .select(SuperEntity::getId, Doctor::getName, Doctor::getImAccount, Doctor::getOpenId, Doctor::getAvatar, Doctor::getMobile));
            if (CollUtil.isNotEmpty(doctors)) {
                List<ConsultationGroupMember> memberList = consultationGroupMemberMapper.selectList(Wraps.<ConsultationGroupMember>lbQ()
                        .eq(ConsultationGroupMember::getConsultationGroupId, groupId)
                        .in(ConsultationGroupMember::getMemberOpenId, doctorOpenId));
                Map<String, ConsultationGroupMember> existOpenIds = new HashMap<>();
                if (CollUtil.isNotEmpty(memberList)) {
                    existOpenIds = memberList.stream().collect(Collectors.toMap(ConsultationGroupMember::getMemberOpenId, item -> item, (o1, o2) -> o2));
                }
                for (Doctor doctor : doctors) {
                    if (!existOpenIds.containsKey(doctor.getOpenId())) {
                        ConsultationGroupMember member = new ConsultationGroupMember(groupId, doctor, ConsultationGroupMember.INVITE, dictItem.get(DOCTOR));
                        member.setInvitePeople(invitePeople);
                        member.setInvitePeopleRole(invitePeopleRole);
                        groupMembers.add(member);
                        doctorSendWxTemplate.add(doctor);
                    } else {
                        ConsultationGroupMember member = existOpenIds.get(doctor.getOpenId());
                        member.setMemberStatus(ConsultationGroupMember.INVITE);
                        member.setInvitePeople(invitePeople);
                        member.setInvitePeopleRole(invitePeopleRole);
                        updateGroupMembers.add(member);
                        doctorSendWxTemplate.add(doctor);
                    }
                }
            }
        } else if (CollUtil.isNotEmpty(doctorMobileId)) {
            // 支持使用医生的手机号进行要求。个人服务号没有openId
            Doctor doctor = null;
            for (String mobile : doctorMobileId) {
                try {
                    doctor = doctorMapper.selectOne(Wraps.<Doctor>lbQ()
                            .select(SuperEntity::getId, Doctor::getName, Doctor::getImAccount, Doctor::getOpenId, Doctor::getAvatar, Doctor::getMobile)
                            .eq(Doctor::getMobile, EncryptionUtil.encrypt(mobile)));
                } catch (Exception e) {
                    continue;
                }
                if (doctor == null) {
                    continue;
                }
                ConsultationGroupMember member = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                        .last(" limit 0,1 ")
                        .eq(ConsultationGroupMember::getConsultationGroupId, groupId)
                        .eq(ConsultationGroupMember::getMemberRole, ConsultationGroupMember.ROLE_DOCTOR)
                        .in(ConsultationGroupMember::getMobile, mobile));
                if (member == null) {
                    member = new ConsultationGroupMember(groupId, doctor, ConsultationGroupMember.INVITE, dictItem.get(DOCTOR));
                    member.setInvitePeople(invitePeople);
                    member.setInvitePeopleRole(invitePeopleRole);
                    groupMembers.add(member);
                    doctorSendWxTemplate.add(doctor);
                } else {
                    member.setMemberStatus(ConsultationGroupMember.INVITE);
                    member.setInvitePeople(invitePeople);
                    member.setInvitePeopleRole(invitePeopleRole);
                    updateGroupMembers.add(member);
                    doctorSendWxTemplate.add(doctor);
                }
            }
        }

        // 保存人员到讨论组中
        if (CollUtil.isNotEmpty(updateGroupMembers)) {
            for (ConsultationGroupMember groupMember : updateGroupMembers) {
                consultationGroupMemberMapper.updateById(groupMember);
            }
        }
        if (CollUtil.isNotEmpty(groupMembers)) {
            consultationGroupMemberMapper.insertBatchSomeColumn(groupMembers);
        }
        if (Objects.nonNull(nursingMember)) {
            setNursingJoinGroup(nursingMember, nursingJoin, group);
        }
        if (CollUtil.isNotEmpty(doctorSendWxTemplate)) {
            // 给邀请人员发送模板消息
            String tenant = BaseContextHandler.getTenant();
            if (Objects.nonNull(group)) {
                ConsultationGroupMember groupMember = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                        .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_PATIENT)
                        .eq(ConsultationGroupMember::getConsultationGroupId, group.getId())
                        .last("limit 0,1"));
                String memberName = "";
                if (Objects.nonNull(groupMember)) {
                    memberName = groupMember.getMemberName();
                }
                sendInviteMessage(doctorSendWxTemplate, tenant, group.getGroupName(), memberName, invitePeople, invitePeopleRole);
            }
        }
    }

    /**
     * 邀请医助加入讨论组
     * @param nursingMember
     * @param nursingJoin
     * @param group
     */
    @Transactional
    public void setNursingJoinGroup(ConsultationGroupMember nursingMember, boolean nursingJoin,  ConsultationGroup group) {

        // 加入到环信群组中去
        if (nursingMember.getId() != null) {
            consultationGroupMemberMapper.updateById(nursingMember);
        } else {
            consultationGroupMemberMapper.insert(nursingMember);
        }
        if (nursingJoin) {
            groupImApi.addUserToGroup(group.getImGroupId(), nursingMember.getMemberImAccount(), false);
        }

    }

    @Override
    public void consultationGroupPage(IPage<ConsultationGroup> page, ConsultationGroupPage model) {
        IPage<ConsultationGroupMember> memberPage = new Page(page.getCurrent(), page.getSize());
        String imAccount = model.getImAccount();
        Long userId = model.getUserId();
        List<Integer> memberState = model.memberState();
        LbqWrapper<ConsultationGroupMember> lbqWrapper = Wraps.<ConsultationGroupMember>lbQ()
                .select(SuperEntity::getId, ConsultationGroupMember::getConsultationGroupId, ConsultationGroupMember::getMemberStatus)
                .in(ConsultationGroupMember::getMemberStatus, memberState)
                .orderByDesc(SuperEntity::getCreateTime);
        if (Objects.nonNull(userId)) {
            lbqWrapper.eq(ConsultationGroupMember::getMemberUserId, userId);
        }
        if (StrUtil.isNotEmpty(imAccount)) {
            lbqWrapper.eq(ConsultationGroupMember::getMemberImAccount, imAccount);
        }
        memberPage = consultationGroupMemberMapper.selectPage(memberPage, lbqWrapper);
        List<ConsultationGroupMember> groupMembers = memberPage.getRecords();
        List<ConsultationGroup> groupList = new ArrayList<>();
        if (CollUtil.isNotEmpty(groupMembers)) {
            List<Long> groupIds = groupMembers.stream().map(ConsultationGroupMember::getConsultationGroupId).collect(Collectors.toList());
            List<ConsultationGroup> consultationGroups = baseMapper.selectBatchIds(groupIds);
            if (CollUtil.isNotEmpty(consultationGroups)) {
                Map<Long, ConsultationGroup> consultationGroupMap = consultationGroups.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
                Set<Long> nurseId = new HashSet<>();
                Set<Long> doctorId = new HashSet<>();
                for (ConsultationGroup consultationGroup : consultationGroups) {
                    if (UserType.UCENTER_DOCTOR.equals(consultationGroup.getCreateUserType())) {
                        doctorId.add(consultationGroup.getCreateUser());
                    }
                    if (UserType.UCENTER_NURSING_STAFF.equals(consultationGroup.getCreateUserType())) {
                        nurseId.add(consultationGroup.getCreateUser());
                    }
                }
                Map<Long, String> nursesName = new HashMap<>();
                Map<Long, String> doctorsName = new HashMap<>();
                if (CollUtil.isNotEmpty(nurseId)) {
                    List<NursingStaff> nursingStaffs = nursingStaffMapper.selectBatchIds(nurseId);
                    nursesName = nursingStaffs.stream().collect(Collectors.toMap(SuperEntity::getId, NursingStaff::getName, (o1, o2) -> o2));
                }
                if (CollUtil.isNotEmpty(doctorId)) {
                    List<Doctor> doctorList = doctorMapper.selectBatchIds(doctorId);
                    doctorsName = doctorList.stream().collect(Collectors.toMap(SuperEntity::getId, Doctor::getName, (o1, o2) -> o2));
                }

                for (ConsultationGroupMember member : groupMembers) {
                    ConsultationGroup group = consultationGroupMap.get(member.getConsultationGroupId());
                    if (Objects.nonNull(group)) {
                        if (UserType.UCENTER_DOCTOR.equals(group.getCreateUserType())) {
                            group.setCreateUserName(doctorsName.get(group.getCreateUser()) == null ? "-" : doctorsName.get(group.getCreateUser()));
                        }
                        if (UserType.UCENTER_NURSING_STAFF.equals(group.getCreateUserType())) {
                            group.setCreateUserName(nursesName.get(group.getCreateUser()) == null ? "-" : nursesName.get(group.getCreateUser()));
                        }
                        group.setMemberStatus(member.getMemberStatus());
                        groupList.add(group);
                    }
                }
            }
        }
        page.setRecords(groupList);
        page.setCurrent(memberPage.getCurrent());
        page.setSize(memberPage.getSize());
        page.setPages(memberPage.getPages());
        page.setTotal(memberPage.getTotal());
    }

    /**
     * 给医生发送 病历讨论邀请
     * @param doctors
     * @param tenantCode
     * @param groupName
     */
    @Async
    public void sendInviteMessage(List<Doctor> doctors, String tenantCode, String groupName, String memberName, Long invitePeople, String invitePeopleRole) {
        BaseContextHandler.setTenant(tenantCode);
        if (CollectionUtils.isEmpty(doctors)) {
            return;
        }
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        if (tenantR.getIsSuccess() == null || tenantR.getIsError()) {
            return;
        }
        Tenant tenant = tenantR.getData();
        if (Objects.isNull(tenant)) {
            return;
        }
        String invitePeopleName = "";
        String patientRole;
        Map<String, String> dictItem = getDictItem();
        if (UserType.UCENTER_NURSING_STAFF.equals(invitePeopleRole)) {
            NursingStaff nursingStaff = nursingStaffMapper.selectOne(Wraps.<NursingStaff>lbQ().eq(NursingStaff::getId, invitePeople).select(SuperEntity::getId, NursingStaff::getName));
            invitePeopleRole = dictItem.get(ASSISTANT);
            if (Objects.nonNull(nursingStaff)) {
                invitePeopleName = nursingStaff.getName();
            }
        } else if (UserType.UCENTER_DOCTOR.equals(invitePeopleRole)) {
            Doctor doctor = doctorMapper.selectOne(Wraps.<Doctor>lbQ().eq(Doctor::getId, invitePeople).select(Doctor::getId, Doctor::getName));
            if (Objects.nonNull(doctor)) {
                invitePeopleName = doctor.getName();
            }
            invitePeopleRole = dictItem.get(DOCTOR);
        }
        patientRole = dictItem.get(PATIENT);

        String consultationNotice = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                H5Router.CONSULTATION_INVITE);
        TenantDiseasesTypeEnum diseasesType = tenant.getDiseasesType();
        // 个人服务号 发送短信进行邀请
        if (tenant.isPersonalServiceNumber()) {

            // 【敏瑞健康】“敏识燕语”提醒您马燕医生向您发起了XXX患者的病例讨论的邀请，点击链接可查看详情
            String smsParams = BusinessReminderType.getDoctorNoticeOfInviteCaseDiscussion(tenant.getName(), invitePeopleName, invitePeopleRole, memberName, patientRole);
            // 邀请人已经不存在。就不要发了。
            if (invitePeopleName == null) {
                return;
            }
            for (Doctor doctor : doctors) {
                // 创建一条 今日待办消息 的推送任务
                BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                        .mobile(doctor.getMobile())
                        .wechatRedirectUrl(consultationNotice)
                        .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                        .type(BusinessReminderType.DOCTOR_NOTICE_OF_INVITE_CASE_DISCUSSION)
                        .tenantCode(tenantCode)
                        .queryParams(smsParams)
                        .doctorId(doctor.getId())
                        .status(0)
                        .openThisMessage(0)
                        .finishThisCheckIn(0)
                        .build();
                reminderLogControllerApi.sendNoticeSms(logSaveDTO);
            }
            return;
        } else {
            // 企业服务号。 发送模版
            for (Doctor doctor : doctors) {
                if (StrUtil.isNotEmpty(doctor.getOpenId())) {
                    weiXinService.sendConsultationTemplateInviteMessage(tenant.getWxAppId(), groupName, TemplateMessageIndefiner.CONSULTATION_NOTICE,
                            doctor.getOpenId(), consultationNotice, memberName);
                }
            }
        }

    }

    /**
     * @Author yangShuai
     * @Description 查询小组的详细, 不包含扫码但是没有确定加入的人员
     * @Date 2021/6/2 15:28
     *
     * @param consultationGroupId
     * @return com.caring.sass.user.entity.ConsultationGroup
     */
    @Override
    public ConsultationGroup getConsultationGroupDetail(Long consultationGroupId,  String openId, String imAccount) {
        ConsultationGroup consultationGroup = baseMapper.selectById(consultationGroupId);
        LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, consultationGroupId);
        lbqWrapper.in(ConsultationGroupMember::getMemberStatus, ConsultationGroupMember.JOINED, ConsultationGroupMember.CANNOT_REMOVED);
        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(lbqWrapper);
        groupMembers = sortGroupMembers(groupMembers, openId, imAccount);
        consultationGroup.setConsultationGroupMembers(groupMembers);


        return consultationGroup;
    }

    /**
     * @Author yangShuai
     * @Description 对会诊成员进行排序。
     * @Date 2021/6/2 15:42
     *
     * @param groupMembers
     * @param openId
     * @param imAccount
     * @return java.util.List<com.caring.sass.user.entity.ConsultationGroupMember>
     */
    public List<ConsultationGroupMember> sortGroupMembers(List<ConsultationGroupMember> groupMembers, String openId, String imAccount) {

        ConsultationGroupMember groupMemberMe = null;
        ConsultationGroupMember patient = null;
        ConsultationGroupMember nursing = null;
        List<ConsultationGroupMember> groupMemberList = new ArrayList<>(groupMembers.size());
        for (ConsultationGroupMember groupMember : groupMembers) {
            if (openId != null && openId.equals( groupMember.getMemberOpenId())) {
                groupMemberMe = groupMember;
                groupMemberMe.setIsMe(true);
                continue;
            }
            if (imAccount != null && imAccount.equals(groupMember.getMemberImAccount())) {
                groupMemberMe = groupMember;
                groupMemberMe.setIsMe(true);
                continue;
            }
            if (groupMember.getMemberRole().equals(UserType.UCENTER_PATIENT)) {
                patient = groupMember;
                Patient patientInfo;
                if (StrUtil.isNotEmpty(groupMember.getMemberOpenId())) {
                    patientInfo = getPatientService().findByOpenId(groupMember.getMemberOpenId());
                } else {
                    patientInfo = getPatientService().findByMobile(groupMember.getMobile());
                }
                // 当前登陆人是医助， 患者医助id和当前登陆人id一样。设置备注
                if (UserType.NURSING_STAFF.equals(BaseContextHandler.getUserType()) &&
                        patientInfo.getServiceAdvisorId().equals(BaseContextHandler.getUserId())) {
                    patient.setNursingRemark(patientInfo.getRemark());
                }
                // 当前登陆人是医生。 患者的医生ID和当前登陆人的id一样。设置备注
                if (UserType.DOCTOR.equals(BaseContextHandler.getUserType()) &&
                        patientInfo.getDoctorId().equals(BaseContextHandler.getUserId())) {
                    patient.setDoctorRemark(patientInfo.getDoctorRemark());
                }
                // 当前登陆人是扫码的来宾， 获取来宾的openId和患者医生的openId，相等则设置备注。
                if (UserType.CONSULTATION_GUEST.equals(BaseContextHandler.getUserType())) {
                    Long userId = BaseContextHandler.getUserId();
                    // 来宾的 openId 和患者医生的openId是一个。设置医生对患者的备注。
                    ConsultationGroupMember consultationGroupMember = consultationGroupMemberMapper.selectById(userId);
                    String memberOpenId = consultationGroupMember.getMemberOpenId();
                    String mobile = consultationGroupMember.getMobile();
                    Doctor doctor = doctorMapper.selectById(patientInfo.getDoctorId());
                    if ((memberOpenId != null && memberOpenId.equals(doctor.getOpenId())) || (mobile != null && mobile.equals(doctor.getMobile()))) {
                        patient.setDoctorRemark(patientInfo.getDoctorRemark());
                    }
                }
                // 设置一下患者的备注
                continue;
            }
            if (groupMember.getMemberRole().equals(UserType.UCENTER_NURSING_STAFF)) {
                nursing = groupMember;
                continue;
            }
            groupMember.setIsMe(false);
            groupMemberList.add(groupMember);
        }
        groupMemberList.sort((o1, o2) -> {
            if (o1.getUpdateTime().isAfter(o2.getUpdateTime()) || o1.getUpdateTime().isEqual(o2.getUpdateTime())) {
                return 1;
            }
            return -1;
        });
        if (nursing != null) {
            groupMemberList.add(nursing);
        }
        if (patient != null) {
            groupMemberList.add(patient);
        }
        if (groupMemberMe != null) {
            groupMemberList.add(groupMemberMe);
        }
        Collections.reverse(groupMemberList);
        return groupMemberList;
    }

    /**
     * 查询除IMAccount之外的医助和医生
     * 并且可以 接收消息的医生和医助
     * @param groupId
     * @param noSenderImAccount
     * @return
     */
    @Override
    public List<ConsultationGroupMember> getConsultationMemberNoImAccount(Long groupId, String noSenderImAccount) {
        LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, groupId);
        lbqWrapper.in(ConsultationGroupMember::getMemberRole, UserType.UCENTER_NURSING_STAFF, ConsultationGroupMember.ROLE_DOCTOR);
        lbqWrapper.in(ConsultationGroupMember::getMemberStatus, ConsultationGroupMember.JOINED, ConsultationGroupMember.CANNOT_REMOVED);
        if (StrUtil.isNotEmpty(noSenderImAccount)) {
            lbqWrapper.ne(ConsultationGroupMember::getMemberImAccount, noSenderImAccount);
        }

        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(lbqWrapper);
        return groupMembers;
    }


    @Override
    public void memberAcceptOrReject(Long groupId, String imAccount, String action, String rejectMessage) {

        ConsultationGroupMember member = getConsultationMemberByImAccount(groupId, imAccount);
        if (member.getMemberStatus().equals(ConsultationGroupMember.INVITE)) {
            if (ConsultationGroupAcceptOrReject.ACCEPT.equals(action)) {
                // 接受进入讨论组
                acceptJoinGroup(member);
            } else if (ConsultationGroupAcceptOrReject.REJECT.equals(action)) {
                // 拒绝进入讨论组
                rejectJsonGroup(member, rejectMessage);
            }
        }

    }

    /**
     * 删除拒绝状态的讨论组
     * @param groupId
     * @param doctorImAccount
     */
    @Override
    public void deleteRejectMember(Long groupId, String doctorImAccount) {

        ConsultationGroupMember member = getConsultationMemberByImAccount(groupId, doctorImAccount);
        if (member.getMemberStatus().equals(ConsultationGroupMember.REFUSE)) {
            consultationGroupMemberMapper.deleteById(member);
        } else {
            throw new BizException("不是拒绝状态，无法删除");
        }

    }

    /**
     * 医生修改医助的时候， 将医生下患者的会诊小组，更换医助。
     * 会诊组结束的， 将旧医助移除， 新医助加入
     * 会诊组进行中， 判断会诊组的管理员是否为医助， 如果是医助，则修改管理员为新医助，并将新医助加入到im群组中，同时添加新医助到会诊组中
     *                                      旧的医助 被移出 im 群聊。 并在会诊组中删除
     */
    @Override
    public void changeNursingToNewNursing(Long doctorId, NursingStaff nursingStaff) {
        // 根据医生ID查询患者。找到有多少会诊组
        List<ConsultationGroupMember> memberList = consultationGroupMemberMapper.selectList(Wraps.<ConsultationGroupMember>lbQ()
                .select(SuperEntity::getId, ConsultationGroupMember::getConsultationGroupId)
                .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_PATIENT)
                .apply(" patient_id in (select id from u_user_patient where doctor_id = " + doctorId + " ) "));
        Set<Long> consultationGroupId = memberList.stream().map(ConsultationGroupMember::getConsultationGroupId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(consultationGroupId)) {
            return;
        }
        Map<String, String> dictItem = getDictItem();
        List<ConsultationGroup> groups = baseMapper.selectBatchIds(consultationGroupId);
        for (ConsultationGroup group : groups) {
            String consultationStatus = group.getConsultationStatus();
            if (ConsultationGroup.FINISH.equals(consultationStatus)) {
                // 将旧的医助移除， 新的医助添加进来。
                if (group.getNurseId() != null) {
                    group.setNurseId(nursingStaff.getId());
                }
                consultationGroupMemberMapper.delete(Wraps.<ConsultationGroupMember>lbQ()
                        .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_NURSING_STAFF)
                        .eq(ConsultationGroupMember::getConsultationGroupId, group.getId()));
                ConsultationGroupMember groupMember = new ConsultationGroupMember(group.getId(), nursingStaff, ConsultationGroupMember.REMOVED, dictItem.get(ASSISTANT));
                consultationGroupMemberMapper.insert(groupMember);
            } else {
                // 先把新医助加入到会诊组中。
                // 把旧医助移出会诊组。
                boolean administrator = false;
                if (group.getNurseId() != null && group.getDoctorId() == null) {
                    // 医助是管理员
                    administrator = true;
                }
                group.setNurseId(nursingStaff.getId());
                ConsultationGroupMember member = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                        .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_NURSING_STAFF)
                        .eq(ConsultationGroupMember::getConsultationGroupId, group.getId())
                        .last(" limit 0,1 "));
                if (Objects.nonNull(member)) {
                    ConsultationGroupMember groupMember = new ConsultationGroupMember(group.getId(), nursingStaff, member.getMemberStatus(), dictItem.get(ASSISTANT));
                    consultationGroupMemberMapper.insert(groupMember);
                    // 新医助 加入im群组
                    if (administrator || member.getMemberStatus() == ConsultationGroupMember.JOINED) {
                        try {
                            groupImApi.addUserToGroup(group.getImGroupId(), groupMember.getMemberImAccount(), administrator);
                        } catch (Exception e) {
                            log.error("加入环信群组失败");
                            //   throw new BizException("加入环信群组失败");
                        }
                    }
                    if (ConsultationGroupMember.JOINED == member.getMemberStatus() || ConsultationGroupMember.CANNOT_REMOVED == member.getMemberStatus()) {
                        // 将旧医助 移出 im 群组。
                        // 新医助 加入im群组
                        try {
                            groupImApi.removeUserFromGroup(group.getImGroupId(), groupMember.getMemberImAccount());
                        } catch (Exception e) {
                            log.error("加入环信群组失败");
                            //   throw new BizException("加入环信群组失败");
                        }
                    }
                    consultationGroupMemberMapper.deleteById(member.getId());

                }
            }
            baseMapper.updateById(group);
        }


    }

    /**
     * 医助将数据转移到新医助下
     * @param formNursingId
     * @param nursingStaff
     */
    @Override
    public void changeNursing(Long formNursingId, NursingStaff nursingStaff) {
        // 先查询 原 医助下面的数据
        List<ConsultationGroupMember> memberList = consultationGroupMemberMapper.selectList(Wraps.<ConsultationGroupMember>lbQ()
                .select(SuperEntity::getId, ConsultationGroupMember::getConsultationGroupId)
                .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_NURSING_STAFF)
                .eq(ConsultationGroupMember::getMemberUserId, formNursingId));

        if (CollUtil.isEmpty(memberList)) {
            return;
        }
        Map<String, String> dictItem = getDictItem();
        Set<Long> groupIds = memberList.stream().map(ConsultationGroupMember::getConsultationGroupId).collect(Collectors.toSet());
        List<ConsultationGroup> consultationGroups = baseMapper.selectBatchIds(groupIds);
        for (ConsultationGroup group : consultationGroups) {
            String consultationStatus = group.getConsultationStatus();
            if (ConsultationGroup.FINISH.equals(consultationStatus)) {
                // 将旧的医助移除， 新的医助添加进来。
                if (group.getNurseId() != null) {
                    group.setNurseId(nursingStaff.getId());
                }
                consultationGroupMemberMapper.delete(Wraps.<ConsultationGroupMember>lbQ()
                        .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_NURSING_STAFF)
                        .eq(ConsultationGroupMember::getConsultationGroupId, group.getId()));
                ConsultationGroupMember groupMember = new ConsultationGroupMember(group.getId(), nursingStaff, ConsultationGroupMember.REMOVED, dictItem.get(ASSISTANT));
                consultationGroupMemberMapper.insert(groupMember);
            } else {
                // 把旧医助移出会诊组。
                // 把新医助加入到会诊组中。
                boolean administrator = false;
                if (group.getNurseId() != null && group.getDoctorId() == null) {
                    // 医助是管理员
                    administrator = true;
                }
                group.setNurseId(nursingStaff.getId());
                ConsultationGroupMember member = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                        .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_NURSING_STAFF)
                        .eq(ConsultationGroupMember::getConsultationGroupId, group.getId())
                        .last(" limit 0,1 "));
                if (Objects.nonNull(member)) {
                    ConsultationGroupMember groupMember = new ConsultationGroupMember(group.getId(), nursingStaff, member.getMemberStatus(),  dictItem.get(ASSISTANT));
                    consultationGroupMemberMapper.insert(groupMember);
                    // 新医助 加入im群组
                    if (administrator || member.getMemberStatus() == ConsultationGroupMember.JOINED) {
                        try {
                            groupImApi.addUserToGroup(group.getImGroupId(), groupMember.getMemberImAccount(), administrator);
                        } catch (Exception e) {
                            log.error("加入环信群组失败");
                            //   throw new BizException("加入环信群组失败");
                        }
                    }
                    if (ConsultationGroupMember.JOINED == member.getMemberStatus() || ConsultationGroupMember.CANNOT_REMOVED == member.getMemberStatus()) {
                        // 将旧医助 移出 im 群组。
                        // 新医助 加入im群组
                        try {
                            groupImApi.removeUserFromGroup(group.getImGroupId(), groupMember.getMemberImAccount());
                        } catch (Exception e) {
                            log.error("加入环信群组失败");
                            //   throw new BizException("加入环信群组失败");
                        }
                    }
                    consultationGroupMemberMapper.deleteById(member.getId());

                }
            }
        }

    }

    /**
     * 小组下 IM 账号绑定人员信息
     * @param groupId
     * @param imAccount
     * @return
     */
    @Override
    public ConsultationGroupMember groupMember(Long groupId, String imAccount) {
        ConsultationGroupMember groupMember = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                .eq(ConsultationGroupMember::getConsultationGroupId, groupId)
                .eq(ConsultationGroupMember::getMemberImAccount, imAccount)
                .last(" limit 0, 1 "));
        return groupMember;
    }

    /**
     * 接受加入到讨论组
     * 医生接收进入讨论组
     * @param member
     */
    private void acceptJoinGroup(ConsultationGroupMember member) {

        ConsultationGroup group = baseMapper.selectById(member.getConsultationGroupId());
        // 判断进入讨论组的这个医生，是不是患者的医生
        ConsultationGroupMember patientMember = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                .eq(ConsultationGroupMember::getMemberRole, UserType.UCENTER_PATIENT)
                .eq(ConsultationGroupMember::getConsultationGroupId, member.getConsultationGroupId())
                .last(" limit 0, 1 "));
        boolean newOwner = false;
        if (Objects.nonNull(patientMember)) {
            Long patientId = patientMember.getPatientId();
            Patient patient = getPatientService().getBasePatientById(patientId);
            Long doctorId = patient.getDoctorId();
            if (Objects.equals(member.getMemberUserId(), doctorId)) {
                newOwner = true;
                group.setDoctorId(doctorId);
            }
        }

        try {
            groupImApi.addUserToGroup(group.getImGroupId(), member.getMemberImAccount(), newOwner);
        } catch (Exception e) {
            log.error("加入环信群组失败");
//            throw new BizException("加入环信群组失败");
        }
        member.setMemberStatus(ConsultationGroupMember.JOINED);
        consultationGroupMemberMapper.updateById(member);
        if (newOwner) {
            baseMapper.updateById(group);
        }
    }



    /**
     * 拒绝加入到讨论组中来
     * @param member
     * @param rejectMessage
     */
    private void rejectJsonGroup(ConsultationGroupMember member, String rejectMessage) {
        member.setMemberStatus(ConsultationGroupMember.REFUSE);
        member.setMemberRefuseMessage(rejectMessage);
        consultationGroupMemberMapper.updateById(member);
        SystemMsg systemMsg = new SystemMsg();
        systemMsg.setMsgType(SystemMsgType.consultation);
        systemMsg.setUserId(member.getInvitePeople());
        systemMsg.setUserRole(member.getInvitePeopleRole());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", member.getMemberName() + "拒绝了你的讨论小组邀请");
        jsonObject.put("reason", rejectMessage);
        systemMsg.setContent(jsonObject.toJSONString());
        systemMsgMapper.insert(systemMsg);


    }

    /**
     * @Author yangShuai
     * @Description 获取群组发送人详细信息
     * @Date 2021/6/4 10:43
     *
     * @param consultationGroupId
     * @param imAccount
     * @return com.caring.sass.user.entity.ConsultationGroupMember
     */
    @Override
    public ConsultationGroupMember getConsultationMemberByImAccount(Long consultationGroupId, String imAccount) {
        LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, consultationGroupId);
        lbqWrapper.eq(ConsultationGroupMember::getMemberImAccount, imAccount);
        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(groupMembers)) {
            throw new BizException("发送人不存在");
        }
        return groupMembers.get(0);
    }

    @Override
    public ConsultationGroupMember getConsultationMemberByMobile(Long consultationGroupId, String mobile) {
        LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, consultationGroupId);
        lbqWrapper.eq(ConsultationGroupMember::getMobile, mobile);
        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(groupMembers)) {
            return null;
        }
        return groupMembers.get(0);
    }

    @Override
    public ConsultationGroupMember getConsultationMemberByOpenId(Long consultationGroupId, String openId) {
        LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, consultationGroupId);
        lbqWrapper.eq(ConsultationGroupMember::getMemberOpenId, openId);
        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(groupMembers)) {
            return null;
        }
        return groupMembers.get(0);
    }

    /**
     * 保存扫码加入的成员
     * @param consultationGroupMember
     * @return
     */
    @Override
    public ConsultationGroupMember saveMember(ConsultationGroupMember consultationGroupMember) {
        consultationGroupMember.setCreateTime(LocalDateTime.now());
        consultationGroupMemberMapper.insert(consultationGroupMember);
        return consultationGroupMember;
    }


    @Override
    public void updateMemberById(ConsultationGroupMember consultationGroupMember) {
        consultationGroupMemberMapper.updateById(consultationGroupMember);
    }


    @Override
    public ConsultationGroupMember getMemberById(Long memberId, boolean hasGroup) {
        ConsultationGroupMember groupMember = consultationGroupMemberMapper.selectById(memberId);
        if (hasGroup) {
            ConsultationGroup group = baseMapper.selectById(groupMember.getConsultationGroupId());
            groupMember.setGroup(group);
        }
        return groupMember;
    }


    @Transactional
    @Override
    public ConsultationGroup deleteAllByGroupId(Long groupId) {
        ConsultationGroup group = baseMapper.selectById(groupId);
        if (Objects.nonNull(group)) {
            LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
            lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, groupId);
            consultationGroupMemberMapper.delete(lbqWrapper);
            baseMapper.deleteById(groupId);
            patientSystemMessageApi.deleteByBusinessId(PlanFunctionTypeEnum.CASE_DISCUSSION.getCode(), groupId);
        }
        return group;
    }


    @Override
    public ConsultationGroup theEndConsultation(Long groupId) {
        ConsultationGroup group = baseMapper.selectById(groupId);
        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            throw new BizException(ExceptionCode.JWT_PARSER_TOKEN_FAIL.getCode(), ExceptionCode.JWT_PARSER_TOKEN_FAIL.getMsg());
        }
        ConsultationGroupMember groupMember = consultationGroupMemberMapper.selectOne(Wraps.<ConsultationGroupMember>lbQ()
                .eq(ConsultationGroupMember::getConsultationGroupId, groupId)
                .eq(ConsultationGroupMember::getMemberUserId, userId)
                .last(" limit 0,1 ")
        );
        if (Objects.nonNull(groupMember) && groupMember.getMemberStatus().equals(ConsultationGroupMember.REMOVED)) {
            throw new BizException("您已被移出小组。请返回列表");
        }

        if (ConsultationGroup.FINISH.equals(group.getConsultationStatus())) {
            throw new BizException("会诊小组已经结束");
        }
        group.setConsultationStatus(ConsultationGroup.FINISH);

        LocalDateTime now = LocalDateTime.now();
        group.setEndTime(now);
        Duration between = Duration.between(group.getCreateTime(), now);
        if (between != null) {
            group.setContinued(between.toMinutes());
        } else {
            group.setContinued(0L);
        }
        baseMapper.updateById(group);
        try {
            sendEndMessage(group, BaseContextHandler.getTenant());
        } catch (Exception e) {
            log.info("发送结束会诊消息失败");
        }
        // 把小组中所有的人员 设置为 已结束状态
        UpdateWrapper<ConsultationGroupMember> wrapper = new UpdateWrapper<>();
        wrapper.set("member_status", ConsultationGroupMember.REMOVED);
        wrapper.eq("consultation_group_id", groupId);
        consultationGroupMemberMapper.update(new ConsultationGroupMember(), wrapper);
        return group;
    }

    /**
     * 发送会诊结束消息
     * @param group
     */
    public void sendEndMessage(ConsultationGroup group, String tenantCode) {
        Config config = new Config();
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, group.getId());
        lbqWrapper.isNotNull(ConsultationGroupMember::getMemberOpenId);
        lbqWrapper.in(ConsultationGroupMember::getMemberStatus, ConsultationGroupMember.CANNOT_REMOVED, ConsultationGroupMember.JOINED);
        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(groupMembers)) {
            return;
        }

        R<Config> apiConfig = weiXinApi.getConfig(config);
        Boolean isSuccess = apiConfig.getIsSuccess();
        R<String> accountType = tenantApi.queryOfficialAccountType(tenantCode);
        String accountTypeData = accountType.getData();
        if (isSuccess != null && isSuccess) {
            Config configData = apiConfig.getData();
            for (ConsultationGroupMember groupMember : groupMembers) {
                if (groupMember.getMemberRole() != null && UserType.UCENTER_PATIENT.equals(groupMember.getMemberRole())) {
                    Patient patient = patientService.getBasePatientById(groupMember.getPatientId());
                    if (Objects.nonNull(patient)) {
                        MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(PlanFunctionTypeEnum.CASE_DISCUSSION.getCode() + "_END",
                                group.getId(),
                                null, groupMember.getPatientId(), LocalDateTime.now(), patient.getDoctorName(), tenantCode, 2);
                        systemMessageSaveDTO.createPushContent( null, null, null);
                        patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);
                    }
                }
                // 企业服务号。给用户发送模板消息
                if (TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER.toString().equals( accountTypeData)) {
                    weiXinService.sendConsultationTemplateMessage(configData.getAppId(), group.getGroupName(), TemplateMessageIndefiner.CONSULTATION_END,
                            groupMember.getMemberOpenId(), null, groupMember.getMemberName());
                }
            }

        }

    }


    @Async
    @Override
    public void handlerSendMessage(ConsultationGroup group, String tenantCode) {
        Long groupId = group.getId();
        BaseContextHandler.setTenant(tenantCode);

        R<String> accountType = tenantApi.queryOfficialAccountType(tenantCode);
        String accountTypeData = accountType.getData();
        // 个人服务号不需要处理此类消息
        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals( accountTypeData)) {
            return;
        }
        LbqWrapper<ConsultationGroupMember> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ConsultationGroupMember::getConsultationGroupId, groupId);
        lbqWrapper.eq(ConsultationGroupMember::getReceiveWeiXinTemplate, ConsultationGroupMember.RECEIVE_WEIXIN_TEMPLATE_YES);
        lbqWrapper.in(ConsultationGroupMember::getMemberStatus,
                ConsultationGroupMember.CANNOT_REMOVED,
                ConsultationGroupMember.JOINED);
        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(groupMembers)) {
            return;
        }

        Config config = new Config();
        R<Config> apiConfig = weiXinApi.getConfig(config);
        Boolean isSuccess = apiConfig.getIsSuccess();

        if (isSuccess != null && isSuccess) {
            Config configData = apiConfig.getData();
            R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
            Tenant tenant = tenantR.getData();
            String consultationNotice = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), String.format(H5Router.CONSULTATION_NOTICE, groupId));

            // 医生端增加了病历讨论功能。 所以修该为跳转到医生端去聊天
            String doctorConsultationNotice = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                    String.format(H5Router.CONSULTATION_DOCTOR_NOTICE, groupId));
            for (ConsultationGroupMember groupMember : groupMembers) {
                Boolean online = imService.online(groupMember.getMemberImAccount());
                if (online != null && !online) {
                    if (groupMember.getMemberRole() != null && UserType.UCENTER_PATIENT.equals(groupMember.getMemberRole())) {
                        weiXinService.sendConsultationTemplateMessage(configData.getAppId(), group.getGroupName(), TemplateMessageIndefiner.CONSULTATION_PROCESSING,
                                groupMember.getMemberOpenId(), consultationNotice, groupMember.getMemberName());
                        Patient patient = patientService.getBasePatientById(groupMember.getPatientId());
                        if (Objects.nonNull(patient)) {
                            MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(PlanFunctionTypeEnum.CASE_DISCUSSION.getCode() + "_RUNNING",
                                    group.getId(),
                                    consultationNotice, groupMember.getPatientId(), LocalDateTime.now(), patient.getDoctorName(), tenantCode, 1);
                            systemMessageSaveDTO.createPushContent(null, null, null);
                            patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);
                        }
                    } else {
                        // 需要判断一下这个医生是否已经退出登录
                        Integer count = doctorMapper.selectCount(Wraps.<Doctor>lbQ().eq(Doctor::getOpenId, groupMember.getMemberOpenId()).eq(Doctor::getWxStatus, 1));
                        if (count != null && count > 0) {
                            weiXinService.sendConsultationTemplateMessage(configData.getAppId(), group.getGroupName(), TemplateMessageIndefiner.CONSULTATION_PROCESSING,
                                    groupMember.getMemberOpenId(), doctorConsultationNotice, groupMember.getMemberName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除患者时。解散现有的会诊组。并且删除聊天记录。删除会诊组记录，删除成员列表
     * @param patient
     */
    @Override
    public void deletePatientEvent(Patient patient) {

        List<ConsultationGroupMember> groupMembers = consultationGroupMemberMapper.selectList(Wraps.<ConsultationGroupMember>lbQ().eq(ConsultationGroupMember::getPatientId, patient.getId()));
        if (CollUtil.isEmpty(groupMembers)) {
            return;
        }
        List<Long> consultationGroupId = groupMembers.stream().map(ConsultationGroupMember::getConsultationGroupId).collect(Collectors.toList());
        List<ConsultationGroup> consultationGroups = baseMapper.selectBatchIds(consultationGroupId);
        if (CollUtil.isEmpty(consultationGroups)) {
            return;
        }
        for (ConsultationGroup group : consultationGroups) {
            try {
                if (ConsultationGroup.PROCESSING.equals(group)) {
                    groupImApi.deleteImGroup(group.getImGroupId());
                    groupImApi.deleteImGroupMessage(group.getId());
                }
            } catch (Exception e) {
                log.error("删除IM会诊群组异常");
            }
        }
        baseMapper.deleteBatchIds(consultationGroupId);
        consultationGroupMemberMapper.delete(Wraps.<ConsultationGroupMember>lbQ().in(ConsultationGroupMember::getConsultationGroupId, consultationGroupId));

    }


    /**
     * 使用ID获取病例讨论。不区分租户
     * @param id
     * @return
     */
    @Override
    public ConsultationGroup getByIdNoTenant(Long id) {
        return baseMapper.selectByIdNoTenant(id);
    }
}

