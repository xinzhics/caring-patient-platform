package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.ChannelContent;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 栏目内容
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Repository
public interface ChannelContentMapper extends SuperMapper<ChannelContent> {

    /**
     * 忽略租户信息
     *
     * @param id 通过id查询
     */
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    ChannelContent selectByIdWithoutTenant(@Param("id") Long id);


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    ChannelContent selectByIdWithoutTenantNoContent(@Param("id") Long id);

    /**
     * 忽略租户信息，查询文章的标题和缩略图
     * @param id
     * @return
     */
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    ChannelContent selectTitleByIdWithoutTenant(@Param("id") Long id);

    @Select("select t.id,t.c_channel_id,t.c_title,t.c_icon,t.c_link,t.n_sort,t.n_is_top, t.n_hit_count,t.c_channel_type," +
            "t.c_summary,t.n_can_comment,t.n_message_num,t.create_time from t_cms_channel_content t where id in ( ${ids} )")
    @Results({
            @Result(column="id", javaType=Long.class, property="id"),
            @Result(column="c_channel_id", javaType=Long.class, property="channelId"),
            @Result(column="c_title", javaType=String.class, property="title"),
            @Result(column="c_icon", javaType=String.class, property="icon"),
            @Result(column="c_link", javaType=String.class, property="link"),
            @Result(column="n_sort", javaType=Integer.class, property="sort"),
            @Result(column="n_is_top", javaType=Integer.class, property="isTop"),
            @Result(column="n_hit_count", javaType=Long.class, property="hitCount"),
            @Result(column="c_channel_type", javaType=String.class, property="channelType"),
            @Result(column="c_summary", javaType=String.class, property="summary"),
            @Result(column="n_can_comment", javaType=Integer.class, property="canComment"),
            @Result(column="n_message_num", javaType=Integer.class, property="messageNum"),
            @Result(column="create_time", javaType= LocalDateTime.class, property="createTime")
    })
    @SqlParser(filter = true)
    List<ChannelContent> listNoTenantCode(@org.apache.ibatis.annotations.Param("ids") String ids);
}
