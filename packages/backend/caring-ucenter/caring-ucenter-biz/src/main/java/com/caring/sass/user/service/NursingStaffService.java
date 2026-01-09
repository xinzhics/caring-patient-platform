package com.caring.sass.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.NursingStaff;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 用户-医助
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
public interface NursingStaffService extends SuperService<NursingStaff> {

    NursingStaff createNursingStaff(NursingStaff nursingStaff);

    NursingStaff findByLoginName(String loginName);

    NursingStaff getByMobile(String phone);

    NursingStaff bindOrg(NursingStaffBindOrg nursingStaffBindOrg);

    NursingStaff resetPassword(NursingStaffResetPw nursingStaffResetPw);

    NursingStaff updatePassword(NursingStaffFindPw nursingStaffFindPw);

    /**
     * 医助群发消息
     * @param chat
     */
    void sendMoreChatToWeiXin(Chat chat);

    List<Chat> sendChatToWeiXin(Chat chat, int group);

    NursingStaff getBaseNursingStaffById(Long id);

    /**
     * 数据权限 分页
     *
     * @param page
     * @param wrapper
     */
    IPage<NursingStaff> findPage(IPage<NursingStaff> page, LbqWrapper<NursingStaff> wrapper);

    @Transactional(rollbackFor = Exception.class)
    Boolean changeNursingOrganId(Long nursingId, Long organId);

    /**
     * 将医助的下小组，医生，患者变更到新医助下
     * @param formNursingId
     * @param toNursingId
     * @return
     */
    Boolean changeBelongingToNursingId(Long formNursingId, Long toNursingId);

    /**
     * 查询app的小组和医生。并统计人员
     * @param nursingId
     * @return
     */
    AppMyDoctorDto getAppDoctor(Long nursingId);

    /**
     * 更新机构下 医助，医生，患者的机构名称
     * @param orgId
     * @param orgName
     */
    void updateUserOrgName(long orgId, String orgName);

    /**
     * 医助更新自己名字之后。 同步 患者，医生，小组中的冗余
     * @param nursingId
     * @param userName
     */
    void changeName(Long nursingId, String userName);


    /**
     * 查询医助所在机构及下属机构id
     * @param nursingId
     * @return
     */
    List<Long> getNursingOrgIds(Long nursingId);

    /**
     * 系统以医助身份发送im提示消息
     * @param receiverImAccount
     */
    void sendPatientImRemind(String receiverImAccount);


    void desensitization(List<NursingStaff> records);

    void sendAtImWeiXinTemplateMessage(String tenantCode, Long nursingId,
                                       @Length(max = 32, message = "医生名字长度不能超过32") String senderImName,
                                       @Length(max = 255, message = "环信账号长度不能超过255") String imAccount,
                                       Long patientId,
                                       @Length(max = 180, message = "真实姓名长度不能超过180") String patientName);

    void sendWeixinTemplateMessage(String tenantCode, Long nursingId,
                                   MediaType type, String content,
                                   Long patientId,
                                   String patientName,
                                   @Length(max = 32, message = "医生名字长度不能超过32") String senderName);
}
