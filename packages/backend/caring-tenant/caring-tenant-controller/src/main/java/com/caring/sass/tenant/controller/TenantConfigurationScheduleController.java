package com.caring.sass.tenant.controller;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.entity.TenantConfigurationMark;
import com.caring.sass.tenant.entity.TenantConfigurationSchedule;
import com.caring.sass.tenant.dto.TenantConfigurationScheduleSaveDTO;
import com.caring.sass.tenant.dto.TenantConfigurationScheduleUpdateDTO;
import com.caring.sass.tenant.dto.TenantConfigurationSchedulePageDTO;
import com.caring.sass.tenant.service.TenantConfigurationScheduleService;
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
 * 项目配置进度表
 * </p>
 *
 * @author 杨帅
 * @date 2023-07-18
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tenantConfigurationSchedule")
@Api(value = "TenantConfigurationSchedule", tags = "项目配置进度表")
@PreAuth(replace = "tenantConfigurationSchedule:")
public class TenantConfigurationScheduleController extends SuperController<TenantConfigurationScheduleService, Long, TenantConfigurationSchedule, TenantConfigurationSchedulePageDTO, TenantConfigurationScheduleSaveDTO, TenantConfigurationScheduleUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TenantConfigurationSchedule> tenantConfigurationScheduleList = list.stream().map((map) -> {
            TenantConfigurationSchedule tenantConfigurationSchedule = TenantConfigurationSchedule.builder().build();
            //TODO 请在这里完成转换
            return tenantConfigurationSchedule;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(tenantConfigurationScheduleList));
    }

    @ApiOperation("初始化项目配置进度和标记")
    @GetMapping("anno/onLinkInit")
    public R<Void> onLinkInit() {
        baseService.onLinkInit();
        return R.success(null);
    }

    @ApiOperation("查询项目的配置进度")
    @GetMapping("getByTenant")
    public R<TenantConfigurationSchedule> getByTenant(@RequestParam("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        TenantConfigurationSchedule schedule = baseService.getOne(Wraps.<TenantConfigurationSchedule>lbQ().last("limit 0,1"));
        if (schedule == null) {
            schedule = new TenantConfigurationSchedule();
            TenantConfigurationSchedule.init(schedule);
            baseService.save(schedule);
        }
        return R.success(schedule);
    }


    @ApiOperation("更新项目配置进度")
    @PutMapping("updateByTenant")
    public R<TenantConfigurationSchedule> getByTenant(@RequestParam("tenantCode") String tenantCode,
                                                  @RequestBody TenantConfigurationSchedule schedule) {
        BaseContextHandler.setTenant(tenantCode);
        TenantConfigurationSchedule oldSchedule = baseService.getOne(Wraps.<TenantConfigurationSchedule>lbQ().last("limit 0,1"));
        if (Objects.nonNull(oldSchedule)) {
            schedule.setId(oldSchedule.getId());
        }
        if (schedule.getId() == null) {
            baseService.save(schedule);
        } else {
            baseService.updateById(schedule);
        }
        return R.success(schedule);
    }


}

