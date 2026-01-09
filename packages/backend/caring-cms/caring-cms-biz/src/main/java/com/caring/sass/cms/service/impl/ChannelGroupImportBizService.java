package com.caring.sass.cms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.caring.sass.cms.dao.ChannelGroupMapper;
import com.caring.sass.cms.dao.ChannelMapper;
import com.caring.sass.cms.dto.ChanelGroupImportDto;
import com.caring.sass.cms.dto.ChannelContentCopyDto;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.cms.entity.ChannelGroup;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.enums.CmsRoleRemark;
import com.caring.sass.common.enums.OwnerTypeEnum;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName ChannelGroupImportBizService
 * @Description
 * @Author yangShuai
 * @Date 2021/10/21 10:12
 * @Version 1.0
 */
@Slf4j
@Service
public class ChannelGroupImportBizService {

    @Autowired
    ChannelGroupMapper channelGroupMapper;

    @Autowired
    ChannelMapper channelMapper;

    @Autowired
    DatabaseProperties databaseProperties;

    @Autowired
    ChannelContentService channelContentService;

    @Transactional
    public void importChannelGroup(ChanelGroupImportDto chanelGroupImportDto) {
        Long systemChanelId = chanelGroupImportDto.getChanelId();
        CmsRoleRemark cmsRoleRemark = chanelGroupImportDto.getCmsRoleRemark();
        String currentTenant = BaseContextHandler.getTenant();

        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        Channel systemChannel = channelMapper.selectById(systemChanelId);
        if (Objects.isNull(systemChannel)) {
            throw new BizException("系统栏目不存在");
        }
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        ChannelGroup channelGroup = ChannelGroup.builder().groupName(systemChannel.getChannelName())
                .cmsRoleRemark(cmsRoleRemark).build();
        channelGroup.setId(snowflake.nextId());
        // 查询系统栏目下的 子栏目
        List<Channel> channels = channelMapper.selectList(Wraps.<Channel>lbQ().eq(Channel::getParentId, systemChanelId));
        Map<Long, Long> saveChannel = new HashMap<>(channels.size());
        List<Channel> saveChannelList = new ArrayList<>();
        if (CollUtil.isNotEmpty(channels)) {
            for (Channel channel : channels) {
                long nextId = snowflake.nextId();
                Channel build = Channel.builder().channelGroupId(channelGroup.getId()).channelName(channel.getChannelName())
                        .channelType(channel.getChannelType())
                        .cmsRoleRemark(cmsRoleRemark)
                        .ownerType(OwnerTypeEnum.TENANT)
                        .build();
                build.setId(nextId);
                saveChannel.put(channel.getId(), nextId);
                saveChannelList.add(build);
            }
        }
        BaseContextHandler.setTenant(currentTenant);
        channelGroupMapper.insert(channelGroup);
        if (CollUtil.isNotEmpty(saveChannelList)) {
            channelMapper.insertBatchSomeColumn(saveChannelList);
        }

        // 页面栏目已经新建完毕。  将系统栏目下的文章导入到指定的页面 栏目下

        // 先复制 根栏目下的文章
        ChannelContentCopyDto channelContentCopyDto = ChannelContentCopyDto.builder()
                .channelGroupId(channelGroup.getId())
                .cmsRoleRemark(cmsRoleRemark)
                .libChannelId(systemChanelId.toString())
                .build();
        channelContentService.copyContent(channelContentCopyDto);
        if (saveChannel.size() > 0) {
            for (Map.Entry<Long, Long> map : saveChannel.entrySet()) {
                Long key = map.getKey();
                Long value = map.getValue();
                channelContentCopyDto = ChannelContentCopyDto.builder()
                        .channelGroupId(channelGroup.getId())
                        .cmsRoleRemark(cmsRoleRemark)
                        .libChannelId(key.toString())
                        .targetChannelId(value.toString())
                        .build();
                channelContentService.copyContent(channelContentCopyDto);
            }
        }
    }

}
