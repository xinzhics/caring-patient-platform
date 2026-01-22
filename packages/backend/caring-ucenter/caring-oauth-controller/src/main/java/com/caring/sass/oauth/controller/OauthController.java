package com.caring.sass.oauth.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.authority.dto.auth.*;
import com.caring.sass.base.R;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.LoginDecryption;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.utils.JwtUtil;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.oauth.dto.MiniAppOpenIdLoginDTO;
import com.caring.sass.oauth.dto.MiniAppPhoneLoginDTO;
import com.caring.sass.oauth.dto.Oauth2AccessToken;
import com.caring.sass.oauth.dto.TokenRequest;
import com.caring.sass.oauth.granter.ClientTokenGranter;
import com.caring.sass.oauth.granter.TokenGranter;
import com.caring.sass.oauth.granter.TokenGranterBuilder;
import com.caring.sass.oauth.service.*;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.user.dto.PatientRegister;
import com.caring.sass.user.entity.ConsultationGroupMember;
import com.caring.sass.wx.WeiXinApi;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.Objects;

import static com.caring.sass.context.BaseContextConstants.JWT_KEY_USER_ID;

/**
 * 认证Controller
 *
 * @author caring
 * @date 2020年03月31日10:10:36
 */
@Slf4j
@RestController
@RequestMapping("/anno")
@AllArgsConstructor
@Api(value = "用户授权认证", tags = "登录接口")
public class OauthController {

    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private TokenGranterBuilder tokenGranterBuilder;
    @Autowired
    private AdminUiService authManager;
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private AppLoginService appLoginService;

    @Autowired
    private WeiXinApi weiXinApi;

    @Autowired
    private WxLoginService wxLoginService;

    @Autowired
    private DoctorLoginService doctorLoginService;

    @Autowired
    private ClientTokenGranter clientTokenGranter;

    @Autowired
    private TempTokenService tempTokenService;

    @Autowired
    private MiniAppLoginService miniAppLoginService;

    @Autowired
    private AiUserTokenService aiUserTokenService;

    /**
     * 第三方应用授权token
     *
     * @param tokenRequest 请求参数
     * @throws BizException
     */
    @ApiOperation(value = "获取应用token", notes = "获取应用token")
    @PostMapping(value = "/client")
    public R<Oauth2AccessToken> client(@Validated @RequestBody TokenRequest tokenRequest) throws BizException {
        return clientTokenGranter.grant(tokenRequest);
    }

    /**
     * 租户登录 caring-ui 系统
     *
     * @param login
     * @return
     * @throws BizException
     */
    @ApiOperation("使用tempToken登录项目端")
    @PostMapping(value = "/tempToken/login")
    public R<AuthInfo> tempTokenLogin(@Validated @RequestBody TempTokenLoginDTO login) throws BizException {
        AuthInfo authInfo = tempTokenService.login(login.getTempToken());
        return R.success(authInfo);
    }

