package com.caring.sass.cms.service.impl;

import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.ContentLibraryMapper;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.ContentLibrary;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.ChannelService;
import com.caring.sass.cms.service.ContentLibraryService;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * @ClassName ContentLibraryServerImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/5/5 13:19
 * @Version 1.0
 */
@Slf4j
@Service
public class ContentLibraryServerImpl extends SuperServiceImpl<ContentLibraryMapper, ContentLibrary> implements ContentLibraryService {

    @Autowired
    ChannelService channelService;

    @Autowired
    ChannelContentService channelContentService;



    @Override
    public void copyContentLibrary(Long libraryId) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        ContentLibrary library = baseMapper.selectById(libraryId);
        if (Objects.isNull(library)){
            return;
        }
        library.setLibraryName(library.getLibraryName() + "副本");
        library.setId(null);
        baseMapper.insert(library);
        channelContentService.copyContent(libraryId,  library.getId());

    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }


    @Override
    public boolean removeById(Serializable id) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        channelService.remove(Wraps.<Channel>lbQ().eq(Channel::getLibraryId, id));
        channelContentService.remove(Wraps.<ChannelContent>lbQ().eq(ChannelContent::getLibraryId, id));
        baseMapper.deleteById(id);
        return true;
    }
}
