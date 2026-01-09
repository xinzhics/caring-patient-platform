package com.caring.sass.wx.service.menu.impl;


import com.caring.sass.wx.dao.menu.SysMenuMapper;
import com.caring.sass.wx.entity.menu.SysMenu;
import com.caring.sass.wx.service.menu.SysMenuService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 系统定义的微信菜单
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class SysMenuServiceImpl extends SuperServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
}
