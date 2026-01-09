package com.caring.sass.cms.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dao.ChannelGroupMapper;
import com.caring.sass.cms.dto.ChanelGroupImportDto;
import com.caring.sass.cms.dto.ChannelPageDTO;
import com.caring.sass.cms.dto.ChannelSaveDTO;
import com.caring.sass.cms.dto.ChannelUpdateDTO;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.ChannelGroup;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.ChannelService;
import com.caring.sass.cms.service.impl.ChannelGroupImportBizService;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 栏目
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/channel")
@Api(value = "Channel", tags = "栏目")
//@PreAuth(replace = "channel:")
public class ChannelController extends SuperController<ChannelService, Long, Channel, ChannelPageDTO, ChannelSaveDTO, ChannelUpdateDTO> {

    private final ChannelContentService contentService;

    @Autowired
    ChannelGroupMapper channelGroupMapper;

    @Autowired
    ChannelGroupImportBizService channelGroupImportBizService;


    public ChannelController(ChannelContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * 对分页结果数据重写
     */
    @Override
    public void handlerResult(IPage<Channel> page) {
        List<Channel> c = page.getRecords();
        for (Channel channel : c) {
            int count = contentService.count(Wraps.<ChannelContent>lbQ().eq(ChannelContent::getChannelId, channel.getId()));
            channel.setArticleCount(count);
        }
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Channel> channelList = list.stream().map((map) -> {
            Channel channel = Channel.builder().build();
            //TODO 请在这里完成转换
            return channel;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(channelList));
    }

    @ApiOperation("修改栏目的排序")
    @PutMapping("/updateSort/{channelId}")
    public R<Boolean> updateSort(@PathVariable("channelId") Long channelId, @RequestParam("sort") Integer sort) {
        Boolean aBoolean = baseService.updateSort(channelId, sort);
        return R.success(aBoolean);
    }

    @ApiOperation("初始化栏目信息")
    @PostMapping("initChannel")
    public R<Boolean> initChannel() {
        baseService.initChannel();
        return R.success();
    }

    @ApiOperation("复制栏目及栏目内容和ChannelGroup")
    @PostMapping("copyChannelAndChannelContent")
    public R<Boolean> copyChannelAndChannelContent(@RequestParam("fromTenantCode") @NotEmpty(message = "待复制的项目编码不能为空") String fromTenantCode,
                                                   @RequestParam("toTenantCode") @NotEmpty(message = "目标项目编码不能为空") String toTenantCode) {
        baseService.copyChannelAndChannelContent(fromTenantCode, toTenantCode);
        return R.success();
    }


    @ApiOperation("查询系统栏目")
    @GetMapping("getSystemChannel")
    public R<List<Channel>> systemChannel() {

        BaseContextHandler.setTenant(BizConstant.SUPER_ADMIN);
        List<Channel> channelList = baseService.list();
        return R.success(channelList);
    }


    @ApiOperation("删除页面")
    @DeleteMapping("deleteChannelGroup/{id}")
    public R<Boolean> deleteChannelGroup(@PathVariable Long id) {
        baseService.deleteChannelGroup(id);
        return R.success(true);
    }


    @ApiOperation("查询栏目组")
    @GetMapping("getChannelGroup")
    public R<List<ChannelGroup>> getChannelGroup() {
        LbqWrapper<ChannelGroup> wrapper = Wraps.lbQ();
        wrapper.orderByDesc(SuperEntity::getCreateTime);
        List<ChannelGroup> channelGroups = channelGroupMapper.selectList(wrapper);
        return R.success(channelGroups);
    }

    @ApiOperation("将一个列表的创建时间按ids顺序更换")
    @PostMapping("changeChangeGroupCreateTime")
    public R<List<ChannelGroup>> changeChangeGroupCreateTime(@RequestBody List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return R.success(null);
        }
        LbqWrapper<ChannelGroup> wrapper = Wraps.<ChannelGroup>lbQ().in(SuperEntity::getId, ids);
        wrapper.orderByDesc(SuperEntity::getCreateTime);
        List<ChannelGroup> channelGroups = channelGroupMapper.selectList(wrapper);
        List<ChannelGroup> result = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            LocalDateTime createTime = channelGroups.get(i).getCreateTime();
            ChannelGroup channelGroup = new ChannelGroup();
            channelGroup.setId(ids.get(i));
            channelGroup.setCreateTime(createTime);
            channelGroupMapper.updateById(channelGroup);
            result.add(channelGroup);
        }
        return R.success(result);
    }


