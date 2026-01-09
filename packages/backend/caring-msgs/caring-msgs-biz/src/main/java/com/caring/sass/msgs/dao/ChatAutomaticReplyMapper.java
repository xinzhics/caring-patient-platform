package com.caring.sass.msgs.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.msgs.entity.ChatAutomaticReply;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-22
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ChatAutomaticReplyMapper extends SuperMapper<ChatAutomaticReply> {

}
