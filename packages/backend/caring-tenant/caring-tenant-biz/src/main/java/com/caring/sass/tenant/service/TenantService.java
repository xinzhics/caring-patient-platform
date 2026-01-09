package com.caring.sass.tenant.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.dto.*;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.wx.entity.guide.RegGuide;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 企业
 * </p>
 *
 * @author caring
 * @date 2019-10-24
 */
//public interface TenantService extends SuperCacheService<Tenant> {
public interface TenantService extends SuperService<Tenant> {
    /**
     * 检测 租户编码是否存在
     *
     * @param tenantCode
     * @return
     */
    boolean check(String tenantCode);

    Tenant getByDomain(String domain);

    /**
     * 保存
     *
     * @param data
     * @return
     */
    Tenant save(TenantSaveDTO data);

    /**
     * 初始化字典
     */
    void initTenantDict();

    void removeRedisByCode(String code);

    /**
     * 根据编码获取
     *
     * @param tenant
     * @return
     */
    Tenant getByCode(String tenant);

    /**
     * 删除租户数据
     *
     * @param ids
     * @return
     */
    Boolean delete(List<Long> ids);

    /**
     * 服务启动时，
     * 查询之前没有删除完毕的 项目。 继续执行删除
     */
    void queryDeletingTenant();

    /**
     * 通知所有服务链接数据源
     *
     * @param tenantConnect 链接信息
     * @return
     */
    Boolean connect(TenantConnectDTO tenantConnect);


    /**
     * 清除医生 医助激活码。重新生产
     * @param tenant
     */
    void clearAndCreatedDoctorQrCode(Tenant tenant);

    /**
     * @return boolean
     * @Author yangShuai
     * @Description 检验域名是否被使用
     * @Date 2020/9/28 9:28
     */
    boolean checkDomain(String domain);

    /**
     * @return com.caring.sass.tenant.entity.Tenant
     * @Author yangShuai
     * @Description 使用微信AppId查看项目信息
     * @Date 2020/9/22 11:12
     */
    Tenant getByWxAppId(String wxAppId);

    /**
     * @return void
     * @Author yangShuai
     * @Description 更新 项目的微信公众号校验状态
     * @Date 2020/9/22 11:24
     */
    void updateWxStatus(Tenant tenant);

    /**
     * @return void
     * /**
     * @Author yangShuai
     * @Description 创建一个机构
     * @Date 2020/9/28 10:23
     **/
    Tenant createTenantAndInit(TenantSaveDTO tenantSaveDTO);


    /**
     * 复制项目
     */
    Tenant copyTenant(TenantCopyDTO tenantCopyDTO);


    TenantDetailDTO getTenantResultDetail(Long tenantId);

    /**
     * 项目统计
     */
    Map<String, Integer> tenantStatistic();

    /**
     * 检查公众号被哪些项目配置了
     * @param wxAppId
     * @param tenantCode
     * @return
     */
    List<Tenant> checkWxAppIdStatus(String wxAppId, String tenantCode);


    void clearWxAppId(String wxAppId);


    void initTenantAssistantQrCode();


//    void initTenantDoctorQrCode();


    /**
     * 查询租户的 AI名称
     */
    TenantAiInfoDTO queryAiInfo(String tenant);


    /**
     * 更新AI名称头像信息，并清除redis缓存
     * @param aiInfoDTO
     */
    void updateAiInfo(TenantAiInfoDTO aiInfoDTO);

    /**
     * @title 创建一个项目端可以免登陆的url
     * @author 杨帅
     * @updateTime 2023/4/12 16:07
     * @throws
     */
    String createTenantLoginUrl(String tenantCode);

    /**
     * 【项目配置】 查询基本信息
     * @param tenantCode
     */
    TenantBaseInfo queryTenantBaseInfo(RegGuide regGuide, String tenantCode);

}
