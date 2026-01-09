package com.caring.sass.ai.face.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.face.MegviiTemplateDiagram;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 模版图管理
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface MegviiTemplateDiagramMapper extends SuperMapper<MegviiTemplateDiagram> {

}
