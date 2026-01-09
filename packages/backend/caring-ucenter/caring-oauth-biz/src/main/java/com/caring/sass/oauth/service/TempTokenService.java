package com.caring.sass.oauth.service;

import cn.hutool.core.bean.copier.CopyOptions;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.auth.UserToken;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.auth.UserTokenService;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.oauth.event.LoginEvent;
import com.caring.sass.oauth.event.model.LoginStatusDTO;
import com.caring.sass.tenant.api.GlobalUserApi;
import com.caring.sass.tenant.entity.GlobalUser;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @className: TempTokenService
 * @author: 杨帅
 * @date: 2023/4/12
 */
@Service
@Slf4j
public class TempTokenService {

    @Autowired
    protected TokenUtil tokenUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    protected UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    public AuthInfo login(String tempToken) {

        Object o = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_LOGIN).get(tempToken);
        if (o == null) {
            throw new BizException("链接已过期,请使用账号密码登录");
        }
        redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_LOGIN).delete(tempToken);
        String tenantCode = o.toString();
        // 查询 租户下对应的账号信息
        BaseContextHandler.setTenant(tenantCode);
        User user = userService.getByAccount(User.ACCOUNT_ADMIN);

        // 5.生成 token

        JwtUserInfo userInfo = new JwtUserInfo(user.getId(), user.getAccount(), user.getName(), UserType.ADMIN_OPERATION);
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, null);
        authInfo.setAvatar(user.getAvatar());
        authInfo.setWorkDescribe(user.getWorkDescribe());

        UserToken userToken = new UserToken();
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("userId", "createUser");
        BeanPlusUtil.copyProperties(authInfo, userToken, CopyOptions.create().setFieldMapping(fieldMapping));
        userToken.setClientId("caring_ui");
        userToken.setExpireTime(DateUtils.date2LocalDateTime(authInfo.getExpiration()));

        //成功登录事件
        userTokenService.save(userToken);
        SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.success(user.getId(), userToken)));
        return authInfo;
    }
}
