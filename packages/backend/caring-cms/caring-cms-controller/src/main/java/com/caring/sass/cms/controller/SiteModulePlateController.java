package com.caring.sass.cms.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.entity.SiteModulePlate;
import com.caring.sass.cms.dto.site.SiteModulePlateSaveDTO;
import com.caring.sass.cms.dto.site.SiteModulePlateUpdateDTO;
import com.caring.sass.cms.dto.site.SiteModulePlatePageDTO;
import com.caring.sass.cms.service.SiteModulePlateService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 建站组件的板块表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteModulePlate")
@Api(value = "SiteModulePlate", tags = "建站组件的板块表")
public class SiteModulePlateController extends SuperController<SiteModulePlateService, Long, SiteModulePlate, SiteModulePlatePageDTO, SiteModulePlateSaveDTO, SiteModulePlateUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteModulePlate> siteModulePlateList = list.stream().map((map) -> {
            SiteModulePlate siteModulePlate = SiteModulePlate.builder().build();
            //TODO 请在这里完成转换
            return siteModulePlate;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteModulePlateList));
    }


    @PostMapping("anno/platePage")
    @ApiOperation("搜索组件查询页面的板块")
    public R<IPage<SiteModulePlate>> platePage(@RequestBody @Validated PageParams<SiteModulePlatePageDTO> params) {
        SiteModulePlatePageDTO model = params.getModel();
        LbqWrapper<SiteModulePlate> wrapper = Wraps.<SiteModulePlate>lbQ();
        wrapper.in(SiteModulePlate::getPageId, model.getPageId());
        IPage<SiteModulePlate> buildPage = params.buildPage();
        if (CollUtil.isEmpty(model.getPageId())) {
            return R.success(buildPage);
        }
        wrapper.like(SiteModulePlate::getPlateTitle, model.getPlateTitle());
        baseService.page(buildPage, wrapper);
        List<SiteModulePlate> buildPageRecords = buildPage.getRecords();
        baseService.setPlateJump(buildPageRecords);
        return R.success(buildPage);
    }



}
