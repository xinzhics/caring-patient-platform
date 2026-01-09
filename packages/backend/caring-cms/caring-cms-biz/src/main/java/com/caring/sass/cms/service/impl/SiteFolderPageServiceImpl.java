package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.constant.SiteJumpTypeEnum;
import com.caring.sass.cms.constant.SitePageModuleType;
import com.caring.sass.cms.constant.SitePageTemplateType;
import com.caring.sass.cms.dao.SiteFolderPageMapper;
import com.caring.sass.cms.entity.*;
import com.caring.sass.cms.service.SiteFolderPageModuleService;
import com.caring.sass.cms.service.SiteFolderPageService;
import com.caring.sass.cms.service.SiteModulePlateService;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 建站文件夹中的页面表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Service

public class SiteFolderPageServiceImpl extends SuperServiceImpl<SiteFolderPageMapper, SiteFolderPage> implements SiteFolderPageService {

    @Autowired
    RedisTemplate<String, String> redisTemplates;

    @Autowired
    SiteFolderPageModuleService siteFolderPageModuleService;

    @Autowired
    SiteModulePlateService siteModulePlateService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 创建一个页面
     * @param model
     * @return
     */
    @Override
    public boolean save(SiteFolderPage model) {
        if (StrUtil.isEmpty(model.getPageName())) {
            model.setPageName(SiteFolderPage.DEFAULT_PAGE_NAME);
        }
        model.setHomeStatus(CommonStatus.NO);
        model.setTemplate(CommonStatus.NO);
        return super.save(model);
    }


    /**
     * 更新页面 和组件信息
     * @param folderId
     * @param siteFolderPages
     * @return
     */
    @Override
    @Transactional
    public String saveOrUpdateSiteFolder(Long folderId, List<SiteFolderPage> siteFolderPages) {
        if (CollUtil.isEmpty(siteFolderPages)) {
            return null;
        }
        List<SiteFolderPage> folderPages = baseMapper.selectList(Wraps.<SiteFolderPage>lbQ()
                .select(SuperEntity::getId, SiteFolderPage::getPageName)
                .eq(SiteFolderPage::getFolderId, folderId));
        List<Long> collect = folderPages.stream().map(SuperEntity::getId).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        String key = String.format(SaasRedisBusinessKey.CMS_SITE_BUILD_PAGE_SAVE, folderId);
        try {
            Long l = Long.MAX_VALUE - siteFolderPages.size() + 1;
            redisTemplate.opsForValue().set(key, l.toString());
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("saveOrUpdateSiteFolder set key {} error", key);
        }
        for (SiteFolderPage folderPage : siteFolderPages) {
            Long pageId = folderPage.getId();
            if (Objects.nonNull(pageId)) {
                collect.remove(pageId);
            }
            List<SiteFolderPageModule> moduleList = folderPage.getModuleList();
            folderPage.setFolderId(folderId);
            folderPage.setCreateTime(null);
            folderPage.setCreateUser(null);
            baseMapper.updateById(folderPage);
            siteFolderPageModuleService.saveOrUpdate(folderId, pageId, stringBuilder, moduleList);
        }
        if (CollUtil.isNotEmpty(collect)) {
            removeByIds(collect);
        }
        return stringBuilder.toString();

    }

    /**
     * 当页面是新增时 处理。
     * @param moduleList
     */
    private void clearPageInfoId(List<SiteFolderPageModule> moduleList) {
        if (CollUtil.isEmpty(moduleList)) {
            return;
        }
        for (SiteFolderPageModule module : moduleList) {
            module.setId(null);
            // 清除组件跳转的ID
            SiteJumpInformation information = module.getJumpInformation();
            if (Objects.nonNull(information)) {
                information.setId(null);
                if (SiteJumpTypeEnum.PAGE.equals(information.getJumpType())) {
                    information.setJumpPageId(null);
                }
            }
            // 清除组件标题样式设置的ID
            List<SiteModuleTitleStyle> titleStyles = module.getModuleTitleStyles();
            if (CollUtil.isNotEmpty(titleStyles)) {
                titleStyles.forEach(siteModuleTitleStyle -> siteModuleTitleStyle.setId(null));
            }

            // 清除组件中 标签的id 标签下板块的id. 板块跳转的ID
            List<SiteModuleLabel> moduleLabelList = module.getModuleLabelList();
            if (CollUtil.isNotEmpty(moduleLabelList)) {
                moduleLabelList.forEach(siteModuleLabel -> {
                    siteModuleLabel.setId(null);
                    List<SiteModulePlate> plates = siteModuleLabel.getLabelPlates();
                    if (CollUtil.isNotEmpty(plates)) {
                        plates.forEach(siteModulePlate -> {
                            siteModulePlate.setId(null);
                            SiteJumpInformation jumpInformation = siteModulePlate.getJumpInformation();
                            if (Objects.nonNull(jumpInformation)) {
                                jumpInformation.setId(null);
                                if (SiteJumpTypeEnum.PAGE.equals(jumpInformation.getJumpType())) {
                                    jumpInformation.setJumpPageId(null);
                                }
                            }
                        });
                    }
                });
            }

            // 清除组件 板块的id. 板块跳转的ID
            List<SiteModulePlate> modulePlates = module.getModulePlates();
            if (CollUtil.isNotEmpty(modulePlates)) {
                modulePlates.forEach(siteModulePlate -> {
                    siteModulePlate.setId(null);
                    SiteJumpInformation jumpInformation = siteModulePlate.getJumpInformation();
                    if (Objects.nonNull(jumpInformation)) {
                        jumpInformation.setId(null);
                        if (SiteJumpTypeEnum.PAGE.equals(jumpInformation.getJumpType())) {
                            jumpInformation.setJumpPageId(null);
                        }
                    }
                });
            }
        }

    }

