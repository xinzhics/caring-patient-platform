package com.caring.sass.tenant.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.authority.api.CoreOrgApi;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.tenant.dao.GlobalUserTenantMapper;
import com.caring.sass.tenant.dao.LibraryTenantMapper;
import com.caring.sass.tenant.dao.StatisticsTenantMapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dto.*;
import com.caring.sass.tenant.entity.*;
import com.caring.sass.tenant.enumeration.SequenceEnum;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.tenant.enumeration.TenantTypeEnum;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.tenant.strategy.InitSystemContext;
import com.caring.sass.tenant.utils.QrCodeUtils;
import com.caring.sass.tenant.utils.SequenceNumUtil;
import com.caring.sass.tenant.utils.TenantCopyUtil;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.CreateFollowerPermanentQrCode;
import com.caring.sass.wx.dto.config.QrCodeDto;
import com.caring.sass.wx.entity.guide.RegGuide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.caring.sass.utils.BizAssert.isFalse;
import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 业务实现类
 * 企业
 * </p>
 *
 * @author caring
 * @date 2019-10-24
 */
@Slf4j
@Service
public class TenantServiceImpl extends SuperServiceImpl<TenantMapper, Tenant> implements TenantService {

    private final InitSystemContext initSystemContext;

    private final CoreOrgApi coreOrgApi;

    private final AppConfigService appConfigService;

    private final TenantCopyUtil tenantCopyUtil;

    private final LibraryTenantMapper libraryTenantMapper;

    private final DictionaryItemService dictionaryItemService;

    private final DoctorApi doctorApi;

    private final RedisTemplate<String, String> redisTemplate;

    private final GlobalUserTenantMapper globalUserTenantMapper;

    private final StatisticsTenantMapper statisticsTenantMapper;


    String RedisTenantKey = "TENANT_INFO_BY_CODE";


    private static final NamedThreadFactory TENANT_DELETE_NAMED_THREAD_FACTORY = new NamedThreadFactory("tenant-delete-", false);

    private static final ExecutorService TENANT_DELETE_EXECUTOR = new ThreadPoolExecutor(0, 2,
            0L, TimeUnit.MILLISECONDS,
            new SaasLinkedBlockingQueue<>(100), TENANT_DELETE_NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    public TenantServiceImpl(InitSystemContext initSystemContext,
                             CoreOrgApi coreOrgApi,
                             DictionaryItemService dictionaryItemService,
                             LibraryTenantMapper libraryTenantMapper,
                             GlobalUserTenantMapper globalUserTenantMapper,
                             AppConfigService appConfigService,
                             DoctorApi doctorApi,
                             StatisticsTenantMapper statisticsTenantMapper,
                             RedisTemplate<String, String> redisTemplate,
                             TenantCopyUtil tenantCopyUtil) {
        this.initSystemContext = initSystemContext;
        this.coreOrgApi = coreOrgApi;
        this.appConfigService = appConfigService;
        this.tenantCopyUtil = tenantCopyUtil;
        this.libraryTenantMapper = libraryTenantMapper;
        this.dictionaryItemService = dictionaryItemService;
        this.redisTemplate = redisTemplate;
        this.doctorApi = doctorApi;
        this.statisticsTenantMapper = statisticsTenantMapper;
        this.globalUserTenantMapper = globalUserTenantMapper;
    }


    @Override
    public void initTenantDict() {
        List<Object> tenantCode = baseMapper.selectObjs(Wraps.<Tenant>lbQ().select(Tenant::getCode));
        List<String> stringList = null;
        stringList = tenantCode.stream().map(Object::toString).collect(toList());
        DictionaryItemService service = SpringUtils.getBean(DictionaryItemService.class);
        if (CollUtil.isNotEmpty(stringList)) {

            for (String tenant : stringList) {
                SaasGlobalThreadPool.execute(() -> initTenantDict(tenant, service));
            }
        }
    }

    private void initTenantDict(String tenant,  DictionaryItemService service) {

        BaseContextHandler.setTenant(tenant);
        List<DictionaryItem> dictionaryItemList = new ArrayList<>();
        dictionaryItemList.add(DictionaryItem.builder().code("doctor").name("医生").sortValue(0).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("patient").name("患者").sortValue(1).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("assistant").name("医助").sortValue(2).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("register").name("注册").sortValue(3).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("notregister").name("未注册").sortValue(4).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("activation").name("激活").sortValue(5).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("notactivation").name("未激活").sortValue(6).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("registerrate").name("注册转化率").sortValue(7).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("unfollowrate").name("会员取关率").sortValue(8).status(true).build());
        dictionaryItemList.add(DictionaryItem.builder().code("diagnostictype").name("诊断类型").sortValue(9).status(true).build());
        service.saveBatchSomeColumn(dictionaryItemList);

    }

    /**
     * 清除redis中的缓存
     * @param code
     */
    @Override
    public void removeRedisByCode(String code) {
        redisTemplate.opsForHash().delete(RedisTenantKey, code);
    }

    /**
     *
     * @param tenant
     * @return
     */
    @Override
    public Tenant getByCode(String tenant) {
        Object object = redisTemplate.opsForHash().get(RedisTenantKey, tenant);
        if (object == null) {
            Tenant selectOne = baseMapper.selectOne(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenant));
            redisTemplate.opsForHash().put(RedisTenantKey, tenant, JSON.toJSONString(selectOne));
            return selectOne;
        } else {
            return JSON.parseObject(object.toString(), Tenant.class);
        }
    }

