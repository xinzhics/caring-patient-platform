package com.caring.sass.tenant.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.tenant.dao.LibraryTenantMapper;
import com.caring.sass.tenant.dto.*;
import com.caring.sass.tenant.dto.router.H5RouterPatientDto;
import com.caring.sass.tenant.entity.*;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.entity.router.H5Ui;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.GlobalUserService;
import com.caring.sass.tenant.service.StepService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.tenant.service.router.H5RouterService;
import com.caring.sass.tenant.service.router.H5UiService;
import com.caring.sass.tenant.utils.QrCodeUtils;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.CreateFollowerPermanentQrCode;
import com.caring.sass.wx.dto.config.QrCodeDto;
import com.caring.sass.wx.entity.guide.RegGuide;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.caring.sass.tenant.enumeration.TenantStatusEnum.NORMAL;

/**
 * <p>
 * 前端控制器
 * 项目
 * <p>
 * 创建项目流程：
 * 1. COLUMN模式： 新增项目、初始化内置项目数据
 * 2. SCHEMA模式： 新增项目、初始化库、初始化表、初始化内置项目数据
 * 3. DATASOURCE模式
 * 该模式有2种动态创建项目数据源的方式
 * LOCAL: 项目数据源跟默认数据源在同一个 物理数据库   （启动时程序连192.168.1.1:3306/caring_defualts库，项目连192.168.1.2:3306/caring_base_xxxx库）
 * REMOTE：项目数据源跟默认数据源不在同一个 物理数据库（启动时程序连192.168.1.1:3306/caring_defualts库，项目连192.168.1.2:3306/caring_base_xxxx库）
 * <p>
 * LOCAL模式会自动使用程序默认库的账号，进行创建项目库操作，所以设置的账号密码必须拥有超级权限，但在程序中配置数据库的超级权限账号是比较危险的事，所以需要谨慎使用。
 * REMOTE模式 考虑到上述问题，决定让新增项目的管理员，手动创建好项目库后，提供账号密码连接信息等，配置到DatasourceConfig表，创建好项目后，在初始化数据源页面，
 * 选择已经创建好的数据源进行初始化操作。
 * <p>
 * 以上2种方式各有利弊，请大家酌情使用。 有更好的意见可以跟我讨论一下。
 * <p>
 * 先调用 POST /datasourceConfig 接口保存数据源
 * 在调用 POST /tenant 接口保存项目信息
 * 然后调用 POST /tenant/connect 接口为每个服务连接自己的数据源，并初始化表和数据
 *
 * @author caring
 * @date 2019-10-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tenant")
@Api(value = "Tenant", tags = "项目")
@SysLog(enabled = false)
//public class TenantController extends SuperCacheController<TenantService, Long, Tenant, Tenant, TenantSaveDTO, TenantUpdateDTO> {
public class TenantController extends SuperController<TenantService, Long, Tenant, Tenant, TenantSaveDTO, TenantUpdateDTO> {

    private final AppConfigService appConfigService;

    private final StepService stepService;

    private final GuideApi guideApi;

    private final OrgService orgService;

    private final WeiXinApi weiXinApi;

    private final LibraryTenantMapper libraryTenantMapper;

    private final H5RouterService h5RouterService;

    private final H5UiService h5UiService;

    private final GlobalUserService globalUserService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    FormApi formApi;

    public TenantController(AppConfigService appConfigService, StepService stepService,
                            LibraryTenantMapper libraryTenantMapper, H5RouterService h5RouterService, H5UiService h5UiService,
                            GlobalUserService globalUserService,
                            GuideApi guideApi, OrgService orgService, WeiXinApi weiXinApi) {
        this.appConfigService = appConfigService;
        this.stepService = stepService;
        this.guideApi = guideApi;
        this.orgService = orgService;
        this.libraryTenantMapper = libraryTenantMapper;
        this.weiXinApi = weiXinApi;
        this.h5RouterService = h5RouterService;
        this.h5UiService = h5UiService;
        this.globalUserService = globalUserService;
    }

    @ApiOperation(value = "查询项目名称通过code")
    @PostMapping(value = "queryTenantNameByCodes")
    public R<List<Tenant>> queryTenantNameByCodes(@RequestBody Set<String> codes) {
        if (CollUtil.isEmpty(codes)) {
            return R.success(null);
        }
        List<Tenant> tenantList = baseService.list(Wraps.<Tenant>lbQ()
                .select(SuperEntity::getId, Tenant::getName)
                .in(Tenant::getCode, codes));
        return R.success(tenantList);
    }

    @ApiOperation(value = "获取项目信息通过请求头的code")
    @GetMapping(value = "getTenantDetail/noCode")
    public R<Tenant> getTenantDetailNoCode() {
        String code = BaseContextHandler.getTenant();
        if (StrUtil.isEmpty(code)) {
            throw new BizException("项目code不存在");
        }
        Tenant tenant = baseService.getByCode(code);
        return R.success(tenant);
    }

    @ApiOperation(value = "根据编码获取项目弹框信息")
    @GetMapping(value = "queryTenantDiseaseType")
    public R<Tenant> queryTenantDiseaseType(@RequestParam(value = "code")
                                                                @NotEmpty(message = "code不能为空") String code) {
        Tenant tenant = baseService.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getCode, code)
                .select(SuperEntity::getId, Tenant::getDiseasesType));
        if (Objects.nonNull(tenant)) {
            return R.success(tenant);
        } else {
            return R.success(null);
        }
    }



    @ApiOperation(value = "根据编码获取项目弹框信息")
    @GetMapping(value = "queryTenantInfo")
    public R<TenantInfoDTO> queryTenantInfo(@RequestParam(value = "code") @NotEmpty(message = "code不能为空") String code) {
        BaseContextHandler.setTenant(code);
        AppConfig appConfig = appConfigService.getOne(Wraps.<AppConfig>lbQ());
        String apkUrl = "";
        if (appConfig != null) {
            apkUrl = appConfig.getApkUrl();
        }

        Org org = orgService.getOne(Wraps.<Org>lbQ().eq(Org::getReadonly, true));
        String orgCode = Objects.nonNull(org) ? org.getCode() : "";
        Tenant tenant = baseService.getByCode(code);
        String adminUrl = ApplicationDomainUtil.adminUrl(tenant.getDomainName());
        TenantInfoDTO tenantInfo = TenantInfoDTO.builder().apkScUrl(apkUrl)
                .packageStatus(appConfig == null ? AppConfig.NOT_PACKAGING : appConfig.getPackageStatus())
                .uniPackageStatus(appConfig == null ? AppConfig.NOT_PACKAGING : appConfig.getUniPackageStatus())
                .uniApkScUrl(appConfig == null ? "" : appConfig.getUniApkUrl())
                .mngUrl(adminUrl).orgCode(orgCode)
                .userName(User.ACCOUNT_ADMIN).password(User.ACCOUNT_PASSWORD)
                .build();

        return R.success(tenantInfo);
    }


    /**
     * 搜索为项目名称、登录名、公众号的搜索
     */
    @Override
    public R<IPage<Tenant>> page(PageParams<Tenant> params) {
        String userType = BaseContextHandler.getUserType();
        List<GlobalUserTenant> globalUserTenants;
        List<Long> tenantIds;
        // 如果当前用户是超管。 超管需要返回的列表中增加创建者、管理者
        IPage<Tenant> page;

        LbqWrapper<Tenant> lbqWrapper = Wraps.<Tenant>lbQ();

        Tenant queryModel = params.getModel();
        // 拼接查询条件
        String keyWord = queryModel.getKeyWord();
        if (queryModel.getStatus() != null) {
            lbqWrapper.eq(Tenant::getStatus, queryModel.getStatus());
        } else {
            lbqWrapper.in(Tenant::getStatus, NORMAL, TenantStatusEnum.FORBIDDEN, TenantStatusEnum.WAIT_INIT);
        }
        if (StrUtil.isNotEmpty(keyWord)) {
            lbqWrapper.and(wq -> wq.eq(Tenant::getCode, keyWord)
                    .or().like(Tenant::getName, keyWord)
                    .or().like(Tenant::getDomainName, keyWord)
                    .or().like(Tenant::getWxName, keyWord));
        }
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            Long userId = BaseContextHandler.getUserId();
            // 如果当前用户是客户。客户的项目列表要设置 授权项目、自建项目。项目范围为自己的授权项目和自建项目
            globalUserTenants = globalUserService.selectTenantIds(userId);
            tenantIds = globalUserTenants.stream().map(GlobalUserTenant::getTenantId).collect(Collectors.toList());
            if (CollUtil.isEmpty(tenantIds)) {
                page = params.buildPage();
                page.setTotal(0);
                return R.success(page);
            }
            lbqWrapper.in(SuperEntity::getId, tenantIds);
        }
        String sort = params.getSort();
        if ("daysRemaining".equals(sort) || "sort".equals(sort)) {
            int count = baseService.count(lbqWrapper);
            long current = params.getCurrent();
            long size = params.getSize();
            page = new Page(params.getCurrent(), params.getSize());
            page.setTotal(count);
            if (count >= size * (current - 1)) {
                if ("daysRemaining".equals(sort)) {
                    sort = "days_remaining";
                }
                if ("sort".equals(sort)) {
                    sort = "sort";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(" order by ISNULL(").append(sort).append("),").append(sort);
                String order = params.getOrder();
                if ("ascending".equals(order)) {
                    stringBuilder.append(" asc");
                } else {
                    stringBuilder.append(" desc");
                }
                if ("days_remaining".equals(sort)) {
                    stringBuilder.append(" ,status_sort asc ");
                }
                stringBuilder.append(" limit ").append((current - 1) * size).append(",").append(size);
                lbqWrapper.last(stringBuilder.toString());
                List<Tenant> tenantList = baseService.list(lbqWrapper);
                page.setRecords(tenantList);
            } else {
                page = params.buildPage();
                baseService.page(page, lbqWrapper);
            }
        } else {
            page = params.buildPage();
            baseService.page(page, lbqWrapper);
        }
        List<Tenant> tenants = page.getRecords();
        if (CollUtil.isEmpty(tenants)) {
            return R.success(page);
        }

        tenantIds = tenants.stream().map(Tenant::getId).collect(Collectors.toList());
        // 如果当前用户是超管。 超管需要返回的列表中增加创建者、管理者
        if (UserType.GLOBAL_ADMIN.equals(userType)) {
            List<Long> accountIds = tenants.stream().map(Tenant::getCreateUser).collect(Collectors.toList());
            // 设置 项目的 创建者 和 管理者。
            List<GlobalUserTenant> userTenantList = globalUserService.selectTenantIds(tenantIds);
            Map<Long, GlobalUserTenant> userTenantMap = userTenantList.stream().collect(Collectors.toMap(GlobalUserTenant::getTenantId, item -> item, (o1, o2) -> o2));

            userTenantList.forEach((item) -> accountIds.add(item.getAccountId()));
            // 查询项目的创建者。和管理者
            List<GlobalUser> globalUserList = globalUserService.list(Wraps.<GlobalUser>lbQ().in(SuperEntity::getId, accountIds));
            Map<Long, GlobalUser> userMap = globalUserList.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
            for (Tenant tenant : tenants) {
                StringBuilder otherInfoRemark = new StringBuilder();
                Long createUser = tenant.getCreateUser();
                // 取 创建人的姓名
                GlobalUser user = userMap.get(createUser);
                if (Objects.nonNull(user)) {
                    otherInfoRemark.append(user.getName());
                } else {
                    otherInfoRemark.append("-");
                }
                otherInfoRemark.append("/");
                GlobalUserTenant userTenant = userTenantMap.get(tenant.getId());
                if (Objects.nonNull(userTenant)) {
                    Long accountId = userTenant.getAccountId();
                    user = userMap.get(accountId);
                    if (Objects.nonNull(user)) {
                        otherInfoRemark.append(user.getName());
                        tenant.setOtherInfoRemark(otherInfoRemark.toString());
                        continue;
                    }
                }
                otherInfoRemark.append("-");
                tenant.setOtherInfoRemark(otherInfoRemark.toString());
            }
        }
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            // 设置第三方客户的 项目是授权项目或自建项目。
            List<GlobalUserTenant> userTenantList = globalUserService.selectTenantIds(tenantIds);
            Map<Long, GlobalUserTenant> userTenantMap = userTenantList.stream().collect(Collectors.toMap(GlobalUserTenant::getTenantId, item -> item, (o1, o2) -> o2));
            for (Tenant tenant : tenants) {
                GlobalUserTenant userTenant = userTenantMap.get(tenant.getId());
                tenant.setOtherInfoRemark(userTenant.getManagementType());
            }
        }

        return R.success(page);
    }

    /**
     * 根据编码获取项目信息
     *
     * @param tenantCode 项目编码
     */
    @ApiOperation(value = "根据编码获取项目信息", notes = "根据编码获取项目信息")
    @ApiImplicitParam(name = "tenantCode", value = "账号", dataType = "string", paramType = "query")
    @GetMapping(value = "/getByCode")
    public R<Tenant> getByCode(@RequestParam String tenantCode) {
        Tenant tenant = baseService.getByCode(tenantCode);
        return success(tenant);
    }

    /**
     * 根据编码获取项目信息
     */
    @ApiOperation(value = "根据编码获取项目信息", notes = "根据编码获取项目信息")
    @ApiImplicitParam(name = "tenantCode", value = "账号", dataType = "string", paramType = "query")
    @GetMapping(value = "/anno/getByCode/{code}")
    public R<Tenant> annoByCode(@PathVariable String code) {
        Tenant tenant = baseService.getByCode(code);
        return success(tenant);
    }

    /**
     * 根据域名获取项目信息
     *
     * @param domain 项目域名
     */
    @ApiOperation(value = "根据域名获取项目信息", notes = "根据域名获取项目信息")
    @ApiImplicitParam(name = "domain", value = "域名", dataType = "string", paramType = "query")
    @GetMapping(value = "/anno/getByDomain")
    public R<Tenant> getByDomain(@RequestParam String domain) {
        Tenant tenant = baseService.getByDomain(domain);
        return success(tenant);
    }


    /**
     * 域名获取appId和code
     *
     * @param domain 项目域名
     */
    @ApiOperation(value = "域名获取appId和code")
    @GetMapping(value = "/anno/getAppIdAndCodeByDomain")
    public R<Tenant> getAppIdAndCodeByDomain(@RequestParam("domain") String domain) {

        Tenant tenant = baseService.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getDomainName, domain).last(" limit 0,1 ")
                .select(Tenant::getWxAppId, Tenant::getCode, Tenant::getName, Tenant::getWxBindTime, SuperEntity::getId));
        return success(tenant);
    }



    @ApiOperation(value = "根据域名获取项目CODE", notes = "根据域名获取项目CODE")
    @GetMapping(value = "/anno/getTenantCodeByDomain")
    public R<String> getTenantCodeByDomain(@RequestParam("domain") String domain) {
        Tenant tenant = baseService.getOne(Wraps.<Tenant>lbQ().select(Tenant::getCode, SuperEntity::getId).eq(Tenant::getDomainName, domain).last(" limit 0,1 "));
        if (Objects.nonNull(tenant)) {
            return success(tenant.getCode());
        } else {
            return R.fail("域名下的项目不存在");
        }
    }


    /**
     * 根据域名获取项目信息
     *
     * @param domain 项目域名
     */
    @ApiOperation(value = "根据域名获取项目信息", notes = "根据域名获取项目信息")
    @Deprecated
    @GetMapping(value = "/anno/patient/getByDomain")
    public R<WxTenantInfoDto> getWxTenantInfoByDomain(@RequestParam String domain) {
        Tenant tenant = baseService.getByDomain(domain);
        WxTenantInfoDto wxTenantInfoDto = new WxTenantInfoDto();
        if (Objects.nonNull(tenant)) {
            BaseContextHandler.setTenant(tenant.getCode());
            BeanUtils.copyProperties(tenant, wxTenantInfoDto);
            List<H5Router> routers = h5RouterService.list(Wraps.<H5Router>lbQ(H5Router.builder().userType(UserType.PATIENT).build())
                    .orderByAsc(H5Router::getSortValue));
            wxTenantInfoDto.setRouterData(routers);
            List<H5Ui> h5Uis = h5UiService.list(Wraps.<H5Ui>lbQ().orderByAsc(H5Ui::getSortValue));
            Map<String, H5Ui> map = h5Uis.stream()
                    .collect(Collectors.toMap(H5Ui::getCode, a -> a, (o, n) -> o));
            wxTenantInfoDto.setStyleDate(map);
            R<RegGuide> regGuideR = guideApi.getGuideByTenantCode(tenant.getCode());
            RegGuide data = regGuideR.getData();
            String jsonString = JSONObject.toJSONString(data);
            wxTenantInfoDto.setRegGuide(JSONObject.parseObject(jsonString));
            return R.success(wxTenantInfoDto);
        }
        return R.success(null);
    }


    @ApiOperation(value = "患者个人中心2.0根据域名获取项目信息")
    @GetMapping(value = "/v2/anno/patient/getByDomain")
    public R<WxTenantInfoDto> getV2WxTenantInfoByDomain(@RequestParam String domain) {
        Tenant tenant = baseService.getByDomain(domain);
        WxTenantInfoDto wxTenantInfoDto = new WxTenantInfoDto();
        if (Objects.nonNull(tenant)) {
            BaseContextHandler.setTenant(tenant.getCode());
            BeanUtils.copyProperties(tenant, wxTenantInfoDto);
            H5RouterPatientDto routerPatientDto = h5RouterService.patientRouter(UserType.PATIENT);
            wxTenantInfoDto.setRouterPatientDto(routerPatientDto);
            List<H5Ui> h5Uis = h5UiService.list(Wraps.<H5Ui>lbQ().orderByAsc(H5Ui::getSortValue));
            Map<String, H5Ui> map = h5Uis.stream()
                    .collect(Collectors.toMap(H5Ui::getCode, a -> a, (o, n) -> o));
            wxTenantInfoDto.setStyleDate(map);
            R<RegGuide> regGuideR = guideApi.getGuideByTenantCode(tenant.getCode());
            RegGuide data = regGuideR.getData();
            String jsonString = JSONObject.toJSONString(data);
            wxTenantInfoDto.setRegGuide(JSONObject.parseObject(jsonString));
            R<Integer> intoTheGroup = formApi.getFormIntoTheGroup(FormEnum.BASE_INFO.getCode(), null);
            if (intoTheGroup.getIsSuccess()) {
                wxTenantInfoDto.setPatientCompleteFormStatus(intoTheGroup.getData());
            }
            return R.success(wxTenantInfoDto);
        }
        return R.success(null);
    }

    /**
     * 根据域名获取微信项目介绍
     *
     * @param domain 项目域名
     */
    @ApiOperation(value = "根据域名获取微信项目介绍", notes = "根据域名获取微信项目介绍")
    @ApiImplicitParam(name = "domain", value = "域名", dataType = "string", paramType = "query")
    @GetMapping(value = "/anno/getGuideByDomain")
    public R<Map<String, String>> getGuideByDomain(@RequestParam String domain) {
        Tenant tenant = baseService.getByDomain(domain);
        if (Objects.isNull(tenant)) {
            return R.fail("项目不存在");
        }
        Map<String, String> ret = new HashMap<>();
        ret.put("name", tenant.getName());
        ret.put("logo", tenant.getLogo());
        R<RegGuide> guideR = guideApi.getGuide();
        if (guideR.getIsSuccess() && Objects.nonNull(guideR.getData())) {
            ret.put("intro", guideR.getData().getIntro());
            ret.put("agreement", guideR.getData().getAgreement());
            ret.put("doctorAgreement", guideR.getData().getDoctorAgreement());
        }
        return success(ret);
    }


    @ApiOperation(value = "查询所有项目", notes = "查询所有项目")
    @GetMapping("/all")
    public R<List<Tenant>> list() {
        LbqWrapper<Tenant> wrapper = Wraps.<Tenant>lbQ().
                eq(Tenant::getStatus, NORMAL);
        List<Tenant> list = baseService.list(wrapper);
        return success(list);
    }

    @Override
    public R<Tenant> handlerSave(TenantSaveDTO model) {
        Tenant tenant = baseService.save(model);
        return success(tenant);
    }

    /**
     * @return com.caring.sass.base.R<java.util.List < com.caring.sass.tenant.entity.Tenant>>
     * @Author yangShuai
     * @Description 查询正常的项目
     * @Date 2020/10/26 16:24
     */
    @GetMapping("getAllNormalTenant")
    public R<List<Tenant>> getAllNormalTenant() {
        LbqWrapper<Tenant> wrapper = Wraps.<Tenant>lbQ().
                eq(Tenant::getStatus, NORMAL)
                .eq(Tenant::getWxStatus, Tenant.WX_STATUS_YES);
        List<Tenant> list = baseService.list(wrapper);
        return R.success(list);
    }

    @ApiOperation("获取所有项目")
    @GetMapping(value = "/getAllTenant")
    public R<List<Tenant>> getAllTenant() {
        List<Tenant> tenants = baseService.list(Wraps.<Tenant>lbQ()
                .select(SuperEntity::getId,
                        Tenant::getName,
                        Tenant::getCode,
                        Tenant::getLogo));
        return R.success(tenants);
    }

    @ApiOperation(value = "检测项目是否存在", notes = "检测项目是否存在")
    @GetMapping("/check/{code}")
    public R<Boolean> check(@PathVariable("code") String code) {
        return success(baseService.check(code));
    }

    @ApiOperation(value = "检测域名是否存在", notes = "检测域名是否存在")
    @GetMapping("/checkDomain/{domain}")
    public R<Boolean> checkDomain(@PathVariable("domain") String domain) {
        return success(baseService.checkDomain(domain));
    }

    /**
     * todo 删除需要二次确认和admin密码确认
     *
     * @param ids id列表
     */
    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        // 这个操作相当的危险，请谨慎操作！！!
        String account = BaseContextHandler.getAccount();
        return success(baseService.delete(ids));
    }

    @ApiOperation(value = "修改项目状态", notes = "修改项目状态")
    @PutMapping("/status")
    public R<Boolean> updateStatus(@RequestParam("ids[]") List<Long> ids,
                                   @RequestParam(defaultValue = "FORBIDDEN") TenantStatusEnum status) {
        for (Long id : ids) {
            Tenant tenant = baseService.getById(id);
            if (Objects.nonNull(tenant)) {
                if (Objects.equals(NORMAL, status)) {
                    tenant.setPublishTime(LocalDateTime.now());
                }
                tenant.setStatus(status);
                baseService.updateById(tenant);
                baseService.removeRedisByCode(tenant.getCode());
            }
        }

        return success(true);
    }


    @ApiOperation(value = "使用WxAppId获取项目信息", notes = "使用WxAppId获取项目信息")
    @GetMapping("getByWxAppId/{wxAppId}")
    public R<Tenant> getByWxAppId(@PathVariable("wxAppId") String wxAppId) {
        Tenant tenant = baseService.getByWxAppId(wxAppId);
        if (Objects.nonNull(tenant)) {
            return R.success(tenant);
        } else {
            return R.fail("未查找项目");
        }
    }

    @ApiOperation("更新项目的微信状态")
    @PostMapping("updateWxStatus")
    public R<Boolean> updateWxStatus(@RequestBody Tenant tenant) {
        baseService.updateWxStatus(tenant);
        return R.success();
    }

    @ApiOperation("检查公众号是否被其他项目配置")
    @GetMapping("checkWxAppIdStatus")
    public R<List<Tenant>> checkWxAppIdStatus(@RequestParam("tenantCode") String tenantCode,
                                              @RequestParam("wxAppId") String wxAppId) {

        List<Tenant> tenants = baseService.checkWxAppIdStatus(wxAppId, tenantCode);
        return R.success(tenants);

    }

    @ApiOperation("清除项目保存的appId")
    @GetMapping("clearWxAppId")
    public R<Boolean> clearWxAppId(@RequestParam("wxAppId") String wxAppId) {
        baseService.clearWxAppId(wxAppId);
        return R.success(true);
    }

    /**
     * 更新项目的步骤信息
     *
     * @param updateTenantStepInfo 项目更新参数
     */
    @ApiOperation("更新项目的步骤信息")
    @PostMapping("updateTenantStepInfo")
    public R<Boolean> updateTenantStepInfo(@RequestBody UpdateTenantStepInfo updateTenantStepInfo) {
        String code = updateTenantStepInfo.getCode();
        BaseContextHandler.setTenant(code);
        Step step = stepService.getOne(Wraps.<Step>lbQ());
        if (step == null) {
            step = new Step();
        }
        Integer curTopStep = updateTenantStepInfo.getCurTopStep();
        Integer curChildStep = updateTenantStepInfo.getCurChildStep();
        step.setCurTopStep(curTopStep);
        step.setCurChildStep(curChildStep);
        stepService.saveOrUpdate(step);
        return R.success();
    }

    /**
     * 查询项目的步骤信息
     */
    @ApiOperation("查询项目的步骤信息")
    @GetMapping("queryTenantStepInfo")
    public R<UpdateTenantStepInfo> queryTenantStepInfo(@RequestParam(value = "code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        Step step = stepService.getOne(Wraps.<Step>lbQ());
        if (step == null) {
            UpdateTenantStepInfo rt = new UpdateTenantStepInfo().setCurTopStep(0).setCurChildStep(0);
            return R.success(rt);
        }
        UpdateTenantStepInfo stepInfo = new UpdateTenantStepInfo().setCode(code)
                .setCurTopStep(step.getCurTopStep())
                .setCurChildStep(step.getCurChildStep());
        return R.success(stepInfo);
    }

    // 获取项目的 app下载地址，状态，用户等信息
    // TODO 需要调用 组织机构服务， 查询项目管理员和初始机构编码
    @ApiOperation("获取项目的 app下载地址，状态，用户等信息")
    @GetMapping("getTenantDetail")
    public R<TenantDetailDTO> getTenantDetail(@RequestParam(required = false) Long tenantId) {
        TenantDetailDTO tenantDetailDTO = baseService.getTenantResultDetail(tenantId);
        return R.success(tenantDetailDTO);
    }

    @ApiOperation("创建项目，并初始化项目下信息")
    @PostMapping("createTenantAndInit")
    public R<Tenant> createTenantAndInit(@RequestBody TenantSaveDTO tenantSaveDTO) {
        String domain = tenantSaveDTO.getDomainName();
        if (baseService.checkDomain(domain)) {
            return R.fail("域名重复，请重新输入");
        }
        Tenant tenant = baseService.createTenantAndInit(tenantSaveDTO);
        return R.success(tenant);
    }

    @ApiOperation("复制项目")
    @PostMapping("copyTenant")
    public R<Tenant> copyTenant(@RequestBody @Validated TenantCopyDTO tenantCopyDTO) {
        Tenant tenant = baseService.copyTenant(tenantCopyDTO);
        return R.success(tenant);
    }

    @ApiOperation("项目统计")
    @GetMapping(value = "/tenantStatistic")
    public R<Map<String, Integer>> tenantStatistic() {
        Map<String, Integer> map = baseService.tenantStatistic();
        return success(map);
    }


    @ApiOperation("项目通知的二维码地址")
    @GetMapping(value = "/getTenantNotificationQrCode")
    public R<String> getTenantNotificationQrCode() {
        String code = BaseContextHandler.getTenant();
        Tenant tenant = baseService.getByCode(code);
        if (Objects.isNull(tenant)) {
            return R.fail("没有找到项目");
        }
        String qunFaNotificationQrUrl = tenant.getQunFaNotificationQrUrl();
        if (StringUtils.isNotEmptyString(qunFaNotificationQrUrl)) {
            return R.success(qunFaNotificationQrUrl);
        }
        CreateFollowerPermanentQrCode form = new CreateFollowerPermanentQrCode();
        form.setWxAppId(tenant.getWxAppId());
        form.setParams("qunfa_notification");
        R<QrCodeDto> permanentQrCode = weiXinApi.createFollowerPermanentQrCode(form);
        if (permanentQrCode.getIsSuccess() && Objects.nonNull(permanentQrCode.getData())) {
            qunFaNotificationQrUrl = permanentQrCode.getData().getUrl();
            if (StrUtil.isNotBlank(qunFaNotificationQrUrl)) {
                tenant.setQunFaNotificationQrUrl(qunFaNotificationQrUrl);
                baseService.updateById(tenant);
            }
        }
        return R.success(qunFaNotificationQrUrl);
    }

    @ApiOperation("获取所有项目的code")
    @GetMapping(value = "/getAllTenantCode")
    public R<List<Object>> getAllTenantCode() {

        List<Object> objects = baseService.listObjs(Wraps.<Tenant>lbQ().select(Tenant::getCode));
        return R.success(objects);

    }

    @ApiOperation("获取项目的内容库")
    @GetMapping(value = "/getTenantLibraryTenant/{tenantCode}")
    public R<List<LibraryTenant>> getTenantLibraryTenant(@PathVariable("tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        List<LibraryTenant> libraryTenants = libraryTenantMapper.selectList(Wraps.lbQ());
        return R.success(libraryTenants);

    }


    @ApiOperation("设置项目内容库, 存在就删除，不存在就新增")
    @GetMapping(value = "/saveOrDeleteTenantLibrary/{tenantCode}")
    public R<String> saveTenantLibraryTenant(@PathVariable("tenantCode") String tenantCode,
                                             @RequestParam("libraryId") Long libraryId) {

        BaseContextHandler.setTenant(tenantCode);
        Integer count = libraryTenantMapper.selectCount(Wraps.<LibraryTenant>lbQ()
                .eq(LibraryTenant::getLibraryId, libraryId));
        if (count > 0) {
            libraryTenantMapper.delete(Wraps.<LibraryTenant>lbQ()
                    .eq(LibraryTenant::getLibraryId, libraryId));
            return R.success("delete");
        } else {
            libraryTenantMapper.insert(LibraryTenant.builder().libraryId(libraryId).build());
            return R.success("save");
        }

    }


    @ApiOperation("重置项目内容库")
    @PutMapping(value = "/saveTenantLibrary")
    public R<String> saveTenantLibraryTenant(@RequestBody TenantLibraryDTO tenantLibraryDTO) {

        String tenantCode = tenantLibraryDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        libraryTenantMapper.delete(Wraps.<LibraryTenant>lbQ());
        List<Long> libraryIds = tenantLibraryDTO.getLibraryIds();
        for (Long libraryId : libraryIds) {
            libraryTenantMapper.insert(LibraryTenant.builder().libraryId(libraryId).build());
        }
        return R.success("success");
    }


    @ApiOperation(value = "修改项目的医生激活码")
    @GetMapping(value = "/updateDoctorQrCode")
    R<Boolean> updateDoctorQrCode(@RequestParam("code") String code,
                                  @RequestParam("dictionaryItemName") String dictionaryItemName) {
        Tenant tenant = baseService.getByCode(code);
        // 医生的激活码还没有生产，不需要更新
        if (StrUtil.isEmpty(tenant.getDoctorQrUrl())) {
            return R.success(true);
        }
        String doctorLoginCode = QrCodeUtils.tenantDoctorLoginCode(tenant.getDoctorQrUrl(), tenant.getName(), tenant.getLogo(), dictionaryItemName);
        tenant.setDoctorShareQrUrl(doctorLoginCode);

        String englishTenantDoctorLoginCode = QrCodeUtils.englishTenantDoctorLoginCode(tenant.getEnglishDoctorQrUrl(), tenant.getName(), tenant.getLogo(), dictionaryItemName);
        tenant.setEnglishDoctorShareQrUrl(englishTenantDoctorLoginCode);
        baseService.updateById(tenant);
        return R.success(true);
    }



    @ApiOperation(value = "清除医生激活码并重新生成")
    @GetMapping(value = "/clearAndCreatedDoctorQrCode")
    public R<Boolean> clearAndCreatedDoctorQrCode(@RequestParam(name = "code") String code) {
        Tenant tenant = baseService.getByCode(code);
        if (Objects.isNull(tenant)) {
            return R.success(true);
        }
        baseService.clearAndCreatedDoctorQrCode(tenant);
        return R.success(true);
    }


    @ApiOperation(value = "无授权查询Ai的头像名称")
    @ApiImplicitParam(value = "租户，请求头中存在可以不传", name = "tenantCode")
    @GetMapping(value = "anno/queryAiInfo")
    public R<TenantAiInfoDTO> annoQueryAiInfo(@RequestParam(name = "tenantCode", required = false) String tenantCode) {
        String tenant;
        if (StrUtil.isNotEmpty(tenantCode)) {
            tenant = tenantCode;
        } else {
            tenant = BaseContextHandler.getTenant();
        }
        TenantAiInfoDTO tenantAiInfoDTO = baseService.queryAiInfo(tenant);
        return R.success(tenantAiInfoDTO);
    }


    @ApiOperation(value = "查询Ai的头像名称")
    @ApiImplicitParam(value = "租户，请求头中存在可以不传", name = "tenantCode")
    @GetMapping(value = "/queryAiInfo")
    public R<TenantAiInfoDTO> queryAiInfo(@RequestParam(name = "tenantCode", required = false) String tenantCode) {
        String tenant;
        if (StrUtil.isNotEmpty(tenantCode)) {
            tenant = tenantCode;
        } else {
            tenant = BaseContextHandler.getTenant();
        }
        TenantAiInfoDTO tenantAiInfoDTO = baseService.queryAiInfo(tenant);
        return R.success(tenantAiInfoDTO);
    }


    @ApiOperation(value = "更新Ai的头像名称")
    @PostMapping(value = "/updateAiInfo")
    public R<TenantAiInfoDTO> updateAiInfo(@Validated @RequestBody TenantAiInfoDTO aiInfoDTO) {

        baseService.updateAiInfo(aiInfoDTO);
        return R.success(aiInfoDTO);
    }

    @ApiOperation(value = "申请一键登录项目端")
    @GetMapping(value = "/createTenantLoginUrl")
    public R<String> createTenantLoginUrl(String tenantCode) {

        String url = baseService.createTenantLoginUrl(tenantCode);
        return R.success(url);
    }


    @ApiOperation(value = "【项目配置】 查询基本信息")
    @GetMapping(value = "/queryTenantBaseInfo")
    public R<TenantBaseInfo> queryTenantBaseInfo(@RequestParam String tenantCode) {

        R<RegGuide> guideByTenantCode = guideApi.getGuideByTenantCode(tenantCode);
        RegGuide regGuide = null;
        if (guideByTenantCode.getIsSuccess()) {
            regGuide = guideByTenantCode.getData();
        }

        TenantBaseInfo baseInfo = baseService.queryTenantBaseInfo(regGuide ,tenantCode);
        return R.success(baseInfo);

    }



    @ApiOperation(value = "查询租户的域名")
    @GetMapping(value = "/queryDomainByCode")
    public R<Tenant> queryDomainByCode(@RequestParam String tenantCode) {
        Tenant one = baseService.getOne(Wraps.<Tenant>lbQ()
                .eq(Tenant::getCode, tenantCode)
                .select(SuperEntity::getId, Tenant::getDomainName, Tenant::getWxAppId, Tenant::getWxBindTime));
        if (Objects.isNull(one)) {
            return R.fail("租户不存在");
        }
        return R.success(one);
    }


    @ApiOperation("查询医生注册的方式")
    @GetMapping("anno/queryDoctorRegisterType")
    public R<Integer> annoQueryDoctorRegisterType() {
        return queryDoctorRegisterType(null);
    }

    @ApiOperation("查询医生注册的方式")
    @GetMapping("queryDoctorRegisterType")
    public R<Integer> queryDoctorRegisterType(@RequestParam(value = "tenantCode", required = false) String tenantCode) {
        if (tenantCode == null || tenantCode.isEmpty()) {
            tenantCode = BaseContextHandler.getTenant();
        }
        if (tenantCode == null || tenantCode.isEmpty()) {
            return R.fail("租户不存在");
        }
        Tenant serviceOne = baseService.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenantCode).select(Tenant::getDoctorRegisterType));
        return R.success(serviceOne.getDoctorRegisterType());
    }


    @ApiOperation("修改医生注册的方式")
    @PutMapping("doctorRegisterType")
    public R<Integer> doctorRegisterType(@RequestBody Tenant tenant) {
        String code = tenant.getCode();
        if (code.isEmpty()) {
            return R.fail("参数异常");
        }
        Integer doctorRegisterType = tenant.getDoctorRegisterType();
        if (doctorRegisterType == 0 || doctorRegisterType == 1) {
            Tenant serviceByCode = baseService.getByCode(code);
            serviceByCode.setDoctorRegisterType(doctorRegisterType);
            baseService.updateById(serviceByCode);
            return R.success(doctorRegisterType);
        } else {
            return R.fail("参数异常");
        }
    }

    @ApiOperation("查询项目的服务号类型")
    @GetMapping("queryOfficialAccountType")
    public R<String> queryOfficialAccountType(@RequestParam(value = "tenantCode", required = false) String tenantCode) {
        if (tenantCode == null || tenantCode.isEmpty()) {
            tenantCode = BaseContextHandler.getTenant();
        }
        if (tenantCode == null || tenantCode.isEmpty()) {
            return R.fail("租户不存在");
        }
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_OFFICIAL_ACCOUNT_TYPE);
        Object object = boundHashOps.get(tenantCode);
        if (object != null) {
            return R.success(object.toString());
        }
        Tenant serviceOne = baseService.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenantCode).select(SuperEntity::getId, Tenant::getOfficialAccountType));
        TenantOfficialAccountType accountType = serviceOne.getOfficialAccountType();
        if (accountType == null) {
            accountType = TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER;
        }
        boundHashOps.put(tenantCode, accountType.toString());
        return R.success(accountType.toString());
    }

    @ApiOperation("查询项目的服务号类型域名")
    @GetMapping("/anno/queryOfficialAccountTypeByDomain")
    public R<String> queryOfficialAccountTypeByDomain(@RequestParam(value = "domain", required = false) String domain) {
        Tenant serviceOne = baseService.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getDomainName, domain)
                .select(SuperEntity::getId, Tenant::getOfficialAccountType));
        TenantOfficialAccountType accountType = serviceOne.getOfficialAccountType();
        if (accountType == null) {
            accountType = TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER;
        }
        return R.success(accountType.toString());
    }

    @ApiOperation("更新项目的服务号类型")
    @PutMapping("updateOfficialAccountType")
    public R<Boolean> updateOfficialAccountType(@RequestParam(value = "tenantCode") String tenantCode,
                                                @RequestParam(value = "officialAccountType") TenantOfficialAccountType officialAccountType) {

        Tenant tenant = baseService.getByCode(tenantCode);
        tenant.setOfficialAccountType(officialAccountType);
        baseService.updateById(tenant);
        baseService.removeRedisByCode(tenantCode);
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_OFFICIAL_ACCOUNT_TYPE);
        boundHashOps.put(tenantCode, officialAccountType.toString());
        return R.success(true);
    }



    @ApiOperation("查询项目的数据安全开关")
    @GetMapping("queryDataSecuritySettings")
    public R<Boolean> queryDataSecuritySettings(@RequestParam(value = "tenantCode", required = false) String tenantCode) {
        if (tenantCode == null || tenantCode.isEmpty()) {
            tenantCode = BaseContextHandler.getTenant();
        }
        if (tenantCode == null || tenantCode.isEmpty()) {
            return R.fail("租户不存在");
        }
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_DATA_SECURITY_SETTINGS);
        Object object = boundHashOps.get(tenantCode);
        if (object != null) {
            return R.success(Boolean.parseBoolean(object.toString()));
        }
        Tenant serviceOne = baseService.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenantCode).select(Tenant::getDataSecuritySettings));
        boundHashOps.put(tenantCode, serviceOne.getDataSecuritySettings().toString());
        return R.success(serviceOne.getDataSecuritySettings());
    }


    @ApiOperation("更新项目的数据安全开关")
    @GetMapping("updateDataSecuritySettings")
    public R<Boolean> queryDataSecuritySettings(@RequestParam(value = "tenantCode") String tenantCode,
                                                @RequestParam(value = "dataSecuritySettings") Boolean dataSecuritySettings) {

        Tenant tenant = baseService.getByCode(tenantCode);
        tenant.setDataSecuritySettings(dataSecuritySettings);
        baseService.updateById(tenant);
        baseService.removeRedisByCode(tenantCode);
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_DATA_SECURITY_SETTINGS);
        boundHashOps.put(tenantCode, dataSecuritySettings.toString());
        return R.success(true);
    }



    @ApiOperation("查询项目列表")
    @GetMapping("queryTenantList/tenantOfficialAccountType")
    public R<List<Tenant>> queryTenantList(@RequestParam(name = "officialAccountType") TenantOfficialAccountType officialAccountType) {

        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.equals(officialAccountType)) {
            // 查询个人服务号列表
            List<Tenant> list = baseService.list(Wraps.<Tenant>lbQ().eq(Tenant::getOfficialAccountType, officialAccountType));
            return R.success(list);
        } else {
            // 企业服务号。或者 没有配置
            List<Tenant> list = baseService.list(Wraps.<Tenant>lbQ()
                    .eq(Tenant::getOfficialAccountType, TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER)
                    .or(i -> i.isNull(Tenant::getOfficialAccountType)));
            return R.success(list);
        }

    }


}
