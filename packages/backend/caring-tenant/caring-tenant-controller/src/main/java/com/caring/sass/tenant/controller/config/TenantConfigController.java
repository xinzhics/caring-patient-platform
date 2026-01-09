package com.caring.sass.tenant.controller.config;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.dto.form.FormUpdateDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.tenant.dto.config.RegFormConfigDTO;
import com.caring.sass.tenant.dto.config.WxBasicInfoDTO;
import com.caring.sass.tenant.dto.config.WxConfigDTO;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.ConfigSaveDTO;
import com.caring.sass.wx.dto.guide.RegGuideSaveDTO;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.guide.RegGuide;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 项目基础配置
 *
 * @author leizhi
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tenantConfig")
@Api(value = "TenantConfig", tags = "项目基础配置")
public class TenantConfigController {

    private final TenantService tenantService;

    private final GuideApi guideApi;
    private final WeiXinApi weiXinApi;

    private final FormApi formApi;

    private final OrgService orgService;

    public TenantConfigController(GuideApi guideApi, WeiXinApi weiXinApi, TenantService tenantService, FormApi formApi, OrgService orgService) {
        this.guideApi = guideApi;
        this.weiXinApi = weiXinApi;
        this.tenantService = tenantService;
        this.formApi = formApi;
        this.orgService = orgService;
    }

    /**
     * 配置注册引导&表单
     */
    @ApiOperation("配置注册引导&表单")
    @PostMapping(value = "saveRegForm/{code}")
    public R<Boolean> saveRegForm(@Validated @RequestBody RegFormConfigDTO formConfigDTO,
                                  @PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        // 保存注册引导信息
        RegGuideSaveDTO regGuideSaveDTO = BeanUtil.toBean(formConfigDTO, RegGuideSaveDTO.class);

        // 保存表单配置信息
        Long basicFormId = formConfigDTO.getBasicFormId();
        if (basicFormId != null) {
            String basicFormName = formConfigDTO.getBasicFormName();
            regGuideSaveDTO.setBaseInfoName(basicFormName);
            formApi.update(FormUpdateDTO.builder().id(basicFormId).name(basicFormName).build());
        }
        Long heathFormId = formConfigDTO.getHeathFormId();
        if (heathFormId != null) {
            String heathFormName = formConfigDTO.getHeathFormName();
            regGuideSaveDTO.setHealthInfoName(heathFormName);
            formApi.update(FormUpdateDTO.builder().id(heathFormId).name(heathFormName).build());
        }
        regGuideSaveDTO.setTenantCode(code);
        R<RegGuide> r = guideApi.upsertByCode(regGuideSaveDTO);
        if (!r.getIsSuccess()) {
            return R.fail(r.getMsg());
        }
        return R.success();
    }

    /**
     * 查询注册引导&表单配置
     */
    @ApiOperation("查询注册引导&表单配置")
    @GetMapping("queryRegForm/{code}")
    public R<RegFormConfigDTO> queryRegForm(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        RegFormConfigDTO reg = new RegFormConfigDTO();
        BaseContextHandler.setTenant(code);
        // 查询注册引导信息
        R<RegGuide> guideR = guideApi.getGuide();
        if (!guideR.getIsSuccess() || guideR.getData() == null) {
            return R.success(new RegFormConfigDTO());
        }
        RegGuide guide = guideR.getData();
        BeanUtil.copyProperties(guide, reg);
        // 查询表单信息
//        List<Form> basicForm = formApi.query(Form.builder().category(FormEnum.BASE_INFO).build()).getData();
//        List<Form> healthForm = formApi.query(Form.builder().category(FormEnum.HEALTH_RECORD).build()).getData();
//        reg.setBasicFormName("基本信息");
//        reg.setHeathFormName("疾病信息");
//        if (CollUtil.isNotEmpty(basicForm)) {
//            Form f1 = basicForm.get(0);
//            reg.setHasBasicForm(true).setBasicFormId(f1.getId())
//                    .setBasicFormName(f1.getName());
//        }
//        if (CollUtil.isNotEmpty(healthForm)) {
//            Form f2 = healthForm.get(0);
//            reg.setHasHealthForm(true).setHeathFormName(f2.getName()).setHeathFormId(f2.getId());
//        }
        return R.success(reg);
    }

