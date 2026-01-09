package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.ChannelGroupMapper;
import com.caring.sass.cms.dao.ChannelMapper;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.ChannelGroup;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.ChannelService;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.enums.OwnerTypeEnum;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 栏目
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Slf4j
@Service

public class ChannelServiceImpl extends SuperServiceImpl<ChannelMapper, Channel> implements ChannelService {

    private final ChannelContentService channelContentService;

    private final ChannelGroupMapper channelGroupMapper;

    private final DatabaseProperties databaseProperties;

    public ChannelServiceImpl(ChannelContentService channelContentService, DatabaseProperties databaseProperties, ChannelGroupMapper channelGroupMapper) {
        this.channelContentService = channelContentService;
        this.databaseProperties = databaseProperties;
        this.channelGroupMapper = channelGroupMapper;
    }

    /**
     * @Author yangShuai
     * @Description 更新栏目的排序字段
     * @Date 2020/9/10 9:58
     *
     * @return java.lang.Boolean
     */
    @Override
    public Boolean updateSort(Long channelId, Integer sort) {
        UpdateWrapper<Channel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("sort", sort);
        updateWrapper.eq("id", channelId);
        return update(updateWrapper);
    }

    public Integer countChannel(Channel channel) {
        return baseMapper.selectCount(Wraps.<Channel>lbQ().eq(Channel::getDoctorOwner, channel.getDoctorOwner())
                .eq(Channel::getChannelGroupId, channel.getChannelGroupId())
                .eq(Channel::getOwnerType, channel.getOwnerType()));
    }

    /**
     * @return boolean
     * @Author yangShuai
     * @Date 2020/9/10 9:30
     */
    @Override
    public boolean save(Channel channel) {
        // 当上传栏目不是 超级管理员 上传时 检验项目是否已经添加了 10 个自己的栏目。 不包含系统自动给其添加的图文消息，Banner两个栏目
        OwnerTypeEnum ownerType = channel.getOwnerType();
        if (!Objects.equals(ownerType, OwnerTypeEnum.SYS)) {
            if (countChannel(channel) > 10) {
                throw new BizException("栏目数量已经达到上限啦！");
            }
        }
        return super.save(channel);
    }



    @Transactional
    @Override
    public Boolean initChannel() {
        checkAndInitSysTextImageCmsExist();
        return true;
    }

    @Async
    public void checkAndInitSysTextImageCmsExist() {
        SaasGlobalThreadPool.execute(() -> {
            BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
            LbqWrapper<Channel> channelLbqWrapper = new LbqWrapper<>();
            channelLbqWrapper.eq(Channel::getChannelType, "TextImage");
            List<Channel> channelList = baseMapper.selectList(channelLbqWrapper);
            if (CollectionUtils.isEmpty(channelList)) {
                Channel textImage = Channel.builder().channelName("图文消息").channelType("TextImage").ownerType(OwnerTypeEnum.SYS).sort(0).build();
                baseMapper.insert(textImage);
                channelContentService.initTextImageContent(textImage);
            }
        });
    }

    @Override
    public List<Channel> findList(LbqWrapper<Channel> lbqWrapper) {
        return baseMapper.selectList(lbqWrapper);
    }

    @Transactional
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        List<Channel> channelList = baseMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(channelList)) {
            return true;
        }
        List<Long> removeIds = new ArrayList<>(30);
        for (Channel channel : channelList) {
            if (channel.getChannelType() != null && channel.getChannelType().equals("TextImage")) {
                continue;
            }
            removeIds.add(channel.getId());
        }
        if (CollectionUtils.isEmpty(removeIds)) {
            return true;
        }
        // 获取栏目下的子栏目
        List<Channel> channels = baseMapper.selectList(Wraps.<Channel>lbQ().select(Channel::getId).in(Channel::getParentId, removeIds));
        if (!CollectionUtils.isEmpty(channels)) {
            channels.forEach(item -> removeIds.add(item.getId()));
        }

        channelContentService.remove(Wraps.<ChannelContent>lbQ().in(ChannelContent::getChannelId, removeIds));
        baseMapper.deleteBatchIds(removeIds);
        return true;
    }

    @Override
    public Boolean copyChannelAndChannelContent(String fromTenantCode, String toTenantCode) {
        String currentTenant = BaseContextHandler.getTenant();

        // 查找出需要复制的项目数据
        BaseContextHandler.setTenant(fromTenantCode);

        List<Channel> channels = baseMapper.selectList(Wrappers.emptyWrapper());
        List<ChannelContent> channelContents = channelContentService.list();
        List<ChannelGroup> channelGroups = channelGroupMapper.selectList(Wrappers.emptyWrapper());
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());
        // 新旧ID
        Map<Long, Long> channelGroupsIds = new HashMap<>(channelGroups.size());
        List<ChannelGroup> toSaveChannelsGroups = channelGroups.stream().peek(channelGroup -> {
            long nextId = snowflake.nextId();
            channelGroupsIds.put(channelGroup.getId(), nextId);
            channelGroup.setId(nextId);
        }).collect(Collectors.toList());
        // 修改栏目数据
        // 新旧id匹配
        Map<Long, Long> channelIdMap = new HashMap<>(channels.size());
        List<Channel> toSaveChannels = channels.stream().peek(c -> {
            c.setOwnerType(OwnerTypeEnum.TENANT);
            long nextId = snowflake.nextId();
            channelIdMap.put(c.getId(), nextId);
            c.setId(nextId);
            if (Objects.nonNull(c.getChannelGroupId())) {
                Long newGroupId = channelGroupsIds.get(c.getChannelGroupId());
                c.setChannelGroupId(newGroupId);
            }
        }).collect(Collectors.toList());

        for (ChannelContent channelContent : channelContents) {
            channelContent.setChannelId(channelIdMap.get(channelContent.getChannelId()));
            if (Objects.nonNull(channelContent.getChannelGroupId())) {
                Long newGroupId = channelGroupsIds.get(channelContent.getChannelGroupId());
                channelContent.setChannelGroupId(newGroupId);
            }
            channelContent.setId(null);
        }

        // 保存修改后的数据
        BaseContextHandler.setTenant(toTenantCode);
        if (CollUtil.isNotEmpty(toSaveChannels)) {
            saveBatchSomeColumn(toSaveChannels);
        }
        if (CollUtil.isNotEmpty(channelContents)) {
            channelContentService.saveBatch(channelContents);
        }
        if (CollUtil.isNotEmpty(toSaveChannelsGroups)) {
            channelGroupMapper.insertBatchSomeColumn(toSaveChannelsGroups);
        }
        BaseContextHandler.setTenant(currentTenant);
        return true;
    }


    @Override
    public List<Long> getChildChanelId(Long channelId) {
        LbqWrapper<Channel> lbqWrapper = Wraps.<Channel>lbQ().eq(Channel::getParentId, channelId)
                .select(Channel::getId);
        List<Channel> channels = baseMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(channels)) {
            return new ArrayList<>();
        } else {
           return channels.stream().map(SuperEntity::getId).collect(Collectors.toList());
        }
    }


    /**
     * 删除页面下的 栏目 和 文章
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteChannelGroup(Long id) {

        channelGroupMapper.deleteById(id);
        baseMapper.delete(Wraps.<Channel>lbQ().eq(Channel::getChannelGroupId, id));
        channelContentService.remove(Wraps.<ChannelContent>lbQ().eq(ChannelContent::getChannelGroupId, id));

    }
}
