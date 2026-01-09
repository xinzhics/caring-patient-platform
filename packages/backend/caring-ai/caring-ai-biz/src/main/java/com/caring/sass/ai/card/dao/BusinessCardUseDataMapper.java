package com.caring.sass.ai.card.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.card.BusinessCardUseData;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 名片使用数据收集
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface BusinessCardUseDataMapper extends SuperMapper<BusinessCardUseData> {

}
