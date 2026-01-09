package com.caring.sass.cms.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.*;
import com.caring.sass.cms.dto.ChannelContentCopyDto;
import com.caring.sass.cms.dto.ChannelContentCopyOrMoveDto;
import com.caring.sass.cms.entity.*;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.SiteModulePlateService;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.enums.OwnerTypeEnum;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileRecentlyUsedApi;
import com.caring.sass.file.dto.image.FileRecentlyUsedImageDTO;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.tenant.api.TenantApi;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 栏目内容
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Slf4j
@Service

public class ChannelContentServiceImpl extends SuperServiceImpl<ChannelContentMapper, ChannelContent> implements ChannelContentService {

    @Autowired
    TenantApi tenantApi;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    ChannelMapper channelMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    FileRecentlyUsedApi fileRecentlyUsedApi;

    @Autowired
    ContentCollectMapper contentCollectMapper;

    @Autowired
    ContentReplyMapper contentReplyMapper;

    @Autowired
    ReplyLikeLogMapper replyLikeLogMapper;

    @Autowired
    SiteModulePlateService siteModulePlateService;
    /**
     * @return java.lang.Boolean
     * @Author yangShuai
     * @Description 更新内容的 排序字段
     * @Date 2020/9/10 9:58
     */
    @Override
    public Boolean updateSort(String contentId, Integer sort) {
        UpdateWrapper<ChannelContent> updateWrapper = new UpdateWrapper<>();
        if (sort == null) {
            throw new BizException("请输入排序数字");
        }
        updateWrapper.set("n_sort", sort)
                .eq("id", contentId);
        return super.update(updateWrapper);
    }


    @Override
    public boolean save(ChannelContent model) {
        if (StringUtils.isEmpty(model.getChannelType())) {
            model.setChannelType("Article");
        }
        Long userId = BaseContextHandler.getUserId();
        String tenant = BaseContextHandler.getTenant();
        String userType = BaseContextHandler.getUserType();
        //检查患者首页设置的文章数量。超过30条就剔除编辑时间最久的文章
        checkPatientHomeCms(model, tenant);
        // 解析其中使用的图片。异步发送给文件服务。生成或更新最近使用记录。
        int insert = baseMapper.insert(model);
        if (insert > 0) {
            SaasGlobalThreadPool.execute(() -> getContentImage(model, userId, userType, tenant));
            if (StrUtil.isNotEmpty(tenant) && !BizConstant.SUPER_TENANT.equals(tenant) ) {
                redisTemplate.opsForHash().put(SaasRedisBusinessKey.CMS_CONTENT_ALL+ tenant, model.getId().toString(), JSON.toJSONString(model));
                redisTemplate.opsForHash().put(SaasRedisBusinessKey.CMS_CONTENT_HIT_COUNT+ tenant, model.getId().toString(), "0");
            }
        }

        return insert > 0;
    }


