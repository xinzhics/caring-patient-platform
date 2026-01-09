package com.caring.sass.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.entity.StudioCms;
import com.caring.sass.cms.entity.StudioContentReply;
import com.caring.sass.cms.dto.StudioContentReplySaveDTO;
import com.caring.sass.cms.dto.StudioContentReplyUpdateDTO;
import com.caring.sass.cms.dto.StudioContentReplyPageDTO;
import com.caring.sass.cms.service.StudioCmsService;
import com.caring.sass.cms.service.StudioContentReplyService;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 医生cms留言表
 * </p>
 *
 * @author 杨帅
 * @date 2025-11-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/studioContentReply")
@Api(value = "StudioContentReply", tags = "医生cms留言表")
public class StudioContentReplyController extends SuperController<StudioContentReplyService, Long, StudioContentReply, StudioContentReplyPageDTO, StudioContentReplySaveDTO, StudioContentReplyUpdateDTO> {

    @Autowired
    StudioCmsService studioCmsService;


    @Override
    public R<IPage<StudioContentReply>> page(PageParams<StudioContentReplyPageDTO> params) {

        IPage<StudioContentReply> builtPage = params.buildPage();
        StudioContentReplyPageDTO model = params.getModel();
        LbqWrapper<StudioContentReply> query = Wraps.<StudioContentReply>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(StudioContentReply::getCCmsId, model.getCCmsId());
        baseService.page(builtPage, query);

        return R.success(builtPage);
    }

    @ApiOperation("评论的文章")
    @PostMapping("pageContent")
    public R<IPage<StudioContentReply>> pageContent(@RequestBody PageParams<StudioContentReplyPageDTO> params) {
        IPage<StudioContentReply> page = params.buildPage();
        StudioContentReplyPageDTO model = params.getModel();
        LbqWrapper<StudioContentReply> query = Wraps.<StudioContentReply>lbQ()
                .select(StudioContentReply::getCCmsId)
                .eq(StudioContentReply::getCReplierId, model.getCReplierId())
                .groupBy(StudioContentReply::getCCmsId)
                .orderByDesc(StudioContentReply::getCreateTime);
        page = baseService.page(page, query);
        List<StudioContentReply> records = page.getRecords();
        List<String> collect = records.stream().map(record -> record.getCCmsId().toString()).collect(Collectors.toList());


        if (collect.isEmpty()) {
            return R.success(page);
        }
        List<StudioCms> studioCms = studioCmsService.listByIds(collect);

        Map<Long, StudioCms> contentMap = studioCms.stream().collect(Collectors.toMap(StudioCms::getId, item -> item, (channelContent, channelContent2) -> channelContent2));

        records.forEach(item -> item.setStudioCms(contentMap.get(item.getCCmsId())));

        return R.success(page);
    }


}
