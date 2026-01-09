package com.caring.sass.tenant.api;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.hystrix.TenantApiFallback;
import com.caring.sass.tenant.dto.TenantAiInfoDTO;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@FeignClient(name = "${caring.feign.authority-server:caring-tenant-server}", fallback = TenantApiFallback.class
        , path = "/tenant", qualifier = "tenantApi")
public interface TenantApi {

    /**
     * 根据编码获取租户信息UserBizApi
     *
     * @param tenantCode 租户编码
     */
    @GetMapping(value = "/getByCode")
    R<Tenant> getByCode(@RequestParam(value = "tenantCode") String tenantCode);


    @ApiOperation(value = "查询项目名称通过code")
    @PostMapping(value = "queryTenantNameByCodes")
    R<List<Tenant>> queryTenantNameByCodes(@RequestBody Set<String> codes);
    /**
     * 使用WxAppId获取项目信息
     *
     * @param wxAppId
     * @return
     */
    @GetMapping("getByWxAppId/{wxAppId}")
    R<Tenant> getTenantByWxAppId(@PathVariable("wxAppId") String wxAppId);

    /**
     * 同步项目的微信公众号校验状态
     *
     * @param tenant
     * @return
     */
    @PostMapping("updateWxStatus")
    R<Boolean> updateWxStatus(@RequestBody Tenant tenant);

    @ApiOperation("检查公众号是否被其他项目配置")
    @GetMapping("checkWxAppIdStatus")
    R<List<Tenant>> checkWxAppIdStatus(@RequestParam("tenantCode") String tenantCode,
                                       @RequestParam("wxAppId") String wxAppId);

    @ApiOperation("清除项目保存的appId")
    @GetMapping("clearWxAppId")
    R<Boolean> clearWxAppId(@RequestParam("wxAppId") String wxAppId );

    @GetMapping("getAllNormalTenant")
    R<List<Tenant>> getAllNormalTenant();


    @GetMapping("getAllTenantCode")
    R<List<Object>> getAllTenantCode();

    @ApiOperation("查询项目列表")
    @GetMapping("queryTenantList/tenantOfficialAccountType")
    R<List<Tenant>> queryTenantList(@RequestParam(name = "officialAccountType") TenantOfficialAccountType officialAccountType);


    @ApiOperation(value = "域名获取appId和code")
    @GetMapping(value = "/anno/getAppIdAndCodeByDomain")
    R<Tenant> getAppIdAndCodeByDomain(@RequestParam("domain") String domain);

    @ApiOperation(value = "修改项目的医生激活码")
    @GetMapping(value = "/updateDoctorQrCode")
    R<Boolean> updateDoctorQrCode(@RequestParam("code") String code, @RequestParam("dictionaryItemName") String dictionaryItemName);


    @ApiOperation(value = "查询Ai的头像名称")
    @ApiImplicitParam(value = "租户，请求头中存在可以不传", name = "tenantCode")
    @GetMapping(value = "/queryAiInfo")
    R<TenantAiInfoDTO> queryAiInfo(@RequestParam(name = "tenantCode", required = false) String tenantCode);

    @ApiOperation(value = "根据域名获取项目CODE", notes = "根据域名获取项目CODE")
    @GetMapping(value = "/anno/getTenantCodeByDomain")
    R<String> getTenantCodeByDomain(@RequestParam("domain") String domain);


    @ApiOperation(value = "清除医生激活码并重新生成")
    @GetMapping(value = "/clearAndCreatedDoctorQrCode")
    R<Boolean> clearAndCreatedDoctorQrCode(@RequestParam(name = "code") String code);


    @ApiOperation(value = "查询租户的域名")
    @GetMapping(value = "/queryDomainByCode")
    R<Tenant> queryDomainByCode(@RequestParam(name = "tenantCode") String tenantCode);


    @ApiOperation(value = "根据编码获取项目弹框信息")
    @GetMapping(value = "queryTenantDiseaseType")
    R<Tenant> queryTenantDiseaseType(@RequestParam(name = "code") String code);


    @ApiOperation("查询项目的数据安全开关")
    @GetMapping("queryDataSecuritySettings")
    R<Boolean> queryDataSecuritySettings(@RequestParam(value = "tenantCode", required = false) String tenantCode);

    @ApiOperation("查询项目的服务号类型")
    @GetMapping("queryOfficialAccountType")
    R<String> queryOfficialAccountType(@RequestParam(value = "tenantCode", required = false) String tenantCode);
}
