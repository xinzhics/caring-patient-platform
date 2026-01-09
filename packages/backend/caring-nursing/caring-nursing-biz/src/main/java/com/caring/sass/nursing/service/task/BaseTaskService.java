package com.caring.sass.nursing.service.task;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.wx.ConfigAdditionalApi;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 推送公共类
 * @author leizhi
 */
@Component
public class BaseTaskService {

    @Autowired
    ConfigAdditionalApi configAdditionalApi;

    @Autowired
    TenantApi tenantApi;

    /**
     * 获取所有的正常项目
     */
    public List<Tenant> getAllNormalTenant() {
        R<List<Tenant>> listR = tenantApi.getAllNormalTenant();
        if (listR.getIsSuccess() && listR.getData() != null) {
            return listR.getData();
        }
        return new ArrayList<>();
    }
}
