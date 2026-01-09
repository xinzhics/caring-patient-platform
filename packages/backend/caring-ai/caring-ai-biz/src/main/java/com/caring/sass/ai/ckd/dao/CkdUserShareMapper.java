package com.caring.sass.ai.ckd.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.ckd.CkdUserShare;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * ckd会员分享成功转换记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-14
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface CkdUserShareMapper extends SuperMapper<CkdUserShare> {

}
