package com.caring.sass.tenant.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.dto.IMConfigDto;
import com.caring.sass.tenant.dao.AppConfigMapper;
import com.caring.sass.tenant.dto.IosConfigVersionDTO;
import com.caring.sass.tenant.entity.AppConfig;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.CreateFollowerPermanentQrCode;
import com.caring.sass.wx.dto.config.QrCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 项目APP配置
 * </p>
 *
 * @author leizhi
 * @date 2020-09-21
 */
@Slf4j
@Service

public class AppConfigServiceImpl extends SuperServiceImpl<AppConfigMapper, AppConfig> implements AppConfigService {

    @Order(6)
    @Lazy
    @Autowired
    private TenantService tenantService;

    @Autowired
    private ApkBuildService apkBuildService;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    ImApi imApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Deprecated
    @Override
    public boolean updateById(AppConfig model) {
        String code = BaseContextHandler.getTenant();
        // 打包完成后再更新appVersionName该字段名
        AppConfig tmp = BeanUtil.copyProperties(model, AppConfig.class);
        tmp.setAppVersionName(null);
        redisAppointmentDoctorScope(code, model.getAppointmentDoctorScope());
        baseMapper.updateById(tmp);

        updateRedisLaunchImage(code, model.getAppLaunchImage());
        return true;
    }