    /**
     * 租户登录 caring-ui 系统
     *
     * @param login
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取认证token（登录接口）", notes = "登录或者清空缓存时调用")
    @PostMapping(value = "/token")
    public R<AuthInfo> login(@Validated @RequestBody LoginParamDTO login) throws BizException {
        if (StrUtil.isEmpty(login.getTenant())) {
            login.setTenant(BaseContextHandler.getTenant());
        }
        login.setTenant(login.getTenant());//2026daxiong 不解密(JwtUtil.base64Decoder(login.getTenant()));
        try {
//            nursing001
//            nursing002
//            nursing003
//            nursing004
//            nursing005
//            nursing006
//            nursing007
//            nursing008
//            nursing009
//            nursing010
            //打印密文
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing001"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing002"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing003"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing004"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing005"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing006"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing007"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing008"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing009"));
            log.info("打印密文：{}",EncryptionUtil.encrypt("nursing010"));

        } catch (Exception e) {

        }

        String grantType = login.getGrantType();
        TokenGranter granter;
        if ("refresh_token".equals(grantType)) {
            granter = tokenGranterBuilder.getGranter(login.getGrantType());
        } else {
            String account = login.getAccount();
            String password = login.getPassword();
            if (StrUtil.isEmpty(account) || StrUtil.isEmpty(password)) {
                return R.fail("账密或密码不能为空");
            }
//            account = LoginDecryption.loginDecryption(account);
//            password = LoginDecryption.loginDecryption(password);
            // 校验 account 字段是否包含非法字符
            if (!LoginDecryption.isValidAccount(account)) {
                return R.fail("账号不能包含 特殊字符：< > & # $ * ( ) -");
            }
            login.setAccount(account);
            login.setPassword(password);
            granter = tokenGranterBuilder.getGranter(login.getGrantType());
        }
        return granter.grant(login);
    }

    /**
     * 验证验证码
     *
     * @param key  验证码唯一uuid key
     * @param code 验证码
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "验证验证码", notes = "验证验证码")
    @GetMapping(value = "/check")
    public R<Boolean> check(@RequestParam(value = "key") String key, @RequestParam(value = "code") String code) throws BizException {
        return this.validateCodeService.check(key, code);
    }

    @ApiOperation(value = "验证码", notes = "验证码")
    @GetMapping(value = "/captcha", produces = "image/png")
    public void captcha(@RequestParam(value = "key") String key, HttpServletResponse response) throws IOException {
        this.validateCodeService.create(key, response);
    }

    /**
     * 验证token
     *
     * @param token
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "验证token", notes = "验证token")
    @GetMapping(value = "/verify")
    public R<AuthInfo> verify(@RequestParam(value = "token") String token) throws BizException {
        return R.success(tokenUtil.getAuthInfo(token));
    }


    /**
     * 管理员登录 caring-admin-ui 系统
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "超级管理员登录", notes = "超级管理员登录")
    @PostMapping(value = "/admin/login")
    public R<AdminAuthInfo> loginAdminTx(@Validated @RequestBody LoginParamDTO login) throws BizException {
        String account = login.getAccount();
        String password = login.getPassword();
        if (StrUtil.isEmpty(account) || StrUtil.isEmpty(password)) {
            return R.fail("账密或密码不能为空");
        }
        account = LoginDecryption.loginDecryption(account);
        password = LoginDecryption.loginDecryption(password);
        // 校验 account 字段是否包含非法字符
        if (!LoginDecryption.isValidAccount(account)) {
            return R.fail("账号不能包含特殊字符：< > & # $ * ( ) -");
        }
        // 仅线上环境校验验证码
        R<Boolean> check = validateCodeService.check(login.getKey(), login.getCode());
        if (check.getIsError()) {
            return R.fail(check.getMsg());
        }

        return authManager.adminLogin(account, password);
    }




    @ApiOperation(value = "超级管理员手机号登录")
    @PostMapping(value = "/admin/mobile/login")
    public R<AdminAuthInfo> loginMobileAdminTx(@Validated @RequestBody AdminMobileLoginDto login) throws BizException {
        // 仅线上环境校验验证码
        @NotEmpty(message = "手机号不能为空") String mobile = login.getMobile();
        if (StrUtil.isEmpty(mobile)) {
            return R.fail("登录的手机号不能为空");
        }
        mobile = LoginDecryption.loginDecryption(mobile);
        // 校验 account 字段是否包含非法字符
        if (!LoginDecryption.isValidAccount(mobile)) {
            return R.fail("账号不能包含特殊字符：< > & # $ * ( ) -");
        }
        authManager.checkAdminCode(mobile, login.getVerifyCode());
        return authManager.adminMobileLogin(mobile, login.getPassword());
    }


    @ApiOperation(value = "app登录", notes = "app登录")
    @PostMapping("anno/app/login")
    public R<AuthInfo> loginApp(@RequestBody AppLoginDTO appLoginDto) {
        if (StringUtils.isEmpty(appLoginDto.getMobile())) {
            throw new BizException("手机号或登录名不能为空");
        }
        if (StringUtils.isEmpty(appLoginDto.getPassword())) {
            throw new BizException("密码不能为空");
        }
        // 校验 account 字段是否包含非法字符
        if (!LoginDecryption.isValidAccount(appLoginDto.getMobile())) {
            return R.fail("账号不能包含特殊字符：< > & # $ * ( ) -");
        }
        return appLoginService.login(appLoginDto.getMobile(), appLoginDto.getPassword());
    }


    @ApiOperation(value = "微信登录", notes = "微信登录")
    @PostMapping("anno/wxUser/login")
    public R<String> wxUserLogin(@RequestBody JSONObject loginUser,
                                   @RequestParam("userType") String userType,
                                   @RequestParam(value = "groupId", required = false) Long groupId) {
        WxOAuth2UserInfo wxOAuth2UserInfo = JSONObject.parseObject(loginUser.toJSONString(), WxOAuth2UserInfo.class);
        return wxLoginService.wxUserLogin(wxOAuth2UserInfo, userType, groupId);
    }

    @ApiOperation(value = "病例讨论扫码用户手机号登录", notes = "微信登录")
    @PostMapping("anno/consultationGroupMember/login")
    public R<String> consultationGroupMemberLogin(@RequestBody ConsultationGroupMember consultationGroupMember) {
        AuthInfo authInfo = wxLoginService.consultationGroupMemberLogin(consultationGroupMember);
        return R.success(JSON.toJSONString(authInfo));
    }


    @ApiOperation(value = "游客选择患者身份后，登录直接openId登录")
    @PostMapping("anno/wxPatient/login")
    public R<AuthInfo> wxPatientLogin(@RequestBody JSONObject loginUser,
                                 @RequestParam("userType") String userType,
                                 @RequestParam(value = "groupId", required = false) Long groupId) {
        Object openId = loginUser.get("openId");
        Object unionId = loginUser.get("unionId");
        if (Objects.isNull(openId)) {
            return R.fail("请在微信中打开");
        }
        WxOAuth2UserInfo wxOAuth2UserInfo = new WxOAuth2UserInfo();
        wxOAuth2UserInfo.setOpenid(openId.toString());
        if (Objects.nonNull(unionId)) {
            wxOAuth2UserInfo.setUnionId(unionId.toString());
        }
        R<String> userLogin = wxLoginService.wxUserLogin(wxOAuth2UserInfo, userType, groupId);
        if (userLogin.getIsSuccess()) {
            String loginData = userLogin.getData();
            AuthInfo authInfo = JSON.parseObject(loginData, AuthInfo.class);
            return R.success(authInfo);
        } else {
            return R.fail(userLogin.getCode(), userLogin.getMsg());
        }
    }

    /**
     * 患者OR医生微信用户登录
     *
     * @param appId 公众号appId
     * @param code  微信code
     * @return
     */
    @ApiOperation(value = "微信患者登录弃用", notes = "微信登录")
    @Deprecated
    @PostMapping("wx/login")
    public R<AuthInfo> patientWxLogin(@RequestParam @NotEmpty(message = "appId不能为空") String appId,
                                      @RequestParam(name = "code") @NotEmpty(message = "code不能为空") String code) {
        R<WxOAuth2UserInfo> wxOAuth2UserInfoR = weiXinApi.getWxUser(appId, code);
        if (wxOAuth2UserInfoR.getIsError()) {
            return R.fail("获取微信用户信息失败");
        }
        WxOAuth2UserInfo wxOAuth2UserInfo = wxOAuth2UserInfoR.getData();
        return wxLoginService.wxLogin(wxOAuth2UserInfo.getOpenid(), wxOAuth2UserInfo.getUnionId());
    }

