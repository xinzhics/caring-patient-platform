package com.caring.sass.wx.controller.menu;

import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.wx.entity.menu.SysMenu;
import com.caring.sass.wx.dto.menu.SysMenuSaveDTO;
import com.caring.sass.wx.dto.menu.SysMenuUpdateDTO;
import com.caring.sass.wx.dto.menu.SysMenuPageDTO;
import com.caring.sass.wx.service.menu.SysMenuService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 系统定义的微信菜单
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/sysMenu")
@Api(value = "SysMenu", tags = "系统定义的微信菜单")
//@PreAuth(replace = "sysMenu:")
public class SysMenuController extends SuperController<SysMenuService, Long, SysMenu, SysMenuPageDTO, SysMenuSaveDTO, SysMenuUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SysMenu> sysMenuList = list.stream().map((map) -> {
            SysMenu sysMenu = SysMenu.builder().build();
            //TODO 请在这里完成转换
            return sysMenu;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(sysMenuList));
    }

    @ApiOperation("获取系统菜单")
    @GetMapping("/getMenu/{buttonType}")
    public R<List<SysMenu>> getMenu(@PathVariable("buttonType") Integer buttonType) {
        LbqWrapper<SysMenu> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(SysMenu::getButtonType, buttonType);
        List<SysMenu> list = baseService.list(lbqWrapper);
        return R.success(list);
    }

}
