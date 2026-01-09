package com.caring.sass.tenant.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.AppointmentDoctorScope;
import com.caring.sass.common.utils.paramtericText.ParametricTextManager;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.dto.AppConfigPageDTO;
import com.caring.sass.tenant.dto.AppConfigSaveDTO;
import com.caring.sass.tenant.dto.AppConfigUpdateDTO;
import com.caring.sass.tenant.entity.AppConfig;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 项目APP配置
 * </p>
 *
 * @author leizhi
 * @date 2020-09-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/appConfig")
@Api(value = "AppConfig", tags = "项目APP配置")
//@PreAuth(replace = "appConfig:")
public class AppConfigController extends SuperController<AppConfigService, String, AppConfig, AppConfigPageDTO, AppConfigSaveDTO, AppConfigUpdateDTO> {


    @Autowired
    TenantService tenantService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<AppConfig> appConfigList = list.stream().map((map) -> {
            AppConfig appConfig = AppConfig.builder().build();
            //TODO 请在这里完成转换
            return appConfig;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(appConfigList));
    }

    @ApiOperation("根据项目code查看配置的角色名")
    @PostMapping(value = {"/queryUserName/{code}", "/{anno}/queryUserName/{code}"})
    public R<Map<String, String>> saveByTenant(@PathVariable("code") String code) {
        Map<String, String> ret = new HashMap<>();
        BaseContextHandler.setTenant(code);
        AppConfig config = baseService.getOne(Wraps.lbQ());
        ret.put("appUserCall", config.getAppUserCall());
        ret.put("wxUserCall", config.getWxUserCall());
        return success(ret);
    }


    @ApiOperation("通过线程注入租户信息查询")
    @GetMapping("getByTenant")
    public R<AppConfig> get() {
        AppConfig config = baseService.getOne(Wrappers.lambdaQuery());
        ParametricTextManager parametricTextManager = SpringUtils.getBean(ParametricTextManager.class);
        parametricTextManager.init();
        String tenantCode = BaseContextHandler.getTenant();
        Tenant tenant = tenantService.getByCode(tenantCode);
        parametricTextManager.addParameter("project", tenant);
        String appAgreement = parametricTextManager.format(config.getAppAgreement());
        config.setAppAgreement(appAgreement);
        config.setDoctorQrUrl(tenant.getDoctorQrUrl());
        config.setAssistantQrUrl(tenant.getAssistantQrUrl());
        config.setDoctorShareQrUrl(tenant.getDoctorShareQrUrl());
        return R.success(config);
    }


