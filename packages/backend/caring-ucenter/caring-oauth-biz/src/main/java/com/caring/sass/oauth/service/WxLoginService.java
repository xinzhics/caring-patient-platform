package com.caring.sass.oauth.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.boot.utils.WebUtils;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.jwt.utils.JwtUtil;
import com.caring.sass.oauth.api.ConsultationGroupApi;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.MiniappInfoApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.dto.PatientRegister;
import com.caring.sass.user.entity.ConsultationGroupMember;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.caring.sass.context.BaseContextConstants.BASIC_HEADER_KEY;

/**
 * @author caring
 * @createTime 2017-12-15 13:42
 */
@Service
@Slf4j
public class WxLoginService {

    protected final TokenUtil tokenUtil;

    private final PatientApi patientApi;

    private final DoctorApi doctorApi;

    private final ConsultationGroupApi consultationGroupApi;

    @Autowired
    MiniappInfoApi miniappInfoApi;

    public WxLoginService(TokenUtil tokenUtil, PatientApi patientApi,
                          DoctorApi doctorApi,
                          ConsultationGroupApi consultationGroupApi) {
        this.tokenUtil = tokenUtil;
        this.patientApi = patientApi;
        this.doctorApi = doctorApi;
        this.consultationGroupApi = consultationGroupApi;
    }


