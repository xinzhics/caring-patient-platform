package com.caring.sass.gateway.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.CacheKey;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.properties.IgnoreTokenProperties;
import com.caring.sass.context.BaseContextConstants;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.utils.JwtUtil;
import com.caring.sass.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.caring.sass.context.BaseContextConstants.BASIC_HEADER_KEY;
import static com.caring.sass.context.BaseContextConstants.BEARER_HEADER_KEY;
import static com.caring.sass.context.BaseContextConstants.JWT_KEY_CLIENT_ID;
import static com.caring.sass.context.BaseContextConstants.JWT_KEY_TENANT;
import static com.caring.sass.exception.code.ExceptionCode.*;

/**
 * 过滤器
 *
 * @author caring
 * @date 2019/07/31
 */
@Component
@Slf4j
@EnableConfigurationProperties({IgnoreTokenProperties.class})
public class TokenContextFilter implements GlobalFilter, Ordered {
    @Value("${spring.profiles.active:dev}")
    protected String profiles;
    @Value("${caring.database.multiTenantType:SCHEMA}")
    protected String multiTenantType;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private IgnoreTokenProperties ignoreTokenProperties;
    @Autowired
    private CacheChannel channel;


    protected boolean isDev(String token) {
        return !StrPool.PROD.equalsIgnoreCase(profiles)
                && (StrPool.TEST_TOKEN.equalsIgnoreCase(token) || StrPool.TEST.equalsIgnoreCase(token));
    }

    @Override
    public int getOrder() {
        return -1000;
    }

    /**
     * Gateway中解析token。获取用户类型
     * userType： cms_admin || THIRD_PARTY_CUSTOMERS || CONSULTATION_GUEST || PATIENT || DOCTOR || NURSING_STAFF || MINIAPPUSER ？拦截有以下路径的请求。
     * (超管内容管理员， 第三方客户， 病例讨论来宾， 患者，医生，医助， 小程序用户)
     */
    public static List<String> authApiList = new ArrayList<>();
    public static List<String> tenantApiList = new ArrayList<>();

    public static List<String> userTypeList = new ArrayList<>();

    static {
        userTypeList.add(UserType.CMS_ADMIN);
        userTypeList.add(UserType.GLOBAL_ADMIN);
        userTypeList.add(UserType.THIRD_PARTY_CUSTOMERS);
        userTypeList.add(UserType.CONSULTATION_GUEST);
        userTypeList.add(UserType.PATIENT);
        userTypeList.add(UserType.DOCTOR);
        userTypeList.add(UserType.NURSING_STAFF);
        userTypeList.add(UserType.MiniAppUSER);

        authApiList.add("/authority/station");
        authApiList.add("/authority/j2cache");
        authApiList.add("/authority/optLog");
        authApiList.add("/authority/loginLog");
        authApiList.add("/authority/parameter");
        authApiList.add("/authority/application");
        authApiList.add("/authority/menu");
        authApiList.add("/authority/resource");
        authApiList.add("/authority/roleAuthority");
        authApiList.add("/authority/role");
        authApiList.add("/authority/user");
        authApiList.add("/authority/userToken");
        authApiList.add("/tenant/globalUserTenant");
        authApiList.add("/tenant/globalUser");

        tenantApiList.add("/tenant/globalUserTenant");
        tenantApiList.add("/tenant/globalUser");
    }


