package com.caring.sass.tenant.api;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.hystrix.GlobalUserApiFallback;
import com.caring.sass.tenant.entity.GlobalUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${caring.feign.authority-server:caring-tenant-server}", fallback = GlobalUserApiFallback.class
        , path = "/globalUser", qualifier = "globalUserApi")
public interface GlobalUserApi {




    @ApiOperation(value = "查询", notes = "查询")
    @GetMapping("/{id}")
    R<GlobalUser> get(@PathVariable(name = "id") Long id);

    /**
     * 批量查询
     *
     * @param data 批量查询
     * @return 查询结果
     */
    @PostMapping("/query")
    R<List<GlobalUser>> query(@RequestBody GlobalUser data);


    @ApiOperation("超管端登录")
    @PostMapping("/admin/login")
    R<GlobalUser> login(@RequestBody GlobalUser globalUser);

    @ApiOperation("手机号登录")
    @PostMapping("/mobile/login")
    R<GlobalUser> mobileLogin(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "password", required = false) String password);


    @ApiOperation("手机号查询")
    @PostMapping("/mobile/query")
    R<GlobalUser> mobile(@RequestParam(name = "mobile") String mobile);
}
