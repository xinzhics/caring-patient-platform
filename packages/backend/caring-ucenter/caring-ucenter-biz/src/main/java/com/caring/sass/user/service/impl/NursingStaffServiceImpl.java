package com.caring.sass.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.api.CoreOrgApi;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.RedisMessageNursingChange;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.PasswordUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatAtRecord;
import com.caring.sass.msgs.entity.ChatGroupSend;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dao.GroupMapper;
import com.caring.sass.user.dao.NursingStaffMapper;
import com.caring.sass.user.dto.AppMyDoctorDto;
import com.caring.sass.user.dto.NursingStaffBindOrg;
import com.caring.sass.user.dto.NursingStaffFindPw;
import com.caring.sass.user.dto.NursingStaffResetPw;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.*;
import com.caring.sass.user.service.redis.PatientMsgsNoReadCenter;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.caring.sass.utils.BizAssert.isFalse;

/**
 * <p>
 * 业务实现类
 * 用户-医助
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Service
@Order(3)
public class NursingStaffServiceImpl extends SuperServiceImpl<NursingStaffMapper, NursingStaff> implements NursingStaffService {

    private final DatabaseProperties databaseProperties;

    private final ImService imService;

    private final CoreOrgApi coreOrgApi;

    private final PatientService patientService;

    private final DoctorService doctorService;

    private final DoctorGroupService doctorGroupService;

    private final GroupMapper groupMapper;

    private final OrgService orgService;

    private final WeiXinService weiXinService;

    private final RedisTemplate<String, String> redisTemplate;

    private final ConsultationGroupService consultationGroupService;

    @Autowired
    DictionaryItemService dictionaryItemService;

    @Autowired
    TenantApi tenantApi;

    public NursingStaffServiceImpl(DatabaseProperties databaseProperties,
                                   ImService imService,
                                   CoreOrgApi coreOrgApi,
                                   GroupMapper groupMapper,
                                   PatientService patientService,
                                   OrgService orgService,
                                   DoctorGroupService doctorGroupService,
                                   DoctorService doctorService,
                                   WeiXinService weiXinService,
                                   RedisTemplate<String, String> redisTemplate,
                                   ConsultationGroupService consultationGroupService) {
        this.databaseProperties = databaseProperties;
        this.imService = imService;
        this.coreOrgApi = coreOrgApi;
        this.patientService = patientService;
        this.groupMapper = groupMapper;
        this.doctorService = doctorService;
        this.doctorGroupService = doctorGroupService;
        this.orgService = orgService;
        this.weiXinService = weiXinService;
        this.redisTemplate = redisTemplate;
        this.consultationGroupService = consultationGroupService;
    }


    @Override
    public NursingStaff createNursingStaff(NursingStaff nursingStaff) {
        long id = IdUtil.getSnowflake(databaseProperties.getId().getWorkerId(), databaseProperties.getId().getDataCenterId()).nextId();
        String passwordMd5 = SecureUtil.md5(nursingStaff.getPassword());
        nursingStaff.setId(id);
        nursingStaff.setPassword(passwordMd5);
        nursingStaff.setImGroupStatus(1);
        String imAccount = imService.registerAccount(ImService.ImAccountKey.NURSING_STAFF, id);
        nursingStaff.setImAccount(imAccount);
        baseMapper.insert(nursingStaff);
        return nursingStaff;
    }

    @Override
    public boolean save(NursingStaff model) {
        model.setImGroupStatus(1);
        return super.save(model);
    }

    @Override
    public NursingStaff findByLoginName(String loginName) {
        NursingStaff nursingStaff = new NursingStaff();
        nursingStaff.setLoginName(loginName);
        return baseMapper.selectOne(Wraps.q(nursingStaff));
    }


    /**
     * @Author yangShuai
     * @Description 手机号获取医助
     * @Date 2020/10/13 17:32
     * @return com.caring.sass.user.entity.NursingStaff
     */
    @Override
    public NursingStaff getByMobile(String phone) {

        NursingStaff nursingStaff = new NursingStaff().setMobile(phone);
        QueryWrap<NursingStaff> queryWrap = Wraps.q(nursingStaff).last(" limit 1 ");
        return baseMapper.selectOne(queryWrap);


    }


    /**
     * @Author yangShuai
     * @Description 绑定机构
     * @Date 2020/10/13 17:32
     *
     * @return com.caring.sass.user.entity.NursingStaff
     */
    @Override
    public NursingStaff bindOrg(NursingStaffBindOrg nursingStaffBindOrg) {
        R<Org> orgApiByCode = coreOrgApi.getByCode(nursingStaffBindOrg.getCode());
        if (orgApiByCode.getIsSuccess()) {
            Org codeData = orgApiByCode.getData();
            if (Objects.nonNull(codeData)) {
                NursingStaff nursingStaff = baseMapper.selectById(nursingStaffBindOrg.getId());
                if (Objects.nonNull(nursingStaff)) {
                    nursingStaff.setOrganCode(nursingStaffBindOrg.getCode());
                    nursingStaff.setName(nursingStaffBindOrg.getName());
                    nursingStaff.setAvatar(nursingStaffBindOrg.getAvatar());
                    nursingStaff.setOrganId(codeData.getId());
                    nursingStaff.setOrganName(codeData.getLabel());
                    nursingStaff.setClassCode(codeData.getTreePath());
                    baseMapper.updateById(nursingStaff);
                }
            } else {
                throw new BizException("机构未找到");
            }
        }
        return null;
    }


    /**
     * @Author yangShuai
     * @Description 手机号重置密码
     * @Date 2020/10/13 17:32
     *
     * @return com.caring.sass.user.entity.NursingStaff
     */
    @Override
    public NursingStaff resetPassword(NursingStaffResetPw nursingStaffResetPw) {

        NursingStaff nursingStaff = new NursingStaff().setMobile(nursingStaffResetPw.getMobile());
        QueryWrap<NursingStaff> queryWrap = Wraps.q(nursingStaff).last(" limit 1 ");

        boolean validString = PasswordUtils.isValidString(nursingStaffResetPw.getNewPassword());
        BizAssert.isTrue(validString, "密码需要符合字母+数字长度8位以上要求");
        nursingStaff = baseMapper.selectOne(queryWrap);
        String passwordMd5 = SecureUtil.md5(nursingStaffResetPw.getNewPassword());
        nursingStaff.setPassword(passwordMd5);
        baseMapper.updateById(nursingStaff);
        return nursingStaff;
    }


    /**
     * @Author yangShuai
     * @Description 修改密码
     * @Date 2020/10/13 17:31
     *
     * @return com.caring.sass.user.entity.NursingStaff
     */
    @Override
    public NursingStaff updatePassword(NursingStaffFindPw nursingStaffFindPw) {
        boolean validString = PasswordUtils.isValidString(nursingStaffFindPw.getNewPassword());
        BizAssert.isTrue(validString, "密码需要符合字母+数字长度8位以上要求");
        NursingStaff nursingStaff = baseMapper.selectById(nursingStaffFindPw.getId());
        if (Objects.isNull(nursingStaff)) {
            throw new BizException("用户不存在");
        }
        String passwordMd5 = SecureUtil.md5(nursingStaffFindPw.getOldPassword());
        if (passwordMd5.equals(nursingStaff.getPassword())) {
            passwordMd5 = SecureUtil.md5(nursingStaffFindPw.getNewPassword());
            nursingStaff.setPassword(passwordMd5);
            baseMapper.updateById(nursingStaff);
            return nursingStaff;
        }
        throw new BizException("旧密码不正确");
    }

    /**
     * 医助群发消息
     * @param chat
     */
    @Override
    public void sendMoreChatToWeiXin(Chat chat) {
        if (StringUtils.isEmpty(chat.getSenderId())) {
            throw new BizException("发送人Id不能为空");
        }
        if (StringUtils.isEmpty(chat.getReceiverId())) {
            throw new BizException("接收人Id不能为空");
        }
        String receiverIds = chat.getReceiverId();
        String[] p = receiverIds.split(",");
        NursingStaff nursingStaff = baseMapper.selectById(chat.getSenderId());
        if (Objects.isNull(nursingStaff)) {
            throw new BizException("发送人不存在");
        }
        if (StringUtils.isEmpty(nursingStaff.getImAccount())) {
            throw new BizException("发送人Im账号不存在");
        }
        ChatGroupSend chatGroupSend = new ChatGroupSend();
        String content = chat.getContent();
        chatGroupSend.setContent(content);
        chatGroupSend.setReceiverIds(receiverIds);
        MediaType chatType = chat.getType();
        chatGroupSend.setType(chatType);
        chatGroupSend.setSenderAvatar(nursingStaff.getAvatar());
        chatGroupSend.setSenderId(nursingStaff.getId().toString());
        chatGroupSend.setSenderImAccount(nursingStaff.getImAccount());
        chatGroupSend.setSenderName(nursingStaff.getName());
        chatGroupSend.setCreateTime(LocalDateTime.now());
        imService.createGroupChat(chatGroupSend);
        String tenantCode = BaseContextHandler.getTenant();
        String userRole = dictionaryItemService.findDictionaryItemName(DictionaryItemService.ASSISTANT);
        BaseContextHandler.setTenant(tenantCode);
        TenantApi tenantApi = SpringUtils.getBean(TenantApi.class);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            log.error("sendMoreChatToWeiXin no has tenant");
            return;
        }
        TemplateMsgDto templateMsgDto = weiXinService.getCurrentSendUseTemplate(tenant.isCertificationServiceNumber(), TemplateMessageIndefiner.CONSULTATION_RESPONSE);
        Boolean noWeiXinTemplate = weiXinService.noSendWeiXinTemplate(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.ONLINE_CONSULTATION);
        String patientChat = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CHAT);
        Set<Long> patientIdsSet = new HashSet<>();
        for (String s : p) {
            patientIdsSet.add(Long.parseLong(s));
        }
        List<Long> patientAllList = ListUtil.toList(patientIdsSet);
        List<List<Long>> listList = ListUtils.subList(patientAllList, 30);
        for (List<Long> patientList : listList) {
            // 查询30个患者的相关数据
            List<Patient> patients = patientService.list(Wraps.<Patient>lbQ().in(SuperEntity::getId, patientList));
            SaasGlobalThreadPool.execute(() -> sendMoreChatToWeiXin(patients, patientChat, tenantCode, templateMsgDto, nursingStaff,
                    tenant.getWxAppId(), content, chatType, noWeiXinTemplate, tenant, userRole));
        }
    }

    /**
     * 医助给 大量患者批量发送 公众号消息。
     * @param patients
     * @param patientChat
     * @param tenantCode
     * @param templateMsgDto
     * @param nursingStaff
     * @param appId
     * @param content
     * @param chatType
     * @param noWeiXinTemplate
     */
    public void sendMoreChatToWeiXin(List<Patient> patients, String patientChat, String tenantCode,
                                     TemplateMsgDto templateMsgDto, NursingStaff nursingStaff, String appId,
                                     String content, MediaType chatType, Boolean noWeiXinTemplate, Tenant tenant, String userRole) {
        if (CollUtil.isEmpty(patients)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        Set<Long> doctorIds = patients.stream().map(Patient::getDoctorId).collect(Collectors.toSet());
        List<Doctor> doctors = doctorService.list(Wraps.<Doctor>lbQ().in(SuperEntity::getId, doctorIds));
        Map<Long, Doctor> doctorMap;
        if (CollUtil.isEmpty(doctors)) {
            doctorMap = new HashMap<>();
        } else {
            doctorMap = doctors.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o1));
        }
        String defaultLanguage = tenant.getDefaultLanguage();
        boolean personalServiceNumber = tenant.isPersonalServiceNumber();
        TenantOfficialAccountType accountType = tenant.getOfficialAccountType();
        patients.forEach(receiver -> {
            Chat chat = new Chat();
            chat.setContent(content);
            chat.setType(chatType);
            chat.setSenderId(nursingStaff.getId().toString());
            chat.setHistoryVisible(CommonStatus.YES);
            chat.setGroupMessage(CommonStatus.YES);
            List<ChatSendRead> chatSendReads = new ArrayList<>(10);
            imService.setSenderMessage(chat, nursingStaff);

            imService.setOtherMessage(chat, receiver);

            // 查询 患者的医生信息。封装医生接收的im消息 由于是医助群发消息， 所以不给医生生产未读记录。
            if (receiver.getDoctorId() != null) {
                Doctor doctor = doctorMap.get(receiver.getDoctorId());
                if (doctor != null) {
                    ChatSendRead doctorSendRead = imService.createDoctorSendRead(accountType.toString(), receiver.getImAccount(), doctor, receiver.getDoctorExitChatListJson());
                    doctorSendRead.setNoCreateReadLog(true);
                    chatSendReads.add(doctorSendRead);
                    // 这个医生不是一个独立医生。
                    if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {
                        // 查询跟患者医生 在同一个小组的医生， 而且医生还是愿意接收患者消息的医生
                        List<Doctor> doctorList = doctorGroupService.getReadMyMsgDoctor(receiver.getDoctorId());
                        List<ChatSendRead> sendRead = imService.createDoctorSendRead(accountType.toString(), receiver.getImAccount(), doctorList, receiver.getDoctorId(), receiver.getDoctorExitChatListJson());
                        if (CollUtil.isNotEmpty(sendRead)) {
                            sendRead.forEach(item -> item.setNoCreateReadLog(true));
                            chatSendReads.addAll(sendRead);
                        }
                    }
                }
            }
            ChatSendRead patientChatSendRead = imService.createPatientSendRead(receiver.getImAccount(), receiver);
            chatSendReads.add(patientChatSendRead);
            chat.setSendReads(chatSendReads);
            this.imService.sendChat(chat);
            if (!noWeiXinTemplate) {
                if (personalServiceNumber) {
                    patientMsgsNoReadCenter.savePatientMsgsNoRead(receiver.getId(), tenantCode, tenant.getDomainName(), tenant.getName(), nursingStaff.getName(), userRole);
                } else {
                    if (Objects.nonNull(templateMsgDto)) {
                        weiXinService.sendConsultationResponseMore(appId, receiver.getOpenId(), chatType, content, receiver.getName(), patientChat, receiver.getId(), templateMsgDto, defaultLanguage);
                    }
                }
            }
        });

    }

    @Autowired
    PatientMsgsNoReadCenter patientMsgsNoReadCenter;


    /**
     * @Author yangShuai
     * @Description  接收人都是给患者。 发送人各不相同
     * @Date 2020/11/12 14:20
     *
     * @return java.util.List<com.caring.sass.msgs.entity.Chat>
     */
    @Override
    public List<Chat> sendChatToWeiXin(Chat chat, int group) {

        if (StringUtils.isEmpty(chat.getSenderId())) {
            throw new BizException("发送人Id不能为空");
        }
        if (StringUtils.isEmpty(chat.getReceiverId())) {
            throw new BizException("接收人Id不能为空");
        }
        List<Chat> results = new ArrayList();
        String receiverIds = chat.getReceiverId();
        String[] p = receiverIds.split(",");
        NursingStaff nursingStaff = baseMapper.selectById(chat.getSenderId());
        if (Objects.isNull(nursingStaff)) {
            throw new BizException("发送人不存在");
        }
        if (StringUtils.isEmpty(nursingStaff.getImAccount())) {
            throw new BizException("发送人Im账号不存在");
        }

        String tenantCode = BaseContextHandler.getTenant();
        Patient receiver;
        String s = p[0];
        // 设置发送人的信息
        imService.setSenderMessage(chat, nursingStaff);
        Boolean weiXinTemplate = weiXinService.noSendWeiXinTemplate(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.ONLINE_CONSULTATION);
        List<ChatSendRead> chatSendReads;
        ChatSendRead send;
        Doctor doctor = null;
        String userRole = dictionaryItemService.findDictionaryItemName(DictionaryItemService.ASSISTANT);
        R<String> accountType = tenantApi.queryOfficialAccountType(tenantCode);
        String accountTypeData = accountType.getData();
        try {
            receiver = patientService.getById(s);
            if (receiver == null) {

            } else {
                chatSendReads = new ArrayList<>(10);
                chat.setHistoryVisible(1);
                // 设置冗余数据 和 消息所属群组
                imService.setOtherMessage(chat, receiver);
                // 查询 患者的医生信息。封装医生接收的im消息
                if (receiver.getDoctorId() != null) {
                    doctor = doctorService.getBaseDoctorAndImOpenById(receiver.getDoctorId());
                    if (doctor != null) {
                        send = imService.createDoctorSendRead(accountTypeData, receiver.getImAccount(), doctor, receiver.getDoctorExitChatListJson());
                        chatSendReads.add(send);
                        // 这个医生不是一个独立医生。
                        if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {

                            // 查询跟患者医生 在同一个小组的医生， 而且医生还是愿意接收患者消息的医生
                            List<Doctor> doctorList = doctorGroupService.getReadMyMsgDoctor(receiver.getDoctorId());
                            List<ChatSendRead> sendRead = imService.createDoctorSendRead(accountTypeData, receiver.getImAccount(), doctorList, receiver.getDoctorId(), receiver.getDoctorExitChatListJson());
                            chatSendReads.addAll(sendRead);
                        }
                    }
                }

                send = imService.createPatientSendRead(receiver.getImAccount(), receiver);
                chatSendReads.add(send);

                chat.setSendReads(chatSendReads);
                List<ChatAtRecord> chatAtRecords = chat.getChatAtRecords();
                chat = this.imService.sendChat(chat);
                chat.setChatAtRecords(null);
                results.add(chat);

                // 开启聊天和退出聊天的通知不需要模板消息
                if (chat.getType().equals(MediaType.exitChat) || chat.getType().equals(MediaType.openChat)) {
                    return results;
                }
                // 检查是否 关闭 在线咨询的模板推送功能
                if (weiXinTemplate) {
                    return results;
                }
                boolean cancelPatient = false;
                if (CollUtil.isNotEmpty(chatAtRecords)) {
                    for (ChatAtRecord atRecord : chatAtRecords) {
                        if (UserType.UCENTER_DOCTOR.equals(atRecord.getUserType())) {
                            if (StrUtil.isEmpty(receiver.getDoctorExitChatListJson()) || !receiver.getDoctorExitChatListJson().contains(atRecord.getAtUserId().toString())) {
                                doctorService.sendAtImWeiXinTemplateMessage(tenantCode, atRecord.getAtUserId(), nursingStaff.getName(), receiver.getImAccount(), receiver.getId(), receiver.getName());
                            }
                        }
                        if (atRecord.getAtUserId().equals(receiver.getId())) {
                            atRecord.setUserType(UserType.UCENTER_PATIENT);
                            patientService.sendAtImWeiXinTemplateMessage(tenantCode, atRecord.getAtUserId(), nursingStaff.getName(), UserType.UCENTER_NURSING_STAFF);
                            cancelPatient = true;
                        }
                    }
                }

                sendImWeiXinTemplateMessage(tenantCode, doctor, receiver.getStatus(),
                        receiver.getImAccount(), receiver.getOpenId(), receiver.getName(),
                        chat.getType(), chat.getContent(), true, cancelPatient, receiver.getId(), nursingStaff.getName(), userRole, receiver.getDoctorExitChatListJson());
            }
        } catch (Exception var18) {
            log.error("SendChatMessage Error", var18);
        }
        return results;
    }

    /**
     * 异步发送微信消息
     * @param tenantCode
     * @param patientStatus 患者的状态
     * @param imAccount 患者得im
     * @param patientOpenId 患者的openId
     * @param type 消息的类型
     * @param content 消息的内容
     */
    @Async
    public void sendImWeiXinTemplateMessage(String tenantCode, Doctor doctor,
                                            Integer patientStatus, String imAccount, String patientOpenId, String patientName,
                                            MediaType type, String content, boolean cancelDoctor, boolean cancelPatient, Long patientId,
                                            String sendUserName, String userRole, String patientExitChatListJson) {
        if (!cancelDoctor) {
            if (StrUtil.isEmpty(patientExitChatListJson) || !patientExitChatListJson.contains(doctor.getId().toString())) {
                doctorService.sendImWeiXinTemplateMessage(tenantCode, doctor, type, content, patientName,
                        imAccount, patientId, TemplateMessageIndefiner.CONSULTATION_RESPONSE_NURSING);
            }
        }
        if (!cancelPatient) {
            patientService.sendWeiXinTemplateMessage(tenantCode, patientStatus, imAccount, patientOpenId, type, content, patientId, patientName, sendUserName, userRole);
        }
    }


    /**
     * @Author yangShuai
     * @Description 返回一个医助。只有医助的基本信息
     * @Date 2020/11/12 11:15
     *
     * @return com.caring.sass.user.entity.NursingStaff
     */
    @Override
    public NursingStaff getBaseNursingStaffById(Long id) {
        return baseMapper.getBaseNursingStaffById(id);
    }

    @Override
    public NursingStaff getById(Serializable id) {
        NursingStaff nursingStaff = baseMapper.selectById(id);
        if (Objects.nonNull(nursingStaff)) {
            nursingStaff.setPatientCount(patientService.countPatientNoStatus((Long) id, UserType.UCENTER_NURSING_STAFF ));
            nursingStaff.setFansCount(patientService.countPatientByStatus(Patient.PATIENT_SUBSCRIBE, (Long) id, UserType.UCENTER_NURSING_STAFF ));
            nursingStaff.setDoctorCount(doctorService.countDoctorByNursing((Long) id));
        }
        String userType = BaseContextHandler.getUserType();
        if (UserType.ORGAN_ADMIN.equals(userType) || UserType.ADMIN_OPERATION.equals(userType)) {
            List<NursingStaff> nursingStaffs = new ArrayList<NursingStaff>();
            nursingStaffs.add(nursingStaff);
            desensitization(nursingStaffs);
        }
        return nursingStaff;
    }

    @Override
    public IPage<NursingStaff> findPage(IPage<NursingStaff> page, LbqWrapper<NursingStaff> wrapper) {
        return baseMapper.findPage(page, wrapper, new DataScope());
    }


    /**
     * 将医助 和 医助下的所有数据 更换机构
     * @param nursingId
     * @param organId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean changeNursingOrganId(Long nursingId, Long organId) {
        NursingStaff nursingStaff = baseMapper.selectById(nursingId);
        Org org = orgService.getById(organId);
        if (Objects.isNull(nursingStaff)) {
            return false;
        }
        if (Objects.isNull(org)) {
            return false;
        }

        nursingStaff.setOrganId(org.getId());
        nursingStaff.setOrganName(org.getLabel());
        nursingStaff.setOrganCode(org.getCode());
        nursingStaff.setClassCode(org.getTreePath());

        baseMapper.updateById(nursingStaff);

        UpdateWrapper<Patient> wrapper = new UpdateWrapper<>();
        wrapper.set("service_advisor_name", nursingStaff.getName());
        wrapper.set("org_id", nursingStaff.getOrganId());
        wrapper.set("organ_code", nursingStaff.getOrganCode());
        wrapper.set("organ_name", nursingStaff.getOrganName());
        wrapper.set("class_code", nursingStaff.getClassCode());
        wrapper.eq("service_advisor_id", nursingId);
        patientService.update(new Patient(), wrapper);

        List<Group> groupList = groupMapper.selectList(Wraps.<Group>lbQ().eq(Group::getNurseId, nursingId));
        for (Group group : groupList) {
            group.setNurseName(nursingStaff.getName());
            group.setClassCode(nursingStaff.getClassCode());
            group.setOrganId(nursingStaff.getOrganId());
            group.setOrganName(nursingStaff.getOrganName());
            groupMapper.updateById(group);
        }

        UpdateWrapper<Doctor> doctorUpdateWrapper = new UpdateWrapper<>();
        doctorUpdateWrapper.set("nursing_name", nursingStaff.getName());
        doctorUpdateWrapper.set("org_id", nursingStaff.getOrganId());
        doctorUpdateWrapper.set("organ_code", nursingStaff.getOrganCode());
        doctorUpdateWrapper.set("organ_name", nursingStaff.getOrganName());
        doctorUpdateWrapper.set("class_code", nursingStaff.getClassCode());
        doctorUpdateWrapper.eq("nursing_id", nursingId);
        doctorService.update(new Doctor(), doctorUpdateWrapper);

        RedisMessageNursingChange redisMessageNursingChange = new RedisMessageNursingChange();
        redisMessageNursingChange.setNursingId(nursingId);
        redisMessageNursingChange.setTenantCode(BaseContextHandler.getTenant());
        redisMessageNursingChange.setClassCode(nursingStaff.getClassCode());
        redisMessageNursingChange.setOrgCode(nursingStaff.getOrganCode());
        redisMessageNursingChange.setOrgId(nursingStaff.getOrganId());
        redisMessageNursingChange.setOrgName(nursingStaff.getOrganName());
        redisMessageNursingChange.setTargetNursingId(nursingStaff.getId());
        redisMessageNursingChange.setTargetNursingName(nursingStaff.getName());
        redisTemplate.convertAndSend(SaasRedisBusinessKey.NURSING_CHANGE, JSON.toJSONString(redisMessageNursingChange));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeBelongingToNursingId(Long formNursingId, Long toNursingId) {
        NursingStaff nursingStaff = baseMapper.selectById(toNursingId);
        // 变更医生到 新的医助下
        if (Objects.isNull(nursingStaff)) {
            return false;
        }
        UpdateWrapper<Patient> wrapper = new UpdateWrapper<>();
        wrapper.set("service_advisor_id", nursingStaff.getId());
        wrapper.set("service_advisor_name", nursingStaff.getName());
        wrapper.set("org_id", nursingStaff.getOrganId());
        wrapper.set("organ_code", nursingStaff.getOrganCode());
        wrapper.set("organ_name", nursingStaff.getOrganName());
        wrapper.set("class_code", nursingStaff.getClassCode());
        wrapper.eq("service_advisor_id", formNursingId);
        patientService.update(new Patient(), wrapper);


        R<String> accountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(accountType.getData())) {
            // 如果个人服务号，将医助的医生小组 下的医生，转到新的医助的医生小组下
            Group group = groupMapper.selectOne(Wraps.<Group>lbQ().eq(Group::getNurseId, formNursingId).last(" limit 0, 1"));
            Group toGroup = groupMapper.selectOne(Wraps.<Group>lbQ().eq(Group::getNurseId, toNursingId).last(" limit 0, 1"));
            if (toGroup == null) {
                toGroup = new Group();
                toGroup.setNurseName(nursingStaff.getName());
                toGroup.setClassCode(nursingStaff.getClassCode());
                toGroup.setOrganId(nursingStaff.getOrganId());
                toGroup.setOrganName(nursingStaff.getOrganName());
                toGroup.setNurseId(toNursingId);
                toGroup.setName(nursingStaff.getName()+ "医生小组");
                groupMapper.insert(toGroup);
            }
            if (group != null) {
                UpdateWrapper<DoctorGroup> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("group_id", toGroup.getId());
                updateWrapper.set("join_group_time", LocalDateTime.now());
                updateWrapper.eq("group_id", group.getId());
                doctorGroupService.update(new DoctorGroup(), updateWrapper);
            }

        } else {
            List<Group> groupList = groupMapper.selectList(Wraps.<Group>lbQ().eq(Group::getNurseId, formNursingId));
            for (Group group : groupList) {
                group.setNurseId(nursingStaff.getId());
                group.setNurseName(nursingStaff.getName());
                group.setClassCode(nursingStaff.getClassCode());
                group.setOrganId(nursingStaff.getOrganId());
                group.setOrganName(nursingStaff.getOrganName());
                group.setNurseName(nursingStaff.getName());
                group.setName(group.getName() + "(迁移)");
                groupMapper.updateById(group);
            }
        }
        UpdateWrapper<Doctor> doctorUpdateWrapper = new UpdateWrapper<>();
        doctorUpdateWrapper.set("nursing_id", nursingStaff.getId());
        doctorUpdateWrapper.set("nursing_name", nursingStaff.getName());
        doctorUpdateWrapper.set("org_id", nursingStaff.getOrganId());
        doctorUpdateWrapper.set("organ_code", nursingStaff.getOrganCode());
        doctorUpdateWrapper.set("organ_name", nursingStaff.getOrganName());
        doctorUpdateWrapper.set("class_code", nursingStaff.getClassCode());
        doctorUpdateWrapper.eq("nursing_id", formNursingId);
        doctorService.update(new Doctor(), doctorUpdateWrapper);

        RedisMessageNursingChange redisMessageNursingChange = new RedisMessageNursingChange();
        redisMessageNursingChange.setNursingId(formNursingId);
        redisMessageNursingChange.setTenantCode(BaseContextHandler.getTenant());
        redisMessageNursingChange.setClassCode(nursingStaff.getClassCode());
        redisMessageNursingChange.setOrgCode(nursingStaff.getOrganCode());
        redisMessageNursingChange.setOrgId(nursingStaff.getOrganId());
        redisMessageNursingChange.setOrgName(nursingStaff.getOrganName());
        redisMessageNursingChange.setTargetNursingId(nursingStaff.getId());
        redisMessageNursingChange.setTargetNursingName(nursingStaff.getName());
        redisTemplate.convertAndSend(SaasRedisBusinessKey.NURSING_CHANGE, JSON.toJSONString(redisMessageNursingChange));

        consultationGroupService.changeNursing(formNursingId, nursingStaff);

        return true;
    }

    /**
     * 查询app的小组和医生。并统计人员
     * 由于一个 app 创建的小组和医生实际情况下 不会超过50个
     * 故这里直接一次查询返回
     * @param nursingId
     * @return
     */
    @Override
    public AppMyDoctorDto getAppDoctor(Long nursingId) {

        // 个人服务号。直接查询医助所有的医生, 并返回
        R<String> accountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(accountType.getData())) {
            LbqWrapper<Doctor> doctorLbqWrapper = Wraps.<Doctor>lbQ()
                    .eq(Doctor::getNursingId, nursingId)
                    .select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar, Doctor::getWxStatus, Doctor::getDoctorLeader);

            List<Doctor> doctorList = doctorService.list(doctorLbqWrapper);
            // 统计医生下有多少 患者
            patientService.countPatientByDoctor(doctorList);
            return AppMyDoctorDto.builder().doctorList(doctorList).groupList(new ArrayList<>()).build();
        }

        LbqWrapper<Group> wrapper = Wraps.<Group>lbQ().eq(Group::getNurseId, nursingId)
                .select(SuperEntity::getId, Group::getName, Group::getIcon);
        List<Group> groups = groupMapper.selectList(wrapper);
        // 统计小组下 有多少医生 患者
        patientService.countPatientByGroupId(groups);
        doctorService.countDoctorByGroupId(groups);

        LbqWrapper<Doctor> doctorLbqWrapper = Wraps.<Doctor>lbQ()
                .eq(Doctor::getNursingId, nursingId)
                .eq(Doctor::getIndependence, 1)
                .select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar, Doctor::getWxStatus);

        List<Doctor> doctorList = doctorService.list(doctorLbqWrapper);
        // 统计医生下有多少 患者
        patientService.countPatientByDoctor(doctorList);

        return AppMyDoctorDto.builder().doctorList(doctorList)
                .groupList(groups).build();
    }

    @Override
    public void updateUserOrgName(long orgId, String orgName) {

        UpdateWrapper<NursingStaff> wrapper = new UpdateWrapper<>();
        wrapper.set("organ_name", orgName).eq("org_id", orgId);
        baseMapper.update(new NursingStaff(), wrapper);

        doctorService.updateUserOrgName(orgId, orgName);
        patientService.updateUserOrgName(orgId, orgName);

    }

    @Transactional
    @Override
    public boolean updateById(NursingStaff model) {
        NursingStaff nursingStaff = getBaseNursingStaffById(model.getId());
        super.updateById(model);
        if (!nursingStaff.getName().equals(model.getName())) {
            changeName(model.getId(), model.getName());
        }
        return true;
    }

    /**
     * 医助更新自己名字之后。 同步 患者，医生，小组中的冗余
     * @param nursingId
     * @param userName
     */
    @Override
    public void changeName(Long nursingId, String userName) {
        if (StrUtil.isEmpty(userName)) {
            return;
        }
        patientService.update(Wraps.<Patient>lbU()
                .set(Patient::getServiceAdvisorName, userName)
                .eq(Patient::getServiceAdvisorId, nursingId));

        doctorService.update(Wraps.<Doctor>lbU()
                .set(Doctor::getNursingName, userName)
                .eq(Doctor::getNursingId, nursingId));

        UpdateWrapper<Group> updateWrapper = new UpdateWrapper<Group>();
        updateWrapper.set("nurse_name", userName);
        updateWrapper.eq("nurse_id", nursingId);
        groupMapper.update(new Group(), updateWrapper);

    }


    @Override
    public List<Long> getNursingOrgIds(Long nursingId) {

        NursingStaff nursingStaff = baseMapper.selectById(nursingId);
        Long organId = nursingStaff.getOrganId();
        List<Long> ids = new ArrayList<>();
        ids.add(organId);
        List<Long> orgIds = new ArrayList<>();
        List<Org> orgList = orgService.findChildren(ids);
        if (CollUtil.isNotEmpty(orgList)) {
            orgIds = orgList.stream().mapToLong(Org::getId).boxed().collect(Collectors.toList());
        }
        orgIds.add(organId);
        return orgIds;
    }


    /**
     * 发送患者im提示
     * @param receiverImAccount
     */
    @Override
    public void sendPatientImRemind(String receiverImAccount) {
        Patient patient = patientService.getOne(Wraps.<Patient>lbQ()
                .select(SuperEntity::getId, Patient::getName, Patient::getServiceAdvisorId)
                .eq(Patient::getImAccount, receiverImAccount));
        if (Objects.isNull(patient)) {
            return;
        }
        Chat chat = new Chat();
        chat.setContent("您好，我是医生助理，为了让医生了解到您具体的需求，给到您对应的健康指导，请将您的症状或者需要咨询的内容直接发送过来！");
        chat.setType(MediaType.text);
        chat.setSenderId(patient.getServiceAdvisorId().toString());
        chat.setReceiverId(patient.getId().toString());
        chat.setReceiverImAccount(receiverImAccount);
        sendChatToWeiXin(chat, 0);

    }


    @Override
    public void desensitization(List<NursingStaff> records) {
        if (records.isEmpty()) {
            return;
        }

        String userType = BaseContextHandler.getUserType();
        if (UserType.ADMIN.equals(userType)) {
            return;
        }

        String tenant = BaseContextHandler.getTenant();
        R<Boolean> securitySettings = tenantApi.queryDataSecuritySettings(tenant);
        if (securitySettings.getIsSuccess()) {
            Boolean data = securitySettings.getData();
            if (data == null || !data) {
                return;
            }
        } else {
            return;
        }

        for (NursingStaff record : records) {
            // 对 换着的 姓名。 联系方式 脱敏
            record.setName(SensitiveInfoUtils.desensitizeName(record.getName()));
            record.setMobile(SensitiveInfoUtils.desensitizePhone(record.getMobile()));
        }
    }


    /**
     * 发送@医助的模板消息
     * @param tenantCode
     * @param nursingId
     * @param senderName
     * @param imAccount
     * @param patientId
     * @param patientName
     */
    @Async
    @Override
    public void sendAtImWeiXinTemplateMessage(String tenantCode, Long nursingId, String senderName, String imAccount, Long patientId, String patientName) {

        BaseContextHandler.setTenant(tenantCode);
        NursingStaff nursingStaff = getBaseNursingStaffById(nursingId);
        if (Objects.isNull(nursingStaff)) {
            return;
        }
        // 医助没有openId 不推送
        String nursingStaffOpenId = nursingStaff.getOpenId();
        if (StringUtils.isEmpty(nursingStaffOpenId)) {
            return;
        }
        // 医助 取关了微信，不推送
        if (nursingStaff.getWxStatus() != null && nursingStaff.getWxStatus() == 0) {
            return;
        }
        if (nursingStaff.getImWxTemplateStatus() != null && nursingStaff.getImWxTemplateStatus() == 0) {
            return;
        }
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            return;
        }
        String baseDomain = ApplicationDomainUtil.wxAssistantBaseDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()));
        String nursingChat = baseDomain + String.format(H5Router.NURSING_PATIENT_CHAT, patientId);
        weiXinService.sendConsultationResponse(tenant.getWxAppId(), nursingStaffOpenId, senderName, nursingChat, patientName, tenant.getDefaultLanguage());

    }


    /**
     * 发送模版消息 给医助
     * @param tenantCode
     * @param nursingId
     * @param type
     * @param content
     * @param patientId
     * @param patientName
     * @param senderName
     */
    @Async
    @Override
    public void sendWeixinTemplateMessage(String tenantCode, Long nursingId, MediaType type, String content, Long patientId, String patientName, String senderName) {

        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        // 个人服务号不需要给医助推送模版
        if (tenant.isPersonalServiceNumber()) {
            return;
        }
        NursingStaff nursingStaff = getBaseNursingStaffById(nursingId);
        if (Objects.isNull(nursingStaff)) {
            return;
        }
        // 医助没有openId 不推送
        String nursingStaffOpenId = nursingStaff.getOpenId();
        if (StringUtils.isEmpty(nursingStaffOpenId)) {
            return;
        }
        // 医助 取关了微信，不推送
        if (nursingStaff.getWxStatus() != null && nursingStaff.getWxStatus() == 0) {
            return;
        }
        if (nursingStaff.getImWxTemplateStatus() != null && nursingStaff.getImWxTemplateStatus() == 0) {
            return;
        }

        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            return;
        }
        String baseDomain = ApplicationDomainUtil.wxAssistantBaseDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()));
        String nursingChat = baseDomain + String.format(H5Router.NURSING_PATIENT_CHAT, patientId);
        Boolean online = this.imService.online(nursingStaff.getImAccount());
        if (online == null || online.equals(false)) {
            // 推送模板消息
            weiXinService.sendConsultationResponse(tenant.getWxAppId(), nursingStaffOpenId, type, content,
                    patientName, nursingChat, patientId, null, tenant.getDefaultLanguage());
        }
    }
}
