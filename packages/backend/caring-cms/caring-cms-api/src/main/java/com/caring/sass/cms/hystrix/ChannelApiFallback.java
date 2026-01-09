package com.caring.sass.cms.hystrix;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.ChannelApi;
import com.caring.sass.cms.dto.ChannelPageDTO;
import com.caring.sass.cms.dto.ChannelSaveDTO;
import com.caring.sass.cms.dto.ChannelUpdateDTO;
import com.caring.sass.cms.entity.Channel;

import java.util.List;


/**
 * @author xinzh
 */
//@Component
public class ChannelApiFallback implements ChannelApi {

    @Override
    public R<Boolean> initChannel() {
        return R.timeout();
    }

    @Override
    public R<Boolean> delete(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Channel> get(Long aLong) {
        return R.timeout();
    }

    @Override
    public R<IPage<Channel>> page(PageParams<ChannelPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<List<Channel>> query(Channel data) {
        return R.timeout();
    }

    @Override
    public R<List<Channel>> listByIds(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Channel> save(ChannelSaveDTO channelSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> saveBatch(List<ChannelSaveDTO> channelSaveDTOS) {
        return R.timeout();
    }

    @Override
    public R<Channel> saveOrUpdate(Channel channel) {
        return R.timeout();
    }

    @Override
    public R<Channel> update(ChannelUpdateDTO channelUpdateDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> copyChannelAndChannelContent(String fromTenantCode, String toTenantCode) {
        return R.timeout();
    }

    @Override
    public R<List<Channel>> list(Channel data) {
        return R.timeout();
    }
}