    /**
     * 更新redis中的ap启动图
     * @param tenantCode
     * @param launchImage
     */
    public void updateRedisLaunchImage(String tenantCode, String launchImage) {

        if (StrUtil.isNotEmpty(launchImage)) {
            BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_APP_CONFIG_LAUNCH_IMAGE);
            boundHashOps.put(tenantCode, launchImage);
        }
    }

    /**
     * 从redis中获取app的启动图
     * @param tenantCode
     * @return
     */
    @Override
    public String annoRedisAppLaunchImage(String tenantCode) {

        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_APP_CONFIG_LAUNCH_IMAGE);
        Object o = boundHashOps.get(tenantCode);
        if (Objects.isNull(o)) {
            BaseContextHandler.setTenant(tenantCode);
            AppConfig appConfig = baseMapper.selectOne(Wraps.<AppConfig>lbQ().select(SuperEntity::getId, AppConfig::getAppLaunchImage).last(" limit 0,1 "));
            @Length(max = 500, message = "启动页图片长度不能超过500") String launchImage = appConfig.getAppLaunchImage();
            if (StrUtil.isNotEmpty(launchImage)) {
                boundHashOps.put(tenantCode, launchImage);
                return launchImage;
            }
        } else {
            return o.toString();
        }
        return null;
    }

    /**
     * 获取 预约医生范围开关
     * @param tenantCode
     * @return
     */
    @Override
    public String getAppointmentDoctorScope(String tenantCode) {
        Object o = redisTemplate.opsForHash().get(SaasRedisBusinessKey.getTenantAppointmentDoctorScope(), tenantCode);
        if (Objects.isNull(o)) {
            AppConfig config = baseMapper.selectOne(Wraps.<AppConfig>lbQ()
                    .select(SuperEntity::getId, AppConfig::getAppointmentDoctorScope)
                    .last(" limit 0,1 "));
            if (config == null) {
                return null;
            }
            String doctorScope = config.getAppointmentDoctorScope();
            redisAppointmentDoctorScope(tenantCode, doctorScope);
            return doctorScope;
        } else {
            return o.toString();
        }
    }

    /**
     * 设置 预约医生范围 开关
     * @param tenantCode
     * @param appointmentDoctorScope
     * @return
     */
    @Override
    public String setAppointmentDoctorScope(String tenantCode, String appointmentDoctorScope) {

        AppConfig config = baseMapper.selectOne(Wraps.<AppConfig>lbQ()
                .select(SuperEntity::getId, AppConfig::getAppointmentDoctorScope)
                .last(" limit 0,1 "));
        if (config == null) {
            config = initConfig();
        }
        if (appointmentDoctorScope.equals(config.getAppointmentDoctorScope())){
            return appointmentDoctorScope;
        }
        config.setAppointmentDoctorScope(appointmentDoctorScope);
        int update = baseMapper.updateById(config);
        if (update > 0) {
            redisAppointmentDoctorScope(tenantCode, appointmentDoctorScope);
        }
        return appointmentDoctorScope;
    }

    /**
     * 存储IOS的版本
     * @param iosConfigVersionDTO
     */
    @Override
    public void generateIosApk(IosConfigVersionDTO iosConfigVersionDTO) {
        String string = JSONObject.toJSONString(iosConfigVersionDTO);
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantIosVersion(), string);
    }

    @Override
    public IosConfigVersionDTO getGenerateIosApk() {
        Object o = redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantIosVersion());
        if (o != null) {
            return JSONObject.parseObject(o.toString(), IosConfigVersionDTO.class);
        }
        return null;
    }

    @Override
    public void initRedisData() {
        // 查询项目的 预约医生的范围
        // 同步到redis中去。
        Boolean hasKey = redisTemplate.hasKey(SaasRedisBusinessKey.getTenantAppointmentDoctorScope());
        if (Objects.nonNull(hasKey) && hasKey) {
            return;
        }
        // 查询项目的 预约医生范围
        List<AppConfig> appConfigs = baseMapper.selectAppointmentDoctorScope();
        for (AppConfig appConfig : appConfigs) {
            redisAppointmentDoctorScope(appConfig.getTenantCode(), appConfig.getAppointmentDoctorScope());
        }

    }

    @Override
    public AppConfig initConfig() {
        AppConfig appConfig = new AppConfig();
        baseMapper.insert(appConfig);
        return appConfig;
    }

    /**
     * 将 预约的范围 存储到redis中
     * @param tenantCode
     * @param appointmentDoctorScope
     */
    public void redisAppointmentDoctorScope(String tenantCode, String appointmentDoctorScope) {
        try {
            redisTemplate.opsForHash().put(SaasRedisBusinessKey.getTenantAppointmentDoctorScope(), tenantCode, appointmentDoctorScope);
        } catch (Exception e) {
            log.info("缓存到redis失败");
        }
    }

    @Deprecated
    @Override
    public boolean save(AppConfig model) {
        String code = BaseContextHandler.getTenant();
        if (model.getTenantId() == null) {
            Tenant tenant = tenantService.getByCode(code);
            model.setTenantId(tenant.getId());
        }
        redisAppointmentDoctorScope(code, model.getAppointmentDoctorScope());
        baseMapper.insert(model);
        updateRedisLaunchImage(code, model.getAppLaunchImage());
        return true;
    }

    /**
     * 保存安卓的配置信息。并发起打包
     * @param appConfig
     */
    @Override
    public void generateApk(AppConfig appConfig) {
        String code = BaseContextHandler.getTenant();
        Tenant t = tenantService.getByCode(code);
        if (appConfig != null) {
            String appVersionName = appConfig.getAppVersionName();
            appConfig.setAppVersionName(null);
            baseMapper.updateById(appConfig);
            redisAppointmentDoctorScope(code, appConfig.getAppointmentDoctorScope());
            updateRedisLaunchImage(code, appConfig.getAppLaunchImage());
            appConfig.setAppVersionName(appVersionName);
            apkBuildService.doBuild(t.getDomainName(), t.getCode(), appConfig, null, Objects.nonNull(t.getWxBindTime()));
        }
    }

    /**
     * 保存UNIapp安卓的配置信息。并发起打包
     * @param appConfig
     */
    @Override
    public void generateUniApk(AppConfig appConfig) {
        String code = BaseContextHandler.getTenant();
        Tenant t = tenantService.getByCode(code);
        if (appConfig != null) {
            String uniAppVersionName = appConfig.getUniAppVersionName();
            appConfig.setUniAppVersionName(null);
            baseMapper.updateById(appConfig);
            redisAppointmentDoctorScope(code, appConfig.getAppointmentDoctorScope());
            updateRedisLaunchImage(code, appConfig.getAppLaunchImage());
            appConfig.setUniAppVersionName(uniAppVersionName);
            apkBuildService.doUniBuild(t.getDomainName(), t.getCode(), appConfig, null, Objects.nonNull(t.getWxBindTime()));
        }
    }


    @Override
    public AppConfig getByTenantId(Long tenantId) {
        LbqWrapper<AppConfig> appConfigLbqWrapper = new LbqWrapper<>();
        appConfigLbqWrapper.eq(AppConfig::getTenantId, tenantId);
        return baseMapper.selectOne(appConfigLbqWrapper);
    }

    @Override
    public List<AppConfig> findByTenantCode(List<String> codes) {
        List<AppConfig> r = new ArrayList<>();
        if (CollUtil.isEmpty(codes)) {
            return r;
        }
        return baseMapper.listAllConfigByTenantCode(codes);
    }


    @Override
    public HashMap<String, Object> getConfigInfo() {

        String tenantCode = BaseContextHandler.getTenant();

        HashMap<String, Object> maps = new HashMap();

        Tenant tenant = tenantService.getByCode(tenantCode);

        LbqWrapper<AppConfig> configLbqWrapper = new LbqWrapper<>();
        AppConfig config = baseMapper.selectOne(configLbqWrapper);
        String doctorLoginCode = config.getDoctorQrUrl();
        JSONObject image = new JSONObject();
        if (StringUtils.isEmptyAfterTrim(doctorLoginCode)) {
            CreateFollowerPermanentQrCode form = new CreateFollowerPermanentQrCode();
            form.setWxAppId(tenant.getWxAppId());
            form.setParams("doctor_login_");
            R<QrCodeDto> permanentQrCode = weiXinApi.createFollowerPermanentQrCode(form);
            if (permanentQrCode.getIsSuccess() && permanentQrCode.getData() != null) {
                QrCodeDto data = permanentQrCode.getData();
                String url = data.getUrl();
                image.put("height", ImageUtils.getImgHeight(url));
                image.put("width", ImageUtils.getImgHeight(url));
                image.put("path", url);
                config.setDoctorQrUrl(url);
                baseMapper.updateById(config);
            } else {
                log.error("获取医生登录码失败");
            }
        } else {
            image.put("height", ImageUtils.getImgHeight(doctorLoginCode));
            image.put("width", ImageUtils.getImgWidth(doctorLoginCode));
            image.put("path", doctorLoginCode);
        }

        R<IMConfigDto> imConfigDtoR = imApi.getIMConfig();
        if (imConfigDtoR.getIsSuccess() && imConfigDtoR.getData() != null) {
            IMConfigDto imConfigDto = imConfigDtoR.getData();
            maps.put("ImKey", imConfigDto.getOrgName() + "#" + imConfigDto.getAppName());
            maps.put("ImPassword", imConfigDto.getUserPassword());
        }

        maps.put("doctorLoginCode", image);
        maps.put("applicationName", config.getAppApplicationName());
        LocalDateTime wxBindTime = tenant.getWxBindTime();
        String wxJsSecurityUrl;
        if (Objects.nonNull(wxBindTime)) {
            wxJsSecurityUrl = ApplicationDomainUtil.wxPatientBaseDomain(tenantCode, true);
        } else {
            wxJsSecurityUrl = ApplicationDomainUtil.wxPatientBaseDomain(tenantCode, false);
        }
        maps.put("appAgreement",  wxJsSecurityUrl + "/mobile/wxpand?tenantCode=" + tenantCode + "&ident=0");
        maps.put("frequentlyAskedQuestion", wxJsSecurityUrl + "/mobile/wxpand?tenantCode=" + tenantCode + "&ident=1");
        maps.put("questionFeedback", wxJsSecurityUrl + "/mobile/wxpand?tenantCode=" + tenantCode + "&ident=2");
        maps.put("aboutUs",  wxJsSecurityUrl + "/mobile/wxpand?tenantCode=" + tenantCode + "&ident=3");
        log.info(maps.toString());
        return maps;
    }

    @Override
    public Boolean copyAppConfig(String fromTenantCode, String toTenantCode) {
        String currentTenant = BaseContextHandler.getTenant();

        BaseContextHandler.setTenant(fromTenantCode);
        AppConfig fromAppConfig = baseMapper.selectOne(Wrappers.emptyWrapper());

        BaseContextHandler.setTenant(toTenantCode);
        Tenant tenant = tenantService.getByCode(toTenantCode);
        Long tenantId = Objects.nonNull(tenant) ? tenant.getId() : null;
        // 修改数据
        fromAppConfig.setId(null);
        fromAppConfig.setTenantId(tenantId);
        fromAppConfig.setUploadUrl(null);
        fromAppConfig.setApkUrl(null);
        fromAppConfig.setAppIcon(null);
        fromAppConfig.setRenewRecord(null);
        fromAppConfig.setJpushAppkey(null);
        fromAppConfig.setJpushChannel(null);
        fromAppConfig.setJpushMasterSecret(null);
        fromAppConfig.setAppApplicationId(null);
        fromAppConfig.setMiAppId(null);
        fromAppConfig.setMiAppKey(null);
        fromAppConfig.setAppHuaweiAppid(null);
        fromAppConfig.setUniAppVersionName(ApplicationProperties.getApkVersion());
        fromAppConfig.setAppVersionName(ApplicationProperties.getApkVersion());
        fromAppConfig.setUniPackageStatus(null);
        fromAppConfig.setUniApkDownloadUrl(null);
        fromAppConfig.setUniApkUrl(null);
        fromAppConfig.setDcloudAppid(null);
        fromAppConfig.setDcloudAppkey(null);
        fromAppConfig.setUniRenewRecord(null);
        fromAppConfig.setPackageStatus(null);
        fromAppConfig.setAppApplicationId(null);
        fromAppConfig.setCreateUser(null);
        fromAppConfig.setCreateTime(null);
        fromAppConfig.setUpdateTime(null);
        fromAppConfig.setUpdateUser(null);
        baseMapper.insert(fromAppConfig);

        BaseContextHandler.setTenant(currentTenant);
        return true;
    }
}
