package com.caring.sass.tenant.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.dto.IosConfigVersionDTO;
import com.caring.sass.tenant.entity.AppConfig;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 项目APP配置
 * </p>
 *
 * @author leizhi
 * @date 2020-09-21
 */
public interface AppConfigService extends SuperService<AppConfig> {

    /**
     * 更新uni的Apk打包
     */
    void generateUniApk(AppConfig appConfig);

    AppConfig getByTenantId(Long tenantId);

    /**
     * 获取 预约医生范围开关
     * @param tenantCode
     * @return
     */
    String getAppointmentDoctorScope(String tenantCode);

    /**
     * 初始化redis 中数据
     */
    void initRedisData();


    void generateApk(AppConfig config);

    /**
     * 根据code列表查询app配置信息
     *
     * @param codes 项目code列表
     * @return 项目配置类别
     */
    List<AppConfig> findByTenantCode(List<String> codes);

    HashMap<String, Object> getConfigInfo();

    /**
     * 复制项目APP配置
     *
     * @param fromTenantCode 从该项目编码复制数据
     * @param toTenantCode   复制数据到该项目编码
     * @return
     */
    Boolean copyAppConfig(String fromTenantCode, String toTenantCode);

    /**
     * 设置 预约医生的范围
     * @param tenantCode
     * @param appointmentDoctorScope
     * @return
     */
    String setAppointmentDoctorScope(String tenantCode, String appointmentDoctorScope);


    void generateIosApk(IosConfigVersionDTO iosConfigVersionDTO);

    IosConfigVersionDTO getGenerateIosApk();

    /**
     * 初始化项目app配置
     * @return
     */
    AppConfig initConfig();


    String annoRedisAppLaunchImage(String tenantCode);

}