    @ApiOperation(value = "微信来宾登录", notes = "微信来宾登录")
    @Deprecated
    @PostMapping("/guest/login")
    public R<AuthInfo> guestLogin(@RequestParam @NotEmpty(message = "appId不能为空") String appId,
                                  @RequestParam(name = "code") @NotEmpty(message = "code不能为空") String code,
                                  @RequestParam("groupId") Long groupId) {
        R<WxOAuth2UserInfo> wxOAuth2UserInfoR = weiXinApi.getWxUser(appId, code);
        if (wxOAuth2UserInfoR.getIsError()) {
            return R.fail("获取微信用户信息失败");
        }
        WxOAuth2UserInfo wxOAuth2UserInfo = wxOAuth2UserInfoR.getData();
        return wxLoginService.wxguestLogin(wxOAuth2UserInfo, groupId);
    }

    @ApiOperation(value = "医生重新绑定", notes = "医生重新绑定")
    @Deprecated
    @PostMapping("/doctor/bind_weixin/{doctorId}/{appId}/{code}")
    public R<Boolean> doctorBindWeixin(@PathVariable("doctorId") Long doctorId,
                                        @PathVariable("appId") String appId,
                                        @PathVariable("code") String code) {
        return doctorLoginService.doctorBindWeixin(doctorId, appId, code);
    }