    /**
     * 查询文件夹的所有页面。包括他页面的组件信息
     * @param folderId
     * @return
     */
    @Override
    public List<SiteFolderPage> queryAllPageByFolderId(Long folderId) {

        List<SiteFolderPage> folderPages = baseMapper.selectList(Wraps.<SiteFolderPage>lbQ().eq(SiteFolderPage::getFolderId, folderId).orderByAsc(SuperEntity::getCreateTime));

        siteFolderPageModuleService.queryModule(folderId, folderPages);
        return folderPages;
    }

    /**
     * 查询模版页面的所有信息
     * @param pageId
     * @return
     */
    @Override
    public SiteFolderPage getTemplatePageInfo(Long pageId) {

        SiteFolderPage folderPage = baseMapper.selectById(pageId);
        if (Objects.isNull(folderPage)) {
            throw new BizException("数据不存在");
        }
        siteFolderPageModuleService.queryPageModule(pageId, folderPage);
        List<SiteFolderPageModule> moduleList = folderPage.getModuleList();

        // 清除组件等id
        clearPageInfoId(moduleList);

        return folderPage;
    }

    /**
     * 查询页面。并设置页面的
     * @param pageId
     * @return
     */
    public SiteFolderPage findPage(Long pageId) {
        SiteFolderPage folderPage = baseMapper.selectById(pageId);
        siteFolderPageModuleService.queryPageModule(pageId, folderPage);
        return folderPage;
    }


    /**
     * 將模版文件夹中的页面。设置到新文件夹中
     * @param templateFolderId
     * @param folderId
     * @param homeId
     * @param replaceHome
     */
    @Override
    public List<SiteFolderPage> saveTemplateToFolder(Long templateFolderId, Long folderId, Long homeId, Boolean replaceHome) {
        List<SiteFolderPage> folderPages = baseMapper.selectList(Wraps.<SiteFolderPage>lbQ().eq(SiteFolderPage::getFolderId, templateFolderId));

        SiteFolderPage home = baseMapper.selectById(homeId);

        Map<Long, Long> pageIdMap = new HashMap<>();
        Map<Long, SiteFolderPage> pageHashMap = new HashMap<>();
        int index = 0;
        for (SiteFolderPage folderPage : folderPages) {
            Long oldPageId = folderPage.getId();
            folderPage.setTemplate(CommonStatus.NO);
            if (index == 0 && replaceHome) {
                folderPage.setFolderId(folderId);
                folderPage.setCopyNumber(0);
                folderPage.setHomeStatus(CommonStatus.YES);
                folderPage.setTemplateType(null);
                folderPage.setCreateTime(home.getCreateTime());
                folderPage.setId(homeId);
                baseMapper.updateById(folderPage);
            } else {
                folderPage.setHomeStatus(CommonStatus.NO);
                folderPage.setFolderId(folderId);
                folderPage.setCopyNumber(0);
                folderPage.setId(null);
                folderPage.setCreateTime(LocalDateTime.now());
                baseMapper.insert(folderPage);
            }
            pageIdMap.put(oldPageId, folderPage.getId());
            pageHashMap.put(oldPageId, folderPage);
            index++;
        }
        Set<Map.Entry<Long, SiteFolderPage>> entries = pageHashMap.entrySet();
        for (Map.Entry<Long, SiteFolderPage> entry : entries) {
            Long oldPageId = entry.getKey();
            SiteFolderPage folderPage = entry.getValue();
            copyPage(folderPage, oldPageId, folderPage.getId(), pageIdMap);
        }

        siteFolderPageModuleService.queryModule(folderId, folderPages);

        return folderPages;

    }