    @Override
    public Tenant getByDomain(String domain) {
        return baseMapper.selectOne(Wraps.<Tenant>lbQ().eq(Tenant::getDomainName, domain));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant save(TenantSaveDTO data) {
        // defaults 库
        isFalse(check(data.getCode()), "编码重复，请重新输入");

        // 1， 保存租户 (默认库)
        Tenant tenant = BeanPlusUtil.toBean(data, Tenant.class);
        tenant.setStatus(TenantStatusEnum.WAIT_INIT);
        tenant.setType(TenantTypeEnum.CREATE);
        // defaults 库
        save(tenant);

        // String key = buildKey(tenant.getCode());
        // cacheChannel.set(TENANT_NAME, key, tenant.getId());

        // 3, 初始化库，表, 数据  2.5.1以后，将初始化数据源和创建租户库逻辑分离 参考 this::connect
        // initSystemContext.init(tenant.getCode());
        return tenant;
    }

    @Override
    public boolean check(String tenantCode) {
        return super.count(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenantCode)) > 0;
    }



    @Override
    public Boolean delete(List<Long> ids) {
        List<Tenant> tenantList = baseMapper.selectList(Wraps.<Tenant>lbQ()
                .in(Tenant::getId, ids)
                .select(SuperEntity::getId, Tenant::getCode));
        if (tenantList.isEmpty()) {
            return true;
        }

        for (Tenant tenant : tenantList) {
            tenant.setStatus(TenantStatusEnum.DELETING);
            baseMapper.updateById(tenant);
            removeRedisByCode(tenant.getCode());
            statisticsTenantMapper.delete(Wraps.<StatisticsTenant>lbQ().eq(StatisticsTenant::getCode, tenant.getCode()));
            globalUserTenantMapper.delete(Wraps.<GlobalUserTenant>lbQ().eq(GlobalUserTenant::getTenantId, tenant.getId()));
            TENANT_DELETE_EXECUTOR.execute(() ->  syncDeleteTenant(tenant.getId(), tenant.getCode()));
        }
        return true;
    }



    /**
     * 服务启动时，
     * 查询之前没有删除完毕的 项目。 继续执行删除
     */
    @Override
    public void queryDeletingTenant() {
        List<Tenant> tenantList = baseMapper.selectList(Wraps.<Tenant>lbQ()
                .in(Tenant::getStatus, TenantStatusEnum.DELETING)
                .select(SuperEntity::getId, Tenant::getCode));
        if (tenantList.isEmpty()) {
            return;
        }
        for (Tenant tenant : tenantList) {
            tenant.setStatus(TenantStatusEnum.DELETING);
            updateById(tenant);
            TENANT_DELETE_EXECUTOR.execute(() ->  syncDeleteTenant(tenant.getId(), tenant.getCode()));
        }
    }