    /**
     * 检查患者首页设置的文章数量。超过30条就剔除编辑时间最久的文章
     * @param model
     * @param tenantCode
     */
    public void checkPatientHomeCms(ChannelContent model, String tenantCode) {

        if (BizConstant.SUPER_TENANT.equals(tenantCode)) {
            return;
        }
        Integer patientHomeShow = model.getPatientHomeShow();
        String homeRegion = model.getPatientHomeRegion();
        if (patientHomeShow == null || patientHomeShow.equals(0)) {
            return;
        }
        if (StrUtil.isBlank(homeRegion)) {
            return;
        }
        String[] split = homeRegion.split(",");
        for (String s : split) {
            LbqWrapper<ChannelContent> wrapper = Wraps.<ChannelContent>lbQ().eq(ChannelContent::getPatientHomeShow, 1)
                    .like(ChannelContent::getPatientHomeRegion, s);
            if (Objects.nonNull(model.getId())) {
                wrapper.ne(SuperEntity::getId, model.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count == null) {
                continue;
            }
            if (count >= 30) {
                int number = count - 30 + 1;
                List<ChannelContent> contents = baseMapper.selectList(Wraps.<ChannelContent>lbQ()
                        .select(SuperEntity::getId, ChannelContent::getPatientHomeShow, ChannelContent::getPatientHomeRegion)
                        .eq(ChannelContent::getPatientHomeShow, 1)
                        .like(ChannelContent::getPatientHomeRegion, s)
                        .orderByAsc(Entity::getUpdateTime)
                        .last(" limit 0," + number));
                if (CollUtil.isEmpty(contents)) {
                    return;
                }
                for (ChannelContent content : contents) {
                    String patientHomeRegion = content.getPatientHomeRegion();
                    String[] strings = patientHomeRegion.split(",");
                    ArrayList<String> arrayList = ListUtil.toList(strings);
                    arrayList.remove(s);
                    if (CollUtil.isEmpty(arrayList)) {
                        UpdateWrapper<ChannelContent> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.set("patient_home_show", 0);
                        updateWrapper.set("patient_home_region", null);
                        updateWrapper.eq("id", content.getId());
                        baseMapper.update(new ChannelContent(), updateWrapper);
                    } else {
                        UpdateWrapper<ChannelContent> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.set("patient_home_region", StrUtil.join(",",arrayList));
                        updateWrapper.eq("id", content.getId());
                        baseMapper.update(new ChannelContent(), updateWrapper);
                    }
                }
            }
        }

    }

    @Override
    public boolean updateById(ChannelContent model) {
        Long userId = BaseContextHandler.getUserId();
        String tenant = BaseContextHandler.getTenant();
        String userType = BaseContextHandler.getUserType();
        // 解析其中使用的图片。异步发送给文件服务。生成或更新最近使用记录。
        checkPatientHomeCms(model, tenant);
        int update = baseMapper.updateById(model);
        if (update > 0) {
            SaasGlobalThreadPool.execute(() -> getContentImage(model, userId, userType, tenant));
            siteModulePlateService.updateCmsInfo(model, tenant);
            if (StrUtil.isNotEmpty(tenant) && !BizConstant.SUPER_TENANT.equals(tenant) ) {
                redisTemplate.opsForHash().delete(SaasRedisBusinessKey.CMS_CONTENT_ALL+ tenant, model.getId().toString());
            }
        }
        return update > 0;
    }

    /**
     * 获取文件内容中的图片
     * @param model
     * @param userId
     * @param tenant
     */
    private void getContentImage(ChannelContent model, Long userId, String userType, String tenant) {
        String content = model.getContent();
        if (StringUtils.isEmpty(content)) {
            return;
        }

        Document doc = Jsoup.parse(content);
        Elements imgs = doc.getElementsByTag("img");
        if (CollUtil.isEmpty(imgs)) {
            return;
        }
        List<String> imageUrl = new ArrayList<>();
        for (Element img : imgs) {
            String url = img.attr("src");
            if (StrUtil.isNotEmpty(url)) {
                imageUrl.add(url);
            }
        }
        if (CollUtil.isNotEmpty(imageUrl)) {
            FileRecentlyUsedImageDTO usedImageDTO = FileRecentlyUsedImageDTO.builder().imageUrl(imageUrl)
                    .userType(userType)
                    .tenant(tenant).userId(userId).build();
            fileRecentlyUsedApi.cmsImageSaveRecentlyUsed(usedImageDTO);
        }
    }



    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        LbqWrapper<ChannelContent> select = Wraps.<ChannelContent>lbQ().select(SuperEntity::getId, ChannelContent::getChannelType,
                ChannelContent::getOwnerType)
                .in(SuperEntity::getId, idList);
        List<ChannelContent> contentList = baseMapper.selectList(select);
        if (CollectionUtils.isEmpty(contentList)) {
            return true;
        }
        for (ChannelContent content : contentList) {
            if (content.getChannelType() != null && "TextImage".equals(content.getChannelType())) {
                if (OwnerTypeEnum.SYS.eq(content.getOwnerType())) {
                    continue;
                }
            }
            contentCollectMapper.delete(Wraps.<ContentCollect>lbQ().eq(ContentCollect::getContentId, content.getId()));
            replyLikeLogMapper.delete(Wraps.<ReplyLikeLog>lbQ()
                    .apply(true, " reply_id in ( select id from t_cms_content_reply where c_content_id = "+ content.getId() +" )"));
            contentReplyMapper.delete(Wraps.<ContentReply>lbQ().eq(ContentReply::getContentId, content.getId()));
            baseMapper.deleteById(content.getId());
            String tenant = BaseContextHandler.getTenant();
            if (StrUtil.isNotEmpty(tenant) && !BizConstant.SUPER_TENANT.equals(tenant) ) {
                redisTemplate.opsForHash().delete(SaasRedisBusinessKey.CMS_CONTENT_ALL+ tenant, content.getId().toString());
                redisTemplate.opsForHash().delete(SaasRedisBusinessKey.CMS_CONTENT_HIT_COUNT+ tenant, content.getId().toString());
            }
        }
        return true;
    }

