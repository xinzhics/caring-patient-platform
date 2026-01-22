package com.caring.sass.oauth.granter;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.caring.sass.authority.dto.auth.LoginParamDTO;
import com.caring.sass.authority.entity.auth.Application;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.auth.UserToken;
import com.caring.sass.authority.service.auth.ApplicationService;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.auth.UserTokenService;
import com.caring.sass.base.R;
import com.caring.sass.boot.utils.WebUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.database.properties.MultiTenantType;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.jwt.utils.JwtUtil;
import com.caring.sass.oauth.event.LoginEvent;
import com.caring.sass.oauth.event.model.LoginStatusDTO;
import com.caring.sass.oauth.utils.TimeUtils;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.utils.StrHelper;
import com.caring.sass.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.caring.sass.context.BaseContextConstants.BASIC_HEADER_KEY;
import static com.caring.sass.utils.BizAssert.gt;
import static com.caring.sass.utils.BizAssert.notNull;

/**
 * 验证码TokenGranter
 *
 * @author Chill
 */
@Slf4j
public abstract class AbstractTokenGranter implements TokenGranter {
    @Autowired
    protected TokenUtil tokenUtil;
    @Autowired
    protected UserService userService;
    @Autowired
    protected TenantApi tenantApi;
    @Autowired
    protected CacheChannel cacheChannel;
    @Autowired
    protected ApplicationService applicationService;
    @Autowired
    protected DatabaseProperties databaseProperties;

    @Autowired
    protected UserTokenService userTokenService;

    /**
     * 处理登录逻辑
     *
     * @param loginParam 登录参数
     * @return 认证信息
     */
    protected R<AuthInfo> login(LoginParamDTO loginParam) {
        if (StrHelper.isAnyBlank(loginParam.getAccount(), loginParam.getPassword())) {
            return R.fail("请输入用户名或密码");
        }
        // 1，检测租户是否可用
        if (!MultiTenantType.NONE.eq(databaseProperties.getMultiTenantType())) {
            Tenant tenant = tenantApi.getByCode(loginParam.getTenant()).getData();
            notNull(tenant, "企业不存在");
            // todo 测试先放开
             BizAssert.equals(TenantStatusEnum.NORMAL, tenant.getStatus(), "企业不可用~");
            if (tenant.getExpirationTime() != null) {
                gt(LocalDateTime.now(), tenant.getExpirationTime(), "企业服务已到期~");
            }
            BaseContextHandler.setTenant(tenant.getCode());
        }

        // 2.检测client是否可用
        R<String[]> checkR = checkClient();
        if (checkR.getIsError()) {
            return R.fail(checkR.getMsg());
        }

        // 3. 验证登录
        R<User> result = this.getUser(loginParam.getAccount(), loginParam.getPassword());
        if (result.getIsError()) {
            return R.fail(result.getCode(), result.getMsg());
        }

        // 4.查询用户的权限
        User user = result.getData();

        // 5.生成 token
        AuthInfo authInfo = this.createToken(user);

        UserToken userToken = getUserToken(checkR.getData()[0], authInfo);

        //成功登录事件
        userTokenService.save(userToken);
        SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.success(user.getId(), userToken)));
        return R.success(authInfo);
    }

    private UserToken getUserToken(String clientId, AuthInfo authInfo) {
        UserToken userToken = new UserToken();
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("userId", "createUser");
        BeanPlusUtil.copyProperties(authInfo, userToken, CopyOptions.create().setFieldMapping(fieldMapping));
        userToken.setClientId(clientId);
        userToken.setExpireTime(DateUtils.date2LocalDateTime(authInfo.getExpiration()));
        return userToken;
    }


    /**
     * 检测 client
     *
     * @return
     */
    protected R<String[]> checkClient() {
        // 打印所有请求头用于调试
        javax.servlet.http.HttpServletRequest request = WebUtils.request();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headersInfo = new StringBuilder();
        headersInfo.append("\n=== 开始打印所有请求头 ===\n");
        while(headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headersInfo.append("Header: ").append(headerName).append(" = ").append(headerValue).append("\n");
        }
        headersInfo.append("=== 打印请求头结束 ===\n");
        log.info(headersInfo.toString());

        String basicHeader = ServletUtil.getHeader(WebUtils.request(), BASIC_HEADER_KEY, StrPool.UTF_8);
        String[] client = JwtUtil.getClient(basicHeader);

        // 如果前端发送的是 undefined:undefined，使用默认客户端
        if (client != null && client.length >= 2 &&
            ("undefined".equals(client[0]) || StrUtil.isEmpty(client[0]))) {
            log.warn("检测到无效的客户端凭证，使用默认客户端配置");
            // 使用默认客户端ID和密钥，这里需要根据实际情况配置
            client = new String[]{"caring_admin_ui", "caring_admin_ui_secret"};
        }

        // 验证客户端是否存在且可用
        Application application = applicationService.getByClient(client[0], client[1]);

        if (application == null) {
            return R.fail("请填写正确的客户端ID或者客户端秘钥");
        }
        if (!application.getStatus()) {
            return R.fail("客户端[%s]已被禁用", application.getClientId());
        }

        log.info("客户端验证成功: clientId={}, applicationName={}", client[0], application.getName());
        return R.success(client);
    }


    /**
     * 检测用户密码是否正确
     *
     * @param account  账号
     * @param password 密码
     * @return 用户信息
     */
    protected R<User> getUser(String account, String password) {
        User user = this.userService.getByAccount(account);
        // 密码错误
        String passwordMd5 = cn.hutool.crypto.SecureUtil.md5(password);
        if (user == null) {
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }

        if (!user.getPassword().equalsIgnoreCase(passwordMd5)) {
            String msg = "用户名或密码错误!";
            // 密码错误事件
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.pwdError(user.getId(), msg)));
            return R.fail(msg);
        }

        // 密码过期
        if (user.getPasswordExpireTime() != null && LocalDateTime.now().isAfter(user.getPasswordExpireTime())) {
            String msg = "用户密码已过期，请修改密码或者联系管理员重置!";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        if (!user.getStatus()) {
            String msg = "用户被禁用，请联系管理员！";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        // 用户锁定
        Integer maxPasswordErrorNum = 0;
        Integer passwordErrorNum = Convert.toInt(user.getPasswordErrorNum(), 0);
        if (maxPasswordErrorNum > 0 && passwordErrorNum > maxPasswordErrorNum) {
            log.info("当前错误次数{}, 最大次数:{}", passwordErrorNum, maxPasswordErrorNum);

            LocalDateTime passwordErrorLockTime = TimeUtils.getPasswordErrorLockTime("0");
            log.info("passwordErrorLockTime={}", passwordErrorLockTime);
            if (passwordErrorLockTime.isAfter(user.getPasswordErrorLastTime())) {
                // 登录失败事件
                String msg = StrUtil.format("密码连续输错次数已达到{}次,用户已被锁定~", maxPasswordErrorNum);
                SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
                return R.fail(msg);
            }
        }

        return R.success(user);
    }

    /**
     * 创建用户TOKEN
     *
     * @param user 用户
     * @return token
     */
    protected AuthInfo createToken(User user) {
        String account = user.getAccount();
        JwtUserInfo userInfo;
        if ("admin".equals(account)) {
            userInfo = new JwtUserInfo(user.getId(), account, user.getName(), UserType.ADMIN);
        } else {
            userInfo = new JwtUserInfo(user.getId(), account, user.getName(), UserType.ORGAN_ADMIN);
        }
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);
        authInfo.setAvatar(user.getAvatar());
        authInfo.setWorkDescribe(user.getWorkDescribe());
        return authInfo;
    }


}
