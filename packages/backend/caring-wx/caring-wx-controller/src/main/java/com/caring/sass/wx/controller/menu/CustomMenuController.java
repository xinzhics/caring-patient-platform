package com.caring.sass.wx.controller.menu;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.wx.dto.enums.MenuActionType;
import com.caring.sass.wx.entity.menu.CustomMenu;
import com.caring.sass.wx.dto.menu.CustomMenuSaveDTO;
import com.caring.sass.wx.dto.menu.CustomMenuUpdateDTO;
import com.caring.sass.wx.dto.menu.CustomMenuPageDTO;
import com.caring.sass.wx.entity.menu.SysMenu;
import com.caring.sass.wx.service.menu.CustomMenuService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;


/**
 * <p>
 * 前端控制器
 * 项目自定义微信菜单
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/customMenu")
@Api(value = "CustomMenu", tags = "项目自定义微信菜单")
//@PreAuth(replace = "customMenu:")
public class CustomMenuController extends SuperController<CustomMenuService, Long, CustomMenu, CustomMenuPageDTO, CustomMenuSaveDTO, CustomMenuUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<CustomMenu> customMenuList = list.stream().map((map) -> {
            CustomMenu customMenu = CustomMenu.builder().build();
            //TODO 请在这里完成转换
            return customMenu;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(customMenuList));
    }


    @ApiOperation("从一个项目中复制自定义按钮到另一个项目中")
    @GetMapping({"/copyCustomButton"})
    public R<Boolean> copyCustomButton(@RequestParam("oldProjectId") String oldProjectId, @RequestParam("newProjectId") String newProjectId) {

        return R.success();
    }

    @ApiOperation("新增一个自定义菜单")
    @PostMapping("saveCustomMenu/{tenantCode}")
    public R<CustomMenu> saveCustomMenu(@PathVariable("tenantCode") String tenantCode, @RequestBody @Validated CustomMenuSaveDTO customMenuSaveDTO) {
        BaseContextHandler.setTenant(tenantCode);
        if (MenuActionType.miniprogram == customMenuSaveDTO.getType()) {
            customMenuSaveDTO.setUrl("http://mp.weixin.qq.com");
        }
        return super.save(customMenuSaveDTO);
    }

    @ApiOperation("修改一个自定义菜单")
    @PutMapping("putCustomMenu/{tenantCode}")
    public R<CustomMenu> putCustomMenu(@PathVariable("tenantCode") String tenantCode,@RequestBody @Validated CustomMenuUpdateDTO customMenuUpdateDTO) {
        BaseContextHandler.setTenant(tenantCode);
        if (MenuActionType.miniprogram == customMenuUpdateDTO.getType()) {
            customMenuUpdateDTO.setUrl("http://mp.weixin.qq.com");
        }
        return super.update(customMenuUpdateDTO);
    }

    @ApiOperation("获取自定义菜单")
    @GetMapping("/getMenu/{tenantCode}")
    public R<List<CustomMenu>> getMenu(@PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        List<CustomMenu> customMenuList = baseService.list();
        return R.success(customMenuList);
    }

    @ApiOperation("删除一个自定义菜单")
    @DeleteMapping("deleteCustomMenu/{tenantCode}/{id}")
    public R<Boolean> deleteCustomMenu(@PathVariable("tenantCode") String tenantCode, @PathVariable("id") Long id) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.removeById(id);
        return R.success(true);
    }

}
