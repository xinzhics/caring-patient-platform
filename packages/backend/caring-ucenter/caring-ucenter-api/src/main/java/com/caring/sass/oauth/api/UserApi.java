package com.caring.sass.oauth.api;

import com.caring.sass.oauth.api.hystrix.UserApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 用户
 *
 * @author caring
 * @date 2019/07/02
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = UserApiFallback.class
        , path = "/user", qualifier = "userApi")
public interface UserApi {
    /**
     * 刷新token
     *
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/ds/{id}", method = RequestMethod.GET)
    Map<String, Object> getDataScopeById(@PathVariable("id") Long id);

    /**
     * 根据id查询用户
     *
     * @param ids
     * @return
     */
    @GetMapping("/findUserByIds")
    Map<Serializable, Object> findUserByIds(@RequestParam(value = "ids") Set<Serializable> ids);

    /**
     * 根据id查询用户名称
     *
     * @param ids
     * @return
     */
    @GetMapping("/findUserNameByIds")
    Map<Serializable, Object> findUserNameByIds(@RequestParam(value = "ids") Set<Serializable> ids);
}