    @ApiOperation(value = "医生使用手机号和openId登录")
    @PostMapping("/doctor/login/openId")
    @SysLog("医生使用手机号和openId登录")
    public R<AuthInfo> doctorLoginByOpenId(@RequestBody @Validated DoctorLoginByOpenId doctorLoginByOpenId) {
        @NotEmpty(message = "手机号不能为空") String mobile = doctorLoginByOpenId.getMobile();
        Boolean decode = doctorLoginByOpenId.getDecode();
        if (decode != null && decode && StrUtil.isNotEmpty(mobile)) {
            mobile = LoginDecryption.loginDecryption(mobile);
            doctorLoginByOpenId.setMobile(mobile);
        }
        // 校验 account 字段是否包含非法字符
        if (!LoginDecryption.isValidAccount(mobile)) {
            return R.fail("账号不能包含特殊字符：< > & # $ * ( ) -");
        }
        return doctorLoginService.doctorLoginByOpenId(doctorLoginByOpenId);
    }


    @ApiOperation(value = "个人服务号患者注册并登录")
    @PostMapping("/patient/register")
    @SysLog("个人服务号患者注册并登录")
    public R<AuthInfo> patientRegister(@RequestBody @Validated PatientRegister patientRegister) {
        String mobile = patientRegister.getPhone();
        if (StrUtil.isNotEmpty(mobile)) {
            mobile = LoginDecryption.loginDecryption(mobile);
            patientRegister.setPhone(mobile);
        }
        // 校验 account 字段是否包含非法字符
        if (!LoginDecryption.isValidAccount(mobile)) {
            return R.fail("账号不能包含特殊字符：< > & # $ * ( ) -");
        }
        return wxLoginService.patientRegister(patientRegister);
    }


    @ApiOperation(value = "个人服务号患者登录")
    @PostMapping("/patient/login")
    @SysLog("个人服务号患者登录")
    public R<AuthInfo> patientLogin(@RequestBody @Validated PatientRegister patientRegister) {
        String mobile = patientRegister.getPhone();
        if (StrUtil.isNotEmpty(mobile)) {
            mobile = LoginDecryption.loginDecryption(mobile);
            patientRegister.setPhone(mobile);
            // 校验 account 字段是否包含非法字符
            if (!LoginDecryption.isValidAccount(mobile)) {
                return R.fail("账号不能包含特殊字符：< > & # $ * ( ) -");
            }
        }
        return wxLoginService.patientLogin(patientRegister);
    }



    @ApiOperation(value = "医生登录2.4 弃用", notes = "医生登录2.4")
    @Deprecated
    @PostMapping("/doctor/login/2_4")
    @SysLog("医生登录2.4")
    public R<AuthInfo> doctorLoginv_2_4(@RequestBody @Validated DoctorLogin2_4Dto doctorLogin2_4Dto) {
        return doctorLoginService.login2_4(doctorLogin2_4Dto);
    }

    @ApiOperation(value = "医生登录 弃用", notes = "医生登录")
    @PostMapping("/doctor/login")
    @Deprecated
    @SysLog("医生登录")
    public R<AuthInfo> doctorLogin(@RequestBody @Validated DoctorLoginDto doctorLoginDto) {
        return doctorLoginService.login(doctorLoginDto);
    }

    @ApiOperation(value = "医生退出登录", notes = "医生退出登录")
    @GetMapping("/doctor/logout")
    public R<Boolean> doctorLogout(@RequestParam(value = "tenantCode") String tenantCode,
                                    @RequestParam(value = "doctorId") Long doctorId) {
        if (Objects.isNull(tenantCode)) {
            return R.fail("参数错误");
        }
        BaseContextHandler.setTenant(tenantCode);
        doctorLoginService.logout(doctorId);
        return R.success();
    }

