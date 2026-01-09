package com.caring.sass.ai.card.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * AI名片
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-22
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface BusinessCardMapper extends SuperMapper<BusinessCard> {

}