    @ApiOperation("通过域名快速访问项目的app的launch")
    @GetMapping("/anno/appLaunchImage")
    public R<String> annoRedisAppLaunchImage(@RequestParam String domain) {

        Tenant tenant = tenantService.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getDomainName, domain).select(Tenant::getCode, SuperEntity::getId).last(" limit 0,1 "));
        @Length(max = 20, message = "项目编码长度不能超过20") String tenantCode = tenant.getCode();
        String launchImage = baseService.annoRedisAppLaunchImage(tenantCode);
        return R.success(launchImage);
    }

    @ApiOperation("通过线程注入租户信息查询(无需登录)")
    @GetMapping("/anno/getByTenant")
    public R<AppConfig> getByTenant() {
        return get();
    }

    @ApiOperation("通过项目Id获取app配置")
    @GetMapping("getByTenant/{tenantId}")
    public R<AppConfig> get(@PathVariable("tenantId") Long tenantId) {
        LbqWrapper<AppConfig> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(AppConfig::getTenantId, tenantId);
        AppConfig config = baseService.getOne(lbqWrapper);
        return R.success(config);
    }


    @ApiOperation("获取app配置医生二维码")
    @GetMapping("getConfigInfo")
    public R<HashMap<String, Object>> getConfigInfo() {
        HashMap<String, Object> configInfo = baseService.getConfigInfo();
        return R.success(configInfo);
    }

    @ApiOperation("获取预约医生范围")
    @GetMapping("getAppointmentDoctorScope/{tenantCode}")
    public R<String> getAppointmentDoctorScope(@PathVariable("tenantCode") String tenantCode) {
        String doctorScope = baseService.getAppointmentDoctorScope(tenantCode);
        if (StrUtil.isEmpty(doctorScope)) {
            return R.success(AppointmentDoctorScope.MYSELF);
        }
        return R.success(doctorScope);
    }

    @ApiOperation("设置预约医生范围")
    @GetMapping("switchAppointmentDoctorScope/{tenantCode}")
    public R<String> switchAppointmentDoctorScope(@PathVariable("tenantCode") String tenantCode,
                                                  @RequestParam("appointmentDoctorScope") String appointmentDoctorScope) {
        BaseContextHandler.setTenant(tenantCode);
        String doctorScope = baseService.setAppointmentDoctorScope(tenantCode, appointmentDoctorScope);
        return R.success(doctorScope);
    }

    @ApiOperation("预约管理开关：0开启，1关闭")
    @Deprecated
    @GetMapping("switchAppointment/{code}")
    public R<Boolean> switchAppointment(@RequestParam(value = "a") Integer a, @PathVariable String code) {
        boolean legal = Objects.equals(a, 1) || Objects.equals(a, 0);
        if (!legal) {
            return R.fail("参数错误");
        }
        BaseContextHandler.setTenant(code);
        baseService.update(Wraps.<AppConfig>lbU()
                .set(AppConfig::getAppointmentSwitch, a));
        return R.success();
    }

    @ApiOperation("会诊管理开关：0开启，1关闭")
    @Deprecated
    @GetMapping("switchConsultation/{code}")
    public R<Boolean> switchConsultation(@RequestParam(value = "a") Integer a, @PathVariable String code) {
        boolean legal = Objects.equals(a, 1) || Objects.equals(a, 0);
        if (!legal) {
            return R.fail("参数错误");
        }
        BaseContextHandler.setTenant(code);
        baseService.update(Wraps.<AppConfig>lbU()
                .set(AppConfig::getConsultationSwitch, a));
        return R.success();
    }

    @ApiOperation("转诊开关：0开启，1关闭")
    @Deprecated
    @GetMapping("switchReferral/{code}")
    public R<Boolean> switchReferral(@RequestParam(value = "a") Integer a, @PathVariable String code) {
        boolean legal = Objects.equals(a, 1) || Objects.equals(a, 0);
        if (!legal) {
            return R.fail("参数错误");
        }
        BaseContextHandler.setTenant(code);
        baseService.update(Wraps.<AppConfig>lbU()
                .set(AppConfig::getReferralSwitch, a));
        return R.success();
    }


    @ApiOperation("患者管理平台：0开启，1关闭")
    @GetMapping("patientManageSwitch/{code}")
    public R<Boolean> patientManageSwitch(@RequestParam(value = "patientManageSwitch") Integer patientManageSwitch, @PathVariable String code) {
        boolean legal = Objects.equals(patientManageSwitch, 1) || Objects.equals(patientManageSwitch, 0);
        if (!legal) {
            return R.fail("参数错误");
        }
        BaseContextHandler.setTenant(code);
        baseService.update(Wraps.<AppConfig>lbU()
                .set(AppConfig::getPatientManageSwitch, patientManageSwitch));
        return R.success();
    }

    @ApiOperation("修改app的更新日志")
    @PostMapping("updateAppRecord/{code}")
    public R<Boolean> updateAppRecord(@PathVariable String code, @RequestBody AppConfigUpdateDTO appConfigUpdateDTO) {
        BaseContextHandler.setTenant(code);
        Long id = appConfigUpdateDTO.getId();
        String renewRecord = appConfigUpdateDTO.getRenewRecord();
        String uniRenewRecord = appConfigUpdateDTO.getUniRenewRecord();
        if (Objects.isNull(id)) {
            throw new BizException("id不能为空");
        }
        baseService.update(Wraps.<AppConfig>lbU()
                .set(AppConfig::getRenewRecord, renewRecord)
                .set(AppConfig::getUniRenewRecord, uniRenewRecord)
                .eq(SuperEntity::getId, id));
        return R.success();
    }


}
