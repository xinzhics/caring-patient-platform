package com.caring.sass.wx.service.config.impl;



import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.wx.dao.config.WxMenuMapper;
import com.caring.sass.wx.entity.config.WxMenu;
import com.caring.sass.wx.service.config.WxMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 微信公众号菜单
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-25
 */
@Slf4j
@Service

public class WxMenuServiceImpl extends SuperServiceImpl<WxMenuMapper, WxMenu> implements WxMenuService {
}
