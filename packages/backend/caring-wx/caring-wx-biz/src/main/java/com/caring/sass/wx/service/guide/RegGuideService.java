package com.caring.sass.wx.service.guide;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.entity.guide.RegGuide;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 微信注册引导
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface RegGuideService extends SuperService<RegGuide> {

    /**
     * 复制注册引导
     */
    Boolean copyRegGuide(String fromTenantCode, String toTenantCode);

    List<RegGuide> getOpenUnregisteredReminder();

    String setWxUserDefaultRole(String tenantCode, String userType);

    String getWxUserDefaultRole(String tenantCode);

    /**
     * 往redis 中初始化数据
     */
    void initRedisData();


    /**
     * 创建项目后。初始化引导内容
     */
    void initGuide();


}