    @ApiOperation(value = "小程序手机号登录")
    @PostMapping("/miniAppPhoneLogin")
    @SysLog("小程序手机号登录")
    public R<AuthInfo> miniAppPhoneLogin(@RequestBody @Validated MiniAppPhoneLoginDTO miniAppPhoneLoginDTO) {

        return miniAppLoginService.loginByPhoneNumber(miniAppPhoneLoginDTO);
    }

    @ApiOperation(value = "小程序openId尝试登录")
    @PostMapping("/miniAppOpenIdLogin")
    @SysLog("小程序openId登录")
    public R<AuthInfo> miniAppOpenIdLogin(@RequestBody @Validated MiniAppOpenIdLoginDTO miniAppOpenIdLoginDTO) {

        return miniAppLoginService.loginByOpenId(miniAppOpenIdLoginDTO);

    }


    @ApiOperation(value = "小程序openId注册并登录")
    @PostMapping("/miniAppOpenIdRegisterLogin")
    @SysLog("小程序openId注册并登录")
    public R<AuthInfo> miniAppOpenIdRegisterLogin(@RequestBody @Validated MiniAppOpenIdLoginDTO miniAppOpenIdLoginDTO) {

        return miniAppLoginService.miniAppOpenIdRegisterLogin(miniAppOpenIdLoginDTO);

    }


    @ApiOperation(value = "医生通过微信code登录 弃用", notes = "医生通过微信code登录")
    @GetMapping("/doctor/loginByWxCode")
    @Deprecated
    @SysLog("医生通过微信code登录")
    public R<AuthInfo> doctorLoginByWxCode(@RequestParam(value = "code") String code,
                                           @RequestParam(value = "tenantCode") String tenantCode,
                                           @RequestParam(value = "appId") String appId) {
        if (Objects.isNull(tenantCode)) {
            return R.fail("参数错误");
        }
        BaseContextHandler.setTenant(tenantCode);
        DoctorCodeLoginDTO doctorCodeLoginDTO = new DoctorCodeLoginDTO();
        doctorCodeLoginDTO.setCode(code);
        doctorCodeLoginDTO.setAppId(appId);
        return doctorLoginService.loginByWxCode(doctorCodeLoginDTO);
    }

    @ApiOperation(value = "发送医生登录验证码", notes = "发送医生登录验证码")
    @PostMapping("/doctor/send")
    public R<Boolean> sendSms(@RequestBody @Validated VerificationCodeDTO verificationCodeDTO) {
        return doctorLoginService.send(verificationCodeDTO);
    }

    @ApiOperation(value = "发送客户登录验证码")
    @PostMapping("/admin/send")
    public R<Boolean> adminSendSms(@RequestBody @Validated VerificationCodeDTO verificationCodeDTO) {
        return authManager.send(verificationCodeDTO);
    }


    @ApiOperation(value = "创建AI的token")
    @PostMapping("/create/aiAuth/{userId}")
    public R<JSONObject> createTempToken(@PathVariable("userId") Long userId) {
        AuthInfo tempToken = aiUserTokenService.createTempToken(userId);
        // 把 tempToken 转成 JSONObject 类型
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", tempToken.getToken());
        jsonObject.put("refreshToken", tempToken.getRefreshToken());
        jsonObject.put("expire", tempToken.getExpire());
        jsonObject.put("expiration", tempToken.getExpiration());
        jsonObject.put("refreshExpiration", tempToken.getRefreshExpiration());
        jsonObject.put("userId", userId);
        return R.success(jsonObject);
    }



    @ApiOperation(value = "AI刷新授权")
    @PostMapping("/aiAuthRefreshToken")
    public R<JSONObject> aiAuthRefreshToken(@RequestBody JSONObject jsonObject) {
        String string = jsonObject.getString("userId");
        String refreshToken = jsonObject.getString("refreshToken");
        Claims claims = JwtUtil.parseJWT(refreshToken);
        Long userId = Convert.toLong(claims.get(JWT_KEY_USER_ID));
        if (string.equals(userId.toString())) {
            return createTempToken(userId);
        } else {
            throw new BizException("无效的refreshToken");
        }
    }


}
