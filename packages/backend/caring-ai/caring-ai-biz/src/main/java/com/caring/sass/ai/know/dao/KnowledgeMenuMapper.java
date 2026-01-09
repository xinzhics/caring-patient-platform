package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.know.KnowledgeMenu;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 知识库菜单
 * </p>
 *
 * @author 杨帅
 * @date 2025-07-21
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface KnowledgeMenuMapper extends SuperMapper<KnowledgeMenu> {

}
