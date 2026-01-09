package com.caring.sass.ai.card.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.card.BusinessCardDiagram;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 医生名片头像模板
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-10
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface BusinessCardDiagramMapper extends SuperMapper<BusinessCardDiagram> {

}
