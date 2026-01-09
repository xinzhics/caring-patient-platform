package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.know.KnowledgeFileCaseDatabaseLabel;
import com.caring.sass.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 知识库文档标签表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-25
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeFileCaseDatabaseLabelMapper extends SuperMapper<KnowledgeFileCaseDatabaseLabel> {

}
