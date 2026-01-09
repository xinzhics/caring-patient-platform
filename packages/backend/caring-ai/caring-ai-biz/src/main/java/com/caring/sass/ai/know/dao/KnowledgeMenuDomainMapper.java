package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.know.KnowledgeMenuDomain;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 知识库专科域名
 * </p>
 *
 * @author 杨帅
 * @date 2025-07-23
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeMenuDomainMapper extends SuperMapper<KnowledgeMenuDomain> {

}