    /**
     * 检查用户类型访问的api 是否被限制。
     * 被限制的api不允许用户访问
     * @param request 请求
     * @param userType token中的用户类型
     * @return 返回 false 表示无权限访问接口
     */
    protected boolean checkApiAuth(ServerHttpRequest request, String userType) {
        if (StrUtil.isEmpty(userType)) {
            return true;
        }
        String methodValue = request.getMethodValue();  // 接口的方法
        String rawPath = request.getURI().getRawPath();
        boolean checkSuccess = true;
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            if (rawPath.startsWith("/tenant/globalUser") && (methodValue.equals("PUT") || methodValue.equals("GET"))) {
                return true;
            } else if (rawPath.startsWith("/tenant/globalUser/reset") && methodValue.equals("PUT")) {
                return true;
            }
        }
        if (UserType.CMS_ADMIN.equals(userType)) {
            if (rawPath.startsWith("/tenant/globalUser") && (methodValue.equals("GET"))) {
                return true;
            }
        }
        if (UserType.GLOBAL_ADMIN.equals(userType)) {
            if (rawPath.startsWith("/tenant/globalUser") || rawPath.startsWith("/tenant/globalUserTenant")) {
                return true;
            }
        }
        if (userTypeList.contains(userType)) {
            for (String s : authApiList) {
                if (rawPath.startsWith(s)) {
                    checkSuccess = false;
                }
            }
        }
        if (userType.equals(UserType.ADMIN)) {
            for (String s : tenantApiList) {
                if (rawPath.startsWith(s)) {
                    checkSuccess = false;
                }
            }
        }
        return checkSuccess;
    }

    /**
     * 忽略应用级token
     *
     * @return
     */
    protected boolean isIgnoreToken(String path) {
        return ignoreTokenProperties.isIgnoreTokenWithOpenApi(path);
    }

    protected String getHeader(String headerName, ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String token = StrUtil.EMPTY;
        if (headers == null || headers.isEmpty()) {
            return token;
        }

        token = headers.getFirst(headerName);

        if (StringUtils.isNotBlank(token)) {
            return token;
        }

        return request.getQueryParams().getFirst(headerName);
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = request.mutate();
        
        BaseContextHandler.setGrayVersion(getHeader(BaseContextConstants.GRAY_VERSION, request));

        AuthInfo authInfo = null;
        try {
            //1, 解码 请求头中的租户信息
            if (!"NONE".equals(multiTenantType)) {
                String base64Tenant = getHeader(JWT_KEY_TENANT, request);
                if (StrUtil.isNotEmpty(base64Tenant)) {
                    String tenant = JwtUtil.base64Decoder(base64Tenant);
                    BaseContextHandler.setTenant(tenant);
                    addHeader(mutate, JWT_KEY_TENANT, BaseContextHandler.getTenant());
                    MDC.put(JWT_KEY_TENANT, BaseContextHandler.getTenant());
                }
            }

            // 2,解码 Authorization 后面完善
            String base64Authorization = getHeader(BASIC_HEADER_KEY, request);
            if (StrUtil.isNotEmpty(base64Authorization)) {
                String[] client = JwtUtil.getClient(base64Authorization);
                BaseContextHandler.setClientId(client[0]);
                addHeader(mutate, JWT_KEY_CLIENT_ID, BaseContextHandler.getClientId());
            }

            // 忽略 token 认证的接口
            if (isIgnoreToken(request.getPath().toString())) {
                log.debug("access filter not execute");
                return chain.filter(exchange);
            }

            //获取token， 解析，然后想信息放入 heade
            //3, 获取token
            String token = getHeader(BEARER_HEADER_KEY, request);

            // 测试环境 token=test 时，写死一个用户信息，便于测试
//            if (isDev(token)) {
//                authInfo = new AuthInfo().setAccount("caring").setUserId(3L)
//                        .setTokenType(BEARER_HEADER_KEY).setName("平台管理员");
//            }

            // 4, 解析 并 验证 token
            authInfo = tokenUtil.getAuthInfo(token);

            // 从token中解析出用户信息后。检查用户身份和接口是否可以正常访问
            if (Objects.nonNull(authInfo)) {
                boolean auth = checkApiAuth(request, authInfo.getUserType());
                if (!auth) {
                    return errorResponse(response, UNAUTHORIZED.getMsg(), UNAUTHORIZED.getCode(), HttpStatus.UNAUTHORIZED);
                }
            }

            // 5，验证 是否在其他设备登录或被挤下线
            String newToken = JwtUtil.getToken(token);
            String tokenKey = CacheKey.buildKey(newToken);
            // todo 缓存改造
            CacheObject tokenCache = channel.get(CacheKey.TOKEN_USER_ID, tokenKey);
            if (tokenCache.getValue() == null) {
                // 为空就认为是没登录或者被T会有bug，该 bug 取决于登录成功后，异步调用UserTokenService.save 方法的延迟
                //  return errorResponse(response, JWT_TOKEN_EXPIRED.getMsg(), JWT_TOKEN_EXPIRED.getCode(), HttpStatus.UNAUTHORIZED);
            } else if (StrUtil.equals(BizConstant.LOGIN_STATUS, (String) tokenCache.getValue())) {
                return errorResponse(response, JWT_OFFLINE.getMsg(), JWT_OFFLINE.getCode(), HttpStatus.UNAUTHORIZED);
            }
        } catch (BizException e) {
             return errorResponse(response, e.getMessage(), e.getCode(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return errorResponse(response, "验证token出错", R.FAIL_CODE, HttpStatus.UNAUTHORIZED);
        }

        //6, 转换，将 token 解析出来的用户身份 和 解码后的tenant、Authorization 重新封装到请求头
        if (authInfo != null) {
            addHeader(mutate, BaseContextConstants.JWT_KEY_ACCOUNT, authInfo.getAccount());
            addHeader(mutate, BaseContextConstants.JWT_KEY_USER_ID, authInfo.getUserId());
            addHeader(mutate, BaseContextConstants.JWT_KEY_NAME, authInfo.getName());
            addHeader(mutate, BaseContextConstants.JWT_KEY_USER_TYPE, authInfo.getUserType());

            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(authInfo.getUserId()));
        }

        ServerHttpRequest build = mutate.build();
        return chain.filter(exchange.mutate().request(build).build());
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (ObjectUtil.isEmpty(value)) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = URLUtil.encode(valueStr);
        mutate.header(name, valueEncode);
    }

    protected Mono<Void> errorResponse(ServerHttpResponse response, String errMsg, int errCode, HttpStatus httpStatus) {
        R tokenError = R.fail(errCode, errMsg);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatusCode(httpStatus);
        DataBuffer dataBuffer = response.bufferFactory().wrap(tokenError.toString().getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

}
