package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 知识库用户购买会员订单
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-11
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeUserOrderMapper extends SuperMapper<KnowledgeUserOrder> {

}
