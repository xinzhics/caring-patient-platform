package com.caring.sass.cms;

import com.caring.sass.base.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @className: CmsKeyWordApi
 * @author: 杨帅
 * @date: 2023/9/15
 */
@Component
@FeignClient(name = "${caring.feign.cms-server:caring-cms-server}", path = "/cmsKeyWord", qualifier = "CmsKeyWordApi")
public interface CmsKeyWordApi {

    @ApiOperation("医生通过ai助手取消订阅关键词")
    @DeleteMapping("cancelSubscribeKeyWord")
    R<Boolean> cancelSubscribeKeyWord(@RequestParam("doctorId") Long doctorId, @RequestParam("keyWord") String keyWord);

}