    /**
     * 配置微信基础信息
     */
    @ApiOperation("配置微信基础信息")
    @PostMapping(value = "saveWxBasicInfo/{code}")
    public R<Boolean> saveWxBasicInfo(@Validated @RequestBody WxBasicInfoDTO wxBasicInfoDTO,
                                      @PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        String wxAppId = wxBasicInfoDTO.getWxAppId();
        String wxName = wxBasicInfoDTO.getWxName();
        ConfigSaveDTO configSaveDTO = ConfigSaveDTO.builder().name(wxName)
                .sourceId(wxBasicInfoDTO.getWxSourceId())
                .appId(wxAppId)
                .appSecret(wxBasicInfoDTO.getWxAppSecret()).id(wxBasicInfoDTO.getId())
                .authorizationFileName(wxBasicInfoDTO.getWxAuthorizationFileName())
                .build();
        R<Config> r = weiXinApi.saveOrUpdate(configSaveDTO);
        if (r.getIsError()) {
            return R.fail(r.getMsg());
        }
        // 更新项目微信名冗余字段
        tenantService.update(Wraps.<Tenant>lbU()
                .set(Tenant::getWxName, wxName)
                .set(Tenant::getWxAppId, wxAppId)
                .eq(Tenant::getCode, code));
        return R.success();
    }

    /**
     * 查询微信配置基础信息
     */
    @ApiOperation("查询微信配置基础信息")
    @GetMapping("queryWxBasicInfo/{code}")
    public R<WxBasicInfoDTO> queryWxBasicInfo(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        List<Config> configs = weiXinApi.query(Config.builder().build()).getData();
        if (CollUtil.isEmpty(configs)) {
            return R.success(new WxBasicInfoDTO());
        }
        Config config = configs.get(0);
        WxBasicInfoDTO basicInfoDTO = WxBasicInfoDTO.builder()
                .wxAppId(config.getAppId())
                .wxName(config.getName())
                .wxSourceId(config.getSourceId()).wxAppSecret(config.getAppSecret())
                .wxAuthorizationFileName(config.getAuthorizationFileName())
                .id(config.getId())
                .build();
        return R.success(basicInfoDTO);
    }

    /**
     * 查询微信配置信息
     */
    @ApiOperation("查询微信配置信息")
    @GetMapping("queryWxConfig/{code}")
    public R<WxConfigDTO> queryWxConfig(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        // todo 域名白名单暂时写死进行调试
        BaseContextHandler.setTenant(code);
        Config data = weiXinApi.getConfig(new Config()).getData();
        if (data == null) {
            return R.success(new WxConfigDTO());
        }
        String token = data.getToken();
        // 查询项目信息
        Tenant tenant = tenantService.getByCode(code);
        if (tenant == null) {
            return R.fail("项目不存在");
        }
        WxConfigDTO wxConfigDTO = WxConfigDTO.builder()
                .bizDomain(ApplicationDomainUtil.wxJsSecurityDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime())))
                .serverToken(token)
                .whitelist(ApplicationProperties.getWhiteList())
                .serverUrl(ApplicationDomainUtil.wxServerUrl(tenant.getCode()))
                .tenantCode(code).build();
        return R.success(wxConfigDTO);
    }


    /**
     * 查询项目后台信息
     * 后台地址、用户名、密码、默认机构码
     *
     * @return 项目后台信息
     */
    @ApiOperation("查询项目后台信息")
    @GetMapping("tenantMngInfo/{code}")
    public R<Map<String, String>> tenantMngInfo(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        Tenant tenant = tenantService.getByCode(code);
        if (tenant == null) {
            return R.fail("项目不存在");
        }

        Map<String, String> ret = new HashMap<>();
        String orgCode = "";
        String adminUrl = ApplicationDomainUtil.adminUrl(tenant.getDomainName());
        ret.put("adminUrl", adminUrl);
        ret.put("userName", User.ACCOUNT_ADMIN);
        ret.put("password", User.ACCOUNT_PASSWORD);
        Org org = orgService.getOne(Wraps.<Org>lbQ().eq(Org::getReadonly, true));
        if (Objects.nonNull(org)) {
            orgCode = org.getCode();
        }
        ret.put("orgCode", orgCode);
        return R.success(ret);
    }

}
