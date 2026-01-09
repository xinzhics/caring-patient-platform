package com.caring.sass.oauth.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.caring.sass.base.R;
import com.caring.sass.boot.utils.WebUtils;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.jwt.utils.JwtUtil;
import com.caring.sass.msgs.api.VerificationCodeApi;
import com.caring.sass.oauth.utils.I18nUtils;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.sms.enumeration.VerificationCodeType;
import com.caring.sass.tenant.api.GlobalUserApi;
import com.caring.sass.tenant.entity.GlobalUser;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.caring.sass.context.BaseContextConstants.BASIC_HEADER_KEY;

/**
 * @author caring
 * @createTime 2017-12-15 13:42
 */
@Service
@Slf4j
public class AdminUiService {

    @Autowired
    protected TokenUtil tokenUtil;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Resource
    private GlobalUserApi globalUserApi;

    @Resource
    private VerificationCodeApi verificationCodeApi;

    @Autowired
    private CacheChannel channel;

    /**
     * 超管账号登录
     *
     * @param account  账号
     * @param password 密码
     * @return
     */
    public R<AdminAuthInfo> adminLogin(String account, String password) {
        String basicHeader = ServletUtil.getHeader(WebUtils.request(), BASIC_HEADER_KEY, StrPool.UTF_8);
        JwtUtil.getClient(basicHeader);

        GlobalUser u = new GlobalUser().setAccount(account).setTenantCode(BizConstant.SUPER_TENANT).setPassword(SecureUtil.md5(password));
        GlobalUser user = globalUserApi.login(u).getData();
        if (Objects.isNull(user)) {
            throw new BizException(ExceptionCode.JWT_USER_INVALID.getCode(), ExceptionCode.JWT_USER_INVALID.getMsg());
        }
        // 过期时间30天
        AdminAuthInfo authInfo = createAuthInfo(user);
        boolean firstLogin = !BizConstant.THIRD_PARTY_CUSTOMERS.equals(user.getGlobalUserType())
                || user.getFirstLogin() == null
                || user.getFirstLogin() != 1;
        authInfo.setFirstLogin(firstLogin);
        return R.success(authInfo);
    }

    /**
     * @title 创建登录授权
     * @author 杨帅 
     * @updateTime 2023/4/12 17:39 
     * @throws 
     */
    public AdminAuthInfo createAuthInfo(GlobalUser user) {
        JwtUserInfo userInfo;
        if (BizConstant.THIRD_PARTY_CUSTOMERS.equals(user.getGlobalUserType())) {
            userInfo = new JwtUserInfo(user.getId(), user.getAccount(), user.getName(), UserType.THIRD_PARTY_CUSTOMERS);
        } else if ("cms".equals(user.getGlobalUserType())) {
            userInfo = new JwtUserInfo(user.getId(), user.getAccount(), user.getName(), UserType.CMS_ADMIN);
        } else  {
            userInfo = new JwtUserInfo(user.getId(), user.getAccount(), user.getName(), UserType.GLOBAL_ADMIN);
        }

        // 过期时间30天
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.THIRTY_DAYS);
        // channel.set(CacheKey.TOKEN_USER_ID, authInfo.getToken(), user.getId(), Constant.THIRTY_DAYS);
        log.info("token={}", authInfo.getToken());
        return new AdminAuthInfo(authInfo);
    }
    
    /**
     * @title 校验超管登录的手机号验证码
     * @author 杨帅
     * @updateTime 2023/4/12 17:32
     * @throws
     */
    public boolean checkAdminCode(String mobile, String verifyCode) {

        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        R<Boolean> verificationR = verificationCodeApi.verification(VerificationCodeDTO.builder()
                .mobile(mobile)
                .type(VerificationCodeType.ADMIN_LOGIN)
                .code(verifyCode)
                .clearKey(true)
                .build());
        boolean verifyCodeError = verificationR.getIsError() ||
                Objects.isNull(verificationR.getData()) ||
                !verificationR.getData();
        if (verifyCodeError) {
            throw new BizException("验证码错误");
        }
        return true;
    }


    /**
     * @title 发送超管登录的验证码
     * @author 杨帅
     * @updateTime 2023/4/12 17:29
     * @throws
     */
    public R<Boolean> send(VerificationCodeDTO verificationCodeDTO) {


        String mobile = verificationCodeDTO.getMobile();

        GlobalUser user = globalUserApi.mobile(mobile).getData();
        if (Objects.isNull(user)) {
            throw new BizException("用户不存在");
        }
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);

        String key = "admin_sms_" + mobile;
        String string = redisTemplate.opsForValue().get(key);
        if (string != null && Integer.parseInt(string) > 10) {
            throw new BizException(I18nUtils.getMessage("SMS_SEND_CODE_MORE"));
        }
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey == null || !hasKey) {
            redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().increment(key, 1);
        }

        return verificationCodeApi.send(verificationCodeDTO);

    }

    /**
     * @title 使用手机号直接登录
     * @author 杨帅 
     * @updateTime 2023/4/12 17:38 
     * @throws 
     */
    public R<AdminAuthInfo> adminMobileLogin(String mobile, String password) {
        String passwordParam = null;
        if (StrUtil.isNotBlank(password)) {
            passwordParam = SecureUtil.md5(password);
        }
        R<GlobalUser> globalUserR = globalUserApi.mobileLogin(mobile, passwordParam);
        if (globalUserR.getIsError()) {
            throw new BizException(globalUserR.getMsg());
        }
        GlobalUser user = globalUserR.getData();
        if (Objects.isNull(user)) {
            throw new BizException("用户不存在");
        }
        AdminAuthInfo authInfo = createAuthInfo(user);
        boolean firstLogin = !BizConstant.THIRD_PARTY_CUSTOMERS.equals(user.getGlobalUserType())
                || user.getFirstLogin() == null
                || user.getFirstLogin() != 1;
        authInfo.setFirstLogin(firstLogin);
        return R.success(authInfo);
    }
}
