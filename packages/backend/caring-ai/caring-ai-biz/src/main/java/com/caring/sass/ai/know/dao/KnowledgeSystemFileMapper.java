package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.know.KnowledgeSystemFile;
import com.caring.sass.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeSystemFileMapper extends SuperMapper<KnowledgeSystemFile> {


}
