package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.MiniappInfoApiFallback;
import com.caring.sass.user.entity.MiniappInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Component
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}" ,fallback = MiniappInfoApiFallback.class
        , path = "/miniappInfo", qualifier = "MiniappInfoApi")
public interface MiniappInfoApi  {


    @ApiOperation("使用小程序的openId获取用户信息")
    @GetMapping("findMiniAppUserByOpenId")
    R<MiniappInfo> findByOpenId(@RequestParam(name = "openId") String openId, @RequestParam(name = "appId") String appId);


    @ApiOperation("创建或者更新微信小程序用户信息")
    @PostMapping("createOrUpdateUser")
    R<MiniappInfo> createOrUpdateUser(@RequestBody @Validated MiniappInfo miniappInfo);


    @ApiOperation("设置用户已经提醒过关注")
    @PutMapping("setRemindSubscriptionMassageTrueByPhone")
    R<Boolean> setRemindSubscriptionMassageTrueByPhone(@RequestParam(name = "phoneNumber") String phoneNumber);

    @ApiOperation("查询用户信息通过ID")
    @GetMapping("selectByIdNoTenant/{id}")
    R<MiniappInfo> selectByIdNoTenant(@PathVariable("id") Long id);

}
