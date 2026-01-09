package com.caring.sass.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.difyclient.DifyChatClient;
import com.caring.sass.common.difyclient.DifyClientFactory;
import com.caring.sass.common.difyclient.enums.ResponseMode;
import com.caring.sass.common.difyclient.model.chat.ChatMessage;
import com.caring.sass.common.difyclient.model.chat.ChatMessageResponse;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.redis.RedisMessageDto;
import com.caring.sass.common.redis.RedisMessagePatientChangeDoctor;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.*;
import com.caring.sass.common.utils.key.Key;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.update.LbuWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatAtRecord;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.api.InformationIntegrityMonitoringApi;
import com.caring.sass.nursing.api.PatientNursingPlanApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.config.DifyConfig;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.dao.PatientUnRegisteredReminderMapper;
import com.caring.sass.user.dao.ReferralMapper;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.*;
import com.caring.sass.user.service.redis.PatientMsgsNoReadCenter;
import com.caring.sass.user.util.I18nUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.BindUserTagsForm;
import com.caring.sass.wx.dto.config.GetFollowerInfoForm;
import com.caring.sass.wx.dto.config.SendKefuMsgForm;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import com.caring.sass.wx.entity.guide.RegGuide;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Service
@Order(0)
public class PatientServiceImpl extends SuperServiceImpl<PatientMapper, Patient> implements PatientService {

    private final WeiXinApi weiXinApi;

    private final WeiXinService weiXinService;

    private final TenantApi tenantApi;

    private DoctorService doctorService;

    private final ImService imService;

    private final DatabaseProperties databaseProperties;

    private final SystemMsgService systemMsgService;

    private NursingStaffService nursingStaffService;

    private final FormApi formApi;

    private final DoctorGroupService doctorGroupService;

    private final CustomGroupingService customGroupingService;

    private final DoctorCustomGroupService doctorCustomGroupService;

    private final GuideApi guideApi;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    InformationIntegrityMonitoringApi informationIntegrityMonitoringApi;

    @Autowired
    PatientRecommendRelationshipService patientRecommendRelationshipService;

    @Autowired
    DictionaryItemService dictionaryItemService;

    @Autowired
    BusinessReminderLogControllerApi reminderLogControllerApi;

    @Autowired
    PatientMsgsNoReadCenter patientMsgsNoReadCenter;

    private static final NamedThreadFactory PATIENT_FACTORY = new NamedThreadFactory("patient-reminder-", false);

