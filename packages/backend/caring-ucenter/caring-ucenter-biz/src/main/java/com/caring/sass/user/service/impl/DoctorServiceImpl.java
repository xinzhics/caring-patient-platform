package com.caring.sass.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.authority.dto.auth.DoctorLoginByOpenId;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.*;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.redis.RedisMessageDoctorChangeNursing;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.dto.SendAssistanceNoticeDto;
import com.caring.sass.msgs.entity.*;
import com.caring.sass.msgs.enumeration.ChatGroupAssociationType;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.tenant.api.AppConfigApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.dao.*;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.*;
import com.caring.sass.user.service.redis.PatientMsgsNoReadCenter;
import com.caring.sass.user.util.I18nUtils;
import com.caring.sass.user.util.qrCode.QrCodeUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.config.Config;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.caring.sass.nursing.constant.FormResultExportConstant.doctor;

/**
 * <p>
 * 业务实现类
 * 医生表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Service
@Order(1)
public class DoctorServiceImpl extends SuperServiceImpl<DoctorMapper, Doctor> implements DoctorService {


    private final WeiXinApi weiXinApi;

    private final ImService imService;

    private final TenantApi tenantApi;

    private NursingStaffService nursingStaffService;

    private final DoctorGroupService doctorGroupService;

    private final DoctorCustomGroupMapper doctorCustomGroupMapper;

    private final DoctorCustomGroupPatientMapper doctorCustomGroupPatientMapper;

    private PatientService patientService;

    private final WeiXinService weiXinService;


    private final AppConfigApi appConfigApi;

    private final DictionaryItemService dictionaryItemService;

    private final RedisTemplate<String, String> redisTemplate;

    private final GroupMapper groupMapper;

    private final ConsultationGroupService consultationGroupService;

    private final OauthApi oauthApi;

    @Autowired
    FormApi formApi;

    @Autowired
    PatientMsgsNoReadCenter patientMsgsNoReadCenter;

    public DoctorServiceImpl(WeiXinApi weiXinApi,
                             ImService imService,
                             TenantApi tenantApi,
                             AppConfigApi appConfigApi,
                             DoctorGroupService doctorGroupService,
                             ConsultationGroupService consultationGroupService,
                             GroupMapper groupMapper,
                             DictionaryItemService dictionaryItemService,
                             RedisTemplate<String, String> redisTemplate,
                             OauthApi oauthApi,
                             DoctorCustomGroupPatientMapper doctorCustomGroupPatientMapper,
                             DoctorCustomGroupMapper doctorCustomGroupMapper,
                             WeiXinService weiXinService) {
        this.weiXinApi = weiXinApi;
        this.weiXinService = weiXinService;
        this.tenantApi = tenantApi;
        this.doctorGroupService = doctorGroupService;
        this.imService = imService;
        this.appConfigApi = appConfigApi;
        this.dictionaryItemService = dictionaryItemService;
        this.groupMapper = groupMapper;
        this.consultationGroupService = consultationGroupService;
        this.redisTemplate = redisTemplate;
        this.oauthApi = oauthApi;
        this.doctorCustomGroupPatientMapper = doctorCustomGroupPatientMapper;
        this.doctorCustomGroupMapper = doctorCustomGroupMapper;
    }

    public NursingStaffService getNursingStaffService() {
        if (nursingStaffService == null) {
            this.nursingStaffService = SpringUtils.getBean(NursingStaffService.class);;
        }
        return nursingStaffService;
    }

    public PatientService getPatientService() {
        if (patientService == null) {
            this.patientService = SpringUtils.getBean(PatientService.class);;
        }
        return patientService;
    }

    @Override
    public Doctor findByOpenId(String openId) {
        LbqWrapper<Doctor> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(Doctor::getOpenId, openId);
        // 防止openId被绑给了好几个医生
        List<Doctor> doctorList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(doctorList)) {
            return null;
        }
        return doctorList.get(0);
    }

    /**
     * @return boolean true 表示是个医生。不需要在执行患者关注
     * @Author yangShuai
     * @Description 检查当前用户是个 医生
     * @Date 2020/9/27 10:11
     */
    @Override
    public Doctor checkOpenId(WxSubscribeDto wxSubscribeDto) {
        Doctor doctor = findByOpenId(wxSubscribeDto.getOpenId());
        return doctor;
    }

    /**
     * @return com.caring.sass.user.entity.Doctor
     * @Author yangShuai
     * @Description 项目创建默认的医生
     * @Date 2020/9/27 10:18
     */
    @Override
    public Doctor createDoctor(Doctor doctor) {
        doctor.setAppointmentReview(DoctorAppointmentReviewEnum.need_review.toString());
        int insert = baseMapper.insert(doctor);
        if (insert > 0) {
            String account = imService.registerAccount(ImService.ImAccountKey.DOCTOR, doctor.getId());
            doctor.setImAccount(account);
            // 将系统医生的 im账号更新上去
            baseMapper.updateById(doctor);
        }
        // TODO 生成 微信的登录二维码。 患者注册二维码 系统默认医生暂时不登录
        return doctor;
    }

    /**
     * @return boolean
     * @Author yangShuai
     * @Description 创建一个医生  默认设置 医生的 im消息 接收状态为打开
     * @Date 2020/10/16 17:11
     */
    @Override
    public boolean save(Doctor doctor) {
        checkDoctorMobile(doctor);
        NursingStaff nursingStaff = getNursingStaffService().getById(doctor.getNursingId());
        if (Objects.nonNull(nursingStaff)) {
            doctor.setNursingName(nursingStaff.getName());
            doctor.setOrganCode(nursingStaff.getOrganCode());
            doctor.setClassCode(nursingStaff.getClassCode());
            doctor.setOrganId(nursingStaff.getOrganId());
            doctor.setOrganName(nursingStaff.getOrganName());
        }
        // 如果新增医生时，项目是个人服务号项目。则医生自动与医助下的小组进行关联。成为小组医生。
        String tenant = BaseContextHandler.getTenant();
        R<String> accountType = tenantApi.queryOfficialAccountType(tenant);
        String accountTypeData = accountType.getData();
        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(accountTypeData)) {
            Group group = groupMapper.selectOne(Wraps.<Group>lbQ().eq(Group::getNurseId, doctor.getNursingId()).last(" limit 0, 1"));
            if (group == null) {
                group = new Group();
                group.setNurseName(nursingStaff.getName());
                group.setClassCode(nursingStaff.getClassCode());
                group.setOrganId(nursingStaff.getOrganId());
                group.setOrganName(nursingStaff.getOrganName());
                group.setNurseId(doctor.getNursingId());
                group.setName(nursingStaff.getName()+ "医生小组");
                groupMapper.insert(group);
            }
            doctor.setGroupId(group.getId());

            // 如果医生是本项目 第一位非 默认医生，那么医生作为医助组长存在
            Integer count = baseMapper.selectCount(Wraps.<Doctor>lbQ().eq(Doctor::getDoctorLeader, 1).eq(Doctor::getBuildIn, 0));
            if (count == null || count == 0) {
                doctor.setDoctorLeader(1);
            } else {
                doctor.setDoctorLeader(0);
            }
            doctor.setIndependence(0);
        } else {
            Long groupId = doctor.getGroupId();
            if (groupId != null) {
                doctor.setIndependence(0);
            } else {
                doctor.setIndependence(1);
            }
        }
        doctor.setCloseAppoint(0);
        doctor.setImGroupStatus(1);
        doctor.setImMsgStatus(1);
        doctor.setImWxTemplateStatus(1);
        doctor.setHasGroup(0);
        if (StrUtil.isEmpty(doctor.getAvatar())) {
            doctor.setAvatar("https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/doctor_default_avator.png");
        }
        doctor.setAppointmentReview(DoctorAppointmentReviewEnum.need_review.toString());
        Long groupId = doctor.getGroupId();
        doctor.setGroupId(null);

        // 如果医生没有设置密码。就给初始化个默认的密码
        if (StrUtil.isBlank(doctor.getPassword())) {
            String passwordMd5 = SecureUtil.md5("doctor" + doctor.getMobile().substring(doctor.getMobile().length() - 4));
            doctor.setPassword(passwordMd5);
            doctor.setPasswordUpdated(CommonStatus.NO);
        } else {
            doctor.setPasswordUpdated(CommonStatus.YES);
        }

        int insert = baseMapper.insert(doctor);
        doctor.setCreateTime(LocalDateTime.now());
        if (insert < 0) {
            return false;
        }

        String account = imService.registerAccount(ImService.ImAccountKey.DOCTOR, doctor.getId());
        doctor.setImAccount(account);
        baseMapper.updateById(doctor);

        // 给医生和小组添加关联关系 并记录医生进入小组的时间
        if (null != groupId) {
            DoctorGroup doctorGroup = DoctorGroup.builder().doctorId(doctor.getId())
                    .groupId(groupId)
                    .joinGroupTime(LocalDateTime.now())
                    .build();
            doctorGroupService.save(doctorGroup);
        }

        SaasGlobalThreadPool.execute(() -> syncSaveDoctorInfo(doctor, tenant));

        return true;
    }

    /**
     * 异步处理医生的其他信息
     * @param doctor
     * @param tenantCode
     */
    public void syncSaveDoctorInfo(Doctor doctor, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> apiTenant = tenantApi.getByCode(tenantCode);
        if (apiTenant.getIsSuccess()) {
            Tenant tenantData = apiTenant.getData();
            if (Objects.isNull(tenantData)) {
                log.error("未查询到医生所在的项目，无法创建医生使用的二维码");
                return;
            }
            if (tenantData.isCertificationServiceNumber()) {
                QrCodeDto qrCodeDto = generatePatientFocusQrCode(tenantData.getWxAppId(), doctor.getId());
                QrCodeDto englishQrCodeDto = englishGeneratePatientFocusQrCode(tenantData.getWxAppId(), doctor.getId());
                doctor.setQrCode(qrCodeDto.getUrl());
                doctor.setEnglishQrCode(englishQrCodeDto.getUrl());
                try {
                    doctor.setBusinessCardQrCode(QrCodeUtils.patientSubscribeCode(qrCodeDto.getUrl(), doctor.getAvatar(), doctor.getName(), tenantData.getName(), doctor.getHospitalName(), tenantData.getLogo()));
                } catch (Exception e) {
                    log.error("生成医生二维码失败", e);
                }
                try {
                    doctor.setDownLoadQrcode(QrCodeUtils.patientSubscribeCode_2(qrCodeDto.getUrl(), doctor.getAvatar(), doctor.getName(), tenantData.getName(), doctor.getHospitalName(), doctor.getTitle()));
                } catch (Exception e) {
                    log.error("生成医生下载二维码失败", e);
                }
                try {
                    doctor.setEnglishBusinessCardQrCode(QrCodeUtils.patientSubscribeCode_3(englishQrCodeDto.getUrl(), doctor.getAvatar(), doctor.getName(), tenantData.getName(), doctor.getHospitalName(), doctor.getTitle()));
                } catch (Exception e) {
                    log.error("生成医生下载二维码失败", e);
                }

                UpdateWrapper<Doctor> wrapper = new UpdateWrapper<>();
                wrapper.eq("id", doctor.getId());
                wrapper.set("qr_code", qrCodeDto.getUrl());
                wrapper.set("english_qr_code", englishQrCodeDto.getUrl());
                wrapper.set("business_card_qr_code", doctor.getBusinessCardQrCode());
                wrapper.set("down_load_qr_code", doctor.getDownLoadQrcode());
                wrapper.set("english_business_card_qr_code", doctor.getEnglishBusinessCardQrCode());
                baseMapper.update(new Doctor(), wrapper);
            } else if (tenantData.isPersonalServiceNumber()) {
                Config config = new Config();
                config.setAppId(tenantData.getWxAppId());
                R<Config> xinApiConfig = weiXinApi.getConfig(config);
                if (xinApiConfig.getIsSuccess()) {
                    Config configData = xinApiConfig.getData();
                    if (Objects.nonNull(configData)) {
                        String qrCodeUrl = configData.getWxQrCodeUrl();
                        doctor.setQrCode(qrCodeUrl);
                        doctor.setEnglishQrCode(qrCodeUrl);
                        try {
                            doctor.setBusinessCardQrCode(QrCodeUtils.patientSubscribeCode(qrCodeUrl, doctor.getAvatar(), doctor.getName(), tenantData.getName(), doctor.getHospitalName(), tenantData.getLogo()));
                        } catch (Exception e) {
                            log.error("生成医生二维码失败", e);
                        }
                        try {
                            doctor.setDownLoadQrcode(QrCodeUtils.patientSubscribeCode_2(qrCodeUrl, doctor.getAvatar(), doctor.getName(), tenantData.getName(), doctor.getHospitalName(), doctor.getTitle()));
                        } catch (Exception e) {
                            log.error("生成医生下载二维码失败", e);
                        }
                        try {
                            doctor.setEnglishBusinessCardQrCode(QrCodeUtils.patientSubscribeCode_3(qrCodeUrl, doctor.getAvatar(), doctor.getName(), tenantData.getName(), doctor.getHospitalName(), doctor.getTitle()));
                        } catch (Exception e) {
                            log.error("生成医生下载二维码失败", e);
                        }
                        UpdateWrapper<Doctor> wrapper = new UpdateWrapper<>();
                        wrapper.eq("id", doctor.getId());
                        wrapper.set("qr_code", qrCodeUrl);
                        wrapper.set("english_qr_code", qrCodeUrl);
                        wrapper.set("business_card_qr_code", doctor.getBusinessCardQrCode());
                        wrapper.set("down_load_qr_code", doctor.getDownLoadQrcode());
                        wrapper.set("english_business_card_qr_code", doctor.getEnglishBusinessCardQrCode());
                        baseMapper.update(new Doctor(), wrapper);
                    }
                }
            }
        }
    }


    /**
     * @return com.caring.sass.wx.dto.config.QrCodeDto
     * @Author yangShuai
     * @Description 生成微信二维码
     * @Date 2020/10/12 16:16
     */
    @Override
    public QrCodeDto generatePatientFocusQrCode(String wxAppId, Long doctorId) {
        CreateFollowerPermanentQrCode form = new CreateFollowerPermanentQrCode();
        form.setWxAppId(wxAppId);
        form.setParams("doctor_personal_" + doctorId);
        R<QrCodeDto> qrCode = weiXinApi.createFollowerPermanentQrCode(form);
        if (qrCode.getIsSuccess()) {
            return qrCode.getData();
        }
        return null;
    }

    @Override
    public QrCodeDto englishGeneratePatientFocusQrCode(String wxAppId, Long doctorId) {
        CreateFollowerPermanentQrCode form = new CreateFollowerPermanentQrCode();
        form.setWxAppId(wxAppId);
        form.setParams("doctor_personal_english_" + doctorId);
        R<QrCodeDto> qrCode = weiXinApi.createFollowerPermanentQrCode(form);
        if (qrCode.getIsSuccess()) {
            return qrCode.getData();
        }
        return null;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 检查手机号是否重复
     * @Date 2020/10/12 15:56
     */
    public void checkDoctorMobile(Doctor doctor) {
        String mobile = StringUtils.trim(doctor.getMobile());
        if (StringUtils.isEmpty(mobile)) {
            throw new BizException("手机号不能为空");
        } else {
            Doctor model = new Doctor();
            model.setMobile(mobile);
            Doctor one = baseMapper.selectOne(Wraps.q(model));
            if (Objects.nonNull(one) && !one.getId().equals(doctor.getId())) {
                throw new BizException("手机号已经被占用");
            }
        }
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 医生关注。 给生成 医生绑定 手机号连接。
     * @Date 2020/9/27 11:24
     */
    @Override
    public void subscribe(String wxAppId, String openId, Doctor doctor, Locale locale) {
        WxMpKefuMessage.WxArticle article1 = new WxMpKefuMessage.WxArticle();
        if (Objects.isNull(doctor)) {
            doctor = findByOpenId(openId);
        }
        if (Objects.nonNull(doctor)) {
            doctor.setWxStatus(1);
            updateById(doctor);
        }
        R<Tenant> tenant = tenantApi.getTenantByWxAppId(wxAppId);
        if (tenant.getIsSuccess()) {
            Tenant tenantData = tenant.getData();
            bindUserTags(wxAppId, openId);

            GeneralForm generalForm = new GeneralForm();
            generalForm.setWxAppId(wxAppId);
            String url = ApplicationDomainUtil.wxDoctorBaseDomain(tenantData.getDomainName(), Objects.nonNull(tenantData.getWxBindTime()));

            article1.setUrl(url);
            article1.setPicUrl(tenantData.getLogo());
            article1.setDescription(I18nUtils.getMessage("CLICK_REGISTER_AND_LOG_IN", locale));
            article1.setTitle(I18nUtils.getMessage("REGISTRATION_LOGIN", locale, getDoctorDict()));
            WxMpKefuMessage wxMpKefuMessage = (WxMpKefuMessage.NEWS().toUser(openId)).addArticle(article1).build();

            SendKefuMsgForm form = new SendKefuMsgForm();
            form.setWxAppId(wxAppId);
            form.setMessage(wxMpKefuMessage);
            weiXinApi.sendKefuMsg(form);
        }

    }

    /**
     * 绑定微信的角色标签
     * @param wxAppId
     * @param openId
     */
    @Override
    public void bindUserTags(String wxAppId, String openId) {
        BindUserTagsForm bindUserTagsForm = new BindUserTagsForm();
        bindUserTagsForm.setWxAppId(wxAppId);
        bindUserTagsForm.setOpenIds(openId);
        bindUserTagsForm.setTagId(TagsEnum.DOCTOR_TAGS.getValue());
        List<String> stringList = new ArrayList<>();
        stringList.add(TagsEnum.PATIENT_TAG.getValue());
        stringList.add(TagsEnum.TOURISTS_TAG.getValue());
        bindUserTagsForm.setClearTagId(stringList);
        weiXinApi.bindUserTags(bindUserTagsForm);

    }


    /**
     * 医生的字典翻译
     * @return
     */
    private String getDoctorDict() {
        String doctor = dictionaryItemService.findDictionaryItemName(DictionaryItemService.DOCTOR);
        if (StrUtil.isNotEmpty(doctor)) {
            return doctor;
        } else {
            return "医生";
        }
    }

    @Override
    public void subscribeChangeDoctor(String wxAppId, String openId, Locale locale) {
        WxMpKefuMessage.WxArticle article1 = new WxMpKefuMessage.WxArticle();
        R<Tenant> tenant = tenantApi.getTenantByWxAppId(wxAppId);
        if (tenant.getIsSuccess()) {
            Tenant tenantData = tenant.getData();

            GeneralForm generalForm = new GeneralForm();
            generalForm.setWxAppId(wxAppId);
            String url = ApplicationDomainUtil.wxDoctorBaseDomain(tenantData.getDomainName(), Objects.nonNull(tenantData.getWxBindTime()));
            log.info("subscribeChangeDoctor 推送链接—— {}", url);
            article1.setUrl(url);
            article1.setPicUrl(tenantData.getLogo());
            article1.setDescription(I18nUtils.getMessage("CLICK_REGISTER_AND_LOG_IN", locale));
            article1.setTitle(I18nUtils.getMessage("REGISTRATION_LOGIN", locale, getDoctorDict()));
            WxMpKefuMessage wxMpKefuMessage = (WxMpKefuMessage.NEWS().toUser(openId)).addArticle(article1).build();

            SendKefuMsgForm form = new SendKefuMsgForm();
            form.setWxAppId(wxAppId);
            form.setMessage(wxMpKefuMessage);
            weiXinApi.sendKefuMsg(form);
        }
    }


    @Override
    public Doctor getById(Serializable id) {
        Doctor doctor = baseMapper.selectById(id);
        if (doctor == null) {
            return null;
        }
        Long doctorId = doctor.getId();
        int counts = getPatientService().count(Wraps.<Patient>lbQ().eq(Patient::getDoctorId, doctorId));
        doctor.setTotalPatientCount((long) counts);
        doctor.setGroupId(null);

        String userType = BaseContextHandler.getUserType();
        if (UserType.ORGAN_ADMIN.equals(userType) || UserType.ADMIN_OPERATION.equals(userType)) {
            List<Doctor> doctors = new ArrayList<Doctor>();
            doctors.add(doctor);
            desensitization(doctors);
        }

        return doctor;
    }

    @Override
    public Doctor findByLoginName(String loginName) {
        LbqWrapper<Doctor> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(Doctor::getLoginName, loginName);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        for (Serializable id : idList) {
            removeById(id);
        }
        return true;
    }

    /**
     * 删除医生和医生下的患者
     * @param id
     */
    @Override
    public void doctorAndPatient(Long id) {

        getPatientService().deletePatient(id);

        Doctor doctor = baseMapper.selectById(id);
        if (Objects.nonNull(doctor)) {
            if (StringUtils.isNotEmpty(doctor.getImAccount())) {
                imService.removeAccount(doctor.getImAccount());
            }
            baseMapper.deleteById(id);
        }
        doctorGroupService.remove(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getDoctorId, id));
        if (StrUtil.isNotEmpty(doctor.getOpenId())) {
            weiXinApi.deleteWeiXinUserInfo(doctor.getOpenId());
        }
    };

    @Override
    public void desensitization(List<Doctor> records) {

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

        for (Doctor record : records) {
            // 对 换着的 姓名。 联系方式 脱敏
            record.setName(SensitiveInfoUtils.desensitizeName(record.getName()));
            record.setMobile(SensitiveInfoUtils.desensitizePhone(record.getMobile()));
            record.setNursingName(SensitiveInfoUtils.desensitizeName(record.getNursingName()));
        }


    }

    /**
     * 更新医生为组长，并设置其他医生为普通医生
     * @param doctorId
     */
    @Transactional
    @Override
    public Doctor updateDoctorLeader(Long doctorId) {

        Doctor doctor = new Doctor();
        UpdateWrapper<Doctor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("doctor_leader", 0);
        updateWrapper.eq("doctor_leader", 1);
        updateWrapper.eq("tenant_code", BaseContextHandler.getTenant());
        baseMapper.update(doctor, updateWrapper);


        doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setDoctorLeader(1);
        baseMapper.updateById(doctor);
        return doctor;

    }

    @Override
    public boolean removeById(Serializable id) {
        long status = getPatientService().countPatientNoStatus((Long) id, UserType.UCENTER_DOCTOR);
        if (status > 0) {
            throw new BizException("医生下还有患者，不可删除");
        }
        Doctor doctor = baseMapper.selectById(id);
        if (Objects.nonNull(doctor)) {
            if (doctor.getDoctorLeader() != null && doctor.getDoctorLeader().equals(1)) {
                throw new BizException("请先将设置其他医生为医生组长后，再进行删除");
            }
            if (StringUtils.isNotEmpty(doctor.getImAccount())) {
                imService.removeAccount(doctor.getImAccount());
            }
            baseMapper.deleteById(id);
        }
        doctorGroupService.remove(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getDoctorId, id));
        if (StrUtil.isNotEmpty(doctor.getOpenId())) {
            weiXinApi.deleteWeiXinUserInfo(doctor.getOpenId());
        }
        return true;
    }

    /**
     * @return java.lang.Object
     * @Author yangShuai
     * @Description 获取医生的 小组内排名
     * @Date 2020/10/12 17:07
     */
    @Override
    public Integer getDoctorRanking(Long groupId, Long doctorId) {
        List<DoctorRanking> doctorRanking = baseMapper.countDoctorRanking(groupId);
        if (CollectionUtils.isEmpty(doctorRanking)) {
            return null;
        }
        int count = 0;
        for (DoctorRanking ranking : doctorRanking) {
            ++count;
            if (ranking.getDoctorId().equals(doctorId)) {
                return count;
            }
        }
        return count;
    }


    @Override
    public long countDoctorByGroupId(Long groupId) {
        return doctorGroupService.count(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getGroupId, groupId));
    }

    @Override
    public int countDoctorByNursing(Long nursingId) {
        LbqWrapper<Doctor> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Doctor::getNursingId, nursingId);
        return baseMapper.selectCount(lbqWrapper);
    }

    @Override
    public Doctor getByMobile(String phone) {
        Doctor model = new Doctor();
        model.setMobile(phone);
        QueryWrap<Doctor> wrap = Wraps.q(model);
        return baseMapper.selectOne(wrap);
    }


    /**
     * @return com.caring.sass.user.entity.Doctor
     * @Author yangShuai
     * @Description 返回一个医生。只有医生的基本信息
     * @Date 2020/11/12 11:15
     */
    @Override
    public Doctor getBaseDoctorAndImOpenById(Long id) {
        return baseMapper.selectOne(Wraps.<Doctor>lbQ().select(Doctor::getId, Doctor::getName,
                Doctor::getNursingId, Doctor::getAvatar, Doctor::getMobile,
                Doctor::getNickName, Doctor::getOpenId, Doctor::getImAccount, Doctor::getWxStatus,
                Doctor::getImWxTemplateStatus, Doctor::getImMsgStatus, Doctor::getBuildIn,Doctor::getTitle,
                        Doctor::getRegistrationInformation, Doctor::getHospitalName, Doctor::getDeptartmentName,
                        Doctor::getAiHosted, Doctor::getLoginStatus, Doctor::getExtraInfo, Doctor::getIndependence)
                .eq(Doctor::getId, id));
    }


    /**
     * 医生身份群发消息
     * 暂时没用上
     *
     * @param chatGroupSend
     * @return
     */
    @Override
    public Long doctorSendGroupMsg(ChatGroupSend chatGroupSend) {

        String senderId = chatGroupSend.getSenderId();
        Doctor doctor = baseMapper.selectById(senderId);
        if (Objects.isNull(doctor)) {
            return 0l;
        }
        NursingStaff nursingStaff = getNursingStaffService().getById(doctor.getNursingId());
        // 整理都有哪些患者需要发送信息
        Chat chat = new Chat();
        chat.setContent(chatGroupSend.getContent());
        chat.setType(chatGroupSend.getType());
        chat.setSenderAvatar(doctor.getAvatar());
        chat.setSenderId(doctor.getId().toString());
        chat.setSenderImAccount(doctor.getImAccount());
        chat.setSenderName(doctor.getName());
        chat.setCreateTime(LocalDateTime.now());

        Set<Long> patientIds = new HashSet<>();
        List<ChatGroupSendObject> sendObjects = chatGroupSend.getSendObjects();
        if (CollUtil.isNotEmpty(sendObjects)) {
            Map<ChatGroupAssociationType, List<ChatGroupSendObject>> collect = sendObjects.stream().collect(Collectors.groupingBy(ChatGroupSendObject::getObjectAssociationType));
            List<ChatGroupSendObject> all = collect.get(ChatGroupAssociationType.ALL);
            if (CollUtil.isNotEmpty(all)) {
                List<Patient> patientList = getPatientService().list(Wraps.<Patient>lbQ()
                        .select(SuperEntity::getId, Patient::getName).eq(Patient::getDoctorId, senderId));
                if (CollUtil.isNotEmpty(patientList)) {
                    patientList.forEach(item -> patientIds.add(item.getId()));
                }
            }

            List<ChatGroupSendObject> diseaseObjList = collect.get(ChatGroupAssociationType.DISEASE);
            if (CollUtil.isNotEmpty(diseaseObjList)) {
                List<String> diseaseIds = diseaseObjList.stream().map(ChatGroupSendObject::getObjectAssociationId).collect(Collectors.toList());
                // 查询诊断类型下的患者ID
                List<Patient> patientList = getPatientService().list(Wraps.<Patient>lbQ()
                        .select(SuperEntity::getId, Patient::getName)
                        .in(Patient::getDiagnosisId, diseaseIds));
                if (CollUtil.isNotEmpty(patientList)) {
                    patientList.forEach(item -> patientIds.add(item.getId()));
                }
            }

            List<ChatGroupSendObject> customGroupList = collect.get(ChatGroupAssociationType.CUSTOM_GROUP);
            if (CollUtil.isNotEmpty(customGroupList)) {
                List<Long> customGroupIds = new ArrayList<>();
                customGroupList.forEach(item -> customGroupIds.add(Long.parseLong(item.getObjectAssociationId())));
                // 查询小组内的患者ID
                List<DoctorCustomGroupPatient> customGroupPatients = doctorCustomGroupPatientMapper.selectList(Wraps.<DoctorCustomGroupPatient>lbQ()
                        .select(DoctorCustomGroupPatient::getPatientId)
                        .in(DoctorCustomGroupPatient::getDoctorCustomGroupId, customGroupIds));
                if (CollUtil.isNotEmpty(customGroupPatients)) {
                    customGroupPatients.forEach(item -> patientIds.add(item.getPatientId()));
                }
            }
            List<ChatGroupSendObject> patientObjectList = collect.get(ChatGroupAssociationType.PATIENT);
            if (CollUtil.isNotEmpty(patientObjectList)) {
                patientObjectList.forEach(item -> patientIds.add(Long.parseLong(item.getObjectAssociationId())));
            }
        }

        if (CollUtil.isEmpty(patientIds)) {
            return 0L;
        }
        List<Doctor> tempDoctor = new ArrayList<>();
        if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {
            // 目前的这个医生可能不是 患者自己的医生
            tempDoctor = doctorGroupService.getReadMyMsgDoctor(doctor.getId());
        }
        final List<Doctor> doctorList = tempDoctor;
        String tenant = BaseContextHandler.getTenant();
        Boolean weiXinTemplate = weiXinService.noSendWeiXinTemplate(tenant, PlanFunctionTypeEnum.ONLINE_CONSULTATION);
        R<String> accountType = tenantApi.queryOfficialAccountType(tenant);
        String accountTypeData = accountType.getData();
        List<Long> patientListIds = new ArrayList<>(patientIds);
        List<List<Long>> listList = ListUtils.subList(patientListIds, 100);
        for (List<Long> longList : listList) {
            SaasGlobalThreadPool.execute(() -> sendGroupChat(longList, doctor, nursingStaff, chat, tenant, doctorList, weiXinTemplate, accountTypeData));
        }
        if (StrUtil.isNotEmpty(nursingStaff.getImAccount())) {
            SendAssistanceNoticeDto noticeDto = SendAssistanceNoticeDto.builder()
                    .assistanceImAccount(nursingStaff.getImAccount())
                    .msgContent(I18nUtils.getMessage("GROUP_MESSAGE", doctor.getName()))
                    .msgType(SendAssistanceNoticeDto.AssistanceNoticeMsgType.DOCTOR_CHAT_GROUP.toString())
                    .build();
            imService.sendAssistanceNotice(noticeDto);
        }
        int size = patientIds.size();
        return Long.valueOf(size);
    }

    /**
     * 分组群发医生的消息
     * @param longList
     * @param doctor
     * @param nursingStaff
     * @param chat
     * @param tenantCode
     * @param doctorList
     * @param weiXinTemplate
     */
    @Deprecated
    public void sendGroupChat(List<Long> longList, Doctor doctor, NursingStaff nursingStaff, Chat chat, String tenantCode, List<Doctor> doctorList, Boolean weiXinTemplate, String accountTypeData) {

        BaseContextHandler.setTenant(tenantCode);
        if (CollUtil.isEmpty(longList)) {
            return;
        }
        List<Patient> patients = getPatientService().list(Wraps.<Patient>lbQ().select().in(SuperEntity::getId, longList));
        if (CollUtil.isEmpty(patients)) {
            return;
        }
        for (Patient patient : patients) {
            sendChat(patient, chat, nursingStaff, doctor, weiXinTemplate, doctorList, tenantCode, accountTypeData);
        }

    }

    @Override
    public void aiSendChatToWeiXin(Chat chat, List<Doctor> doctors, String accountTypeData) {

        String receiverId = chat.getReceiverId();
        Doctor doctor = baseMapper.selectById(Long.parseLong(chat.getSenderId()));
        Patient receiver = getPatientService().getById(Long.parseLong(receiverId));

        List<ChatSendRead> chatSendReads;
        ChatSendRead send;
        chatSendReads = new ArrayList<>(5);
        chat.setHistoryVisible(1);
        imService.setSenderMessage(chat, doctor);
        imService.setOtherMessage(chat, receiver);

        chatSendReads.add(imService.createDoctorSendRead(accountTypeData, receiver.getImAccount(), doctor, receiver.getDoctorExitChatListJson()));
        NursingStaff nursingStaff = getNursingStaffService().getById(receiver.getServiceAdvisorId());
        // 添加 医助接收人 单不需要给医助增加未读记录
        if (nursingStaff != null) {
            send = imService.createNursingStaffSendRead(receiver.getImAccount(), nursingStaff, receiver.getNursingExitChat());
            send.setNoCreateReadLog(true);
            chatSendReads.add(send);
        }
        // 医生这里 不要给自己发消息
        // 这个医生不是一个独立医生。
        if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {
            // 目前的这个医生可能不是 患者自己的医生
            // 添加 患者医生所在小组 愿意接收消息的 其他医生
            if (doctors == null) {
                doctors = doctorGroupService.getReadMyMsgDoctor(doctor.getId());
            }
            chatSendReads.addAll(imService.createDoctorSendRead(accountTypeData, receiver.getImAccount(), doctors, doctor.getId(), receiver.getDoctorExitChatListJson()));
        }
        // 针对场景。
        // 当A医生给B医生的患者发送消息。 B医生设置不看A医生的患者
        // 此时 doctorList 中的医生 是设置了 接收A医生消息的人。 B则被忽略
        // 由于B 是患者医生。所以B 必须能查看 自己患者收到的消息。
        if (!Objects.equals(doctor.getId(), receiver.getDoctorId())) {
            Doctor patientDoctor = getBaseDoctorAndImOpenById(receiver.getDoctorId());
            if (Objects.nonNull(patientDoctor)) {
                // 如果患者的医生。已经被添加到 消息未读接收人中，则放弃添加
                boolean addPatientDoctor = true;
                for (ChatSendRead sendRead : chatSendReads) {
                    if (sendRead.getUserId().equals(patientDoctor.getId())) {
                        addPatientDoctor = false;
                        break;
                    }
                }
                if (addPatientDoctor) {
                    chatSendReads.add(imService.createDoctorSendRead(accountTypeData, receiver.getImAccount(), patientDoctor, receiver.getDoctorExitChatListJson()));
                }
            }
        }

        // 添加患者接收人
        send = imService.createPatientSendRead(receiver.getImAccount(), receiver);

        chatSendReads.add(send);
        chat.setSendReads(chatSendReads);
        imService.sendChat(chat);


    }

    /**
     * 医生发送一条环信消息
     * @param receiver
     * @param chat
     * @param nursingStaff
     * @param doctor
     * @param weiXinTemplate
     * @param tenantCode
     */
    public Chat sendChat(Patient receiver, Chat chat, NursingStaff nursingStaff, Doctor doctor, Boolean weiXinTemplate, List<Doctor> doctorList, String tenantCode, String accountTypeData) {
        List<ChatSendRead> chatSendReads;
        ChatSendRead send;
        chatSendReads = new ArrayList<>(5);
        chat.setHistoryVisible(1);
        imService.setOtherMessage(chat, receiver);

        // 添加 医助接收人
        if (nursingStaff != null) {
            send = imService.createNursingStaffSendRead(receiver.getImAccount(), nursingStaff, receiver.getNursingExitChat());
            chatSendReads.add(send);
        }
        // 医生这里 不要给自己发消息
        // 这个医生不是一个独立医生。
        if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {
            // 目前的这个医生可能不是 患者自己的医生
            // 添加 患者医生所在小组 愿意接收消息的 其他医生
            chatSendReads.addAll(imService.createDoctorSendRead(accountTypeData, receiver.getImAccount(), doctorList, doctor.getId(), receiver.getDoctorExitChatListJson()));
        }
        // 针对场景。
        // 当A医生给B医生的患者发送消息。 B医生设置不看A医生的患者
        // 此时 doctorList 中的医生 是设置了 接收A医生消息的人。 B则被忽略
        // 由于B 是患者医生。所以B 必须能查看 自己患者收到的消息。
        if (!Objects.equals(doctor.getId(), receiver.getDoctorId())) {
            Doctor patientDoctor = getBaseDoctorAndImOpenById(receiver.getDoctorId());
            if (Objects.nonNull(patientDoctor)) {
                // 如果患者的医生。已经被添加到 消息未读接收人中，则放弃添加
                boolean addPatientDoctor = true;
                for (ChatSendRead sendRead : chatSendReads) {
                    if (sendRead.getUserId().equals(patientDoctor.getId())) {
                        addPatientDoctor = false;
                        break;
                    }
                }
                if (addPatientDoctor) {
                    chatSendReads.add(imService.createDoctorSendRead(accountTypeData, receiver.getImAccount(), patientDoctor, receiver.getDoctorExitChatListJson()));
                }
            }
        }

        // 添加患者接收人
        send = imService.createPatientSendRead(receiver.getImAccount(), receiver);

        chatSendReads.add(send);
        chat.setSendReads(chatSendReads);
        List<ChatAtRecord> chatAtRecords = chat.getChatAtRecords();
        chat = imService.sendChat(chat);

        // 开启聊天和退出聊天的通知不需要模板消息
        if (chat.getType().equals(MediaType.exitChat) || chat.getType().equals(MediaType.openChat)) {
            return chat;
        }
        if (weiXinTemplate) {
            return chat;
        }
        String userRole = dictionaryItemService.findDictionaryItemName(DictionaryItemService.DOCTOR);
        boolean cancel = false;
        boolean cancelNursing = false;
        if (CollUtil.isNotEmpty(chatAtRecords)) {
            for (ChatAtRecord record : chatAtRecords) {
                if (receiver.getId().equals(record.getAtUserId())) {
                    getPatientService().sendAtImWeiXinTemplateMessage(tenantCode, record.getAtUserId(), doctor.getName(), UserType.UCENTER_DOCTOR);
                    cancel = true;
                } else if (UserType.UCENTER_DOCTOR.equals(record.getUserType())) {
                    // 医生如果设置了 拒绝这个患者聊天 则不发送模版消息
                    if (receiver.getDoctorExitChatListJson().isEmpty() || !receiver.getDoctorExitChatListJson().contains(record.getAtUserId().toString())) {
                        sendAtImWeiXinTemplateMessage(tenantCode, record.getAtUserId(), doctor.getName(), receiver.getImAccount(), receiver.getId(), receiver.getName());
                    }
                } else if (UserType.UCENTER_NURSING_STAFF.equals(record.getUserType())) {
                    cancelNursing = true;
                    if (receiver.getNursingExitChat() == null || receiver.getNursingExitChat() == 0) {
                        getNursingStaffService().sendAtImWeiXinTemplateMessage(tenantCode, record.getAtUserId(), doctor.getName(), receiver.getImAccount(), receiver.getId(), receiver.getName());
                    }
                }
            }
        }
        if (!cancel) {
            getPatientService().sendWeiXinTemplateMessage(tenantCode, receiver.getStatus(), receiver.getImAccount(),
                    receiver.getOpenId(), chat.getType(), chat.getContent(), receiver.getId(), receiver.getName(), doctor.getName(), userRole);
        }
        if (!cancelNursing) {
            // 给医助发送微信模版消息
            if (receiver.getNursingExitChat() == null || receiver.getNursingExitChat() == 0) {
                getNursingStaffService().sendWeixinTemplateMessage(tenantCode, doctor.getNursingId(),
                        chat.getType(), chat.getContent(), receiver.getId(), receiver.getName(), doctor.getName());
            }
        }
        return chat;
    }


    /**
     * 医生给 大量患者批量发送 环信和 公众号消息。
     * @param patients 患者
     * @param patientChat 患者端打开的路径
     * @param tenantCode 租户
     * @param templateMsgDto 本次群发使用的模版信息
     * @param appId 项目微信公众号的appId
     * @param content 消息的内容
     * @param chatType 消息的类型
     * @param noWeiXinTemplate 是否取消发送微信公众号信息
     * @param doctor 发送人
     */
    public void sendMoreChatToWeiXin(List<Patient> patients, String patientChat, String tenantCode,
                                     TemplateMsgDto templateMsgDto, Tenant tenant, Doctor doctor, String appId,
                                     String content, MediaType chatType, Boolean noWeiXinTemplate, String userRole) {
        if (CollUtil.isEmpty(patients)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        String defaultLanguage = tenant.getDefaultLanguage();
        boolean certificationServiceNumber = tenant.isCertificationServiceNumber();
        patients.forEach(receiver -> {
            List<ChatSendRead> chatSendReads = new ArrayList<>(10);
            Chat chat = new Chat();
            chat.setContent(content);
            chat.setType(chatType);
            chat.setSenderId(doctor.getId().toString());
            chat.setHistoryVisible(CommonStatus.YES);
            chat.setGroupMessage(CommonStatus.YES);
            // 设置医生作为发送人的信息
            imService.setSenderMessage(chat, doctor);
            // 设置患者的其他信息
            imService.setOtherMessage(chat, receiver);
            chat.setSystemMessage(CommonStatus.YES);
            // 生成患者和医助之间的消息未读记录
            // 添加患者接收人
            ChatSendRead patientSendRead = imService.createPatientSendRead(receiver.getImAccount(), receiver);
            chatSendReads.add(patientSendRead);
            chat.setSendReads(chatSendReads);
            this.imService.sendChat(chat);
            if (!noWeiXinTemplate) {
                if (certificationServiceNumber) {
                    if (Objects.nonNull(templateMsgDto)) {
                        weiXinService.sendConsultationResponseMore(appId, receiver.getOpenId(), chatType, content, receiver.getName(), patientChat, receiver.getId(), templateMsgDto, defaultLanguage);
                    }
                } else {
                    patientMsgsNoReadCenter.savePatientMsgsNoRead(receiver.getId(), tenantCode, tenant.getDomainName(),
                            tenant.getName(), doctor.getName(), userRole);
                }
            }
        });

    }


    /**
     * 医生群发消息
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
        Doctor doctor = baseMapper.selectById(chat.getSenderId());
        if (Objects.isNull(doctor)) {
            throw new BizException("发送人不存在");
        }
        if (org.springframework.util.StringUtils.isEmpty(doctor.getImAccount())) {
            throw new BizException("发送人Im账号不存在");
        }

        // 创建一条群发记录
        ChatGroupSend chatGroupSend = new ChatGroupSend();
        String content = chat.getContent();
        chatGroupSend.setContent(content);
        chatGroupSend.setReceiverIds(receiverIds);
        MediaType chatType = chat.getType();
        chatGroupSend.setType(chatType);
        chatGroupSend.setSenderAvatar(doctor.getAvatar());
        chatGroupSend.setSenderId(doctor.getId().toString());
        chatGroupSend.setSenderImAccount(doctor.getImAccount());
        chatGroupSend.setSenderName(doctor.getName());
        chatGroupSend.setCreateTime(LocalDateTime.now());
        imService.createGroupChat(chatGroupSend);

        String tenantCode = BaseContextHandler.getTenant();
        BaseContextHandler.setTenant(tenantCode);
        TenantApi tenantApi = SpringUtils.getBean(TenantApi.class);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            log.error("sendMoreChatToWeiXin no has tenant");
            return;
        }
        // 查询本次群发使用的模版消息
        TemplateMsgDto templateMsgDto = weiXinService.getCurrentSendUseTemplate(tenant.isCertificationServiceNumber(), TemplateMessageIndefiner.CONSULTATION_RESPONSE);
        Boolean noWeiXinTemplate = weiXinService.noSendWeiXinTemplate(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.ONLINE_CONSULTATION);
        String patientChat = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CHAT);
        Set<Long> patientIdsSet = new HashSet<>();
        for (String s : p) {
            patientIdsSet.add(Long.parseLong(s));
        }
        List<Long> patientAllList = ListUtil.toList(patientIdsSet);
        List<List<Long>> listList = ListUtils.subList(patientAllList, 30);

        String userRole = dictionaryItemService.findDictionaryItemName(DictionaryItemService.DOCTOR);
        NursingStaff nursingStaff = getNursingStaffService().getBaseNursingStaffById(doctor.getNursingId());
        for (List<Long> patientList : listList) {
            // 查询30个患者的相关数据
            List<Patient> patients = getPatientService().list(Wraps.<Patient>lbQ().in(SuperEntity::getId, patientList));
            SaasGlobalThreadPool.execute(() -> sendMoreChatToWeiXin(patients, patientChat, tenantCode, templateMsgDto, tenant,
                    doctor, tenant.getWxAppId(), content, chatType, noWeiXinTemplate, userRole));
        }
        if (StrUtil.isNotEmpty(nursingStaff.getImAccount())) {
            SendAssistanceNoticeDto noticeDto = SendAssistanceNoticeDto.builder()
                    .assistanceImAccount(nursingStaff.getImAccount())
                    .msgContent(String.format("通知: %s群发了一条消息", doctor.getName()))
                    .msgType(SendAssistanceNoticeDto.AssistanceNoticeMsgType.DOCTOR_CHAT_GROUP.toString())
                    .build();
            imService.sendAssistanceNotice(noticeDto);
        }
    }

    /**
     * @Author yangShuai
     * @Description 医生发送 一条 消息到患者。
     * @Date 2020/11/12 14:23
     */
    @Override
    public Chat sendChatToWeiXin(Chat chat) {
        if (org.springframework.util.StringUtils.isEmpty(chat.getSenderId())) {
            throw new BizException("发送人Id不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(chat.getReceiverId())) {
            throw new BizException("接收人Id不能为空");
        }
        String receiverIds = chat.getReceiverId();
        Doctor doctor = getBaseDoctorAndImOpenById(Long.parseLong(chat.getSenderId()));
        if (Objects.isNull(doctor)) {
            throw new BizException("发送人不存在");
        }
        if (org.springframework.util.StringUtils.isEmpty(doctor.getImAccount())) {
            throw new BizException("发送人Im账号不存在");
        }
        String[] p = receiverIds.split(",");
        String s = p[0];

        String tenantCode = BaseContextHandler.getTenant();
        Patient receiver;
        NursingStaff nursingStaff = getNursingStaffService().getBaseNursingStaffById(doctor.getNursingId());

        // 设置发送人的信息
        imService.setSenderMessage(chat, doctor);
        R<String> accountType = tenantApi.queryOfficialAccountType(tenantCode);
        String accountTypeData = accountType.getData();
        Boolean weiXinTemplate = weiXinService.noSendWeiXinTemplate(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.ONLINE_CONSULTATION);
        List<Doctor> doctorList = new ArrayList<>();
        if (doctor.getIndependence() != null && doctor.getIndependence() == 0) {
            // 目前的这个医生可能不是 患者自己的医生
            doctorList = doctorGroupService.getReadMyMsgDoctor(doctor.getId());
        }
        try {
            receiver = getPatientService().getBasePatientById(Long.parseLong(s));
            if (receiver == null) {
                log.error("未找到id为：" + receiverIds + "的患者[Patient对象]，无法向其发送聊天[Chat]信息");
            } else {
                chat = sendChat(receiver, chat, nursingStaff, doctor, weiXinTemplate, doctorList, tenantCode, accountTypeData);
            }
        } catch (Exception var18) {
            log.error("SendChatMessage Error", var18);
        }
        return chat;
    }



    @Override
    public boolean updateById(Doctor model) {

        // 检验手机号是否已经被使用
        if (StringUtils.isNotEmpty(model.getMobile())) {
            checkDoctorMobile(model);
        }
        R<String> accountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        String accountTypeData = accountType.getData();
        if (TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER.toString().equals(accountTypeData)) {
            Long groupId = model.getGroupId();
            if (groupId != null) {
                if (groupId == 1) {
                    model.setIndependence(1);
                    // 清除小组医生关联关系。
                    // 清除医生设置的不看某医生信息设置。
                    doctorGroupService.removeByDoctorId(model.getId());
                    // 清理医生所在小组，其他医生患者群组给他生产的未读消息记录
                    List<String> patientImAccounts = getPatientService().getPatientImAccountByDoctorId(model.getId());
                    imService.refreshDoctorMsgStatus(model.getId(), patientImAccounts);
                } else {
                    DoctorGroup doctorGroup = doctorGroupService.getOne(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getDoctorId, model.getId()).last(" limit 0 , 1 "));
                    if (Objects.nonNull(doctorGroup)) {
                        Long groupGroupId = doctorGroup.getGroupId();
                        // 检查是否触发了更换小组
                        if (!groupId.equals(groupGroupId)) {
                            doctorGroup.setGroupId(groupId);
                            doctorGroup.setJoinGroupTime(LocalDateTime.now());
                            doctorGroupService.updateById(doctorGroup);
                            // 清除医生设置的不看某医生信息设置。
                            // 清除 别人 不看我的消息 的记录
                            doctorGroupService.removeNotReadMessage(model.getId());
                            // 由于医生更换了小组。清除医生之前存在的小组其他医生患者的未读消息记录
                            List<String> patientImAccounts = getPatientService().getPatientImAccountByDoctorId(model.getId());
                            imService.refreshDoctorMsgStatus(model.getId(), patientImAccounts);
                        }
                    } else {
                        doctorGroup = DoctorGroup.builder()
                                .doctorId(model.getId())
                                .groupId(groupId)
                                .joinGroupTime(LocalDateTime.now())
                                .build();
                        doctorGroupService.save(doctorGroup);
                    }
                    model.setIndependence(0);
                }
            }
            model.setGroupId(null);
        }
        Doctor doctor = baseMapper.selectOne(Wraps.<Doctor>lbQ()
                .eq(SuperEntity::getId, model.getId())
                .select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar, Doctor::getOrganName, Doctor::getQrCode));
        super.updateById(model);
        boolean updateBusinessCardQrCode = false;
        boolean updatePatientTableDoctorName = false;
        if (StrUtil.isNotEmpty(model.getName()) && !StrUtil.equals(model.getName(), doctor.getName())) {
            updateBusinessCardQrCode = true;
            updatePatientTableDoctorName = true;
        }
        if (!updateBusinessCardQrCode && StrUtil.isNotEmpty(model.getHospitalName()) && !StrUtil.equals(model.getHospitalName(), doctor.getHospitalName())) {
            updateBusinessCardQrCode = true;
        }
        if (!updateBusinessCardQrCode && StrUtil.isNotEmpty(model.getAvatar()) && !StrUtil.equals(model.getAvatar(), doctor.getAvatar())) {
            updateBusinessCardQrCode = true;
        }
        if (updateBusinessCardQrCode) {
            doctor = baseMapper.selectOne(Wraps.<Doctor>lbQ()
                    .eq(SuperEntity::getId, model.getId())
                    .select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar, Doctor::getHospitalName, Doctor::getQrCode, Doctor::getTitle, Doctor::getEnglishQrCode));
            List<Doctor> objects = new ArrayList<>();
            objects.add(doctor);
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() -> updateDoctorBusinessCardQrCode(objects, null, tenant));
        }
        if (updatePatientTableDoctorName) {
            UpdateWrapper<Patient> wrapper = new UpdateWrapper();
            wrapper.set("doctor_name", model.getName());
            wrapper.eq("doctor_id", model.getId());
            getPatientService().update(wrapper);
        }
        return true;
    }

    @Async
    @Override
    public void sendAtImWeiXinTemplateMessage(String tenantCode, Long doctorId, String senderName, String patientImAccount, Long patientId, String patientName) {

        BaseContextHandler.setTenant(tenantCode);
        Doctor doctor = getBaseDoctorAndImOpenById(doctorId);
        if (Objects.isNull(doctor)) {
            return;
        }
        String doctorOpenId = doctor.getOpenId();
        if (StrUtil.isEmpty(doctorOpenId)) {
            return;
        }
        Integer doctorWxStatus = doctor.getWxStatus();
        if (doctorWxStatus == null || doctorWxStatus != 1) {
            return;
        }
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            return;
        }
        if (tenant.isCertificationServiceNumber()) {
            String doctorChat = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                    String.format(H5Router.DOCTOR_PATIENT_CHAT, patientImAccount, patientId));

            weiXinService.sendConsultationResponse(tenant.getWxAppId(), doctorOpenId, senderName, doctorChat, patientName, tenant.getDefaultLanguage());
        }

    }


    /**
     * 发送微信模板消息
     * @param tenantCode
     * @param doctor
     * @param type
     * @param content
     * @param patientName
     */
    @Async
    @Override
    public void sendImWeiXinTemplateMessage(String tenantCode, Doctor doctor, MediaType type, String content,
                                            String patientName, String patientImAccount, Long patientId,
                                            String templateMessageIndefiner) {
        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (tenantR.getIsSuccess().equals(false) || tenant == null) {
            return;
        }
        String doctorChat = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                String.format(H5Router.DOCTOR_PATIENT_CHAT, patientImAccount, patientId));
        if (Objects.nonNull(doctor)) {
            String doctorOpenId = doctor.getOpenId();
            Integer doctorWxStatus = doctor.getWxStatus();
            String doctorImAccount = doctor.getImAccount();
            Integer imWxTemplateStatus = doctor.getImWxTemplateStatus();
            if (!org.springframework.util.StringUtils.isEmpty(doctorOpenId)) {
                if (doctorWxStatus != null && doctorWxStatus == 1
                        // 医生打开了 接收微信Im模板消息
                        && imWxTemplateStatus != null && imWxTemplateStatus == 1) {
                    // 增加IM是否在线
                    Boolean online = this.imService.online(doctorImAccount);
                    if (online == null || online.equals(false)) {
                        // 认证服务号才需要 推送模板消息
                        if (tenant.isCertificationServiceNumber()) {
                            weiXinService.sendConsultationResponse(tenant.getWxAppId(), doctorOpenId, type, content, patientName, doctorChat, patientId, templateMessageIndefiner, tenant.getDefaultLanguage());
                        }
                    }
                }
            }
        }

    }

    @Override
    public IPage<Doctor> findPage(IPage<Doctor> page, LbqWrapper<Doctor> wrapper) {
        return baseMapper.findPage(page, wrapper, new DataScope());
    }


    @Override
    public Doctor getBaseInfoByOpenId(String openId) {

        List<Doctor> doctorList = baseMapper.selectList(Wraps.<Doctor>lbQ().eq(Doctor::getOpenId, openId)
                .select(Doctor::getId, Doctor::getAvatar, Doctor::getName, Doctor::getImAccount));
        if (CollectionUtils.isEmpty(doctorList)) {
            return null;
        }
        return doctorList.get(0);
    }

    /**
     * 查询和doctorId 同一个小组的其他医生基本信息
     * @param doctorId
     * @return
     */
    @Override
    public List<Doctor> getGroupDoctorBaseInfoNoMe(Long doctorId) {

        // 获取医生所在的小组ID
        LbqWrapper<DoctorGroup> wrapper = Wraps.<DoctorGroup>lbQ()
                .eq(DoctorGroup::getDoctorId, doctorId)
                .select(DoctorGroup::getGroupId);
        List<DoctorGroup> doctorGroups = doctorGroupService.list(wrapper);

        // 医生没有小组
        if (CollectionUtils.isEmpty(doctorGroups)) {
            return new ArrayList<>();
        }
        List<Long> groupIds = doctorGroups.stream().map(DoctorGroup::getGroupId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }

        // 查询 小组下的其他医生的id 排除 我自己
        wrapper = Wraps.<DoctorGroup>lbQ()
                .in(DoctorGroup::getGroupId, groupIds)
                .ne(DoctorGroup::getDoctorId, doctorId)
                .select(DoctorGroup::getDoctorId);
        List<DoctorGroup> doctorIdList = doctorGroupService.list(wrapper);

        if (CollectionUtils.isEmpty(doctorIdList)) {
            return new ArrayList<>();
        }
        List<Long> doctorIds = doctorIdList.stream().map(DoctorGroup::getDoctorId).collect(Collectors.toList());
        LbqWrapper<Doctor> select = Wraps.<Doctor>lbQ()
                .in(SuperEntity::getId, doctorIds)
                .select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar);
        List<Doctor> doctors = baseMapper.selectList(select);
        return doctors;
    }


    /**
     * 查询每个小组下的医生数量
     * @param groups
     */
    @Override
    public void countDoctorByGroupId(List<Group> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return;
        }
        List<Long> collect = groups.stream().map(SuperEntity::getId).collect(Collectors.toList());
        QueryWrapper<DoctorGroup> wrapper = Wrappers.<DoctorGroup>query()
                .select("group_id as groupId", "count(*) as total")
                .groupBy("group_id")
                .in("group_id", collect);
        List<Map<String, Object>> groupDoctorNumber = doctorGroupService.listMaps(wrapper);

        Map<Long, Long> countDoctor = new HashMap<>(groupDoctorNumber.size());
        for (Map<String, Object> objectMap : groupDoctorNumber) {
            Object groupId = objectMap.get("groupId");
            Object total = objectMap.get("total");
            if (total != null && groupId != null) {
                countDoctor.put(Long.parseLong(groupId.toString()), Convert.toLong(total));
            }
        }
        for (Group group : groups) {
            Long aLong = countDoctor.get(group.getId());
            if (aLong != null) {
                group.setDoctorCount(aLong);
            }
        }
    }


    /**
     * 异步批量更新医生的 名片
     * @param doctors 存在时， 更新集合内的医生
     * @param lbqWrapper doctors 为空时， 使用此条件查询医生。 然后更新
     * @param tenantCode 租户
     */
    @Override
    public void updateDoctorBusinessCardQrCode(List<Doctor> doctors,
                                               LbqWrapper<Doctor> lbqWrapper,
                                               String tenantCode) {

        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant data = tenantR.getData();
        BaseContextHandler.setTenant(tenantCode);
        if (CollUtil.isEmpty(doctors) && Objects.nonNull(lbqWrapper)) {
            lbqWrapper.select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar, Doctor::getHospitalName, Doctor::getQrCode, Doctor::getTitle, Doctor::getEnglishQrCode);
            doctors = baseMapper.selectList(lbqWrapper);
        }
        if (CollUtil.isNotEmpty(doctors)) {
            String tenantName = data.getName();
            for (Doctor doctor : doctors) {
                try {
                    doctor.setBusinessCardQrCode(QrCodeUtils.patientSubscribeCode(doctor.getQrCode(), doctor.getAvatar(), doctor.getName(), tenantName, doctor.getHospitalName(), data.getLogo()));
                } catch (Exception e) {
                    log.error("更新医生信息中机构的名字后， 生成名片二维码失败。", e);
                }
                try {
                    doctor.setDownLoadQrcode(QrCodeUtils.patientSubscribeCode_2(doctor.getQrCode(), doctor.getAvatar(), doctor.getName(), tenantName, doctor.getHospitalName(), doctor.getTitle()));
                } catch (Exception e) {
                    log.error("更新医生信息中机构的名字后， 生成下载二维码失败。", e);
                }
                try {
                    doctor.setEnglishBusinessCardQrCode(QrCodeUtils.patientSubscribeCode_3(doctor.getEnglishQrCode(), doctor.getAvatar(), doctor.getName(), tenantName, doctor.getHospitalName(), doctor.getTitle()));
                } catch (Exception e) {
                    log.error("更新医生信息中机构的名字后， 生成下载二维码失败。", e);
                }
                baseMapper.updateById(doctor);
            }
        }
    }

    /**
     * 批量更新机构下的医生信息中的机构名称
     * @param orgId
     * @param orgName
     */
    @Override
    public void updateUserOrgName(long orgId, String orgName) {
        UpdateWrapper<Doctor> wrapper = new UpdateWrapper<>();
        wrapper.set("organ_name", orgName).eq("org_id", orgId);
        baseMapper.update(new Doctor(), wrapper);
    }

    /**
     * 查询可以预约的医生列表
     * @param buildPage
     * @param paramsModel
     * @return
     */
    @Override
    public IPage<Doctor> getAppointDoctor(IPage<Doctor> buildPage, DoctorAppointPageDTO paramsModel) {

        String tenant = BaseContextHandler.getTenant();
        LbqWrapper<Doctor> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Doctor::getOrganId, paramsModel.getOrganId())
                .eq(Doctor::getBuildIn, 0)
                .ne(Doctor::getCloseAppoint, 1);
        R<String> doctorScope = appConfigApi.getAppointmentDoctorScope(tenant);
        if (doctorScope.getIsSuccess() != null && doctorScope.getIsSuccess()) {
            String scopeData = doctorScope.getData();
            if (AppointmentDoctorScope.MYSELF.equals(scopeData)) {
                lbqWrapper.eq(SuperEntity::getId, paramsModel.getDoctorId());
            }
        }
        baseMapper.selectPage(buildPage, lbqWrapper);
        return buildPage;
    }


    /**
     * 获取医生所在的小组。 如果医生是独立医生，则返回 {id: 1, name: "独立医生"}
     * @param doctorId
     * @return
     */
    @Override
    public Group getDoctorGroup(Long doctorId) {
        Doctor doctor = baseMapper.selectById(doctorId);
        if (Objects.isNull(doctor)) {
            return null;
        }
        if (doctor.getIndependence() != null && doctor.getIndependence().equals(1)) {
            Group group = Group.builder().name("独立医生").build();
            group.setId(1L);
            return group;
        }
        DoctorGroup doctorGroup = doctorGroupService.getOne(Wraps.<DoctorGroup>lbQ()
                .eq(DoctorGroup::getDoctorId, doctorId).last(" limit 0,1 "));
        if (Objects.isNull(doctorGroup)) {
            return null;
        }
        Long groupId = doctorGroup.getGroupId();
        return groupMapper.selectById(groupId);
    }


    /**
     * 将医生转移到新医助下
     * @param doctorId
     * @param nursingId
     * @return
     */
    @Transactional
    @Override
    public Boolean changeNursing(Long doctorId, Long nursingId) {

        Doctor doctor = baseMapper.selectById(doctorId);
        Long doctorNursingId = doctor.getNursingId();
        NursingStaff nursingStaff = getNursingStaffService().getById(nursingId);
        if (Objects.isNull(nursingStaff)) {
            return false;
        }
        // 移除 医生原有所在小组。
        doctorGroupService.removeByDoctorId(doctorId);

        UpdateWrapper<Doctor> wrapper = new UpdateWrapper<>();
        wrapper.set("nursing_id", nursingStaff.getId());
        wrapper.set("nursing_name", nursingStaff.getName());

        wrapper.set("org_id", nursingStaff.getOrganId());
        wrapper.set("organ_code", nursingStaff.getOrganCode());
        wrapper.set("organ_name", nursingStaff.getOrganName());
        wrapper.set("class_code", nursingStaff.getClassCode());
        R<String> accountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(accountType.getData())) {
            Group group = groupMapper.selectOne(Wraps.<Group>lbQ().eq(Group::getNurseId, doctor.getNursingId()).last(" limit 0, 1"));
            if (group == null) {
                group = new Group();
                group.setNurseName(nursingStaff.getName());
                group.setClassCode(nursingStaff.getClassCode());
                group.setOrganId(nursingStaff.getOrganId());
                group.setOrganName(nursingStaff.getOrganName());
                group.setNurseId(doctor.getNursingId());
                group.setName(nursingStaff.getName()+ "医生小组");
                groupMapper.insert(group);
            }
            doctor.setGroupId(group.getId());
            DoctorGroup doctorGroup = DoctorGroup.builder().doctorId(doctor.getId())
                    .groupId(group.getId())
                    .joinGroupTime(LocalDateTime.now())
                    .build();
            doctorGroupService.save(doctorGroup);
        } else {
            // 变更为 独立医生
            wrapper.set("independence", 1);
        }
        wrapper.set("class_code", nursingStaff.getClassCode());
        wrapper.eq("id", doctorId);
        baseMapper.update(new Doctor(), wrapper);

        // 将医生的下患者转移到新的医助 机构下面
        UpdateWrapper<Patient> patientWrapper = new UpdateWrapper<>();
        patientWrapper.set("service_advisor_id", nursingStaff.getId());
        patientWrapper.set("service_advisor_name", nursingStaff.getName());

        patientWrapper.set("org_id", nursingStaff.getOrganId());
        patientWrapper.set("remark", "");
        patientWrapper.set("organ_code", nursingStaff.getOrganCode());
        patientWrapper.set("organ_name", nursingStaff.getOrganName());
        patientWrapper.set("class_code", nursingStaff.getClassCode());
        patientWrapper.eq("doctor_id", doctorId);
        getPatientService().update(new Patient(), patientWrapper);


        // 把会诊组中的医助移除。更换为新的医助
        consultationGroupService.changeNursingToNewNursing(doctorId, nursingStaff);

        // 主要处理其他服务的业务数据
        RedisMessageDoctorChangeNursing redisMessageDoctorChangeNursing = new RedisMessageDoctorChangeNursing();
        redisMessageDoctorChangeNursing.setDoctorId(doctorId);
        redisMessageDoctorChangeNursing.setTenantCode(BaseContextHandler.getTenant());
        redisMessageDoctorChangeNursing.setOrgId(nursingStaff.getOrganId());
        redisMessageDoctorChangeNursing.setPrimaryNursingId(doctorNursingId);
        redisMessageDoctorChangeNursing.setOrgCode(nursingStaff.getOrganCode());
        redisMessageDoctorChangeNursing.setOrgName(nursingStaff.getOrganName());
        redisMessageDoctorChangeNursing.setClassCode(nursingStaff.getClassCode());
        redisMessageDoctorChangeNursing.setTargetNursingId(nursingStaff.getId());
        redisMessageDoctorChangeNursing.setTargetNursingName(nursingStaff.getName());
        redisTemplate.convertAndSend(SaasRedisBusinessKey.DOCTOR_CHANGE_NURSING, JSON.toJSONString(redisMessageDoctorChangeNursing));


        return true;
    }

    /**
     * 通过IDS查询医生。不限租户
     * @return
     */
    @Override
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    public List<Doctor> findByIdsNoTenant(List<Long> doctorIds) {
        return baseMapper.findByIdsNoTenant(doctorIds);
    }

    /**
     * 通过手机号查询医生。不限租户
     * @param mobile
     * @return
     */
    @Override
    public List<Doctor> findByMobileNoTenant(String mobile) {

        try {
            String encryptedValue = EncryptionUtil.encrypt(mobile.toString());
            return baseMapper.findByMobileNoTenant(encryptedValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    public R<JSONObject> registerDoctorAndCreateToken(DoctorRegisterDTO doctorRegisterDTO) {
        Doctor defaultDoctor = findByLoginName(doctorRegisterDTO.getDomain() + Constant.DOCTOR_LOGIN_NAME_SUFFIX);
        Doctor doctor = new Doctor();
        doctor.setName(doctorRegisterDTO.getName());
        doctor.setRegisterOrgName(doctorRegisterDTO.getRegisterOrgName());
        doctor.setOpenId(doctorRegisterDTO.getOpenId());
        doctor.setWxStatus(1);
        doctor.setMobile(doctorRegisterDTO.getMobile());
        doctor.setHospitalId(doctorRegisterDTO.getHospitalId());
        doctor.setHospitalName(doctorRegisterDTO.getHospitalName());
        if (Objects.isNull(defaultDoctor)) {
            NursingStaff nursingStaff = new NursingStaff();
            nursingStaff.setLoginName(doctorRegisterDTO.getDomain() + Constant.SERVICE_LOGIN_NAME_SUFFIX);
            nursingStaff = getNursingStaffService().getOne(Wraps.<NursingStaff>lbQ(nursingStaff));
            if (Objects.nonNull(nursingStaff)) {
                doctor.setNursingId(nursingStaff.getId());
                doctor.setNursingName(nursingStaff.getName());
            }
        } else {
            doctor.setNursingId(defaultDoctor.getNursingId());
            doctor.setNursingName(defaultDoctor.getNursingName());
        }
        if (StrUtil.isEmpty(doctorRegisterDTO.getPassword())) {
            throw new BizException("密码不能为空");
        }
        String passwordMd5 = SecureUtil.md5(doctorRegisterDTO.getPassword());
        doctor.setPassword(passwordMd5);
        save(doctor);
        DoctorLoginByOpenId login = new DoctorLoginByOpenId();
        login.setMobile(doctor.getMobile());
        login.setOpenId(doctorRegisterDTO.getOpenId());
        login.setDecode(false);
        login.setPassword(doctorRegisterDTO.getPassword());
        R<JSONObject> userLogin = oauthApi.doctorLoginByOpenId(login);
        if (userLogin.getIsSuccess()) {
            JSONObject jsonObject = userLogin.getData();
            return R.success(jsonObject);
        } else {
            return R.fail(userLogin.getCode(), userLogin.getMsg());
        }

    }


    /**
     * 医生统计自己的诊断类型下人数
     * @param doctorId
     * @return
     */
    @Override
    public List<DoctorGlobalDiseaseDTO> doctorCountDisease(Long doctorId) {

        JSONArray jsonArray = getPatientService().countDiagnosisId(doctorId, UserType.UCENTER_DOCTOR);
        if (CollUtil.isEmpty(jsonArray)) {
            return new ArrayList<>();
        }
        List<DoctorGlobalDiseaseDTO> diseaseDTOS = new ArrayList<>();
        DoctorGlobalDiseaseDTO dto;
        for (Object o : jsonArray) {
            JSONObject object = JSON.parseObject(o.toString());
            dto = new DoctorGlobalDiseaseDTO();
            Object id = object.get("id");
            Object name = object.get("name");
            Object count = object.get("count");
            if (Objects.nonNull(id) && Objects.nonNull(name)) {
                dto.setId(id.toString());
                dto.setName(name.toString());
            }
            if (Objects.nonNull(count)) {
                dto.setCountPatientNumber(Long.parseLong(count.toString()));
            }
            diseaseDTOS.add(dto);
        }
        return diseaseDTOS;
    }


    /**
     * 医生全局搜索患者。诊断类型，自定义小组
     * @param doctorId
     * @param dimension
     * @param searchContent
     * @return
     */
    @Override
    public DoctorGlobalQuery doctorGlobalQuery(Long doctorId, String dimension, String searchContent) {

        final String searchKey = searchContent.trim();
        DoctorGlobalQuery doctorGlobalQuery = new DoctorGlobalQuery();
        Doctor doctor = getBaseDoctorAndImOpenById(doctorId);
        Integer independence = doctor.getIndependence();
        List<Long> doctorIds = null;
        if (independence == null) {
            independence = 1;
        }
        String role = UserType.UCENTER_DOCTOR;
        // 非独立医生。并且查询未读是 all 直接统计医生所在小组内所有医生的数据
        if (independence.equals(0) && "all".equals(dimension)) {
            String sql = " group_id in (select group_id from u_user_doctor_group where doctor_id = "+ doctorId +")";
            List<DoctorGroup> doctorGroups = doctorGroupService.list(Wraps.<DoctorGroup>lbQ()
                    .select(DoctorGroup::getDoctorId)
                    .apply(sql));
            doctorIds = doctorGroups.stream().map(DoctorGroup::getDoctorId).collect(Collectors.toList());
            role = UserType.UCENTER_DOCTOR_GROUP;
        }
        // 统计符合条件患者的列表
        LbqWrapper<Patient> select = Wraps.<Patient>lbQ()
                .select(SuperEntity::getId, Patient::getName, Patient::getSex, Patient::getBirthday,
                        Patient::getStatus, Patient::getDiagnosisId, Patient::getDiagnosisName,
                        Patient::getAvatar, Patient::getIsCompleteEnterGroup, Patient::getDoctorRemark);
        if (CollUtil.isNotEmpty(doctorIds)) {
            select.in(Patient::getDoctorId, doctorIds);
        } else {
            select.eq(Patient::getDoctorId, doctorId);
        }
        select.and(andWrapper -> andWrapper
                .or(wrapper -> wrapper.like(Patient::getDoctorRemark, searchKey))
                .or(wrapper -> wrapper.like(Patient::getName, searchKey)));
        List<Patient> patients = getPatientService().list(select);
        // 查询患者所在的医生自定义小组
        if (CollUtil.isNotEmpty(patients)) {
            List<Long> patientIds = patients.stream().map(SuperEntity::getId).collect(Collectors.toList());
            List<DoctorCustomGroupPatient> groupPatients = doctorCustomGroupPatientMapper.selectList(Wraps.<DoctorCustomGroupPatient>lbQ()
                    .select(SuperEntity::getId, DoctorCustomGroupPatient::getDoctorCustomGroupId, DoctorCustomGroupPatient::getPatientId)
                    .in(DoctorCustomGroupPatient::getPatientId, patientIds));
            List<DoctorCustomGroup> customGroups;
            Map<Long, DoctorCustomGroup> customGroupMap = new HashMap<>();
            Map<Long, List<DoctorCustomGroupPatient>> longListMap = new HashMap<>();
            if (CollUtil.isNotEmpty(groupPatients)) {
                Set<Long> groupIds = groupPatients.stream().map(DoctorCustomGroupPatient::getDoctorCustomGroupId).collect(Collectors.toSet());
                longListMap = groupPatients.stream().collect(Collectors.groupingBy(DoctorCustomGroupPatient::getPatientId));
                LbqWrapper<DoctorCustomGroup> lbqWrapper = Wraps.<DoctorCustomGroup>lbQ()
                        .select(DoctorCustomGroup::getGroupName, SuperEntity::getId)
                        .in(SuperEntity::getId, groupIds);
                if (CollUtil.isNotEmpty(doctorIds)) {
                    lbqWrapper.in(DoctorCustomGroup::getDoctorId, doctorIds);
                } else {
                    lbqWrapper.eq(DoctorCustomGroup::getDoctorId, doctorId);
                }
                customGroups = doctorCustomGroupMapper.selectList(lbqWrapper);
                if (CollUtil.isNotEmpty(customGroups)) {
                    customGroupMap = customGroups.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
                }
            }
            List<DoctorGlobalQueryPatientDto> patientDtos = new ArrayList<>();
            DoctorGlobalQueryPatientDto dto;
            for (Patient patient : patients) {
                dto = new DoctorGlobalQueryPatientDto();
                BeanUtils.copyProperties(patient, dto);
                dto.setId(patient.getId());
                List<DoctorCustomGroupPatient> groupPatientList = longListMap.get(patient.getId());
                if (CollUtil.isNotEmpty(groupPatientList)) {
                    for (DoctorCustomGroupPatient groupPatient : groupPatientList) {
                        DoctorCustomGroup group = customGroupMap.get(groupPatient.getDoctorCustomGroupId());
                        dto.addPatientExistGroupList(group);
                    }
                }
                patientDtos.add(dto);
            }
            doctorGlobalQuery.setPatientDtos(patientDtos);
        }


        R<JSONArray> diagnosis = formApi.getDiagnosis();
        List<DoctorGlobalDiseaseDTO> diseaseDTOS = new ArrayList<>();
        if (diagnosis.getIsSuccess()) {
            JSONArray datas = diagnosis.getData();
            if (CollUtil.isNotEmpty(datas)) {
                DoctorGlobalDiseaseDTO dto;
                for (Object data : datas) {
                    JSONObject object = JSONObject.parseObject(JSON.toJSONString(data));
                    String id = object.getString("id");
                    String name = object.getString("name");
                    if (name.contains(searchContent)) {
                        dto = new DoctorGlobalDiseaseDTO();
                        dto.setId(id);
                        dto.setName(name);
                        Long patient = getPatientService().countPatientByDiagnosisId(doctorId, id, role);
                        dto.setCountPatientNumber(patient);
                        diseaseDTOS.add(dto);
                    }
                }
            }
        }
        doctorGlobalQuery.setDiseaseDTOS(diseaseDTOS);

        LbqWrapper<DoctorCustomGroup> groupLbqWrapper = Wraps.<DoctorCustomGroup>lbQ()
                .select(DoctorCustomGroup::getGroupName, SuperEntity::getId)
                .like(DoctorCustomGroup::getGroupName, searchKey);
        if (CollUtil.isNotEmpty(doctorIds)) {
            groupLbqWrapper.in(DoctorCustomGroup::getDoctorId, doctorIds);
        } else {
            groupLbqWrapper.eq(DoctorCustomGroup::getDoctorId, doctorId);
        }
        List<DoctorCustomGroup> customGroups = doctorCustomGroupMapper.selectList(groupLbqWrapper);
        doctorGlobalQuery.setDoctorCustomGroups(customGroups);
        return doctorGlobalQuery;
    }
}
