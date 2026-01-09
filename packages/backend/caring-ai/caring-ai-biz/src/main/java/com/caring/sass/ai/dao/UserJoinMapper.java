package com.caring.sass.ai.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.UserJoin;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * ai用户关联表
 * </p>
 *
 * @author 杨帅
 * @date 2025-07-29
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface UserJoinMapper extends SuperMapper<UserJoin> {

}
