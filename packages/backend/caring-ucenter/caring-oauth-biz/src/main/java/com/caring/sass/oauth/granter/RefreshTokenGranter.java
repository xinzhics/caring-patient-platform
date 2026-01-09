package com.caring.sass.oauth.granter;

import com.caring.sass.authority.dto.auth.LoginParamDTO;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.base.R;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextConstants;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.oauth.event.LoginEvent;
import com.caring.sass.oauth.event.model.LoginStatusDTO;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.utils.StrHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.caring.sass.oauth.granter.RefreshTokenGranter.GRANT_TYPE;

/**
 * RefreshTokenGranter
 *
 * @author caring
 * @date 2020年03月31日10:23:53
 */
@Component(GRANT_TYPE)
public class RefreshTokenGranter extends AbstractTokenGranter implements TokenGranter {

    public static final String GRANT_TYPE = "refresh_token";

    @Override
    public R<AuthInfo> grant(LoginParamDTO loginParam) {
        String tenant = loginParam.getTenant();
        if (StringUtils.isNotEmptyString(tenant)) {
            BaseContextHandler.setTenant(tenant);
        }
        String grantType = loginParam.getGrantType();
        String refreshTokenStr = loginParam.getRefreshToken();
        if (StrHelper.isAnyBlank(grantType, refreshTokenStr) || !GRANT_TYPE.equals(grantType)) {
            return R.fail("加载用户信息失败");
        }

        AuthInfo authInfo = tokenUtil.parseRefreshToken(refreshTokenStr);

        if (!BaseContextConstants.REFRESH_TOKEN_KEY.equals(authInfo.getTokenType())) {
            return R.fail("refreshToken无效，无法加载用户信息");
        }

        User user = userService.getByIdCache(authInfo.getUserId());

        if (user == null || !user.getStatus()) {
            String msg = "您已被禁用！";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        // 密码过期
        if (user.getPasswordExpireTime() != null && LocalDateTime.now().isAfter(user.getPasswordExpireTime())) {
            String msg = "用户密码已过期，请修改密码或者联系管理员重置!";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        return R.success(createToken(user));

    }
}