    /**
     * @param textImage
     * @return boolean
     * @Author yangShuai
     * @Description 初始化三个项目必用的图文消息。
     * @Date 2020/10/26 14:55
     */
    @Override
    public boolean initTextImageContent(Channel textImage) {
        LbqWrapper<ChannelContent> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChannelContent::getChannelId, textImage.getId());
        List<ChannelContent> channelContents = baseMapper.selectList(lbqWrapper);
        if (!CollectionUtils.isEmpty(channelContents)) {
            return true;
        }
        ChannelContent content = new ChannelContent();
        content.setTitle("用药打卡提醒已启用");
        content.setIcon("https://caring-deploy.obs.cn-north-4.myhuaweicloud.com:443/402880c77041aedd0170b3695be900c0.jpg");
        content.setSummary("查看打卡说明 >");
        content.setOwnerType(OwnerTypeEnum.SYS);
        content.setChannelType(textImage.getChannelType());
        content.setChannelId(textImage.getId());
        content.setCanComment(0);
        baseMapper.insert(content);

        content = new ChannelContent();
        content.setTitle("明天是血糖监测日");
        content.setIcon("https://caring-deploy.obs.cn-north-4.myhuaweicloud.com:443/402880c77041aedd0170b30208f000bb.jpg");
        content.setSummary("点击查看测量血糖的正确方法 >");
        content.setOwnerType(OwnerTypeEnum.SYS);
        content.setChannelType(textImage.getChannelType());
        content.setChannelId(textImage.getId());
        content.setCanComment(0);
        baseMapper.insert(content);

        content = new ChannelContent();
        content.setTitle("明天是血压监测日");
        content.setIcon("https://caring-deploy.obs.cn-north-4.myhuaweicloud.com:443/402880c77041aedd0170b2f2da1900ba.jpg");
        content.setChannelId(textImage.getId());
        content.setCanComment(0);
        content.setSummary("点击查看测量血压的正确方法 >");
        content.setOwnerType(OwnerTypeEnum.SYS);
        content.setChannelType(textImage.getChannelType());
        baseMapper.insert(content);

