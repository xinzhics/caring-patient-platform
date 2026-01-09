package com.caring.sass.oauth.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.caring.sass.authority.dto.auth.DoctorCodeLoginDTO;
import com.caring.sass.authority.dto.auth.DoctorLogin2_4Dto;
import com.caring.sass.authority.dto.auth.DoctorLoginByOpenId;
import com.caring.sass.authority.dto.auth.DoctorLoginDto;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.common.utils.WxHeadImgUrl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.msgs.api.VerificationCodeApi;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.MiniappInfoApi;
import com.caring.sass.oauth.utils.I18nUtils;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.sms.enumeration.VerificationCodeType;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.wx.WeiXinApi;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 医生登录服务
 *
 * @author xinzh
 */
@Service
@Slf4j
public class DoctorLoginService {
    private final TokenUtil tokenUtil;

    private final DoctorApi doctorApi;

    private final VerificationCodeApi verificationCodeApi;

    private final WeiXinApi weiXinApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    MiniappInfoApi miniappInfoApi;

    public DoctorLoginService(TokenUtil tokenUtil, DoctorApi doctorApi, VerificationCodeApi verificationCodeApi, WeiXinApi weiXinApi) {
        this.tokenUtil = tokenUtil;
        this.doctorApi = doctorApi;
        this.verificationCodeApi = verificationCodeApi;
        this.weiXinApi = weiXinApi;
    }


