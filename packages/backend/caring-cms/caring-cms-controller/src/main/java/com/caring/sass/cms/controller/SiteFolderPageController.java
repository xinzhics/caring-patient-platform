package com.caring.sass.cms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dto.site.SiteNameCheckDto;
import com.caring.sass.cms.entity.SiteFolderPage;
import com.caring.sass.cms.dto.site.SiteFolderPageSaveDTO;
import com.caring.sass.cms.dto.site.SiteFolderPageUpdateDTO;
import com.caring.sass.cms.dto.site.SiteFolderPagePageDTO;
import com.caring.sass.cms.service.SiteFolderPageService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
@RequestMapping("/siteFolderPage")
@Api(value = "SiteFolderPage", tags = "建站文件夹中的页面表")
public class SiteFolderPageController extends SuperController<SiteFolderPageService, Long, SiteFolderPage, SiteFolderPagePageDTO, SiteFolderPageSaveDTO, SiteFolderPageUpdateDTO> {

    @Autowired
    TenantApi tenantApi;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteFolderPage> siteFolderPageList = list.stream().map((map) -> {
            SiteFolderPage siteFolderPage = SiteFolderPage.builder().build();
            //TODO 请在这里完成转换
            return siteFolderPage;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteFolderPageList));
    }


    @ApiOperation("复制页面")
    @PutMapping("copyPage")
    public R<SiteFolderPage> copyPage(@RequestBody SiteFolderPage siteFolderPage) {

        baseService.copyPage(siteFolderPage);
        return R.success(siteFolderPage);
    }

    @ApiOperation("检查页面模板名称是否可用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageName", value = "模板名称")
    })
    @PostMapping("checkNameExist")
    public R<Boolean> checkNameExist(@RequestBody SiteNameCheckDto siteNameCheckDto) {
        boolean canUse = baseService.nameCanUse(siteNameCheckDto.getFolderName());
        return R.success(canUse);
    }


    @ApiOperation("将页面存为模板")
    @PutMapping("savePageTemplate")
    public R<SiteFolderPage> savePageTemplate(@RequestBody SiteFolderPage siteFolderPage,
                                              String pageName) {

        baseService.savePageTemplate(siteFolderPage, pageName);
        return R.success(siteFolderPage);
    }

    @ApiOperation("将页面存为系统模板")
    @PutMapping("saveSystemPageTemplate")
    public R<Boolean> savePageTemplate(@RequestParam Long pageId) {

        baseService.saveSystemPageTemplate(pageId);
        return R.success(true);
    }

    @Override
    public R<IPage<SiteFolderPage>> page(PageParams<SiteFolderPagePageDTO> params) {

        SiteFolderPagePageDTO model = params.getModel();
        IPage<SiteFolderPage> iPage = params.buildPage();
        LbqWrapper<SiteFolderPage> wrapper = Wraps.<SiteFolderPage>lbQ();
        if (model.getTemplate() != null) {
            wrapper.eq(SiteFolderPage::getTemplate, model.getTemplate());
        }
        if (StrUtil.isNotEmpty(model.getPageName())) {
            wrapper.like(SiteFolderPage::getPageName, model.getPageName());
        }
        if (Objects.nonNull(model.getTemplateType())) {
            wrapper.eq(SiteFolderPage::getTemplateType, model.getTemplateType());
        }
        if (Objects.nonNull(model.getFolderId())) {
            wrapper.eq(SiteFolderPage::getFolderId, model.getFolderId());
        }
        if (StrUtil.isNotEmpty(model.getTenantCode())) {
            BaseContextHandler.setTenant(model.getTenantCode());
        }
        baseService.page(iPage, wrapper);
        return R.success(iPage);
    }

    @ApiOperation("PC预览某一个页面的链接")
    @GetMapping("preview")
    public R<String> preview(@RequestParam Long pageId) {
        String htmlUrl =  baseService.preview(pageId);
        return R.success(htmlUrl);
    }


    @ApiOperation("获取模版页面的所有信息")
    @GetMapping("getTemplatePageInfo")
    public R<SiteFolderPage> getTemplatePageInfo(@RequestParam Long pageId,
                                                 @RequestParam(value = "tenantCode", required = false) String tenantCode) {

        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        SiteFolderPage siteFolderPage = baseService.getTemplatePageInfo(pageId);
        return R.success(siteFolderPage);
    }



    @ApiOperation("C端 使用页面ID获取页面配置")
    @GetMapping("anno/getPage")
    public R<SiteFolderPage> getPage(@RequestParam Long pageId, @RequestParam String domain) {

        SiteFolderPage siteFolderPage = baseService.getPage(pageId, domain);
        return R.success(siteFolderPage);
    }


}
