package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.RoleApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 角色API
 *
 * @author caring
 * @date 2019/08/02
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", path = "/role", fallback = RoleApiFallback.class)
public interface RoleApi {
    /**
     * 根据角色编码，查找用户id
     *
     * @param codes 角色编码
     * @return
     */
    @GetMapping("/codes")
    R<List<Long>> findUserIdByCode(@RequestParam(value = "codes") String[] codes);
}
