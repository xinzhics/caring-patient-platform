package com.caring.sass.oauth.service;

import com.caring.sass.jwt.model.AuthInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: AdminAuthInfo
 * @author: 杨帅
 * @date: 2023/4/13
 */
@ApiModel("超级管理员端登录后的model")
@Data
public class AdminAuthInfo extends AuthInfo {

    @ApiModelProperty(value = "客户是否首次登录")
    private Boolean firstLogin;

    public AdminAuthInfo(AuthInfo authInfo) {
        this.setAccount(authInfo.getAccount());
        this.setAvatar(authInfo.getAvatar());
        this.setExpiration(authInfo.getExpiration());
        this.setExpire(authInfo.getExpire());
        this.setName(authInfo.getName());
        this.setToken(authInfo.getToken());
        this.setTokenType(authInfo.getTokenType());
        this.setRefreshToken(authInfo.getRefreshToken());
        this.setUserId(authInfo.getUserId());
        this.setUserType(authInfo.getUserType());
        this.setWorkDescribe(authInfo.getWorkDescribe());
    }


}
