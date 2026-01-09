package com.caring.sass.oauth.service;

import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.oauth.api.MiniappInfoApi;
import com.caring.sass.oauth.dto.MiniAppOpenIdLoginDTO;
import com.caring.sass.oauth.dto.MiniAppPhoneLoginDTO;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.entity.MiniappInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @className: MiniAppLogin
 * @author: 杨帅
 * @date: 2024/3/26
 *
 * 小程序登录服务
 */
@Service
@Slf4j
public class MiniAppLoginService {

    private final TokenUtil tokenUtil;

    private final MiniappInfoApi miniappInfoApi;

    public MiniAppLoginService(TokenUtil tokenUtil,  MiniappInfoApi miniappInfoApi) {
        this.tokenUtil = tokenUtil;
        this.miniappInfoApi = miniappInfoApi;
    }


    /**
     * 使用小程序的用户信息，给生成授权
     * @param miniappInfo
     * @return
     */
    private AuthInfo login(MiniappInfo miniappInfo) {

        JwtUserInfo userInfo = new JwtUserInfo();
        userInfo.setAccount(miniappInfo.getPhoneNumber());
        userInfo.setName(miniappInfo.getPhoneNumber());
        userInfo.setUserId(miniappInfo.getId());
        userInfo.setUserType(UserType.MiniAppUSER);

        return tokenUtil.createAuthInfo(userInfo, Constant.THIRTY_DAYS);
    }


    /**
     * 使用手机号 openId 创建小程序用户。并生成sass系统的授权
     * @param miniAppPhoneLoginDTO
     * @return
     */
    public R<AuthInfo> loginByPhoneNumber(MiniAppPhoneLoginDTO miniAppPhoneLoginDTO) {

        @NotNull @NotEmpty String phoneNumber = miniAppPhoneLoginDTO.getPhoneNumber();
        @NotNull @NotEmpty String openId = miniAppPhoneLoginDTO.getOpenId();
        @NotNull @NotEmpty String appId = miniAppPhoneLoginDTO.getAppId();

        MiniappInfo miniappInfo = new MiniappInfo();
        miniappInfo.setMiniAppId(appId);
        miniappInfo.setMiniAppOpenId(openId);
        miniappInfo.setPhoneNumber(phoneNumber);
        miniappInfo.setClientType(miniAppPhoneLoginDTO.getClientType());
        miniappInfo.setId(miniAppPhoneLoginDTO.getKnowUserId());
        R<MiniappInfo> updateUser = miniappInfoApi.createOrUpdateUser(miniappInfo);
        if (updateUser.getIsSuccess() && updateUser.getData() != null) {
            MiniappInfo userData = updateUser.getData();
            AuthInfo login = login(userData);
            return R.success(login);
        } else {
            return R.timeout();
        }

    }



    /**
     * 使用openId查询用户信息。
     * 用户存在时，直接给用户授权
     * @param miniAppOpenIdLoginDTO
     * @return
     */
    public R<AuthInfo> loginByOpenId(MiniAppOpenIdLoginDTO miniAppOpenIdLoginDTO) {

        @NotNull @NotEmpty String openId = miniAppOpenIdLoginDTO.getOpenId();
        String appId = miniAppOpenIdLoginDTO.getAppId();
        R<MiniappInfo> miniappInfoR = miniappInfoApi.findByOpenId(openId, appId);
        if (!miniappInfoR.getIsSuccess() || Objects.isNull(miniappInfoR.getData())) {
            return R.fail("请使用手机号登录");
        }
        MiniappInfo data = miniappInfoR.getData();

        AuthInfo login = login(data);

        return R.success(login);
    }

    /**
     * 使用openId直接注册并登录
     * @param miniAppOpenIdLoginDTO
     * @return
     */
    public R<AuthInfo> miniAppOpenIdRegisterLogin(MiniAppOpenIdLoginDTO miniAppOpenIdLoginDTO) {

        @NotNull @NotEmpty String openId = miniAppOpenIdLoginDTO.getOpenId();
        String appId = miniAppOpenIdLoginDTO.getAppId();

        MiniappInfo miniappInfo = new MiniappInfo();
        miniappInfo.setMiniAppId(appId);
        miniappInfo.setMiniAppOpenId(openId);
        R<MiniappInfo> updateUser = miniappInfoApi.createOrUpdateUser(miniappInfo);
        if (updateUser.getIsSuccess() && updateUser.getData() != null) {
            MiniappInfo userData = updateUser.getData();
            AuthInfo login = login(userData);
            return R.success(login);
        } else {
            return R.timeout();
        }
    }
}
