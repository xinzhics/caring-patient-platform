package com.caring.sass.ai.card.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import  com.caring.sass.ai.entity.card.BusinessCardMemberVersion;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户的会员版本
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")

public interface BusinessCardMemberVersionMapper extends SuperMapper<BusinessCardMemberVersion> {

}
