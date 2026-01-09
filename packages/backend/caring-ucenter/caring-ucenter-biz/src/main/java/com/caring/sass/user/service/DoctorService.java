package com.caring.sass.user.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatGroupSend;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Group;
import com.caring.sass.wx.dto.config.QrCodeDto;

import java.util.List;
import java.util.Locale;

/**
 * <p>
 * 业务接口
 * 医生表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
public interface DoctorService extends SuperService<Doctor> {

    Doctor findByOpenId(String openId);


    Doctor checkOpenId(WxSubscribeDto wxSubscribeDto);

    Doctor createDoctor(Doctor doctor);

    QrCodeDto generatePatientFocusQrCode(String wxAppId, Long doctorId);

    QrCodeDto englishGeneratePatientFocusQrCode(String wxAppId, Long doctorId);

    void subscribe(String wxAppId, String openId, Doctor doctor, Locale locale);

    Doctor findByLoginName(String loginName);

    /**
     * @return java.lang.Object
     * @Author yangShuai
     * @Description 获取医生的 小组内排名
     * @Date 2020/10/12 17:07
     */
    Integer getDoctorRanking(Long groupId, Long doctorId);

    long countDoctorByGroupId(Long groupId);

    int countDoctorByNursing(Long nursingId);

    Doctor getByMobile(String phone);

    Doctor getBaseDoctorAndImOpenById(Long id);

    /**
     * 医生身份群发消息
     * @param chatGroupSend
     * @return
     */
    Long doctorSendGroupMsg(ChatGroupSend chatGroupSend);

    void sendMoreChatToWeiXin(Chat chat);

    Chat sendChatToWeiXin(Chat chat);

    void sendImWeiXinTemplateMessage(String tenantCode, Doctor doctor, MediaType type, String content, String prefix,
                                     String patientImAccount, Long patientId, String templateMessageIndefiner);

    void sendAtImWeiXinTemplateMessage(String tenantCode, Long doctorId, String senderName, String patientImAccount, Long patientId, String patientName);

    /**
     * 数据权限 分页
     *
     * @param page
     * @param wrapper
     */
    IPage<Doctor> findPage(IPage<Doctor> page, LbqWrapper<Doctor> wrapper);

    /**
     * 查询医生 通过openId
     * @param openId
     * @return
     */
    Doctor getBaseInfoByOpenId(String openId);

    /**
     * 查询和doctorId 同一个小组的其他医生基本信息
     * @param doctorId
     * @return
     */
    List<Doctor> getGroupDoctorBaseInfoNoMe(Long doctorId);

    /**
     * 统计小组下有多少的医生
     * @param groups
     */
    void countDoctorByGroupId(List<Group> groups);

    /**
     * 绑定用户的标签
     * @param wxAppId
     * @param openId
     */
    void bindUserTags(String wxAppId, String openId);

    /**
     * 直接发送医生的登录链接
     *
     * @param wxAppId
     * @param openId
     * @param locale
     */
    void subscribeChangeDoctor(String wxAppId, String openId, Locale locale);

    /**
     * 异步批量更新医生的 名片
     * @param doctors 存在时， 更新集合内的医生
     * @param lbqWrapper doctors 为空时， 使用此条件查询医生。 然后更新
     * @param tenantCode 租户
     */
    void updateDoctorBusinessCardQrCode(List<Doctor> doctors,
                                        LbqWrapper<Doctor> lbqWrapper,
                                        String tenantCode);

    /**
     * 更新机构下 医助，医生，患者的机构名称
     * @param orgId
     * @param orgName
     */
    void updateUserOrgName(long orgId, String orgName);

    /**
     * 查询可以预约的医生列表
     * @param buildPage
     * @return
     */
    IPage<Doctor> getAppointDoctor(IPage<Doctor> buildPage, DoctorAppointPageDTO paramsModel);

    /**
     * 将医生转移到新医助下
     * @param doctorId
     * @param nursingId
     * @return
     */
    Boolean changeNursing(Long doctorId, Long nursingId);

    /**
     * 获取医生所在的小组。 如果医生是独立医生，则返回 {id: 1, name: "独立医生"}
     * @param doctorId
     * @return
     */
    Group getDoctorGroup(Long doctorId);


    List<Doctor> findByIdsNoTenant(List<Long> doctorIds);

    /**
     * 查询手机号相关的医生。不限租户
     * @param mobile
     * @return
     */
    List<Doctor> findByMobileNoTenant(String mobile);

    /**
     * 医生注册，并完成授权
     * @param doctorRegisterDTO
     */
    R<JSONObject> registerDoctorAndCreateToken(DoctorRegisterDTO doctorRegisterDTO);

    /**
     * 医生全局搜索 患者， 诊断类型， 自定义小组
     * @param doctorId
     * @param dimension
     * @param searchContent
     * @return
     */
    DoctorGlobalQuery doctorGlobalQuery(Long doctorId, String dimension, String searchContent);


    /**
     * 医生群发时统计诊断类型
     * @param doctorId
     * @return
     */
    List<DoctorGlobalDiseaseDTO> doctorCountDisease(Long doctorId);


    void doctorAndPatient(Long id);


    void desensitization(List<Doctor> records);


    Doctor updateDoctorLeader(Long doctorId);

    void aiSendChatToWeiXin(Chat chat, List<Doctor> doctors, String accountTypeData);
}
