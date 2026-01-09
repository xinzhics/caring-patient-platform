package com.caring.sass.tenant.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.tenant.dto.GlobalUserTenantPageDTO;
import com.caring.sass.tenant.dto.GlobalUserTenantSaveDTO;
import com.caring.sass.tenant.dto.GlobalUserTenantUpdateDTO;
import com.caring.sass.tenant.entity.GlobalUserTenant;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.GlobalUserTenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 用户项目管理表
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/globalUserTenant")
@Api(value = "GlobalUserTenant", tags = "用户项目管理表")
public class GlobalUserTenantController extends SuperController<GlobalUserTenantService, Long, GlobalUserTenant, GlobalUserTenantPageDTO, GlobalUserTenantSaveDTO, GlobalUserTenantUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<GlobalUserTenant> globalUserTenantList = list.stream().map((map) -> {
            GlobalUserTenant globalUserTenant = GlobalUserTenant.builder().build();
            //TODO 请在这里完成转换
            return globalUserTenant;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(globalUserTenantList));
    }



    @ApiOperation("授权项目")
    @PostMapping("saveGlobalUserTenant")
    public R<Boolean> saveGlobalUserTenant(@RequestBody @Validated GlobalUserTenantSaveDTO saveDTO) {
        GlobalUserTenant globalUserTenant;
        @NotNull(message = "项目租户ID不能为空") List<Long> tenantIds = saveDTO.getTenantIds();
        List<GlobalUserTenant> globalUserTenants = new ArrayList<>();
        for (Long tenantId : tenantIds) {
            globalUserTenant = new GlobalUserTenant();
            globalUserTenant.setTenantId(tenantId);
            globalUserTenant.setAccountId(saveDTO.getAccountId());
            globalUserTenant.setManagementType(GlobalUserTenant.AUTHORIZED_PROJECTS);
            globalUserTenants.add(globalUserTenant);
        }
        baseService.saveBatch(globalUserTenants);
        return R.success(true);
    }


    @ApiOperation("取消授权")
    @PostMapping("cancelGlobalUserTenant/{id}")
    public R<Boolean> cancelGlobalUserTenant(@PathVariable Long id) {
        baseService.removeById(id);
        return R.success(true);
    }


    @ApiOperation("查看授权管理的项目列表信息和自建项目的列表信息")
    @PostMapping("findGlobalUserTenant")
    public R<IPage<GlobalUserTenant>> saveGlobalUserTenant(@RequestBody @Validated PageParams<GlobalUserTenantPageDTO> pageParams) {

        R<IPage<GlobalUserTenant>> pageR = super.page(pageParams);
        IPage<GlobalUserTenant> pageRData = pageR.getData();
        List<GlobalUserTenant> records = pageRData.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            baseService.setTenantInfo(records);
        }
        return pageR;

    }


    @ApiOperation("查看客户授权管理和自建项目的数量")
    @PostMapping("countGlobalUserTenant")
    public R<JSONObject> countGlobalUserTenant(@RequestParam Long accountId) {
        QueryWrap<GlobalUserTenant> queryWrap = Wraps.<GlobalUserTenant>q()
                .select("management_type", "count(*) countNumber")
                .eq("account_id", accountId)
                .groupBy("management_type");
        List<Map<String, Object>> mapList = baseService.listMaps(queryWrap);
        if (CollUtil.isNotEmpty(mapList)) {
            JSONObject jsonObject = new JSONObject();
            for (Map<String, Object> objectMap : mapList) {
                jsonObject.set(objectMap.get("management_type").toString(), objectMap.get("countNumber").toString());
            }
            return R.success(jsonObject);
        }
        return R.success(new JSONObject());
    }

    @ApiOperation("添加授权时查询的项目列表接口")
    @PostMapping("pageTenant")
    public R<IPage<Tenant>> pageTenant(@RequestBody @Validated PageParams<GlobalUserTenantPageDTO> pageParams) {

        IPage<Tenant> page = pageParams.buildPage();
        GlobalUserTenantPageDTO paramsModel = pageParams.getModel();
        baseService.pageTenant(page, paramsModel);
        return R.success(page);

    }

}
