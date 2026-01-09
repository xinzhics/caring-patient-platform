package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.dto.ChannelContentCopyDto;
import com.caring.sass.cms.dto.ChannelContentCopyOrMoveDto;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.cms.entity.ChannelContent;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 栏目内容
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
public interface ChannelContentService extends SuperService<ChannelContent> {


    /**
     * 从内容库 复制 cms 内容到 用户所属项目 的内容库
     */
    Boolean copyContent(ChannelContentCopyDto channelContentCopyDto);

    /**
     * 从内容库复制到另一个内容库
     * @param fromLibraryId
     * @param toLibraryId
     * @return
     */
    boolean copyContent(Long fromLibraryId, Long toLibraryId);


    /**
     * @param id 主键
     * @return void
     * @Author yangShuai
     * @Description 增加 cms 内容的阅读量
     * @Date 2020/9/9 16:48
     */
    void updateHitCount(Long id);


    /**
     * @return java.lang.Boolean
     * @Author yangShuai
     * @Description 更新内容的 排序字段
     * @Date 2020/9/10 9:58
     */
    Boolean updateSort(String contentId, Integer sort);

    /**
     * @return boolean
     * @Author yangShuai
     * @Description 初始化图文消息
     * @Date 2020/10/26 14:45
     */
    boolean initTextImageContent(Channel textImage);


    ChannelContent getCmsByRedis(Long id);

    /**
     * 通过id查询，不带项目信息
     *
     * @param id 主键
     */
    ChannelContent getByIdWithoutTenant(Long id);

    /**
     * 通过id查询标题链接图标，不带项目信息
     *
     * @param id 主键
     */
    ChannelContent getByIdWithoutTenantNoContent(Long id);

    /**
     * 忽略租户信息，查询文章的标题和缩略图
     * @param id
     * @return
     */
    ChannelContent getTitleByIdWithoutTenant(Long id);


    List<ChannelContent> listNoTenantCode(List<String> collect );


    ChannelContent getContentByIdAndSetWeiXinCode(Long id, Long doctorId);

    Boolean copyOrMoveContent(ChannelContentCopyOrMoveDto channelContentCopyOrMoveDto);




}
