package com.caring.sass.oauth.granter;

import com.caring.sass.authority.dto.auth.LoginParamDTO;
import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.oauth.event.LoginEvent;
import com.caring.sass.oauth.event.model.LoginStatusDTO;
import com.caring.sass.oauth.service.ValidateCodeService;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.caring.sass.oauth.granter.CaptchaTokenGranter.GRANT_TYPE;

/**
 * 验证码TokenGranter
 *
 * @author Chill
 */
@Component(GRANT_TYPE)
@Slf4j
public class CaptchaTokenGranter extends AbstractTokenGranter implements TokenGranter {

    public static final String GRANT_TYPE = "captcha";
    @Autowired
    private ValidateCodeService validateCodeService;

    @Override
    public R<AuthInfo> grant(LoginParamDTO loginParam) {
        R<Boolean> check = validateCodeService.check(loginParam.getKey(), loginParam.getCode());
        if (check.getIsError()) {
            String msg = check.getMsg();
            BaseContextHandler.setTenant(loginParam.getTenant());
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(loginParam.getAccount(), msg)));
            throw BizException.validFail(check.getMsg());
        }

        return login(loginParam);
    }

}
