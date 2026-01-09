package com.caring.sass.tenant.service.router.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dao.router.H5CoreFunctionsMapper;
import com.caring.sass.tenant.dao.router.H5RouterMapper;
import com.caring.sass.tenant.dao.router.H5UiMapper;
import com.caring.sass.tenant.dto.router.H5RouterPatientDto;
import com.caring.sass.tenant.entity.AppConfig;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.entity.router.H5Ui;
import com.caring.sass.tenant.entity.sys.Dict;
import com.caring.sass.tenant.entity.sys.DictItem;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.tenant.service.router.H5RouterService;
import com.caring.sass.tenant.service.sys.DictItemService;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.entity.guide.RegGuide;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者路由
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Service

public class H5RouterServiceImpl extends SuperServiceImpl<H5RouterMapper, H5Router> implements H5RouterService {

    private final DictItemService dictItemService;

    private final AppConfigService appConfigService;

    private final TenantMapper tenantMapper;

    private final H5UiMapper h5UiMapper;

    private final H5CoreFunctionsMapper h5CoreFunctionsMapper;
    /**
     * 预约管理
     */
    private static String NURSING_BOOKING_MANAGEMENT = "NURSING_BOOKING_MANAGEMENT";
    /**
     * 病例讨论
     */
    private static String NURSING_CASE_DISCUSSION = "NURSING_CASE_DISCUSSION";
    /**
     * 患者转诊
     */
    private static String NURSING_PATIENT_REFERRAL = "NURSING_PATIENT_REFERRAL";
    /**
     * 患者管理平台
     */
    private static String NURSING_PATIENT_MANAGE_PLATFORM = "NURSING_PATIENT_MANAGE_PLATFORM";

    public H5RouterServiceImpl(DictItemService dictItemService,
                               TenantMapper tenantMapper,
                               H5CoreFunctionsMapper h5CoreFunctionsMapper,
                               H5UiMapper h5UiMapper,
                               AppConfigService appConfigService) {
        this.dictItemService = dictItemService;
        this.appConfigService = appConfigService;
        this.tenantMapper = tenantMapper;
        this.h5CoreFunctionsMapper = h5CoreFunctionsMapper;
        this.h5UiMapper = h5UiMapper;
    }

    @Override
    public List<H5Router> createIfNotDoctorExist() {
        List<H5Router> routers = super.list(Wraps.<H5Router>lbQ(H5Router.builder().userType(UserType.DOCTOR).build()).orderByAsc(H5Router::getSortValue));
        if (CollUtil.isNotEmpty(routers)) {
            return routers;
        }
        List<DictItem> dictItems = dictItemService.list(Wraps.<DictItem>lbQ()
                .eq(DictItem::getDictionaryType, "H5_DOCTOR_ROUTER_TYPE")
                .orderByAsc(DictItem::getSortValue));
        routers = createIfNotExist(dictItems, UserType.DOCTOR);
        return routers;
    }

    /**
     * 初始化所有项目的 app菜单
     *
     */
    @Override
    public void initNursingAppMenu() {
        List<Tenant> tenantList = tenantMapper.selectList(Wraps.<Tenant>lbQ());
        for (Tenant tenant : tenantList) {
            String code = tenant.getCode();
            BaseContextHandler.setTenant(code);
            createIfNotNursingExist();
        }
    }