    /**
     * 异步删除项目的业务数据。
     * 业务数据删除完毕之后，再删除项目
     * @param id
     * @param code
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncDeleteTenant(Long id, String code) {

        List<Long> ids = new ArrayList<>();
        List<String> tenantCodeList = new ArrayList<>();
        tenantCodeList.add(code);
        initSystemContext.delete(ids, tenantCodeList);
        globalUserTenantMapper.delete(Wraps.<GlobalUserTenant>lbQ().eq(GlobalUserTenant::getTenantId, id));
        baseMapper.deleteById(id);
    }


    @Override
    public Boolean connect(TenantConnectDTO tenantConnect) {
        boolean flag = initSystemContext.initConnect(tenantConnect);
        if (flag) {
            Tenant build = Tenant.builder().connectType(tenantConnect.getConnectType())
                    .status(TenantStatusEnum.NORMAL).build();
            build.setId(tenantConnect.getId());
            updateById(build);
        }
        return flag;
    }


    @Override
    public Tenant getByWxAppId(String wxAppId) {

        LbqWrapper<Tenant> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(Tenant::getWxAppId, wxAppId);
        List<Tenant> tenants = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(tenants)) {
            return tenants.get(0);
        }
        return null;
    }


    @Override
    public void updateWxStatus(Tenant tenant) {
        Tenant byCode = getByCode(tenant.getCode());
        byCode.setWxAppId(tenant.getWxAppId());
        byCode.setWxStatus(tenant.getWxStatus());
        byCode.setWxName(tenant.getWxName());
        byCode.setWxBindErrorMessage(tenant.getWxBindErrorMessage());
        byCode.setWxBindTime(LocalDateTime.now());
        byCode.setWxBindErrorMessage("");
        baseMapper.updateById(byCode);

        removeRedisByCode(tenant.getCode());
        Tenant tenant1 = baseMapper.selectById(tenant.getId());
        SaasGlobalThreadPool.execute(() -> clearAndCreatedDoctorQrCode(tenant1));
    }

    /**
     * 清除医生医助激活码重新生成
     * @param tenant
     */
    @Override
    public void clearAndCreatedDoctorQrCode(Tenant tenant) {
        BaseContextHandler.setTenant(tenant.getCode());
        if (tenant.isPersonalServiceNumber()) {
            return;
        }
        WeiXinApi weiXinApi = SpringUtils.getBean(WeiXinApi.class);
        CreateFollowerPermanentQrCode form = new CreateFollowerPermanentQrCode();
        form.setWxAppId(tenant.getWxAppId());
        form.setParams("doctor_login_");
        R<QrCodeDto> permanentQrCode = weiXinApi.createFollowerPermanentQrCode(form);
        String itemName = dictionaryItemService.findDictionaryItemName(UserType.UCENTER_DOCTOR);
        String doctorDictItem = "医生";
        if (StrUtil.isNotEmpty(itemName)) {
            doctorDictItem = itemName;
        }
        if (permanentQrCode.getIsSuccess() && Objects.nonNull(permanentQrCode.getData())) {
            String doctorQrUrl = permanentQrCode.getData().getUrl();
            String doctorLoginCode = QrCodeUtils.tenantDoctorLoginCode(doctorQrUrl, tenant.getName(), tenant.getLogo(), doctorDictItem);
            tenant.setDoctorQrUrl(doctorQrUrl);
            tenant.setDoctorShareQrUrl(doctorLoginCode);
        }

        CreateFollowerPermanentQrCode englishForm = new CreateFollowerPermanentQrCode();
        englishForm.setWxAppId(tenant.getWxAppId());
        englishForm.setParams("doctor_english_login_");
        R<QrCodeDto> englishFormQrCode = weiXinApi.createFollowerPermanentQrCode(englishForm);
        if (englishFormQrCode.getIsSuccess() && Objects.nonNull(englishFormQrCode.getData())) {
            String url = englishFormQrCode.getData().getUrl();
            String doctorLoginCode = QrCodeUtils.englishTenantDoctorLoginCode(url, tenant.getName(), tenant.getLogo(), doctorDictItem);
            tenant.setEnglishDoctorQrUrl(url);
            tenant.setEnglishDoctorShareQrUrl(doctorLoginCode);
        }

        CreateFollowerPermanentQrCode assistantForm = new CreateFollowerPermanentQrCode();
        assistantForm.setWxAppId(tenant.getWxAppId());
        assistantForm.setParams("assistant_login_");
        permanentQrCode = weiXinApi.createFollowerPermanentQrCode(assistantForm);
        if (permanentQrCode.getIsSuccess() && Objects.nonNull(permanentQrCode.getData())) {
            String assistantQrUrl = permanentQrCode.getData().getUrl();
            tenant.setAssistantQrUrl(assistantQrUrl);
        }

        CreateFollowerPermanentQrCode englishAssistantForm = new CreateFollowerPermanentQrCode();
        englishAssistantForm.setWxAppId(tenant.getWxAppId());
        englishAssistantForm.setParams("assistant_english_login_");
        permanentQrCode = weiXinApi.createFollowerPermanentQrCode(englishAssistantForm);
        if (permanentQrCode.getIsSuccess() && Objects.nonNull(permanentQrCode.getData())) {
            String assistantQrUrl = permanentQrCode.getData().getUrl();
            tenant.setEnglishAssistantQrUrl(assistantQrUrl);
        }

        baseMapper.updateById(tenant);
        removeRedisByCode(tenant.getCode());

    }

