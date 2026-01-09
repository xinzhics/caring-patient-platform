package com.caring.sass.msgs.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.msgs.dto.InitNursingMsgListDTO;
import com.caring.sass.msgs.entity.ChatUserNewMsg;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserNewMsgMapper extends SuperMapper<ChatUserNewMsg> {


    /**
     * 禁止使用  2.3 初始化im列表信息使用一次
     */
    @Select("SELECT id, name, remark, doctor_remark, avatar, im_account, service_advisor_id, doctor_id from u_user_patient where im_account in (SELECT t.receiver_im_account from m_msg_chat t where t.id in (${chatId})) ")
    @Results({
            @Result(column="id", javaType=Long.class, property="patientId"),
            @Result(column="name", javaType=String.class, property="patientName"),
            @Result(column="remark", javaType=String.class, property="patientRemark"),
            @Result(column="doctor_remark", javaType=String.class, property="doctorRemark"),
            @Result(column="avatar", javaType=String.class, property="patientAvatar"),
            @Result(column="im_account", javaType=String.class, property="receiverImAccount"),
            @Result(column="service_advisor_id", javaType=Long.class, property="serviceAdvisorId"),
            @Result(column="doctor_id", javaType=Long.class, property="doctorId")
    })
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<InitNursingMsgListDTO> getPatientInfoFromChatId(@Param("chatId") String chatId);


    @Select("(SELECT DISTINCT un.id from m_msg_chat_user_new as un" +
            " left join ( SELECT ar.* from m_msg_chat_at_record ar where ar.at_user_id = #{atUserId} and ar.user_type = #{userType}) as ar on un.id = ar.chat_user_new_msg_id " +
            " LEFT JOIN ( SELECT unread.* FROM m_msg_chat_send_read unread WHERE unread.role_type = #{userType} and unread.user_id = #{atUserId} and unread.is_delete = 0) as unread on unread.chat_id = un.chat_id " +
            "where ${sql} ) ${pageSql}")
    @Results({
            @Result(column="id", javaType=Long.class, property="id"),
    })
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Long> distinctIdForChatMsg(@Param("atUserId") Long atUserId, @Param("userType") String userType, @Param("sql") String sql, @Param("pageSql") String pageSql);

}
