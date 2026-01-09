package com.caring.sass.tenant.controller;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.entity.TenantConfigurationMark;
import com.caring.sass.tenant.dto.TenantConfigurationMarkSaveDTO;
import com.caring.sass.tenant.dto.TenantConfigurationMarkUpdateDTO;
import com.caring.sass.tenant.dto.TenantConfigurationMarkPageDTO;
import com.caring.sass.tenant.entity.TenantConfigurationSchedule;
import com.caring.sass.tenant.service.TenantConfigurationMarkService;
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
 * 项目配置标记表
 * </p>
 *
 * @author 杨帅
 * @date 2023-07-18
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tenantConfigurationMark")
@Api(value = "TenantConfigurationMark", tags = "项目配置标记表")
@PreAuth(replace = "tenantConfigurationMark:")
public class TenantConfigurationMarkController extends SuperController<TenantConfigurationMarkService, Long, TenantConfigurationMark, TenantConfigurationMarkPageDTO, TenantConfigurationMarkSaveDTO, TenantConfigurationMarkUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TenantConfigurationMark> tenantConfigurationMarkList = list.stream().map((map) -> {
            TenantConfigurationMark tenantConfigurationMark = TenantConfigurationMark.builder().build();
            //TODO 请在这里完成转换
            return tenantConfigurationMark;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(tenantConfigurationMarkList));
    }

    @ApiOperation("查询项目的配置标记")
    @GetMapping("getByTenant")
    public R<TenantConfigurationMark> getByTenant(@RequestParam("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        TenantConfigurationMark mark = baseService.getOne(Wraps.<TenantConfigurationMark>lbQ().last("limit 0,1"));
        if (mark == null) {
            mark = new TenantConfigurationMark();
            TenantConfigurationMark.init(mark);
            baseService.save(mark);
        }
        return R.success(mark);
    }


    @ApiOperation("更新项目配置中的标记")
    @PutMapping("updateByTenant")
    public R<TenantConfigurationMark> getByTenant(@RequestParam("tenantCode") String tenantCode,
                                                  @RequestBody TenantConfigurationMark mark) {
        BaseContextHandler.setTenant(tenantCode);
        TenantConfigurationMark oldMark = baseService.getOne(Wraps.<TenantConfigurationMark>lbQ().last("limit 0,1"));
        if (Objects.nonNull(oldMark)) {
            mark.setId(oldMark.getId());
        }
        if (mark.getId() == null) {
            baseService.save(mark);
        } else {
            baseService.updateById(mark);
        }
        return R.success(mark);
    }



}
