package com.caring.sass.ai.sms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.sms.AiSmsTask;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * AI短信验证码表
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-12
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface AiSmsTaskMapper extends SuperMapper<AiSmsTask> {

}
