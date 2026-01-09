package com.caring.sass.oauth.service;


import cn.hutool.core.bean.copier.CopyOptions;
import com.caring.sass.authority.entity.auth.UserToken;
import com.caring.sass.authority.service.auth.UserTokenService;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.oauth.event.LoginEvent;
import com.caring.sass.oauth.event.model.LoginStatusDTO;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AiUserTokenService {

    @Autowired
    protected TokenUtil tokenUtil;

    @Autowired
    private UserTokenService userTokenService;


    /**
     * 创建一个2小时的临时的token
     * @param userId
     * @return
     */
    public AuthInfo createTempToken(Long userId) {

        JwtUserInfo userInfo = new JwtUserInfo(userId, userId.toString(), userId.toString(), UserType.AI_ARTICLE_USER);
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.THIRTY_DAYS);

        UserToken userToken = new UserToken();
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("userId", "createUser");
        BeanPlusUtil.copyProperties(authInfo, userToken, CopyOptions.create().setFieldMapping(fieldMapping));
        userToken.setClientId("AI");
        userToken.setExpireTime(DateUtils.date2LocalDateTime(authInfo.getExpiration()));

        //成功登录事件
        userTokenService.save(userToken);
        SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.success(userId, userToken)));
        return authInfo;
    }




}
