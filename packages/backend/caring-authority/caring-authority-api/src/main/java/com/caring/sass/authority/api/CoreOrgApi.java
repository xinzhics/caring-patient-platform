package com.caring.sass.authority.api;

import com.caring.sass.authority.api.hystrix.CoreOrgApiFallback;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户
 *
 * @author caring
 * @date 2019/07/02
 */
@FeignClient(name = "${caring.feign.authority-server:caring-authority-server}", fallback = CoreOrgApiFallback.class
        , path = "/org", qualifier = "CoreOrgApi")
public interface CoreOrgApi {

    @GetMapping({"/{id}"})
    R<Org> get(@PathVariable("id") Long id);

    @GetMapping("getByCode/{code}")
    R<Org> getByCode(@PathVariable("code") String code);

    @GetMapping("/queryByTenant")
    R<Org> queryByTenant();
}
