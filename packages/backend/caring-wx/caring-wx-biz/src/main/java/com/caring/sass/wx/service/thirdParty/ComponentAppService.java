package com.caring.sass.wx.service.thirdParty;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.dao.config.PreAuthCodeMapper;
import com.caring.sass.wx.entity.config.AppComponentToken;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.PreAuthCode;
import com.caring.sass.wx.service.config.AppComponentTokenService;
import com.caring.sass.wx.service.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jeewx.api.core.common.WxstoreUtils;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.third.JwThirdAPI;
import org.jeewx.api.third.model.ApiGetAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 更新第三方平台的VerifyTicket
 * 更新授权公众号的信息
 * 完成公众号 按照使用授权码换取公众号或小程序的接口调用凭据和授权信息 接口文档 ，调接口获取authorizer_refresh_token
 */
@Slf4j
@Component
public class ComponentAppService {

    @Autowired
    AppComponentTokenService appComponentTokenService;

    @Autowired
    ConfigService configService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    PreAuthCodeMapper preAuthCodeMapper;

    private static String api_get_authorizer_info_url = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=COMPONENT_ACCESS_TOKEN";

    /**
     * 更新微信推送过来的ComponentVerifyTicket
     * 微信每10分钟调用一下此方法
     */
    public void updateComponentVerifyTicket(Element rootElt) {
        String verifyTicket = rootElt.elementText("ComponentVerifyTicket");
        String appId = rootElt.elementText("AppId");
        if (!StringUtils.isEmpty(verifyTicket)) {
            AppComponentToken componentToken = appComponentTokenService.getComponentToken(appId);
            if (Objects.isNull(componentToken)) {
                componentToken = new AppComponentToken();
            }
            componentToken.setComponentVerifyTicket(verifyTicket);
            componentToken.setAppId(appId);
            componentToken.setComponentAppSecret(ApplicationProperties.getThirdPlatformComponentAppSecret());
            if (componentToken.getId() != null) {
                appComponentTokenService.updateById(componentToken);
            } else {
                appComponentTokenService.save(componentToken);
            }
            // 检查 componentAccessToken 是否快要过期。
            // 快过期时，更新componentAccessToken
            appComponentTokenService.checkTokenAndUpdate(componentToken);
        }

    }


    /**
     * @Author yangShuai
     * @Description 公众号取消授权
     * @Date 2020/12/23 13:56
     *
     * @param rootElt
     * @return void
     */
    public void unauthorizedApp(Element rootElt) {
        String authorizerAppid = rootElt.elementText("AuthorizerAppid");
        if (!StringUtils.isEmpty(authorizerAppid)) {
            Config config = configService.getConfigByAppId(authorizerAppid);
            if (Objects.nonNull(config) && ( config.getAuthType() == null || config.getAuthType().equals(1))) {
                BaseContextHandler.setTenant(config.getTenantCode());
                configService.removeById(config.getId());

                // 取消项目和公众号的绑定关系
                tenantApi.clearWxAppId(authorizerAppid);
                WxMpServiceHolder.delete(authorizerAppid);
            }
        }
    }

    /**
     * 获取授权方的帐号基本信息
     * 该 API 用于获取授权方的基本信息，包括头像、昵称、帐号类型、认证类型、微信号、原始ID和二维码图片URL。
     * @param apiGetAuthorizer
     * @param component_access_token
     * @return
     */
    public static JSONObject apiGetAuthorizerInfo(ApiGetAuthorizer apiGetAuthorizer, String component_access_token) {
        String requestUrl = api_get_authorizer_info_url.replace("COMPONENT_ACCESS_TOKEN", component_access_token);
        JSONObject param = JSONObject.fromObject(apiGetAuthorizer);
        JSONObject result = WxstoreUtils.httpRequest(requestUrl, "POST", param.toString());
        JSONObject authorizerInfo = result.getJSONObject("authorizer_info");
        return authorizerInfo;
    }

    @Autowired
    FileUploadApi fileUploadApi;

