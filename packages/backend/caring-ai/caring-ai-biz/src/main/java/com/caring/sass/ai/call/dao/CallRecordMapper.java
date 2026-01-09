package com.caring.sass.ai.call.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.call.CallRecord;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 分身通话记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface CallRecordMapper extends SuperMapper<CallRecord> {

}
