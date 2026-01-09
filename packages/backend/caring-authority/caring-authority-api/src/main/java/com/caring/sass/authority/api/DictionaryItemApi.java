package com.caring.sass.authority.api;

import com.caring.sass.authority.api.hystrix.DictionaryItemFallback;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.base.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "${caring.feign.authority-server:caring-authority-server}", fallback = DictionaryItemFallback.class
        , path = "/dictionaryItem/anno", qualifier = "userBizApi")
public interface DictionaryItemApi {

    @ApiOperation("根据租户查询项目的字典")
    @GetMapping("queryTenantDict")
    R<List<DictionaryItem>> queryTenantDict(@RequestParam("tenantCode") String tenantCode);


}