    /**
     * @Author yangShuai
     * @Description 用户授权后。 拉取公众号的  授权令牌
     * @Date 2020/12/23 15:35
     * @return void
     */
    public boolean getAppAuthInfo(Config config) {
        boolean saveConfigSuccess = false;
        try {
            String componentAccessToken = appComponentTokenService.getComponentAccessToken(config.getComponentAppId());
            JSONObject authorizationInfoJson = JwThirdAPI.getApiQueryAuthInfo(config.getComponentAppId(),
                    config.getAuthorizationCode(), componentAccessToken);
            ApiGetAuthorizer apiGetAuthorizer = new ApiGetAuthorizer();
            apiGetAuthorizer.setComponent_appid(config.getComponentAppId());
            apiGetAuthorizer.setAuthorizer_appid(config.getAppId());

            //获取授权方的帐号基本信息
            //该 API 用于获取授权方的基本信息，包括头像、昵称、帐号类型、认证类型、微信号、原始ID和二维码图片URL。
            JSONObject authorizerInfo = apiGetAuthorizerInfo(apiGetAuthorizer, componentAccessToken);
            if (Objects.nonNull(authorizerInfo)) {
                Object nickName = authorizerInfo.get("nick_name");
                if (Objects.nonNull(nickName)) {
                    config.setName(nickName.toString());
                }
                Object userName = authorizerInfo.get("user_name");
                if (Objects.nonNull(userName)) {
                    config.setSourceId(userName.toString());
                }
                String qrcode_url = authorizerInfo.getString("qrcode_url");
                if (StrUtil.isNotBlank(qrcode_url)) {
                    try {
                        String string = FileUtils.downLoadFromUrl(qrcode_url, "qrcode_url" + config.getAppId(), null);
                        if (StrUtil.isNotBlank(string)) {
                            File file = new File(string);
                            MockMultipartFile multipartFile = FileUtils.fileToFileItem(file);
                            R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(1L, multipartFile);
                            com.caring.sass.file.entity.File data = upload.getData();
                            config.setWxQrCodeUrl(data.getUrl());
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    } catch (IOException e) {

                    }
                }
            }
            if (Objects.nonNull(authorizationInfoJson)) {
                JSONObject authorizationInfo = authorizationInfoJson.getJSONObject("authorization_info");
                String authorizerAccessToken = authorizationInfo.getString("authorizer_access_token");
                long expiresIn = authorizationInfo.getLong("expires_in");
                String authorizerRefreshToken = authorizationInfo.getString("authorizer_refresh_token");
                JSONArray funcInfo = authorizationInfo.getJSONArray("func_info");
                config.setAuthorizerAccessToken(authorizerAccessToken);
                config.setExpiresIn(expiresIn);
                config.setAuthorizerRefreshToken(authorizerRefreshToken);
                config.setFuncInfoJson(funcInfo.toString());
                config.setAccessTokenCreateTime(System.currentTimeMillis());
                if (Objects.isNull(config.getId())) {
                    configService.save(config);
                } else {
                    configService.updateById(config);
                }
                saveConfigSuccess = true;
            }
        } catch (WexinReqException e) {
            log.error("ComponentAppService.getAppAuthInfo__error, {}", e.getMessage());
        }

        return saveConfigSuccess;
    }

    /**
     * @Author yangShuai
     * @Description 公众号授权 或者小程序授权
     * @Date 2020/12/23 13:56
     *
     * @return void
     */
    @Async
    public void authorizedApp(Element rootElt) {
        String componentAppId = rootElt.elementText("AppId");
        String createTime = rootElt.elementText("CreateTime");
        Long authorizeCreateTime = null;
        if (!StringUtils.isEmpty(createTime)) {
            authorizeCreateTime = Long.parseLong(createTime);
        }
        String authorizerAppId = rootElt.elementText("AuthorizerAppid");
        String authorizationCode = rootElt.elementText("AuthorizationCode");
        String preAuthCode = rootElt.elementText("PreAuthCode");
        String authorizationCodeExpiredTime = rootElt.elementText("AuthorizationCodeExpiredTime");
        Long authorizationCodeExpiredTimeLong = null;
        if (!StringUtils.isEmpty(authorizationCodeExpiredTime)) {
            authorizationCodeExpiredTimeLong = Long.parseLong(authorizationCodeExpiredTime);
        }
        List<PreAuthCode> preAuthCodes = preAuthCodeMapper.selectList(Wraps.<PreAuthCode>lbQ()
                .eq(PreAuthCode::getPreAuthCode, preAuthCode));
        String tenantCode = null;
        if (CollUtil.isNotEmpty(preAuthCodes)) {
            PreAuthCode authCode = preAuthCodes.get(0);
            tenantCode = authCode.getTenantCode();
            BaseContextHandler.setTenant(tenantCode);

            // 一个项目目前只需要绑定一个公众号。 确定这个公众号是否已经绑定到其他项目上。
            if (authCode.getAuthType().equals(1)) {
                R<List<Tenant>> tenantListReturn = tenantApi.checkWxAppIdStatus(authorizerAppId, tenantCode);
                Boolean isSuccess = tenantListReturn.getIsSuccess();
                // 项目信息查询失败。直接返回。
                if (isSuccess != null && isSuccess) {
                    List<Tenant> tenants = tenantListReturn.getData();
                    if (CollUtil.isNotEmpty(tenants)) {
                        List<String> tenantNameList = tenants.stream().map(Tenant::getName).collect(Collectors.toList());
                        String tenantName = String.join(",", tenantNameList);
                        Tenant tenant = Tenant.builder()
                                .wxAppId("")
                                .wxStatus(Tenant.WX_STATUS_NO)
                                .wxBindErrorMessage("授权公众号已经被" + tenantName + "使用")
                                .build();
                        tenantApi.updateWxStatus(tenant);
                    } else {
                        // 当前公众号没有绑定到系统项目下。可以进行绑定
                        Config config = configService.getConfigByTenant();
                        if (Objects.isNull(config)) {
                            config = new Config();
                        }
                        config.setAuthType(1);
                        boolean saveConfigSuccess = createOrUpdateConfig(config, authorizeCreateTime, authorizerAppId, authorizationCode, preAuthCode, componentAppId, authorizationCodeExpiredTimeLong);
                        Tenant.TenantBuilder tenantBuilder = Tenant.builder()
                                .code(tenantCode);
                        if (saveConfigSuccess) {
                            tenantBuilder.wxStatus(Tenant.WX_STATUS_YES);
                            tenantBuilder.wxAppId(config.getAppId());
                            tenantBuilder.wxName(config.getName());
                        } else {
                            tenantBuilder.wxStatus(Tenant.WX_STATUS_NO);
                            tenantBuilder.wxName(config.getName());
                            tenantBuilder.wxBindErrorMessage("绑定公众号异常，请联系工程师");
                        }
                        tenantApi.updateWxStatus(tenantBuilder.build());
                    }
                }
            } else if (authCode.getAuthType().equals(2)) {
                // 是扫了小程序的授权码 得到的消息
                Config config = configService.getConfigByAppId(authorizerAppId);
                if (config == null) {
                    config = new Config();
                }
                config.setAuthType(2);
                createOrUpdateConfig(config, authorizeCreateTime, authorizerAppId, authorizationCode, preAuthCode, componentAppId, authorizationCodeExpiredTimeLong);
            }
        }
    }

    /**
     * 公众号或者小程序授权之后。创建config或者更新。他的信息。
     * @param config
     * @param authorizeCreateTime
     * @param authorizerAppId
     * @param authorizationCode
     * @param preAuthCode
     * @param componentAppId
     * @param authorizationCodeExpiredTimeLong
     * @return
     */
    public boolean createOrUpdateConfig(Config config, Long authorizeCreateTime, String authorizerAppId, String authorizationCode, String preAuthCode, String componentAppId, Long authorizationCodeExpiredTimeLong) {
        config.setAuthStatus("authorized");
        config.setThirdCreateTime(authorizeCreateTime);
        config.setAppId(authorizerAppId);
        config.setAuthorizationCode(authorizationCode);
        config.setPreAuthCode(preAuthCode);
        config.setComponentAppId(componentAppId);
        config.setAuthorizationCodeExpiredTime(authorizationCodeExpiredTimeLong);
        config.setThirdAuthorization(true);
        config.setAuthType(1);
        // 拉取公众号的授权和公众号的其他信息
        return getAppAuthInfo(config);
    }

    // xml 已经解密
    public void parsingXmlInfoType(Document doc) {
        Element rootElt = doc.getRootElement();
        String infoType = rootElt.elementText("InfoType");
        if (!StringUtils.isEmpty(infoType)) {
            switch (infoType) {
                // 更新第三方平台的 ticket
                case "component_verify_ticket":
                    updateComponentVerifyTicket(rootElt);
                    break;
                // 取消授权
                case "unauthorized":
                    unauthorizedApp(rootElt);
                    break;
                // 确认授权
                case "authorized":
                    authorizedApp(rootElt);
                    break;
                default:
            }
        }
    }


}
