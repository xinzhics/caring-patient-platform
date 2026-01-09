package com.caring.sass.wx;

import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.wx.dto.menu.CustomMenuPageDTO;
import com.caring.sass.wx.dto.menu.CustomMenuSaveDTO;
import com.caring.sass.wx.dto.menu.CustomMenuUpdateDTO;
import com.caring.sass.wx.entity.menu.CustomMenu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 自定义菜单api
 *
 * @author xinzh
 */
@FeignClient(name = "${caring.feign.wx-server:caring-wx-server}", path = "/customMenu",
        qualifier = "CustomMenuApi")
public interface CustomMenuApi extends SuperApi<Long, CustomMenu, CustomMenuPageDTO, CustomMenuSaveDTO, CustomMenuUpdateDTO> {

    /**
     * 新增一个自定义菜单
     */
    @PostMapping("saveCustomMenu")
    R<CustomMenu> saveCustomMenu(@RequestBody CustomMenuSaveDTO customMenuSaveDTO);

    /**
     * 修改一个自定义菜单
     */
    @PutMapping("putCustomMenu")
    R<CustomMenu> putCustomMenu(@RequestBody CustomMenuUpdateDTO customMenuUpdateDTO);

}
