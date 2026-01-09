package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 栏目
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
public interface ChannelService extends SuperService<Channel> {

    Boolean updateSort(Long channelId, Integer sort);

    Boolean initChannel();

    List<Channel> findList(LbqWrapper<Channel> lbqWrapper);

    /**
     * 复制栏目及栏目内容
     *
     * @param fromTenantCode 从该项目编码复制数据
     * @param toTenantCode 复制数据到该项目编码
     * @return
     */
    Boolean copyChannelAndChannelContent(String fromTenantCode, String toTenantCode);

    /**
     * 假设下面只有 一级
     * 获取栏目下的子栏目
     * @param channelId
     * @return
     */
    List<Long> getChildChanelId(Long channelId);

    /**
     * 删除页面和页面下的所有
     * @param id
     */
    void deleteChannelGroup(Long id);


}