    @ApiOperation("无授权分页获取栏目信息")
    @PostMapping("anno/page")
    public R<IPage<Channel>> annoPage(@RequestBody PageParams<ChannelPageDTO> params){
        return this.page(params);
    }

    @Override
    public R<IPage<Channel>> page(PageParams<ChannelPageDTO> params) {
        IPage<Channel> page = params.buildPage();
        ChannelPageDTO model = params.getModel();
        Map<String, String> map = params.getMap();
        LbqWrapper<Channel> wrapper = Wraps.<Channel>lbQ();
        if (model.getChannelGroupId() == null) {
            wrapper.isNull(Channel::getChannelGroupId);
        } else {
            wrapper.eq(Channel::getChannelGroupId, model.getChannelGroupId());
        }
        if (model.getDoctorOwner() != null) {
            wrapper.eq(Channel::getDoctorOwner, model.getDoctorOwner());
        }
        wrapper.eq(Channel::getLibraryId, model.getLibraryId());
        if (CollectionUtils.isNotEmpty(map)) {
            String createTimeSt = map.get("createTime_st");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotEmptyString(createTimeSt)) {
                try {
                    LocalDateTime parse = LocalDateTime.parse(createTimeSt, df);
                    wrapper.ge(SuperEntity::getCreateTime, parse);
                } catch (Exception e) {
                    throw new BizException("时间格式有错误");
                }
            }
            String createTimeEd = map.get("createTime_ed");
            if (StringUtils.isNotEmptyString(createTimeEd)) {
                try {
                    LocalDateTime parse = LocalDateTime.parse(createTimeEd, df);
                    wrapper.le(SuperEntity::getCreateTime, parse);
                } catch (Exception e) {
                    throw new BizException("时间格式有错误");
                }
            }
        }
        if (StringUtils.isNotEmptyString(model.getTenantCode())) {
            BaseContextHandler.setTenant(model.getTenantCode());
        }
        wrapper.orderByDesc(Channel::getSort);
        page = baseService.page(page, wrapper);
        return R.success(page);
    }

    @ApiOperation("查询租户下的栏目")
    @PostMapping("queryByTenantCode/{tenantCode}")
    public R<List<Channel>> queryByTenantCode(@PathVariable("tenantCode") String tenantCode, @RequestBody Channel data) {
        BaseContextHandler.setTenant(tenantCode);
        return this.query(data);
    }

    @PutMapping("webQuery")
    @ApiOperation("项目端查询栏目")
    public R<List<Channel>> webQuery(@RequestBody Channel data) {
        LbqWrapper<Channel> wrapper = Wraps.<Channel>lbQ();
        if (data.getChannelGroupId() != null) {
            wrapper.eq(Channel::getChannelGroupId, data.getChannelGroupId());
        }
        if (data.getDoctorOwner() != null) {
            wrapper.eq(Channel::getDoctorOwner, data.getDoctorOwner());
        }
        if (StringUtils.isNotEmptyString(data.getTenantCode())) {
            BaseContextHandler.setTenant(data.getTenantCode());
        }
        wrapper.eq(Channel::getLibraryId, data.getLibraryId());
        wrapper.orderByDesc(Channel::getSort);
        List<Channel> channelList = baseService.list(wrapper);
        return R.success(channelList);
    }

    @Override
    public R<List<Channel>> query(Channel data) {
        LbqWrapper<Channel> wrapper = Wraps.<Channel>lbQ();
        if (data.getChannelGroupId() == null) {
            wrapper.isNull(Channel::getChannelGroupId);
        } else {
            wrapper.eq(Channel::getChannelGroupId, data.getChannelGroupId());
        }
        if (data.getDoctorOwner() != null) {
            wrapper.eq(Channel::getDoctorOwner, data.getDoctorOwner());
        }
        if (StringUtils.isNotEmptyString(data.getTenantCode())) {
            BaseContextHandler.setTenant(data.getTenantCode());
        }
        wrapper.eq(Channel::getLibraryId, data.getLibraryId());
        wrapper.orderByDesc(Channel::getSort);
        List<Channel> channelList = baseService.list(wrapper);
        return R.success(channelList);
    }


    @ApiOperation("查询栏目组")
    @PostMapping("channelList")
    public R<List<Channel>> list(@RequestBody Channel data) {
        LbqWrapper<Channel> wrapper = Wraps.<Channel>lbQ();
        if (data.getChannelGroupId() == null) {
            wrapper.isNull(Channel::getChannelGroupId);
        } else {
            wrapper.eq(Channel::getChannelGroupId, data.getChannelGroupId());
        }
        if (data.getDoctorOwner() != null) {
            wrapper.eq(Channel::getDoctorOwner, data.getDoctorOwner());
        }
        if (StringUtils.isNotEmptyString(data.getTenantCode())) {
            BaseContextHandler.setTenant(data.getTenantCode());
        }
        if (Objects.isNull(data.getParentId())){
            wrapper.isNull(Channel::getParentId);
        } else {
            wrapper.eq(Channel::getParentId, data.getParentId());
        }
        wrapper.eq(Channel::getLibraryId, data.getLibraryId());
        wrapper.orderByDesc(Channel::getSort, SuperEntity::getCreateTime);
        List<Channel> channelList = baseService.list(wrapper);
        if (CollectionUtils.isEmpty(channelList)) {
            return R.success(channelList);
        }
        List<Long> collect = channelList.stream().map(Channel::getId).collect(Collectors.toList());
        QueryWrapper<Channel> queryWrapper = Wrappers.<Channel>query().select("parent_id as parentId", "count(*) as total")
                .in("parent_id", collect)
                .groupBy("parent_id");
        List<Map<String, Object>> mapList = baseService.listMaps(queryWrapper);
        Map<Long, Boolean> map = new HashMap<>();
        for (Map<String, Object> objectMap : mapList) {
            Object parentId = objectMap.get("parentId");
            Object total = objectMap.get("total");
            if (Objects.nonNull(parentId) && Objects.nonNull(total)) {
                int parseInt = Integer.parseInt(total.toString());
                map.put(Long.parseLong(parentId.toString()), parseInt > 0);
            }
        }
        for (Channel channel : channelList) {
            Boolean hasChildren = map.get(channel.getId());
            channel.setHasChildren(hasChildren == null ? false : hasChildren);
        }

        return R.success(channelList);

    }

    @ApiOperation("无授权获取分页信息")
    @GetMapping("anno/getChannelGroup/{id}")
    public R<ChannelGroup> getAnnoChannelGroup(@PathVariable("id") Long id) {

        ChannelGroup group = channelGroupMapper.selectById(id);
        return R.success(group);

    }


    @ApiOperation("获取分页信息")
    @GetMapping("getChannelGroup/{id}")
    public R<ChannelGroup> getChannelGroup(@PathVariable("id") Long id) {

        ChannelGroup group = channelGroupMapper.selectById(id);
        return R.success(group);

    }

    @ApiOperation("新增或者更新栏目组名称")
    @PostMapping("saveOrUpdateChannelGroup")
    public R<ChannelGroup> saveOrUpdateChannelGroup(@RequestBody ChannelGroup channelGroup) {

        if (Objects.isNull(channelGroup.getId())) {
            channelGroupMapper.insert(channelGroup);
        } else {
            channelGroupMapper.updateById(channelGroup);
        }
        return R.success(channelGroup);
    }


    @ApiOperation("导入页面")
    @PostMapping("importChannelGroup")
    public R<Boolean> importChannelGroup(@RequestBody ChanelGroupImportDto chanelGroupImportDto) {

        channelGroupImportBizService.importChannelGroup(chanelGroupImportDto);
        return R.success(true);
    }

}
