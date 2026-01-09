package com.caring.sass.cms.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.cms.entity.Banner;
import com.caring.sass.cms.dto.BannerSaveDTO;
import com.caring.sass.cms.dto.BannerUpdateDTO;
import com.caring.sass.cms.dto.BannerPageDTO;
import com.caring.sass.cms.service.BannerService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;

import javax.validation.constraints.NotNull;


/**
 * <p>
 * 前端控制器
 * banner
 * </p>
 *
 * @author 杨帅
 * @date 2023-12-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/banner")
@Api(value = "Banner", tags = "banner")
public class BannerController extends SuperController<BannerService, Long, Banner, BannerPageDTO, BannerSaveDTO, BannerUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<Banner> bannerList = list.stream().map((map) -> {
            Banner banner = Banner.builder().build();
            //TODO 请在这里完成转换
            return banner;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(bannerList));
    }

    @Override
    public R<List<Banner>> query(Banner data) {
        String tenantCode = data.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }

        R<List<Banner>> query = super.query(data);
        List<Banner> banners = query.getData();
        if (CollUtil.isNotEmpty(banners)) {
            banners.sort((o1, o2) -> {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                } else if (o1.getOrder() < o2.getOrder()) {
                    return -1;
                } else {
                    return 0;
                }
            });
        }
        return query;
    }


    @Override
    public R<Banner> save(BannerSaveDTO bannerSaveDTO) {
        @NotNull String tenantCode = bannerSaveDTO.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        return super.save(bannerSaveDTO);
    }

    @Override
    public R<Banner> update(BannerUpdateDTO bannerUpdateDTO) {
        @NotNull String tenantCode = bannerUpdateDTO.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        return super.update(bannerUpdateDTO);
    }

    @ApiOperation("批量修改")
    @PutMapping("updateBatchOrder")
    public R<Boolean> updateBatchOrder(@RequestBody @Validated List<BannerUpdateDTO> bannerUpdateDTOS) {

        if (CollUtil.isNotEmpty(bannerUpdateDTOS)) {
            BannerUpdateDTO dto = bannerUpdateDTOS.get(0);
            @NotNull String tenantCode = dto.getTenantCode();
            BaseContextHandler.setTenant(tenantCode);
            List<Banner> banners = new ArrayList<>();
            for (BannerUpdateDTO updateDTO : bannerUpdateDTOS) {
                Banner banner = new Banner();
                BeanUtils.copyProperties(updateDTO, banner);
                banners.add(banner);
            }
            baseService.updateBatchById(banners);
        }
        return R.success(true);
    }

    @ApiOperation("删除banner")
    @DeleteMapping("delete/tenantCode")
    public R<Boolean> delete(@RequestParam Long id, @RequestParam String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.removeById(id);
        return R.success(true);
    }
}
