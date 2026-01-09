package com.caring.sass.wx.controller.menu;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.constant.MenuType;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import com.caring.sass.wx.entity.menu.SysMenu;
import com.caring.sass.wx.service.authLogin.WxUserAuthLoginServiceImpl;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequestMapping("/router")
@Api(value = "WxH5Router", tags = "个人服务号H5页面路由")
public class WxH5RouterController {

    @Autowired
    TenantApi tenantApi;
    /**
     * 第三方平台 公众号使用 获取code 的授权路径
     */
    private static String AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&component_appid=%s#wechat_redirect";


    /**
     * 个人服务号的路由设置
     *
     * 用户点击服务号的菜单，或推送的链接
     *
     * @param tenantCode
     * @param menuType
     * @return
     */
    @ApiOperation("获取系统菜单")
    @GetMapping("/anno/{tenantCode}/{menuType}")
    public void getMenu(
            HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("tenantCode") String tenantCode,
            @PathVariable("menuType") String menuType) {

        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        if (tenantR.getIsSuccess() != null && tenantR.getIsSuccess()) {
            Tenant data = tenantR.getData();
            String wxAppId = data.getWxAppId();
            if (StrUtil.isEmpty(wxAppId)) {
                throw new BizException("项目未绑定公众号");
            }
            String dataDomainName = data.getDomainName();

            String redirectSaasUrl = "";
            // 使用第三方的方式获取用户的accessToken
            // 如果用户要求重新选项身份， 那么去患者端的选择身份页面。并携带openId
            // 患者端使用openId重新设置身份后， 进行校验登录，或注册。
            if (MenuType.SELECT_ROLE.eq(menuType)) {
                redirectSaasUrl = ApplicationDomainUtil.wxPatientBizUrl(dataDomainName, true, MenuType.SELECT_ROLE.getPath());
            }
            String redirected = redirectUrlJointOpenId(redirectSaasUrl, tenantCode);
            log.error("getMenu redirected :{}", redirected);
            try {
                resp.sendRedirect(redirected);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new BizException("服务繁忙");
        }

    }


    /**
     * 拼接openId
     * @param redirectSaasUrl
     * @param tenantCode
     * @return
     */
    private String redirectUrlJointOpenId(String redirectSaasUrl, String tenantCode) {
        if (redirectSaasUrl.contains("?")) {
            return redirectSaasUrl + "&tenantCode=" + tenantCode;
        } else {
            return redirectSaasUrl + "?tenantCode=" + tenantCode;
        }
    }





}
