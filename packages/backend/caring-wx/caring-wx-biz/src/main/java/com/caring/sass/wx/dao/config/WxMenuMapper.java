package com.caring.sass.wx.dao.config;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;

import com.caring.sass.wx.entity.config.WxMenu;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 微信公众号菜单
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-25
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface WxMenuMapper extends SuperMapper<WxMenu> {

}
