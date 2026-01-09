package com.caring.sass.sms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.sms.entity.BusinessReminderLog;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-17
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface BusinessReminderLogMapper extends SuperMapper<BusinessReminderLog> {

}
