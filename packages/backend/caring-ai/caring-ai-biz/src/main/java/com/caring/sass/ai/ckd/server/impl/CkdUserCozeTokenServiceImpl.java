package com.caring.sass.ai.ckd.server.impl;


import com.caring.sass.ai.ckd.dao.CkdUserCozeTokenMapper;
import com.caring.sass.ai.ckd.server.CkdUserCozeTokenService;
import com.caring.sass.ai.dto.ckd.CkdUserCozeTokenSaveDTO;
import com.caring.sass.ai.entity.ckd.CkdUserCozeToken;
import com.caring.sass.ai.utils.IOUtil;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.coze.openapi.client.auth.OAuthToken;
import com.coze.openapi.service.auth.JWTOAuthClient;
import com.coze.openapi.service.config.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * CKD用户coze的token
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-17
 */
@Slf4j
@Service

public class CkdUserCozeTokenServiceImpl extends SuperServiceImpl<CkdUserCozeTokenMapper, CkdUserCozeToken> implements CkdUserCozeTokenService {


    // ckd 营养食谱的appId
    private static final String appId = "1148225270883";

    private static final String kid = "nHgd3-x9KNFKF1u_1YtKTQy2HWPBV4MgLskY5U1wgFk";

    private static final String clientId = "1148225270883";

    protected String readPrivateKey() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String privateKey = null;
        try (InputStream inputStream = classLoader.getResourceAsStream("coze_token.pem")) {
            privateKey = IOUtil.toString(inputStream);

        } catch (Exception e) {
            log.error(" load apiclient_key error, file read error ");
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     *
     * 使用 coze 提供的 token jdk 。使用 私钥 和 公钥 生成 token
     *
     * @return
     */
    private OAuthToken getToken(String openId) {
        try {
            JWTOAuthClient client = new JWTOAuthClient.JWTOAuthBuilder()
                    .clientID(clientId)
                    .privateKey(readPrivateKey())
                    .publicKey("lBJIcu3Zq7LsnjUN6cpYLhXW-tVC-UOfDFvfRYUBd2M")
                    .baseURL(Consts.COZE_CN_BASE_URL)
                    .build();

            return client.getAccessToken(86399, null, openId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询用户的 coze token 如果token不存在或者过期，重新生产
     * @param saveDTO
     * @return
     */
    @Override
    public CkdUserCozeToken queryOrCreateToken(CkdUserCozeTokenSaveDTO saveDTO) {

        CkdUserCozeToken cozeToken = baseMapper.selectOne(Wraps.<CkdUserCozeToken>lbQ()
                .eq(CkdUserCozeToken::getOpenId, saveDTO.getOpenId())
                .last(" limit 1 "));
        if (Objects.isNull(cozeToken)) {
            cozeToken = new CkdUserCozeToken();
            cozeToken.setOpenId(saveDTO.getOpenId());
            cozeToken.setAppId(appId);
            cozeToken.setKid(kid);
            cozeToken.setSessionName(saveDTO.getOpenId());
        }
        if (cozeToken.getExpiresTime() == null || cozeToken.getExpiresTime().isBefore(LocalDateTime.now())) {
            OAuthToken token = getToken(saveDTO.getOpenId());
            if (token == null) {
                throw new BizException("coze auth error");
            }
            String accessToken = token.getAccessToken();
            Integer expiresIn = token.getExpiresIn();
            cozeToken.setAccessToken(accessToken);
            Instant instant = Instant.ofEpochSecond(expiresIn);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            cozeToken.setExpiresTime(dateTime.plusHours(-1));
        }

        if (cozeToken.getId() == null) {
            baseMapper.insert(cozeToken);
        } else {
            baseMapper.updateById(cozeToken);
        }
        return cozeToken;
    }
}
