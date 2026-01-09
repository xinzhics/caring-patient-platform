package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;

import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.entity.MiniappInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 小程序用户openId关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-22
 */
@Repository
public interface MiniappInfoMapper extends SuperMapper<MiniappInfo> {



    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    MiniappInfo selectByIdNoTenant(@Param("id") Long id);


}
