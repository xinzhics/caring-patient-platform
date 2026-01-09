package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.SiteModuleLabel;
import com.caring.sass.cms.dto.site.SiteModuleLabelSaveDTO;
import com.caring.sass.cms.dto.site.SiteModuleLabelUpdateDTO;
import com.caring.sass.cms.dto.site.SiteModuleLabelPageDTO;
import com.caring.sass.cms.service.SiteModuleLabelService;
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
 * 建站多功能导航标签
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteModuleLabel")
@Api(value = "SiteModuleLabel", tags = "建站多功能导航标签")
public class SiteModuleLabelController extends SuperController<SiteModuleLabelService, Long, SiteModuleLabel, SiteModuleLabelPageDTO, SiteModuleLabelSaveDTO, SiteModuleLabelUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteModuleLabel> siteModuleLabelList = list.stream().map((map) -> {
            SiteModuleLabel siteModuleLabel = SiteModuleLabel.builder().build();
            //TODO 请在这里完成转换
            return siteModuleLabel;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteModuleLabelList));
    }
}
