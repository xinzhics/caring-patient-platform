package com.caring.sass.tenant.strategy.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.caring.sass.authority.entity.auth.*;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.entity.common.Parameter;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.enumeration.auth.ApplicationAppTypeEnum;
import com.caring.sass.authority.enumeration.auth.AuthorizeType;
import com.caring.sass.authority.enumeration.auth.Sex;
import com.caring.sass.authority.service.auth.*;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.authority.service.common.DictionaryService;
import com.caring.sass.authority.service.common.ParameterService;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.cms.ChannelApi;
import com.caring.sass.common.constant.ParameterKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.auth.DataScopeType;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.msgs.api.SmsTemplateApi;
import com.caring.sass.nursing.api.PlanApi;
import com.caring.sass.nursing.api.StatisticsTaskApi;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.sms.dto.SmsTemplateSaveDTO;
import com.caring.sass.sms.enumeration.ProviderType;
import com.caring.sass.tenant.dao.InitDbMapper;
import com.caring.sass.tenant.dto.TenantConnectDTO;
import com.caring.sass.tenant.service.router.H5UiService;
import com.caring.sass.tenant.service.router.PatientManageMenuService;
import com.caring.sass.tenant.strategy.InitSystemStrategy;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.TemplateMsgApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 初始化规则:
 * 手动创建数据
 *
 * @author caring
 * @date 2020年04月05日13:14:28
 */
@Service("COLUMN")
@Slf4j
public class ColumnInitSystemStrategy implements InitSystemStrategy {
    private static final String UCENTER_GROUP = "ucenter:group";
    private static final String UCENTER_DOCTOR = "ucenter:doctor";
    private static final String UCENTER_NURSINGSTAFF = "ucenter:nursingstaff";
    private static final String UCENTER_PATIENT = "ucenter:patient";
    private static final String CMS_CHANNEL = "cms:channel";
    private static final String CMS_SITE = "cms:site";
    private static final String CMS_CHANNEL_CONTENT = "cms:channelcontent";

    private static final String ORG = "org";
    private static final String HOME = "home";
    private static final String STATION = "station";
    private static final String USER = "user";
    private static final String MENU = "menu";
    private static final String RESOURCE = "resource";
    private static final String ROLE = "role";
    private static final String DICT = "dict";
    private static final String AREA = "area";
    private static final String PARAMETER = "parameter";
    private static final String APPLICATION = "application";
    private static final String DB = "db";

    @Autowired
    private MenuService menuService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private DictionaryItemService dictionaryItemService;
    @Autowired
    private ParameterService parameterService;
    @Autowired
    private DatabaseProperties databaseProperties;
    @Autowired
    private UserService userService;

    @Autowired
    private ChannelApi channelApi;

    @Autowired
    private OrgService orgService;

    @Autowired
    private NursingStaffApi nursingStaffApi;

    @Autowired
    private PlanApi planApi;

    @Autowired
    private TemplateMsgApi templateMsgApi;

    @Autowired
    private InitDbMapper initDbMapper;

    @Autowired
    private SmsTemplateApi smsTemplateApi;

    @Autowired
    private StatisticsTaskApi statisticsTaskApi;

    @Autowired
    private GuideApi guideApi;

    @Autowired
    H5UiService h5UiService;

    /**
     * 我*，这种方式太脑残了，但想不出更好的方式初始化数据，希望各位大神有好的初始化方法记得跟我说声！！！
     * 写这段代码写得想去si ~~~
     * <p>
     * 不能用 SCHEMA 模式的初始化脚本方法： 因为id 会重复，租户编码会重复！
     *
     * @param tenantConnect 待初始化租户编码
     * @return
     */
    @Override
    public boolean initConnect(TenantConnectDTO tenantConnect) {
        String domain = tenantConnect.getDomain();
        String name = tenantConnect.getName();
        BizAssert.notEmpty(domain);
        BizAssert.notEmpty(name);

        String tenant = tenantConnect.getTenant();
        // 初始化数据
        //1, 生成并关联 ID TENANT
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        BaseContextHandler.setTenant(tenant);

        // 菜单 资源 角色 角色_资源 字典 参数
        List<Menu> menuList = new ArrayList<>();
        Map<String, Long> menuMap = new HashMap<>();
        boolean menuFlag = initMenu(snowflake, menuList, menuMap);

        List<Resource> resourceList = new ArrayList<>();
        boolean resourceFlag = initResource(resourceList, menuMap);

        // 角色
        Long roleId = snowflake.nextId();
        boolean roleFlag = initRole(roleId);

        // 资源权限
        boolean roleAuthorityFlag = initRoleAuthority(menuList, resourceList, roleId);

        // 字典
         initDict();

        //参数
        initParameter();

        initApplication();

        // 项目管理员
        initUser();

        initOrg(name, domain);

        initSmsTemplate(tenantConnect);
        return menuFlag && resourceFlag && roleFlag && roleAuthorityFlag;
    }

