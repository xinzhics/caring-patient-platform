package com.caring.sass.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dto.ContentCollectPageDTO;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.ContentCollect;
import com.caring.sass.cms.entity.StudioCms;
import com.caring.sass.cms.entity.StudioCollect;
import com.caring.sass.cms.dto.StudioCollectSaveDTO;
import com.caring.sass.cms.dto.StudioCollectUpdateDTO;
import com.caring.sass.cms.dto.StudioCollectPageDTO;
import com.caring.sass.cms.service.StudioCmsService;
import com.caring.sass.cms.service.StudioCollectService;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 医生CMS收藏记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-11-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/studioCollect")
@Api(value = "StudioCollect", tags = "医生CMS收藏记录")
public class StudioCollectController extends SuperController<StudioCollectService, Long, StudioCollect, StudioCollectPageDTO, StudioCollectSaveDTO, StudioCollectUpdateDTO> {

    @Autowired
    StudioCmsService studioCmsService;



    /**
     * @Author yangShuai
     * @Description 我的收藏中可能存在系统级别的 cms
     * @Date 2021/4/20 15:26
     */
    @ApiOperation("我的收藏")
    @Override
    public R<IPage<StudioCollect>> page(PageParams<StudioCollectPageDTO> params) {
        IPage<StudioCollect> page = params.buildPage();
        StudioCollectPageDTO model = params.getModel();
        LbqWrapper<StudioCollect> query = Wraps.<StudioCollect>lbQ()
                .eq(StudioCollect::getUserId, model.getUserId())
                .eq(StudioCollect::getCollectStatus, 1)
                .orderByDesc(StudioCollect::getCreateTime);
        page = baseService.page(page, query);
        List<StudioCollect> records = page.getRecords();
        List<String> collect = records.stream().map(record -> record.getCmsId().toString()).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return R.success(page);
        }
        List<StudioCms> studioCms = studioCmsService.listByIds(collect);

        Map<Long, StudioCms> contentMap = studioCms.stream().collect(Collectors.toMap(StudioCms::getId, item -> item, (channelContent, channelContent2) -> channelContent2));

        records.forEach(item -> item.setStudioCms(contentMap.get(item.getCmsId())));

        return R.success(page);
    }
}
