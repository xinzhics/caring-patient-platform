package com.caring.sass.wx.service.authLogin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.wx.service.config.AppComponentTokenService;
import com.caring.sass.wx.wxUtil.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @ClassName WxUserAuthLoginServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/1/7 10:44
 * @Version 1.0
 */
@Slf4j
@Service
public class WxUserAuthLoginServiceImpl {

    @Autowired
    OauthApi oauthApi;

    @Autowired
    AppComponentTokenService appComponentTokenService;

    /**
     * 第三方平台代公众号网页授权
     * 通过 code 换取 access_token
     */
    private static final String GETACCESS_TOKENBYCODE = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=APPID&code=CODE&grant_type=authorization_code&component_appid=COMPONENT_APPID&component_access_token=COMPONENT_ACCESS_TOKEN";


    /**
     * @Author yangShuai
     * @Description 使用第三方平台的参数，来获取用户的 accessToken 和 openId
     * @Date 2021/1/7 11:50
     *
     * @param code
     * @return me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken
     */
    public WxOAuth2AccessToken getAccessToken(String code, String appId) {
        String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();
        String componentAccessToken = appComponentTokenService.getComponentAccessToken(componentAppId);

        String getAccessTokenByCode = GETACCESS_TOKENBYCODE
                .replace("COMPONENT_APPID", componentAppId)
                .replace("APPID",  appId)
                .replace("CODE", code)
                .replace("COMPONENT_ACCESS_TOKEN", componentAccessToken);
        log.error("getAccess_tokenByCode: {}", getAccessTokenByCode);

        String s = HttpUtils.get(getAccessTokenByCode);
        if (s != null && s.contains("errcode")) {
            log.error("获取用户的accessToken失败, 返回异常信息{}" , s);
        } else {
            return WxOAuth2AccessToken.fromJson(s);
        }
        return null;
    }

    /**
     * 使用微信用户基本信息，获取用户的系统授权
     * @param wxOAuth2UserInfo
     * @param userRole
     * @param groupId
     * @param resp
     * @return
     */
    public void getWxUserAuth(WxOAuth2UserInfo wxOAuth2UserInfo, String userRole, String domain, Long groupId, String redirectPath, String role, HttpServletResponse resp, Boolean thirdAuthorization) {

        String jsonString = JSON.toJSONString(wxOAuth2UserInfo);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        R<String> userLogin = oauthApi.wxUserLogin(jsonObject, userRole, groupId);
        if (userLogin.getIsSuccess() != null && userLogin.getIsSuccess()) {
            String loginData = userLogin.getData();
            JSONObject authToken = JSON.parseObject(loginData);
            if (UserType.CONSULTATION_GUEST.equals(userRole) && UserType.PATIENT.equals(authToken.get("userType"))) {
                // 路径是去病例讨论。但是授权发现openId是个患者。
                // 那么修改重定向的路径去患者端的病例讨论
                redirectPath = ApplicationDomainUtil.wxPatientBizUrl(domain, thirdAuthorization, String.format(H5Router.CONSULTATION_NOTICE, groupId));
            }
            String domainRedirectUrl = getDomainRedirectUrl(redirectPath, authToken);
            log.info("重定向的地址 {}", domainRedirectUrl);
            try {
                if (groupId != null) {
                    resp.sendRedirect(domainRedirectUrl + "&groupId=" + groupId);
                } else {
                    resp.sendRedirect(domainRedirectUrl);
                }
            } catch (Exception e) {
                log.error("getWxUserAuth sendRedirect error {}", domainRedirectUrl);
            }
        } else {
            int code = userLogin.getCode();
            if (ExceptionCode.OPENID_USER_NOT_FOUND.getCode() == code) {
                try {
                    patientNotFont(resp, domain, thirdAuthorization, userRole);
                } catch (IOException e) {
                    log.error("getWxUserAuth patientNotFont error openId: {}", wxOAuth2UserInfo.getOpenid());
                }
            }
            if (ExceptionCode.DOCTOR_NEED_LOGIN.getCode() == code) {
                // 医生需要去登录
                try {
                    resp.sendRedirect(getDoctorLoginUrl(redirectPath, wxOAuth2UserInfo, role));
                } catch (IOException e) {
                    log.error("getWxUserAuth sendRedirect error {}", redirectPath);
                }
            }
        }
    }



    /**
     * 拿到授权后。， 跳转到前端要求的页面，并携带token和userId
     * userId和token 必须保持 userId=XXX&token=XXX 否则前端可能异常
     * @param redirectPath
     * @param wxUserAuth
     * @return
     */
    private String getDomainRedirectUrl(String redirectPath, JSONObject wxUserAuth) {

        if (Objects.isNull(wxUserAuth)) {
            return redirectPath;
        }
        if (redirectPath.contains("?")) {
            return redirectPath + "&userId=" + wxUserAuth.get("userId")
                    + "&token=" + wxUserAuth.get("token");
        } else {
            return redirectPath + "?userId=" + wxUserAuth.get("userId")
                    + "&token=" + wxUserAuth.get("token");
        }
    }


    /**
     * 使用openID没有找到患者信息
     */
    private void patientNotFont(HttpServletResponse resp, String domain, Boolean thirdAuthorization, String userRole) throws IOException {
        if (UserType.UCENTER_PATIENT.equals(userRole)) {
            // 重定向到患者的授权失败页面
            String url = ApplicationDomainUtil.wxPatientBizUrl(domain, thirdAuthorization, H5Router.PATIENT_NOT_FIND );
            resp.sendRedirect(url);
        }

    }


    /**
     * 授权失败，需要医生去登录页面完成 openId 和 医生绑定
     * @param redirectPath
     * @param userInfo
     * @return
     */
    public String getDoctorLoginUrl(String redirectPath, WxOAuth2UserInfo userInfo, String role) {
        if (redirectPath.contains("?")) {
            if (!redirectPath.contains("openId")) {
                redirectPath = redirectPath + "&openId=" + userInfo.getOpenid();
            }
            if (StringUtils.isNotEmptyString(role)) {
                redirectPath = redirectPath + "&role=" + role;
            }
            return redirectPath + "&doctorLogin=true";
        } else {
            redirectPath = redirectPath+ "?openId=" + userInfo.getOpenid() + "&doctorLogin=true";
            if (StringUtils.isNotEmptyString(role)) {
                redirectPath = redirectPath + "&role=" + role;
            }
            return redirectPath;
        }
    }

}
