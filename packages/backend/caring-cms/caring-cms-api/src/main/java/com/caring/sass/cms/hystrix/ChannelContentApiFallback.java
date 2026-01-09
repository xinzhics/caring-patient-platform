package com.caring.sass.cms.hystrix;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.base.R;
import com.caring.sass.cms.dto.ChannelContentPageDTO;
import com.caring.sass.cms.dto.ChannelContentSaveDTO;
import com.caring.sass.cms.dto.ChannelContentUpdateDTO;
import com.caring.sass.cms.entity.ChannelContent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChannelContentApiFallback implements ChannelContentApi {

    @Override
    public R<ChannelContent> queryContentById(Long id) {
        return R.timeout();
    }

    @Override
    public R<Boolean> delete(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<ChannelContent> get(Long aLong) {
        return R.success(ChannelContent.builder().build());
    }

    @Override
    public R<IPage<ChannelContent>> page(PageParams<ChannelContentPageDTO> params) {
        return R.success(new Page<>(1,10));
    }

    @Override
    public R<List<ChannelContent>> query(ChannelContent data) {
        return R.success(new ArrayList<>());
    }

    @Override
    public R<List<ChannelContent>> listByIds(List<Long> longs) {
        return R.success(new ArrayList<>());
    }

    @Override
    public R<ChannelContent> save(ChannelContentSaveDTO channelContentSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> saveBatch(List<ChannelContentSaveDTO> channelContentSaveDTOS) {
        return R.timeout();
    }

    @Override
    public R<ChannelContent> saveOrUpdate(ChannelContent channelContent) {
        return R.timeout();
    }

    @Override
    public R<ChannelContent> update(ChannelContentUpdateDTO channelContentUpdateDTO) {
        return R.timeout();
    }

    @Override
    public R<ChannelContent> getWithoutTenant(Long id) {
        return R.timeout();
    }

    @Override
    public R<ChannelContent> getBaseContent(Long id) {
        return R.timeout();
    }

    @Override
    public R<ChannelContent> getTitle(Long id) {
        return R.timeout();
    }
}
