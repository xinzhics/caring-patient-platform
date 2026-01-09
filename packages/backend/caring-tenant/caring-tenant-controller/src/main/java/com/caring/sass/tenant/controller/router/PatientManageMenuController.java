package com.caring.sass.tenant.controller.router;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dto.router.PatientManageMenuPageDTO;
import com.caring.sass.tenant.dto.router.PatientManageMenuSaveDTO;
import com.caring.sass.tenant.dto.router.PatientManageMenuUpdateDTO;
import com.caring.sass.tenant.entity.router.PatientManageMenu;
import com.caring.sass.tenant.service.router.PatientManageMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 患者管理平台菜单
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientManageMenu")
@Api(value = "PatientManageMenu", tags = "患者管理平台菜单")
public class PatientManageMenuController extends SuperController<PatientManageMenuService, Long, PatientManageMenu, PatientManageMenuPageDTO, PatientManageMenuSaveDTO, PatientManageMenuUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<PatientManageMenu> patientManageMenuList = list.stream().map((map) -> {
            PatientManageMenu patientManageMenu = PatientManageMenu.builder().build();
            //TODO 请在这里完成转换
            return patientManageMenu;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(patientManageMenuList));
    }


    @ApiOperation("初始化当前项目的患者管理平台菜单")
    @GetMapping("init")
    public R<Boolean> init() {
        baseService.init();
        return R.success(true);
    }



    @ApiOperation("查询患者管理平台的菜单")
    @GetMapping("queryList")
    public R<List<PatientManageMenu>> queryList(@RequestParam(required = false) String code) {
        if (StrUtil.isNotEmpty(code)) {
            BaseContextHandler.setTenant(code);
        }
        List<PatientManageMenu> list = baseService.list(Wraps.<PatientManageMenu>lbQ().orderByAsc(PatientManageMenu::getMenuSort));
        if (CollUtil.isEmpty(list)) {
            code = BaseContextHandler.getTenant();
            list = baseService.createMenu(code);
        }
        return R.success(list);

    }

    @ApiOperation("批量更新")
    @PutMapping("updateList")
    public R<List<PatientManageMenu>> updateList(@RequestBody PatientManageMenuUpdateDTO manageMenuUpdateDTO) {

        String tenantCode = manageMenuUpdateDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        List<PatientManageMenu> menuList = manageMenuUpdateDTO.getMenuList();
        for (PatientManageMenu manageMenu : menuList) {
            baseService.updateById(manageMenu);
        }
        return R.success(menuList);
    }



}
