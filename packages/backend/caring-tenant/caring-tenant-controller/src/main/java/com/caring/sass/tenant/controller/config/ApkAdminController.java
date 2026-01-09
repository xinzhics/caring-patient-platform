package com.caring.sass.tenant.controller.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dto.AppConfigSaveDTO;
import com.caring.sass.tenant.dto.AppConfigUpdateDTO;
import com.caring.sass.tenant.dto.IosConfigVersionDTO;
import com.caring.sass.tenant.entity.AppConfig;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.tenant.service.sys.DictItemService;
import com.caring.sass.tenant.utils.QrCodeUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.CreateFollowerPermanentQrCode;
import com.caring.sass.wx.dto.config.QrCodeDto;
import com.caring.sass.wx.entity.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("/apkConfig")
@Api(value = "ApkConfig", tags = "项目apk配置管理")
public class ApkAdminController {

    private final AppConfigService appConfigService;

    private final TenantService tenantService;

    private final DictionaryItemService dictionaryItemService;

    private final WeiXinApi weiXinApi;

    public ApkAdminController(AppConfigService appConfigService,
                              TenantService tenantService,
                              DictionaryItemService dictionaryItemService,
                              WeiXinApi weiXinApi) {
        this.appConfigService = appConfigService;
        this.tenantService = tenantService;
        this.weiXinApi = weiXinApi;
        this.dictionaryItemService = dictionaryItemService;
    }

    /**
     * 查询项目apk配置基础信息
     */
    @ApiOperation("查询项目apk配置基础信息")
    @GetMapping("queryApkConfig/{code}")
    public R<AppConfig> queryApkConfig(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        Tenant tenant = tenantService.getByCode(code);
        // 配置app时，检查医生的登录二维码是否生成
        if (StringUtils.isEmpty(tenant.getDoctorQrUrl())) {
            tenantService.clearAndCreatedDoctorQrCode(tenant);
        }

        AppConfig appConfig = appConfigService.getOne(Wraps.lbQ());
        boolean init = false;
        if (appConfig == null) {
            appConfig = appConfigService.initConfig();
            init = true;
        }
        if (appConfig.getTenantId() == null) {
            appConfig.setTenantId(tenant.getId());
        }
        // 设置默认值
        if (StrUtil.isBlank(appConfig.getAppLaunchImage())) {
            appConfig.setAppLaunchImage(ApplicationProperties.getApkLaunchImage());
        }
        if (StrUtil.isBlank(appConfig.getAppVersionName())) {
            appConfig.setAppVersionName(ApplicationProperties.getApkVersion());
        }
        if (StrUtil.isBlank(appConfig.getAppAgreement())) {
            appConfig.setAppAgreement(ApplicationProperties.APK_AGREEMENT);
        }
        if (Objects.isNull(appConfig.getAppVersionCode())) {
            appConfig.setAppVersionCode(1);
        }

        if (StrUtil.isBlank(appConfig.getAppApplicationName())) {
            appConfig.setAppApplicationName(tenant.getName());
        }
        if (StrUtil.isBlank(appConfig.getAppIcon())) {
            appConfig.setAppIcon(tenant.getLogo());
        }
        if (StrUtil.isBlank(appConfig.getAppApplicationId())) {
            appConfig.setAppApplicationId("com.caringsaas." + tenant.getDomainName());
        }
        if (init) {
            appConfigService.updateById(appConfig);
        }
        return R.success(appConfig);
    }

    @ApiOperation("保存项目apk配置基础信息")
    @PostMapping("saveApkConfig/{code}")
    public R<AppConfig> saveApkConfig(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                      @RequestBody @Validated AppConfigSaveDTO appConfigSaveDTO) {
        BaseContextHandler.setTenant(code);
        AppConfig appConfig = BeanUtil.toBean(appConfigSaveDTO, AppConfig.class);
        List<AppConfig> configs = appConfigService.list();
        if (CollUtil.isNotEmpty(configs)) {
            return R.fail("项目app配置已存在");
        }
        appConfigService.save(appConfig);
        return R.success(appConfig);
    }

    @ApiOperation("修改项目apk配置基础信息")
    @PostMapping("updateApkConfig/{code}")
    public R<AppConfig> updateApkConfig(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                        @RequestBody @Validated AppConfigUpdateDTO appConfigUpdateDTO) {
        BaseContextHandler.setTenant(code);
        if (Objects.isNull(appConfigUpdateDTO.getAppVersionCode())) {
            appConfigUpdateDTO.setAppVersionCode(1);
        }
        AppConfig appConfig = BeanUtil.toBean(appConfigUpdateDTO, AppConfig.class);
        appConfigService.updateById(appConfig);
        return R.success(appConfig);
    }

    @ApiOperation("老安卓项目打包")
    @PostMapping("generateApk/{code}")
    public R<Boolean> generateApk(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code, @RequestBody @Validated AppConfigUpdateDTO appConfigUpdateDTO) {
        BaseContextHandler.setTenant(code);
        appConfigUpdateDTO.setUniRenewRecord(null);
        appConfigUpdateDTO.setUniAppVersionName(null);
        AppConfig config = new AppConfig();
        BeanUtils.copyProperties(appConfigUpdateDTO, config);
        config.setId(appConfigUpdateDTO.getId());
        appConfigService.generateApk(config);
        return R.success();
    }


    @ApiOperation("UNIApp项目打包")
    @PostMapping("generateUniApk/{code}")
    public R<Boolean> generateUniApk(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code, @RequestBody @Validated AppConfigUpdateDTO appConfigUpdateDTO) {
        BaseContextHandler.setTenant(code);
        appConfigUpdateDTO.setAppVersionName(null);
        appConfigUpdateDTO.setRenewRecord(null);
        AppConfig config = new AppConfig();
        BeanUtils.copyProperties(appConfigUpdateDTO, config);
        config.setId(appConfigUpdateDTO.getId());
        appConfigService.generateUniApk(config);
        return R.success();
    }


    @ApiOperation("更新IOS版本")
    @PostMapping("generateIosApk")
    public R<Boolean> generateIosApk(@RequestBody IosConfigVersionDTO iosConfigVersionDTO) {
        appConfigService.generateIosApk(iosConfigVersionDTO);
        return R.success();
    }

    @ApiOperation("查询IOS版本")
    @GetMapping("generateIosApk")
    public R<IosConfigVersionDTO> generateIosApk() {
        IosConfigVersionDTO iosConfigVersionDTO = appConfigService.getGenerateIosApk();
        return R.success(iosConfigVersionDTO);
    }
}