    public boolean initSmsTemplate(TenantConnectDTO tenantConnect) {
        smsTemplateApi.save(SmsTemplateSaveDTO
                .builder()
                .appId(System.getenv().getOrDefault("ALIYUN_SMS_ACCESS_KEY_ID", ""))
                .appSecret(System.getenv().getOrDefault("ALIYUN_SMS_ACCESS_KEY_SECRET", ""))
                .url("http://service.winic.org/sys_port/gateway/index.asp")
                .customCode("COMMON_SMS").name("阿里云短信验证码").content("您的验证码是${code},请于5分钟内正确输入!").templateCode("SMS_488435144")
                .signName("卡柠科技").providerType(ProviderType.ALI)
                .templateDescribe("吉信通短信验证码")
                .build());
        return true;
    }

    @Override
    public boolean initConnectWithAdditional(TenantConnectDTO tenantConnect) {
        boolean su = initConnect(tenantConnect);
        initAdditional(tenantConnect);
        return su;
    }

    @Autowired
    PatientManageMenuService patientManageMenuService;

    /**
     * 初始化项目的机构、栏目、模板消息, 项目统计 数据
     */
    private void initAdditional(TenantConnectDTO tenantConnectDTO) {
        String tenantName = tenantConnectDTO.getName();
        R<Boolean> channel = channelApi.initChannel();
        if (channel.getIsError()) {
            log.error("项目[{}]初始化栏目失败", tenantName);
        }
        R<Boolean> initMsg = templateMsgApi.initMsg();
        if (initMsg.getIsError()) {
            log.error("项目[{}]初始化微信模板消息失败", tenantName);
        }
        R<Boolean> initPlan = planApi.createProjectInitPlan();
        if (initPlan.getIsError()) {
            log.error("项目[{}]初始化护理计划失败", tenantName);
        }
        R<String> statistics = statisticsTaskApi.initTenantDefaultMaster();
        if (statistics.getIsError()) {
            log.error("项目[{}]初始化默认的统计主图", tenantName);
        }
        String tenant = BaseContextHandler.getTenant();
        R<String> initGuide = guideApi.initGuide(tenant);
        if (initGuide.getIsError()) {
            log.error("项目[{}]初始化默认的注册引导失败", tenantName);
        }
        h5UiService.resetUI();
        patientManageMenuService.createMenu(tenant);

    }



    private boolean initApplication() {
        List<Application> list = new ArrayList<>();
        list.add(Application.builder().clientId("caring_ui").clientSecret("caring_ui_secret").website("http://tangyh.top:10000/caring-ui/")
                .name("Sass微服务管理后台").appType(ApplicationAppTypeEnum.PC).tenantCode(BaseContextHandler.getTenant()).status(true).build());
        list.add(Application.builder().clientId("caring_admin_ui").clientSecret("caring_admin_ui_secret").website("http://tangyh.top:180/caring-admin-ui/")
                .name("Sass微服务管理后台").appType(ApplicationAppTypeEnum.PC).tenantCode(BaseContextHandler.getTenant()).status(true).build());
        return applicationService.saveBatch(list);
    }

    private boolean initOrg(String tenantName, String tenantDomain) {
        Org org = new Org().setLabel(tenantName).setAbbreviation(tenantName).setReadonly(true)
                .setStatus(true).setDescribe(tenantName);
        orgService.save(org);
        if (org != null) {
            R<Boolean> defaultUser = nursingStaffApi.createDefaultUser(org, tenantDomain, tenantName);
            if (defaultUser.getIsError()) {
                log.error("项目[{}]初始化护理医助、小组、医生失败", tenantName);
            }
        }
        return true;
    }

