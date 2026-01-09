package com.caring.sass.oauth.granter;


import com.caring.sass.authority.dto.auth.LoginParamDTO;
import com.caring.sass.base.R;
import com.caring.sass.jwt.model.AuthInfo;

/**
 * 授权认证统一接口.
 *
 * @author caring
 * @date 2020年03月31日10:21:21
 */
public interface TokenGranter {

    /**
     * 获取用户信息
     *
     * @param loginParam 授权参数
     * @return LoginDTO
     */
    R<AuthInfo> grant(LoginParamDTO loginParam);

}
