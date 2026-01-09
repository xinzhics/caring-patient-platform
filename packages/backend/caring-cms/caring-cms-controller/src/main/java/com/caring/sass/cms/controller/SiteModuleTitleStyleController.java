package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.SiteModuleTitleStyle;
import com.caring.sass.cms.dto.site.SiteModuleTitleStyleSaveDTO;
import com.caring.sass.cms.dto.site.SiteModuleTitleStyleUpdateDTO;
import com.caring.sass.cms.dto.site.SiteModuleTitleStylePageDTO;
import com.caring.sass.cms.service.SiteModuleTitleStyleService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 建站组件主题样式表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteModuleTitleStyle")
@Api(value = "SiteModuleTitleStyle", tags = "建站组件主题样式表")
public class SiteModuleTitleStyleController extends SuperController<SiteModuleTitleStyleService, Long, SiteModuleTitleStyle, SiteModuleTitleStylePageDTO, SiteModuleTitleStyleSaveDTO, SiteModuleTitleStyleUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteModuleTitleStyle> siteModuleTitleStyleList = list.stream().map((map) -> {
            SiteModuleTitleStyle siteModuleTitleStyle = SiteModuleTitleStyle.builder().build();
            //TODO 请在这里完成转换
            return siteModuleTitleStyle;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteModuleTitleStyleList));
    }
}
