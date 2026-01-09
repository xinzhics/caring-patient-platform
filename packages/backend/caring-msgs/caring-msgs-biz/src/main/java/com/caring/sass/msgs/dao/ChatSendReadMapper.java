package com.caring.sass.msgs.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.msgs.entity.ChatSendRead;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 未读消息
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ChatSendReadMapper extends SuperMapper<ChatSendRead> {


    @Select("SELECT DISTINCT group_id from m_msg_chat_send_read where user_id in (#{userId}) and role_type = #{userType} and is_delete = 0 ORDER BY create_time asc ${pageSql}")
    @Results({
            @Result(column="group_id", javaType=String.class, property="group_id"),
    })
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<String> distinctGroupId(@Param("userId") String userId, @Param("userType") String userType,  @Param("pageSql") String pageSql);


    @Select("SELECT count(a.group_id) as countNumber FROM (SELECT group_id FROM `m_msg_chat_send_read` WHERE user_id = #{userId}" +
            " and is_delete = 0 and send_user_role = 'patient' and role_type = #{userType} and need_sms_notice = 1 and create_time >= #{now} GROUP BY group_id) as a")
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    Integer countPatientNumber(@Param("userId") Long userId, @Param("userType") String userType, @Param("now") LocalDateTime now);

}
