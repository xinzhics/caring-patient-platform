package com.caring.sass.cms.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.entity.SiteVideoWarehouse;
import com.caring.sass.cms.dto.site.SiteVideoWarehouseSaveDTO;
import com.caring.sass.cms.dto.site.SiteVideoWarehouseUpdateDTO;
import com.caring.sass.cms.dto.site.SiteVideoWarehousePageDTO;
import com.caring.sass.cms.service.SiteVideoWarehouseService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.ChineseToEnglishUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 建站视频库
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteVideoWarehouse")
@Api(value = "SiteVideoWarehouse", tags = "建站视频库")
public class SiteVideoWarehouseController extends SuperController<SiteVideoWarehouseService, Long, SiteVideoWarehouse, SiteVideoWarehousePageDTO, SiteVideoWarehouseSaveDTO, SiteVideoWarehouseUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteVideoWarehouse> siteVideoWarehouseList = list.stream().map((map) -> {
            SiteVideoWarehouse siteVideoWarehouse = SiteVideoWarehouse.builder().build();
            //TODO 请在这里完成转换
            return siteVideoWarehouse;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteVideoWarehouseList));
    }


    @Override
    public R<IPage<SiteVideoWarehouse>> page(PageParams<SiteVideoWarehousePageDTO> params) {

        SiteVideoWarehousePageDTO model = params.getModel();
        IPage<SiteVideoWarehouse> buildPage = params.buildPage();
        LbqWrapper<SiteVideoWarehouse> wrapper = Wraps.<SiteVideoWarehouse>lbQ();
        String videoTitle = model.getVideoTitle();
        if (StrUtil.isNotEmpty(videoTitle)) {
            String letter = ChineseToEnglishUtil.getPinYinFirstLetter(videoTitle);
            if (StrUtil.isNotEmpty(letter)) {
                wrapper.and(query -> query.like(SiteVideoWarehouse::getVideoTitle, videoTitle).or().eq(SiteVideoWarehouse::getTitleFirstLetter, letter));
            } else {
                wrapper.and(query -> query.like(SiteVideoWarehouse::getVideoTitle, videoTitle));
            }
        }
        List<Long> videoIds = model.getFilterVideoIds();
        if (CollUtil.isNotEmpty(videoIds)) {
            wrapper.notIn(SuperEntity::getId, videoIds);
        }
        wrapper.eq(SiteVideoWarehouse::getDeleteMark, CommonStatus.NO);
        baseService.page(buildPage, wrapper);
        return R.success(buildPage);
    }

    @Override
    public R<SiteVideoWarehouse> update(SiteVideoWarehouseUpdateDTO siteVideoWarehouseUpdateDTO) {

        SiteVideoWarehouse warehouse = new SiteVideoWarehouse();
        BeanUtils.copyProperties(siteVideoWarehouseUpdateDTO, warehouse);
        warehouse.setId(siteVideoWarehouseUpdateDTO.getId());
        baseService.updateById(warehouse);
        Boolean synchronizeInformation = siteVideoWarehouseUpdateDTO.getSynchronizeInformation();
        if (synchronizeInformation != null && synchronizeInformation) {
            baseService.synchronizeInformation(siteVideoWarehouseUpdateDTO);
        }
        return R.success(warehouse);
    }

    @PutMapping("updateDeleteMarkTrue")
    @ApiOperation("修改为删除状态")
    public R<Boolean> updateDeleteMarkTrue(@RequestParam Long videoId) {

        SiteVideoWarehouse warehouse = new SiteVideoWarehouse();
        warehouse.setId(videoId);
        warehouse.setDeleteMark(CommonStatus.YES);
        baseService.updateById(warehouse);
        return R.success(true);
    }



    @PutMapping("checkVideoUse")
    @ApiOperation("检查视频是否被使用")
    public R<Boolean> checkVideoUse(@RequestParam Long videoId) {
        boolean status = baseService.checkVideoUse(videoId);
        return R.success(status);
    }

}
