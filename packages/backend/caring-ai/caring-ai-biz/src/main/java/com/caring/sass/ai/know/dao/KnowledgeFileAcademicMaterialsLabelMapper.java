package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.know.KnowledgeFileAcademicMaterialsLabel;
import com.caring.sass.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 专业学术资料标签
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-25
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeFileAcademicMaterialsLabelMapper extends SuperMapper<KnowledgeFileAcademicMaterialsLabel> {

}
