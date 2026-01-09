package com.caring.sass.tenant.controller;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.dto.TenantDayRemindDto;
import com.caring.sass.tenant.entity.TenantOperate;
import com.caring.sass.tenant.dto.TenantOperateSaveDTO;
import com.caring.sass.tenant.dto.TenantOperateUpdateDTO;
import com.caring.sass.tenant.dto.TenantOperatePageDTO;
import com.caring.sass.tenant.service.TenantOperateService;
import java.util.List;
import java.util.Map;
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
 * 项目运营配置
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-10
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tenantOperate")
@Api(value = "TenantOperate", tags = "项目运营配置")
@PreAuth(replace = "tenantOperate:")
public class TenantOperateController extends SuperController<TenantOperateService, Long, TenantOperate, TenantOperatePageDTO, TenantOperateSaveDTO, TenantOperateUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TenantOperate> tenantOperateList = list.stream().map((map) -> {
            TenantOperate tenantOperate = TenantOperate.builder().build();
            //TODO 请在这里完成转换
            return tenantOperate;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(tenantOperateList));
    }


    @Override
    public R<TenantOperate> saveOrUpdate(@RequestBody @Validated TenantOperate tenantOperate) {

        String tenantCode = tenantOperate.getTenantCode();
        if (StrUtil.isEmpty(tenantCode)) {
            throw new BizException("租户不能为空");
        }
        baseService.saveOrUpdate(tenantOperate);
        return R.success(tenantOperate);
    }


    @ApiOperation("超管端查询剩余时长30天的项目名称")
    @GetMapping("adminQuery30DayTenantName")
    public R<List<String>> adminQuery30DayTenantName(@RequestParam Long userId) {

        List<String> tenantName = baseService.adminQuery30DayTenantName(userId);
        return R.success(tenantName);
    }

    @ApiOperation("项目管理员登录提醒")
    @GetMapping("userQuery30DayTenantName")
    public R<TenantDayRemindDto> userQuery30DayTenantName(@RequestParam Long userId) {

        TenantDayRemindDto dayRemindDto = baseService.userQueryTenantDay(userId);
        return R.success(dayRemindDto);
    }


    @ApiOperation("医助登陆后的提醒")
    @GetMapping("nursingQueryTenantDay")
    public R<Integer> nursingQueryTenantDay(@RequestParam Long userId) {

        Integer dayRemindDto = baseService.nursingQueryTenantDay(userId);
        return R.success(dayRemindDto);
    }

    @ApiOperation("医生登陆后的提醒")
    @GetMapping("doctorQueryTenantDay")
    public R<Integer> doctorQueryTenantDay(@RequestParam Long userId) {

        Integer dayRemindDto = baseService.doctorQueryTenantDay(userId);
        return R.success(dayRemindDto);
    }

    @ApiOperation("创建机构时数量限制判断 0 可以创建 -1不可创建")
    @GetMapping("createOrganNumberCheck")
    public R<Integer> createOrganNumberCheck() {

        Integer dayRemindDto = baseService.createOrganNumberCheck();
        return R.success(dayRemindDto);
    }

    @ApiOperation("创建医生时数量限制判断  0 可以创建 -1不可创建")
    @GetMapping("createDoctorNumberCheck")
    public R<Integer> createDoctorNumberCheck() {

        Integer dayRemindDto = baseService.createDoctorNumberCheck();
        return R.success(dayRemindDto);
    }

    @ApiOperation("创建医助时数量限制判断  0 可以创建 -1不可创建")
    @GetMapping("/anno/createNursingNumberCheck")
    public R<Integer> createNursingNumberCheck() {

        Integer dayRemindDto = baseService.createNursingNumberCheck();
        return R.success(dayRemindDto);
    }


    @ApiOperation("查询项目的运营配置")
    @GetMapping("getTenantOperate")
    public R<TenantOperate>  getTenantOperate(@RequestParam("tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        TenantOperate tenantOperate = baseService.getOne(Wraps.<TenantOperate>lbQ()
                .eq(TenantOperate::getTenantCode, tenantCode)
                .last("limit 0,1"));
        return R.success(tenantOperate);

    }

}
