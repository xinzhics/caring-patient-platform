package com.caring.sass.wx.service.config.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.key.Key;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.constant.WeiXinConstants;
import com.caring.sass.wx.dao.config.ApiErrorMapper;
import com.caring.sass.wx.dao.config.ConfigMapper;
import com.caring.sass.wx.dao.config.WxMenuMapper;
import com.caring.sass.wx.dao.menu.CustomMenuMapper;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.entity.config.ApiError;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.entity.menu.CustomMenu;
import com.caring.sass.wx.service.authLogin.WxUserAuthLoginServiceImpl;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.config.mass.SaasWxMpMassMessageServiceImpl;
import com.caring.sass.wx.utils.I18nUtils;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.bean.menu.WxMenuRule;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.common.util.json.GsonParser;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.material.*;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import me.chanjar.weixin.mp.bean.result.*;
import me.chanjar.weixin.mp.bean.tag.WxTagListUser;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.enums.AiLangType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static me.chanjar.weixin.mp.enums.WxMpApiUrl.TemplateMsg.TEMPLATE_API_ADD_TEMPLATE;

/**
 * 业务实现类
 * 微信配置信息
 * 该服务 只追对公众号接口使用。
 * 拒绝小程序的业务代码
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class ConfigServiceImpl extends SuperServiceImpl<ConfigMapper, Config> implements ConfigService {


    @Autowired
    ConfigAdditionalService configAdditionalService;

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    CustomMenuMapper customMenuMapper;

    @Autowired
    WxUserAuthLoginServiceImpl wxUserAuthLoginService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WxMenuMapper wxMenuMapper;

    @Autowired
    ApiErrorMapper apiErrorMapper;

    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("weixin-upload-", false);

    private static final ExecutorService WEIXIN_UPLOAD_EXECUTOR = new ThreadPoolExecutor(2, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000), THREAD_FACTORY);

    @Override
    public boolean updateById(Config config) {
        Config old = baseMapper.selectById(config.getId());
        // token需要保存，这个在微信服务端上
        config.setToken(old.getToken());
        baseMapper.updateById(config);
        try {
            WxMpServiceHolder.delete(config.getAppId());
        } catch (Exception e) {
        }
        return super.updateById(config);
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 删除微信公众号信息
     * @Date 2020/9/16 16:35
     */
    @Override
    public void deleteConfig(Config config) {
        if (Objects.nonNull(config)) {
            LbqWrapper<Config> queryWrapper = new LbqWrapper<>();
            if (!StringUtils.isEmpty(config.getAppId())) {
                queryWrapper.eq(Config::getAppId, config.getAppId());
            } else if (!StringUtils.isEmpty(config.getId())) {
                queryWrapper.eq(Config::getId, config.getId());
            }
            baseMapper.delete(queryWrapper);
        }
    }

    /**
     * @return com.caring.sass.wx.entity.config.Config
     * @Author yangShuai
     * @Description 查询微信公众号信息
     * @Date 2020/9/16 13:58
     */
    @Override
    public Config getConfig(Config config) {
        if (Objects.isNull(config)) {
            config = new Config();
        }
        // 尝试通过appID获取配置
        if (!StringUtils.isEmpty(config.getAppId())) {
            Config getConfigByAppId = getConfigByAppId(config.getAppId());
            if (getConfigByAppId != null) {
                return getConfigByAppId;
            }
        }

        // 尝试通过sourceID获取配置
        if (!StringUtils.isEmpty(config.getSourceId())) {
            Config getConfigBySourceId = getConfigBySourceId(config.getSourceId());
            if (getConfigBySourceId != null) {
                return getConfigBySourceId;
            }
        }

        // 尝试通过ID获取配置
        if (!StringUtils.isEmpty(config.getId())) {
            Config getConfigById = baseMapper.selectByIdWithoutTenant(config.getId());
            if (getConfigById != null) {
                return getConfigById;
            }
        }

        // sql注入项目编码，获取项目全局唯一的配置
        Config getConfig = getConfigByTenant();
        if (getConfig == null) {
            throw new BizException("未查询到微信公众号信息");
        }

        return getConfig;
    }

    /**
     * 只查询公众号
     * @return
     */
    @Override
    public Config getConfigByTenant() {
        LbqWrapper<Config> wrapper = Wraps.<Config>lbQ().eq(Config::getAuthType, 1);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 只查询公众号
     * @param generalForm
     * @return
     */
    private Config getConfig(GeneralForm generalForm) {
        Config config = new Config();
        if (Objects.nonNull(generalForm)) {
            config.setAppId(generalForm.getWxAppId());
        }
        return getConfig(config);
    }

    /**
     * @return com.caring.sass.wx.entity.config.Config
     * @Author yangShuai
     * @Description 通过 原始Id 获取到微信公众号信息
     * @Date 2020/9/15 17:14
     */
    @Override
    public Config getConfigBySourceId(String sourceId) {
        return baseMapper.selectBySourceIdWithoutTenant(sourceId);
    }

    /**
     * @return com.caring.sass.wx.entity.config.Config
     * @Author yangShuai
     * @Description 获取微信公众号
     * @Date 2020/9/15 17:19
     */
    @Override
    public Config getConfigByAppId(String appId) {
        List<Config> configs = baseMapper.selectByAppIdWithoutTenant(appId);
        if (CollUtil.isEmpty(configs)) {
            return null;
        }
        return configs.get(0);
    }

    /**
     * 查询微信公众号授权文件信息
     *
     * @return
     */
    @Deprecated
    @Override
    public String findTxtValueByTxtName() {
        Config selectOne = getConfigByTenant();
        if (Objects.nonNull(selectOne)) {
            return selectOne.getAuthorizationFileName();
        } else {
            throw new BizException("未查询到微信公众号信息");
        }
    }

    /**
     * @return java.util.List<me.chanjar.weixin.mp.bean.tag.WxUserTag>
     * @Author yangShuai
     * @Description 获取微信公众号标签列表
     * @Date 2020/9/16 9:34
     */
    @Override
    public List<WxUserTag> loadTagList(String appId) {
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        try {
            return wxService.getUserTagService().tagGet();
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * @return java.util.List<me.chanjar.weixin.mp.bean.tag.WxUserTag>
     * @Author yangShuai
     * @Description 获取微信公众号标签列表
     * @Date 2020/9/16 9:34
     */
    @Override
    public List<WxUserTag> loadTagList(GeneralForm generalForm) {
        R<String> officialAccountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        String accountTypeData = officialAccountType.getData();
        if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(accountTypeData)) {
            return new ArrayList<>();
        }
        Config config = getConfig(generalForm);
        return loadTagList(config.getAppId());
    }

    /**
     * @return me.chanjar.weixin.mp.bean.tag.WxUserTag
     * @Author yangShuai
     * @Description 不存在标签，则创建
     * @Date 2020/9/16 9:35
     */
    @Override
    public WxUserTag createTagIfNotExist(String appId, String tagName) {
        try {
            WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
            List<WxUserTag> wxUserTags = loadTagList(appId);

            // 存在相同名称的标签，直接返回
            for (WxUserTag wxUserTag : wxUserTags) {
                String wxUserTagName = wxUserTag.getName();
                if (Objects.equals(tagName, wxUserTagName)) {
                    return wxUserTag;
                }
            }
            // 重新创建标签
            return wxService.getUserTagService().tagCreate(tagName);
        } catch (Exception var5) {
            apiErrorMapper.insert(ApiError.builder().wxAppId(appId).wxErrorMessage(var5.getMessage()).build());
            throw new BizException(var5.getMessage());
        }
    }


    /**
     * @param wxAppId
     * @param ticket  生成二维码所需要的ticket
     * @param expire
     * @return java.lang.String
     * @Author yangShuai
     * @Description
     * @Date 2020/9/15 17:22
     */
    @Override
    public String generateConfigQrCode(String wxAppId, String ticket, Integer expire) {
        WxMpService wxService = WxMpServiceHolder.getWxMpService(wxAppId);
        WxMpQrcodeService qrcodeService = wxService.getQrcodeService();
        if (!StringUtils.isEmpty(ticket) && expire == null) {
            try {
                WxMpQrCodeTicket wxMpQrCodeTicket = qrcodeService.qrCodeCreateLastTicket(ticket);
                return wxMpQrCodeTicket.getUrl();
            } catch (Exception var8) {
                apiErrorMapper.insert(ApiError.builder().wxAppId(wxAppId).wxErrorMessage(var8.getMessage()).build());
                throw new BizException("生成二维码失败");
            }
        } else {
            return null;
        }
    }

    /**
     * @return boolean
     * @Author yangShuai
     * @Description 保存公众号信息，并检验公众号配置是否可以授权
     * @Date 2020/9/15 17:45
     */
    @Override
    public boolean save(Config config) {
        Config hasConfig = getConfigByAppId(config.getAppId());
        if (Objects.nonNull(hasConfig)) {
            throw new BizException("公众号已经被使用");
        }
        config.setToken(RandomUtil.randomString(8));
        baseMapper.insert(config);
        // 公众号的config保存。
        if (config.getAuthType() == null || config.getAuthType().equals(1)) {
            ConfigAdditional setting = configAdditionalService.getConfigAdditionalByWxAppId(config.getAppId());
            if (Objects.isNull(setting)) {
                setting = configAdditionalService.getConfigAdditionalByProjectId();
                if (Objects.isNull(setting)) {
                    setting = ConfigAdditional.builder().wxAppId(config.getAppId())
                            .build();
                    // throw new BizException("微信公众号项目配置失败");
                    configAdditionalService.save(setting);
                } else {
                    configAdditionalService.updateAllById(setting);
                    setting.setWxAppId(config.getAppId());
                }
            }
        }
        return true;
    }


    /**
     * 发布菜单到微信
     *
     * @param appId      微信appId
     * @param wxMenuJson 微信菜单json
     */
    @Deprecated
    @Override
    public void publishMenu2Wx(String appId, String wxMenuJson) {
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMenuService menuService = wxService.getMenuService();
        try {
            // 删除原本菜单
            menuService.menuDelete();
            // 重新创建菜单
            menuService.menuCreate(wxMenuJson);
            String res = menuService.menuGet().toJson();
            // 更新数据库菜单快照
            Config config = getConfigByAppId(appId);
            config.setMenu(res);
            baseMapper.updateById(config);
        } catch (WxErrorException e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 查询微信配置的菜单
     *
     * @param appId 项目appdId
     */
    @Override
    public WxMpMenu queryWxMenu(String appId) {
        // 优先从数据库快照读取
        Config config = getConfigByAppId(appId);
        if (StrUtil.isNotBlank(config.getMenu())) {
            return WxMpMenu.fromJson(config.getMenu());
        }
        // 从微信上获取
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMenuService menuService = wxService.getMenuService();
        try {
            return menuService.menuGet();
        } catch (WxErrorException e) {
            throw new BizException(e);
        }
    }


    /**
     * 个人服务号菜单的区别就是没有 个性化菜单
     * @param config
     * @param menu
     * @throws WxErrorException
     */
    @Override
    public void personalServiceNumberMenu(Config config, WxMpMenu menu) throws WxErrorException {
        // 获取appId
        String appId = config.getAppId();

        // 根据appId获取租户信息
        R<Tenant> tenantR = this.tenantApi.getTenantByWxAppId(appId);
        if (tenantR.getIsError()) {
            throw new BizException("项目不存在");
        }
        Tenant tenant = tenantR.getData();


        // 获取微信服务
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMenuService menuService = wxService.getMenuService();
        // 清除所有菜单
        menuService.menuDelete();

        wxMenuMapper.delete(Wraps.<com.caring.sass.wx.entity.config.WxMenu>lbQ().eq(com.caring.sass.wx.entity.config.WxMenu::getWxAppId, config.getAppId()));
        List<com.caring.sass.wx.entity.config.WxMenu> wxMenus = new ArrayList<>();

        // 更新默认菜单
        WxMpMenu.WxMpConditionalMenu menuMenu = menu.getMenu();
        List<WxMenuButton> buttons = menuMenu.getButtons();
        personalServiceUpdateMenu(buttons, tenant, wxMenus);
        menuService.menuCreate(menuMenu.toString());

        WxMpMenu wxMpMenu = menuService.menuGet();
        config.setMenu(wxMpMenu.toJson());
        baseMapper.updateById(config);

        Set<String> menuKeys = new HashSet<>();
        for (com.caring.sass.wx.entity.config.WxMenu wxMenu : wxMenus) {
            String menuKey = wxMenu.getMenuKey();
            if (menuKeys.contains(menuKey)) {
                continue;
            }
            menuKeys.add(menuKey);
            wxMenuMapper.insert(wxMenu);
        }



    }


    public void personalServiceUpdateMenu(List<WxMenuButton> wxMenuButtons, Tenant project, List<com.caring.sass.wx.entity.config.WxMenu> wxMenus) {
        wxMenuButtons.forEach(wxMenuButton -> {
            String type = wxMenuButton.getType();
            List<WxMenuButton> subButtons = wxMenuButton.getSubButtons();
            if (!subButtons.isEmpty()) {
                personalServiceUpdateMenu(subButtons, project,  wxMenus);
            } else {
                if ("view".equals(type)) {
                    String url = wxMenuButton.getUrl();
                    if (StrUtil.isNotEmpty(url)) {
                        if (!url.startsWith("http")) {
                            wxMenuButton.setUrl(ApplicationDomainUtil.wxPatientBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())) + "/" + url);
                        }
                    }
                } else if ("click".equals(type)) {
                    com.caring.sass.wx.entity.config.WxMenu wxMenu = com.caring.sass.wx.entity.config.WxMenu.builder()
                            .menuType(type)
                            .menuName(wxMenuButton.getName())
                            .menuKey(wxMenuButton.getKey())
                            .build();
                    if (wxMenuButton.getKey() == null) {
                        throw new BizException("点击类型菜单数据错误，请删除菜单后重新添加");
                    }
                    long menuId;
                    try {
                        menuId = Long.parseLong(wxMenuButton.getKey());
                    } catch (Exception e) {
                        throw new BizException("点击类型菜单数据错误，请删除点击类型菜单后重新添加");
                    }
                    CustomMenu customMenu = customMenuMapper.selectById(menuId);
                    if (Objects.isNull(customMenu)) {
                        throw new BizException("点击类型菜单数据错误，请删除菜单后重新添加");
                    }
                    String customMenuKey = customMenu.getKey();
                    if ("image".equals(customMenuKey)) {
                        wxMenu.setMediaId(customMenu.getAppid());
                        wxMenuButton.setMediaId(customMenu.getAppid());
                    } else if ("text".equals(customMenuKey)) {
                        wxMenu.setTextContent(customMenu.getUrl());
                    }
                    wxMenu.setWxAppId(project.getWxAppId());
                    wxMenus.add(wxMenu);
                }
            }
        });

    }


    @Transactional
    public boolean publishMenu(Config config, WxMpMenu menu) throws WxErrorException {
        // 获取appId
        String appId = config.getAppId();

        // 根据appId获取租户信息
        R<Tenant> tenantR = this.tenantApi.getTenantByWxAppId(appId);
        if (tenantR.getIsError()) {
            throw new BizException("项目不存在");
        }
        Tenant tenant = tenantR.getData();

        // 获取微信服务
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMenuService menuService = wxService.getMenuService();

        // 创建医生标签
        WxUserTag doctorTag = createTagIfNotExist(appId, TagsEnum.DOCTOR_TAGS.getValue());
        // 创建患者标签
        WxUserTag patientTag = createTagIfNotExist(appId, TagsEnum.PATIENT_TAG.getValue());
        // 创建游客标签
        WxUserTag touristsTag = createTagIfNotExist(appId, TagsEnum.TOURISTS_TAG.getValue());

        WxUserTag assistantTag = createTagIfNotExist(appId, TagsEnum.ASSISTANT_TAG.getValue());

        // 清除所有菜单
        menuService.menuDelete();
        wxMenuMapper.delete(Wraps.<com.caring.sass.wx.entity.config.WxMenu>lbQ().eq(com.caring.sass.wx.entity.config.WxMenu::getWxAppId, config.getAppId()));
        List<com.caring.sass.wx.entity.config.WxMenu> wxMenus = new ArrayList<>();


        // 更新默认菜单
        WxMpMenu.WxMpConditionalMenu menuMenu = menu.getMenu();
        List<WxMenuButton> buttons = menuMenu.getButtons();
        updateMenu(buttons, tenant, null, wxMenus);
        menuService.menuCreate(menuMenu.toString());

        // 更新个性化菜单
        List<WxMpMenu.WxMpConditionalMenu> conditionalMenus = menu.getConditionalMenu();
        for (WxMpMenu.WxMpConditionalMenu conditionalMenu : conditionalMenus) {
            WxMenuRule rule = conditionalMenu.getRule();
            List<WxMenuButton> menuButtons = conditionalMenu.getButtons();
            String tagId = rule.getTagId();
            if (TagsEnum.DOCTOR_TAGS.getValue().equals(tagId)) {
                rule.setTagId(doctorTag.getId().toString());
                updateMenu(menuButtons, tenant, TagsEnum.DOCTOR_TAGS, wxMenus);
            } else if (TagsEnum.TOURISTS_TAG.getValue().equals(tagId)) {
                rule.setTagId(touristsTag.getId().toString());
                updateMenu(menuButtons, tenant, TagsEnum.TOURISTS_TAG, wxMenus);
            } else if (TagsEnum.PATIENT_TAG.getValue().equals(tagId)) {
                rule.setTagId(patientTag.getId().toString());
                updateMenu(menuButtons, tenant, TagsEnum.PATIENT_TAG, wxMenus);
            } else if (TagsEnum.ASSISTANT_TAG.getValue().equals(tagId)) {
                rule.setTagId(assistantTag.getId().toString());
                updateMenu(menuButtons, tenant, TagsEnum.ASSISTANT_TAG, wxMenus);
            } else {
                continue;
            }
            menuService.menuCreate(conditionalMenu.toString());
        }
        WxMpMenu wxMpMenu = menuService.menuGet();
        config.setMenu(wxMpMenu.toJson());
        baseMapper.updateById(config);

        if (!wxMenus.isEmpty()) {
            Set<String> menuKeys = new HashSet<>();
            for (com.caring.sass.wx.entity.config.WxMenu wxMenu : wxMenus) {
                String menuKey = wxMenu.getMenuKey();
                if (menuKeys.contains(menuKey)) {
                    continue;
                }
                menuKeys.add(menuKey);
                wxMenuMapper.insert(wxMenu);
            }
        }

        return true;

    }

    /**
     * 发布菜单到微信公众号
     *
     * @param config 微信配置信息
     * @param menu   待发布的菜单信息
     * @throws WxErrorException 发生微信异常时抛出错误
     * @Author yangShuai
     * @Date 2020/9/15 17:47
     */
    @Override
    public void publishMenu(Config config, JSONArray menu) throws WxErrorException {
        // 获取appId
        String appId = config.getAppId();

        // 根据appId获取租户信息
        R<Tenant> tenantR = this.tenantApi.getTenantByWxAppId(appId);
        if (tenantR.getIsError()) {
            throw new BizException("项目不存在");
        }
        Tenant tenant = tenantR.getData();

        // 获取微信服务
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMenuService menuService = wxService.getMenuService();
        menuService.menuDelete();

        // 创建医生标签
        WxUserTag doctorTag = createTagIfNotExist(appId, TagsEnum.DOCTOR_TAGS.getValue());
        // 创建患者标签
        WxUserTag patientTag = createTagIfNotExist(appId, TagsEnum.PATIENT_TAG.getValue());
        // 创建游客标签
        WxUserTag touristsTag = createTagIfNotExist(appId, TagsEnum.TOURISTS_TAG.getValue());

        WxUserTag assistantTag = createTagIfNotExist(appId, TagsEnum.ASSISTANT_TAG.getValue());
        String tagIdString = "tag_id";

        JSONObject json;
        // 循环一次，找到默认菜单
        boolean defaultMenu = false;
        for (int i = 0; i < menu.size(); i++) {
            json = JSON.parseObject(JSON.toJSONString(menu.get(i)));
            JSONObject matchRule = json.getJSONObject("matchrule");
            if (matchRule == null) {
                JSONArray jsonArray = json.getJSONArray("button");
                // 更新菜单
                updateMenu(jsonArray, tenant, null);
                // 发布菜单
                menuService.menuCreate(JSON.toJSONString(json));
                defaultMenu = true;
                menu.remove(i);
                break;
            }
        }
        if (defaultMenu) {
            for (Object o : menu) {
                json = JSON.parseObject(JSON.toJSONString(o));
                JSONArray jsonArray = json.getJSONArray("button");
                JSONObject matchRule = json.getJSONObject("matchrule");
                if (matchRule != null) {
                    String tagId = matchRule.getString(tagIdString);
                    if (TagsEnum.DOCTOR_TAGS.getValue().equals(tagId)) {
                        matchRule.put(tagIdString, doctorTag.getId());
                        updateMenu(jsonArray, tenant, TagsEnum.DOCTOR_TAGS);
                    } else if (TagsEnum.TOURISTS_TAG.getValue().equals(tagId)) {
                        matchRule.put(tagIdString, touristsTag.getId());
                        updateMenu(jsonArray, tenant, TagsEnum.TOURISTS_TAG);
                    } else if (TagsEnum.PATIENT_TAG.getValue().equals(tagId)) {
                        matchRule.put(tagIdString, patientTag.getId());
                        updateMenu(jsonArray, tenant, TagsEnum.PATIENT_TAG);
                    } else if (TagsEnum.ASSISTANT_TAG.getValue().equals(tagId)) {
                        matchRule.put(tagIdString, assistantTag.getId());
                        updateMenu(jsonArray, tenant, TagsEnum.ASSISTANT_TAG);
                    } else {
                        continue;
                    }
                    menuService.menuCreate(JSON.toJSONString(json));
                }
            }
        } else {
            throw new BizException("请先设置默认菜单");
        }

        String accessToken = wxService.getAccessToken();
        String result = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=[?]".replace("[?]", accessToken));
        json = JSONObject.parseObject(result);
        config.setMenu(json.toJSONString());
        baseMapper.updateById(config);
    }


    public void updateMenu(List<WxMenuButton> wxMenuButtons, Tenant project, TagsEnum tagsEnum, List<com.caring.sass.wx.entity.config.WxMenu> wxMenus) {
        wxMenuButtons.forEach(wxMenuButton -> {
            String type = wxMenuButton.getType();
            List<WxMenuButton> subButtons = wxMenuButton.getSubButtons();
            if (!subButtons.isEmpty()) {
                updateMenu(subButtons, project, tagsEnum,  wxMenus);
            } else {
                if ("view".equals(type)) {
                    String url = wxMenuButton.getUrl();
                    if (StrUtil.isNotEmpty(url)) {
                        if (url.startsWith("http")) {
                            wxMenuButton.setUrl(isPlatformDomain(ApplicationProperties.getDomainProtocol(), project.getDomainName(), project.getOldDomainName(), url));
                        } else {
                            if (tagsEnum == TagsEnum.DOCTOR_TAGS) {
                                wxMenuButton.setUrl(ApplicationDomainUtil.wxDoctorBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())) + "/" + url);
                            } else if (tagsEnum == TagsEnum.ASSISTANT_TAG) {
                                wxMenuButton.setUrl(ApplicationDomainUtil.wxAssistantBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())) + "/" + url);
                            } else {
                                wxMenuButton.setUrl(ApplicationDomainUtil.wxPatientBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())) + "/" + url);
                            }
                        }
                    } else {
                        if (tagsEnum == TagsEnum.ASSISTANT_TAG) {
                            wxMenuButton.setUrl(ApplicationDomainUtil.wxAssistantBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())));
                        }
                    }
                } else if ("click".equals(type)) {
                    com.caring.sass.wx.entity.config.WxMenu wxMenu = com.caring.sass.wx.entity.config.WxMenu.builder()
                            .menuType(type)
                            .menuName(wxMenuButton.getName())
                            .menuKey(wxMenuButton.getKey())
                            .build();
                    if (wxMenuButton.getKey() == null) {
                        throw new BizException("点击类型菜单数据错误，请删除菜单后重新添加");
                    }
                    long menuId;
                    try {
                        menuId = Long.parseLong(wxMenuButton.getKey());
                    } catch (Exception e) {
                        throw new BizException("点击类型菜单数据错误，请删除点击类型菜单后重新添加");
                    }
                    CustomMenu customMenu = customMenuMapper.selectById(menuId);
                    if (Objects.isNull(customMenu)) {
                        throw new BizException("点击类型菜单数据错误，请删除菜单后重新添加");
                    }
                    String customMenuKey = customMenu.getKey();
                    if ("image".equals(customMenuKey)) {
                        wxMenu.setMediaId(customMenu.getAppid());
                        wxMenuButton.setMediaId(customMenu.getAppid());
                    } else if ("text".equals(customMenuKey)) {
                        wxMenu.setTextContent(customMenu.getUrl());
                    }
                    wxMenu.setWxAppId(project.getWxAppId());
                    wxMenus.add(wxMenu);
                }
            }
        });

    }


    /**
     * @return void
     * @Author yangShuai
     * @Description 更新 菜单中的链接地址
     * @Date 2020/9/16 9:37
     */
    public void updateMenu(JSONArray jsonArray, Tenant project, TagsEnum tagsEnum) {
        if (jsonArray != null) {
            Iterator var4 = jsonArray.iterator();
            while (true) {
                Object o;
                do {
                    if (!var4.hasNext()) {
                        return;
                    }
                    o = var4.next();
                } while (o == null);

                JSONObject json = (JSONObject) o;
                JSONArray jsonArray1 = json.getJSONArray("sub_button");
                if (jsonArray1 != null && jsonArray1.size() > 0) {
                    this.updateMenu(jsonArray1, project, tagsEnum);
                } else {
                    String type = json.getString("type");
                    if ("view".equals(type)) {
                        String url = json.getString("url");
                        if (url != null && url.length() > 0) {
                            if (url.startsWith("http")) {
                                json.put("url", isPlatformDomain(ApplicationProperties.getDomainProtocol(), project.getDomainName(), project.getOldDomainName(), url));
                            } else {
                                if (tagsEnum == TagsEnum.DOCTOR_TAGS) {
                                    json.put("url", ApplicationDomainUtil.wxDoctorBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())) + "/" + url);
                                } else if (tagsEnum == TagsEnum.ASSISTANT_TAG) {
                                    json.put("url", ApplicationDomainUtil.wxAssistantBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())) + "/" + url);
                                } else {
                                    json.put("url", ApplicationDomainUtil.wxPatientBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())) + "/" + url);
                                }
                            }
                        } else {
                            if (tagsEnum == TagsEnum.ASSISTANT_TAG) {
                                json.put("url", ApplicationDomainUtil.wxAssistantBaseDomain(project.getDomainName(), Objects.nonNull(project.getWxBindTime())));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断链接是否是一个完整的链接
     * @param sufixDomainName
     * @param domainName
     * @param oldDomainName
     * @param str
     * @return
     */
    static String isPlatformDomain(String sufixDomainName, String domainName, String oldDomainName, String str) {
        try {
            URL url = new URL(str);
            String authority = url.getAuthority();
            if (authority.endsWith(sufixDomainName) && !authority.startsWith("www")) {
                String prefix = authority.substring(0, authority.indexOf("."));
                String subfix = authority.substring(authority.indexOf("."));
                if (!prefix.equals(domainName) && oldDomainName.equals(prefix)) {
                    return url.getProtocol() + "://" + domainName + subfix + url.getFile();
                }
            }
        } catch (Exception var8) {
        }

        return str;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 删除微信公众号
     * @Date 2020/9/16 14:35
     */
    @Override
    public void deleteMenu(String appId, String menuId) {
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        try {
            wxService.getMenuService().menuDelete(menuId);
        } catch (Exception var5) {
            throw new BizException("删除菜单失败");
        }
    }

    @Override
    public WxMpMenu getMenu() {
        Config config = getConfigByTenant();
        if (config != null) {
            if (!StringUtils.isEmpty(config.getMenu())) {
                return WxMpMenu.fromJson(config.getMenu());
            } else {
                WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
                WxMpMenuService menuService = wxService.getMenuService();
                try {
                    WxMpMenu wxMpMenu = menuService.menuGet();
                    if (wxMpMenu != null) {
                        config.setMenu(wxMpMenu.toJson());
                        baseMapper.updateById(config);
                    }
                    return wxMpMenu;
                } catch (WxErrorException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            return null;
        }
    }

    /**
     * @return com.alibaba.fastjson.JSONObject
     * @Author yangShuai
     * @Description 获取微信菜单
     * @Date 2020/9/16 14:41
     */
    @Override
    public JSONObject getMenu(String appId) {
        try {
            Config config = this.getConfigByAppId(appId);
            if (config != null) {
                if (!StringUtils.isEmpty(config.getMenu())) {
                    return JSONObject.parseObject(config.getMenu());
                } else {
                    WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
                    WxMpMenuService menuService = wxService.getMenuService();
                    WxMpMenu wxMpMenu = menuService.menuGet();
                    String accessToken = wxService.getAccessToken();
                    String result = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=[?]".replace("[?]", accessToken));
                    JSONObject json = JSONObject.parseObject(result);
                    config.setMenu(json.toJSONString());
                    baseMapper.updateById(config);
                    return json;
                }
            } else {
                return null;
            }
        } catch (Exception ignored) {
        }
        throw new BizException("获取菜单失败");
    }

    /**
     * 发送模板消息 到微信公众号
     *
     * @param form
     * @return
     */
    @Override
    public R<String> sendTemplateMessage(@RequestBody SendTemplateMessageForm form) {
        String wxAppId = form.getWxAppId();
        Config config;
        if (StringUtils.isEmpty(wxAppId)) {
            config = getConfig(form);
        } else {
            config = new Config();
            config.setAppId(wxAppId);
        }
        try {
            if (null != form.getTemplateMessage()) {
                String templateId = form.getTemplateMessage().getTemplateId();
                if (StringUtils.isEmpty(templateId)) {
                    return R.fail("模板消息Id不能为空");
                }
                WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
                wxService.getTemplateMsgService().sendTemplateMsg(form.getTemplateMessage());
                return R.success("");
            }

        } catch (WxErrorException wxErrorException) {
            WxError error = wxErrorException.getError();
            apiErrorMapper.insert(ApiError.builder().wxErrorMessage(error.getErrorMsg()).wxAppId(wxAppId).build());
            return R.fail(JSON.toJSONString(error));
        } catch (Exception var9) {
            log.error(var9.getMessage());
        }
        return R.fail("");

    }

    /**
     * @return java.util.List<me.chanjar.weixin.mp.bean.template.WxMpTemplate>
     * @Author yangShuai
     * @Description 拉取微信公众号的 模板配置
     * @Date 2020/9/16 14:54
     */
    @Override
    public List<WxMpTemplate> loadTemplateMessage(GeneralForm generalForm) {
        Config config = getConfig(generalForm);
        R<String> accountType = tenantApi.queryOfficialAccountType(config.getTenantCode());
        if (TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER.toString().equals(accountType.getData())) {
            WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
            WxMpTemplateMsgService templateMsgService = wxService.getTemplateMsgService();
            try {
                return templateMsgService.getAllPrivateTemplate();
            } catch (WxErrorException e) {
                throw new BizException(e.getMessage());
            }
        }
        return new ArrayList<>();
    }


    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 发送客服消息
     * @Date 2020/9/16 15:06
     */
    @Override
    public R sendKefuMsg(SendKefuMsgForm sendKefuMsgForm) {
        Config config = getConfig(sendKefuMsgForm);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpKefuMessage wxMpKefuMessage = sendKefuMsgForm.getMessage();
        try {
            if (wxMpKefuMessage.getMsgType() != null && wxMpKefuMessage.getMsgType().equals(WeiXinConstants.MediaType.image.toString())) {
                UploadTemporaryMatrialForm mediaDto = new UploadTemporaryMatrialForm();
                mediaDto.setUrl(wxMpKefuMessage.getContent());
                mediaDto.setMediaType(WeiXinConstants.MediaType.image);
                mediaDto.setFileType(WeiXinConstants.FileType.jpeg);
                WxMediaUploadResult wxMediaUploadResult = this.uploadTemporaryMatrial(config.getAppId(), mediaDto);
                String mediaId = wxMediaUploadResult.getMediaId();
                wxMpKefuMessage.setMediaId(mediaId);
            }

            boolean result = wxService.getKefuService().sendKefuMessage(wxMpKefuMessage);
            if (!result) {
                R.fail("发送客服消息失败");
            }
        } catch (WxErrorException var7) {
            WxError error = var7.getError();
            log.info(error.getErrorMsg());
            R.fail(error.getErrorMsg());
        }
        return R.success();

    }


    /**
     * 上传图文消息内的图片获取URL
     * 使用 url 下载图片 然后上传到 微信的素材库去
     * 用于解析cms内容中出现第三方图片路径时的解决办法
     * url: https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN
     *
     * @param appId
     * @param fileName
     * @param imgUrl
     */
    @Override
    public String mediaImgUpload(String appId, String fileName, String imgUrl) {
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMaterialService materialService = wxService.getMaterialService();
        File file = null;
        try {
            file = FileUtils.downLoadArticleFromUrl(imgUrl, fileName + ".png");
        } catch (IOException e) {
            throw new BizException("下载文件到本地失败");
        }
        return mediaImgUpload(materialService, file);

    }

    /**
     * 上传图文消息内的图片获取URL
     *
     * @param materialService
     * @param file
     * @return
     */
    private String mediaImgUpload(WxMpMaterialService materialService, File file) {
        String url = null;
        try {
            WxMediaImgUploadResult imgUpload = materialService.mediaImgUpload(file);
            url = imgUpload.getUrl();
        } catch (WxErrorException wxErrorException) {
            WxError error = wxErrorException.getError();
            log.info("mediaImgUpload error {} {} {}", error.getErrorCode(), error.getErrorMsg(), error.getErrorMsgEn());
            throw new BizException(error.getErrorMsg());
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
        return url;
    }

    /**
     * 统计微信公众号 素材库中各类素材总数，防止上传异常
     *
     * @param materialService
     * @return
     */
    private void checkMaterialCount(String materialType, WxMpMaterialService materialService) {
        WxMpMaterialCountResult materialCount = null;
        try {
            materialCount = materialService.materialCount();
        } catch (WxErrorException wxErrorException) {
            wxErrorException.printStackTrace();
        }
        if (Objects.nonNull(materialCount)) {
            // 判断素材库是否还有空间
            if (WeiXinConstants.MaterialType.image.toString().equals(materialType)) {
                if (materialCount.getImageCount() >= 100000) {
                    throw new BizException("微信公众号素材库<图片>数量已达上限，请清理后重新上传");
                }
            }
            if (WeiXinConstants.MaterialType.video.toString().equals(materialType)) {
                if (materialCount.getVideoCount() >= 1000) {
                    throw new BizException("微信公众号素材库<视频>数量已达上限，请清理后重新上传");
                }
            }
            if (WeiXinConstants.MaterialType.voice.toString().equals(materialType)) {
                if (materialCount.getVoiceCount() >= 1000) {
                    throw new BizException("微信公众号素材库<语音>数量已达上限，请清理后重新上传");
                }
            }
            if (WeiXinConstants.MaterialType.news.toString().equals(materialType)) {
                if (materialCount.getNewsCount() >= 100000) {
                    throw new BizException("微信公众号素材库<图文素材>数量已达上限，请清理后重新上传");
                }
            }
        }
    }

    /**
     * <pre>
     * 新增非图文永久素材
     * 通过POST表单来调用接口，表单id为media，包含需要上传的素材内容，有filename、filelength、content-type等信息。请注意：图片素材将进入公众平台官网素材管理模块中的默认分组。
     * 新增永久视频素材需特别注意：
     * 在上传视频素材时需要POST另一个表单，id为description，包含素材的描述信息，内容格式为JSON，格式如下：
     * {   "title":VIDEO_TITLE,   "introduction":INTRODUCTION   }
     * 详情请见: <a href="http://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738729&token=&lang=zh_CN">新增永久素材</a>
     * 接口url格式：https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE
     *
     * 除了3天就会失效的临时素材外，开发者有时需要永久保存一些素材，届时就可以通过本接口新增永久素材。
     * 永久图片素材新增后，将带有URL返回给开发者，开发者可以在腾讯系域名内使用（腾讯系域名外使用，图片将被屏蔽）。
     * 请注意：
     * 1、新增的永久素材也可以在公众平台官网素材管理模块中看到
     * 2、永久素材的数量是有上限的，请谨慎新增。图文消息素材和图片素材的上限为5000，其他类型为1000
     * 3、素材的格式大小等要求与公众平台官网一致。具体是，图片大小不超过2M，支持bmp/png/jpeg/jpg/gif格式，语音大小不超过5M，长度不超过60秒，支持mp3/wma/wav/amr格式
     * 4、调用该接口需https协议
     * </pre>
     *
     * @param material 上传的素材, 请看{@link WxMpMaterial}
     */
    @Override
    public WxMpMaterialUploadResult materialFileUpload(String appId, UploadTemporaryMatrialForm material) {
        String tenant = BaseContextHandler.getTenant();
        if (material.getAsynchronous() != null && material.getAsynchronous()) {
            // 将打任务的处理丢给多线程池去 执行
            WEIXIN_UPLOAD_EXECUTOR.execute(() -> asycMaterialFileUpload(tenant, appId, material));
            return null;
        } else {
            return asycMaterialFileUpload(tenant, appId, material);
        }
    }

    private WxMpMaterialUploadResult asycMaterialFileUpload(String tenantCode, String appId, UploadTemporaryMatrialForm material) {

        BaseContextHandler.setTenant(tenantCode);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMaterialService materialService = wxService.getMaterialService();
        WeiXinConstants.MediaType mediaType = material.getMediaType();
        checkMaterialCount(mediaType.toString(), materialService);
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        String url = material.getUrl();
        File file = null;
        if (StringUtils.isEmpty(url)) {
            throw new BizException("素材路径不能为空");
        }
        try {
            file = FileUtils.downLoadArticleFromUrl(url, material.getFileName());
        } catch (IOException e) {
            throw new BizException("素材下载失败");
        }
        wxMpMaterial.setVideoIntroduction(material.getVideoIntroduction());
        wxMpMaterial.setVideoTitle(material.getVideoTitle());
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName(material.getFileName());
        // 如果是异步的。 将回调发送到 redis
        try {
            WxMpMaterialUploadResult materialFileUpload = materialService.materialFileUpload(mediaType.toString(), wxMpMaterial);
            if (material.getAsynchronous() != null && material.getAsynchronous()) {
                String callBackValue = material.getRedisCallBackValue();
                JSONObject jsonObject = JSON.parseObject(callBackValue);
                if (WeiXinConstants.MediaType.video.equals(material.getMediaType())) {
                    // 非 图文消息 素材， 微信不返回素材的url。 需要自己去要
                    WxMpMaterialVideoInfoResult videoInfo = materialService.materialVideoInfo(materialFileUpload.getMediaId());
                    jsonObject.put("mediaUrl", videoInfo.getDownUrl());
                } else {
                    jsonObject.put("mediaUrl", materialFileUpload.getUrl());
                }
                jsonObject.put("mediaId", materialFileUpload.getMediaId());
                jsonObject.put("tenantCode", BaseContextHandler.getTenant());
                redisTemplate.opsForList().leftPush(material.getRedisCallBackKey(), jsonObject.toJSONString());
                return null;
            } else {
                return materialFileUpload;
            }
        } catch (WxErrorException wxErrorException) {
            throw new BizException(wxErrorException.getError().getErrorMsg());
        }
    }


    /**
     * 新增永久图文素材
     * url：https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN
     *
     * @param appId
     * @param news
     * @return
     */
    @Override
    public WxMpMaterialUploadResult mediaAddNews(String appId, WxMpMaterialNews news) {

        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMaterialService materialService = wxService.getMaterialService();
        checkMaterialCount(WeiXinConstants.MaterialType.news.toString(), materialService);
        try {
            return materialService.materialNewsUpload(news);
        } catch (WxErrorException wxErrorException) {
            throw new BizException(wxErrorException.getError().getErrorMsg());
        }
    }

    /**
     * 上传群发用的图文消息，上传后才能群发图文消息.
     *
     * @param news
     */
    public WxMpMassUploadResult massNewsUpload(WxMpService wxMpService, WxMpMassNews news) {
        try {
            SaasWxMpMassMessageServiceImpl massMessageService = new SaasWxMpMassMessageServiceImpl(wxMpService);
            return massMessageService.massNewsUpload(news);
        } catch (WxErrorException wxErrorException) {
            wxErrorException.printStackTrace();
            int errorCode = wxErrorException.getError().getErrorCode();
            if (errorCode == 88000) {
                throw new BizException("公众号没有打开留言权限");
            } else {
                throw new BizException("上传群发用的图文消息失败");
            }
        }
    }

    /**
     * 上传群发用的视频，上传后才能群发视频消息.
     *
     * @param messageService
     * @param video
     * @return
     */
    public WxMpMassUploadResult massVideoUpload(WxMpMassMessageService messageService, WxMpMassVideo video) {
        try {
            return messageService.massVideoUpload(video);
        } catch (WxErrorException wxErrorException) {
            throw new BizException("上传群发用的图文消息失败");
        }
    }

    /**
     * 根据 openId 群发图文消息
     *
     * @param news
     */
    @Override
    public R<WxMpMassSendResult> massOpenIdsMessageSend(WxMpMassOpenIdsMessage message,
                                                        WxMpMassTagMessage tagMessage,
                                                        WxMpMassNews news) {

        Config config = getConfigByTenant();
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpMassUploadResult newsUpload = massNewsUpload(wxService, news);
        if (Objects.nonNull(message)) {
            message.setMediaId(newsUpload.getMediaId());
            message.setMsgType(WxConsts.MassMsgType.MPNEWS);
            try {
                SaasWxMpMassMessageServiceImpl service = new SaasWxMpMassMessageServiceImpl(wxService);
                WxMpMassSendResult messageSend = service.massOpenIdsMessageSend(message);
                return R.success(messageSend);
            } catch (WxErrorException wxErrorException) {
                WxError error = wxErrorException.getError();
                apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(error)).build());
                log.info("群发消息失败 {} {} {}", error.getErrorCode(), error.getErrorMsg(), error.getErrorMsgEn());
                return R.fail(error.getErrorCode(), error.getErrorMsg());
            }
        } else if (Objects.nonNull(tagMessage)) {
            tagMessage.setMediaId(newsUpload.getMediaId());
            tagMessage.setMsgType(WxConsts.MassMsgType.MPNEWS);
            try {
                SaasWxMpMassMessageServiceImpl service = new SaasWxMpMassMessageServiceImpl(wxService);
                WxMpMassSendResult messageSend = service.massGroupMessageSend(tagMessage);
                return R.success(messageSend);
            } catch (WxErrorException wxErrorException) {
                WxError error = wxErrorException.getError();
                apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(error)).build());
                log.info("群发消息失败 {} {} {}", error.getErrorCode(), error.getErrorMsg(), error.getErrorMsgEn());
                return R.fail(error.getErrorCode(), error.getErrorMsg());
            }
        } else {
            return null;
        }

    }


    @Override
    public R<WxMpMassSendResult> massTagMessageSend(WxMpMassTagMessage message) {

        Config config = getConfigByTenant();
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            SaasWxMpMassMessageServiceImpl service = new SaasWxMpMassMessageServiceImpl(wxService);
            WxMpMassSendResult messageSend = service.massGroupMessageSend(message);
            return R.success(messageSend);
        } catch (WxErrorException wxErrorException) {
            WxError error = wxErrorException.getError();
            apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(error)).build());
            log.info("群发消息失败 {} {} {}", error.getErrorCode(), error.getErrorMsg(), error.getErrorMsgEn());
            return R.fail(error.getErrorCode(), error.getErrorMsg());
        }
    }

    /**
     * 根据openId 群发 文本、语音、图片
     *
     * @param message
     * @return
     */
    @Override
    public R<WxMpMassSendResult> massOpenIdsMessageSend(WxMpMassOpenIdsMessage message) {

        Config config = getConfigByTenant();
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            SaasWxMpMassMessageServiceImpl service = new SaasWxMpMassMessageServiceImpl(wxService);
            WxMpMassSendResult messageSend = service.massOpenIdsMessageSend(message);
            return R.success(messageSend);
        } catch (WxErrorException wxErrorException) {
            WxError error = wxErrorException.getError();
            apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(error)).build());
            log.info("群发消息失败 {} {} {}", error.getErrorCode(), error.getErrorMsg(), error.getErrorMsgEn());
            return R.fail(error.getErrorCode(), error.getErrorMsg());
        }

    }

    /**
     * 查询标签下的粉丝
     * 并存入redis中。返回redis的key
     */
    @Override
    public String queryWxTagUser(TagsEnum tagsEnum) throws WxErrorException {
        Config config = getConfigByTenant();
        if (Objects.isNull(config)) {
            return null;
        }
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpUserTagService userTagService = wxService.getUserTagService();
        List<WxUserTag> wxUserTags = userTagService.tagGet();
        Thread thread = Thread.currentThread();
        long threadId = thread.getId();
        if (TagsEnum.NO_TAG.equals(tagsEnum)) {
            String allTagRedisKey = "allTagRedisKey" + threadId;
            // allTagRedisKey 所有拥有标签的粉丝
            BoundSetOperations<String, String> setOperations = redisTemplate.boundSetOps(allTagRedisKey);
            for (WxUserTag userTag : wxUserTags) {
                if (TagsEnum.PATIENT_TAG.getValue().equals(userTag.getName())) {
                    getWxTagUserSaveRedis(userTagService, userTag.getId(), setOperations);
                } else if (TagsEnum.DOCTOR_TAGS.getValue().equals(userTag.getName())) {
                    getWxTagUserSaveRedis(userTagService, userTag.getId(), setOperations);
                } else if (TagsEnum.TOURISTS_TAG.getValue().equals(userTag.getName())) {
                    getWxTagUserSaveRedis(userTagService, userTag.getId(), setOperations);
                }
            }
            // 查询用户列表列表。
            WxMpUserService userService = wxService.getUserService();
            WxMpUserList mpUserList = userService.userList(null);
            int count = mpUserList.getCount();
            List<String> openIds = mpUserList.getOpenids();
            String allUserRedisKey = "allUserRedisKey" + threadId;
            setOperations = redisTemplate.boundSetOps(allUserRedisKey);
            if (CollUtil.isNotEmpty(openIds)) {
                setOperations.add(openIds.<String>toArray(new String[0]));
            }
            while (10000 == count) {
                mpUserList = userService.userList(null);
                mpUserList.getNextOpenid();
                count = mpUserList.getCount();
                openIds = mpUserList.getOpenids();
                if (CollUtil.isNotEmpty(openIds)) {
                    setOperations.add(openIds.<String>toArray(new String[0]));
                }
            }
            // 没有标签的微信用户
            String noTagWeiXinUserOpenIds = "noTagWeiXinUserOpenIds" + threadId;

            // 求 医生 患者 游客和 所有粉丝之间的差集。 并存入到 新的 集合中
            setOperations.diffAndStore(allTagRedisKey, noTagWeiXinUserOpenIds);

            // 移除两个集合
            Collection<String> deleteKeys = new ArrayList<>();
            deleteKeys.add(allUserRedisKey);
            deleteKeys.add(allTagRedisKey);
            redisTemplate.delete(deleteKeys);
            return noTagWeiXinUserOpenIds;
        }
        WxUserTag tags = this.getTags(wxService, tagsEnum.getValue());

        String tagWeiXinUserOpenIds = "tagWeiXinUserOpenIds" + threadId;
        BoundSetOperations<String, String> setOperations = redisTemplate.boundSetOps(tagWeiXinUserOpenIds);
        getWxTagUserSaveRedis(userTagService, tags.getId(), setOperations);
        return tagWeiXinUserOpenIds;
    }


    /**
     * 添加一个模板到 公众号模板库
     *
     * @param templateIdShort
     * @param
     * @return
     */
    @Override
    public String apiAddTemplate(String templateIdShort, List<String> keywordNameList) {
        Config configByTenant = getConfigByTenant();
        WxMpService wxMpService = WxMpServiceHolder.getWxMpService(configByTenant.getAppId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("template_id_short", templateIdShort);
        if (CollUtil.isNotEmpty(keywordNameList)) {
            jsonObject.put("keyword_name_list", keywordNameList);
        }
        String responseContent = null;
        try {
            responseContent = wxMpService.post(TEMPLATE_API_ADD_TEMPLATE, jsonObject.toString());
            final JsonObject result = GsonParser.parse(responseContent);
            if (result.get("errcode").getAsInt() == 0) {
                return result.get("template_id").getAsString();
            }
        } catch (WxErrorException e) {
            apiErrorMapper.insert(ApiError.builder().wxAppId(configByTenant.getAppId()).wxErrorMessage(JSON.toJSONString(e.getError())).build());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询标签下的粉丝。然后放入redis
     *
     * @param userTagService
     * @param tagId
     * @param setOperations
     */
    public void getWxTagUserSaveRedis(WxMpUserTagService userTagService, Long tagId, BoundSetOperations<String, String> setOperations) {

        try {
            WxTagListUser tagListUser = userTagService.tagListUser(tagId, null);
            String nextOpenid = tagListUser.getNextOpenid();
            WxTagListUser.WxTagListUserData listUserData = tagListUser.getData();
            if (listUserData == null) {
                return;
            }
            List<String> openidList = listUserData.getOpenidList();
            String[] toArray = openidList.<String>toArray(new String[0]);
            setOperations.add(toArray);
            while (StrUtil.isNotEmpty(nextOpenid)) {
                tagListUser = userTagService.tagListUser(tagId, nextOpenid);

                listUserData = tagListUser.getData();
                if (listUserData == null) {
                    break;
                }
                openidList = listUserData.getOpenidList();
                toArray = openidList.<String>toArray(new String[0]);
                setOperations.add(toArray);
                nextOpenid = tagListUser.getNextOpenid();
            }

        } catch (WxErrorException e) {
            e.printStackTrace();
        }

    }


    /**
     * 统计微信标签下 各粉丝的数量
     *
     * @param notificationTarget
     * @return
     * @throws WxErrorException
     */
    @Override
    public Map<NotificationTarget, Integer> countWxTagUser(NotificationTarget notificationTarget) throws WxErrorException {

        Map<NotificationTarget, Integer> map = new HashMap<>();
        Config config = getConfigByTenant();
        if (Objects.isNull(config)) {
            return map;
        }
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpUserTagService userTagService = wxService.getUserTagService();
        List<WxUserTag> wxUserTags = userTagService.tagGet();
        // 患者粉丝有多少
        if (NotificationTarget.ALL.equals(notificationTarget) || NotificationTarget.PATIENT.equals(notificationTarget)) {
            map.put(NotificationTarget.PATIENT, 0);
            for (WxUserTag userTag : wxUserTags) {
                if (userTag.getName().equals(TagsEnum.PATIENT_TAG.getValue())) {
                    map.put(NotificationTarget.PATIENT, userTag.getCount());
                    break;
                }
            }
        }
        // 医生粉丝有多少
        if (NotificationTarget.ALL.equals(notificationTarget) || NotificationTarget.DOCTOR.equals(notificationTarget)) {
            map.put(NotificationTarget.DOCTOR, 0);
            for (WxUserTag userTag : wxUserTags) {
                if (userTag.getName().equals(TagsEnum.DOCTOR_TAGS.getValue())) {
                    map.put(NotificationTarget.DOCTOR, userTag.getCount());
                    break;
                }
            }
        }
        // 游客粉丝有多少
        if (NotificationTarget.ALL.equals(notificationTarget) || NotificationTarget.TOURISTS_TAG.equals(notificationTarget)) {
            map.put(NotificationTarget.TOURISTS_TAG, 0);
            for (WxUserTag userTag : wxUserTags) {
                if (userTag.getName().equals(TagsEnum.TOURISTS_TAG.getValue())) {
                    map.put(NotificationTarget.TOURISTS_TAG, userTag.getCount());
                    break;
                }
            }
        }
        if (NotificationTarget.ALL.equals(notificationTarget) || NotificationTarget.CARING_SASS_PATIENT.equals(notificationTarget)) {
            map.put(NotificationTarget.CARING_SASS_PATIENT, 0);
            for (WxUserTag userTag : wxUserTags) {
                if (userTag.getName().equals(TagsEnum.CARING_SASS_PATIENT.getValue())) {
                    map.put(NotificationTarget.CARING_SASS_PATIENT, userTag.getCount());
                    break;
                }
            }
        }
        // 无标签
        if (NotificationTarget.ALL.equals(notificationTarget) || NotificationTarget.NO_TAG.equals(notificationTarget)) {
            map.put(NotificationTarget.NO_TAG, 0);
            // 查询一下粉丝列表。 把粉丝的总数拿到。
            WxMpUserService userService = wxService.getUserService();
            // 粉丝总数
            WxMpUserList userList = userService.userList(null);
            long userListTotal = userList.getTotal();
            // 有标签的总人数
            long tagUserTotal = 0;
            // 总数减去有标签的。就是无标签的总数
            for (WxUserTag userTag : wxUserTags) {
                Integer count = userTag.getCount();
                if (count == null) {
                    continue;
                }
                if (userTag.getName().equals(TagsEnum.TOURISTS_TAG.getValue())) {
                    tagUserTotal += count;
                } else if (userTag.getName().equals(TagsEnum.DOCTOR_TAGS.getValue())) {
                    tagUserTotal += count;
                } else if (userTag.getName().equals(TagsEnum.PATIENT_TAG.getValue())) {
                    tagUserTotal += count;
                }
            }
            long total = userListTotal - tagUserTotal;
            if (total < 0) {
                map.put(NotificationTarget.NO_TAG, 0);
            } else {
                map.put(NotificationTarget.NO_TAG, Integer.parseInt(total + ""));
            }
        }
        return map;
    }

    /**
     * 预览群发 图文、 视频、 语音、文本、 图片消息
     *
     * @param message
     * @return
     */
    @Override
    public R<WxMpMassSendResult> massMessagePreview(WxMpMassPreviewMessage message,
                                                    WxMpMassNews news,
                                                    WxMpMassVideo video) {
        Config config = getConfigByTenant();
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpMassMessageService messageService = wxService.getMassMessageService();
        if (Objects.nonNull(news)) {
            WxMpMassUploadResult newsUpload = massNewsUpload(wxService, news);
            message.setMediaId(newsUpload.getMediaId());
        }
        if (Objects.nonNull(video)) {
            WxMpMassUploadResult videoUpload = massVideoUpload(messageService, video);
            message.setMediaId(videoUpload.getMediaId());
        }
        try {
            WxMpMassSendResult messagePreview = messageService.massMessagePreview(message);
            return R.success(messagePreview);
        } catch (WxErrorException wxErrorException) {
            int errorCode = wxErrorException.getError().getErrorCode();
            if (errorCode == 43004) {
                return R.fail(errorCode, "微信号未关注公众号，请关注后重试");
            } else {
                apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(wxErrorException.getError())).build());
                throw new BizException(wxErrorException.getError().getErrorMsg());
            }
        }
    }

    /**
     * 根据openId 群发视频
     *
     * @param message
     * @return
     */
    @Override
    public R<WxMpMassSendResult> massOpenIdsMessageSend(WxMpMassOpenIdsMessage message, WxMpMassTagMessage tagMessage, WxMpMassVideo video) {

        Config config = getConfigByTenant();
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        SaasWxMpMassMessageServiceImpl messageService = new SaasWxMpMassMessageServiceImpl(wxService);
        WxMpMassUploadResult videoUpload = massVideoUpload(messageService, video);
        WxMpMassSendResult messageSend;
        if (Objects.nonNull(message)) {
            message.setMediaId(videoUpload.getMediaId());
            try {
                messageSend = messageService.massOpenIdsMessageSend(message);
                return R.success(messageSend);
            } catch (WxErrorException wxErrorException) {
                WxError error = wxErrorException.getError();
                apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(error)).build());
                log.info("群发消息失败 {} {} {}", error.getErrorCode(), error.getErrorMsg(), error.getErrorMsgEn());
                return R.fail(error.getErrorCode(), error.getErrorMsg());
            }
        } else if (Objects.nonNull(tagMessage)) {
            tagMessage.setMediaId(videoUpload.getMediaId());
            try {
                messageSend = messageService.massGroupMessageSend(tagMessage);
                return R.success(messageSend);
            } catch (WxErrorException wxErrorException) {
                WxError error = wxErrorException.getError();
                apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(error)).build());
                log.info("群发消息失败 {} {} {}", error.getErrorCode(), error.getErrorMsg(), error.getErrorMsgEn());
                return R.fail(error.getErrorCode(), error.getErrorMsg());
            }
        } else {
            return null;
        }

    }


    /**
     * 上传图文消息内的图片获取URL
     * 直接上传 图片文件 到微信 图文素材
     * url: https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN
     *
     * @param appId
     * @param simpleFile
     */
    @Override
    public String mediaImgUpload(String appId, MultipartFile simpleFile) {
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        WxMpMaterialService materialService = wxService.getMaterialService();
        File file = com.caring.sass.wx.utils.ImageUtils.multipartFileToFile(simpleFile);
        if (file.exists()) {
            return mediaImgUpload(materialService, file);
        }
        throw new BizException("文件不存在");
    }


    /**
     * @return me.chanjar.weixin.common.bean.result.WxMediaUploadResult
     * @Author yangShuai
     * @Description 上传临时素材到微信公众号
     * @Date 2020/9/16 15:04
     */
    @Override
    public WxMediaUploadResult uploadTemporaryMatrial(String appId, UploadTemporaryMatrialForm mediaDto) {
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);
        if (!StringUtils.isEmpty(mediaDto.getUrl())) {
            try {
                InputStream inputStream = FileUtils.getInputStreamFromUrl(mediaDto.getUrl());

                WxMediaUploadResult var7;
                try {
                    var7 = wxService.getMaterialService().mediaUpload(mediaDto.getMediaType().name(), mediaDto.getFileType().name(), inputStream);
                } catch (Throwable var18) {
                    throw var18;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }

                return var7;
            } catch (Exception var21) {
                throw new BizException(var21.getMessage());
            }
        } else if (!StringUtils.isEmpty(mediaDto.getPath())) {
            File file = new File(mediaDto.getPath());
            if (file.exists()) {
                try {
                    return wxService.getMaterialService().mediaUpload(mediaDto.getMediaType().name(), file);
                } catch (Exception var19) {
                    throw new BizException(var19.getMessage());
                }
            } else {
                throw new BizException("File doesnot exist :" + file.getPath() + "!");
            }
        } else {
            throw new BizException("Upload metrial error , the parameter path/url doesnot exist in mediaDto!");
        }
    }

    /**
     * @param paramSignatureForm
     * @return me.chanjar.weixin.common.bean.WxJsapiSignature
     * @Author yangShuai
     * @Description 获取授权页ticket
     * @Date 2020/9/16 15:17
     */
    @Override
    public WxJsapiSignature signature(SignatureForm paramSignatureForm) {
        Config config = getConfig(paramSignatureForm);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());

        try {
            return wxService.createJsapiSignature(paramSignatureForm.getUrl());
        } catch (Exception var5) {
            log.error(var5.getMessage());
        }
        return null;
    }


    /**
     *
     * @Author yangShuai
     * @Description 微信回调时会返回code给应用，应用通过code调用微信的oauth2getUserInfo接口来从微信公众平台获取用户信息
     * @Date 2020/9/16 15:18
     */
    @Override
    public WxOAuth2UserInfo oauth2getUserInfo(Oauth2getUserInfoForm oauth2getUserInfoForm) {

        Config config = getConfig(oauth2getUserInfoForm);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxOAuth2UserInfo wxMpUser = null;
        WxOAuth2Service oAuth2Service = wxService.getOAuth2Service();
        try {
            // 授权给第三方平台的走这里拿用户的信息
            if (config.getThirdAuthorization() != null && config.getThirdAuthorization()) {
                WxOAuth2AccessToken accessToken = wxUserAuthLoginService.getAccessToken(oauth2getUserInfoForm.getCode(), config.getAppId());
                wxMpUser = oAuth2Service.getUserInfo(accessToken, (String) null);
            } else {
                WxOAuth2AccessToken accessToken = oAuth2Service.getAccessToken(oauth2getUserInfoForm.getCode());
                wxMpUser = oAuth2Service.getUserInfo(accessToken, (String) null);
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new BizException("微信授权， 通过code获取微信用户信息失败");
        }
        return wxMpUser;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 获取accessToken
     * @Date 2020/9/16 15:26
     */
    @Override
    public WxOAuth2UserInfo oauth2RefreshAccessToken(Oauth2getUserInfoForm oauth2getUserInfoForm) {
        Config config = getConfig(oauth2getUserInfoForm);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            WxOAuth2Service oauth2Service = wxService.getOAuth2Service();
            if (config.getThirdAuthorization() != null && config.getThirdAuthorization()) {
                WxOAuth2AccessToken accessToken = wxUserAuthLoginService.getAccessToken(oauth2getUserInfoForm.getCode(), config.getAppId());
                return oauth2Service.getUserInfo(accessToken, null);
            } else {
                WxOAuth2AccessToken wxMpOAuth2AccessToken = oauth2Service.getAccessToken(oauth2getUserInfoForm.getCode());
                return oauth2Service.getUserInfo(wxMpOAuth2AccessToken, null);
            }
        } catch (WxErrorException var7) {
            apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(var7.getError())).build());
            throw new BizException("微信授权， 通过code获取微信用户信息失败");
        }
    }

    /**
     * @return me.chanjar.weixin.common.bean.result.WxMediaUploadResult
     * @Author yangShuai
     * @Description 上传临时素材到微信公众号
     * @Date 2020/9/16 15:30
     */
    @Override
    public WxMediaUploadResult uploadTemporaryMatrial(UploadTemporaryMatrialForm uploadTemporaryMatrialForm) {
        Config config = getConfig(uploadTemporaryMatrialForm);
        WxMediaUploadResult uploadResult = uploadTemporaryMatrial(config.getAppId(), uploadTemporaryMatrialForm);
        return uploadResult;
    }

    /**
     * @return me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult
     * @Author yangShuai
     * @Description 上传永久素材消息
     * @Date 2020/9/17 15:20
     */
    @Override
    public WxMpMaterialUploadResult uploadPermanentMatrial(UploadTemporaryMatrialForm uploadTemporaryMatrialForm) {

        Config config = getConfig(uploadTemporaryMatrialForm);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpMaterial wxMaterial = new WxMpMaterial();
        try {
            String dir = System.getProperty("java.io.tmpdir");
            String fileName = "Matrial" + System.currentTimeMillis();
            File file = FileUtils.downLoadFromFile(uploadTemporaryMatrialForm.getUrl(), fileName, dir);
            if (Objects.isNull(file)) {
                return null;
            }
            wxMaterial.setName(file.getName());
            wxMaterial.setFile(file);
            return wxService.getMaterialService().materialFileUpload(uploadTemporaryMatrialForm.getMediaType().name(), wxMaterial);
        } catch (IOException | WxErrorException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 删除永久素材
     *
     * @param materialId 1、请谨慎操作本接口，因为它可以删除公众号在公众平台官网素材管理模块中新建的图文消息、语音、视频等素材（但需要先通过获取素材列表来获知素材的media_id）
     *                   2、临时素材无法通过本接口删除
     */
    @Override
    public Boolean deleteMaterial(String materialId) {
        Config config = new Config();
        config = getConfig(config);
        if (StringUtils.isEmpty(config.getAppId())) {
            return false;
        }
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            wxService.getMaterialService().materialDelete(materialId);
            return true;
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @return java.lang.String
     * @Author yangShuai
     * @Description 下载微信素材到 系统云存储
     * @Date 2020/9/16 15:33
     */
    @Override
    public String downloadMatrial(DownloadMatrialForm downloadMatrialForm) {

        Config config = getConfig(downloadMatrialForm);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            File file = wxService.getMaterialService().mediaDownload(downloadMatrialForm.getMediaId());

            MultipartFile simpleFile = FileUtils.fileToFileItem(file);
            R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(0L, simpleFile);
            file.delete();
            return upload.getData().getUrl();
        } catch (Exception var6) {
        }
        return null;
    }

    /**
     * 获取 js sdk上传的临时语音
     *
     * @param downloadMatrialForm
     * @return
     */
    @Override
    public String get_material(DownloadMatrialForm downloadMatrialForm) {
        Config config = getConfig(downloadMatrialForm);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        File file = null;
        String fileWavPath = "";
        try {
            file = wxService.getMaterialService().jssdkMediaDownload(downloadMatrialForm.getMediaId());
            if (file == null) {
                return null;
            }
            String filePath = file.getPath();
            fileWavPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".wav";
            SpeexUtils.execCommand(filePath, fileWavPath);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
        if (!StringUtils.isEmpty(fileWavPath)) {
            File fileWav = new File(fileWavPath);
            MultipartFile simpleFile = FileUtils.fileToFileItem(fileWav);
            R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(0L, simpleFile);
            file.delete();
            fileWav.delete();
            return upload.getData().getUrl();
        }
        return null;
    }

    /**
     * 获取素材列表
     *
     * @param offset 起始下标
     * @param count  分页数量
     * @return
     */
    @Override
    public WxMpMaterialNewsBatchGetResult batchgetMmaterial(int offset, int count) {
        Config config = getConfig(new Config());
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            WxMpMaterialNewsBatchGetResult batchGet = wxService.getMaterialService().materialNewsBatchGet(offset, count);
            return batchGet;
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取草稿箱列表
     *
     * @param offset
     * @param count
     * @return
     */
    @Override
    public String batchGet(int offset, int count) {
        Config config = getConfig(new Config());
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("offset", offset);
            jsonObject.put("count", count);
            jsonObject.put("no_content", 0);
            return wxService.post("https://api.weixin.qq.com/cgi-bin/draft/batchget", jsonObject);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 为用户绑定 微信标签
     * @Date 2020/9/16 15:50
     */
    @Override
    public void bindUserTags(BindUserTagsForm bindUserTagsForm) {
        String wxAppId = bindUserTagsForm.getWxAppId();
        WxMpService wxService;
        if (StrUtil.isEmpty(wxAppId)) {
            Config config = getConfig(bindUserTagsForm);
            wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        } else {
            wxService = WxMpServiceHolder.getWxMpService(wxAppId);
        }
        try {
            WxMpUserTagService tagService = wxService.getUserTagService();
            tagService.batchTagging(this.getTags(wxService, bindUserTagsForm.getTagId()).getId(),
                    bindUserTagsForm.getOpenIds().split(","));

            // 检查是否还要清除标签
            List<String> clearTagIdList = bindUserTagsForm.getClearTagId();
            if (CollUtil.isNotEmpty(clearTagIdList)) {
                for (String clearTagId : clearTagIdList) {
                    WxUserTag tags = this.getTags(wxService, clearTagId);
                    Long tagsId = tags.getId();
                    String[] openIds = bindUserTagsForm.getOpenIds().split(",");
                    tagService.batchUntagging(tagsId, openIds);
                }

            }
        } catch (Exception var6) {
            log.error("config service bindUserTags error {}", JSON.toJSONString(bindUserTagsForm));
            var6.printStackTrace();
        }
    }

    /**
     * 获取微信标签。不存在则创建标签
     *
     * @param wxMpService
     * @param value
     * @return
     * @throws WxErrorException
     */
    private WxUserTag getTags(WxMpService wxMpService, String value) throws WxErrorException {
        List<WxUserTag> wxUserTags = wxMpService.getUserTagService().tagGet();
        if (!CollectionUtils.isEmpty(wxUserTags)) {
            for (WxUserTag wxUserTag : wxUserTags) {
                if (wxUserTag.getName().equals(value)) {
                    return wxUserTag;
                }
            }
        }

        return wxMpService.getUserTagService().tagCreate(value);
    }


    /**
     *
     * @Author yangShuai
     * @Description 获取微信用户信息
     * @Date 2020/9/16 15:51
     */
    @Override
    public WxMpUser getFollowerInfo(GetFollowerInfoForm form) {
        Config config = getConfig(form);
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        try {
            return wxService.getUserService().userInfo(form.getUserOpenId());
        } catch (Exception var5) {
            throw new BizException("获取用户信息失败");
        }
    }

    /**
     * 为 一个公众号的关注者创建永久二维码|临时二维码 根据是否有过期时间决定
     * @param createFollowerPermanentQrCode
     * @return
     */
    @Override
    public QrCodeDto createFollowerPermanentQrCode(CreateFollowerPermanentQrCode createFollowerPermanentQrCode) {

        Config config = getConfig(createFollowerPermanentQrCode);

        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpQrcodeService qrcodeService = wxService.getQrcodeService();

        try {
            WxMpQrCodeTicket wxMpQrCodeTicket = null;
            if (!ObjectUtils.isEmpty(createFollowerPermanentQrCode) && createFollowerPermanentQrCode.getExpireSeconds() != null) {
                wxMpQrCodeTicket = qrcodeService.qrCodeCreateTmpTicket(createFollowerPermanentQrCode.getParams(), createFollowerPermanentQrCode.getExpireSeconds());
            } else {
                wxMpQrCodeTicket = qrcodeService.qrCodeCreateLastTicket(createFollowerPermanentQrCode.getParams());
            }
            File file = qrcodeService.qrCodePicture(wxMpQrCodeTicket);
            MultipartFile multipartFile = FileUtils.fileToFileItem(file);
            R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(0L, multipartFile);
            if (upload.getIsSuccess()) {
                Rectangle rect = ImageUtils.getImageRect(file);
                QrCodeDto qrcode = new QrCodeDto();
                qrcode.setHeight(rect.height);
                qrcode.setWidth(rect.width);
                com.caring.sass.file.entity.File data = upload.getData();
                if (Objects.isNull(data)) {
                    return null;
                }
                qrcode.setUrl(data.getUrl());
                return qrcode;
            } else {
                return null;
            }
        } catch (Exception var10) {
            log.error(var10.getMessage());
        }
        return null;
    }

    /**
     * 创建一个静默授权的 url
     * @param appId
     * @param form
     * @return
     */
    private String createSilentAuthUrl(String appId, CreateSilentAuthUrlForm form) {
        StringBuilder sb = new StringBuilder("https://open.weixin.qq.com/connect/oauth2/authorize?appid=");
        sb.append(appId);
        sb.append("&redirect_uri=" + form.getRedirectUri());
        sb.append("&response_type=code&scope=snsapi_userinfo");
        if (!StringUtils.isEmpty(form.getOpenId())) {
            sb.append("&state=" + form.getOpenId());
        }

        sb.append("#wechat_redirect");
        return sb.toString();
    }

    /**
     * @return com.caring.sass.base.R<java.lang.String>
     * @Author yangShuai
     * @Description 创建一个静默授权的 url
     * @Date 2020/9/16 16:08
     */
    @Override
    public R<String> createSilentAuthUrl(CreateSilentAuthUrlForm createSilentAuthUrlForm) {

        Config config = getConfig(createSilentAuthUrlForm);
        try {
            String url = createSilentAuthUrl(config.getAppId(), createSilentAuthUrlForm);
            return R.success(url);
        } catch (Exception var15) {
            return R.fail(var15.getMessage());
        }

    }

    @Override
    public R<Object> slientAuthLoginCheck(SlientAuthLoginCheckForm slientAuthLoginCheckForm) {
        Config config = getConfig(slientAuthLoginCheckForm);
        try {
            if (config.getAppId() != null) {
                R<Object> objectR = this.slientAuthLoginCheck(config.getAppId(), slientAuthLoginCheckForm.getCode());
                return objectR;
            }
            return R.fail("静默授权登录失败");
        } catch (Exception var15) {
            return R.fail("静默授权登录失败");
        }
    }


    /**
     * @return com.caring.sass.base.R<java.lang.Object>
     * @Author yangShuai
     * @Description 为 微信用户 请求系统授权登录
     * @Date 2020/9/16 16:10
     */
    public R<Object> slientAuthLoginCheck(String appId, String code) {
        String openId = null;
        WxMpService wxService = WxMpServiceHolder.getWxMpService(appId);

        try {
            openId = wxService.getOAuth2Service().getAccessToken(code).getOpenId();
        } catch (Exception var6) {
            throw new BizException("微信用户登录获取授权失败");
        }

        return null;
    }

    @Override
    public void saveNotCheck(Config config) {
        Config config1 = getConfigByAppId(config.getAppId());
        if (Objects.nonNull(config1)) {
            throw new BizException("公众号已经被使用");
        }
        config.setToken(Key.key());
        baseMapper.insert(config);
    }

    public boolean varifyTokenForVerifyDomainName(String signature, String timestamp, String nonce) {

        List<Config> list = this.list();
        Iterator var5 = list.iterator();
        boolean accept;
        do {
            if (!var5.hasNext()) {
                return false;
            }
            Config config = (Config) var5.next();
            WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
            accept = wxService.checkSignature(timestamp, nonce, signature);

        } while (!accept);

        return true;
    }


    @Override
    public R varifyTokenForVerifyDomainName(VarifyTokenForVerifyDomainNameForm form) {
        try {
            String result = "";
            boolean valid = this.varifyTokenForVerifyDomainName(form.getSignature(), form.getTimestamp(), form.getNonce());
            if (valid) {
                result = form.getEchostr();
            }
            return R.success(result);
        } catch (Exception var4) {
            return R.fail(var4.getMessage());
        }
    }

    /**
     * 查询所有的公众号信息
     * @return
     */
    @Override
    public List<Config> listAllConfigWithoutTenant() {
        return baseMapper.listAllConfigWithoutTenant();
    }

    @Override
    public List<Config> queryByAppIdWithoutTenant(String appId) {
        if (StrUtil.isBlank(appId)) {
            baseMapper.listAllConfigWithoutTenant();
        }
        return baseMapper.selectByAppIdWithoutTenant(appId);
    }

    @Override
    public String getWxUserOpenId(String appId, String code) {
        try {
            Config config = getConfigByAppId(appId);
            WxOAuth2Service oAuth2Service = WxMpServiceHolder.getWxMpService(appId).getOAuth2Service();
            if (config.getThirdAuthorization() != null && config.getThirdAuthorization()) {
                WxOAuth2AccessToken accessToken = wxUserAuthLoginService.getAccessToken(code, appId);
                return accessToken.getOpenId();
            } else {
                WxOAuth2AccessToken wxMpOAuth2AccessToken = oAuth2Service.getAccessToken(code);
                return wxMpOAuth2AccessToken.getOpenId();
            }
        } catch (WxErrorException e) {
            WxError error = e.getError();
            apiErrorMapper.insert(ApiError.builder().wxAppId(appId).wxErrorMessage(JSON.toJSONString(error)).build());
            log.error("weixin_error_code: {} message：{}", error.getErrorCode(), error.getErrorMsg());
            throw new BizException("获取微信openId失败");
        }
    }

    @Override
    public WxOAuth2UserInfo getWxUser(String code) {
        Config config = getConfigByTenant();
        return getWxUser(config.getAppId(), code);
    }

    @Override
    public WxOAuth2UserInfo getWxUser(String appId, String code) {
        try {
            Config config = getConfigByAppId(appId);
            WxOAuth2Service oAuth2Service = WxMpServiceHolder.getWxMpService(appId).getOAuth2Service();
            if (config.getThirdAuthorization() != null && config.getThirdAuthorization()) {
                WxOAuth2AccessToken accessToken = wxUserAuthLoginService.getAccessToken(code, appId);
                return oAuth2Service.getUserInfo(accessToken, "zh_CN");
            } else {
                WxOAuth2AccessToken wxMpOAuth2AccessToken = oAuth2Service.getAccessToken(code);
                return oAuth2Service.getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
            }
        } catch (WxErrorException e) {
            WxError error = e.getError();
            apiErrorMapper.insert(ApiError.builder().wxAppId(appId).wxErrorMessage(JSON.toJSONString(error)).build());
            log.error("weixin_error_code: {} message：{}", error.getErrorCode(), error.getErrorMsg());
            throw new BizException("获取微信用户信息失败");
        }
    }


    @Override
    public WxJsapiSignature createJsapiSignature(String appId, String url) {
        try {
            return WxMpServiceHolder.getWxMpService(appId).createJsapiSignature(url);
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }


    @Override
    public String addvoicetorecofortext(String voiceUrl, AiLangType lang) {
        String dir = System.getProperty("java.io.tmpdir");
        String fileName = "Matrial" + System.currentTimeMillis();
        File file = null;
        try {
            file = FileUtils.downLoadFromFile(voiceUrl, fileName, dir);
        } catch (IOException e) {
            return null;
        }
        if (Objects.isNull(file)) {
            return null;
        }
        Config config = getConfig(new Config());
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpAiOpenService aiOpenService = wxService.getAiOpenService();

        UUID uuid = UUID.randomUUID();
        String voiceId = uuid.toString().replace("-", "");
        try {
            aiOpenService.uploadVoice(voiceId, lang, file);
        } catch (WxErrorException e) {
            log.info("上传语音文件到微信ai失败");
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
        try {
            return aiOpenService.queryRecognitionResult(voiceId, lang);
        } catch (WxErrorException e) {
            apiErrorMapper.insert(ApiError.builder().wxAppId(config.getAppId()).wxErrorMessage(JSON.toJSONString(e.getError())).build());
            log.info("获取微信翻译后的文件内容");
        }
        return null;
    }


    @Override
    public List<String> checkFolderShareUrlExist(String url) {

        List<String> tenantCodeList = new ArrayList<>();
        String shareUrl = null;
        try {
            shareUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return tenantCodeList;
        }
        List<Config> configList = baseMapper.selectMenuLikeUrl("%" + shareUrl + "%");
        if (CollUtil.isNotEmpty(configList)) {
            for (Config config : configList) {
                String tenantCode = config.getTenantCode();
                tenantCodeList.add(tenantCode);
            }
        }

        // 检查自定义菜单中是否有添加这个链接
        List<CustomMenu> customMenus = customMenuMapper.selectMenuLikeUrl("%" + shareUrl + "%");
        if (CollUtil.isNotEmpty(customMenus)) {
            for (CustomMenu customMenu : customMenus) {
                String tenantCode = customMenu.getTenantCode();
                tenantCodeList.add(tenantCode);
            }
        }
        return tenantCodeList;
    }


    /**
     * 公众号迁移查询openId在新公众号的openId
     *
     * @param officialAccountMigrationDTO
     * @return
     */
    @Override
    public Map<String, String> officialAccountMigration(OfficialAccountMigrationDTO officialAccountMigrationDTO) {
        List<String> dtoOpenIds = officialAccountMigrationDTO.getOpenIds();
        if (CollUtil.isEmpty(dtoOpenIds)) {
            return new HashMap<>();
        }
        Config config = getConfigByTenant();
        if (Objects.isNull(config)) {
            log.error("officialAccountMigration error config is null, tenant code is {}", BaseContextHandler.getTenant());
        }
        WxMpService wxService = WxMpServiceHolder.getWxMpService(config.getAppId());
        WxMpUserService userService = wxService.getUserService();
        List<List<String>> subList = ListUtils.subList(dtoOpenIds, 100);
        Map<String, String> openIdMapId = new HashMap<>();
        for (List<String> stringList : subList) {
            try {
                List<WxMpChangeOpenid> mpChangeOpenIds = userService.changeOpenid(officialAccountMigrationDTO.getOldAppId(), stringList);
                if (CollUtil.isNotEmpty(mpChangeOpenIds)) {
                    for (WxMpChangeOpenid changeOpenid : mpChangeOpenIds) {
                        if (StrUtil.isEmpty(changeOpenid.getOriOpenid()) || StrUtil.isEmpty(changeOpenid.getNewOpenid())) {
                            continue;
                        }
                        openIdMapId.put(changeOpenid.getOriOpenid(), changeOpenid.getNewOpenid());
                    }
                }
            } catch (WxErrorException e) {
                log.error("officialAccountMigration  changeOpenid error");
                e.printStackTrace();
            }
        }

        return openIdMapId;
    }


    @Override
    public void sendTouristRegister(WxSubscribeDto subscribeDto, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        SendKefuMsgForm sendKefuMsgForm = new SendKefuMsgForm();
        WxMpKefuMessage message = new WxMpKefuMessage();
        message.setToUser(subscribeDto.getOpenId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        String bizUrl = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                H5Router.H5_RULE_SELECT);

        Locale locale = subscribeDto.getLocale();
        String clickHereRegister = I18nUtils.getMessage("PLEASE_CLICK_HERE_REGISTER", locale);
        message.setContent("<a href=\"" + bizUrl + "\">"+clickHereRegister+"</a>");
        sendKefuMsgForm.setMessage(message);
        sendKefuMsg(sendKefuMsgForm);
    }

    @Override
    public void initAssistantMenu() {
        List<Config> configs = baseMapper.listAllConfigWithoutTenant();
        for (Config config : configs) {
            String tenantCode = config.getTenantCode();
            if (StrUtil.isEmpty(tenantCode)) {
                continue;
            }
            BaseContextHandler.setTenant(tenantCode);
            R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
            Tenant tenant = tenantR.getData();
            if (Objects.isNull(tenant)) {
                continue;
            }
            WxUserTag assistantTag = createTagIfNotExist(config.getAppId(), TagsEnum.ASSISTANT_TAG.getValue());
            // 更新 医助的菜单
            WxMenu wxMenu = new WxMenu();
            List<WxMenuButton> youKeMenu = new ArrayList<>();
            WxMenuButton button = new WxMenuButton();
            String bizUrl = ApplicationDomainUtil.wxAssistantBaseDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()));
            button.setUrl(bizUrl);
            button.setName("首页");
            button.setType("view");
            youKeMenu.add(button);
            wxMenu.setButtons(youKeMenu);
            WxMenuRule rule = new WxMenuRule();
            rule.setTagId(assistantTag.getId().toString());
            WxMpService mpService = WxMpServiceHolder.getWxMpService(config.getAppId());
            wxMenu.setMatchRule(rule);
            try {
                WxMpMenuService menuService = mpService.getMenuService();
                menuService.menuCreate(wxMenu);
            } catch (Exception e) {
                log.error("getWxMpService error {} configName {}, error info: {}", config.getAppId(), config.getName(), e.getMessage());
                return;
            }
            String accessToken = null;
            try {
                accessToken = mpService.getAccessToken();
                String result = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=[?]".replace("[?]", accessToken));
                JSONObject jsonObject = JSONObject.parseObject(result);
                config.setMenu(jsonObject.toJSONString());
                baseMapper.updateById(config);
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }
    }


}

