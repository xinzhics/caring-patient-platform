package com.caring.sass.user.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Group;
import com.caring.sass.user.entity.Patient;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 患者表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
public interface PatientService extends SuperService<Patient> {

    Patient subscribe(WxSubscribeDto wxSubscribeDto);

    Patient createNewPatient(Tenant tenant, String wxAppId, Long doctorId, String openId, String unionId);

    Patient findByOpenId(String openId);

    Patient findByMobile(String mobile);

    boolean checkOpenId(String openId, String checkIsDoctor);

    void unsubscribe(Patient patient);

    /**
     * @Author yangShuai
     * @Description 统计医生或者医助下全部患者
     * @Date 2020/10/12 16:43
     *
     * @return long
     */
    long countPatientNoStatus(Long roleId, String role);

    /**
     * @Author yangShuai
     * @Description 统计医生或者医助下 某状态的患者
     * @Date 2020/10/12 16:48
     *
     * @return int
     */
    int countPatientByStatus(Integer status, Long userId, String role);

    @Deprecated
    long countPatientByGroupId(Long groupId);

    long countPatientByDiagnosisId(Long userId, String diagnosisId, String role);

    JSONArray countDiagnosisId(Long userId, String dimension);


    Patient getBasePatientById(Long id);

    Chat sendChat(Chat chat, Integer forcedManualReply);

    List<ImGroupUser> getImGroupUser(Long patientId);

    void diseaseInformationStatus(Long patientId);

    void agreeAgreement(Long patientId);

    void completeEnterGroup(Long patientId);

    /**
     * 数据权限 分页
     *
     * @param page
     * @param wrapper
     */
    IPage<Patient> findPage(IPage<Patient> page, Wrapper<Patient> wrapper);

    /**
     * 给患者添加微信的 患者标签
     */
    void createWeiXinTag(Patient subscribe, String tenant);

    void sendAtImWeiXinTemplateMessage(String tenantCode, Long patientId, String sendUserName, String userRole);

    /**
     * 医助医生给患者发送消息后。给患者提醒
     * @param tenantCode
     * @param status
     * @param imAccount
     * @param openId
     * @param type
     * @param content
     * @param patientId
     * @param patientName
     * @param sendUserName
     * @param userRole
     */
    void sendWeiXinTemplateMessage(String tenantCode, Integer status, String imAccount, String openId, MediaType type, String content,
                                   Long patientId, String patientName, String sendUserName, String userRole);

    /**
     * 假设患者肯定是存在的。
     * 查询医生的信息。 直接将患者的关联字段修改为新的医生和医助
     * @param patientId
     * @param doctorId
     * @return
     */
    Boolean changeDoctor(Long patientId, Long doctorId);


    /**
     * 批量转移医生的患者到其他医生
     * @param changeDoctorMore
     * @return
     */
    Boolean changeDoctorMore(ChangeDoctorMore changeDoctorMore);

    /**
     * 统计小组下有多少的患者 2.4
     * @param groups
     */
    void countPatientByGroupId(List<Group> groups);

    /**
     * 统计医生下有多少的患者
     * @param doctorList
     */
    void countPatientByDoctor(List<Doctor> doctorList);

    /**
     * 获取群组的详细成员
     * @param patientId
     * @return
     */
    ImGroupDetail getImGroupDetail(Long patientId, Long nursingId, Long doctorId);


    void unregisteredReminder();

    /**
     * 更新机构下 医助，医生，患者的机构名称
     * @param orgId
     * @param orgName
     */
    void updateUserOrgName(long orgId, String orgName);

    void updateByOpenId(WxOAuth2UserInfo wxUser);

    /**
     * 查询医生对患者备注
     * @param ids
     * @return
     */
    Map<Long, String> findDoctorPatientRemark(List<Long> ids);

    /**
     * 查询医助对患者备注
     * @param ids
     * @return
     */
    Map<Long, String> findNursingPatientRemark(List<Long> ids);

    /**
     * 通过ID查询患者的基本信息，医生医助的备注，聊天开关状态。
     * @param ids
     * @return
     */
    Map<Long, Patient> findPatientBaseInfoByIds(List<Long> ids);

    /**
     * 医助设置退出聊天
     * @param patientId
     * @param exitChat
     * @param dictionaryMap
     */
    void nursingExitChat(Long patientId, Integer exitChat, Map<String, String> dictionaryMap);

    /**
     * 医生设置退出聊天
     * @param patientId
     * @param exitChat
     * @param dictionaryMap
     */
    void doctorExitChat(Long patientId, Integer exitChat, Map<String, String> dictionaryMap);


    List<String> getPatientImAccountByDoctorId(Long doctorId);

    Patient findByUnionId(String unionId);

    void deletePatient(Long id);

    void desensitization(List<Patient> records);


    /**
     * 个人服务号的患者注册
     * @param patientRegister
     * @return
     */
    Patient registerPatient(PatientRegister patientRegister);


    void sendManualTemplate(Long patientId);

}