    private static final ExecutorService PATIENT_EXECUTOR = new ThreadPoolExecutor(2, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000), PATIENT_FACTORY);

    private static final String PATIENT_REMINDER_REDIS_KEY = "PATIENT_REMINDER_REDIS_KEY";

    public PatientServiceImpl(WeiXinApi weiXinApi,
                              WeiXinService weiXinService,
                              TenantApi tenantApi,
                              ImService imService,
                              DatabaseProperties databaseProperties,
                              SystemMsgService systemMsgService,
                              DoctorGroupService doctorGroupService,
                              CustomGroupingService customGroupingService,
                              RedisTemplate<String, String> redisTemplate,
                              DoctorCustomGroupService doctorCustomGroupService,
                              FormApi formApi, GuideApi guideApi) {
        this.weiXinApi = weiXinApi;
        this.weiXinService = weiXinService;
        this.tenantApi = tenantApi;
        this.imService = imService;
        this.databaseProperties = databaseProperties;
        this.systemMsgService = systemMsgService;
        this.formApi = formApi;
        this.doctorGroupService = doctorGroupService;
        this.customGroupingService = customGroupingService;
        this.redisTemplate = redisTemplate;
        this.doctorCustomGroupService = doctorCustomGroupService;
        this.guideApi = guideApi;
    }

    public NursingStaffService getNursingStaffService() {
        if (nursingStaffService == null) {
            nursingStaffService = SpringUtils.getBean(NursingStaffService.class);
        }
        return nursingStaffService;
    }

    public DoctorService getDoctorService() {
        if (doctorService == null) {
            doctorService = SpringUtils.getBean(DoctorService.class);
        }
        return doctorService;
    }

    @Override
    public Patient getById(Serializable id) {
        Patient patient = super.getById(id);
        String userType = BaseContextHandler.getUserType();
        if (UserType.ORGAN_ADMIN.equals(userType) || UserType.ADMIN_OPERATION.equals(userType)) {
            List<Patient> patients = new ArrayList<Patient>();
            patients.add(patient);
            desensitization(patients);
        }
        return patient;
    }

    @Override
    public boolean updateById(Patient model) {
        if (!StringUtils.isEmpty(model.getName())) {
            String letter = ChineseToEnglishUtil.getPinYinFirstLetter(model.getName());
            if (StringUtils.isEmpty(letter)) {
                letter = "Z#";
            }
            model.setNameFirstLetter(letter);
        }
        Patient oldPatient = getBasePatientById(model.getId());
        super.updateById(model);
        if (Objects.isNull(oldPatient)) {
            return true;
        }
        // 检查患者名字是否更换、
        boolean changeName = false;
        boolean changeRemark = false;
        boolean changeDoctorRemark = false;
        boolean changeBirthday = false;
        boolean changeSex = false;
        boolean changeAvatar = false;
        if (!StringUtils.isEmpty(model.getName()) && !Objects.equals(model.getName(), oldPatient.getName())) {
            changeName = true;
        }
        // 检查 app 对患者的备注是否更换
        if (!StringUtils.isEmpty(model.getRemark()) && !Objects.equals(model.getRemark(), oldPatient.getRemark())) {
            changeRemark = true;
        }
        // 检查 医生的 患者的备注是否更换
        if (!StringUtils.isEmpty(model.getDoctorRemark()) && !Objects.equals(model.getDoctorRemark(), oldPatient.getRemark())) {
            changeDoctorRemark = true;
        }

        if (!StringUtils.isEmpty(model.getBirthday()) && !Objects.equals(model.getBirthday(), oldPatient.getBirthday())) {
            changeBirthday = true;
        }
        if (!StringUtils.isEmpty(model.getAvatar()) && !Objects.equals(model.getAvatar(), oldPatient.getAvatar())) {
            changeAvatar = true;
        }
        if (Objects.nonNull(model.getSex()) && !Objects.equals(model.getSex(), oldPatient.getSex())) {
            changeSex = true;
        }

        String tenant = BaseContextHandler.getTenant();
        if (changeName || changeRemark || changeDoctorRemark) {
            // 异步处理 im 记录中的患者的信息
            asyncUpdatePatientOtherMessage(tenant, model);
        }
        if (changeBirthday || changeSex || changeName || changeAvatar) {
            // 异步处理 更新 转诊中患者的年龄和性别
            asyncUpdatePatientReferal(tenant, model);
        }
        return true;
    }


    /**
     * 异步处理 患者相关的冗余信息
     * @param tenantCode
     * @param patient
     */
    @Async
    public void asyncUpdatePatientOtherMessage(String tenantCode, Patient patient) {
        imService.batchUpdatePatientForAllTable(tenantCode, patient);
    }

    @Autowired
    ReferralMapper referralMapper;

    /**
     * 异步处理患者的转诊信息
     * @param tenantCode
     * @param patient
     */
    @Async
    public void asyncUpdatePatientReferal(String tenantCode, Patient patient) {
        BaseContextHandler.setTenant(tenantCode);
        UpdateWrapper<Referral> updateWrapper = new UpdateWrapper<>();
        if (!StringUtils.isEmpty(patient.getAvatar())) {
            updateWrapper.set("patient_avatar", patient.getAvatar());
        }
        if (!StringUtils.isEmpty(patient.getBirthday())) {
            updateWrapper.set("patient_age", SassDateUtis.getAge(patient.getBirthday()));
        }
        if (!StringUtils.isEmpty(patient.getName())) {
            updateWrapper.set("patient_name", patient.getName());
        }
        if (Objects.nonNull(patient.getSex())) {
            updateWrapper.set("patient_sex", patient.getSex());
        }
        updateWrapper.eq("patient_id", patient.getId());
        referralMapper.update(new Referral(), updateWrapper);
    }


    /**
     * 患者关注进来。通过openId 或 unionId 生成患者。
     * @param tenant
     * @param wxAppId
     * @param doctorId
     * @param openId
     * @param unionId
     * @return
     */
    public Patient patientSubscribeCreatePatient(Tenant tenant, String wxAppId, Long doctorId, String openId, String unionId) {
        @Length(max = 20, message = "项目编码长度不能超过20") String tenantCode = tenant.getCode();
        BaseContextHandler.setTenant(tenantCode);
        Patient patient = null;
        if (StrUtil.isNotEmpty(unionId)) {
            // 由于openId 没有查询到用户。 unionId 查询到了。所以用户可能是导入进来的。 需要给用户发送正常关注用户的消息。
            patient = baseMapper.selectOne(Wraps.<Patient>lbQ().eq(Patient::getUnionId, unionId));
            // 对应这种的患者。需要将openId给他绑定上。并设置他的状态为关注，以入组。
            if (Objects.nonNull(patient)) {
                patient.setOpenId(openId);
                patient.setNursingTime(LocalDateTime.now());
                patient.setCompleteEnterGroupTime(LocalDateTime.now());
                patient.setAgreeAgreement(1);
                patient.setStatus(Patient.PATIENT_SUBSCRIBE_NORMAL);
                patient.setIsCompleteEnterGroup(1);
                baseMapper.updateById(patient);
                // 给这个患者订阅所有的护理计划
                patientNursingPlanApi.patientFirstSubscribePlanAll(patient.getId());
            }
        }
        if (Objects.isNull(patient)) {
            patient = createNewPatient(tenant, wxAppId, doctorId, openId, unionId);
        }
        if (Objects.isNull(patient)) {
            return null;
        }
        weiXinService.sendGuideMessage(patient, tenant, openId, true);

        StringBuilder str = new StringBuilder();
        str.append("【系统消息】有新会员加入，您可以为TA服务了。");

        String sysMessageContent = patient.getNickName() + "刚刚关注了公众号";
        sendSysMessage(patient, SystemMsgType.subscribe, sysMessageContent);
        patientSubToDoctorMessage(patient, SystemMsgType.subscribe, sysMessageContent);
        if (null != patient.getServiceAdvisorId()) {
            NursingStaff nursingStaff = getNursingStaffService().getById(patient.getServiceAdvisorId());
            patientSubscribePushServiceAdvisorMessage(patient, nursingStaff, str.toString());
        }
        Long patientId = patient.getId();
        try {
            informationIntegrityMonitoringApi.singleSynInformationIntegrityMonitoringTask(tenant.getCode(), patientId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【患者关注业务】执行同步任务信息完整度的逻辑(指定患者触发)失败！",e);
        }
        return patient;
    }

    /**
     * @return com.caring.sass.user.entity.Patient
     * @Author yangShuai
     * @Description 患者关注业务
     * @Date 2020/9/27 14:46
     */
    @Override
    public Patient subscribe(WxSubscribeDto wxSubscribeDto) {
        String wxAppId = wxSubscribeDto.getWxAppId();
        String openId = wxSubscribeDto.getOpenId();
        Long doctorId = wxSubscribeDto.getDoctorId();
        String unionId = null;
        Patient patient = findByOpenId(openId);
        Tenant tenant = tenantApi.getTenantByWxAppId(wxAppId).getData();
        // 患者第一次关注
        if (patient == null) {
            // 查询微信openId对应的unionId
            R<WeiXinUserInfo> userInfo = weiXinApi.getWeiXinUserInfo(openId);
            if (userInfo.getIsSuccess()) {
                WeiXinUserInfo data = userInfo.getData();
                if (Objects.nonNull(data)) {
                    unionId = data.getUnionId();
                }
            }
            return patientSubscribeCreatePatient(tenant, wxAppId, doctorId, openId, unionId);
        }

        Doctor doctor = doctorId == null ? null : getDoctorService().getById(doctorId);
        Long patientDoctorId = patient.getDoctorId();
        boolean sameDoctor = Objects.equals(doctorId, patientDoctorId);
        Doctor lastDoctor = getDoctorService().getById(patientDoctorId);
        boolean buildIn = lastDoctor != null
                && lastDoctor.getBuildIn() != null
                && Objects.equals(lastDoctor.getBuildIn(), 1);

        // 患者已经关注，扫码另一个医生的二维码
        // 如果患者之前绑定的系统医生。那么当扫描正常医生时，给他切换到正常医生关注
        if (patient.getStatus() == Patient.PATIENT_SUBSCRIBE_NORMAL) {
            if (doctor == null) {
                return patient;
            }

            // 同一个医生不处理，直接返回
            if (sameDoctor) {
                return patient;
            }

            // 之前绑定的非系统医生不处理，直接返回
            if (!buildIn) {
                return patient;
            }

            // 仅系统医生可以更换医生
            weiXinService.sendTextKefuMessage(wxAppId, patient.getOpenId(), I18nUtils.getMessage("DOCTOR_CHANGE", wxSubscribeDto.getLocale(), doctor.getName()));

            // TODO: 患者更换了医生。 之前的历史数据需要处理。
            changeDoctor(patient.getId(), doctorId);
            return patient;
        }

        // 患者 是取关后 关注 的流程
        if (patient.getStatus() == Patient.PATIENT_NO_SUBSCRIBE) {
            // 判断患者是否已经完成 入组
            String sysMessageContent;
            String commsg;
            // 内置医生，需要修改为新的医生
            String kfMsg = "";
            if (buildIn && doctor != null) {
                patient.setDoctorId(doctorId);
                patient.setDoctorName(doctor.getName());
                patient.setServiceAdvisorId(doctor.getNursingId());
                patient.setServiceAdvisorName(doctor.getNursingName());
                commsg = "成为了您的会员，您可以为TA服务了。";
                kfMsg = I18nUtils.getMessage("DOCTOR_CHANGE", wxSubscribeDto.getLocale(), doctor.getName());
            } else {
                commsg = "重新关注了您，您可以继续为TA服务了。";
                kfMsg = I18nUtils.getMessage("WELCOME_BACK", wxSubscribeDto.getLocale());
            }
            sysMessageContent = patient.getName() + "刚刚关注了公众号";
            StringBuilder str = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String dateStr = sdf.format(date);
            sendSysMessage(patient, SystemMsgType.subscribe, sysMessageContent);
            patientSubToDoctorMessage(patient, SystemMsgType.subscribe, sysMessageContent);
            if (patient.getIsCompleteEnterGroup() == 1) {
                // TODO 发送系统消息
                patient.setStatus(Patient.PATIENT_SUBSCRIBE_NORMAL);
                str.append("【").append(patient.getName()).append("】于").append(dateStr);
                str.append(commsg);
                weiXinService.sendTextKefuMessage(wxAppId, patient.getOpenId(), kfMsg);
                baseMapper.updateById(patient);
                if (!StringUtils.isEmpty(patient.getName())) {
                    patientSubscribePushServiceAdvisorMessage(patient, null, str.toString());
                }
                return patient;
            } else {
                // 患者没有完成入组
                patient.setStatus(Patient.PATIENT_SUBSCRIBE);

                // 发送入组引导 图片消息
                weiXinService.sendGuideMessage(patient, tenant, patient.getOpenId(), false);
                baseMapper.updateById(patient);
                str.append("【").append(patient.getName()).append("】于").append(dateStr);
                str.append(commsg);
                // 患者关注，为医助推送环信消息
                if (!StringUtils.isEmpty(patient.getName())) {
                    patientSubscribePushServiceAdvisorMessage(patient, null, str.toString());
                }
                return patient;
            }
        } else {
            return patient;
        }
    }


    /**
     * 个人服务号的患者注册
     * @param patientRegister
     * @return
     */
    @Override
    public Patient registerPatient(PatientRegister patientRegister) {

        Patient patient = new Patient();
        // 约定， 个人服务号只能有一个医生。
        Doctor doctor = getDoctorService().getOne(Wraps.<Doctor>lbQ()
                .eq(Doctor::getDoctorLeader, 1)
                .last(" limit 0,1 "));
        if (doctor == null) {
            // 如果没有医生， 则使用默认医生。
            doctor = getDoctorService().getOne(Wraps.<Doctor>lbQ()
                    .eq(Doctor::getBuildIn, 1).last(" limit 0,1 "));
        }
        if (doctor == null) {
            throw new BizException("请先创建医生");
        }
        patientSubscribeUpdate(patient, doctor);
        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        Tenant data = tenantR.getData();
        long id = IdUtil.getSnowflake(databaseProperties.getId().getWorkerId(), databaseProperties.getId().getDataCenterId()).nextId();
        String imAccount = imService.registerAccount(ImService.ImAccountKey.PATIENT, id);
        patient.setWxAppId(data.getWxAppId());
        patient.setMobile(patientRegister.getPhone());
        patient.setOpenId(patientRegister.getOpenId());
        patient.setPassword(SecureUtil.md5(patientRegister.getPassword()));
        patient.setIsCompleteEnterGroup(0);
        patient.setImGroupStatus(1);
        patient.setNursingExitChat(0);
        patient.setDoctorExitChat(0);
        patient.setId(id);
        // 由于通过微信关注已经无法获取微信用户的昵称 头像，此处 取消 拉取关注用户信息。
        patient.setAvatar("https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/head-portrait-patient.png");
        // 给患者初始化一个随机字符串作为标记， web端和微信端对比查找患者的依据
        String number = Key.generate6Number();
        patient.setNickName(number);
        // 用户初次入组时，name字段要根据系统设置决定是否填充
        patient.setName(number);
        String letter = ChineseToEnglishUtil.getPinYinFirstLetter(number);
        if (StringUtils.isEmpty(letter)) {
            letter = "Z#";
        }
        patient.setNameFirstLetter(letter);
        String code = genUniqueKey();
        for (int i = countCode(code); i > 0; i = countCode(code)) {
            code = genUniqueKey();
        }
        patient.setCode(code);
        patient.setImAccount(imAccount);
        patient.setStatus(Patient.PATIENT_SUBSCRIBE);
        baseMapper.insert(patient);
        String sysMessageContent = patient.getName() + "关注了公众号";
        // 患者关注并在注册账号，给医助发送一条系统消息
        sendSysMessage(patient, SystemMsgType.subscribe, sysMessageContent);
        // 患者关注并在注册账号，给医生发送一条系统消息
        patientSubToDoctorMessage(patient, SystemMsgType.subscribe, sysMessageContent);
        StringBuilder str = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = sdf.format(date);
        str.append("【").append(patient.getName()).append("】于").append(dateStr).append("关注了公众号");
        // 给医助推一条im消息
        patientSubscribePushServiceAdvisorMessage(patient, null, str.toString());
        return patient;
    }

    /**
     * @return com.caring.sass.user.entity.Patient
     * @Author yangShuai
     * @Description 创建一个新的患者
     * @Date 2020/9/27 13:40
     */
    @Override
    public Patient createNewPatient(Tenant tenant, String wxAppId, Long doctorId, String openId, String unionId) {
        // 准备患者需要的 基本数据
        Patient patient = new Patient();
        Doctor doctor = null;
        if (doctorId != null) {
            doctor = getDoctorService().getById(doctorId);
        }
        if (Objects.isNull(doctor)) {
            // 为患者 设置项目默认的管理人员信息
            boolean success = setDefaultDoctor(patient, tenant);
            if (!success) {
                return null;
            }
        } else {
            patientSubscribeUpdate(patient, doctor);
        }
        long id = IdUtil.getSnowflake(databaseProperties.getId().getWorkerId(), databaseProperties.getId().getDataCenterId()).nextId();
        String imAccount = imService.registerAccount(ImService.ImAccountKey.PATIENT, id);
        patient.setWxAppId(wxAppId);
        patient.setOpenId(openId);
        patient.setUnionId(unionId);
        patient.setIsCompleteEnterGroup(0);
        patient.setImGroupStatus(1);
        patient.setNursingExitChat(0);
        patient.setDoctorExitChat(0);
        GetFollowerInfoForm form = new GetFollowerInfoForm();
        form.setWxAppId(wxAppId);
        form.setUserOpenId(openId);

        // 由于通过微信关注已经无法获取微信用户的昵称 头像，此处 取消 拉取关注用户信息。
        patient.setAvatar("https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/head-portrait-patient.png");
        // 给患者初始化一个随机字符串作为标记， web端和微信端对比查找患者的依据
        String number = Key.generate6Number();
        patient.setNickName(number);
        // 用户初次入组时，name字段要根据系统设置决定是否填充
        patient.setName(number);
        String letter = ChineseToEnglishUtil.getPinYinFirstLetter(number);
        if (StringUtils.isEmpty(letter)) {
            letter = "Z#";
        }
        patient.setNameFirstLetter(letter);

        String code = genUniqueKey();
        for (int i = countCode(code); i > 0; i = countCode(code)) {
            code = genUniqueKey();
        }
        patient.setCode(code);
        patient.setImAccount(imAccount);
        patient.setStatus(Patient.PATIENT_SUBSCRIBE);
        baseMapper.insert(patient);
        return patient;
    }

    @Override
    public void desensitization(List<Patient> records) {

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

        for (Patient record : records) {
            // 对 换着的 姓名。 联系方式 脱敏
            record.setName(SensitiveInfoUtils.desensitizeName(record.getName()));
            record.setMobile(SensitiveInfoUtils.desensitizePhone(record.getMobile()));
            record.setServiceAdvisorName(SensitiveInfoUtils.desensitizeName(record.getServiceAdvisorName()));
            record.setDoctorName(SensitiveInfoUtils.desensitizeName(record.getDoctorName()));
        }
    }

    @Override
    public void deletePatient(Long id) {
        IPage<Patient> page = new Page<>();
        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ();
        lbqWrapper.eq(Patient::getDoctorId, id);
        while (true) {
            page.setSize(30);
            page.setCurrent(1);
            baseMapper.selectPage(page, lbqWrapper);

            List<Patient> records = page.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                for (Patient patient : records) {
                    baseMapper.deleteById(patient.getId());
                }
                PatientDeleteEvent(records);
            } else {
                break;
            }
        }
    }

    @Override
    public boolean removeById(Serializable id) {

        Patient patient = baseMapper.selectById(id);
        if (Objects.nonNull(patient)) {
            baseMapper.deleteById(id);
            List<Patient> patients = new ArrayList<>();
            patients.add(patient);
            PatientDeleteEvent(patients);
        }
        // TODO: 删除其他用户相关的信息
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        List<Patient> patients = baseMapper.selectBatchIds(idList);
        baseMapper.deleteBatchIds(idList);
        PatientDeleteEvent(patients);
        return true;
    }

    @Override
    public boolean remove(Wrapper<Patient> queryWrapper) {

        List<Patient> patients = baseMapper.selectList(queryWrapper);
        baseMapper.delete(queryWrapper);
        PatientDeleteEvent(patients);
        return true;
    }


    public void PatientDeleteEvent(List<Patient> patients) {
        // 清除患者的 IM 账号信息 和 聊天记录。
        String tenant = BaseContextHandler.getTenant();
        for (Patient patient : patients) {
            if (!StringUtils.isEmpty(patient.getImAccount())) {
                imService.removeAccount(patient.getImAccount());
            }
            customGroupingService.removeByPatientId(patient.getId());
            doctorCustomGroupService.removePatient(patient.getId(), patient.getDoctorId());
            try {
                ConsultationGroupService groupService = SpringUtils.getBean(ConsultationGroupService.class);
                if (Objects.nonNull(groupService)) {
                    groupService.deletePatientEvent(patient);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RedisMessageDto messageDto = RedisMessageDto.builder()
                    .tenantCode(tenant)
                    .businessId(patient.getId().toString())
                    .patientImAccount(patient.getImAccount())
                    .build();
            redisTemplate.convertAndSend(SaasRedisBusinessKey.getPatientDeleteKey(), JSON.toJSONString(messageDto));
            if (StrUtil.isNotEmpty(patient.getOpenId())) {
                weiXinApi.deleteWeiXinUserInfo(patient.getOpenId());
            }
        }

    }


    /**
     * @return int
     * @Author yangShuai
     * @Description 统计 code 是否已经存在
     * @Date 2020/9/27 14:38
     */
    public int countCode(String code) {
        LbqWrapper<Patient> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(Patient::getCode, code);
        return baseMapper.selectCount(queryWrapper);
    }

    /**
     * @return java.lang.String
     * @Author yangShuai
     * @Description 生成一个
     * @Date 2020/9/27 14:39
     */
    public static String genUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900) + 100;
        String time = System.currentTimeMillis() + "";
        time = time.substring(time.length() - 6, time.length());
        return time + number;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 忽略 考虑 项目的默认医生，默认护理医助，默认小组不存在情况。
     * @Date 2020/9/27 13:47
     */
    public boolean setDefaultDoctor(Patient patient, Tenant tenant) {
        if (Objects.nonNull(tenant) && Objects.nonNull(patient)) {
            Doctor defaultDoctor = getDoctorService().findByLoginName(tenant.getDomainName() + Constant.DOCTOR_LOGIN_NAME_SUFFIX);
            if (Objects.isNull(defaultDoctor)) {
                log.error("查询系统项目的默认医生未找到，项目Id +" + tenant.getId());
                return false;
            }
            patientSubscribeUpdate(patient, defaultDoctor);
            return true;
        }
        return false;
    }

    /**
     * 患者关注取关。给医生发送系统消息
     * @param patient
     * @param msgType
     * @param sysMessageContent
     */
    private void patientSubToDoctorMessage(Patient patient, String msgType, String sysMessageContent) {
        SystemMsg systemMsg = new SystemMsg();
        systemMsg.setContent(sysMessageContent);
        systemMsg.setMsgType(msgType);
        systemMsg.setUserId(patient.getDoctorId());
        systemMsg.setUserRole(UserType.UCENTER_DOCTOR);
        systemMsgService.save(systemMsg);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description TODO 发送患者关注的 系统消息
     * @Date 2020/9/27 13:12
     */
    private void sendSysMessage(Patient patient, String msgType, String sysMessageContent) {
        SystemMsg systemMsg = new SystemMsg();
        systemMsg.setContent(sysMessageContent);
        systemMsg.setMsgType(msgType);
        systemMsg.setUserId(patient.getServiceAdvisorId());
        systemMsg.setUserRole(UserType.UCENTER_NURSING_STAFF);
        systemMsgService.save(systemMsg);
    }

    /**
     * @return com.caring.sass.user.entity.Patient
     * @Author yangShuai
     * @Description 设置患者得 医生，护理医助，小组信息
     * @Date 2020/9/27 13:09
     */
    private Patient patientSubscribeUpdate(Patient newPatient, Doctor doctor) {
        newPatient.setServiceAdvisorId(doctor.getNursingId());
        newPatient.setOrganCode(doctor.getOrganCode());
        newPatient.setOrganId(doctor.getOrganId());
        newPatient.setOrganName(doctor.getOrganName());
        newPatient.setDoctorId(doctor.getId());
        newPatient.setDoctorName(doctor.getName());
        newPatient.setClassCode(doctor.getClassCode());
        newPatient.setServiceAdvisorId(doctor.getNursingId());
        newPatient.setServiceAdvisorName(doctor.getNursingName());
        Integer buildIn = doctor.getBuildIn();
        if (buildIn != null && buildIn == 1) {
            newPatient.setDefaultDoctorPatient(true);
        } else {
            newPatient.setDefaultDoctorPatient(false);
        }
        return newPatient;
    }

    /**
     * 患者关注，推送 im 消息 到护理医助
     * @return void
     * @Author yangShuai
     * @Description
     * @Date 2020/9/27 13:10
     */
    private void patientSubscribePushServiceAdvisorMessage(Patient patient, NursingStaff nursingStaff, String commsg) {

        if (Objects.isNull(nursingStaff) && null != patient.getServiceAdvisorId()) {
            nursingStaff = getNursingStaffService().getById(patient.getServiceAdvisorId());
        }

        if (null != nursingStaff) {
            Chat chat = new Chat();
            chat.setRegisterMessage(1);
            chat.setSystemMessage(1);
            chat.setHistoryVisible(0);
            imService.setOtherMessage(chat, patient);
            imService.setSenderMessage(chat, patient);

            List<ChatSendRead> chatSendReads = new ArrayList<>(1);
            ChatSendRead send = imService.createNursingStaffSendRead(patient.getImAccount(), nursingStaff, patient.getNursingExitChat());
            chatSendReads.add(send);
            chat.setType(MediaType.text);
            chat.setContent(commsg);
            chat.setSendReads(chatSendReads);
            this.imService.sendChat(chat);
        }

    }


    @Override
    public Patient findByOpenId(String openId) {
        LbqWrapper<Patient> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(Patient::getOpenId, openId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Patient findByMobile(String mobile) {
        try {
            return baseMapper.selectOne(Wraps.<Patient>lbQ()
                    .eq(Patient::getMobile, EncryptionUtil.encrypt(mobile))
                    .last(" limit 0,1 "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Patient findByUnionId(String unionId) {
        LbqWrapper<Patient> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(Patient::getUnionId, unionId).last(" limit 0,1 ");
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 检查openId 是否是一个患者。
     * 当是扫医生登录码。检查时，对于已经入组的患者，修改为关注状态。
     * @param openId
     * @param checkIsDoctor
     * @return
     */
    @Override
    public boolean checkOpenId(String openId, String checkIsDoctor) {
        Patient patient = findByOpenId(openId);
        if (patient != null) {
            if ("checkIsDoctor".equals(checkIsDoctor)) {
                if (patient.getIsCompleteEnterGroup() == 1) {
                    patient.setStatus(1);
                } else {
                    patient.setStatus(0);
                }
                baseMapper.updateById(patient);
            }
            return true;
        }
        return false;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 患者取消关注
     * @Date 2020/10/16 17:09
     */
    @Override
    public void unsubscribe(Patient patient) {
        patient.setStatus(Patient.PATIENT_NO_SUBSCRIBE);
        patient.setUnSubscribeTime(LocalDateTime.now());
        baseMapper.updateById(patient);
        if (null != patient.getServiceAdvisorId()) {
            NursingStaff nursingStaff = this.getNursingStaffService().getById(patient.getServiceAdvisorId());
            StringBuilder str = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String dateStr = sdf.format(date);
            if (!StringUtils.isEmpty(patient.getName())) {
                str.append("【").append(patient.getName()).append("】于").append(dateStr);
                str.append("取消了对您的关注");
                Chat chat = new Chat();

                imService.setOtherMessage(chat, patient);
                imService.setSenderMessage(chat, patient);
                List<ChatSendRead> chatSendReads = new ArrayList<>(2);
                ChatSendRead sendRead = imService.createNursingStaffSendRead(patient.getImAccount(), nursingStaff, patient.getNursingExitChat());
                chatSendReads.add(sendRead);

                chat.setHistoryVisible(0);
                chat.setContent(str.toString());
                chat.setType(MediaType.text);
                chat.setSendReads(chatSendReads);
                chat = imService.sendChat(chat);
            }
        }

        if (null != patient.getServiceAdvisorId() && !StringUtils.isEmpty(patient.getName())) {
            String sysMessageContent = patient.getName() + "刚刚取消了关注";
            sendSysMessage(patient, SystemMsgType.nosubscribe, sysMessageContent);
            patientSubToDoctorMessage(patient, SystemMsgType.nosubscribe, sysMessageContent);
        }

    }



    /**
     * @return long
     * @Author yangShuai
     * @Description 统计医生或者专员下全部患者
     * @Date 2020/10/12 16:43
     */
    @Override
    public long countPatientNoStatus(Long roleId, String role) {

        LbqWrapper<Patient> lbqWrapper = new LbqWrapper<>();
        if (UserType.UCENTER_NURSING_STAFF.equals(role)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, roleId);
        } else if (UserType.UCENTER_DOCTOR.equals(role)) {
            lbqWrapper.eq(Patient::getDoctorId, roleId);
        }
        return baseMapper.selectCount(lbqWrapper);
    }


    /**
     * @return int
     * @Author yangShuai
     * @Description 统计医生或者专员下 某状态的患者
     * @Date 2020/10/12 16:48
     */
    @Override
    public int countPatientByStatus(Integer status, Long userId, String role) {
        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ();
        if (UserType.UCENTER_NURSING_STAFF.equals(role)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, userId);
        } else if (UserType.UCENTER_DOCTOR.equals(role)) {
            lbqWrapper.eq(Patient::getDoctorId, userId);
        } else if (UserType.UCENTER_group.equals(role)) {
            lbqWrapper.apply(" doctor_id in (select udg.doctor_id from u_user_doctor_group udg where udg.group_id = " + userId + ")");
        }
        lbqWrapper.eq(Patient::getStatus, status);

        return baseMapper.selectCount(lbqWrapper);

    }

    /**
     * @return long
     * @Author yangShuai
     * @Description 统计小组下会员人数
     * @Date 2020/10/13 10:21
     */
    @Override
    public long countPatientByGroupId(Long groupId) {
        LbqWrapper<Patient> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Patient::getGroupId, groupId);
        return baseMapper.selectCount(lbqWrapper);
    }

    @Override
    public long countPatientByDiagnosisId(Long userId, String diagnosisId, String role) {
        LbqWrapper<Patient> lbqWrapper = new LbqWrapper<>();
        if (UserType.UCENTER_NURSING_STAFF.equals(role)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, userId);
        } else if (UserType.UCENTER_DOCTOR.equals(role)) {
            lbqWrapper.eq(Patient::getDoctorId, userId);
        } else if (UserType.UCENTER_group.equals(role)) {
            lbqWrapper.apply(" doctor_id in (select udg.doctor_id from u_user_doctor_group udg where udg.group_id = " + userId + ")");
        } else if (UserType.UCENTER_DOCTOR_GROUP.equals(role)) {
            lbqWrapper.apply(" doctor_id in (select udg.doctor_id from u_user_doctor_group udg where" +
                    " udg.group_id in ( select group_id from u_user_doctor_group where doctor_id = " + userId + " ))");
        }
        lbqWrapper.eq(Patient::getDiagnosisId, diagnosisId);
        return baseMapper.selectCount(lbqWrapper);
    }


    /**
     * @return com.alibaba.fastjson.JSONArray
     * @Author yangShuai
     * @Description 统计专员下 各诊断类型人员数量
     * @Date 2020/11/10 16:14
     */
    @Override
    public JSONArray countDiagnosisId(Long userId, String dimension) {
        String tenant = BaseContextHandler.getTenant();
        R<List<Form>> apiFromByCategory = formApi.getFromByCategory(tenant, FormEnum.HEALTH_RECORD.getCode());
        if (apiFromByCategory.getIsSuccess()) {
            List<Form> categoryData = apiFromByCategory.getData();
            if (CollectionUtils.isNotEmpty(categoryData)) {
                Form form = categoryData.get(0);
                String fieldsJson = form.getFieldsJson();
                List<FormField> fields = JSONArray.parseArray(fieldsJson, FormField.class);
                JSONArray ja = new JSONArray();
                for (FormField formField : fields) {
                    if (FormFieldExactType.DIAGNOSE.equals(formField.getExactType())) {
                        List<FormOptions> options = formField.getOptions();
                        for (FormOptions option : options) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("id", option.getId());
                            jsonObject1.put("name", option.getAttrValue());
                            long count = countPatientByDiagnosisId(userId, option.getId(), dimension);
                            jsonObject1.put("count", count);
                            jsonObject1.put("type", "diagnosis");
                            ja.add(jsonObject1);
                        }
                    }
                }
                return ja;
            } else {
                return new JSONArray();
            }
        } else {
            return new JSONArray();
        }
    }

    /**
     * 获取患者的基本信息
     *
     * @param id
     * @return
     */
    @Override
    public Patient getBasePatientById(Long id) {
        return baseMapper.selectOne(Wraps.<Patient>lbQ().select(SuperEntity::getId,Patient::getName, Patient::getAvatar, Patient::getMobile,
                Patient::getDoctorId, Patient::getGroupId, Patient::getServiceAdvisorId, Patient::getDoctorName,
                Patient::getServiceAdvisorName, Patient::getNickName, Patient::getImAccount,
                Patient::getDoctorImGroupStatus, Patient::getNursingStaffImGroupStatus, Patient::getNursingExitChat, Patient::getDoctorExitChatListJson,
                Patient::getStatus, Patient::getOpenId, Patient::getDiagnosisName, Patient::getRemark, Patient::getDoctorRemark)
                .eq(SuperEntity::getId, id));
    }


    @Override
    public void sendManualTemplate(Long patientId) {

        Patient patient = baseMapper.selectById(patientId);

        String tenant = BaseContextHandler.getTenant();
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("AI_STUDIO_PATIENT_MANUAL_TEMPLATE_KEY_" + patientId, "1", 600, TimeUnit.SECONDS);
        if (aBoolean != null && aBoolean) {

            Long doctorId = patient.getDoctorId();
            if (Objects.nonNull(doctorId)) {
                Doctor doctor = getDoctorService().getById(doctorId);
                if (Objects.nonNull(doctor)) {
                    if (doctor.getLoginStatus() != null && doctor.getLoginStatus()) {
                        // 查询患者给医生发送的最后一条消息。
                        Chat chat = imService.queryLastMsg(patientId, patient.getImAccount());
                        if (chat == null) {
                            getDoctorService().sendImWeiXinTemplateMessage(tenant, doctor, MediaType.text, "患者有消息待回复", patient.getName(), patient.getImAccount(),
                                    patient.getId(), TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT);
                        } else {
                            getDoctorService().sendImWeiXinTemplateMessage(tenant, doctor, chat.getType(), chat.getContent(), patient.getName(), patient.getImAccount(),
                                    patient.getId(), TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT);
                        }
                    }

                }
            }
        }
        R<String> accountType = tenantApi.queryOfficialAccountType(tenant);
        String accountTypeData = accountType.getData();
        // 医生身份 给患者发一条 医生在忙，请稍等的消息。
        if (patient.getDoctorId() != null) {
            Chat chat = new Chat();
            chat.setSenderId(patient.getDoctorId().toString());
            chat.setSenderRoleType(UserType.UCENTER_SYSTEM_IM);
            chat.setReceiverId(patient.getId().toString());
            chat.setType(MediaType.text);
            chat.setSystemMessage(1);
            chat.setContent("已转入人工回复，医生繁忙，回复较慢，请耐心等候…");
            chat.setAiHostedSend(true);
            try {
                getDoctorService().aiSendChatToWeiXin(chat, null, accountTypeData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * @return com.caring.sass.msgs.entity.Chat
     * @Author yangShuai
     * @Description 发送消息
     * @Date 2020/11/12 14:32
     */
    @Override
    public Chat sendChat(Chat chat, Integer forcedManualReply) {
        if (StringUtils.isEmpty(chat.getSenderId())) {
            throw new BizException("发送人Id不能为空");
        }
        Patient patient = getBasePatientById(Long.parseLong(chat.getSenderId()));
        if (Objects.isNull(patient)) {
            throw new BizException("发送人不存在");
        }
        if (StringUtils.isEmpty(patient.getImAccount())) {
            throw new BizException("发送人Im账号不存在");
        }
        chat.setHistoryVisible(1);
        imService.setOtherMessage(chat, patient);

        imService.setSenderMessage(chat, patient);

        String tenant = BaseContextHandler.getTenant();
        Doctor doctor = getDoctorService().getBaseDoctorAndImOpenById(patient.getDoctorId());
        R<String> accountType = tenantApi.queryOfficialAccountType(tenant);
        String accountTypeData = accountType.getData();
        NursingStaff nursingStaff = getNursingStaffService().getBaseNursingStaffById(patient.getServiceAdvisorId());
        List<ChatSendRead> chatSendReads = new ArrayList<>(10);
        ChatSendRead send;
        List<Doctor> doctorList = null;
        if (Objects.nonNull(doctor)) {
            send = imService.createDoctorSendRead(accountTypeData, patient.getImAccount(), doctor, patient.getDoctorExitChatListJson());
            chatSendReads.add(send);

            // 这个医生不是一个独立医生。
            if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {

                // 添加 患者医生所在小组 愿意接收消息的 其他医生
                doctorList = doctorGroupService.getReadMyMsgDoctor(patient.getDoctorId());
                chatSendReads.addAll(imService.createDoctorSendRead(accountTypeData, patient.getImAccount(), doctorList, doctor.getId(), patient.getDoctorExitChatListJson()));
            }
        }
        if (Objects.nonNull(nursingStaff)) {
            send = imService.createNursingStaffSendRead(patient.getImAccount(), nursingStaff, patient.getNursingExitChat());
            chatSendReads.add(send);
        }
        chat.setSendReads(chatSendReads);
        chat = imService.sendChat(chat);
        baseMapper.updateById(patient.setHasSendChat(true));
        // 患者只能 @ 医生。
        List<ChatAtRecord> chatAtRecords = chat.getChatAtRecords();
        Boolean weiXinTemplate = weiXinService.noSendWeiXinTemplate(tenant, PlanFunctionTypeEnum.ONLINE_CONSULTATION);
        patientMsgsNoReadCenter.removePatientMsgsNoRead(patient.getId(), tenant);

        // 判断是否要给医助推送模版消息
        boolean cancelNursing = false;
        if (!weiXinTemplate) {
            if (CollUtil.isNotEmpty(chatAtRecords)) {
                for (ChatAtRecord chatAtRecord : chatAtRecords) {
                    if (UserType.UCENTER_NURSING_STAFF.equals(chatAtRecord.getUserType())) {
                        cancelNursing = true;
                        if (patient.getNursingExitChat() == null || patient.getNursingExitChat() == 0) {
                            nursingStaffService.sendAtImWeiXinTemplateMessage(tenant, chatAtRecord.getAtUserId(), patient.getName(), patient.getImAccount(), patient.getId(), patient.getName());
                        }
                    }

                }
            }
            if (!cancelNursing) {
                // 医助没有退出小组才可以
                if (patient.getNursingExitChat() == null || patient.getNursingExitChat() == 0) {
                    nursingStaffService.sendWeixinTemplateMessage(tenant, nursingStaff.getId(), chat.getType(), chat.getContent(), patient.getId(), patient.getName(), patient.getName());
                }
            }
        }

        // 查找@的人员 ，并发送@消息。
        boolean aiHosted = null != doctor.getAiHosted() && doctor.getAiHosted();
        // 强制医生本人来回复。 或者 医生没有托管。那么给医生判断是否发送模版消息
        if (forcedManualReply != null && forcedManualReply == 1 || !aiHosted) {
            boolean cancel = false;
            if (!weiXinTemplate) {
                if (CollUtil.isNotEmpty(chatAtRecords)) {
                    for (ChatAtRecord chatAtRecord : chatAtRecords) {
                        if (UserType.UCENTER_DOCTOR.equals(chatAtRecord.getUserType())) {
                            if (chatAtRecord.getAtUserId().equals(doctor.getId())) {
                                cancel = true;
                            }
                            // 发送@提醒
                            if (StrUtil.isEmpty(patient.getDoctorExitChatListJson()) || !patient.getDoctorExitChatListJson().contains(doctor.getId().toString())) {
                                getDoctorService().sendAtImWeiXinTemplateMessage(tenant, chatAtRecord.getAtUserId(), patient.getName(), patient.getImAccount(), patient.getId(), patient.getName());
                            }
                        }
                    }
                }
                // 如果患者自己的医生 设置了 关闭和患者的沟通。则设置取消给医生发送微信模板推送
                if (StrUtil.isNotEmpty(patient.getDoctorExitChatListJson()) && patient.getDoctorExitChatListJson().contains(patient.getDoctorId().toString())) {
                    cancel = true;
                }
                // 通过@的医生和 doctor 是同一个人。则下面的提醒就取消发送
                if (!cancel) {
                    if (StrUtil.isEmpty(patient.getDoctorExitChatListJson()) || !patient.getDoctorExitChatListJson().contains(doctor.getId().toString())) {
                        getDoctorService().sendImWeiXinTemplateMessage(tenant, doctor, chat.getType(), chat.getContent(), patient.getName(), patient.getImAccount(),
                                patient.getId(), TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT);
                    }
                }
            }
        } else {
            // ai托管了。 异步让AI回复
            String content = chat.getContent();
            MediaType chatType = chat.getType();
            List<Doctor> doctors = doctorList;
            SaasGlobalThreadPool.execute(() -> aiReplyChat(content, doctor, doctors, accountTypeData, patient, tenant, chatType));
        }


        // 将患者发送的消息内容。和患者信息。 加入到消息队列中去。 后续的关键字消息回复 由消息队列去处理
        // 即将接收 关键字回复的所有人 作为接受者 放入消息队列中去。 由于是 系统身份推送。
        // 接收者中还需要患者自身。用于接收im消息
        if (MediaType.text.equals(chat.getType())) {
            try {
                send = imService.createPatientSendRead(patient.getImAccount(), patient);
                chatSendReads.add(send);
                KeywordChatDto keywordChatDto = new KeywordChatDto();
                keywordChatDto.setPatientId(patient.getId());
                keywordChatDto.setContent(chat.getContent());
                keywordChatDto.setTenantCode(BaseContextHandler.getTenant());
                keywordChatDto.setChatSendReads(JSON.parseArray(JSON.toJSONString(chatSendReads)));
                redisTemplate.opsForList().leftPush(SaasRedisBusinessKey.PATIENT_MESSAGE_KEYWORD_LIST, JSON.toJSONString(keywordChatDto));
            } catch (Exception e) {
                log.info("推送关键字后续处理事件失败");
            }

        }
        return chat;
    }


    @Autowired
    DifyConfig difyConfig;

    public void aiReplyChat(String content, Doctor doctor, List<Doctor> doctorList,
                              String accountTypeData, Patient patient, String tenantCode, MediaType chatType) {

        BaseContextHandler.setTenant(tenantCode);
        Chat aiChat = new Chat();
        String aiReplyContent = "";
        try {
            if (MediaType.text.equals(chatType) || MediaType.image.equals(chatType)) {
                aiReplyContent = replyChat(content, doctor, patient.getId().toString());
            } else {
                if (MediaType.voice.equals(chatType)) {
                   aiReplyContent =  "您好，我无法分析语音，请您描述一下您的问题，我会尽力提供一些健康建议。如果情况紧急或症状严重，建议尽快前往医院就诊。";
                } else if (MediaType.file.equals(chatType)) {
                    aiReplyContent =  "您好，我无法分析文件，请您描述一下您的问题，我会尽力提供一些健康建议。如果情况紧急或症状严重，建议尽快前往医院就诊。";
                } else {
                    return;
                }
            }
            aiChat.setSenderId(doctor.getId().toString());
            aiChat.setReceiverId(patient.getId().toString());
            aiChat.setType(MediaType.text);
            aiChat.setContent(aiReplyContent);
            aiChat.setAiHostedSend(true);
            getDoctorService().aiSendChatToWeiXin(aiChat, doctorList, accountTypeData);

        } catch (Exception e) {
            log.info("开始订阅流式响应失败,{}", e.getMessage());
        }

    }


    /**
     * 调用Dify接口回复
     *
     * @param query  患者消息
     * @param doctor 医生信息
     * @param userId 患者ID
     * @return 医生回复
     * @throws IOException
     */
    public String replyChat(String query, Doctor doctor, String userId) throws IOException {
        DifyChatClient chatClient = DifyClientFactory.createChatClient(difyConfig.getDIFY_BASE_URL(), difyConfig.getDIFY_SAAS_AI_REPLY_API_KEY());

        // 创建聊天消息
        Map<String, Object> inputs = new HashMap<>(5);
        inputs.put("name", doctor.getName());
        inputs.put("hospital", doctor.getHospitalName());
        inputs.put("department", doctor.getDeptartmentName());
        inputs.put("title", doctor.getTitle());
        inputs.put("attach_info", doctor.getRegistrationInformation());
        String extraInfo = doctor.getExtraInfo();
        if (extraInfo != null) {
            JSONObject jsonObject = JSON.parseObject(extraInfo);
            String specialties = jsonObject.getString("Specialties");
            inputs.put("specialty", specialties);
        }
        ChatMessage message = ChatMessage.builder()
                .query(query)
                .user(userId)
                .inputs(inputs)
                .responseMode(ResponseMode.BLOCKING)
                .build();

        // 发送聊天消息并获取响应
        ChatMessageResponse response = chatClient.sendChatMessage(message);
        // 处理响应
        return response.getAnswer();
    }


    @Override
    public List<ImGroupUser> getImGroupUser(Long patientId) {
        Patient patient = getBasePatientById(patientId);
        List<ImGroupUser> imGroupUsers = new ArrayList<>();
        if (patient == null) {
            return new ArrayList<>();
        }
        ImGroupUser imGroupUser = new ImGroupUser();
        BeanUtils.copyProperties(patient, imGroupUser);
        imGroupUser.setId(patient.getId());
        imGroupUser.setType(UserType.UCENTER_PATIENT);
        imGroupUser.setPatient(patient);
        imGroupUsers.add(imGroupUser);
        String doctorExitChatListJson = patient.getDoctorExitChatListJson();
        if (patient.getDoctorId() != null) {
            patient.setDoctorExitChat(0);
            if (StrUtil.isNotEmpty(doctorExitChatListJson)) {
                if (doctorExitChatListJson.contains(patient.getDoctorId().toString())) {
                    patient.setDoctorExitChat(1);
                }
            }
            Doctor doctor = getDoctorService().getBaseDoctorAndImOpenById(patient.getDoctorId());
            if (doctor != null) {
                // 医生需要关注后，拥有微信端后才会出现在小组中
                if (doctor.getImMsgStatus() != null && doctor.getImMsgStatus() == 1 || (doctor.getAiHosted() != null && doctor.getAiHosted())) {
                    imGroupUser = new ImGroupUser();
                    BeanUtils.copyProperties(doctor, imGroupUser);
                    imGroupUser.setId(doctor.getId());
                    imGroupUser.setType(UserType.UCENTER_DOCTOR);
                    imGroupUser.setAiHosted(doctor.getAiHosted());
                    imGroupUsers.add(imGroupUser);
                }
            }
        }

        if (patient.getServiceAdvisorId() != null) {
            NursingStaff nursingStaff = getNursingStaffService().getBaseNursingStaffById(patient.getServiceAdvisorId());
            if (nursingStaff != null) {
                imGroupUser = new ImGroupUser();
                BeanUtils.copyProperties(nursingStaff, imGroupUser);
                imGroupUser.setId(nursingStaff.getId());
                imGroupUser.setType(UserType.UCENTER_NURSING_STAFF);
                imGroupUsers.add(imGroupUser);
            }
        }
        return imGroupUsers;
    }

    @Autowired
    PatientNursingPlanApi patientNursingPlanApi;

    @Override
    public void diseaseInformationStatus(Long patientId) {
        UpdateWrapper<Patient> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("disease_information_status", 1);
        updateWrapper.eq("id", patientId);
        baseMapper.update(new Patient(), updateWrapper);
    }

    @Override
    public void agreeAgreement(Long patientId) {
        UpdateWrapper<Patient> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("agree_agreement", 1);
        updateWrapper.eq("id", patientId);
        baseMapper.update(new Patient(), updateWrapper);
    }

    @Override
    public void completeEnterGroup(Long patientId) {
        Patient patient = baseMapper.selectById(patientId);
        if (patientId == null) {
            return;
        }
        if (patient.getIsCompleteEnterGroup() != null && patient.getIsCompleteEnterGroup().equals(1)) {
            // 已经入组过了。就不在入组了
            return;
        }

        patient.setIsCompleteEnterGroup(1);
        if (patient.getNursingTime() == null) {
            patient.setNursingTime(LocalDateTime.now());
        }
        patient.setCompleteEnterGroupTime(LocalDateTime.now());
        patient.setAgreeAgreement(1);
        patient.setDiseaseInformationStatus(0);
        patient.setStatus(Patient.PATIENT_SUBSCRIBE_NORMAL);
        baseMapper.updateById(patient);
        weiXinService.sendJoinGroupSuccess(patient);
        sendSysMessage(patient, SystemMsgType.complete, patient.getName() + "完成了注册");
        // 将患者入组后首次默认绑定全部随访
        patientNursingPlanApi.patientFirstSubscribePlanAll(patientId);

        // 若是患者拉新进来的患者，则需要更新患者推荐关系中的注册状态
        LbuWrapper<PatientRecommendRelationship> patientRecommendRelationship = new LbuWrapper<>();
        patientRecommendRelationship.set(PatientRecommendRelationship::getPassivePatientRegister,2);
        patientRecommendRelationship.set(PatientRecommendRelationship::getPassivePatientRegisterTime,LocalDateTime.now());
        patientRecommendRelationship.eq(PatientRecommendRelationship::getPassivePatientId,patientId);
        patientRecommendRelationshipService.update(patientRecommendRelationship);


        // 查询要求注册完成时推送的护理计划。 并直接进行推送
        patientNursingPlanApi.queryRegisterCompletePlanAndPush(patientId);
    }

    @Override
    public IPage<Patient> findPage(IPage<Patient> page, Wrapper<Patient> wrapper) {
        return baseMapper.findPage(page, wrapper, new DataScope());
    }


    /**
     * 给患者添加新标签，并清除他可能存在的医生标签
     * @param subscribe
     * @param tenant
     */
    @Override
    public void createWeiXinTag(Patient subscribe, String tenant) {
        BaseContextHandler.setTenant(tenant);
        BindUserTagsForm bindUserTagsForm = new BindUserTagsForm();
        bindUserTagsForm.setWxAppId(subscribe.getWxAppId());
        bindUserTagsForm.setOpenIds(subscribe.getOpenId());
        bindUserTagsForm.setTagId(TagsEnum.PATIENT_TAG.getValue());
        List<String> clearTagIds = new ArrayList<>();
        clearTagIds.add(TagsEnum.DOCTOR_TAGS.getValue());
        clearTagIds.add(TagsEnum.TOURISTS_TAG.getValue());
        bindUserTagsForm.setClearTagId(clearTagIds);
        weiXinApi.bindUserTags(bindUserTagsForm);

    }



    /**
     * 为患者发送消息提醒的模板
     * @param tenantCode
     * @param status
     * @param imAccount
     * @param openId
     * @param type
     * @param content
     * @param prefix
     */
    @Async
    @Override
    public void sendWeiXinTemplateMessage(String tenantCode, Integer status, String imAccount, String openId,
                                          MediaType type, String content, Long patientId, String prefix, String sendUserName, String userRole) {
        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            return;
        }
        String patientChat = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CHAT);
        if (status != null && status != 2) {
            Boolean online = this.imService.online(imAccount);
            if (online == null || online.equals(false)) {
                if (tenant.isCertificationServiceNumber()) {
                    weiXinService.sendConsultationResponse(tenant.getWxAppId(), openId, type, content , prefix, patientChat, patientId, TemplateMessageIndefiner.CONSULTATION_RESPONSE, tenant.getDefaultLanguage());
                } else {
                    patientMsgsNoReadCenter.savePatientMsgsNoRead(patientId, tenantCode, tenant.getDomainName(), tenant.getName(), sendUserName, userRole);
                }
            }
        }
    }


    /**
     * 给患者发送 @ 模板消息
     * @param tenantCode
     * @param sendUserName
     */
    @Async
    @Override
    public void sendAtImWeiXinTemplateMessage(String tenantCode, Long patientId, String sendUserName, String userRole) {

        BaseContextHandler.setTenant(tenantCode);
        Patient patient = getBasePatientById(patientId);
        if (Objects.isNull(patient)) {
            return;
        }
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            return;
        }

        String patientChat = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CHAT);
        TenantDiseasesTypeEnum diseasesType = tenant.getDiseasesType();
        if (tenant.isCertificationServiceNumber()) {
            if (patient.getStatus() != null && patient.getStatus() != 2) {
                weiXinService.sendConsultationResponse(tenant.getWxAppId(), patient.getOpenId(), sendUserName, patientChat, patient.getName(), tenant.getDefaultLanguage());
            }
        } else {
            // 给患者发送一条@消息的提醒
            if (StrUtil.isEmpty(patient.getMobile())) {
                return;
            }
            if (UserType.UCENTER_DOCTOR.equals(userRole)) {
                userRole = dictionaryItemService.findDictionaryItemName(DictionaryItemService.DOCTOR);
            } else if (UserType.UCENTER_NURSING_STAFF.equals(userRole)) {
                userRole = dictionaryItemService.findDictionaryItemName(DictionaryItemService.ASSISTANT);
            }
            String smsParams = BusinessReminderType.getPatientImmediatelyConsultationNotice(tenant.getName(), sendUserName, userRole);
            BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                    .mobile(patient.getMobile())
                    .wechatRedirectUrl(patientChat)
                    .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                    .type(BusinessReminderType.PATIENT_IMMEDIATELY_CONSULTATION_NOTICE)
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
     * 批量转移医生的患者到其他医生
     * @param changeDoctorMore
     * @return
     */
    public Boolean changeDoctorMore(ChangeDoctorMore changeDoctorMore) {

        Boolean moreAll = changeDoctorMore.getAll();
        List<Long> morePatientIds = changeDoctorMore.getPatientIds();

        if (moreAll) {
            // 查询医生所有的的患者，进行转移
            List<Patient> patientList = baseMapper.selectList(Wraps.<Patient>lbQ()
                    .eq(Patient::getDoctorId, changeDoctorMore.getFormDoctorId())
                    .select(SuperEntity::getId, Patient::getName));
            if (CollUtil.isEmpty(patientList)) {
                return true;
            }
            for (Patient patient : patientList) {
                try {
                    Boolean changed = changeDoctor(patient.getId(), changeDoctorMore.getToDoctorId());
                    if (!changed) {
                        log.error("changeDoctor error patientId: {}, doctorId: {}", patient.getId(), changeDoctorMore.getToDoctorId());
                    }
                } catch (Exception e) {
                }
            }
            return true;
        } else if (CollUtil.isNotEmpty(morePatientIds)) {
            for (Long patientId : morePatientIds) {
                try {
                    Boolean changed = changeDoctor(patientId, changeDoctorMore.getToDoctorId());
                    if (!changed) {
                        log.error("changeDoctor error patientId: {}, doctorId: {}", patientId, changeDoctorMore.getToDoctorId());
                    }
                } catch (Exception e) {
                }
            }
            return true;
        } else {
            return false;
        }
    }


    @Transactional
    @Override
    public Boolean changeDoctor(Long patientId, Long doctorId) {

        Doctor doctor = getDoctorService().getById(doctorId);
        if (Objects.isNull(doctor)) {
            return false;
        }
        Patient patient = baseMapper.selectById(patientId);
        if (Objects.isNull(patient)) {
            return false;
        }
        Long oldDoctorId = patient.getDoctorId();
        Long oldNursingId = patient.getServiceAdvisorId();
        boolean changeNursing = false;
        // 患者原先的医助和现在医生的医助不是同一人， 清除医助对患者的备注
        if (!oldNursingId.equals(doctor.getNursingId())) {
            patient.setRemark("");
            changeNursing = true;
        }
        patient.setDoctorRemark("");
        patient.setServiceAdvisorName(doctor.getNursingName());
        patient.setServiceAdvisorId(doctor.getNursingId());
        patient.setOrganId(doctor.getOrganId());
        patient.setClassCode(doctor.getClassCode());
        patient.setOrganCode(doctor.getOrganCode());
        patient.setOrganName(doctor.getOrganName());
        patient.setCreateTime(LocalDateTime.now());
        patient.setDoctorId(doctorId);
        patient.setDoctorName(doctor.getName());
        Integer buildIn = doctor.getBuildIn();
        if (buildIn != null && buildIn == 1) {
            patient.setDefaultDoctorPatient(true);
        } else {
            patient.setDefaultDoctorPatient(false);
        }
        baseMapper.updateAllById(patient);
        // 如果更换了医助。 清除医助小组和患者的关系。
        if (changeNursing) {
            customGroupingService.removeByPatientId(patientId);
        }
        doctorCustomGroupService.removePatient(patientId, oldDoctorId);

        // 发出患者转移医生的消息事件
        RedisMessagePatientChangeDoctor redisMessagePatientChangeDoctor = new RedisMessagePatientChangeDoctor();
        // 患者信息
        redisMessagePatientChangeDoctor.setPatientId(patientId);
        redisMessagePatientChangeDoctor.setTenantCode(BaseContextHandler.getTenant());
        redisMessagePatientChangeDoctor.setOldDoctorId(oldDoctorId);
        redisMessagePatientChangeDoctor.setOldNursingId(oldNursingId);
        redisMessagePatientChangeDoctor.setPatientImAccount(patient.getImAccount());

        // 目标医生
        redisMessagePatientChangeDoctor.setTargetDoctorId(doctorId);
        redisMessagePatientChangeDoctor.setTargetDoctorName(doctor.getName());

        // 目标医助
        redisMessagePatientChangeDoctor.setChangeNursing(changeNursing);
        redisMessagePatientChangeDoctor.setTargetNursingId(doctor.getNursingId());
        redisMessagePatientChangeDoctor.setTargetDoctorName(doctor.getNursingName());

        // 组织信息
        redisMessagePatientChangeDoctor.setOrgId(doctor.getOrganId());
        redisMessagePatientChangeDoctor.setOrgName(doctor.getOrganName());
        redisMessagePatientChangeDoctor.setOrgCode(doctor.getOrganCode());
        redisMessagePatientChangeDoctor.setClassCode(doctor.getClassCode());

        redisTemplate.convertAndSend(SaasRedisBusinessKey.PATIENT_CHANGE_DOCTOR, JSON.toJSONString(redisMessagePatientChangeDoctor));
        return true;
    }


    /**
     * 统计 小组下的患者 数量
     * 先 统计小组下 每个医生自己的患者数量
     * 然后对小组下的医生患者进行 加法 计算
     * @param groups
     */
    @Override
    public void countPatientByGroupId(List<Group> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return;
        }
        List<Long> groupIds = groups.stream().map(SuperEntity::getId).collect(Collectors.toList());
        String joinGroupIds = ListUtils.getSqlIdsJoin(groupIds);
        // 按医生分组统计 每个医生的患者数量
        QueryWrapper<Patient> queryWrapper = Wrappers.<Patient>query()
                .select("doctor_id as doctorId", "count(*) as total")
                .groupBy("doctor_id")
                .apply(" doctor_id in (select udg.doctor_id from u_user_doctor_group udg where udg.group_id in ( " + joinGroupIds + "))");
        Map<Long, Long> countDoctor = countPatient(queryWrapper);

        // 查询小组下的所有医生
        LbqWrapper<DoctorGroup> wrapper = Wraps.<DoctorGroup>lbQ().in(DoctorGroup::getGroupId, groupIds)
                .select(DoctorGroup::getDoctorId, DoctorGroup::getGroupId);
        List<DoctorGroup> doctorGroups = doctorGroupService.list(wrapper);

        // 获取小组内医生的患者统计。 累加到小组下
        Map<Long, Long> groupCountPatient = new HashMap<>(groups.size());
        for (DoctorGroup doctorGroup : doctorGroups) {
            Long groupPatientNumber = groupCountPatient.get(doctorGroup.getGroupId());
            Long doctorPatientNumber = countDoctor.get(doctorGroup.getDoctorId());
            if (doctorPatientNumber != null) {
                if (groupPatientNumber != null) {
                    groupCountPatient.put(doctorGroup.getGroupId(), doctorPatientNumber + groupPatientNumber);
                } else {
                    groupCountPatient.put(doctorGroup.getGroupId(), doctorPatientNumber);
                }
            }
        }
        for (Group group : groups) {
            Long aLong = groupCountPatient.get(group.getId());
            if (aLong != null) {
                group.setPatientCount(aLong);
            }
        }

    }

    /**
     * 统计
     * @param wrapper
     * @return
     */
    private Map<Long, Long> countPatient(QueryWrapper<Patient> wrapper) {
        List<Map<String, Object>> doctorPatientCount = baseMapper.selectMaps(wrapper);

        Map<Long, Long> countDoctor = new HashMap<>(doctorPatientCount.size());
        for (Map<String, Object> objectMap : doctorPatientCount) {
            Object doctorId = objectMap.get("doctorId");
            Object total = objectMap.get("total");
            if (total != null && doctorId != null) {
                countDoctor.put(Long.parseLong(doctorId.toString()), Convert.toLong(total));
            }
        }
        return countDoctor;
    }

    /**
     * 统计医生下的患者数量
     * @param doctorList
     */
    @Override
    public void countPatientByDoctor(List<Doctor> doctorList) {
        if (CollectionUtils.isEmpty(doctorList)) {
            return;
        }
        List<Long> doctorIds = doctorList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        QueryWrapper<Patient> wrapper = Wrappers.<Patient>query()
                .select("doctor_id as doctorId", "count(*) as total")
                .groupBy("doctor_id")
                .in("doctor_id", doctorIds);

        Map<Long, Long> countDoctor = countPatient(wrapper);
        for (Doctor doctor : doctorList) {
            doctor.setTotalPatientCount(countDoctor.get(doctor.getId()));
        }
    }

    /**
     * 群组 详细成员
     * @param patientId
     * @return
     */
    @Override
    public ImGroupDetail getImGroupDetail(Long patientId, Long nursingId, Long doctorId) {
        ImGroupDetail detail = new ImGroupDetail();
        Patient patient = getBasePatientById(patientId);
        detail.setPatient(patient);
        Long serviceAdvisorId = patient.getServiceAdvisorId();
        List<ImGroupMember> imGroupMembers = new ArrayList<>();
        if (serviceAdvisorId != null) {
            NursingStaff nursingStaff = getNursingStaffService().getBaseNursingStaffById(serviceAdvisorId);
            if (nursingStaff != null) {
                if (patient.getNursingExitChat() == null || patient.getNursingExitChat() == 0) {
                    imGroupMembers.add(new ImGroupMember(nursingStaff, nursingId));
                }
            }
        }
        Long patientDoctorId = patient.getDoctorId();
        if (patientDoctorId != null) {
            Doctor doctor = getDoctorService().getById(patientDoctorId);
            // 非独立医生
            String doctorExitChatListJson = patient.getDoctorExitChatListJson();
            if (StrUtil.isNotEmpty(doctorExitChatListJson) && Objects.nonNull(doctorId) && doctorExitChatListJson.contains(doctorId.toString())) {
                patient.setDoctorExitChat(1);
            } else {
                patient.setDoctorExitChat(0);
            }
            if (doctor != null) {
                if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {
                    // 查询医生所在小组 的所有医生
                    LbqWrapper<DoctorGroup> wrapper = Wraps.<DoctorGroup>lbQ()
                            .select(DoctorGroup::getDoctorId)
                            .apply("group_id in (select group_id from u_user_doctor_group where doctor_id = " + patientDoctorId + ")");
                    List<DoctorGroup> doctorGroups = doctorGroupService.list(wrapper);
                    if (CollectionUtils.isNotEmpty(doctorGroups)) {
                        List<Long> doctorIds = doctorGroups.stream().map(DoctorGroup::getDoctorId).collect(Collectors.toList());
                        LbqWrapper<Doctor> lbqWrapper = Wraps.<Doctor>lbQ().in(SuperEntity::getId, doctorIds)
                                .select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar, Doctor::getAiHosted, Doctor::getImAccount);
                        List<Doctor> doctors = doctorService.list(lbqWrapper);
                        for (Doctor doctorOther : doctors) {
                            if (StrUtil.isEmpty(doctorExitChatListJson) || !doctorExitChatListJson.contains(doctorOther.getId().toString())) {
                                imGroupMembers.add(new ImGroupMember(doctorOther, doctorId));
                            }
                        }
                    }
                } else {
                    if (StrUtil.isEmpty(doctorExitChatListJson) || !doctorExitChatListJson.contains(patientDoctorId.toString())) {
                        imGroupMembers.add(new ImGroupMember(doctor, doctorId));
                    }
                }
            }
        }
        detail.setGroupMembers(imGroupMembers);
        return detail;
    }

    /**
     * 查询 code 下 createTime 距今 在 47小时到 48小时之前的患者。
     *
     * @param tenantCode
     * @param reminder
     */
    @Deprecated
    public void unregisteredReminder(String tenantCode, String reminder, LocalDateTime startTime, LocalDateTime endTime) {

        BaseContextHandler.setTenant(tenantCode);

        LbqWrapper<Patient> wrapper = Wraps.<Patient>lbQ().eq(Patient::getStatus, 0)
                .eq(Patient::getIsCompleteEnterGroup, 0)
                .ge(SuperEntity::getCreateTime, startTime)
                .lt(SuperEntity::getCreateTime, endTime)
                .select(SuperEntity::getId, Patient::getOpenId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        if (CollUtil.isEmpty(patientList)) {
            return;
        }
        SendKefuMsgForm msgForm ;
        WxMpKefuMessage message ;
        List<PatientUnRegisteredReminder> patientUnRegisteredReminders = new ArrayList<>(patientList.size());
        PatientUnRegisteredReminder registeredReminder;
        for (Patient patient : patientList) {
            msgForm = new SendKefuMsgForm();
            message = new WxMpKefuMessage();
            message.setToUser(patient.getOpenId());
            message.setMsgType(WxConsts.KefuMsgType.TEXT);
            message.setContent(reminder);
            msgForm.setMessage(message);
            registeredReminder = new PatientUnRegisteredReminder();
            registeredReminder.setPatientId(patient.getId());
            registeredReminder.setReminder(reminder);
            try {
                weiXinApi.sendKefuMsg(msgForm);
            } catch (BizException exception) {
                String exceptionMessage = exception.getMessage();
                registeredReminder.setReminder(exceptionMessage);
            }
            patientUnRegisteredReminders.add(registeredReminder);
        }
        if (CollUtil.isNotEmpty(patientUnRegisteredReminders)) {
            PatientUnRegisteredReminderMapper mapper = SpringUtils.getBean(PatientUnRegisteredReminderMapper.class);
            mapper.insertBatchSomeColumn(patientUnRegisteredReminders);
        }

    }

    /**
     * 查询开启了 注册提醒 的项目
     */
    @Deprecated
    @Override
    public void unregisteredReminder() {

        LocalDateTime now = null;
        // 存储本次任务， 查询用户创建时间的最小时间
        String anotherTime = redisTemplate.execute((RedisCallback<String>) redisConnection -> {
            byte[] subscribeKeys = redisConnection.get(PATIENT_REMINDER_REDIS_KEY.getBytes());
            if (subscribeKeys != null) {
                return new String(subscribeKeys);
            }
            return null;
        });
        // redis中没有记录最小时间，以当前时间的 -47小时为准
        if (StringUtils.isEmpty(anotherTime)) {
            now = LocalDateTime.now();
            now = now.plusHours(-47);
            now = now.withNano(0);
            now = now.withSecond(0);
        } else {
            now = LocalDateTime.parse(anotherTime);
        }
        final LocalDateTime startTime = now;
        final LocalDateTime localDateTime = now.plusMinutes(30);
        final String nextTime = localDateTime.toString();
        // 存储下一次任务开始时， 查询用户创建时间的最小时间
        redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Boolean set = redisConnection.set(PATIENT_REMINDER_REDIS_KEY.getBytes(), nextTime.getBytes());
            return set;
        });

        R<List<RegGuide>> unregisteredReminder = guideApi.getOpenUnregisteredReminder();
        if (unregisteredReminder.getIsSuccess() != null && unregisteredReminder.getIsSuccess()) {
            List<RegGuide> unregisteredReminderData = unregisteredReminder.getData();
            Map<String, String> collect = unregisteredReminderData.stream()
                    .filter(item -> !StringUtils.isEmpty(item.getUnregisteredReminder()))
                    .collect(Collectors.toMap(RegGuide::getTenantCode, RegGuide::getUnregisteredReminder));
            for (Map.Entry<String, String> stringEntry : collect.entrySet()) {
                String tenantCode = stringEntry.getKey();
                String reminder = stringEntry.getValue();
                PATIENT_EXECUTOR.execute(() -> unregisteredReminder(tenantCode, reminder, startTime, localDateTime));
            }
        }
    }


    @Override
    public void updateUserOrgName(long orgId, String orgName) {
        UpdateWrapper<Patient> wrapper = new UpdateWrapper<>();
        wrapper.set("organ_name", orgName).eq("org_id", orgId);
        baseMapper.update(new Patient(), wrapper);
    }


    @Override
    public void updateByOpenId(WxOAuth2UserInfo wxOAuth2UserInfo) {
        String openId = wxOAuth2UserInfo.getOpenid();
        String nickname = wxOAuth2UserInfo.getNickname();
        String headImgUrl = wxOAuth2UserInfo.getHeadImgUrl();
        boolean update = false;
        if (!StringUtils.isEmpty(openId) && !StringUtils.isEmpty(nickname) && !StringUtils.isEmpty(headImgUrl)) {
            Patient patient = findByOpenId(openId);
            if (Objects.isNull(patient)) {
                return;
            }
            String name = patient.getName();
            String avatar = patient.getAvatar();
            if (StringUtils.isEmpty(avatar)) {
                patient.setAvatar(headImgUrl);
                update = true;
            }
            String nameFirstLetter = patient.getNameFirstLetter();
            // 姓名首字母是空。 很可能是因为名字是 初始化的随机数字编号。
            if (StringUtils.isEmpty(nameFirstLetter)) {
                Integer enterGroup = patient.getIsCompleteEnterGroup();
                if (enterGroup != null && enterGroup == 0) {
                    Pattern pattern = Pattern.compile("[0-9]*");
                    boolean matches = pattern.matcher(name).matches();
                    if (matches) {
                        patient.setName(nickname);
                        patient.setRemark(nickname);
                        patient.setNickName(nickname);
                        update = true;
                    }
                }
            }
            if (update) {
                updateById(patient);
            }
        }
    }

    /**
     * 查询医生对患者备注
     * @param ids
     * @return
     */
    @Override
    public Map<Long, String> findDoctorPatientRemark(List<Long> ids) {

        List<Patient> patients = baseMapper.selectList(Wraps.<Patient>lbQ()
                .select(SuperEntity::getId, Patient::getDoctorRemark)
                .in(SuperEntity::getId, ids));
        if (CollUtil.isNotEmpty(patients)) {
            return patients.stream().filter(item -> StrUtil.isNotEmpty(item.getDoctorRemark()))
                    .collect(Collectors.toMap(SuperEntity::getId, Patient::getDoctorRemark));
        }
        return new HashMap<>();
    }

    /**
     * 查询医助对患者备注
     * @param ids
     * @return
     */
    @Override
    public Map<Long, String> findNursingPatientRemark(List<Long> ids) {

        List<Patient> patients = baseMapper.selectList(Wraps.<Patient>lbQ()
                .select(SuperEntity::getId, Patient::getRemark)
                .in(SuperEntity::getId, ids));
        if (CollUtil.isNotEmpty(patients)) {
            return patients.stream().filter(item -> StrUtil.isNotEmpty(item.getRemark()))
                    .collect(Collectors.toMap(SuperEntity::getId, Patient::getRemark));
        }
        return new HashMap<>();
    }


    /**
     * 通过ID查询患者的基本信息，医生医助的备注，聊天开关状态。
     * @param ids
     * @return
     */
    @Override
    public Map<Long, Patient> findPatientBaseInfoByIds(List<Long> ids) {

        List<Patient> patients = baseMapper.selectList(Wraps.<Patient>lbQ()
                .select(SuperEntity::getId, Patient::getRemark, Patient::getDoctorRemark, Patient::getName,
                        Patient::getSex, Patient::getBirthday, Patient::getStatus, Patient::getDiagnosisName,
                        Patient::getDoctorExitChatListJson, Patient::getNursingExitChat)
                .in(SuperEntity::getId, ids));
        if (CollUtil.isNotEmpty(patients)) {
            return patients.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
        }
        return new HashMap<>();
    }

    /**
     * 医助设置退出聊天
     * @param patientId
     * @param exitChat
     * @param dictionaryMap
     */
    @Override
    public void nursingExitChat(Long patientId, Integer exitChat, Map<String, String> dictionaryMap) {
        if (exitChat.equals(1) || exitChat.equals(0)) {
            Patient patient = new Patient();
            patient.setId(patientId);
            patient.setNursingExitChat(exitChat);
            baseMapper.updateById(patient);
        } else {
            return;
        }
        // 给患者这个群组相关的用户发送一条 打开聊天 或者 关闭聊天。
        Long nursingId = BaseContextHandler.getUserId();
        if (nursingId == null) {
            return;
        }
        Chat chat = new Chat();
        chat.setType(exitChat.equals(1) ? MediaType.exitChat : MediaType.openChat);
        chat.setReceiverId(patientId.toString());
        chat.setSenderId(nursingId.toString());
        // 设置聊天内容 XXX 医助
        NursingStaff nursingStaff = getNursingStaffService().getById(nursingId);
        if (nursingStaff == null) {
            return;
        }
        String assistant = dictionaryMap.get("assistant");
        StringBuilder content = new StringBuilder();
        content.append(nursingStaff.getName());
        if (StrUtil.isEmpty(assistant)) {
            content.append("(医助)");
        } else {
            content.append("(").append(assistant).append(")");
        }
        if (exitChat.equals(1)) {
            content.append(" 退出聊天");
        } else {
            content.append(" 重新加入聊天");
        }
        chat.setContent(content.toString());
        getNursingStaffService().sendChatToWeiXin(chat, 0);
    }

    /**
     * 医生设置退出聊天
     * @param patientId
     * @param exitChat
     * @param dictionaryMap
     */
    @Override
    public void doctorExitChat(Long patientId, Integer exitChat, Map<String, String> dictionaryMap) {
        Long doctorId = BaseContextHandler.getUserId();
        if (exitChat.equals(1) || exitChat.equals(0)) {
            Patient patient = getBasePatientById(patientId);
            String exitChatListJson = patient.getDoctorExitChatListJson();
            JSONArray jsonArray = new JSONArray();
            if (StrUtil.isNotEmpty(exitChatListJson)) {
                jsonArray = JSONArray.parseArray(exitChatListJson);
            }
            if (exitChat.equals(1)) {
                jsonArray.add(doctorId.toString());
            }
            if (exitChat.equals(0)) {
                jsonArray.remove(doctorId.toString());
            }
            patient.setDoctorExitChatListJson(jsonArray.toJSONString());
            baseMapper.updateById(patient);
        }
        // 给患者这个群组相关的用户发送一条 打开聊天 或者 关闭聊天。
        if (doctorId == null) {
            return;
        }
        Chat chat = new Chat();
        chat.setType(exitChat.equals(1) ? MediaType.exitChat : MediaType.openChat);
        chat.setReceiverId(patientId.toString());
        chat.setSenderId(doctorId.toString());
        Doctor doctor = getDoctorService().getById(doctorId);
        if (Objects.isNull(doctor)) {
            return;
        }
        String assistant = dictionaryMap.get("doctor");
        StringBuilder content = new StringBuilder();
        content.append(doctor.getName());
        if (StrUtil.isEmpty(assistant)) {
            content.append("(医生)");
        } else {
            content.append("(").append(assistant).append(")");
        }
        if (exitChat.equals(1)) {
            content.append(" 退出聊天");
        } else {
            content.append(" 重新加入聊天");
        }
        chat.setContent(content.toString());
        getDoctorService().sendChatToWeiXin(chat);

    }


    @Override
    public List<String> getPatientImAccountByDoctorId(Long doctorId) {

        List<Patient> patientList = baseMapper.selectList(Wraps.<Patient>lbQ().eq(Patient::getDoctorId, doctorId).select(Patient::getImAccount, SuperEntity::getId));
        if (CollUtil.isEmpty(patientList)) {
            return new ArrayList<>();
        }
        return patientList.stream().map(Patient::getImAccount).collect(Collectors.toList());
    }
}