    @Override
    public void initFollowUpMenu() {
        List<Tenant> tenantList = tenantMapper.selectList(Wraps.<Tenant>lbQ());
        String nursingFollowUp = "NURSING_FOLLOW_UP";
        String doctorFollowUp = "DOCTOR_FOLLOW_UP";
        String followUp = "FOLLOW_UP";
        List<String> codes = new ArrayList<>();
        codes.add(nursingFollowUp);
        codes.add(doctorFollowUp);
        codes.add(followUp);
        List<DictItem> dictItemList = dictItemService.list(Wraps.<DictItem>lbQ().in(DictItem::getCode, codes));
        if (CollUtil.isEmpty(dictItemList)) {
            return;
        }
        Map<String, DictItem> dictItemMap = dictItemList.stream().collect(Collectors.toMap(DictItem::getCode, item -> item, (o1, o2) -> o2));
        DictItem nursingFollow = dictItemMap.get(nursingFollowUp);
        DictItem doctorFollow = dictItemMap.get(doctorFollowUp);
        DictItem patientFollow = dictItemMap.get(followUp);
        Map<String, DictItem> map = new HashMap<>();
        for (Tenant tenant : tenantList) {
            String code = tenant.getCode();
            BaseContextHandler.setTenant(code);
            List<H5Router> h5Routers = new ArrayList<>();
            // 检查医助的随访菜单是否设置
            if (Objects.nonNull(nursingFollow)) {
                Integer integer = baseMapper.selectCount(Wraps.<H5Router>lbQ().eq(H5Router::getDictItemId, nursingFollow.getId()));
                if (integer == null || integer == 0) {
                    H5Router router = H5Router.builder()
                            .path(nursingFollow.getAttr1())
                            .iconUrl(nursingFollow.getDescribe())
                            .dictItemId(nursingFollow.getId())
                            .dictItemName(nursingFollow.getName())
                            .dictItemType(nursingFollow.getCode())
                            .name(nursingFollow.getName())
                            .sortValue(nursingFollow.getSortValue())
                            .status(false)
                            .patientMenuDoctorStatus(false)
                            .patientMenuNursingStatus(false)
                            .banDelete(true)
                            .userType(UserType.NURSING_STAFF)
                            .build();
                    h5Routers.add(router);
                }
            }
            // 检查医生的随访菜单是否设置
            if (Objects.nonNull(doctorFollow)) {
                Integer integer = baseMapper.selectCount(Wraps.<H5Router>lbQ().eq(H5Router::getDictItemId, doctorFollow.getId()));
                if (integer == null || integer == 0) {
                    H5Router router = H5Router.builder()
                            .path(doctorFollow.getAttr1())
                            .iconUrl(doctorFollow.getDescribe())
                            .dictItemId(doctorFollow.getId())
                            .dictItemName(doctorFollow.getName())
                            .dictItemType(doctorFollow.getCode())
                            .name(doctorFollow.getName())
                            .sortValue(doctorFollow.getSortValue())
                            .status(false)
                            .patientMenuDoctorStatus(false)
                            .patientMenuNursingStatus(false)
                            .banDelete(true)
                            .userType(UserType.DOCTOR)
                            .build();
                    h5Routers.add(router);
                }
            }

            // 检查患者的随访菜单是否设置
            if (Objects.nonNull(patientFollow)) {
                Integer integer = baseMapper.selectCount(Wraps.<H5Router>lbQ().eq(H5Router::getDictItemId, patientFollow.getId()));
                if (integer == null || integer == 0) {
                    H5Router router = H5Router.builder()
                            .path(patientFollow.getAttr1())
                            .iconUrl(patientFollow.getDescribe())
                            .dictItemId(patientFollow.getId())
                            .dictItemName(patientFollow.getName())
                            .dictItemType(patientFollow.getCode())
                            .name(patientFollow.getName())
                            .sortValue(patientFollow.getSortValue())
                            .status(false)
                            .patientMenuDoctorStatus(false)
                            .patientMenuNursingStatus(false)
                            .banDelete(true)
                            .userType(UserType.PATIENT)
                            .build();
                    h5Routers.add(router);
                }
            }
            if (CollUtil.isNotEmpty(h5Routers)) {
                baseMapper.insertBatchSomeColumn(h5Routers);
            }
        }

    }

