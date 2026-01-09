package com.caring.sass.tenant.api;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.hystrix.H5RouterApiFallback;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${caring.feign.authority-server:caring-tenant-server}", fallback = H5RouterApiFallback.class
        , path = "/h5Router", qualifier = "H5RouterApi")
public interface H5RouterApi {

    @ApiOperation("统计一下path使用了多少次，主要验证护理计划被使用了多少次")
    @GetMapping("/countByPath")
    R<Integer> countByPath(@RequestParam("path") String path);

    @ApiOperation("根据功能分类查询显示的患者菜单")
    @GetMapping("getH5RouterByModuleType")
    R<List<H5Router>> getH5RouterByModuleType(@RequestParam("moduleType") RouterModuleTypeEnum moduleType, @RequestParam("userType") String userType);

    @ApiOperation("查询某一个菜单的信息")
    @GetMapping("getH5Router")
    R<H5Router> getH5Router(@RequestParam("dictItemType") String dictItemType,  @RequestParam("userType") String userType);


    @ApiOperation("查询文件夹的分享链接被用在那个项目")
    @GetMapping({"/checkFolderShareUrlExist"})
    R<List<String>> checkFolderShareUrlExist(@RequestParam("url") String url);


}