    /**
     * 患者注册
     * @return
     */
    public R<AuthInfo> patientRegister(PatientRegister patientRegister) {

        patientRegister.setType(1);
        R<Patient> register = patientApi.register(patientRegister);
        if (register.getIsError()) {
            throw new BizException(register.getMsg());
        }
        Patient patient = register.getData();

        JwtUserInfo userInfo = new JwtUserInfo(patient.getId(), patient.getMobile(), patient.getName(), UserType.PATIENT);
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);
        return R.success(authInfo);

    }


    /**
     * 患者登录
     * @param patientRegister
     * @return
     */
    public R<AuthInfo> patientLogin(PatientRegister patientRegister) {

        patientRegister.setType(0);
        R<Patient> register = patientApi.register(patientRegister);
        if (register.getIsError()) {
            String msg = register.getMsg();
            return R.fail(msg);
        }
        Patient patient = register.getData();

        JwtUserInfo userInfo = new JwtUserInfo(patient.getId(), patient.getMobile(), patient.getName(), UserType.PATIENT);
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);
        return R.success(authInfo);


    }


    /**
     * 微信用户登录
     *
     * @param openId 账号
     */
    public R<AuthInfo> wxLogin(String openId, String unionId) {
        String basicHeader = ServletUtil.getHeader(WebUtils.request(), BASIC_HEADER_KEY, StrPool.UTF_8);
        JwtUtil.getClient(basicHeader);

        return getPatientAuthInfo(openId, unionId);
    }

    /**
     * 会诊人员的微信登录
     * @param wxOAuth2UserInfo
     * @param groupId
     * @return
     */
    public R<AuthInfo> wxguestLogin(WxOAuth2UserInfo wxOAuth2UserInfo, Long groupId) {

        R<ConsultationGroupMember> groupMember = consultationGroupApi.findConsultationGroupMember(groupId, JSONObject.parseObject(wxOAuth2UserInfo.toString()));
        String basicHeader = ServletUtil.getHeader(WebUtils.request(), BASIC_HEADER_KEY, StrPool.UTF_8);
        JwtUtil.getClient(basicHeader);
        if (groupMember.getIsSuccess() != null && groupMember.getIsSuccess()) {
            ConsultationGroupMember data = groupMember.getData();
            JwtUserInfo userInfo = new JwtUserInfo(data.getId(), "会诊小组来宾人员", "会诊小组来宾人员", UserType.CONSULTATION_GUEST);
            AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);
            return R.success(authInfo);
        }
        return R.fail("用户服务异常授权失败");
    }

    /**
     * 使用患者openId 获取患者授权
     * @param openId
     * @param unionId
     * @return
     */
    private R<AuthInfo> getPatientAuthInfo(String openId, String unionId) {
        R<Patient> patientR = patientApi.queryByOpenId(openId);
        if (patientR.getIsError() || patientR.getData() == null) {
            return R.fail(patientR.getCode(), patientR.getMsg());
        }
        Patient patient = patientR.getData();

        JwtUserInfo userInfo = new JwtUserInfo(patient.getId(), patient.getName(), patient.getNickName(), UserType.PATIENT);
        // token过期时间 一年
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);

        String tenantCode = BaseContextHandler.getTenant();

        // 异步更新患者的 unionId
        SaasGlobalThreadPool.execute(() -> getPatientAuthInfo(patient, unionId, tenantCode));

        return R.success(authInfo);
    }

    /**
     * 患者的授权
     * @param patient
     * @param unionId
     * @return
     */
    private void getPatientAuthInfo(Patient patient, String unionId, String tenantCode) {
        // 第三方可能需要unionId，故为空则保存
        String wxUnionId = patient.getUnionId();
        BaseContextHandler.setTenant(tenantCode);
        try {
            if (StrUtil.isBlank(wxUnionId)) {
                patient.setUnionId(unionId);
                patientApi.update(patient);
            }
        } catch (Exception e) {
            log.error("修改用户unionId失败", e);
        }
    }

    /**
     * 病例讨论的扫码用户授权
     * @param data
     * @return
     */
    public AuthInfo consultationGroupMemberLogin(ConsultationGroupMember data) {

        JwtUserInfo userInfo;
        if (UserType.UCENTER_PATIENT.equals(data.getMemberRole())) {
            userInfo = new JwtUserInfo(data.getPatientId(), data.getMemberName(), data.getMemberName(), UserType.PATIENT);
        } else {
            userInfo = new JwtUserInfo(data.getId(), "会诊小组来宾人员", "会诊小组来宾人员", UserType.CONSULTATION_GUEST);
        }
        return tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);


    }


    /**
     * 使用微信服务提供的微信用户信息。 查询用户完成登录
     * @param wxOAuth2UserInfo
     * @param userType
     * @param groupId
     * @return
     */
    public R<String> wxUserLogin(WxOAuth2UserInfo wxOAuth2UserInfo, String userType, Long groupId) {
        String openId = wxOAuth2UserInfo.getOpenid();
        if (UserType.CONSULTATION_GUEST.equals(userType)) {
            R<ConsultationGroupMember> groupMember = consultationGroupApi.findConsultationGroupMember(groupId, JSON.parseObject(JSON.toJSONString(wxOAuth2UserInfo)));
            if (groupMember.getIsSuccess() != null && groupMember.getIsSuccess()) {
                ConsultationGroupMember data = groupMember.getData();
                JwtUserInfo userInfo;
                if (UserType.UCENTER_PATIENT.equals(data.getMemberRole())) {
                    userInfo = new JwtUserInfo(data.getPatientId(), data.getMemberName(), data.getMemberName(), UserType.PATIENT);
                } else {
                    userInfo = new JwtUserInfo(data.getId(), "会诊小组来宾人员", "会诊小组来宾人员", UserType.CONSULTATION_GUEST);
                }
                AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);

                String jsonString = JSON.toJSONString(authInfo);
                return R.success(jsonString);
            } else {
                return R.fail("");
            }
        }
        if (UserType.UCENTER_PATIENT.equals(userType)) {

            R<AuthInfo> authInfoR = getPatientAuthInfo(openId, wxOAuth2UserInfo.getUnionId());
            if (authInfoR.getIsSuccess()) {
                AuthInfo authInfo = authInfoR.getData();
                String jsonString = JSON.toJSONString(authInfo);
                return R.success(jsonString);
            } else {
                return R.fail(authInfoR.getCode(), authInfoR.getMsg());
            }
        }
        if (UserType.UCENTER_DOCTOR.equals(userType)) {
            // 使用openId 查询 医生是否存在。存在则，授权登录
            String tenant = BaseContextHandler.getTenant();
            R<Doctor> doctorR = doctorApi.findByOpenId(wxOAuth2UserInfo.getOpenid(), tenant);
            Doctor doctor = doctorR.getData();
            // 查询医生不存在， 则要求跳转到 医生的登录 页面
            if (Objects.isNull(doctor)) {
                return R.fail(ExceptionCode.DOCTOR_NEED_LOGIN);
            } else {
                if (Objects.isNull(doctor.getFirstLoginTime())) {
                    doctor.setFirstLoginTime(LocalDateTime.now());
                }
                doctor.setLastLoginTime(System.currentTimeMillis());
                JwtUserInfo userInfo = new JwtUserInfo();
                userInfo.setAccount(doctor.getMobile());
                userInfo.setName(doctor.getName());
                userInfo.setUserId(doctor.getId());
                userInfo.setUserType(UserType.DOCTOR);

                // token有效期 改为360天
                AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);
                authInfo.setWorkDescribe(doctor.getImAccount());
                log.info("token={}", authInfo.getToken());

                SaasGlobalThreadPool.execute(() -> doctorLoginAsync(doctor,  tenant));
                String jsonString = JSON.toJSONString(authInfo);
                return R.success(jsonString);
            }
        }
        return R.fail("");
    }




    /**
     * 医生生产授权token后。异步更新其他信息
     * @param doctor
     * @param tenantCode
     */
    private void doctorLoginAsync(Doctor doctor, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        doctor.setWxStatus(1);
        doctor.setLoginStatus(true);
        doctorApi.updateWithTenant(doctor, BaseContextHandler.getTenant());
        miniappInfoApi.setRemindSubscriptionMassageTrueByPhone(doctor.getMobile());
    }



}