    private boolean initUser() {
        User user = User.builder()
                .account(User.ACCOUNT_ADMIN).name("项目管理员").password(User.ACCOUNT_PASSWORD)
                .readonly(true).sex(Sex.M).avatar("cnrhVkzwxjPwAaCfPbdc.png")
                .status(true).passwordErrorNum(0)
                .build();
        return userService.initUser(user);
    }

    private boolean initParameter() {
        List<Parameter> list = new ArrayList<>();
        list.add(Parameter.builder().key(ParameterKey.LOGIN_POLICY).name("登录策略").value(ParameterKey.LoginPolicy.MANY.name()).describe("ONLY_ONE:一个用户只能登录一次; MANY:用户可以任意登录; ONLY_ONE_CLIENT:一个用户在一个应用只能登录一次").status(true).readonly(true).build());
        return parameterService.saveBatch(list);
    }

    private boolean initRoleAuthority(List<Menu> menuList, List<Resource> resourceList, Long roleId) {
        List<RoleAuthority> roleAuthorityList = new ArrayList<>();
        menuList.forEach(item -> {
            roleAuthorityList.add(RoleAuthority.builder().authorityType(AuthorizeType.MENU).authorityId(item.getId()).roleId(roleId).build());
        });
        resourceList.forEach(item -> {
            roleAuthorityList.add(RoleAuthority.builder().authorityType(AuthorizeType.RESOURCE).authorityId(item.getId()).roleId(roleId).build());
        });
        return roleAuthorityService.saveBatch(roleAuthorityList);
    }

    private boolean initRole(Long roleId) {
        Role role = Role.builder().id(roleId).name("平台管理员").code("PT_ADMIN").describe("平台内置管理员").dsType(DataScopeType.ALL).readonly(true).build();
        return roleService.save(role);
    }

    private boolean initResource(List<Resource> resourceList, Map<String, Long> menuMap) {
        Long groupId = menuMap.get(UCENTER_GROUP);
        resourceList.add(Resource.builder().code("group:add").name("新增").menuId(groupId).build());
        resourceList.add(Resource.builder().code("group:update").name("修改").menuId(groupId).build());
        resourceList.add(Resource.builder().code("group:view").name("查看").menuId(groupId).build());
        resourceList.add(Resource.builder().code("group:delete").name("删除").menuId(groupId).build());
        Long doctorId = menuMap.get(UCENTER_DOCTOR);
        resourceList.add(Resource.builder().code("doctor:view").name("查看").menuId(doctorId).build());
        resourceList.add(Resource.builder().code("doctor:delete").name("删除").menuId(doctorId).build());
        Long ucenterNursingStaffId = menuMap.get(UCENTER_NURSINGSTAFF);
        resourceList.add(Resource.builder().code("nursingStaff:view").name("查看").menuId(ucenterNursingStaffId).build());
        resourceList.add(Resource.builder().code("nursingStaff:delete").name("删除").menuId(ucenterNursingStaffId).build());
        Long patientId = menuMap.get(UCENTER_PATIENT);
        resourceList.add(Resource.builder().code("patient:view").name("查看").menuId(patientId).build());
        resourceList.add(Resource.builder().code("patient:delete").name("删除").menuId(patientId).build());

        Long orgId = menuMap.get(ORG);
        resourceList.add(Resource.builder().code("org:add").name("新增").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:delete").name("删除").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:export").name("导出").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:import").name("导入").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:update").name("修改").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:view").name("查看").menuId(orgId).build());

        Long homeId = menuMap.get(HOME);
        if (Objects.nonNull(homeId)) {
            resourceList.add(Resource.builder().code("index:view").name("首页").menuId(homeId).build());
        }

