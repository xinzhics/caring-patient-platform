package com.caring.sass.oauth.granter;

import com.caring.sass.authority.entity.auth.Application;
import com.caring.sass.authority.service.auth.ApplicationService;
import com.caring.sass.base.R;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.context.BaseContextConstants;
import com.caring.sass.jwt.model.Token;
import com.caring.sass.jwt.utils.JwtUtil;
import com.caring.sass.oauth.dto.Oauth2AccessToken;
import com.caring.sass.oauth.dto.TokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 客户端认证模式
 *
 * @author xinzh
 */
@Component
public class ClientTokenGranter {

    private final ApplicationService applicationService;

    private static final String GRANT_TYPE = "client";
    private static final String CLIENT_ID = "client_id";
    private static final String TENANT_CODE = "tenant_code";
    private static final Long EXPIRE_MILLIS = 3600 * 24 * 7L;

    private final CacheRepository cacheRepository;

    public ClientTokenGranter(ApplicationService applicationService, CacheRepository cacheRepository) {
        this.applicationService = applicationService;
        this.cacheRepository = cacheRepository;
    }

    public R<Oauth2AccessToken> grant(TokenRequest tokenRequest) {
        String clientId = tokenRequest.getClientId();
        String secret = tokenRequest.getClientSecret();
        Long expireMillis = tokenRequest.getExpireMillis();
        Application application = applicationService.findByClientId(clientId);
        if (Objects.isNull(application)) {
            return R.fail("请填写正确的客户端ID或者客户端秘钥");
        }
        if (!Objects.equals(application.getClientSecret(), secret)) {
            return R.fail("请填写正确的客户端ID或者客户端秘钥");
        }
        Oauth2AccessToken token = createAuthInfo(application, expireMillis);
        return R.success(token);
    }

    public Oauth2AccessToken createAuthInfo(Application application, Long expireMillis) {
        String clientId = application.getClientId();
        long expireMillis2 = Objects.isNull(expireMillis) ? EXPIRE_MILLIS : expireMillis;
        String cacheKey = "Oauth2AccessToken:" + clientId;

        Oauth2AccessToken oauth2AccessToken2 = cacheRepository.getOrDef(cacheKey, f -> {
            Map<String, String> param = new HashMap<>(16);
            //设置jwt参数
            param.put(GRANT_TYPE, GRANT_TYPE);
            param.put(CLIENT_ID, clientId);
            param.put(TENANT_CODE, application.getTenantCode());
            Token token = JwtUtil.createJWT(param, expireMillis2);
            Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken();
            oauth2AccessToken.setToken(BaseContextConstants.BEARER_HEADER_PREFIX + token.getToken());
            oauth2AccessToken.setExpiration(token.getExpiration());
            oauth2AccessToken.setExpire(expireMillis2);
            oauth2AccessToken.setTokenType(GRANT_TYPE);
            return oauth2AccessToken;
        });
        cacheRepository.setExpire(cacheKey, oauth2AccessToken2, expireMillis2);
        return oauth2AccessToken2;
    }

}
