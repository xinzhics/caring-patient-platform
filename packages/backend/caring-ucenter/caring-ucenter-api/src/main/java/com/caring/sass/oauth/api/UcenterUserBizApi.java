package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.UcenterUserBizApiFallback;
import com.caring.sass.user.dto.UserBizDto;
import com.caring.sass.user.dto.UserBizInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @ClassName UserBizApi
 * @Description
 * @Author yangShuai
 * @Date 2022/4/13 10:26
 * @Version 1.0
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = UcenterUserBizApiFallback.class
        , path = "/userBiz", qualifier = "UcenterUserBizApi")
public interface UcenterUserBizApi {

    @ApiOperation("查询用户的名称")
    @PostMapping("/info/query")
    R<Map<String, Map<Long, String>>> queryUserInfo(@RequestBody UserBizDto userBizDto);


    @ApiOperation("查询单人用户的名称")
    @PostMapping("/info/get")
    R<UserBizInfo> queryUserInfo(@RequestBody UserBizInfo userBizInfo);


}
