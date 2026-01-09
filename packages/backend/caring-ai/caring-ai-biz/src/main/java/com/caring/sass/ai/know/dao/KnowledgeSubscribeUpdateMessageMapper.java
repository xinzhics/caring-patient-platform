package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.know.KnowledgeSubscribeUpdateMessage;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 博主订阅设置修改记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-08-05
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeSubscribeUpdateMessageMapper extends SuperMapper<KnowledgeSubscribeUpdateMessage> {

}
