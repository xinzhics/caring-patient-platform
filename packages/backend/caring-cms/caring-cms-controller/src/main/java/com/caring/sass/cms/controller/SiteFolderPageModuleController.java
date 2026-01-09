package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.SiteFolderPageModule;
import com.caring.sass.cms.dto.site.SiteFolderPageModuleSaveDTO;
import com.caring.sass.cms.dto.site.SiteFolderPageModuleUpdateDTO;
import com.caring.sass.cms.dto.site.SiteFolderPageModulePageDTO;
import com.caring.sass.cms.service.SiteFolderPageModuleService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 建站文件夹中的页面表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteFolderPageModule")
@Api(value = "SiteFolderPageModule", tags = "建站页面的组件")
public class SiteFolderPageModuleController extends SuperController<SiteFolderPageModuleService, Long, SiteFolderPageModule, SiteFolderPageModulePageDTO, SiteFolderPageModuleSaveDTO, SiteFolderPageModuleUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteFolderPageModule> siteFolderPageModuleList = list.stream().map((map) -> {
            SiteFolderPageModule siteFolderPageModule = SiteFolderPageModule.builder().build();
            //TODO 请在这里完成转换
            return siteFolderPageModule;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteFolderPageModuleList));
    }


    @GetMapping("batchBuildModuleStyle")
    @ApiOperation("批量处理组件的宽度从350到400")
    public R<String> batchBuildModuleStyle() {

        baseService.batchBuildModuleStyle();
        return R.success("success");

    }

}
