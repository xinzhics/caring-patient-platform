package com.caring.sass.wx.service.menu.impl;


import com.caring.sass.wx.dao.menu.CustomMenuMapper;
import com.caring.sass.wx.entity.menu.CustomMenu;
import com.caring.sass.wx.service.menu.CustomMenuService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 项目自定义微信菜单
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class CustomMenuServiceImpl extends SuperServiceImpl<CustomMenuMapper, CustomMenu> implements CustomMenuService {
}