    /**
     * 给app的菜单返回项目域名
     * @param routers
     */
    // TODO: 未知的用途。
    private void setTenantDomain(List<H5Router> routers) {
        if (CollUtil.isEmpty(routers)) {
            return;
        }
        String code = BaseContextHandler.getTenant();
        Tenant tenant = tenantMapper.getByCode(code);
        if (Objects.isNull(tenant)) {
            return;
        }
        String tenantDomain = ApplicationDomainUtil.tenantDomain(tenant.getDomainName());
        for (H5Router router : routers) {
            router.setTenantDomain(tenantDomain);
        }
    }

    /**
     * 根据医助每个类型的菜单
     * @param dictItemType
     * @return
     */
    @Override
    public H5Router getNursingH5RouterByModuleType(String dictItemType) {

        H5Router h5Router = baseMapper.selectOne(Wraps.<H5Router>lbQ().eq(H5Router::getUserType, UserType.NURSING_STAFF).eq(H5Router::getDictItemType, dictItemType).last("limit 0,1"));
        if (Objects.isNull(h5Router)) {
            List<H5Router> h5RouterList = createIfNotNursingExist();
            for (H5Router router : h5RouterList) {
                if (dictItemType.equals(router.getDictItemType())) {
                    h5Router = router;
                    break;
                }
            }
        }
        return h5Router;
    }