    /**
     * @return boolean
     * /**
     * @Author yangShuai
     * @Description 检验域名是否被使用
     * @Date 2020/9/28 9:28
     **/
    @Override
    public boolean checkDomain(String domain) {
        LbqWrapper<Tenant> wrapper = Wraps.<Tenant>lbQ().eq(Tenant::getDomainName, domain);
        return super.count(wrapper) > 0;
    }

    /**
     * @return boolean
     * /**
     * @Author yangShuai
     * @Description 更新租户前， 检验域名是否已经被别人使用
     * @Date 2020/9/29 10:21
     **/
    @Override
    public boolean updateById(Tenant model) {
        Long id = model.getId();
        String domain = model.getDomainName();
        Tenant original = getById(id);
        boolean hasUpdateDomain = Objects.nonNull(domain)
                && !Objects.equals(original.getDomainName(), domain);
        // 项目域名被修改了，才验重复域名
        if (hasUpdateDomain) {
            boolean hasDomain = checkDomain(model.getDomainName());
            BizAssert.isFalse(hasDomain, "域名已经被使用");
        }
        int update = baseMapper.updateById(model);
        boolean updateDoctorLoginCode = false;
        if (StrUtil.isNotEmpty(model.getName()) && !model.getName().equals(original.getName())) {
            // 项目换名称了。更新项目的医生激活码和 医生的名片
            updateDoctorLoginCode = true;
        }
        if (StrUtil.isNotEmpty(model.getLogo()) && !model.getLogo().equals(original.getLogo())) {
            // 项目换名称了。更新项目的医生激活码和 医生的名片
            updateDoctorLoginCode = true;
            doctorApi.updateDoctorBusinessCardQrCodeForTenantInfo(original.getCode());
        }
        if (updateDoctorLoginCode) {
            String tenantName = model.getName() != null ? model.getName() : original.getName();
            String tenantLogo = model.getLogo() != null ? model.getLogo() : original.getLogo();
            BaseContextHandler.setTenant(original.getCode());
            String itemName = dictionaryItemService.findDictionaryItemName(UserType.UCENTER_DOCTOR);
            String doctorDictItem = "医生";
            if (StrUtil.isNotEmpty(itemName)) {
                doctorDictItem = itemName;
            }
            boolean updateStatus = false;
            if (original.isCertificationServiceNumber()) {
                if (original.getDoctorQrUrl() != null) {
                    String doctorLoginCode = QrCodeUtils.tenantDoctorLoginCode(original.getDoctorQrUrl(), tenantName, tenantLogo, doctorDictItem);
                    model.setDoctorShareQrUrl(doctorLoginCode);
                    updateStatus = true;
                }
                if (original.getEnglishDoctorQrUrl() != null) {
                    String doctorLoginCode = QrCodeUtils.englishTenantDoctorLoginCode(original.getEnglishDoctorQrUrl(), tenantName, tenantLogo, doctorDictItem);
                    model.setEnglishDoctorShareQrUrl(doctorLoginCode);
                    updateStatus = true;
                }
            }
            if (updateStatus) {
                baseMapper.updateById(model);
            }
        }
        removeRedisByCode(original.getCode());
        return update > 0;
    }

