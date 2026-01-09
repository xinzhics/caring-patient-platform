package com.caring.sass.gateway.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.properties.IgnoreTokenProperties;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.jwt.utils.JwtUtil;
import com.caring.sass.oauth.dto.ClientInfo;
import com.caring.sass.utils.StrPool;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.caring.sass.context.BaseContextConstants.*;

/**
 * 开放平台客户端认证
 *
 * @author xinz
 */
@Component
public class OpenApiGatewayFilterFactory extends AbstractGatewayFilterFactory {
    private static final String CLIENT_ID = "client_id";

    private static final String TENANT_CODE = "tenant_code";

    @Value("${spring.profiles.active:dev}")
    protected String profiles;

    @Autowired
    private IgnoreTokenProperties ignoreTokenProperties;

    protected boolean isDev(String token) {
        return !StrPool.PROD.equalsIgnoreCase(profiles)
                && (StrPool.TEST_TOKEN.equalsIgnoreCase(token)
                || StrPool.TEST.equalsIgnoreCase(token));
    }

    protected boolean isIgnoreToken(String path) {
        return ignoreTokenProperties.isIgnoreToken(path);
    }

    protected String getHeader(String headerName, ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String token = StrUtil.EMPTY;
        if (headers.isEmpty()) {
            return token;
        }

        token = headers.getFirst(headerName);

        if (StringUtils.isNotBlank(token)) {
            return token;
        }

        return request.getQueryParams().getFirst(headerName);
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (ObjectUtil.isEmpty(value)) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = URLUtil.encode(valueStr);
        mutate.header(name, valueEncode);
    }

    protected Mono<Void> errorResponse(ServerHttpResponse response, String errMsg, int errCode) {
        R tokenError = R.fail(errCode, errMsg);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        DataBuffer dataBuffer = response.bufferFactory().wrap(tokenError.toString().getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public GatewayFilter apply(Object  config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest.Builder mutate = request.mutate();

            ClientInfo clientInfo = null;
            try {
                // 忽略 token 认证的接口
                String path = request.getPath().toString();
                if (isIgnoreToken(path)) {
                    return chain.filter(exchange);
                }

                /*
                  该平台无法进行权限校验，暂时写死
                 */
                if (ignoreTokenProperties.thirdPartyMatch(path)) {
                    BaseContextHandler.setClientId("yikangzhihu_open_client");
                    addHeader(mutate, JWT_KEY_CLIENT_ID, BaseContextHandler.getClientId());

                    BaseContextHandler.setTenant("0396");
                    addHeader(mutate, JWT_KEY_TENANT, BaseContextHandler.getTenant());
                    MDC.put(JWT_KEY_TENANT, BaseContextHandler.getTenant());

                    ServerHttpRequest build = mutate.build();
                    return chain.filter(exchange.mutate().request(build).build());
                }

                String token = getHeader(BEARER_HEADER_KEY, request);
                // 测试环境写死一个项目，方便调试
                if (isDev(token)) {
                    clientInfo = new ClientInfo().setClientId("caring_ui").setClientCode("caring_ui_secret");
                }
                // 解析 并 验证 token
                if (clientInfo == null) {
                    String newToken = JwtUtil.getToken(token);
                    Claims claims = JwtUtil.parseJWT(newToken);
                    String clientId = Convert.toStr(claims.get(CLIENT_ID));
                    String clientCode = Convert.toStr(claims.get(TENANT_CODE));
                    boolean noClient = StrUtil.isBlank(clientId) || StrUtil.isBlank(clientCode);
                    if (noClient) {
                        return errorResponse(response, "验证token出错", R.FAIL_CODE);
                    }
                    clientInfo = new ClientInfo().setClientId(clientId).setClientCode(clientCode);
                }
            } catch (Exception e) {
                return errorResponse(response, "验证token出错", R.FAIL_CODE);
            }

            //将 token 解析出来的用户身份 和 解码后的tenant 重新封装到请求头
            if (clientInfo != null) {
                BaseContextHandler.setClientId(clientInfo.getClientId());
                addHeader(mutate, JWT_KEY_CLIENT_ID, BaseContextHandler.getClientId());

                BaseContextHandler.setTenant(clientInfo.getClientCode());
                addHeader(mutate, JWT_KEY_TENANT, BaseContextHandler.getTenant());
                MDC.put(JWT_KEY_TENANT, BaseContextHandler.getTenant());
            }

            ServerHttpRequest build = mutate.build();
            return chain.filter(exchange.mutate().request(build).build());

        };
    }
}
