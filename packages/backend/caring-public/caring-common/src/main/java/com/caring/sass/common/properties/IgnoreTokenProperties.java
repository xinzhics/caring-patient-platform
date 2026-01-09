package com.caring.sass.common.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 忽略token 配置类
 * <p>
 * 做接口权限时，考虑修改成读取配置文件
 *
 * @author caring
 * @date 2019/01/03
 */
@Data
@ConfigurationProperties(prefix = IgnoreTokenProperties.PREFIX)
public class IgnoreTokenProperties {
    public static final String PREFIX = "ignore.token";
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private List<String> baseUri = CollUtil.newArrayList(
            "/error",
            "/actuator/**",
            "/gate/**",
            "/static/**",
            "/anno/**",
            "/**/anno/**",
            "/wx/*/callback",
            "/**/swagger-ui.html",
            "/**/doc.html",
            "/**/webjars/**",
            "/**/v2/api-docs/**",
            "/**/v2/api-docs-ext/**",
            "/**/swagger-resources/**"
    );

    private List<String> tenant = CollUtil.newArrayList("/**/noTenant/**");
    private List<String> token = CollUtil.newArrayList("/**/noToken/**", "/ds/**");

    public boolean isIgnoreToken(String path) {
        List<String> all = new ArrayList<>();
        all.addAll(getBaseUri());
        all.addAll(getToken());
        return all.stream().anyMatch(url -> path.startsWith(url) || ANT_PATH_MATCHER.match(url, path));
    }

    public boolean isIgnoreTokenWithOpenApi(String path) {
        List<String> all = new ArrayList<>();
        all.addAll(getBaseUri());
        all.addAll(getToken());
        all.add("/**/open/**");
        return all.stream().anyMatch(url -> path.startsWith(url) || ANT_PATH_MATCHER.match(url, path));
    }
    public boolean isIgnoreTenant(String path) {
        List<String> all = new ArrayList<>();
        all.addAll(getBaseUri());
        all.addAll(getTenant());
        return all.stream().anyMatch(url -> path.startsWith(url) || ANT_PATH_MATCHER.match(url, path));
    }

    public boolean thirdPartyMatch(String path) {
        return ANT_PATH_MATCHER.match("/**/ykzh/**", path);
    }
}
