package com.caring.sass.ai.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.CkdRecipeRecord;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户食谱记录
 * </p>
 *
 * @author leizhi
 * @date 2025-04-23
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface CkdRecipeRecordMapper extends SuperMapper<CkdRecipeRecord> {

}
