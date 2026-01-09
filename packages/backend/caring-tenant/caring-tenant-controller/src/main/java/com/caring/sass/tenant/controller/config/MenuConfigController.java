package com.caring.sass.tenant.controller.config;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.tenant.dto.config.WxMenuJsonDTO;
import com.caring.sass.wx.CustomMenuApi;
import com.caring.sass.wx.SysMenuApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.PublishMenuDTO;
import com.caring.sass.wx.dto.config.PublishMenuForm;
import com.caring.sass.wx.dto.menu.CustomMenuSaveDTO;
import com.caring.sass.wx.dto.menu.CustomMenuUpdateDTO;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.menu.CustomMenu;
import com.caring.sass.wx.entity.menu.SysMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

/**
 * @author xinzh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/menuConfig")
@Api(value = "menuConfig", tags = "项目微信菜单配置")
public class MenuConfigController {

    private final SysMenuApi sysMenuApi;

    private final CustomMenuApi customMenuApi;

    private final WeiXinApi weiXinApi;

    public MenuConfigController(SysMenuApi sysMenuApi, CustomMenuApi customMenuApi, WeiXinApi weiXinApi) {
        this.sysMenuApi = sysMenuApi;
        this.customMenuApi = customMenuApi;
        this.weiXinApi = weiXinApi;
    }

    /**
     * 查询系统菜单
     */
    @ApiOperation(value = "查询系统菜单")
    @ApiImplicitParam(value = "buttonType", required = true, defaultValue = "0")
    @GetMapping(value = "listSysMenu")
    public R<List<SysMenu>> listSysMenu(@RequestParam(value = "buttonType", defaultValue = "0") String buttonType) {
        return sysMenuApi.query(new SysMenu().setButtonType(buttonType));
    }

    /**
     * 查询自定义菜单
     */
    @ApiOperation(value = "查询自定义菜单")
    @GetMapping(value = "listCustomMenu/{code}")
    public R<List<CustomMenu>> listCustomMenu(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        return customMenuApi.query(CustomMenu.builder().build());
    }


    /**
     * 新增自定义菜单
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增自定义菜单")
    @PostMapping(value = "/saveCustomMenu/{code}")
    public R<CustomMenu> saveCustomMenu(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                        @RequestBody @Validated CustomMenuSaveDTO saveDTO) {
        BaseContextHandler.setTenant(code);
        return customMenuApi.save(saveDTO);
    }

    /**
     * 修改自定义菜单
     *
     * @param updateDTO 修改参数
     */
    @ApiOperation(value = "修改自定义菜单")
    @PutMapping(value = "/updateCustomMenu/{code}")
    public R<CustomMenu> updateCustomMenu(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                          @RequestBody CustomMenuUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(code);
        return customMenuApi.update(updateDTO);
    }

    /**
     * 删除自定义菜单
     *
     * @param ids 需要删除的主键
     */
    @ApiOperation(value = "删除自定义菜单")
    @DeleteMapping(value = "/{code}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids[]", value = "主键id", dataType = "array", paramType = "query"),
    })
    public R<Boolean> menuDelete(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                 @RequestParam("ids[]") List<Long> ids) {
        BaseContextHandler.setTenant(code);
        return customMenuApi.delete(ids);
    }

    /**
     * 发布微信菜单
     */
    @ApiOperation("发布微信菜单")
    @PostMapping({"/publish/{code}"})
    public R<Boolean> publishMenu(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                  @RequestBody WxMenuJsonDTO menuJson) {
        BaseContextHandler.setTenant(code);
        R<Config> a = weiXinApi.getConfig(new Config());
        if (a.getIsError() || Objects.isNull(a.getData())) {
            return R.fail("获取微信配置信息失败");
        }
        Config c = a.getData();
        weiXinApi.publishMenu2Wx(PublishMenuDTO.builder()
                .appId(c.getAppId())
                .wxMenuJson(menuJson.getWxMenuJson())
                .build());
        return R.success();
    }

    @ApiOperation("获取微信公众平台上定义的菜单")
    @GetMapping({"/queryWxMenu/{code}"})
    public R<String> queryWxMenu(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        R<Config> a = weiXinApi.getConfig(new Config());
        if (a.getIsError() || Objects.isNull(a.getData())) {
            return R.success(null);
        }
        String appId = a.getData().getAppId();
        return weiXinApi.queryWxMenu(appId);
    }

}
