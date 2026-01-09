package com.caring.sass.ai.ckd.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.ckd.CkdUserGfr;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户GFR
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface CkdUserGfrMapper extends SuperMapper<CkdUserGfr> {

}
