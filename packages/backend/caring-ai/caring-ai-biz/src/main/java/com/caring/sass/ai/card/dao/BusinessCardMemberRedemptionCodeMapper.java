package com.caring.sass.ai.card.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.card.BusinessCardMemberRedemptionCode;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 机构会员兑换码
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")

public interface BusinessCardMemberRedemptionCodeMapper extends SuperMapper<BusinessCardMemberRedemptionCode> {

}