    /**
     * 检查文件夹的分享链接是否被使用到菜单中
     * @param url
     * @return
     */
    @Override
    public List<String> checkFolderShareUrlExist(String url) {
        String shareUrl = null;
        try {
            shareUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        List<String> tenantCodes = new ArrayList<>();
        List<H5Router> h5Routers = baseMapper.selectMenuLikeUrl("%"+ shareUrl+ "%");
        if (CollUtil.isNotEmpty(h5Routers)) {
            for (H5Router router : h5Routers) {
                String tenantCode = router.getTenantCode();
                tenantCodes.add(tenantCode);
            }
        }
        return tenantCodes;
    }

    /**
     * 查询或者初始化医助的菜单
     *
     * @return
     */
    @Override
    public List<H5Router> createIfNotNursingExist() {
        List<H5Router> routers = super.list(Wraps.<H5Router>lbQ().eq(H5Router::getUserType, UserType.NURSING_STAFF).orderByAsc(H5Router::getSortValue));
        if (CollUtil.isNotEmpty(routers)) {
            setTenantDomain(routers);
            return routers;
        }
        List<DictItem> dictItems = dictItemService.list(Wraps.<DictItem>lbQ()
                .eq(DictItem::getDictionaryType, "H5_NURSING_ROUTER_TYPE")
                .orderByAsc(DictItem::getSortValue));
        // 查询app的菜单配置。根据菜单配置初始化菜单。
        LbqWrapper<AppConfig> last = Wraps.<AppConfig>lbQ().last(" limit 0,1 ");
        AppConfig appConfig = appConfigService.getOne(last);
        for (DictItem item : dictItems) {
            boolean status = true;
            if (NURSING_BOOKING_MANAGEMENT.equals(item.getCode()) && Objects.nonNull(appConfig)) {
                Integer aSwitch = appConfig.getAppointmentSwitch();
                if (aSwitch != null && 1 == aSwitch) {
                    status = false;
                }
            }
            if (NURSING_CASE_DISCUSSION.equals(item.getCode()) && Objects.nonNull(appConfig)) {
                Integer aSwitch = appConfig.getConsultationSwitch();
                if (aSwitch != null && 1 == aSwitch) {
                    status = false;
                }
            }
            if (NURSING_PATIENT_REFERRAL.equals(item.getCode()) && Objects.nonNull(appConfig)) {
                Integer aSwitch = appConfig.getReferralSwitch();
                if (aSwitch != null && 1 == aSwitch) {
                    status = false;
                }
            }
            if (NURSING_PATIENT_MANAGE_PLATFORM.equals(item.getCode())) {
                status = false;
                if ( Objects.nonNull(appConfig)) {
                    Integer aSwitch = appConfig.getPatientManageSwitch();
                    if (aSwitch != null && 0 == aSwitch) {
                        status = true;
                    }
                }
            }
            H5Router router = H5Router.builder()
                    .path(item.getAttr1())
                    .iconUrl(item.getDescribe())
                    .dictItemId(item.getId())
                    .dictItemName(item.getName())
                    .dictItemType(item.getCode())
                    .name(item.getName())
                    .sortValue(item.getSortValue())
                    .status(status)
                    .patientMenuDoctorStatus(false)
                    .patientMenuNursingStatus(false)
                    .banDelete(true)
                    .userType(UserType.NURSING_STAFF)
                    .build();
            routers.add(router);
        }
        super.saveBatch(routers);
        setTenantDomain(routers);
        return routers;
    }

    @Override
    public void copy(String fromTenantCode, String toTenantCode) {
        BaseContextHandler.setTenant(fromTenantCode);
        List<H5Router> h5Routers = baseMapper.selectList(Wraps.<H5Router>lbQ());
        H5CoreFunctions h5CoreFunctions = h5CoreFunctionsMapper.selectOne(Wraps.<H5CoreFunctions>lbQ().last("limit 0,1"));
        if (Objects.nonNull(h5CoreFunctions)) {
            BaseContextHandler.setTenant(toTenantCode);
            h5CoreFunctions.setId(null);
            BaseContextHandler.setTenant(fromTenantCode);
        }
        if (CollUtil.isEmpty(h5Routers)) {
            return;
        }
        for (H5Router h5Router : h5Routers) {
            h5Router.setId(null);
        }
        BaseContextHandler.setTenant(toTenantCode);
        baseMapper.insertBatchSomeColumn(h5Routers);
        BaseContextHandler.setTenant(fromTenantCode);
    }

    public List<H5Router> createIfNotExist(List<DictItem> dictItems, String userType) {
        /*code值为路径，name为类型名，describe为路径 */
        List<String> doctorNoSee = new ArrayList<>();
        doctorNoSee.add("PATIENT_GET_NEW");     // 患者拉新
        doctorNoSee.add("MY_DOCTOR");   // 我的医生
        doctorNoSee.add("RESERVATION_INDEX");   // 在线预约
        doctorNoSee.add("FOLLOW_UP");   // 我的随访
        doctorNoSee.add("Applets");   // 小程序
        doctorNoSee.add("CMS_INDEX");   // 小程序
        List<H5Router> routers = new ArrayList<>(dictItems.size());
        for (DictItem item : dictItems) {
            if (Objects.equals(item.getCode(), "OTHER") || Objects.equals(item.getCode(), "CUSTOM_FOLLOW_UP") ||
                    Objects.equals(item.getCode(), "Applets")) {
                continue;
            }
            H5Router router = H5Router.builder()
                    .dictItemId(item.getId())
                    .dictItemName(item.getName())
                    .dictItemType(item.getCode())
                    .name(item.getName())
                    .sortValue(item.getSortValue())
                    .status(true)
                    .patientMenuDoctorStatus(true)
                    .patientMenuNursingStatus(true)
                    .banDelete(true)
                    .userType(userType)
                    .build();
            // 如果是患者个人中心。 根据功能类型的code。对菜单进行分类分组。在线咨询不保存为菜单
            if (userType.equals(UserType.PATIENT)) {
                if (doctorNoSee.contains(item.getCode())) {
                    router.setPatientMenuNursingStatus(false);
                    router.setPatientMenuDoctorStatus(false);
                }
                RouterModuleTypeEnum moduleTypeEnum = RouterModuleTypeEnum.matchEnum(item.getCode());
                if (moduleTypeEnum == null) {
                    continue;
                }
                router.setModuleType(moduleTypeEnum);
            }
            fillAttr(router, item.getDescribe());
            routers.add(router);
        }
        super.saveBatch(routers);
        return routers;
    }

    @Override
    public List<H5Router> patientRouter(RouterModuleTypeEnum moduleType, String userType) {
        LbqWrapper<H5Router> wrapper = Wraps.<H5Router>lbQ().eq(H5Router::getModuleType, moduleType).eq(H5Router::getUserType, UserType.PATIENT);
        if (UserType.PATIENT.equals(userType)) {
            wrapper.eq(H5Router::getStatus, true);
        } else if (UserType.DOCTOR.equals(userType)) {
            wrapper.eq(H5Router::getPatientMenuDoctorStatus, true);
        } else if (UserType.NURSING_STAFF.equals(userType)) {
            wrapper.eq(H5Router::getPatientMenuNursingStatus, true);
        }
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<H5Router> getPatientJoinGroupAfterMenu() {
        // 查询 疾病信息。 药箱，健康日志，检查数据 检验数据
        LbqWrapper<H5Router> wrapper = Wraps.<H5Router>lbQ().eq(H5Router::getUserType, UserType.PATIENT);
        wrapper.eq(H5Router::getStatus, true);
        wrapper.in(H5Router::getDictItemType, "HEALTH", "TEST_NUMBER", "MONITOR", "HEALTH_CALENDAR", "MEDICINE");
        return baseMapper.selectList(wrapper);
    }

    /**
     * 更新 个人中心的 基本信息，核心功能， 我的功能， 我的档案， 个人中心背景图
     * @param h5RouterPatientDto
     */
    @Transactional
    @Override
    public void patientRouter(H5RouterPatientDto h5RouterPatientDto) {

        // 保存核心功能
        H5CoreFunctions h5CoreFunctions = h5RouterPatientDto.getH5CoreFunctions();
        h5CoreFunctionsMapper.updateById(h5CoreFunctions);

        // 更新患者个人中心背景图
        H5Ui h5UiImage = h5RouterPatientDto.getH5UiImage();
        if (h5UiImage.getId() != null) {
            h5UiMapper.updateById(h5UiImage);
        } else {
            h5UiMapper.insert(h5UiImage);
        }

        // 清除患者个人中心的 所有的菜单。 然后重新保存  在线咨询菜单不删除
        baseMapper.delete(Wraps.<H5Router>lbQ().eq(H5Router::getUserType, UserType.PATIENT));
        H5Router patientBaseInfo = h5RouterPatientDto.getPatientBaseInfo();
        patientBaseInfo.setId(null);
        baseMapper.insert(patientBaseInfo);

        List<H5Router> routerList = new ArrayList<>();
        List<H5Router> myFeatures = h5RouterPatientDto.getPatientMyFeatures();
        if (CollUtil.isNotEmpty(myFeatures)) {
            myFeatures.forEach(item -> item.setId(null));
            routerList.addAll(myFeatures);
        }
        List<H5Router> patientMyFile = h5RouterPatientDto.getPatientMyFile();
        if (CollUtil.isNotEmpty(patientMyFile)) {
            patientMyFile.forEach(item -> item.setId(null));
            routerList.addAll(patientMyFile);
        }
        if (CollUtil.isNotEmpty(routerList)) {
            baseMapper.insertBatchSomeColumn(routerList);
        }
    }

    /**
     * 增加医助 医生 可见条件
     * @param currentUserType
     * @return
     */
    @Override
    public H5RouterPatientDto patientRouter(String currentUserType) {
        List<H5Router> routerList = new ArrayList<>();
        LbqWrapper<H5Router> wrapper = Wraps.<H5Router>lbQ().eq(H5Router::getUserType, UserType.PATIENT)
                .orderByAsc(H5Router::getSortValue);
        if (UserType.NURSING_STAFF.equals(currentUserType)) {
            wrapper.eq(H5Router::getPatientMenuNursingStatus, true);
        }
        if (UserType.DOCTOR.equals(currentUserType)) {
            wrapper.eq(H5Router::getPatientMenuDoctorStatus, true);
        }
        if (UserType.PATIENT.equals(currentUserType)) {
            wrapper.eq(H5Router::getStatus, true);
        }
        routerList = baseMapper.selectList(wrapper);
        return getH5RouterPatientDto(routerList);
    }

    /**
     * 根据患者的个人中心菜单配置和背景图生成个人中心数据配置
     * @param routerList
     * @return
     */
    private H5RouterPatientDto getH5RouterPatientDto(List<H5Router> routerList) {
        // 根据业务情况和数据库字典数据。规定H5Router的dictItemId符合某数值时为特定的字典
        H5RouterPatientDto h5RouterPatientDto = new H5RouterPatientDto();
        H5Router imMenu = null;
        if (CollUtil.isNotEmpty(routerList)) {
            for (H5Router h5Router : routerList) {
                if (RouterModuleTypeEnum.BASE_INFO.eq(h5Router.getModuleType())) {
                    h5RouterPatientDto.setPatientBaseInfo(h5Router);
                } else if (RouterModuleTypeEnum.MY_FEATURES.eq(h5Router.getModuleType())) {
                    h5RouterPatientDto.addPatientMyFeatures(h5Router);
                } else if (RouterModuleTypeEnum.MY_FILE.eq(h5Router.getModuleType())) {
                    h5RouterPatientDto.addPatientMyFile(h5Router);
                }
                if ("IM_INDEX".equals(h5Router.getDictItemType())) {
                    imMenu = h5Router;
                }
            }
        }
        h5RouterPatientDto.sortRouter();
        LbqWrapper<H5CoreFunctions> last = Wraps.<H5CoreFunctions>lbQ().last(" limit 0,1 ");
        H5CoreFunctions coreFunctions = h5CoreFunctionsMapper.selectOne(last);
        if (coreFunctions == null) {
            if (imMenu == null) {
                coreFunctions = H5CoreFunctions.init(null);
            } else {
                coreFunctions = H5CoreFunctions.init(imMenu.getStatus());
            }
            h5CoreFunctionsMapper.insert(coreFunctions);
        }
        h5RouterPatientDto.setH5CoreFunctions(coreFunctions);

        // 查询 中心背景图
        H5Ui ucenterBg = h5UiMapper.selectOne(Wraps.<H5Ui>lbQ().eq(H5Ui::getCode, "UCENTER_BG").last("limit 0,1"));
        h5RouterPatientDto.setH5UiImage(ucenterBg);
        return h5RouterPatientDto;
    }

    /**
     * 查询个人中心的 基本信息， 核心功能，我的功能，我的档案 个人中心背景图。
     */
    @Override
    public H5RouterPatientDto patientRouter() {
        List<H5Router> routerList = createIfNotExist();
        return getH5RouterPatientDto(routerList);
    }



    @Override
    public List<H5Router> createIfNotExist() {
        List<H5Router> routers = super.list(Wraps.<H5Router>lbQ(H5Router.builder().userType(UserType.PATIENT).build()).orderByAsc(H5Router::getSortValue));
        if (CollUtil.isNotEmpty(routers)) {
            return routers;
        }
        List<DictItem> dictItems = dictItemService.list(Wraps.<DictItem>lbQ()
                .eq(DictItem::getDictionaryType, "H5_ROUTER_TYPE")
                .orderByAsc(DictItem::getSortValue));
        routers = createIfNotExist(dictItems, UserType.PATIENT);
        return routers;
    }

    private void fillAttr(H5Router h5Router, String attr) {
        String path = "", iconUrl = "";
        if (StrUtil.isNotBlank(attr)) {
            if (attr.contains(",")) {
                String[] array = attr.split(",");
                path = array[0];
                iconUrl = array[1];
            } else {
                path = attr;
            }
        }
        h5Router.setPath(path);
        h5Router.setIconUrl(iconUrl);
    }
}
