package com.caring.sass.cms.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dao.ChannelGroupMapper;
import com.caring.sass.cms.dto.*;
import com.caring.sass.cms.dto.site.SiteCmsQueryDTO;
import com.caring.sass.cms.entity.*;
import com.caring.sass.cms.reptile.OzhlCbptReptile;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.ChannelService;
import com.caring.sass.cms.service.ContentCollectService;
import com.caring.sass.cms.service.ContentReplyService;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 栏目内容
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/channelContent")
@Api(value = "ChannelContent", tags = "栏目内容")
//@PreAuth(replace = "channelContent:")
public class ChannelContentController extends SuperController<ChannelContentService, Long, ChannelContent, ChannelContentPageDTO, ChannelContentSaveDTO, ChannelContentUpdateDTO> {

    private final ChannelService channelService;

    private final ContentReplyService contentReplyService;

    private final ContentCollectService contentCollectService;

    public ChannelContentController(ChannelService channelService, ContentReplyService contentReplyService, ContentCollectService contentCollectService) {
        this.channelService = channelService;
        this.contentReplyService = contentReplyService;
        this.contentCollectService = contentCollectService;
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<ChannelContent> channelContentList = list.stream().map((map) -> {
            ChannelContent channelContent = ChannelContent.builder().build();
            //TODO 请在这里完成转换
            return channelContent;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(channelContentList));
    }

    @ApiOperation("项目端-移动或复制文章")
    @PostMapping("copyOrMoveContent")
    @SysLog(value = "项目端-移动或复制文章", request = true)
    public R<Boolean> copyOrMoveContent(@RequestBody @Validated ChannelContentCopyOrMoveDto channelContentCopyOrMoveDto) {
        Boolean copyContent = baseService.copyOrMoveContent(channelContentCopyOrMoveDto);
        return R.success(copyContent);
    }


    @ApiOperation("复制内容库cms到项目cms库")
    @PostMapping("copyContent")
    @SysLog(value = "复制内容库cms到项目cms库", request = false)
    public R<Boolean> copyContent(@RequestBody @Validated ChannelContentCopyDto channelContentCopyDto) {
        Boolean copyContent = baseService.copyContent(channelContentCopyDto);
        return R.success(copyContent);
    }

    @ApiOperation("无授权点击cms增加阅读数")
    @PutMapping("anno/updateHitCount/{contentId}")
    public R<Boolean> annoUpdateHitCount(@PathVariable("contentId") Long contentId) {
        return updateHitCount(contentId);
    }

    @ApiOperation("点击cms增加阅读数")
    @PutMapping("updateHitCount/{contentId}")
    public R<Boolean> updateHitCount(@PathVariable("contentId") Long contentId) {
        baseService.updateHitCount(contentId);
        return R.success();
    }

    @ApiOperation("修改栏目的排序")
    @PutMapping("/updateSort/{contentId}")
    public R<Boolean> updateSort(@PathVariable("contentId") String contentId, @RequestParam("sort") Integer sort) {
        Boolean aBoolean = baseService.updateSort(contentId, sort);
        return R.success(aBoolean);
    }


    /**
     * 主要用于 护理计划中 选择图文消息
     *
     * @param tenantCode
     * @param channelType
     * @return
     */
    @ApiOperation("查询项目某个栏目类型下的cms")
    @GetMapping("getChannelContent/{tenantCode}/{channelType}")
    public R<List<ChannelContent>> getChannelContent(@PathVariable("tenantCode") String tenantCode,
                                                     @PathVariable("channelType") String channelType) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<ChannelContent> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChannelContent::getChannelType, channelType);
        List<ChannelContent> contentList = baseService.list(lbqWrapper);
        return R.success(contentList);
    }

    /**
     * 主要用于 学习计划中查询cms库
     * 最大返回50条。
     *
     * @param tenantCode
     * @param channelType
     * @return
     */
    @ApiOperation("查询项目某个栏目类型下的cms")
    @GetMapping("getChannelContent/{tenantCode}/no/{channelType}")
    public R<List<ChannelContent>> getNoChannelContent(@PathVariable("tenantCode") String tenantCode,
                                                       @PathVariable("channelType") String channelType,
                                                       @RequestParam("title") String title) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<ChannelContent> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.ne(ChannelContent::getChannelType, channelType);
        if (StringUtils.isNotEmpty(title)) {
            lbqWrapper.like(ChannelContent::getTitle, title);
        }
        PageParams<ChannelContentPageDTO> pageDTOPageParams = new PageParams<>();
        pageDTOPageParams.setModel(new ChannelContentPageDTO());
        pageDTOPageParams.setCurrent(1);
        pageDTOPageParams.setSize(50);
        IPage<ChannelContent> buildPage = pageDTOPageParams.buildPage();
        IPage<ChannelContent> contentIPage = baseService.page(buildPage, lbqWrapper);
        List<ChannelContent> records = contentIPage.getRecords();
        return R.success(records);
    }


    @ApiOperation("通过Id查询。无视租户")
    @GetMapping("getChannelContentById/{id}/{tenantCode}")
    public R<ChannelContent> getChannelContentById(@PathVariable("id") String id, @PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        ChannelContent channelContent = baseService.getById(id);
        return R.success(channelContent);
    }

    @Override
    public R<ChannelContent> get(Long id) {

        ChannelContent channelContent = baseService.getCmsByRedis(id);
        return R.success(channelContent);
    }


    @ApiOperation("不带项目信息，查询cms的标题和缩略图")
    @GetMapping("getTitle/WithoutTenant/{id}")
    public R<ChannelContent> getTitle(@PathVariable("id") Long id) {
        ChannelContent content = baseService.getTitleByIdWithoutTenant(id);
        return R.success(content);
    }

    @ApiOperation("不带项目信息，根据id查询cms内容")
    @GetMapping("getByIdWithoutTenantNoContent/{id}")
    public R<ChannelContent> getByIdWithoutTenantNoContent(@PathVariable("id") Long id) {
        ChannelContent content = baseService.getByIdWithoutTenantNoContent(id);
        return R.success(content);
    }

    @ApiOperation("不带项目信息，根据id查询cms内容")
    @GetMapping("getWithoutTenant/{id}")
    public R<ChannelContent> getWithoutTenant(@PathVariable("id") Long id) {
        ChannelContent content = baseService.getByIdWithoutTenant(id);
        return R.success(content);
    }

    @ApiOperation("无授权根据id查询cms内容，带评论")
    @GetMapping("/anno/channelContentWithReply/{id}")
    public R<ChannelContent> annoChannelContentWithReply(@PathVariable("id") Long id,
                                                         @RequestParam(value = "userId", required = false) Long userId,
                                                         @RequestParam(value = "roleType", required = false) String roleType) {
        return channelContentWithReply(id, userId, roleType);
    }

    @ApiOperation("根据id查询cms内容，带评论")
    @GetMapping("channelContentWithReply/{id}")
    public R<ChannelContent> channelContentWithReply(@PathVariable("id") Long id,
                                                     @RequestParam(value = "userId", required = false) Long userId,
                                                     @RequestParam(value = "roleType", required = false) String roleType) {
        ChannelContent content = baseService.getContentByIdAndSetWeiXinCode(id, userId);

        if (Objects.isNull(content)) {
            String currentTenant = BaseContextHandler.getTenant();
            BaseContextHandler.setTenant(BizConstant.SUPER_ADMIN);
            content = baseService.getById(id);
            if (Objects.isNull(content)) {
                return R.fail("文章不存在或已删除");
            }
            BaseContextHandler.setTenant(currentTenant);
        }
        if (StringUtils.isEmpty(roleType)) {
            roleType = UserType.UCENTER_PATIENT;
        }
        // 评论列表
        List<ContentReply> replies = contentReplyService.listReplyWithLike(id, userId, roleType);
        content.setContentReplies(replies);

        // 是否收藏
        boolean collect = contentCollectService.hasCollect(id, userId, roleType);
        content.setContentCollect(collect);
        return R.success(content);
    }

    @ApiOperation("无授权查询文章列表")
    @PostMapping("anno/page")
    public R<IPage<ChannelContent>> annoPage(@RequestBody PageParams<ChannelContentPageDTO> params){
        return this.page(params);
    }

    @ApiOperation("查询租户下的文章")
    @PostMapping("queryByTenantCode/{tenantCode}")
    public R<IPage<ChannelContent>> queryByTenantCode(@PathVariable("tenantCode") String tenantCode, @RequestBody PageParams<ChannelContentPageDTO> params) {
        BaseContextHandler.setTenant(tenantCode);
        return this.page(params);
    }

    @ApiOperation("项目端文章列表")
    @PostMapping("webPage")
    public R<IPage<ChannelContent>> webPage(@RequestBody PageParams<ChannelContentPageDTO> params) {
        // 处理参数
        IPage<ChannelContent> page = params.buildPage();
        Map<String, String> map = params.getMap();
        ChannelContentPageDTO model = params.getModel();
        List<Long> chanelId = new ArrayList<>();
        if (!StringUtils.isEmpty(model.getChannelId())) {
            chanelId = channelService.getChildChanelId(Long.parseLong(model.getChannelId()));
        }
        LbqWrapper<ChannelContent> query = Wraps.<ChannelContent>lbQ()
                .like(ChannelContent::getTitle, model.getTitle())
                .eq(ChannelContent::getChannelType, model.getChannelType())
                .eq(ChannelContent::getOwnerType, model.getOwnerType())
                .eq(ChannelContent::getDoctorOwner, model.getDoctorOwner())
                .eq(ChannelContent::getLibraryId, model.getLibraryId())
                .ne(ChannelContent::getChannelType, model.getNoChannelType())
                .select(ChannelContent::getId, ChannelContent::getTitle, ChannelContent::getChannelType, ChannelContent::getChannelId,
                        ChannelContent::getSummary, ChannelContent::getHitCount, ChannelContent::getIcon,ChannelContent::getOwnerType,
                        ChannelContent::getLink, ChannelContent::getIsTop, ChannelContent::getCanComment, ChannelContent::getChannelGroupId,
                        ChannelContent::getMessageNum, ChannelContent::getSort, ChannelContent::getCreateTime,  Entity::getUpdateTime);
        if (StrUtil.isNotEmpty(model.getTitle())) {
            query.and(w -> w.like(ChannelContent::getTitle, model.getTitle()).or().like(ChannelContent::getKeyWord, model.getTitle()));
        }
        if (CollectionUtils.isEmpty(chanelId)) {
            query.eq(ChannelContent::getChannelId, model.getChannelId());
        } else {
            chanelId.add(Long.parseLong(model.getChannelId()));
            query.in(ChannelContent::getChannelId, chanelId);
        }
        if (!CollectionUtils.isEmpty(map)) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createTimeSt = map.get("createTime_st");
            if (StringUtils.isNotEmpty(createTimeSt)) {
                try {
                    LocalDateTime parse = LocalDateTime.parse(createTimeSt, df);
                    query.ge(SuperEntity::getCreateTime, parse);
                } catch (Exception e) {
                    throw new BizException("时间格式有错误");
                }
            }
            String createTimeEd = map.get("createTime_ed");
            if (StringUtils.isNotEmpty(createTimeEd)) {
                try {
                    LocalDateTime parse = LocalDateTime.parse(createTimeEd, df);
                    query.le(SuperEntity::getCreateTime, parse);
                } catch (Exception e) {
                    throw new BizException("时间格式有错误");
                }
            }
        }
        if (Objects.nonNull(model.getChannelGroupId())) {
            query.eq(ChannelContent::getChannelGroupId, model.getChannelGroupId());
        }
        if (model.getDoctorOwner() != null && model.getDoctorOwner().equals(false)) {
            query.isNull(ChannelContent::getChannelGroupId);
        }
        if (model.getDoctorOwner() != null && model.getDoctorOwner().equals(true)) {
            query.isNull(ChannelContent::getChannelGroupId);
        }
        if (StringUtils.isNotEmpty(model.getTenantCode())) {
            BaseContextHandler.setTenant(model.getTenantCode());
        }
        page = baseService.page(page, query);
        List<ChannelContent> records = page.getRecords();
        setChannelName(records);
        return R.success(page);
    }



    @Override
    public R<IPage<ChannelContent>> page(PageParams<ChannelContentPageDTO> params) {
        // 处理参数
        IPage<ChannelContent> page = params.buildPage();
        Map<String, String> map = params.getMap();
        ChannelContentPageDTO model = params.getModel();
        List<Long> chanelId = new ArrayList<>();
        if (!StringUtils.isEmpty(model.getChannelId())) {
            chanelId = channelService.getChildChanelId(Long.parseLong(model.getChannelId()));
        }
        LbqWrapper<ChannelContent> query = Wraps.<ChannelContent>lbQ()
                .like(ChannelContent::getTitle, model.getTitle())
                .eq(ChannelContent::getChannelType, model.getChannelType())
                .eq(ChannelContent::getOwnerType, model.getOwnerType())
                .eq(ChannelContent::getDoctorOwner, model.getDoctorOwner())
                .eq(ChannelContent::getLibraryId, model.getLibraryId())
                .ne(ChannelContent::getChannelType, model.getNoChannelType())
                .select(ChannelContent::getId, ChannelContent::getTitle, ChannelContent::getChannelType, ChannelContent::getChannelId,
                        ChannelContent::getSummary, ChannelContent::getHitCount, ChannelContent::getIcon,ChannelContent::getOwnerType,
                        ChannelContent::getLink, ChannelContent::getIsTop, ChannelContent::getCanComment, ChannelContent::getChannelGroupId,
                        ChannelContent::getMessageNum, ChannelContent::getSort, ChannelContent::getCreateTime,  Entity::getUpdateTime);
        if (StrUtil.isNotEmpty(model.getTitle())) {
            query.and(w -> w.like(ChannelContent::getTitle, model.getTitle()).or().like(ChannelContent::getKeyWord, model.getTitle()));
        }
        if (CollectionUtils.isEmpty(chanelId)) {
            query.eq(ChannelContent::getChannelId, model.getChannelId());
        } else {
            chanelId.add(Long.parseLong(model.getChannelId()));
            query.in(ChannelContent::getChannelId, chanelId);
        }
        if (!CollectionUtils.isEmpty(map)) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createTimeSt = map.get("createTime_st");
            if (StringUtils.isNotEmpty(createTimeSt)) {
                try {
                    LocalDateTime parse = LocalDateTime.parse(createTimeSt, df);
                    query.ge(SuperEntity::getCreateTime, parse);
                } catch (Exception e) {
                    throw new BizException("时间格式有错误");
                }
            }
            String createTimeEd = map.get("createTime_ed");
            if (StringUtils.isNotEmpty(createTimeEd)) {
                try {
                    LocalDateTime parse = LocalDateTime.parse(createTimeEd, df);
                    query.le(SuperEntity::getCreateTime, parse);
                } catch (Exception e) {
                    throw new BizException("时间格式有错误");
                }
            }
        }
        if (model.getChannelGroupId() == null) {
            query.isNull(ChannelContent::getChannelGroupId);
        } else {
            query.eq(ChannelContent::getChannelGroupId, model.getChannelGroupId());
        }
        if (StringUtils.isNotEmpty(model.getTenantCode())) {
            BaseContextHandler.setTenant(model.getTenantCode());
        }
        query.orderByDesc(ChannelContent::getIsTop);
        query.orderByDesc(ChannelContent::getSort);
        query.orderByDesc(Entity::getUpdateTime);
        page = baseService.page(page, query);
        List<ChannelContent> records = page.getRecords();
        setChannelName(records);
        return R.success(page);
    }

    public void setChannelName(List<ChannelContent> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        Set<Long> collect = records.stream().map(ChannelContent::getChannelId).collect(Collectors.toSet());
        LbqWrapper<Channel> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.in(Channel::getId, collect);
        List<Channel> channelList = channelService.findList(lbqWrapper);
        if (CollectionUtils.isEmpty(channelList)) {
            return;
        }
        Map<Long, String> longStringMap = channelList.stream().collect(Collectors.toMap(Channel::getId, Channel::getChannelName));
        for (ChannelContent record : records) {
            record.setChannelName(longStringMap.get(record.getChannelId()));
        }
    }

    @ApiOperation("查询未被导入到内容库中的系统文章")
    @PostMapping("getNoImportMyContent")
    public R<IPage<ChannelContent>> getNoImportMyContent(@RequestBody PageParams<ChannelContentPageDTO> params) {
        IPage<ChannelContent> page = params.buildPage();
        ChannelContentPageDTO model = params.getModel();
        LbqWrapper<ChannelContent> eq = Wraps.<ChannelContent>lbQ();
        if (model.getChannelGroupId() != null) {
            eq.eq(ChannelContent::getChannelGroupId, model.getChannelGroupId());
        } else {
            eq.eq(ChannelContent::getDoctorOwner, model.getDoctorOwner());
        }
        // 获取本项目已经导入过的文章的id
        List<ChannelContent> contentList = baseService.list(eq
                .isNotNull(ChannelContent::getSourceId)
                .select(ChannelContent::getSourceId));
        List<Long> collect = null;
        if (!CollectionUtils.isEmpty(contentList)) {
            collect = contentList.stream().map(ChannelContent::getSourceId).collect(Collectors.toList());
        }

        BaseContextHandler.setTenant(BizConstant.SUPER_ADMIN);
        LbqWrapper<ChannelContent> query = Wraps.<ChannelContent>lbQ()
                .like(ChannelContent::getTitle, model.getTitle())
                .eq(ChannelContent::getChannelType, model.getChannelType())
                .eq(ChannelContent::getOwnerType, model.getOwnerType())
                .eq(ChannelContent::getChannelId, model.getChannelId())
                .eq(ChannelContent::getLibraryId, model.getLibraryId())
                .select(ChannelContent::getId, ChannelContent::getTitle, ChannelContent::getChannelType, ChannelContent::getChannelId,
                        ChannelContent::getSummary, ChannelContent::getIcon,ChannelContent::getOwnerType,  Entity::getUpdateTime);
        if (collect != null) {
            query.notIn(ChannelContent::getId, collect);
        }

        page = baseService.page(page, query);
        setChannelName(page.getRecords());
        return R.success(page);
    }

    @ApiOperation("获取文章的基本信息")
    @GetMapping("getBaseContent/{id}")
    public R<ChannelContent> getBaseContent(@PathVariable("id") Long id) {
        LbqWrapper<ChannelContent> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChannelContent::getId, id);
        lbqWrapper.select(ChannelContent::getId, ChannelContent::getTitle,
                ChannelContent::getDoctorOwner, ChannelContent::getChannelGroupId,
                ChannelContent::getSummary,
                ChannelContent::getIcon, ChannelContent::getLink);
        ChannelContent serviceOne = baseService.getOne(lbqWrapper);
        return R.success(serviceOne);
    }

    /**
     * 只适用于分享出去的cms详情页面。
     * @param tenantCode
     * @param id
     * @return
     */
    @ApiOperation("无需授权根据id查询cms内容")
    @GetMapping("anno/channelContentWithReply/{tenantCode}/{id}")
    public R<ChannelContent> annoChannelContentWithReply(
            @PathVariable("tenantCode") String tenantCode,
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId) {
        BaseContextHandler.setTenant(tenantCode);
        ChannelContent content = baseService.getContentByIdAndSetWeiXinCode(id, userId);

        if (Objects.isNull(content)) {
            return R.fail("文章不存在或已删除");
        }

        // 评论列表
        List<ContentReply> replies = contentReplyService.listReplyWithLike(id);
        content.setContentReplies(replies);

        // 是否收藏
        content.setContentCollect(false);
        return R.success(content);
    }


    @ApiOperation("无授权批量查询文章")
    @PostMapping("anno/query")
    public R<List<ChannelContent>> annoQuery(@RequestBody ChannelContent data) {
        return this.query(data);
    }

    @Override
    public R<List<ChannelContent>> query(ChannelContent data) {

        LbqWrapper<ChannelContent> wrapper = Wraps.<ChannelContent>lbQ()
                .eq(ChannelContent::getDoctorOwner, data.getDoctorOwner());
        if (data.getChannelGroupId() == null) {
            wrapper.isNull(ChannelContent::getChannelGroupId);
        } else {
            wrapper.eq(ChannelContent::getChannelGroupId, data.getChannelGroupId());
        }
        wrapper.eq(ChannelContent::getChannelId, data.getChannelId());
        wrapper.eq(ChannelContent::getLibraryId, data.getLibraryId());
        wrapper.eq(ChannelContent::getChannelType, data.getChannelType());
        wrapper.eq(ChannelContent::getOwnerType, data.getOwnerType());
        wrapper.select(ChannelContent::getId, ChannelContent::getTitle, ChannelContent::getChannelType, ChannelContent::getChannelId,
                ChannelContent::getSummary, ChannelContent::getHitCount, ChannelContent::getIcon,ChannelContent::getOwnerType,
                ChannelContent::getLink, ChannelContent::getIsTop, ChannelContent::getCanComment, ChannelContent::getChannelGroupId,
                ChannelContent::getMessageNum, ChannelContent::getSort, ChannelContent::getCreateTime, Entity::getUpdateTime);
        wrapper.orderByDesc(ChannelContent::getIsTop);
        wrapper.orderByDesc(ChannelContent::getSort);
        wrapper.orderByDesc(Entity::getUpdateTime);
        List<ChannelContent> list = baseService.list(wrapper);
        return R.success(list);
    }

    @ApiOperation("查询患者首页的文章")
    @ApiImplicitParam(name = "patientHomeRegion", value = "是否显示在患者首页 推荐：recommend, 文章：article, 视频：video")
    @PostMapping("queryPatientHomeContent")
    public R<List<ChannelContent>> queryPatientHomeContent(String patientHomeRegion) {

        List<ChannelContent> contentList = baseService.list(Wraps.<ChannelContent>lbQ()
                .select(SuperEntity::getId,
                        ChannelContent::getLink,
                        ChannelContent::getAuthor,
                        ChannelContent::getSummary,
                        ChannelContent::getIcon,
                        ChannelContent::getTitle,
                        ChannelContent::getHitCount,
                        ChannelContent::getMessageNum,
                        ChannelContent::getSort,
                        Entity::getUpdateTime)
                .eq(ChannelContent::getPatientHomeShow, 1)
                .like(ChannelContent::getPatientHomeRegion, patientHomeRegion)
                .orderByDesc(ChannelContent::getIsTop)
                .orderByDesc(ChannelContent::getSort)
                .orderByDesc(Entity::getUpdateTime)
                .last(" limit 0, 30 ")
        );
        return R.success(contentList);
    }

    @ApiOperation("统计用户(默认患者)收藏评论数量")
    @GetMapping("countPatient")
    public R<ContentPatientCountDTO> queryPatientHomeContent(@RequestParam Long userId, @RequestParam(required = false) String roleType ) {

        if (roleType == null) {
            roleType = UserType.UCENTER_PATIENT;
        }
        // 收藏的文章数量
        int count = contentCollectService.count(Wraps.<ContentCollect>lbQ()
                .eq(ContentCollect::getCollectStatus, 1)
                .eq(ContentCollect::getUserId, userId)
                .eq(ContentCollect::getRoleType, roleType));
        ContentPatientCountDTO countDTO = new ContentPatientCountDTO();
        countDTO.setCollectCount(count);
        QueryWrapper<ContentReply> queryWrapper = new QueryWrapper<>();
        List<Map<String, Object>> maps = contentReplyService.listMaps(queryWrapper
                .select(" count( DISTINCT c_content_id ) as countNum ")
                .eq("c_replier_id", userId.toString())
                .eq("role_type", roleType));
        int replyCount = 0;
        if (CollUtil.isNotEmpty(maps)) {
            Map<String, Object> objectMap = maps.get(0);
            if (CollUtil.isNotEmpty(objectMap)) {
                Object countNum = objectMap.get("countNum");
                if (Objects.nonNull(countNum)) {
                    replyCount = Integer.parseInt(countNum.toString());
                }
            }
        }
        countDTO.setReplyContentCount(replyCount);
        return R.success(countDTO);
    }

    @Autowired
    ChannelGroupMapper channelGroupMapper;


    @ApiOperation("建站文章的全部分类，支持文章名称搜索")
    @PutMapping("queryChannelContentGroup")
    public R<List<ChannelContentGroupListDTO>> queryChannelContentGroup(@RequestBody SiteCmsQueryDTO queryDTO) {

        String title = queryDTO.getTitle();
        List<Long> filterCmsIds = queryDTO.getFilterCmsIds();
        List<ChannelContentGroupListDTO> groupListDTOS = new ArrayList<>();
        if (StrUtil.isNotEmpty(title)) {
            // 模糊搜索 文章标题所在的 分类ID
            LbqWrapper<ChannelContent> lbqWrapper = Wraps.<ChannelContent>lbQ().select(ChannelContent::getDoctorOwner)
                    .isNotNull(ChannelContent::getDoctorOwner)
                    .isNull(ChannelContent::getChannelGroupId)
                    .groupBy(ChannelContent::getDoctorOwner);

            LbqWrapper<ChannelContent> channelContentLbqWrapper = Wraps.<ChannelContent>lbQ().select(ChannelContent::getChannelGroupId)
                    .like(ChannelContent::getTitle, title)
                    .isNotNull(ChannelContent::getChannelGroupId)
                    .groupBy(ChannelContent::getChannelGroupId);
            if (StrUtil.isNotEmpty(title)) {
                lbqWrapper.like(ChannelContent::getTitle, title);
                channelContentLbqWrapper.like(ChannelContent::getTitle, title);
            }
            if (CollUtil.isNotEmpty(filterCmsIds)) {
                lbqWrapper.notIn(ChannelContent::getId, filterCmsIds);
                channelContentLbqWrapper.notIn(ChannelContent::getId, filterCmsIds);
            }
            List<ChannelContent> contents = baseService.list(lbqWrapper);
            boolean patientCms = false;
            boolean doctorCms = false;
            if (CollUtil.isNotEmpty(contents)) {
                for (ChannelContent content : contents) {
                    if (content.getDoctorOwner()) {
                        doctorCms = true;
                    } else {
                        patientCms = true;
                    }
                }
            }
            if (patientCms) {
                groupListDTOS.add(ChannelContentGroupListDTO.builder().id(0L).name("患教库").build());
            }
            if (doctorCms) {
                groupListDTOS.add(ChannelContentGroupListDTO.builder().id(1L).name("医生库").build());
            }
            List<ChannelContent> cmsGroupIds = baseService.list(channelContentLbqWrapper);
            if (CollUtil.isNotEmpty(cmsGroupIds)) {
                List<Long> groupIds = cmsGroupIds.stream().map(ChannelContent::getChannelGroupId).collect(Collectors.toList());
                List<ChannelGroup> channelGroups = channelGroupMapper.selectBatchIds(groupIds);
                if (CollUtil.isNotEmpty(channelGroups)) {
                    channelGroups.forEach(item ->
                        groupListDTOS.add(
                            ChannelContentGroupListDTO
                                .builder()
                                .id(item.getId())
                                .name(item.getGroupName())
                                .cmsRoleRemark(item.getCmsRoleRemark())
                                .build()
                        )
                    );
                }
            }
        } else {
            groupListDTOS.add(ChannelContentGroupListDTO.builder().id(0L).name("患教库").build());
            groupListDTOS.add(ChannelContentGroupListDTO.builder().id(1L).name("医生库").build());
            List<ChannelGroup> channelGroups = channelGroupMapper.selectList(Wraps.<ChannelGroup>lbQ());
            if (CollUtil.isNotEmpty(channelGroups)) {
                channelGroups.forEach(item -> groupListDTOS.add(ChannelContentGroupListDTO.builder()
                        .id(item.getId())
                        .name(item.getGroupName())
                        .cmsRoleRemark(item.getCmsRoleRemark())
                        .build()));
            }
        }

        return R.success(groupListDTOS);
    }


    @ApiOperation("建站分类下的文章，支持文章名称搜索")
    @PutMapping("queryChannelContent")
    public R<IPage<ChannelContent>> queryChannelContent(@RequestBody PageParams<SiteCmsQueryDTO> dtoPage) {


        IPage<ChannelContent> buildPage = dtoPage.buildPage();
        SiteCmsQueryDTO model = dtoPage.getModel();
        Long groupId = model.getGroupId();
        List<Long> cmsIds = model.getFilterCmsIds();
        String modelTitle = model.getTitle();
        LbqWrapper<ChannelContent> lbqWrapper = Wraps.<ChannelContent>lbQ().select(SuperEntity::getId, ChannelContent::getTitle, ChannelContent::getIcon, ChannelContent::getSummary);
        if (groupId.equals(0L)) {
            lbqWrapper.eq(ChannelContent::getDoctorOwner, false);
            lbqWrapper.isNull(ChannelContent::getChannelGroupId);
        } else if (groupId.equals(1L)) {
            lbqWrapper.eq(ChannelContent::getDoctorOwner, true);
            lbqWrapper.isNull(ChannelContent::getChannelGroupId);
        } else {
            lbqWrapper.eq(ChannelContent::getChannelGroupId, groupId);
        }
        if (StrUtil.isNotEmpty(modelTitle)) {
            lbqWrapper.like(ChannelContent::getTitle, modelTitle);
        }
        if (CollUtil.isNotEmpty(cmsIds)) {
            lbqWrapper.notIn(SuperEntity::getId, cmsIds);
        }
        lbqWrapper.orderByDesc(ChannelContent::getIsTop)
                .orderByDesc(ChannelContent::getSort)
                .orderByDesc(Entity::getUpdateTime);
        baseService.page(buildPage, lbqWrapper);
        return R.success(buildPage);
    }

    @Autowired
    OzhlCbptReptile ozhlCbptReptile;

    @ApiOperation("抓取中华临床免疫和变态反应杂志排行数据")
    @GetMapping("anno/ozhlCbptReptile")
    public R<Boolean> ozhlCbptReptile() throws IOException, InterruptedException {
        ozhlCbptReptile.startFirstReptile();
        return R.success(true);
    }


    @ApiOperation("每日刷新中华临床免疫和变态反应杂志首页数据")
    @GetMapping("anno/ozhlCbptReptile/everyDay")
    public R<Boolean> ozhlCbptReptileEveryDay() throws IOException, InterruptedException {
        ozhlCbptReptile.getNewCms();
        return R.success(true);
    }



}
