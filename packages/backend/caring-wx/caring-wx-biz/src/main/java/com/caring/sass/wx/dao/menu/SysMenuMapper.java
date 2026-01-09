package com.caring.sass.wx.dao.menu;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.menu.SysMenu;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 系统定义的微信菜单
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface SysMenuMapper extends SuperMapper<SysMenu> {

}