        return false;
    }

    @Autowired
    DatabaseProperties databaseProperties;


    @Override
    public boolean copyContent(Long fromLibraryId, Long toLibraryId) {
        if (Objects.isNull(fromLibraryId) || Objects.isNull(toLibraryId)) {
            return false;
        }
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        // 查询 内容库下所有的栏目
        List<Channel> channels = channelMapper.selectList(Wraps.<Channel>lbQ().eq(Channel::getLibraryId, fromLibraryId));
        List<Channel> parentChannel = new ArrayList<>(channels.size());
        List<Channel> childChannel = new ArrayList<>(channels.size());

        // 根据 父级ID 进行分组
        for (Channel channel : channels) {
            if (channel.getParentId() == null) {
                parentChannel.add(channel);
            } else {
                childChannel.add(channel);
            }
        }
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        // 保存一份新的 栏目，并将 新旧栏目通过ID关联。
        Map<Long, Long> channelIdMap = new HashMap<>(channels.size());
        for (Channel channel : parentChannel) {
            long nextId = snowflake.nextId();
            channelIdMap.put(channel.getId(), nextId);
            channel.setId(nextId);
            channel.setLibraryId(toLibraryId);
        }
        for (Channel channel : childChannel) {
            Long parentId = channel.getParentId();
            Long newParentId = channelIdMap.get(parentId);
            long nextId = snowflake.nextId();
            channelIdMap.put(channel.getId(), nextId);
            channel.setId(nextId);
            channel.setParentId(newParentId);
            channel.setLibraryId(toLibraryId);

        }
        if (CollUtil.isNotEmpty(parentChannel)) {
            channelMapper.insertBatchSomeColumn(parentChannel);
        }
        if (CollUtil.isNotEmpty(childChannel)) {
            channelMapper.insertBatchSomeColumn(childChannel);
        }

        // 查询内容库下的 文章。
        LbqWrapper<ChannelContent> wrapper = Wraps.lbQ();
        wrapper.eq(ChannelContent::getLibraryId, fromLibraryId);
        Page<ChannelContent> contentPage = new Page<>();
        contentPage.setCurrent(0);
        contentPage.setSize(10);
        do {
            contentPage.setCurrent(contentPage.getCurrent() +1);
            contentPage = baseMapper.selectPage(contentPage, wrapper);
            List<ChannelContent> channelContents = contentPage.getRecords();
            if (CollUtil.isEmpty(channelContents)) {
                return false;
            }
            // 将查询到的 cms 复制 保存到项目的 cms库
            for (ChannelContent content : channelContents) {
                content.setId(null);
                content.setLibraryId(toLibraryId);
                Long channelId = channelIdMap.get(content.getChannelId());
                content.setChannelId(channelId);
                content.setHitCount(0L);
                content.setMessageNum(0);
            }
            baseMapper.insertBatchSomeColumn(channelContents);
        } while (contentPage.hasNext());
        return true;
    }

    /**
     * 从内容库 复制 cms 内容到 用户所属项目 的内容库
     */
    @Override
    public Boolean copyContent(ChannelContentCopyDto channelContentCopyDto) {
        Long libraryId = channelContentCopyDto.getLibraryId();
        String oldChannelId = channelContentCopyDto.getLibChannelId();
        String newChannelId = channelContentCopyDto.getTargetChannelId();
        List<String> ids = channelContentCopyDto.getLibContentIds();
        LbqWrapper<ChannelContent> wrapper = Wraps.lbQ();
        Boolean doctorOwner = channelContentCopyDto.getDoctorOwner();
        Long channelGroupId = channelContentCopyDto.getChannelGroupId();

        if (StringUtils.isEmpty(oldChannelId) && CollUtil.isEmpty(ids)) {
            return false;
        }
        if (Objects.nonNull(libraryId)) {
            wrapper.eq(ChannelContent::getLibraryId, libraryId);
        }

        // 没有选择文章。但是选择了栏目
        if (CollectionUtils.isEmpty(ids) && Objects.nonNull(oldChannelId)) {
            wrapper.eq(ChannelContent::getChannelId, oldChannelId);
        } else {
            wrapper.in(ChannelContent::getId, ids);
        }
        Page<ChannelContent> contentPage = new Page<>();
        contentPage.setCurrent(0);
        contentPage.setSize(10);
        String tenant = BaseContextHandler.getTenant();
        do {
            contentPage.setCurrent(contentPage.getCurrent() +1);
            List<ChannelContent> channelContents = queryChannelContentByOwnerType(contentPage, wrapper);
            if (CollUtil.isEmpty(channelContents)) {
                return false;
            }
            // 将查询到的 cms 复制 保存到项目的 cms库
            SaasGlobalThreadPool.execute(() -> saveCopyContent(channelContents, newChannelId, doctorOwner, channelGroupId, tenant));
        } while (contentPage.hasNext());
        return true;

    }

    /**
     * 查询cms频道内容
     * 考虑cms内容量大，修改为分页查询
     * @param query     查询条件
     * @return 频道内容列表
     */
    private List<ChannelContent> queryChannelContentByOwnerType( Page<ChannelContent> contentPage, LbqWrapper<ChannelContent> query) {
        List<ChannelContent> channelContents = new ArrayList<>();
        // 查询系统cms频道内容，此处需要切换当前租户为超级租户后再查询
        String originTenant = BaseContextHandler.getTenant();
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        baseMapper.selectPage(contentPage, query);
        channelContents = contentPage.getRecords();
        // 恢复为原租户信息
        BaseContextHandler.setTenant(originTenant);
        return channelContents;
    }

    /**
     * @param contentId
     * @return void
     * @Author yangShuai
     * @Description 增加 cms 内容的阅读量
     * @Date 2020/9/9 16:48
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHitCount(Long contentId) {
        ChannelContent content = baseMapper.selectByIdWithoutTenantNoContent(contentId);
        if (Objects.nonNull(content)) {
            Long hitCount = content.getHitCount();
            content.setHitCount(hitCount != null ? ++hitCount : 1);
            baseMapper.updateById(content);
            String tenant = BaseContextHandler.getTenant();
            BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.CMS_CONTENT_HIT_COUNT + tenant);
            Boolean hasKey = boundHashOps.hasKey(contentId.toString());
            if (Objects.nonNull(hasKey) && hasKey) {
                boundHashOps.increment(contentId.toString(), 1);
            } else {
                boundHashOps.put(contentId.toString(), content.getHitCount().toString());
            }
        }
    }


    /**
     * @param sourceId          来源表主键ID
     * @param doctorOwner
     * @param sourceEntityClass 来源实体类
     * @param channelGroupId 如果为null 则表示医生就是患者
     * @Author yangShuai
     * @Description 是否内容已经 复制到 项目
     * @Date 2020/9/9 17:04
     */
    public Boolean exist(Long sourceId, String newChannelId,  Boolean doctorOwner, String sourceEntityClass, Long channelGroupId) {
        LbqWrapper<ChannelContent> query = Wraps.<ChannelContent>lbQ()
                .eq(ChannelContent::getSourceId, sourceId)
                .eq(ChannelContent::getChannelId, newChannelId)
                .eq(ChannelContent::getSourceEntityClass, sourceEntityClass);
        if (Objects.isNull(channelGroupId)) {
            query.eq(ChannelContent::getDoctorOwner, doctorOwner);
        } else {
            query.eq(ChannelContent::getChannelGroupId, channelGroupId);
        }
        Integer integer = baseMapper.selectCount(query);
        return integer != null && integer > 0;
    }

    /**
     * @return java.lang.Boolean
     * @Author yangShuai
     * @Description 拷贝栏目内容到新的栏目下
     * @Date 2020/9/9 16:18
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveCopyContent(List<ChannelContent> channelContents, String newChannelId, Boolean doctorOwner, Long channelGroupId, String tenant) {
        BaseContextHandler.setTenant(tenant);
        List<ChannelContent> toSaveContent = new ArrayList<>(channelContents.size());
        for (ChannelContent channelContent : channelContents) {
            String sourceEntityClass = ChannelContent.class.getSimpleName();
            // 取消验证

            ChannelContent content = BeanUtil.copyProperties(channelContent, ChannelContent.class);
            if (doctorOwner != null) {
                content.setDoctorOwner(doctorOwner);
            }
            content.setSourceId(channelContent.getId());
            content.setSourceEntityClass(sourceEntityClass);
            if (!StringUtils.isEmpty(newChannelId)) {
                content.setChannelId(Long.parseLong(newChannelId));
            } else {
                content.setChannelId(null);
            }
            content.setHitCount(0L);
            content.setMessageNum(0);
            content.setId(null);
            content.setOwnerType(OwnerTypeEnum.TENANT);
            if (channelContent.getChannelType() == null || !"Article".equals(channelContent.getChannelType())) {
                channelContent.setChannelType("Article");
            }
            content.setChannelGroupId(channelGroupId);
            toSaveContent.add(content);
        }
        saveBatchSomeColumn(toSaveContent);
        return true;
    }

    /**
     * 优先从redis 中获取cms
     * 其次从tenant库获取cms
     * 最终从系统库获取cms
     * @param id
     * @return
     */
    @Override
    public ChannelContent getCmsByRedis(Long id) {

        String tenant = BaseContextHandler.getTenant();
        Object o = redisTemplate.opsForHash().get(SaasRedisBusinessKey.CMS_CONTENT_ALL + tenant, id.toString());
        if (Objects.isNull(o)) {
            ChannelContent channelContent = baseMapper.selectById(id);
            if (Objects.nonNull(channelContent)) {
                redisTemplate.opsForHash().put(SaasRedisBusinessKey.CMS_CONTENT_ALL + tenant, id.toString(), JSON.toJSONString(channelContent));
                return channelContent;
            }
            // 项目的没有，查系统的
            channelContent = getByIdWithoutTenant(id);
            return channelContent;
        } else {
            ChannelContent content = JSON.parseObject(o.toString(), ChannelContent.class);
            BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.CMS_CONTENT_HIT_COUNT + tenant);
            Object hitCount = boundHashOps.get(id.toString());
            if (Objects.nonNull(hitCount)) {
                content.setHitCount(Long.parseLong(hitCount.toString()));
            }
            return content;
        }
    }


    @Override
    public ChannelContent getByIdWithoutTenant(Long id) {
        return baseMapper.selectByIdWithoutTenant(id);
    }


    @Override
    public ChannelContent getByIdWithoutTenantNoContent(Long id) {
        return baseMapper.selectByIdWithoutTenantNoContent(id);
    }


    @Override
    public ChannelContent getTitleByIdWithoutTenant(Long id) {

        return baseMapper.selectTitleByIdWithoutTenant(id);
    }

    @Override
    public List<ChannelContent> listNoTenantCode(List<String> collect ) {

        String join = String.join("','", collect);
        return baseMapper.listNoTenantCode("'" + join + "'");
    }



    /**
     * 获取cms内容并设置二维码对应的链接
     * @param id
     * @param doctorId
     * @return
     */
    @Override
    public ChannelContent getContentByIdAndSetWeiXinCode(Long id, Long doctorId) {
//        String tenant = BaseContextHandler.getTenant();
        ChannelContent content = getCmsByRedis(id);
        if (Objects.isNull(content)) {
            return null;
        }
        // 检查cms内容中是否加入了医生二维码图片
//        if (!StringUtils.isEmpty(content.getContent())) {
//            if (content.getContent().contains("doctorWeiXinLoginCode") ||
//                    content.getContent().contains("patientWeiXinCode")) {
//                Document doc = Jsoup.parse(content.getContent());
//                Element doctorWeiXinLoginCode = doc.getElementById("doctorWeiXinLoginCode");
//                Element patientWeiXinCode = doc.getElementById("patientWeiXinCode");
//                if (Objects.nonNull(doctorWeiXinLoginCode)) {
//
//                    // 根据 temant 获取机构 内医生的登陆码
//                    R<Tenant> tenantApiByCode = tenantApi.getByCode(tenant);
//                    if (tenantApiByCode.getIsSuccess().equals(true) && Objects.nonNull(tenantApiByCode.getData())) {
//                        String doctorLoginUrl = tenantApiByCode.getData().getDoctorQrUrl();
//                        doctorWeiXinLoginCode.attr("src", doctorLoginUrl);
//                    } else {
//                        doctorWeiXinLoginCode.attr("src", "");
//                    }
//                }
//                if (Objects.nonNull(patientWeiXinCode)) {
//                    patientWeiXinCode.attr("src", "");
//                    if (doctorId != null) {
//                        // 获取医生的关注二维码
//                        R<Doctor> doctorR = doctorApi.get(doctorId);
//                        if (doctorR.getIsSuccess().equals(true) && Objects.nonNull(doctorR.getData())) {
//                            String businessCode = doctorR.getData().getBusinessCardQrCode();
//                            patientWeiXinCode.attr("src", businessCode);
//                        }
//                    }
//                }
//                String html = doc.body().html();
//                content.setContent(html.replace("\n", ""));
//
//            }
//        }
        return content;
    }

    /**
     * 复制一个文章
     * @param id
     * @param channelContentCopyOrMoveDto
     */
    public void copy(Long id, ChannelContentCopyOrMoveDto channelContentCopyOrMoveDto) {
        ChannelContent content = baseMapper.selectById(id);
        content.setId(null);
        content.setChannelGroupId(channelContentCopyOrMoveDto.getChannelGroupId());
        content.setDoctorOwner(channelContentCopyOrMoveDto.getDoctorOwner());
        content.setChannelId(channelContentCopyOrMoveDto.getChannelId());
        content.setMessageNum(0);
        content.setHitCount(0L);
        baseMapper.insert(content);
    }

    /**
     * 复制或者移动一个文章
     * @param channelContentCopyOrMoveDto
     * @return
     */
    @Override
    public Boolean copyOrMoveContent(ChannelContentCopyOrMoveDto channelContentCopyOrMoveDto) {
        Long id = channelContentCopyOrMoveDto.getId();
        Integer copyOrMove = channelContentCopyOrMoveDto.getCopyOrMove();
        List<Long> ids = channelContentCopyOrMoveDto.getIds();
        if (copyOrMove == 1) {
            if (ids != null && !ids.isEmpty()) {
                for (Long aLong : ids) {
                    copy(aLong, channelContentCopyOrMoveDto);
                }
            } else if (Objects.nonNull(id)) {
                copy(id, channelContentCopyOrMoveDto);
            }
        } else {
            UpdateWrapper<ChannelContent> updateWrapper = new UpdateWrapper<>();
            String tenant = BaseContextHandler.getTenant();
            if (ids != null && !ids.isEmpty()) {
                updateWrapper.in("id", ids);
                if (StrUtil.isNotEmpty(tenant) && !BizConstant.SUPER_TENANT.equals(tenant) ) {
                    for (Long aLong : ids) {
                        redisTemplate.opsForHash().delete(SaasRedisBusinessKey.CMS_CONTENT_ALL+ tenant, aLong.toString());
                    }
                }
            } else if (Objects.nonNull(id)) {
                updateWrapper.eq("id", id);
                if (StrUtil.isNotEmpty(tenant) && !BizConstant.SUPER_TENANT.equals(tenant) ) {
                    redisTemplate.opsForHash().delete(SaasRedisBusinessKey.CMS_CONTENT_ALL+ tenant, id.toString());
                }
            } else {
                return false;
            }
            if (channelContentCopyOrMoveDto.getChannelGroupId() == null) {
                updateWrapper.set("channel_group_id", null);
            } else {
                updateWrapper.set("channel_group_id", channelContentCopyOrMoveDto.getChannelGroupId());
            }
            updateWrapper.set("c_channel_id", channelContentCopyOrMoveDto.getChannelId());
            if (channelContentCopyOrMoveDto.getDoctorOwner() != null) {
                updateWrapper.set("doctor_owner", channelContentCopyOrMoveDto.getDoctorOwner());
            }

            baseMapper.update(new ChannelContent(), updateWrapper);
        }
        return true;
    }



}