package com.caring.open.service.controller.oauth;

import com.caring.sass.base.R;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.oauth.dto.Oauth2AccessToken;
import com.caring.sass.oauth.dto.TokenRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author xinzh
 */
@Slf4j
@Validated
@RestController
@RequestMapping
@Api(value = "token认证", tags = "获取客户端token")
public class ClientCredentialsTokenGranter {
    private static final Long LONG_EXPIRE_MILLIS = 3600 * 24 * 720L;

    @Autowired
    private OauthApi oauthApi;

    /**
     * 第三方应用授权token
     *
     * @param tokenRequest 请求参数
     * @throws BizException
     */
    @ApiOperation(value = "获取应用token", notes = "获取应用token")
    @PostMapping(value = "/anno/token")
    public R<Oauth2AccessToken> client(@Validated @RequestBody TokenRequest tokenRequest) throws BizException {
        tokenRequest.setExpireMillis(LONG_EXPIRE_MILLIS);
        return oauthApi.client(tokenRequest);
    }

    @ApiOperation(value = "获取应用token")
    @GetMapping("/anno/token")
    public R<Oauth2AccessToken> getByClient(@RequestParam("clientId") String clientId,
                                            @RequestParam("clientSecret") String clientSecret) {
        TokenRequest tokenRequest = new TokenRequest()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setExpireMillis(LONG_EXPIRE_MILLIS);
        return oauthApi.client(tokenRequest);
    }

}
