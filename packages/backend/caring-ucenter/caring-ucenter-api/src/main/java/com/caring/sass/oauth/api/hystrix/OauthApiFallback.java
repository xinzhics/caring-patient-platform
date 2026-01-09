package com.caring.sass.oauth.api.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.authority.dto.auth.DoctorLoginByOpenId;
import com.caring.sass.base.R;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.oauth.dto.ClientInfo;
import com.caring.sass.oauth.dto.Oauth2AccessToken;
import com.caring.sass.oauth.dto.TokenRequest;
import com.caring.sass.user.entity.ConsultationGroupMember;
import org.springframework.stereotype.Component;

/**
 * @author xinzh
 */
@Component
public class OauthApiFallback implements OauthApi {
    @Override
    public R<Oauth2AccessToken> client(TokenRequest tokenRequest) throws BizException {
        return R.timeout();
    }

    @Override
    public R<ClientInfo> parseClientToken(String token) {
        return R.timeout();
    }

    @Override
    public R<String> wxUserLogin(JSONObject wxOAuth2UserInfo, String userType, Long groupId) {
        return R.timeout();
    }

    @Override
    public R<JSONObject> doctorLoginByOpenId(DoctorLoginByOpenId doctorLoginByOpenId) {
        return R.timeout();
    }

    @Override
    public R<JSONObject> createTempToken(Long userId) {
        return R.timeout();
    }

    @Override
    public R<JSONObject> aiAuthRefreshToken(JSONObject jsonObject) {
        return R.timeout();
    }

    @Override
    public R<String> consultationGroupMemberLogin(ConsultationGroupMember consultationGroupMember) {
        return R.timeout();
    }
}
