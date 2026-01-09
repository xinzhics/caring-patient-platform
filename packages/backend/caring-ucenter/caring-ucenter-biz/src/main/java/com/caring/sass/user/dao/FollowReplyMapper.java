package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;

import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.entity.FollowReply;
import com.caring.sass.user.entity.KeywordSetting;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 关注后回复和关注未注册回复
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Repository
public interface FollowReplyMapper extends SuperMapper<FollowReply> {


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<FollowReply> selectAllTenantCode(@Param("replyCategory") String replyCategory, @Param("replyTime") String replyTime, @Param("replySwitch") String open);

}