        Long userId = menuMap.get(USER);
        resourceList.add(Resource.builder().code("user:add").name("新增").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:delete").name("删除").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:export").name("导出").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:import").name("导入").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:update").name("修改").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:view").name("查看").menuId(userId).build());

        Long menuId = menuMap.get(MENU);
        resourceList.add(Resource.builder().code("menu:add").name("新增").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:delete").name("删除").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:export").name("导出").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:import").name("导入").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:update").name("修改").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:view").name("查看").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:add").name("添加").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:update").name("修改").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:delete").name("删除").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:view").name("查看").menuId(menuId).build());

        Long roleId = menuMap.get(ROLE);
        resourceList.add(Resource.builder().code("role:add").name("新增").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:delete").name("删除").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:export").name("导出").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:import").name("导入").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:update").name("修改").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:view").name("查看").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:config").name("配置").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:auth").name("授权").menuId(roleId).build());


        Long applicationId = menuMap.get(APPLICATION);
        resourceList.add(Resource.builder().code("application:add").name("新增").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:delete").name("删除").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:export").name("导出").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:update").name("修改").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:view").name("查看").menuId(applicationId).build());


        Long channelContentId = menuMap.get(CMS_CHANNEL_CONTENT);
        resourceList.add(Resource.builder().code("channelContent:delete").name("删除").menuId(channelContentId).build());
        resourceList.add(Resource.builder().code("channelContent:view").name("查看").menuId(channelContentId).build());
        resourceList.add(Resource.builder().code("channelContent:add").name("新增").menuId(channelContentId).build());
        resourceList.add(Resource.builder().code("channelContent:update").name("修改").menuId(channelContentId).build());

        Long siteId = menuMap.get(CMS_SITE);
        resourceList.add(Resource.builder().code("cmsSite:importSystem").name("存为系统模板").menuId(siteId).build());
        return resourceService.saveBatch(resourceList);
    }

    private boolean initMenu(Snowflake snowflake, List<Menu> menuList, Map<String, Long> menuMap) {
        Long ucenterId = snowflake.nextId();
        Long cmsId = snowflake.nextId();
        Long menuUserCenterId = snowflake.nextId();
        Long authId = snowflake.nextId();
        Long weiXinPushId = snowflake.nextId();
        Long aiId = snowflake.nextId();
        Long exportId = snowflake.nextId();
        Long homeId = snowflake.nextId();

        // 1级菜单
        menuList.add(Menu.builder().id(homeId).label("首页").path("/dashboard").component("dashboard/Index").icon("el-icon-s-home").sortValue(0).build());
        menuList.add(Menu.builder().id(menuUserCenterId).label("组织架构").describe("用户组织机构").path("/user").component("Layout").icon("el-icon-user-solid").sortValue(1).build());
        menuList.add(Menu.builder().id(ucenterId).label("系统角色").describe("系统角色").path("/ucenter").component("Layout").icon("el-icon-user").sortValue(2).build());
        menuList.add(Menu.builder().id(cmsId).label("内容管理").describe("内容管理中心").path("/cms").component("Layout").icon("el-icon-document-copy").sortValue(3).build());
        menuList.add(Menu.builder().id(weiXinPushId).label("微信推送").describe("微信群发推送").path("/wxGroup").component("Layout").icon("el-icon-document").sortValue(4).build());
        menuList.add(Menu.builder().id(aiId).label("AI助手").path("/assistant").component("Layout").icon("el-icon-message").sortValue(5).build());
        menuList.add(Menu.builder().id(exportId).label("导出管理").path("/export/index").component("caring/export/exportManagement/index").icon("el-icon-download").sortValue(6).build());
        menuList.add(Menu.builder().id(authId).label("权限管理").describe("管理权限相关").path("/auth").component("Layout").icon("el-icon-lock").sortValue(7).build());

        menuMap.put(HOME, homeId);


        // 2级菜单
        Long ucenterPatientId = snowflake.nextId();
        menuMap.put(UCENTER_PATIENT, ucenterPatientId);
        menuList.add(Menu.builder().id(ucenterPatientId).parentId(ucenterId).label("会员").path("/ucenter/patient").component("caring/ucenter/patient/Index").sortValue(0).build());
        Long ucenterDoctorId = snowflake.nextId();
        menuMap.put(UCENTER_DOCTOR, ucenterDoctorId);
        menuList.add(Menu.builder().id(ucenterDoctorId).parentId(ucenterId).label("医生").path("/ucenter/doctor").component("caring/ucenter/doctor/Index").sortValue(1).build());
        Long ucenterGroupId = snowflake.nextId();
        menuMap.put(UCENTER_GROUP, ucenterGroupId);
        menuList.add(Menu.builder().id(ucenterGroupId).parentId(ucenterId).label("小组").path("/ucenter/group").component("caring/ucenter/group/Index").sortValue(2).build());
        Long ucenterNursingStaffId = snowflake.nextId();
        menuMap.put(UCENTER_NURSINGSTAFF, ucenterNursingStaffId);
        menuList.add(Menu.builder().id(ucenterNursingStaffId).parentId(ucenterId).label("医助").path("/ucenter/nursingstaff").component("caring/ucenter/nursingstaff/Index").sortValue(3).build());

        // 群发通知
        Long groupModelId = snowflake.nextId();
        menuList.add(Menu.builder().id(groupModelId).parentId(cmsId).label("群发通知").path("/groupmodel/Index").component("caring/groupmodel/Index").sortValue(1).build());
        Long siteId = snowflake.nextId();
        menuMap.put(CMS_SITE, siteId);
        menuList.add(Menu.builder().id(siteId).parentId(cmsId).label("建站管理").path("/magento/Index").component("caring/magento/index").sortValue(2).build());
        Long cmsChannelContentId = snowflake.nextId();
        menuMap.put(CMS_CHANNEL_CONTENT, cmsChannelContentId);
        menuList.add(Menu.builder().id(cmsChannelContentId).parentId(cmsId).label("文章管理").path("/cms/channelcontent").component("caring/cms/channelcontent/Index").sortValue(3).build());
        menuList.add(Menu.builder().id(snowflake.nextId()).parentId(cmsId).label("常用语").path("/commonPhrases").component("caring/cms/commonPhrases/Index").sortValue(4).build());

        // 微信推送
        Long materialLibraryId = snowflake.nextId();
        menuList.add(Menu.builder().id(materialLibraryId).parentId(weiXinPushId).label("素材管理").path("/wxgroup/materiallibrary").component("caring/wxgroup/Materiallibrary").sortValue(1).build());
        Long pushId = snowflake.nextId();
        menuList.add(Menu.builder().id(pushId).parentId(weiXinPushId).label("推送管理").path("/wxgroup/pushmanager").component("caring/wxgroup/PushManager").sortValue(2).build());

        // ai助手
        menuList.add(Menu.builder().id(snowflake.nextId()).parentId(aiId).label("关键字回复").path("/assistant/keyword").component("caring/assistant/keyword/index").sortValue(1).build());
        menuList.add(Menu.builder().id(snowflake.nextId()).parentId(aiId).label("关注后自动回复").path("/assistant/automatic").component("caring/assistant/automatic/index").sortValue(2).build());


        Long orgId = snowflake.nextId();
        menuMap.put(ORG, orgId);
        menuList.add(Menu.builder().id(orgId).parentId(menuUserCenterId).label("组织管理").path("/user/org").component("caring/user/org/Index").sortValue(0).build());

        Long userId = snowflake.nextId();
        menuMap.put(USER, userId);
        menuList.add(Menu.builder().id(userId).parentId(menuUserCenterId).label("管理员").path("/user/user").component("caring/user/user/Index").sortValue(2).build());

        Long roleId = snowflake.nextId();
        menuMap.put(ROLE, roleId);
        menuList.add(Menu.builder().id(roleId).parentId(authId).label("系统角色").path("/auth/role").component("caring/auth/role/Index").sortValue(1).build());
        Long menuId = snowflake.nextId();
        menuMap.put(MENU, menuId);
        menuList.add(Menu.builder().id(menuId).parentId(authId).label("菜单配置").path("/auth/user").component("caring/auth/menu/Index").sortValue(2).build());

        return menuService.saveBatch(menuList);
    }


    private boolean initDict() {
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
        return dictionaryItemService.saveBatch(dictionaryItemList);
    }

    @Override
    public boolean reset(String tenant) {
        //TODO 待实现
        // 1，清空所有表的数据
        // 2，重新初始化 tenant
        // 3，重新初始化 业务数据
        //        init(tenant);
        return true;
    }

    /**
     * 清空所有表中当前租户的数据
     *
     * @param ids            租户id
     * @param tenantCodeList 租户编码
     * @return
     */
    @Override
    public boolean delete(List<Long> ids, List<String> tenantCodeList) {
        // 1,查询系统中的所有表
        // 删除该租户的所有数据
        if (CollUtil.isEmpty(tenantCodeList)) {
            return true;
        }
        for (String s : tenantCodeList) {
            initDbMapper.deleteDate(s);
        }
        return true;
    }
}
