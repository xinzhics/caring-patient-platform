package com.caring.sass.ai.docuSearch.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.docuSearch.KnowledgeUserBrowsingHistory;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 知识库证据浏览记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-17
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeUserBrowsingHistoryMapper extends SuperMapper<KnowledgeUserBrowsingHistory> {

}
