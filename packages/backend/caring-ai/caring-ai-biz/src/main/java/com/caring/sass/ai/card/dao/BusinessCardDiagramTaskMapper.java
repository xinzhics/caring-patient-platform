package com.caring.sass.ai.card.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.card.BusinessCardDiagramTask;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户名片任务
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-18
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface BusinessCardDiagramTaskMapper extends SuperMapper<BusinessCardDiagramTask> {

}
