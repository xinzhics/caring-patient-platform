package com.caring.sass.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.ConsultationGroupMemberAddSomeDTO;
import com.caring.sass.user.dto.ConsultationGroupPage;
import com.caring.sass.user.entity.*;

import java.util.List;

/**
 * @ClassName ConsultationService
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 13:56
 * @Version 1.0
 **/
public interface ConsultationGroupService extends SuperService<ConsultationGroup> {


    /**
     * @Author yangShuai
     * @Description 生成会诊小组的初始人员，人员不可移除
     * @Date 2021/6/2 14:18
     *
     * @param consultationGroup
     * @param nursingStaff
     * @param patientIds
     * @return void
     */
    void appSaveMember(ConsultationGroup consultationGroup, NursingStaff nursingStaff, List<Long> patientIds, Tenant tenant);


    /**
     * 医生 创建病例讨论。生成病例组的人员
     * @param consultationGroup
     * @param doctor
     * @param patientIds
     */
    void doctorSaveMember(ConsultationGroup consultationGroup, Doctor doctor, List<Long> patientIds, Tenant tenant);




    /**
     * @Author yangShuai
     * @Description 查询会诊小组的详细
     * @Date 2021/6/2 14:54
     *
     * @param consultationGroupId
     * @return com.caring.sass.user.entity.ConsultationGroup
     */
    ConsultationGroup getConsultationGroupDetail(Long consultationGroupId, String openId, String imAccount);


    /**
     * @Author yangShuai
     * @Description 获取群组发送人详细信息
     * @Date 2021/6/4 10:43
     *
     * @param consultationGroupId
     * @param imAccount
     * @return com.caring.sass.user.entity.ConsultationGroupMember
     */
    ConsultationGroupMember getConsultationMemberByImAccount(Long consultationGroupId, String imAccount);


    ConsultationGroupMember getConsultationMemberByOpenId(Long consultationGroupId, String openId);


    ConsultationGroupMember getConsultationMemberByMobile(Long consultationGroupId, String mobile);

    /**
     * 保存扫码加入的成员
     * @param consultationGroupMember
     * @return
     */
    ConsultationGroupMember saveMember(ConsultationGroupMember consultationGroupMember);


    void updateMemberById(ConsultationGroupMember consultationGroupMember);

    /**
     * 获取一个群组成员
     * @param memberId
     * @param hasGroup
     * @return
     */
    ConsultationGroupMember getMemberById(Long memberId, boolean hasGroup);


    /**
     * 删除会诊
     * @param groupId
     * @return imGroupId
     */
    ConsultationGroup deleteAllByGroupId(Long groupId);

    /**
     * 结束会诊
     * @param groupId
     * @return imGroupId
     */
    ConsultationGroup theEndConsultation(Long groupId);

    /**
     * 处理消息发送后，给患者的模板消息推送
     * @param group
     */
    void handlerSendMessage(ConsultationGroup group, String tenantCode);


    Integer countInviteNumber(Long memberUserId, String memberRole);

    /**
     * 给病例讨论组 邀请医生加入。
     * @param memberAddSomeDTO
     *
     */
    void addMemberToGroup(ConsultationGroupMemberAddSomeDTO memberAddSomeDTO);


    /**
     * app和医生的病历讨论分页列表
     * @param page
     * @param model
     */
    void consultationGroupPage(IPage<ConsultationGroup> page, ConsultationGroupPage model);

    /**
     * 查询除IMAccount之外的医助和医生
     * @param groupId
     * @param noSenderImAccount
     * @return
     */
    List<ConsultationGroupMember> getConsultationMemberNoImAccount(Long groupId, String noSenderImAccount);

    /**
     * 接受或者拒绝讨论组的邀请
     * @param groupId
     * @param imAccount
     * @param action
     * @param rejectMessage
     */
    void memberAcceptOrReject(Long groupId, String imAccount, String action, String rejectMessage);

    /**
     * 删除拒绝的讨论组邀请
     * @param groupId
     * @param doctorImAccount
     */
    void deleteRejectMember(Long groupId, String doctorImAccount);

    /**
     * 小组下 IM 账号绑定人员信息
     * @param groupId
     * @param imAccount
     * @return
     */
    ConsultationGroupMember groupMember(Long groupId, String imAccount);

    /**
     * 更换群组中的医助为 新医助
     * @param doctorId
     * @param nursingStaff
     */
    void changeNursingToNewNursing(Long doctorId, NursingStaff nursingStaff);

    /**
     * 医助把数据转移到新医助下面
     * @param formNursingId
     * @param nursingStaff
     */
    void changeNursing(Long formNursingId, NursingStaff nursingStaff);

    /**
     * 患者被删除后的事情
     * @param patient
     */
    void deletePatientEvent(Patient patient);

    /**
     * 使用ID获取病例讨论。不区分租户
     * @param id
     * @return
     */
    ConsultationGroup getByIdNoTenant(Long id);
}
