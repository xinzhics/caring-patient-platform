package com.caring.sass.tenant.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantAiInfoDTO;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TenantApiFallback implements TenantApi {
    @Override
    public R<Tenant> getByCode(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<List<Tenant>> queryTenantNameByCodes(Set<String> codes) {
        return R.timeout();
    }

    @Override
    public R<Tenant> getTenantByWxAppId(String wxAppId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> updateWxStatus(Tenant tenant) {
        return R.timeout();
    }

    @Override
    public R<List<Tenant>> checkWxAppIdStatus(String tenantCode, String wxAppId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> clearWxAppId(String wxAppId) {
        return R.timeout();
    }

    @Override
    public R<List<Tenant>> getAllNormalTenant() {
        return R.timeout();
    }

    @Override
    public R<List<Object>> getAllTenantCode() {
        return R.timeout();
    }

    @Override
    public R<List<Tenant>> queryTenantList(TenantOfficialAccountType officialAccountType) {
        return R.timeout();
    }

    @Override
    public R<Tenant> getAppIdAndCodeByDomain(String domain) {
        return R.timeout();
    }

    @Override
    public R<Boolean> updateDoctorQrCode(String code, String dictionaryItemName) {
        return R.timeout();
    }

    @Override
    public R<TenantAiInfoDTO> queryAiInfo(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<String> getTenantCodeByDomain(String domain) {
        return R.timeout();
    }

    @Override
    public R<Boolean> clearAndCreatedDoctorQrCode(String code) {
        return  R.timeout();
    }

    @Override
    public R<Tenant> queryDomainByCode(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<Tenant> queryTenantDiseaseType(String code) {
        return R.timeout();
    }

    @Override
    public R<Boolean> queryDataSecuritySettings(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<String> queryOfficialAccountType(String tenantCode) {
        return R.success("CERTIFICATION_SERVICE_NUMBER");//2026daxiong 调试加入
        //return R.timeout();
    }

}
