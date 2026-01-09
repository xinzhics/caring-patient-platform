package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.SystemMsgApiFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", path = "/systemMsg", fallback = SystemMsgApiFallback.class)
public interface SystemMsgApi {

    @GetMapping("countMessage")
    @ApiOperation("统计医生未读的患者关注消息")
    R<Integer> countMessage(@RequestParam(name = "doctorId") Long doctorId);
}
