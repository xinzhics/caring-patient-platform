package com.caring.sass.msgs.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.msgs.entity.ChatGroupSend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatGroupSendMapper extends SuperMapper<ChatGroupSend> {

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<ChatGroupSend> queryChatGroupTiming(@Param("sendTime") LocalDateTime sendTime);


    /**
     * 医生查询群发消息列表
     * @param userId
     * @param searchKeyName
     * @param userRole
     * @return
     */
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<ChatGroupSend> pageDoctorChatGroupSearch(@Param("userId") Long userId,
                                                  @Param("searchKeyName") String searchKeyName,
                                                  @Param("pageIndex") Integer pageIndex,
                                                  @Param("userRole") String userRole);



}
