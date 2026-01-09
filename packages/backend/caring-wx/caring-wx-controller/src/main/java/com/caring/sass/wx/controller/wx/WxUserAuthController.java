package com.caring.sass.wx.controller.wx;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.dao.config.ApiErrorMapper;
import com.caring.sass.wx.entity.config.ApiError;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import com.caring.sass.wx.service.authLogin.WxUserAuthLoginServiceImpl;
import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.config.WeiXinUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName WxUserAuthController
 * @Description
 * @Author yangShuai
 * @Date 2022/1/7 9:41
 * @Version 1.0
 */
@Slf4j
@Api(value = "AppComponentToken", tags = "微信第三方平台")
@RestController
@RequestMapping("/wxUserAuth/anno")
public class WxUserAuthController {


    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("change-patient-info-", false);

    private static final ExecutorService CHANGE_PATIENT_INFO_EXECUTOR = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(300), THREAD_FACTORY);


    /**
     * 旧公众号使用的 获取code的授权路径
     */
    private static String CALLBACK_USERINFO_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    /**
     * 第三方平台 公众号使用 获取code 的授权路径
     */
    private static String AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&component_appid=%s#wechat_redirect";

    @Autowired
    TenantApi tenantApi;

    @Autowired
    ConfigService configService;

    @Autowired
    PatientApi patientApi;

    @Autowired
    WxUserAuthLoginServiceImpl wxUserAuthLoginService;

    @Autowired
    WeiXinUserInfoService weiXinUserInfoService;

    @Autowired
    WXCallBackApi wxCallBackApi;

    @Autowired
    ApiErrorMapper apiErrorMapper;
    /**
     * 获取微信用户的code
     * @param req
     * @param resp
     * @param domain 前端域名
     * @param redirectUri 要返回的前端的路径
     * @param groupId
     */
    @ApiOperation("获取微信用户的code")
    @GetMapping("getWxUserCode")
    public void getWxUserCode(
            HttpServletRequest req, HttpServletResponse resp,
            @RequestParam("domain") String domain,
            @RequestParam("redirectUri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "onlyNeedCode", required = false) Boolean onlyNeedCode,
            @RequestParam(value = "onlyNeedOpenId", required = false) Boolean onlyNeedOpenId,
            @RequestParam(value = "groupId", required = false) Long groupId) {
        log.info("第三方授权公众号 getWxUserCode, {} ,{}， {}", domain, redirectUri, groupId);
        // 请求的角色
        String role = req.getParameter("role");
        R<Tenant> apiByDomain = tenantApi.getAppIdAndCodeByDomain(domain);
        if (apiByDomain.getIsSuccess() != null && apiByDomain.getIsSuccess()) {
            Tenant data = apiByDomain.getData();
            BaseContextHandler.setTenant(data.getCode());
            String wxAppId = data.getWxAppId();
            if (StrUtil.isEmpty(wxAppId)) {
                return;
            }
            String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();
            String apiUrl = null;
            String state = "1";
            if (StrUtil.isEmpty(scope)) {
                scope = "snsapi_userinfo";
            }
            // 在页面路径中添加进租户信息
            if (Objects.nonNull(data.getWxBindTime())) {
                apiUrl = ApplicationDomainUtil.apiUrl() + "/api/wx/wxUserAuth/anno/thirdRedirectCode?" +
                        "tenantCode=" + data.getCode() + "&domain=" + domain + "&wxAppId=" + wxAppId;
            } else {
                apiUrl = ApplicationDomainUtil.tenantDomain(domain) + "/api/wx/wxUserAuth/anno/redirectCode?" +
                        "tenantCode=" + data.getCode()  + "&domain=" + domain + "&wxAppId=" + wxAppId;
            }
            if (onlyNeedCode != null && onlyNeedCode) {
                // 只需要获取到code
                apiUrl = apiUrl + "&onlyNeedCode=" + "1";
            }
            if (onlyNeedOpenId != null && onlyNeedOpenId) {
                apiUrl = apiUrl + "&onlyNeedOpenId=" + "1";
            }
            if (StringUtils.isNotEmptyString(role)) {
                apiUrl = apiUrl + "&role=" + role;
            }
            if (groupId != null) {
                apiUrl = apiUrl + "&groupId=" + groupId;
            }
            try {
                redirectUri = URLEncoder.encode(redirectUri, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            apiUrl = apiUrl + "&redirectSaasUrl=" + redirectUri;
            if (Objects.nonNull(data.getWxBindTime())) {

                String encode = null;
                try {
                    encode = URLEncoder.encode(apiUrl, "utf-8");
                    String wechatRedirectUrl = String.format(AUTHORIZE, wxAppId, encode, scope, state, componentAppId);
                    resp.sendRedirect(wechatRedirectUrl);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 这里需要nginx帮忙吧请求转发到后端服务来
                String encode = null;
                try {
                    encode = URLEncoder.encode(apiUrl, "utf-8");

                    String wechatRedirectUrl = String.format(CALLBACK_USERINFO_URL, wxAppId, encode, scope, state);
                    resp.sendRedirect(wechatRedirectUrl);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new BizException("服务繁忙");
        }
    }

    @ApiOperation("知识库微信静默授权回来携带code和state")
    @GetMapping("thirdRedirectCodeOpenId")
    public void thirdRedirectCodeOpenId(HttpServletRequest req, HttpServletResponse resp) {
        String code = req.getParameter("code");
        String redirectSaasUrl = req.getParameter("redirectSaasUrl");
        String appId = req.getParameter("wxAppId");
        // 使用第三方的方式获取用户的accessToken
        WxOAuth2AccessToken accessToken = wxUserAuthLoginService.getAccessToken(code, appId);
        String redirectPath;
        if (redirectSaasUrl.contains("?")) {
            redirectPath = redirectSaasUrl + "&openId=" + accessToken.getOpenId() + "&appId=" + appId;
        } else {
            redirectPath = redirectSaasUrl + "?openId=" + accessToken.getOpenId() + "&appId=" + appId;
        }
        try {
            resp.sendRedirect(redirectPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @ApiOperation("患者端 医生端 第三方授权公众号重定向回来携带code和state")
    @GetMapping("thirdRedirectCode")
    public void thirdRedirectCode(HttpServletRequest req, HttpServletResponse resp) {
        String code = req.getParameter("code");

        String tenantCode = req.getParameter("tenantCode");
        String domain = req.getParameter("domain");

        String groupId = req.getParameter("groupId");
        String role = req.getParameter("role");
        String onlyNeedCode = req.getParameter("onlyNeedCode");
        String onlyNeedOpenId = req.getParameter("onlyNeedOpenId");
        // 想要前往的前端路径 redirectSaasUrl
        String redirectSaasUrl = req.getParameter("redirectSaasUrl");
        String appId = req.getParameter("wxAppId");
        log.info("第三方授权公众号重定向回来携带code {}, tenantCode {} ,groupId {}, role {}, state {}, appid {}, onlyNeedOpenId: {}", code, tenantCode, groupId, role, redirectSaasUrl, appId, onlyNeedOpenId);
        BaseContextHandler.setTenant(tenantCode);
        if (StringUtils.isEmpty(code)) {
            // 用户拒绝授权
            return;
        }
        if (StrUtil.isNotEmpty(onlyNeedCode) && onlyNeedCode.equals("1")) {
            // 重定向回前端。并吧code带上
            try {
                resp.sendRedirect(redirectUrlJointCode(redirectSaasUrl, code, tenantCode, appId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 使用第三方的方式获取用户的accessToken
        WxOAuth2AccessToken accessToken = wxUserAuthLoginService.getAccessToken(code, appId);

        if (StrUtil.isNotEmpty(onlyNeedOpenId) && onlyNeedOpenId.equals("1")) {
            // 重定向回前端。并吧code带上
            String openId = accessToken.getOpenId();
            WxOAuth2UserInfo userInfo = new WxOAuth2UserInfo();
            userInfo.setUnionId(accessToken.getUnionId());
            userInfo.setOpenid(openId);
            weiXinUserInfoService.asyncSaveWeiXinUserInfo(userInfo);
            try {
                resp.sendRedirect(redirectUrlJointOpenId(redirectSaasUrl, openId, tenantCode, appId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 使用第三方的方式获取用户的基本信息
        WxMpService wxMpService = WxMpServiceHolder.getWxMpService(appId);
        WxOAuth2Service oAuth2Service = wxMpService.getOAuth2Service();
        try {
            WxOAuth2UserInfo userInfo = oAuth2Service.getUserInfo(accessToken, "zh_CN");
            // 如果这个授权用户是患者。患者未入组时，可以尝试患者的信息更新成微信信息
            String userRole = getUserRole(redirectSaasUrl);

            // 判断是否是前往医生的申请提交信心页面
            if (doctorApply(redirectSaasUrl)) {
                redirectDoctorApply(resp, redirectSaasUrl, userInfo);
                return;
            }
            if (UserType.UCENTER_PATIENT.equals(userRole)) {
                CHANGE_PATIENT_INFO_EXECUTOR.execute(() -> tryChangePatientInfo(userInfo, tenantCode));
            }
            authToken(resp, redirectSaasUrl, userInfo, role, groupId, domain, true);
        } catch (WxErrorException wxErrorException) {
            wxErrorException.printStackTrace();
        }
    }

    /**
     * 拼接openId
     * @param redirectSaasUrl
     * @param openId
     * @param tenantCode
     * @param appId
     * @return
     */
    private String redirectUrlJointOpenId(String redirectSaasUrl, String openId, String tenantCode, String appId) {
        if (redirectSaasUrl.contains("?")) {
            return redirectSaasUrl + "&wxOpenId=" + openId + "&tenantCode=" + tenantCode + "&wxAppId=" + appId;
        } else {
            return redirectSaasUrl + "?wxOpenId=" + openId + "&tenantCode=" + tenantCode + "&wxAppId=" + appId;
        }
    }

    /**
     * 给前端路径拼接code
     * @param redirectSaasUrl
     * @param code
     * @return
     */
    private String redirectUrlJointCode(String redirectSaasUrl, String code, String tenantCode, String appId) {
        if (redirectSaasUrl.contains("?")) {
            return redirectSaasUrl + "&wxCode=" + code + "&tenantCode=" + tenantCode + "&wxAppId=" + appId;
        } else {
            return redirectSaasUrl + "?wxCode=" + code + "&tenantCode=" + tenantCode + "&wxAppId=" + appId;
        }
    }

    @ApiOperation("患者端 医生端 普通公众号微信重定向回来携带code和state")
    @GetMapping("redirectCode")
    public void redirectCode(HttpServletRequest req, HttpServletResponse resp) {
        String code = req.getParameter("code");
        String tenantCode = req.getParameter("tenantCode");
        String domain = req.getParameter("domain");
        String groupId = req.getParameter("groupId");
        String role = req.getParameter("role");
        // 这个state里面拼接了租户信息
        String redirectSaasUrl = req.getParameter("redirectSaasUrl");
        String onlyNeedCode = req.getParameter("onlyNeedCode");
        String onlyNeedOpenId = req.getParameter("onlyNeedOpenId");
        String appId = req.getParameter("wxAppId");
        log.info("普通公众号微信重定向回来携带code {} ,state {}, role {}, groupId {} ,tenantCode {}", code, redirectSaasUrl, role, groupId, tenantCode);
        if (StrUtil.isNotEmpty(onlyNeedCode) && onlyNeedCode.equals("1")) {
            // 重定向回前端。并吧code带上
            try {
                resp.sendRedirect(redirectUrlJointCode(redirectSaasUrl, code, tenantCode, appId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        BaseContextHandler.setTenant(tenantCode);

        if (StrUtil.isNotEmpty(onlyNeedOpenId) && onlyNeedOpenId.equals("1")) {
            // 重定向回前端。并吧code带上
            WxOAuth2Service oAuth2Service = WxMpServiceHolder.getWxMpService(appId).getOAuth2Service();
            String openId = "";
            try {
                WxOAuth2AccessToken accessToken = oAuth2Service.getAccessToken(code);
                openId = accessToken.getOpenId();
                WxOAuth2UserInfo userInfo = new WxOAuth2UserInfo();
                userInfo.setUnionId(accessToken.getUnionId());
                userInfo.setOpenid(openId);
                weiXinUserInfoService.asyncSaveWeiXinUserInfo(userInfo);

                resp.sendRedirect(redirectUrlJointOpenId(redirectSaasUrl, openId, tenantCode, appId));
            } catch (WxErrorException e) {
                apiErrorMapper.insert(ApiError.builder().wxAppId(appId).wxErrorMessage(e.getMessage()).build());
            } catch (IOException e) {
                log.error("Redirect error: "+ openId);
            }
            return;
        }

        WxOAuth2UserInfo wxOAuth2UserInfo = configService.getWxUser(code);
        String userRole = getUserRole(redirectSaasUrl);
        if (doctorApply(redirectSaasUrl)) {
            redirectDoctorApply(resp, redirectSaasUrl, wxOAuth2UserInfo);
            return;
        }
        if (UserType.UCENTER_PATIENT.equals(userRole)) {
            CHANGE_PATIENT_INFO_EXECUTOR.execute(() -> tryChangePatientInfo(wxOAuth2UserInfo, tenantCode));
        }
        authToken(resp, redirectSaasUrl, wxOAuth2UserInfo, role, groupId, domain, false);

    }

    /**
     * 异步更新一下
     * @param wxOAuth2UserInfo
     * @param tenantCode
     */
    public void tryChangePatientInfo(WxOAuth2UserInfo wxOAuth2UserInfo, String tenantCode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nickname", wxOAuth2UserInfo.getNickname());
        jsonObject.put("openid", wxOAuth2UserInfo.getOpenid());
        jsonObject.put("headImgUrl", wxOAuth2UserInfo.getHeadImgUrl());
        BaseContextHandler.setTenant(tenantCode);
        patientApi.updateByOpenId(jsonObject);
    }




    /**
     * 更新微信的信息。 完成本系统的授权
     * @param resp
     * @param redirectPath
     * @param wxUser
     */
    public void authToken(HttpServletResponse resp, String redirectPath, WxOAuth2UserInfo wxUser, String role, String groupId, String domain, Boolean thirdAuthorization) {
        Long groupIdLong = null;
        if (StringUtils.isNotEmptyString(groupId)) {
            groupIdLong = Long.parseLong(groupId);
        }
        weiXinUserInfoService.asyncSaveWeiXinUserInfo(wxUser);
        // 根据要前往的前端页面路径，判断授权微信的角色身份是患者还是医生或者是会诊人员
        String userRole = getUserRole(redirectPath);
        List<String> stringList = new ArrayList<>();
        stringList.add("openId=" + wxUser.getOpenid());
        if (StrUtil.isNotEmpty(wxUser.getUnionId())) {
            stringList.add("unionId=" + wxUser.getUnionId());
        }
        String[] strings = stringList.toArray(new String[]{});
        if (UserType.TOURISTS.equals(userRole) || UserType.UCENTER_PATIENT.equals(userRole)) {
            // 检测 用户是否已经成为医生 或 患者 或已经选择成为医生或患者
            // 当已经成为医生 或 患者
            // 完成医生 患者授权，并重定向到医生 患者首页。
            WxSubscribeDto subscribeDto = new WxSubscribeDto();
            subscribeDto.setOpenId(wxUser.getOpenid());
            R<String> userExist = wxCallBackApi.wxUserExist(subscribeDto);
            if (userExist.getIsSuccess() != null && userExist.getIsSuccess()) {
                String userType = userExist.getData();
                if (UserType.UCENTER_PATIENT.equals(userType)) {
                    // 修改重定向路径 是患者首页。 并继续授权
                    // 判断如果本身就要进入到患者端，而且他本身就是一个患者。那么路径不重新定义
                    if (UserType.TOURISTS.equals(userRole)) {
                        redirectPath = ApplicationDomainUtil.wxPatientBizUrl(domain, thirdAuthorization, "home",
                                strings);;
                    }
                    wxUserAuthLoginService.getWxUserAuth(wxUser, UserType.UCENTER_PATIENT, domain, groupIdLong, redirectPath, role, resp, thirdAuthorization);
                    return;
                } else if (UserType.UCENTER_DOCTOR.equals(userType)) {
                    // 医生已经完成账号和openid绑定 修改重定向路径是医生首页， 并继续授权
                    redirectPath = ApplicationDomainUtil.wxDoctorBizUrl(domain, thirdAuthorization, "index");
                    wxUserAuthLoginService.getWxUserAuth(wxUser, UserType.UCENTER_DOCTOR, domain, groupIdLong, redirectPath, role, resp, thirdAuthorization);
                    return;
                }
            }

            // 查询openId 是否选择了 身份
            WeiXinUserInfo xinUserInfo = weiXinUserInfoService.getByOpenId(wxUser.getOpenid());
            if (Objects.isNull(xinUserInfo) || StrUtil.isEmpty(xinUserInfo.getUserRole())) {
                // 说明没有选择身份
                // 直接重定向到 选择身份页面
                try {
                    String url = ApplicationDomainUtil.wxPatientBizUrl(domain, thirdAuthorization, H5Router.H5_RULE_SELECT,
                            strings);
                    resp.sendRedirect(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } else {
                // 已经 选择 成为医生或患者。
                if (UserType.UCENTER_PATIENT.equals(xinUserInfo.getUserRole())) {
                    // 已经选择成为患者，但是患者信息又不存在。可能是患者被删除。
                    redirectPath = ApplicationDomainUtil.wxPatientBizUrl(domain, thirdAuthorization, "home");
                    wxUserAuthLoginService.getWxUserAuth(wxUser, UserType.UCENTER_PATIENT, domain, groupIdLong, redirectPath, role, resp, thirdAuthorization);
                    return;
                } else {
                    String url = ApplicationDomainUtil.wxDoctorBizUrl(domain, thirdAuthorization, null);
                    String doctorLoginUrl = wxUserAuthLoginService.getDoctorLoginUrl(url, wxUser, role);
                    log.info("redirect path to :{}", doctorLoginUrl);
                    try {
                        resp.sendRedirect(doctorLoginUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
        wxUserAuthLoginService.getWxUserAuth(wxUser, userRole, domain, groupIdLong, redirectPath, role, resp, thirdAuthorization);


    }

    /**
     * 重定向到 医生的申请 页面
     * @param redirectPath
     * @param wxUser
     */
    private void redirectDoctorApply(HttpServletResponse resp, String redirectPath, WxOAuth2UserInfo wxUser) {
        weiXinUserInfoService.asyncSaveWeiXinUserInfo(wxUser);
        if (redirectPath.contains("?")) {
            redirectPath = redirectPath + "&headImgUrl=" + wxUser.getHeadImgUrl() + "&openId=" + wxUser.getOpenid();
        } else {
            redirectPath = redirectPath + "?headImgUrl=" + wxUser.getHeadImgUrl() + "&openId=" + wxUser.getOpenid();
        }
        try {
            resp.sendRedirect(redirectPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断当前进入的路径是否医生申请表单页面
     * @param redirectPath
     * @return
     */
    private boolean doctorApply(String redirectPath) {
        String domainName = ApplicationProperties.getDomainName();
        String mainDomainName = ApplicationProperties.getMainDomainName();
        if (redirectPath.contains(domainName + "/doctor") || redirectPath.contains(mainDomainName + "/doctor")) {
            if (redirectPath.contains("saasDoctorApply")) {
                return true;
            }
        }
        return false;
    }

    private String getUserRole(String redirectPath) {
        String domainName = ApplicationProperties.getDomainName();
        String mainDomainName = ApplicationProperties.getMainDomainName();
        if (redirectPath.contains(domainName + "/wx/" + H5Router.H5_RULE_SELECT) || redirectPath.contains(mainDomainName + "/wx/" + H5Router.H5_RULE_SELECT)) {
            return UserType.TOURISTS;
        } else if (redirectPath.contains(domainName + "/wx") || redirectPath.contains(mainDomainName + "/wx")) {
            return UserType.UCENTER_PATIENT;
        } else if (redirectPath.contains(domainName + "/doctor") || redirectPath.contains(mainDomainName + "/doctor")) {
            return UserType.UCENTER_DOCTOR;
        } else {
            return UserType.CONSULTATION_GUEST;
        }
    }


}