    /**
     * 医生通过微信code登录
     *
     * @return 医生通过微信code登录
     */
    public R<AuthInfo> loginByWxCode(DoctorCodeLoginDTO doctorCodeLoginDTO) {
        String appId = doctorCodeLoginDTO.getAppId();
        String code = doctorCodeLoginDTO.getCode();
        R<WxOAuth2UserInfo> wxOAuth2UserInfoR = weiXinApi.getWxUser(appId, code);
        if (wxOAuth2UserInfoR.getIsError()) {
            return R.fail("无法通过code获取医生信息");
        }
        WxOAuth2UserInfo wxOAuth2UserInfo = wxOAuth2UserInfoR.getData();
        String openId = wxOAuth2UserInfo.getOpenid();
        String tenant = BaseContextHandler.getTenant();
        R<Doctor> doctorR = doctorApi.findByOpenId(openId, tenant);
        if (doctorR.getIsError() || Objects.isNull(doctorR.getData())) {
            return R.fail("请通过手机号登录");
        }
        Doctor doctor = doctorR.getData();
        doctor.setCity(wxOAuth2UserInfo.getCity())
                .setProvince(wxOAuth2UserInfo.getProvince())
                .setSex(wxOAuth2UserInfo.getSex())
                .setWxStatus(1)
                .setOpenId(openId);
        String avatar = StrUtil.isBlank(doctor.getAvatar()) ? WxHeadImgUrl.size640(wxOAuth2UserInfo.getHeadImgUrl()) : doctor.getAvatar();
        doctor.setAvatar(avatar);
        if (doctor.getFirstLoginTime() == null) {
            doctor.setFirstLoginTime(LocalDateTime.now());
        }
        doctor.setLastLoginTime(System.currentTimeMillis());
        SaasGlobalThreadPool.execute(() -> updateDoctor(doctor, doctor.getMobile(), tenant));

        JwtUserInfo userInfo = new JwtUserInfo();
        userInfo.setAccount(doctor.getMobile());
        userInfo.setName(doctor.getName());
        userInfo.setUserId(doctor.getId());
        userInfo.setUserType(UserType.DOCTOR);

        // token有效期 改为360天
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);
        authInfo.setWorkDescribe(doctor.getImAccount());
        log.info("token={}", authInfo.getToken());
        return R.success(authInfo);
    }

    /**
     * 退出登录 解绑openId和医生关系
     *
     * @param doctorId 医生id
     */
    public void logout(Long doctorId) {
        R<Doctor> doctorR = doctorApi.get(doctorId);
        if (doctorR.getIsSuccess() && Objects.nonNull(doctorR.getData())) {
            Doctor doctor = doctorR.getData();
            doctor.setOpenId("");
            doctor.setLoginStatus(false);
            // 医生退出登录。 医生和微信接触了绑定。等于医生取关
            doctor.setWxStatus(3);
            doctorApi.update(doctor);
        }
    }
    /**
     * 医生登录
     *
     * @return 登录信息
     */
    @Deprecated
    public R<AuthInfo> login(DoctorLoginDto doctorLoginDto) {
        String mobile = doctorLoginDto.getMobile();
        String verifyCode = doctorLoginDto.getVerifyCode();
        String appId = doctorLoginDto.getAppId();
        String code = doctorLoginDto.getCode();
        R<Boolean> verificationR = verificationCodeApi.verification(VerificationCodeDTO.builder()
                .mobile(mobile)
                .type(VerificationCodeType.DOCTOR_LOGIN)
                .clearKey(true)
                .code(verifyCode)
                .build());

        boolean verifyCodeError = verificationR.getIsError() ||
                Objects.isNull(verificationR.getData()) ||
                !verificationR.getData();
        if (verifyCodeError) {
            return R.fail("验证码错误");
        }

        R<Doctor> doctorR = doctorApi.findByMobile(mobile);
        if (doctorR.getIsError()) {
            return R.fail("用户不存在");
        }

        Doctor doctor = doctorR.getData();
        if (Objects.isNull(doctor)) {
            return R.fail("用户不存在");
        }

        // 获取医生微信信息
        try {
            R<WxOAuth2UserInfo> wxOAuth2UserInfoR = weiXinApi.getWxUser(appId, code);
            if (wxOAuth2UserInfoR.getIsError()) {
                return R.fail("根据code获取用户信息失败");
            }
            // 清空已经绑定openId的医生
            String openId = doctor.getOpenId();
            if (StrUtil.isNotBlank(openId)) {
                R<List<Doctor>> doctorsR = doctorApi.query(Doctor.builder().openId(openId).build());
                if (doctorsR.getIsSuccess()) {
                    for (Doctor d : doctorsR.getData()) {
                        // 当前这个医生
                        if (Objects.equals(d.getId(), doctor.getId())) {
                            continue;
                        }
                        d.setOpenId("");
                        doctorApi.update(d);
                    }
                }
            }
            WxOAuth2UserInfo wxOAuth2UserInfo = wxOAuth2UserInfoR.getData();
            doctor.setCity(wxOAuth2UserInfo.getCity())
                    .setProvince(wxOAuth2UserInfo.getProvince())
                    .setSex(wxOAuth2UserInfo.getSex())
                    .setWxStatus(1)
                    .setOpenId(wxOAuth2UserInfo.getOpenid());
            if (doctor.getFirstLoginTime() == null) {
                doctor.setFirstLoginTime(LocalDateTime.now());
            }
            doctor.setLastLoginTime(System.currentTimeMillis());
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() -> updateDoctor(doctor, doctor.getMobile(), tenant));
        } catch (Exception e) {
            log.error("获取医生微信信息失败", e);
        }

        JwtUserInfo userInfo = new JwtUserInfo();
        userInfo.setAccount(mobile);
        userInfo.setName(doctor.getName());
        userInfo.setUserId(doctor.getId());
        userInfo.setUserType(UserType.DOCTOR);
        // token有效期 改为360天
        Long expireTime = ApplicationProperties.getDoctorTokenExpire() == null ? Constant.ONE_YEAR_DAYS : ApplicationProperties.getDoctorTokenExpire();
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, expireTime);

        log.info("token={}", authInfo.getToken());
        return R.success(authInfo);
    }

    /**
     * 发送验证码
     */
    public R<Boolean> send(VerificationCodeDTO verificationCodeDTO) {
        String mobile = verificationCodeDTO.getMobile();
        R<Doctor> r = doctorApi.findByMobile(mobile);
        if (r.getIsError() || Objects.isNull(r.getData())) {
            return R.fail(I18nUtils.getMessage("DOCTOR_NOT_EXIST"));
        }

        String key = "doctor_sms_" + mobile;
        String string = redisTemplate.opsForValue().get(key);
        if (string != null && Integer.parseInt(string) > 10) {
            throw new BizException(I18nUtils.getMessage("SMS_SEND_CODE_MORE"));
        }
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey == null || !hasKey) {
            redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().increment(key, 1);
        }

        return verificationCodeApi.send(verificationCodeDTO);
    }

    /**
     * 清除 此openID绑定的其他医生
     * @param openId
     * @param doctorId
     */
    public void clearDoctorOpenId(String openId, Long doctorId) {
        if (StrUtil.isNotBlank(openId)) {
            R<List<Doctor>> doctorsR = doctorApi.query(Doctor.builder().openId(openId).build());
            if (doctorsR.getIsSuccess()) {
                for (Doctor d : doctorsR.getData()) {
                    // 当前这个医生
                    if (Objects.equals(d.getId(), doctorId)) {
                        continue;
                    }
                    d.setOpenId("");
                    doctorApi.update(d);
                }
            }
        }
    }

    /**
     * 校验手机号提交的短信验证码是否正确
     * @param mobile
     * @param verifyCode
     */
    public void verificationCode(String mobile, String verifyCode) {
        R<Boolean> verificationR = verificationCodeApi.verification(VerificationCodeDTO.builder()
                .mobile(mobile)
                .type(VerificationCodeType.DOCTOR_LOGIN)
                .clearKey(true)
                .code(verifyCode)
                .build());

        boolean verifyCodeError = verificationR.getIsError() ||
                Objects.isNull(verificationR.getData()) ||
                !verificationR.getData();
        if (verifyCodeError) {
            throw new BizException("验证码错误");
        }
    }

    /**
     * 通过手机号获取医生的信息
     * @param mobile
     * @return
     */
    public Doctor getDoctorInfoByMobile(String mobile) {
        R<Doctor> doctorR = doctorApi.findByMobile(mobile);
        if (doctorR.getIsError()) {
            throw new BizException("获取医生信息异常");
        }

        Doctor doctor = doctorR.getData();
        if (Objects.isNull(doctor)) {
            throw new BizException("医生不存在");
        }
        return doctor;
    }

    private  WxOAuth2UserInfo getWxUser(Object mpUser) {
        WxOAuth2UserInfo wxOAuth2UserInfo = new WxOAuth2UserInfo();
        Map<String, Object> map = (Map)mpUser;
        Object openId = map.get("openId");
        Object city = map.get("city");
        Object province = map.get("province");
        Object country = map.get("country");
        Object headImgUrl = map.get("headImgUrl");
        Object unionId = map.get("unionId");
        Object nickname = map.get("nickname");
        Object sex = map.get("sex");
        if (Objects.nonNull(openId)) {
            wxOAuth2UserInfo.setOpenid(openId.toString());
        }
        if (Objects.nonNull(city)) {
            wxOAuth2UserInfo.setCity(city.toString());
        }
        if (Objects.nonNull(province)) {
            wxOAuth2UserInfo.setProvince(province.toString());
        }
        if (Objects.nonNull(country)) {
            wxOAuth2UserInfo.setCountry(country.toString());
        }
        if (Objects.nonNull(headImgUrl)) {
            wxOAuth2UserInfo.setHeadImgUrl(headImgUrl.toString());
        }
        if (Objects.nonNull(unionId)) {
            wxOAuth2UserInfo.setUnionId(unionId.toString());
        }
        if (Objects.nonNull(nickname)) {
            wxOAuth2UserInfo.setNickname(nickname.toString());
        }
        if (Objects.nonNull(sex)) {
            wxOAuth2UserInfo.setSex(Integer.parseInt(sex.toString()));
        }
        return wxOAuth2UserInfo;
    }

    /**
     * 医生登录
     * 防止前端提交的code是过期的。此处处理为前端提交微信信息
     * @param doctorLoginDto
     * @return
     */
    public R<AuthInfo> login2_4(DoctorLogin2_4Dto doctorLoginDto) {


        String mobile = doctorLoginDto.getMobile();
        String verifyCode = doctorLoginDto.getVerifyCode();
        verificationCode(mobile, verifyCode);

        Doctor doctor = getDoctorInfoByMobile(mobile);
        WxOAuth2UserInfo wxOAuth2UserInfo;
        try {
            wxOAuth2UserInfo = getWxUser(doctorLoginDto.getWxMpUser());
        } catch (Exception e) {
            return R.fail("微信用户信息异常");
        }
        // 获取医生微信信息
        try {
            // 清空已经绑定openId的医生
            clearDoctorOpenId(wxOAuth2UserInfo.getOpenid(), doctor.getId());
        } catch (Exception e) {
            log.error("clearDoctorOpenId_error", e);
        }
        // 给医生绑定微信信息
        bindDoctorWechat(doctor, wxOAuth2UserInfo);

        // 设置授权
        AuthInfo authInfo = getDoctorToken(mobile, doctor.getName(), doctor.getId());
        return R.success(authInfo);

    }

    /**
     * 给医生绑定微信信息
     */
    private void bindDoctorWechat(Doctor doctor, WxOAuth2UserInfo wxOAuth2UserInfo) {

        if (Objects.nonNull(wxOAuth2UserInfo)) {
            if (StringUtils.isNotEmptyString(wxOAuth2UserInfo.getCity())) {
                doctor.setCity(wxOAuth2UserInfo.getCity());
            }
            if (StringUtils.isNotEmptyString(wxOAuth2UserInfo.getProvince())) {
                doctor.setCity(wxOAuth2UserInfo.getProvince());
            }
            if (Objects.nonNull(wxOAuth2UserInfo.getSex())) {
                doctor.setSex(wxOAuth2UserInfo.getSex());
            }
            doctor.setWxStatus(1).setOpenId(wxOAuth2UserInfo.getOpenid());
            if (doctor.getFirstLoginTime() == null) {
                doctor.setFirstLoginTime(LocalDateTime.now());
            }
            doctor.setLastLoginTime(System.currentTimeMillis());
            String tenant = BaseContextHandler.getTenant();
//            doctorApi.update(doctor);
//            miniappInfoApi.setRemindSubscriptionMassageTrueByPhone(doctor.getMobile());
            SaasGlobalThreadPool.execute(() -> updateDoctor(doctor, doctor.getMobile(), tenant));

        }

    }




    /**
     * 生成医生的登录授权
     * @param mobile
     * @param doctorName
     * @param doctorId
     * @return
     */
    private AuthInfo getDoctorToken(String mobile, String doctorName, Long doctorId) {
        JwtUserInfo userInfo = new JwtUserInfo();
        userInfo.setAccount(mobile);
        userInfo.setName(doctorName);
        userInfo.setUserId(doctorId);
        userInfo.setUserType(UserType.DOCTOR);
        // token有效期 改为360天
        Long expireTime = ApplicationProperties.getDoctorTokenExpire() == null ? Constant.ONE_YEAR_DAYS : ApplicationProperties.getDoctorTokenExpire();
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, expireTime);

        log.info("token={}", authInfo.getToken());
        return authInfo;
    }



    public R<Boolean> doctorBindWeixin(Long doctorId, String appId, String code) {
        R<Doctor> doctorR = doctorApi.get(doctorId);
        Doctor doctor;
        if (doctorR.getIsSuccess()) {
            doctor = doctorR.getData();
        } else {
            return R.fail("医生信息不存在");
        }
        R<WxOAuth2UserInfo> apiWxUser = weiXinApi.getWxUser(appId, code);
        WxOAuth2UserInfo wxOAuth2UserInfo;
        if (apiWxUser.getIsSuccess()) {
            wxOAuth2UserInfo = apiWxUser.getData();
        } else {
            return R.fail("微信信息获取异常");
        }

        bindDoctorWechat(doctor, wxOAuth2UserInfo);
        return R.success(true);
    }

    /**
     * 医生使用openId进行登录
     * @param doctorLoginByOpenId
     * @return
     */
    public R<AuthInfo> doctorLoginByOpenId(DoctorLoginByOpenId doctorLoginByOpenId) {

        String mobile = doctorLoginByOpenId.getMobile();
        String verifyCode = doctorLoginByOpenId.getVerifyCode();
        String openId = doctorLoginByOpenId.getOpenId();
        Doctor doctor = getDoctorInfoByMobile(mobile);
        if (StrUtil.isNotBlank(verifyCode)) {
            verificationCode(mobile, verifyCode);
        } else {
            if (Objects.isNull(doctor)) {
                return R.fail("医生不存在");
            }
            String inputPassword = doctorLoginByOpenId.getPassword();
            String password = doctor.getPassword();
            String passwordMd5 = SecureUtil.md5(inputPassword);
            if (!password.equals(passwordMd5)) {
                return R.fail("密码不正确");
            }
        }
        try {
            // 清空已经绑定openId的医生
            clearDoctorOpenId(openId, doctor.getId());
        } catch (Exception e) {
            log.error("clearDoctorOpenId_error", e);
        }
        if (StrUtil.isNotEmpty(openId)) {
            doctor.setWxStatus(1).setOpenId(openId);
        }
        if (doctor.getFirstLoginTime() == null) {
            doctor.setFirstLoginTime(LocalDateTime.now());
        }
        String tenant = BaseContextHandler.getTenant();
        doctor.setLastLoginTime(System.currentTimeMillis());
        SaasGlobalThreadPool.execute(() -> updateDoctor(doctor, mobile, tenant));
        // 设置授权
        AuthInfo authInfo = getDoctorToken(mobile, doctor.getName(), doctor.getId());
        return R.success(authInfo);

    }

    /**
     * 异步更新医生信息和小程序用户是否需要关注的状态
     * @param doctor
     * @param mobile
     * @param tenantCode
     */
    public void updateDoctor(Doctor doctor, String mobile, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        doctor.setLoginStatus(true);
        doctorApi.update(doctor);
        miniappInfoApi.setRemindSubscriptionMassageTrueByPhone(mobile);
    }
}
