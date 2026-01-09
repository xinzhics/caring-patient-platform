package com.caring.sass.authority.controller.auth;

import com.caring.sass.authority.dto.auth.InitTenantAuth;
import com.caring.sass.authority.dto.auth.MenuSaveDTO;
import com.caring.sass.authority.dto.auth.MenuUpdateDTO;
import com.caring.sass.authority.entity.auth.Menu;
import com.caring.sass.authority.service.auth.MenuService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperCacheController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.security.annotation.PreAuth;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.TreeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 菜单
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/menu")
@Api(value = "Menu", tags = "菜单")
//@PreAuth(replace = "menu:")
public class MenuController extends SuperCacheController<MenuService, Long, Menu, Menu, MenuSaveDTO, MenuUpdateDTO> {


    @Autowired
    TenantApi tenantApi;

    @Override
    public R<Menu> handlerSave(MenuSaveDTO menuSaveDTO) {
        Menu menu = BeanPlusUtil.toBean(menuSaveDTO, Menu.class);
        baseService.saveWithCache(menu);
        return success(menu);
    }

    @Override
    public R<Menu> handlerUpdate(MenuUpdateDTO model) {
        Menu menu = BeanPlusUtil.toBean(model, Menu.class);
        baseService.updateWithCache(menu);
        return success(menu);
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        baseService.removeByIdWithCache(ids);
        return success();
    }

    /**
     * 查询系统中所有的的菜单树结构， 不用缓存，因为该接口很少会使用，就算使用，也会管理员维护菜单时使用
     *
     * @return
     */
    @ApiOperation(value = "查询系统所有的菜单", notes = "查询系统所有的菜单")
    @GetMapping("/tree")
    @SysLog("查询系统所有的菜单")
    public R<List<Menu>> allTree() {
        List<Menu> list = baseService.list(Wraps.<Menu>lbQ().orderByAsc(Menu::getSortValue));
        // 约定，个人服务号项目。不需要一下菜单
        //   - 微信推送 -> 素材管理
        //  - 微信推送 -> 推送管理
        //  - AI助手 -> 关注后自动回复
        R<String> accountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        String accountTypeData = accountType.getData();
        Set<String> excludedPaths = new HashSet<>(Arrays.asList(
                // 微信推送
                "/wxGroup".toLowerCase(),
                // 素材管理  推送管理
                "/wxgroup/materiallibrary".toLowerCase(),
                "/wxgroup/pushmanager".toLowerCase(),
                // 关注后自动回复
                "/assistant/automatic".toLowerCase()
        ));
        if (accountTypeData.equals(TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString())) {
            list = list.stream()
                    .filter(menu -> !excludedPaths.contains(menu.getPath().toLowerCase())) // 统一转小写
                    .collect(Collectors.toList());
        }

        List<Menu> menuList = TreeUtil.buildTree(list);

        return success(menuList);
    }


    @ApiOperation("初始化菜单")
    @PostMapping("initTenantAuth")
    public R<Boolean> initTenantMenu(@RequestBody InitTenantAuth initTenantAuth) {

        baseService.initTenantMenu(initTenantAuth);
        return R.success(true);
    }

}
