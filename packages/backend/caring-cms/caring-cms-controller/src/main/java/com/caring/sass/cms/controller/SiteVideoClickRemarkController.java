package com.caring.sass.cms.controller;

import com.caring.sass.cms.dto.site.SiteVideoClickRemarkPageDTO;
import com.caring.sass.cms.dto.site.SiteVideoClickRemarkSaveDTO;
import com.caring.sass.cms.dto.site.SiteVideoClickRemarkUpdateDTO;
import com.caring.sass.cms.entity.SiteVideoClickRemark;
import com.caring.sass.cms.service.SiteVideoClickRemarkService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 视频点击播放
 * </p>
 *
 * @author 杨帅
 * @date 2023-11-07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteVideoClickRemark")
@Api(value = "SiteVideoClickRemark", tags = "视频点击播放")
public class SiteVideoClickRemarkController extends SuperController<SiteVideoClickRemarkService, Long, SiteVideoClickRemark, SiteVideoClickRemarkPageDTO, SiteVideoClickRemarkSaveDTO, SiteVideoClickRemarkUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteVideoClickRemark> siteVideoClickRemarkList = list.stream().map((map) -> {
            SiteVideoClickRemark siteVideoClickRemark = SiteVideoClickRemark.builder().build();
            //TODO 请在这里完成转换
            return siteVideoClickRemark;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteVideoClickRemarkList));
    }


    @ApiOperation("无授权新增")
    @PostMapping("/anno/save")
    public R<SiteVideoClickRemark> annoSave(@RequestBody SiteVideoClickRemarkSaveDTO siteVideoClickRemarkSaveDTO) {
        SiteVideoClickRemark clickRemark = new SiteVideoClickRemark();
        BeanUtils.copyProperties(siteVideoClickRemarkSaveDTO, clickRemark);
        baseService.save(clickRemark);
        return R.success(clickRemark);
    }
}
