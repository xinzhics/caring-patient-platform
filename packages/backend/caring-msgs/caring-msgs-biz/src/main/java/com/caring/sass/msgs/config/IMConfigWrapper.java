package com.caring.sass.msgs.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.common.constant.CacheKey;
import com.caring.sass.lock.DistributedLock;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthenticationApi;
import io.swagger.client.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

/**
 * 环信配置包装类，添加token信息
 *
 * @author xinzh
 */
@Slf4j
@Component
public class IMConfigWrapper {

    private static IMConfig imConfig;

    private static DistributedLock distributedLock;

    private static CacheRepository cacheRepository;

    @Autowired
    public IMConfigWrapper(IMConfig imConfig, DistributedLock distributedLock, CacheRepository cacheRepository) {
        IMConfigWrapper.imConfig = imConfig;
        IMConfigWrapper.distributedLock = distributedLock;
        IMConfigWrapper.cacheRepository = cacheRepository;
    }

    public static String getAppName() {
        return imConfig.getAppName();
    }

    public static String getGrantType() {
        return imConfig.getGrantType();
    }

    public static String getClientId() {
        return imConfig.getClientId();
    }

    public static String getClientSecret() {
        return imConfig.getClientSecret();
    }

    public static String getUserPassword() {
        return imConfig.getUserPassword();
    }

    public static String getOrgName() {
        return imConfig.getOrgName();
    }

    public static String getKey() {
        return CacheKey.buildKey(CacheKey.HX_IM, imConfig.getClientId());
    }

    // 当调用环信接口是。出现 401 异常时。 调用此接口。重新刷新 token
    public static String initAccessToken(String key) {
        return getAccessToken();
    }

    public static String getAccessToken() {
        String key = CacheKey.buildKey(CacheKey.HX_IM, imConfig.getClientId());
        try {
            String accessToken = cacheRepository.get(key);
            if (StrUtil.isNotBlank(accessToken)) {
                return accessToken;
            }

            String lock = key + ":lock";
            try {
                boolean acquireLock = distributedLock.lock(lock, 20 * 1000);
                if (!acquireLock) {
                    return cacheRepository.get(key);
                }
                Token token = new Token().clientId(imConfig.getClientId()).grantType(imConfig.getGrantType()).clientSecret(imConfig.getClientSecret());
                String resp = new AuthenticationApi().orgNameAppNameTokenPost(imConfig.getOrgName(), imConfig.getAppName(), token);
                JSONObject ret = JSON.parseObject(resp);
                accessToken = " Bearer " + ret.get("access_token");
                Long expiresIn = ret.getLong("expires_in");
                if (expiresIn != null && expiresIn > 3600 * 24 ) {
                    cacheRepository.setExpire(key, accessToken, expiresIn - 3600 * 24);
                } else {
                    cacheRepository.setExpire(key, accessToken, 3600 * 24);
                }
                return accessToken;
            } finally {
                distributedLock.releaseLock(lock);
            }
        } catch (ApiException e) {
            log.error("获取环信tokens失败", e);
        }
        return cacheRepository.get(key);
    }
}