    /**
     * 初始化医助的激活二维码
     */
    public void initTenantAssistantQrCode() {
        WeiXinApi weiXinApi = SpringUtils.getBean(WeiXinApi.class);
        List<Tenant> tenantList = baseMapper.selectList(Wraps.<Tenant>lbQ()
                .eq(Tenant::getWxStatus, Tenant.WX_STATUS_YES)
                .eq(Tenant::getStatus, TenantStatusEnum.NORMAL)
                .isNull(Tenant::getAssistantQrUrl));
        R<QrCodeDto> permanentQrCode;
        String assistantQrUrl = "";
        for (Tenant tenant : tenantList) {
            CreateFollowerPermanentQrCode assistantForm = new CreateFollowerPermanentQrCode();
            assistantForm.setWxAppId(tenant.getWxAppId());
            assistantForm.setParams("assistant_login_");
            try {
                permanentQrCode = weiXinApi.createFollowerPermanentQrCode(assistantForm);
                if (permanentQrCode.getIsSuccess() && Objects.nonNull(permanentQrCode.getData())) {
                    assistantQrUrl = permanentQrCode.getData().getUrl();
                    tenant.setAssistantQrUrl(assistantQrUrl);
                    baseMapper.updateById(tenant);
                    removeRedisByCode(tenant.getCode());
                }
            } catch (Exception e) {
                log.error("initTenantAssistantQrCode wx config error ");
            }
        }
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 创建一个项目。并检验他的域名
     * @Date 2020/9/29 10:23
     */
    @Override
    public Tenant createTenantAndInit(TenantSaveDTO tenantSaveDTO) {
        Tenant tenant = new Tenant();

        BeanUtils.copyProperties(tenantSaveDTO, tenant);
        tenant.setWxStatus(Tenant.WX_STATUS_NO);
        tenant.setStatus(TenantStatusEnum.WAIT_INIT);
        tenant.setStatusSort(TenantStatusEnum.WAIT_INIT.getSort());
        tenant.setType(TenantTypeEnum.CREATE);
        tenant.setReadonly(false);

        // 生成租户编码
        String code = SequenceNumUtil.incrSequenceNum(SequenceEnum.TENANT_CODE, 4);
        tenant.setCode(code);
        int insert = baseMapper.insert(tenant);
        BaseContextHandler.setTenant(code);
        // 创建项目时。 关联默认内容库
        LibraryTenant libraryTenant = new LibraryTenant();
        libraryTenant.setLibraryId(1L);
        libraryTenantMapper.insert(libraryTenant);
        if (insert > 0) {
            createUserTenantInfo(tenant.getId());
            // 初始化项目数据
            initSystemContext.initConnectWithAdditional(TenantConnectDTO.builder()
                    .id(tenant.getId()).tenant(code)
                    .domain(tenant.getDomainName()).name(tenant.getName())
                    .build());
            return tenant;
        }
        return tenant;
    }


    @Override
    public TenantDetailDTO getTenantResultDetail(Long tenantId) {
        Tenant tenant;
        if (tenantId == null) {
            tenant = baseMapper.selectOne(Wrappers.lambdaQuery());
        } else {
            tenant = baseMapper.selectById(tenantId);
        }
        if (Objects.isNull(tenant)) {
            return null;
        }
        AppConfig appConfig = appConfigService.getByTenantId(tenantId);
        TenantDetailDTO tenantDetailDTO = new TenantDetailDTO();
        tenantDetailDTO.setId(tenant.getId());
        tenantDetailDTO.setAppLoadUrl(appConfig.getApkUrl());
        tenantDetailDTO.setStatus(tenant.getStatus());
        tenantDetailDTO.setDomainName(tenant.getDomainName());

        R<Org> defaultOrg = coreOrgApi.queryByTenant();
        if (defaultOrg.getIsSuccess() && defaultOrg.getData() != null) {
            tenantDetailDTO.setOrgCode(defaultOrg.getData().getCode());
        }
        // TODO： 需要查询默认的项目管理员，目前是默认的
        tenantDetailDTO.setProjectUserPassword(User.ACCOUNT_PASSWORD);
        tenantDetailDTO.setProjectUserName(User.ACCOUNT_ADMIN);
        return tenantDetailDTO;
    }

    /**
     * @title 项目管理首页的 项目状态统计。
     * @author 杨帅
     * @updateTime 2023/4/13 16:43
     * @throws
     */
    @Override
    public Map<String, Integer> tenantStatistic() {
        String userType = BaseContextHandler.getUserType();
        LbqWrapper<Tenant> totalWrap = Wraps.<Tenant>lbQ().in(Tenant::getStatus, TenantStatusEnum.NORMAL, TenantStatusEnum.FORBIDDEN, TenantStatusEnum.WAIT_INIT);
        LbqWrapper<Tenant> normalWrap = Wraps.<Tenant>lbQ().eq(Tenant::getStatus, TenantStatusEnum.NORMAL);
        LbqWrapper<Tenant> forbiddenWrap = Wraps.<Tenant>lbQ().eq(Tenant::getStatus, TenantStatusEnum.FORBIDDEN);
        Map<String, Integer> statistic = new HashMap<>();
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            List<GlobalUserTenant> userTenants = globalUserTenantMapper.selectList(Wraps.<GlobalUserTenant>lbQ()
                    .eq(GlobalUserTenant::getAccountId, BaseContextHandler.getUserId()));
            if (CollUtil.isEmpty(userTenants)) {
                statistic.put("total", 0);
                statistic.put("normal", 0);
                statistic.put("forbiddenCount", 0);
                return statistic;
            }
            List<Long> tenantIds = userTenants.stream().map(GlobalUserTenant::getTenantId).collect(toList());
            totalWrap.in(SuperEntity::getId, tenantIds);
            normalWrap.in(SuperEntity::getId, tenantIds);
            forbiddenWrap.in(SuperEntity::getId, tenantIds);
        }
        Integer totalCount = baseMapper.selectCount(totalWrap);
        Integer normalCount = baseMapper.selectCount(normalWrap);
        Integer forbiddenCount = baseMapper.selectCount(forbiddenWrap);
        statistic.put("total", totalCount);
        statistic.put("normal", normalCount);
        statistic.put("forbiddenCount", forbiddenCount);
        return statistic;
    }