    /**
     * 复制文件夹内所有页面
     * @param oldFolder
     * @param newFolder
     */
    @Override
    public void copyPageByFolder(Long oldFolder, Long newFolder) {

        List<SiteFolderPage> folderPages = baseMapper.selectList(Wraps.<SiteFolderPage>lbQ().eq(SiteFolderPage::getFolderId, oldFolder));

        Map<Long, Long> pageIdMap = new HashMap<>();
        Map<Long, SiteFolderPage> pageHashMap = new HashMap<>();
        for (SiteFolderPage folderPage : folderPages) {

            Long oldPageId = folderPage.getId();
            folderPage.setFolderId(newFolder);
            folderPage.setCopyNumber(0);
            folderPage.setId(null);

            baseMapper.insert(folderPage);
            pageIdMap.put(oldPageId, folderPage.getId());
            pageHashMap.put(oldPageId, folderPage);
        }
        Set<Map.Entry<Long, SiteFolderPage>> entries = pageHashMap.entrySet();
        for (Map.Entry<Long, SiteFolderPage> entry : entries) {
            Long oldPageId = entry.getKey();
            SiteFolderPage folderPage = entry.getValue();
            copyPage(folderPage, oldPageId, folderPage.getId(), pageIdMap);
        }

    }


    /**
     * 复制页面内的所有元素
     */
    public void copyPage(SiteFolderPage folderPage, Long oldPageId, Long newPageId, Map<Long, Long> pageIdMap) {

        Long folderId = folderPage.getFolderId();

        siteFolderPageModuleService.copyModule(oldPageId, newPageId, folderId, pageIdMap);

    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return false;
    }

    @Override
    public boolean removeById(Serializable id) {

        siteFolderPageModuleService.deletePageInfo(Long.parseLong(id.toString()));
        baseMapper.deleteById(id);
        redisTemplates.boundHashOps("BUILD_FOLDER_PAGE").delete(id.toString());
        return true;
    }

    /**
     * 复制文件夹内的一个页面
     */
    @Override
    @Transactional
    public void copyPage(SiteFolderPage folderPage) {
        if (Objects.isNull(folderPage.getCopyNumber())) {
            folderPage.setCopyNumber(0);
        }
        folderPage.setCopyNumber(folderPage.getCopyNumber() + 1);
        baseMapper.updateById(folderPage);


        folderPage.setHomeStatus(CommonStatus.NO);

        String pageName = folderPage.getPageName();
        if (StrUtil.isEmpty(pageName)) {
            pageName = SiteFolderPage.DEFAULT_PAGE_NAME;
        }

        folderPage.setPageName(pageName + "(复制)");
        folderPage.setCopyNumber(0);
        folderPage.setId(null);
        List<SiteFolderPageModule> moduleList = folderPage.getModuleList();
        baseMapper.insert(folderPage);

        siteFolderPageModuleService.copyModule(moduleList, folderPage.getId(), true, true);
    }



    /**
     * 将前端提交的数据存成一个模版
     */
    @Override
    public void savePageTemplate(SiteFolderPage folderPage, String pageName) {

        folderPage.setCopyNumber(0);
        folderPage.setId(null);
        folderPage.setHomeStatus(CommonStatus.NO);
        folderPage.setTemplate(CommonStatus.YES);
        folderPage.setTemplateType(SitePageTemplateType.PAGE);
        folderPage.setCopyNumber(0);
        folderPage.setPageName(pageName);
        folderPage.setCreateTime(LocalDateTime.now());
        folderPage.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(folderPage);

        List<SiteFolderPageModule> moduleList = folderPage.getModuleList();
        if (CollUtil.isEmpty(moduleList)) {
            return;
        }

        siteFolderPageModuleService.copyModule(moduleList, folderPage.getId(), false, true);

    }

