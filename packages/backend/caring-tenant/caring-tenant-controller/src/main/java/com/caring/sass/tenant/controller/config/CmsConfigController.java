package com.caring.sass.tenant.controller.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.ChannelApi;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.dto.*;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.context.BaseContextHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xinzh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/cmsConfig")
@Api(value = "cmsConfig", tags = "项目cms配置")
public class CmsConfigController {

    private final ChannelApi channelApi;
    private final ChannelContentApi channelContentApi;

    public CmsConfigController(ChannelApi channelApi, ChannelContentApi channelContentApi) {
        this.channelApi = channelApi;
        this.channelContentApi = channelContentApi;
    }

    /**
     * 分页查询栏目
     *
     * @param params 查询参数
     */
    @ApiOperation(value = "分页查询栏目")
    @PostMapping(value = "/channel/page")
    public R<IPage<Channel>> channelPage(@RequestBody @Validated PageParams<ChannelPageDTO> params) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelApi.page(params);
    }


    /**
     * 新增栏目
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增栏目")
    @PostMapping(value = "channel")
    public R<Channel> save(@RequestBody @Validated ChannelSaveDTO saveDTO) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelApi.save(saveDTO);
    }

    @ApiOperation(value = "获取一个栏目")
    @GetMapping(value = "channel/{id}")
    public R<Channel> getChannel(@PathVariable("id") Long id) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelApi.get(id);
    }

    /**
     * 修改栏目
     *
     * @param updateDTO 修改参数
     */
    @ApiOperation(value = "修改栏目")
    @PutMapping(value = "/channel")
    public R<Channel> channelUpdate(@RequestBody @Validated(SuperEntity.Update.class) ChannelUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelApi.update(updateDTO);
    }

    /**
     * 删除方法
     *
     * @param ids 需要删除的主键
     */
    @ApiOperation(value = "删除栏目")
    @DeleteMapping(value = "/channel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids[]", value = "主键id", dataType = "array", paramType = "query"),
    })
    public R<Boolean> channelDelete(@RequestParam("ids[]") List<Long> ids) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelApi.delete(ids);
    }

    /**
     * 分页查询内容
     *
     * @param params 查询参数
     */
    @ApiOperation(value = "分页查询内容")
    @PostMapping(value = "/content/page")
    public R<IPage<ChannelContent>> contentPage(@RequestBody @Validated PageParams<ChannelContentPageDTO> params) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelContentApi.page(params);
    }

    @ApiOperation(value = "栏目列表")
    @PostMapping(value = "/channel/list")
    public R<List<Channel>> channelList(@RequestBody Channel channel) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelApi.list(channel);
    }

    /**
     * 新增内容
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增内容")
    @PostMapping(value = "content")
    public R<ChannelContent> save(@RequestBody @Validated ChannelContentSaveDTO saveDTO) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelContentApi.save(saveDTO);
    }

    /**
     * 修改内容
     *
     * @param updateDTO 修改参数
     */
    @ApiOperation(value = "修改内容")
    @PutMapping(value = "/content")
    public R<ChannelContent> contentUpdate(@RequestBody @Validated(SuperEntity.Update.class) ChannelContentUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelContentApi.update(updateDTO);
    }

    /**
     * 删除内容
     *
     * @param ids 需要删除的主键
     */
    @ApiOperation(value = "删除内容")
    @DeleteMapping(value = "/content")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids[]", value = "主键id", dataType = "array", paramType = "query"),
    })
    public R<Boolean> contentDelete(@RequestParam("ids[]") List<Long> ids) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        return channelContentApi.delete(ids);
    }
}
