package com.caring.sass.tenant.api;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.hystrix.PatientManageMenuApiFallback;
import com.caring.sass.tenant.entity.router.PatientManageMenu;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @className: PatientManageMenuApi
 * @author: 杨帅
 * @date: 2023/8/17
 */
@FeignClient(name = "${caring.feign.authority-server:caring-tenant-server}", fallback = PatientManageMenuApiFallback.class
        , path = "/patientManageMenu", qualifier = "PatientManageMenuApi")
public interface PatientManageMenuApi {



    @ApiOperation("查询患者管理平台的菜单")
    @GetMapping("queryList")
    R<List<PatientManageMenu>> queryList(@RequestParam(required = false, name = "code") String code);

}