    @Override
    public Tenant copyTenant(TenantCopyDTO tenantCopyDTO) {
        // 校验项目域名是否重复
        boolean hasDomain = checkDomain(tenantCopyDTO.getDomainName());
        BizAssert.isFalse(hasDomain, "域名已经被使用");

        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(tenantCopyDTO, tenant);
        tenant.setWxStatus(Tenant.WX_STATUS_NO);
        tenant.setStatus(TenantStatusEnum.FORBIDDEN);
        tenant.setStatusSort(TenantStatusEnum.FORBIDDEN.getSort());
        tenant.setType(TenantTypeEnum.CREATE);
        tenant.setReadonly(false);
        // 生成租户编码
        String code = SequenceNumUtil.incrSequenceNum(SequenceEnum.TENANT_CODE, 4);
        tenant.setCode(code);
        int insert = baseMapper.insert(tenant);
        BaseContextHandler.setTenant(tenant.getCode());
        if (insert > 0) {
            createUserTenantInfo(tenant.getId());
            // 初始化项目数据
            initSystemContext.initConnect(TenantConnectDTO.builder()
                    .id(tenant.getId()).tenant(code)
                    .domain(tenant.getDomainName()).name(tenant.getName())
                    .build());
            // 复制项目数据
            String fromTenantCode = tenantCopyDTO.getFromTenantCode();
            tenantCopyUtil.copy(fromTenantCode, code);
            return tenant;
        }
        return tenant;
    }

    /**
     * @title 创建或者复制项目时， 设置客户和用户的关联关系。
     * @author 杨帅
     * @updateTime 2023/4/12 15:54
     * @throws
     */
    public void createUserTenantInfo(Long tenantId) {
        String userType = BaseContextHandler.getUserType();
        if (UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            GlobalUserTenant globalUserTenant = new GlobalUserTenant();
            globalUserTenant.setAccountId(BaseContextHandler.getUserId());
            globalUserTenant.setTenantId(tenantId);
            globalUserTenant.setManagementType(GlobalUserTenant.CREATED_PROJECTS);
            globalUserTenantMapper.insert(globalUserTenant);
        }

    }

