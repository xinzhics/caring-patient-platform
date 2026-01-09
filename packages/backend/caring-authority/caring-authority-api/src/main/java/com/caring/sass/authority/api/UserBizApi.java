package com.caring.sass.authority.api;

import com.caring.sass.authority.api.hystrix.UserBizApiFallback;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.base.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 用户
 *
 * @author caring
 * @date 2019/07/02
 */
@FeignClient(name = "${caring.feign.authority-server:caring-authority-server}", fallback = UserBizApiFallback.class
        , path = "/user", qualifier = "userBizApi")
public interface UserBizApi {

    /**
     * 查询所有的用户id
     *
     * @return
     */
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    R<List<Long>> findAllUserId();


    @ApiOperation("查询用户列表")
    @PutMapping("/findUserList")
    R<List<User>> findUserList(@RequestBody Set<Serializable> ids);
}
