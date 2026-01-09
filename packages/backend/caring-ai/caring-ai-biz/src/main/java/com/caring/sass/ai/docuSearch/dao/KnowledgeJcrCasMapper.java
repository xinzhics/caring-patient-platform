package com.caring.sass.ai.docuSearch.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.docuSearch.KnowledgeJcrCas;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * jcr和cas分区信息表
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-18
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeJcrCasMapper extends SuperMapper<KnowledgeJcrCas> {

}
