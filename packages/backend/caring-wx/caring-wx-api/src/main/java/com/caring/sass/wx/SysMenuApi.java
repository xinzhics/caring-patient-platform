package com.caring.sass.wx;

import com.caring.sass.base.api.SuperApi;
import com.caring.sass.wx.dto.menu.SysMenuPageDTO;
import com.caring.sass.wx.dto.menu.SysMenuSaveDTO;
import com.caring.sass.wx.dto.menu.SysMenuUpdateDTO;
import com.caring.sass.wx.entity.menu.SysMenu;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * 系统菜单api
 *
 * @author xinzh
 */
@FeignClient(name = "${caring.feign.wx-server:caring-wx-server}", path = "/sysMenu",
        qualifier = "SysMenuApi")
public interface SysMenuApi extends SuperApi<Long, SysMenu, SysMenuPageDTO, SysMenuSaveDTO, SysMenuUpdateDTO> {


}
