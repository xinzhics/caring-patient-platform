package com.caring.sass.wx.dao.config;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.config.Config;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 微信配置信息
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface ConfigMapper extends SuperMapper<Config> {

    /**
     * 查询系统所有微信公众号配置信息
     */
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Config> listAllConfigWithoutTenant();

    /**
     * 查询系统所有的微信小程序
     * @return
     */
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Config> listAllMiniAppWithoutTenant();

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Config> selectByAppIdWithoutTenant(@Param(value = "appId") String appId);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    Config selectBySourceIdWithoutTenant(@Param(value = "sourceId") String sourceId);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    Config selectByIdWithoutTenant(@Param(value = "id") Long id);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Config> selectTokenTimeOutWithoutTenant(@Param(value = "accessTokenCreateTime") Long accessTokenCreateTime);

    @Override
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    int deleteById(Serializable id);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Config> selectMenuLikeUrl(@Param(value = "shareUrl") String shareUrl);


    @Override
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    <E extends IPage<Config>> E selectPage(E page, Wrapper<Config> queryWrapper);
}
