package com.caring.sass.oauth.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.authority.dto.auth.DoctorLoginByOpenId;
import com.caring.sass.base.R;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.oauth.api.hystrix.OauthApiFallback;
import com.caring.sass.oauth.dto.ClientInfo;
import com.caring.sass.oauth.dto.Oauth2AccessToken;
import com.caring.sass.oauth.dto.TokenRequest;
import com.caring.sass.user.entity.ConsultationGroupMember;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 权限认证API
 *
 * @author caring
 * @date 2019/08/02
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", path = "/anno",
        qualifier = "oauthApi", fallback = OauthApiFallback.class)
public interface OauthApi {

    /**
     * 第三方应用授权token
     *
     * @param tokenRequest 请求参数
     * @throws BizException
     */
    @ApiOperation(value = "获取应用token", notes = "获取应用token")
    @PostMapping(value = "/client")
    R<Oauth2AccessToken> client(@Validated @RequestBody TokenRequest tokenRequest) throws BizException;

    /**
     * 第三方应用授权token解析
     *
     * @param clientToken token
     */
    @ApiOperation(value = "获取应用token", notes = "获取应用token")
    @GetMapping(value = "/parseClientToken")
    R<ClientInfo> parseClientToken(@RequestParam(value = "clientToken") String clientToken);


    @ApiOperation(value = "微信登录", notes = "微信登录")
    @PostMapping("anno/wxUser/login")
    R<String> wxUserLogin(@RequestBody JSONObject wxOAuth2UserInfo,
                                   @RequestParam("userType") String userType,
                                   @RequestParam(value = "groupId", required = false) Long groupId);


    @ApiOperation(value = "医生使用手机号和openId登录")
    @PostMapping("/doctor/login/openId")
    @SysLog("医生使用手机号和openId登录")
    R<JSONObject> doctorLoginByOpenId(@RequestBody @Validated DoctorLoginByOpenId doctorLoginByOpenId);

    @ApiOperation(value = "创建AI的token")
    @PostMapping("/create/aiAuth/{userId}")
    R<JSONObject> createTempToken(@PathVariable("userId") Long userId);

    @ApiOperation(value = "AI刷新授权")
    @PostMapping("/aiAuthRefreshToken")
    R<JSONObject> aiAuthRefreshToken(@RequestBody JSONObject jsonObject);


    @ApiOperation(value = "病例讨论扫码用户手机号登录", notes = "微信登录")
    @PostMapping("anno/consultationGroupMember/login")
    R<String> consultationGroupMemberLogin(@RequestBody ConsultationGroupMember consultationGroupMember);

}