    @Override
    public List<Tenant> checkWxAppIdStatus(String wxAppId, String tenantCode) {

        LbqWrapper<Tenant> wrapper = Wraps.<Tenant>lbQ().eq(Tenant::getWxAppId, wxAppId)
                .ne(Tenant::getCode, tenantCode);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public void clearWxAppId(String wxAppId) {

        List<Tenant> tenants = baseMapper.selectList(Wraps.<Tenant>lbQ().eq(Tenant::getWxAppId, wxAppId));
        if (CollUtil.isNotEmpty(tenants)) {
            for (Tenant tenant : tenants) {
                tenant.setWxAppId("");
                tenant.setDoctorQrUrl(null);
                tenant.setDoctorShareQrUrl(null);
                tenant.setAssistantQrUrl(null);
                tenant.setWxBindErrorMessage("公众号取消授权");
                tenant.setWxStatus(Tenant.WX_STATUS_NO);
                baseMapper.updateAllById(tenant);
                removeRedisByCode(tenant.getCode());
            }
        }
    }

    @Override
    public TenantAiInfoDTO queryAiInfo(String tenant) {
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(Tenant.TENANT_AI_REDIS_INFO_KEY);
        Object value = hashOperations.get(tenant);
        if (value == null) {
            Tenant one = baseMapper.selectOne(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenant)
                    .select(SuperEntity::getId, Tenant::getAiAssistantImage, Tenant::getAiAssistantName)
                    .last(" limit 0,1 "));
            if (Objects.nonNull(one)) {
                TenantAiInfoDTO infoDTO = TenantAiInfoDTO.builder()
                        .aiAssistantImage(one.getAiAssistantImage())
                        .aiAssistantName(one.getAiAssistantName())
                        .tenantCode(tenant)
                        .build();
                hashOperations.put(tenant, JSON.toJSONString(infoDTO));
                return infoDTO;
            } else {
                throw new BizException("项目不存在");
            }
        } else {
            return JSON.parseObject(value.toString(), TenantAiInfoDTO.class);
        }
    }

    @Override
    public void updateAiInfo(TenantAiInfoDTO aiInfoDTO) {
        Tenant tenant = getByCode(aiInfoDTO.getTenantCode());
        tenant.setAiAssistantImage(aiInfoDTO.getAiAssistantImage());
        tenant.setAiAssistantName(aiInfoDTO.getAiAssistantName());
        baseMapper.updateById(tenant);
        removeRedisByCode(aiInfoDTO.getTenantCode());
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(Tenant.TENANT_AI_REDIS_INFO_KEY);
        hashOperations.delete(aiInfoDTO.getTenantCode());
    }

    /**
     * @title 创建一个项目端可以免登陆的url
     * @author 杨帅
     * @updateTime 2023/4/12 16:07
     * @throws
     */
    @Override
    public String createTenantLoginUrl(String tenantCode) {

        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            throw new BizException("请登录");
        }
        Tenant tenant = getByCode(tenantCode);
        // http://kailing.domain/#/login
        StringBuilder stringBuilder = new StringBuilder();
        String tenantDomain = ApplicationDomainUtil.adminUrl(tenant.getDomainName());
        StringBuilder builder = stringBuilder.append(tenantDomain).append("/#/login?tempToken=");

        String key = userId + "D" + new Date().getTime() + "login";
        builder.append(key);
        redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_LOGIN)
                .put(key, tenantCode);
        return builder.toString();
    }


    @Override
    public TenantBaseInfo queryTenantBaseInfo(RegGuide regGuide, String tenantCode) {
        Tenant tenant = getByCode(tenantCode);
        TenantBaseInfo tenantBaseInfo = new TenantBaseInfo();
        tenantBaseInfo.setTenantId(tenant.getId());
        tenantBaseInfo.setTenantCode(tenant.getCode());
        tenantBaseInfo.setName(tenant.getName());
        tenantBaseInfo.setDomainName(tenant.getDomainName());
        tenantBaseInfo.setLogo(tenant.getLogo());
        tenantBaseInfo.setProjectManageLink(ApplicationDomainUtil.adminUrl(tenant.getDomainName()));
        if (Objects.nonNull(regGuide)) {
            tenantBaseInfo.setGuideId(regGuide.getId());
            tenantBaseInfo.setIcon(StrUtil.isEmpty(regGuide.getIcon()) ? tenant.getLogo() : regGuide.getIcon());
            tenantBaseInfo.setAgreement(regGuide.getAgreement());
            tenantBaseInfo.setDescribe(regGuide.getDescribe());
            tenantBaseInfo.setGuide(regGuide.getGuide());
            tenantBaseInfo.setEnableIntro(regGuide.getEnableIntro());
            tenantBaseInfo.setIntro(regGuide.getIntro());
            tenantBaseInfo.setDoctorAgreement(regGuide.getDoctorAgreement());
            tenantBaseInfo.setSuccessMsg(regGuide.getSuccessMsg());
        }
        BaseContextHandler.setTenant(tenantCode);
        List<LibraryTenant> libraryTenants = libraryTenantMapper.selectList(Wraps.lbQ());
        tenantBaseInfo.setLibraryTenants(libraryTenants);
        return tenantBaseInfo;
    }
}