    /**
     * 将一个页面模板。存为系统模板
     * @param pageId
     */
    @Override
    public void saveSystemPageTemplate(Long pageId) {

        SiteFolderPage pageInfo = getTemplatePageInfo(pageId);
        pageInfo.setId(null);
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        List<SiteFolderPageModule> moduleList = pageInfo.getModuleList();

        baseMapper.insert(pageInfo);
        if (CollUtil.isEmpty(moduleList)) {
            return;
        }
        for (SiteFolderPageModule pageModule : moduleList) {
            pageModule.setJumpInformation(null);
            List<SiteModuleLabel> labelList = pageModule.getModuleLabelList();
            if (CollUtil.isNotEmpty(labelList)) {
                for (SiteModuleLabel label : labelList) {
                    label.setLabelPlates(new ArrayList<>());
                }
            }
            pageModule.setModulePlates(new ArrayList<>());
            pageModule.setPageIdJsonArray(null);
        }
        siteFolderPageModuleService.copyModule(moduleList, pageInfo.getId(), false, false);


    }

    /**
     * 生成页面的分享链接
     * @param pageId
     * @return
     */
    @Override
    public String preview(Long pageId) {

        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        if (tenantR.getIsSuccess()) {
            Tenant tenant = tenantR.getData();
            return ApplicationDomainUtil.cmsShowDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),"index/" + pageId);
        }
        return null;
    }

    /**
     * 保存页面配置到缓存
     * @param siteFolderPages
     */
    @Override
    public void savePageToRedis(List<SiteFolderPage> siteFolderPages) {

        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplates.boundHashOps("BUILD_FOLDER_PAGE");
        for (SiteFolderPage folderPage : siteFolderPages) {
            boundHashOps.delete(folderPage.getId().toString());
            boundHashOps.put(folderPage.getId().toString(), JSON.toJSONString(folderPage));
        }

    }


    /**
     * 检查模板的名称是否重复
     * @param folderName
     * @return
     */
    @Override
    public boolean nameCanUse(String folderName) {

        Integer count = baseMapper.selectCount(Wraps.<SiteFolderPage>lbQ().eq(SiteFolderPage::getPageName, folderName)
                .eq(SiteFolderPage::getTemplate, CommonStatus.YES));
        if (count != null && count > 0) {
            return false;
        }
        return true;
    }

    /**
     * c端获取页面的详细数据
     * @param pageId
     * @param domain
     * @return
     */
    @Override
    public SiteFolderPage getPage(Long pageId, String domain) {

        R<String> tenantR = tenantApi.getTenantCodeByDomain(domain);
        if (tenantR.getIsSuccess()) {
            String data = tenantR.getData();
            BaseContextHandler.setTenant(data);
        } else {
            throw new BizException("域名错误");
        }
        Object folderPage = redisTemplates.boundHashOps("BUILD_FOLDER_PAGE").get(pageId.toString());
        SiteFolderPage siteFolderPage;
        if (Objects.nonNull(folderPage)) {
            siteFolderPage = JSON.parseObject(folderPage.toString(), SiteFolderPage.class);
        } else {
            siteFolderPage = findPage(pageId);
        }
        if (Objects.nonNull(siteFolderPage)) {
            List<SiteFolderPageModule> moduleList = siteFolderPage.getModuleList();
            if (CollUtil.isNotEmpty(moduleList)) {
                // 提取其中的 文章组件。 视频组件。 多功能导航组件
                // 对文章板块 设置最新的文章数据。包括 文章的点击量，评论数，
                // 视频板块 设置最新的视频 点击量， 评论数量
                List<SiteModulePlate> siteModulePlates = new ArrayList<>();
                for (SiteFolderPageModule pageModule : moduleList) {
                    @Length(max = 200, message = "组件类型长度不能超过200") SitePageModuleType moduleType = pageModule.getModuleType();
                    if (SitePageModuleType.ARTICLE.equals(moduleType) || SitePageModuleType.MULTIPLE_VIDEOS.equals(moduleType)) {
                        List<SiteModulePlate> modulePlates = pageModule.getModulePlates();
                        if (CollUtil.isNotEmpty(modulePlates)) {
                            siteModulePlates.addAll(modulePlates);
                        }
                    } else if (SitePageModuleType.MULTIFUNCTIONAL_NAVIGATION.equals(moduleType)) {
                        List<SiteModuleLabel> labelList = pageModule.getModuleLabelList();
                        if (CollUtil.isNotEmpty(labelList)) {
                            for (SiteModuleLabel moduleLabel : labelList) {
                                List<SiteModulePlate> labelPlates = moduleLabel.getLabelPlates();
                                if (CollUtil.isNotEmpty(labelPlates)) {
                                    siteModulePlates.addAll(labelPlates);
                                }
                            }
                        }
                    }
                }
                siteModulePlateService.setPlatesAllInfo(siteModulePlates);
            }
        }
        return siteFolderPage;
    }



}
