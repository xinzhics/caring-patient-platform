package com.caring.sass.msgs.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.msgs.entity.Chat;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMapper extends SuperMapper<Chat> {


    @Select("select count(1) from m_msg_chat t where t.sender_im_account = #{receiverImAccount} and t.receiver_id = #{requestId} and t.receiver_role_type = #{requestRoleType} and t.status = 0")
    @SqlParser(filter = true)
    Integer countDoctorNoReadTotal(@Param("receiverImAccount") String receiverImAccount,
                                   @Param("requestId") String requestId,
                                   @Param("requestRoleType") String requestRoleType);

    @Delete("delete from m_msg_chat where sender_im_account = #{patientImAccount} or receiver_im_account = #{patientImAccount}")
    @SqlParser(filter = true)
    void deleteByPatientImAccount(@Param("patientImAccount") String patientImAccount);


    @Select("SELECT receiver_im_account FROM `m_msg_chat` WHERE tenant_code=#{tenant} AND receiver_im_account IN ( SELECT im_account FROM u_user_patient WHERE service_advisor_id = #{requestId} )" +
            " AND content LIKE #{keyWord} " +
            " GROUP BY " +
            " receiver_im_account" +
            " UNION" +
            " SELECT im_account FROM u_user_patient WHERE service_advisor_id = #{requestId} AND NAME LIKE #{keyWord}")
    @Results({
            @Result(column="receiver_im_account", javaType=String.class, property="receiverImAccount")
    })
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<String> selectReceiverImAccount(@Param("tenant") String tenant, @Param("requestId") String requestId, @Param("keyWord") String keyWord);
}
