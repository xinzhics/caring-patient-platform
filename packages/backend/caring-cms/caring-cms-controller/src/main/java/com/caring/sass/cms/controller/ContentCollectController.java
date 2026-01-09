package com.caring.sass.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dto.ContentCollectPageDTO;
import com.caring.sass.cms.dto.ContentCollectSaveDTO;
import com.caring.sass.cms.dto.ContentCollectUpdateDTO;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.ContentCollect;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.ContentCollectService;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/contentCollect")
@Api(value = "ContentCollect", tags = "文章收藏")
//@PreAuth(replace = "contentReply:")
public class ContentCollectController extends SuperController<ContentCollectService, Long, ContentCollect, ContentCollectPageDTO, ContentCollectSaveDTO, ContentCollectUpdateDTO> {

    @Autowired
    ChannelContentService channelContentService;

    @Override
    public R<ContentCollect> save(@RequestBody ContentCollectSaveDTO contentCollectSaveDTO) {
        if (contentCollectSaveDTO.getUserId() == null) {
            Long userId = BaseContextHandler.getUserId();
            contentCollectSaveDTO.setUserId(userId);
        }

        return super.save(contentCollectSaveDTO);
    }


    /**
     * @Author yangShuai
     * @Description 我的收藏中可能存在系统级别的 cms
     * @Date 2021/4/20 15:26
     */
    @ApiOperation("我的收藏")
    @Override
    public R<IPage<ContentCollect>> page(PageParams<ContentCollectPageDTO> params) {
        IPage<ContentCollect> page = params.buildPage();
        ContentCollectPageDTO model = params.getModel();
        LbqWrapper<ContentCollect> query = Wraps.<ContentCollect>lbQ()
                .eq(ContentCollect::getUserId, model.getUserId())
                .eq(ContentCollect::getCollectStatus, 1)
                .eq(ContentCollect::getRoleType, model.getRoleType())
                .orderByDesc(ContentCollect::getCreateTime);
        page = baseService.page(page, query);
        List<ContentCollect> records = page.getRecords();
        List<String> collect = records.stream().map(record -> record.getContentId().toString()).collect(Collectors.toList());

        List<ChannelContent> channelContents = channelContentService.listNoTenantCode(collect);

        Map<Long, ChannelContent> contentMap = channelContents.stream().collect(Collectors.toMap(ChannelContent::getId, item -> item, (channelContent, channelContent2) -> channelContent2));

        records.forEach(item -> item.setChannelContent(contentMap.get(item.getContentId())));

        return R.success(page);
    }
}
