package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cms.constant.SiteJumpTypeEnum;
import com.caring.sass.cms.constant.SitePageModuleType;
import com.caring.sass.cms.constant.SitePlateType;
import com.caring.sass.cms.dao.SiteFolderPageModuleMapper;
import com.caring.sass.cms.entity.*;
import com.caring.sass.cms.service.*;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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

public class SiteFolderPageModuleServiceImpl extends SuperServiceImpl<SiteFolderPageModuleMapper, SiteFolderPageModule> implements SiteFolderPageModuleService {

    @Autowired
    SiteModuleLabelService siteModuleLabelService;

    @Autowired
    SiteModulePlateService siteModulePlateService;

    @Autowired
    SiteModuleTitleStyleService siteModuleTitleStyleService;

    @Autowired
    SiteJumpInformationService siteJumpInformationService;

    @Autowired
    DatabaseProperties databaseProperties;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveOrUpdate(Long folderId, Long pageId, StringBuilder errorMessage, List<SiteFolderPageModule> moduleList) {
        if (CollUtil.isEmpty(moduleList)) {
            deletePageInfo(pageId);
            String key = String.format(SaasRedisBusinessKey.CMS_SITE_BUILD_PAGE_SAVE, folderId);
            try {
                redisTemplate.opsForValue().increment(key, 1);
            } catch (Exception e) {
                log.error("syncPageSave redis increment {} error", key);
                redisTemplate.delete(key);
            }
            return;
        }
        for (SiteFolderPageModule pageModule : moduleList) {
            // 根据前端提交的组件，标签，板块，跳转，样式设置。 确定需要从数据库中删除的 组件，标签，板块，跳转，样式设置
            if (pageModule == null) {
                continue;
            }
            String checkModule = checkModule(pageModule);
            errorMessage.append(checkModule);
        }
        String tenant = BaseContextHandler.getTenant();
        SaasGlobalThreadPool.execute(() -> syncPageSave(folderId, pageId, tenant, moduleList));
    }

    /**
     * 异步保存页面中的每个组件。
     * 组件更新完毕后。修改redis中的的数值
     * @param folderId
     * @param pageId
     * @param tenantCode
     * @param moduleList
     */
    public void syncPageSave(Long folderId, Long pageId, String tenantCode, List<SiteFolderPageModule> moduleList) {

        try {
            BaseContextHandler.setTenant(tenantCode);
            deletePageInfo(pageId);
            DatabaseProperties.Id id = databaseProperties.getId();
            Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

            for (SiteFolderPageModule pageModule : moduleList) {
                // 根据前端提交的组件，标签，板块，跳转，样式设置。 确定需要从数据库中删除的 组件，标签，板块，跳转，样式设置
                saveOrUpdate(folderId, pageId, pageModule, snowflake);
            }
        } catch (Exception e) {

        }
        String key = String.format(SaasRedisBusinessKey.CMS_SITE_BUILD_PAGE_SAVE, folderId);
        try {
            redisTemplate.opsForValue().increment(key, 1);
        } catch (Exception e) {
            log.error("syncPageSave redis increment {} error", key);
            redisTemplate.delete(key);
        }

    }



    /**
     * 检查一个组件是否可以分享。 并保持更新他的属性
     * @param folderId
     * @param pageId
     * @param entity
     * @return
     */
    public void saveOrUpdate(Long folderId, Long pageId, SiteFolderPageModule entity, Snowflake snowflake) {

        long nextId = snowflake.nextId();
        Long moduleId = nextId;
        entity.setFolderId(folderId);
        entity.setId(nextId);
        entity.setFolderPageId(pageId);
        baseMapper.insert(entity);

        // 处理组件的跳转
        SiteJumpInformation jumpInformation = entity.getJumpInformation();
        if (Objects.nonNull(jumpInformation)) {
            siteJumpInformationService.saveOrUpdate(folderId, pageId, moduleId, jumpInformation, snowflake);
        }

        // 处理组件的标签
        List<SiteModuleLabel> moduleLabelList = entity.getModuleLabelList();
        if (CollUtil.isNotEmpty(moduleLabelList)) {
            List<SiteModulePlate> labelPlates = new ArrayList<>(moduleLabelList.size() * 20);
            for (SiteModuleLabel moduleLabel : moduleLabelList) {
                long labelId = snowflake.nextId();
                moduleLabel.setId(labelId);
                moduleLabel.setFolderId(folderId);
                moduleLabel.setModuleId(moduleId);
                moduleLabel.setPageId(pageId);
                List<SiteModulePlate> tempLabelPlates = moduleLabel.getLabelPlates();
                if (CollUtil.isNotEmpty(tempLabelPlates)) {
                    tempLabelPlates.forEach(siteModulePlate -> siteModulePlate.setModuleLabelId(labelId));
                    labelPlates.addAll(tempLabelPlates);
                }
            }
            siteModuleLabelService.saveBatch(moduleLabelList);

            if (CollUtil.isNotEmpty(labelPlates)) {
                siteModulePlateService.saveList(folderId, pageId, moduleId, labelPlates, snowflake);
            }
        }

        // 处理组件的板块 板块有一对一的跳转
        List<SiteModulePlate> modulePlates = entity.getModulePlates();
        siteModulePlateService.saveList(folderId, pageId, moduleId, modulePlates, snowflake);
        // 处理组件的样式设置。
        List<SiteModuleTitleStyle> moduleTitleStyles = entity.getModuleTitleStyles();
        siteModuleTitleStyleService.saveOrUpdate(folderId, pageId, moduleId, moduleTitleStyles, snowflake);
    }


    /**
     * 查询一个文件夹下所有页面的信息。并封装
     * @param folderId
     * @param folderPages
     */
    @Override
    public void queryModule(Long folderId, List<SiteFolderPage> folderPages) {


        if (CollUtil.isEmpty(folderPages)) {
            return;
        }
        // 查询页面的所有的组件。 标签。 样式设置。 跳转信息。 板块
        List<SiteFolderPageModule> moduleList = baseMapper.selectList(Wraps.<SiteFolderPageModule>lbQ().eq(SiteFolderPageModule::getFolderId, folderId));
        if (CollUtil.isEmpty(moduleList)) {
            return;
        }
        Map<Long, List<SiteFolderPageModule>> modulePageMaps = null;
        if (CollUtil.isNotEmpty(moduleList)) {
            modulePageMaps = moduleList.stream().collect(Collectors.groupingBy(SiteFolderPageModule::getFolderPageId));
        }

        queryModuleAllInfo(folderId, null, modulePageMaps, folderPages);

    }

    /**
     * 查询页面下的组件信息
     * @param pageId
     * @param folderPages
     */
    @Override
    public void queryPageModule(Long pageId, SiteFolderPage folderPages) {

        // 查询页面的所有的组件。 标签。 样式设置。 跳转信息。 板块
        List<SiteFolderPageModule> moduleList = baseMapper.selectList(Wraps.<SiteFolderPageModule>lbQ().eq(SiteFolderPageModule::getFolderPageId, pageId));
        if (CollUtil.isEmpty(moduleList)) {
            return;
        }
        Map<Long, List<SiteFolderPageModule>> modulePageMaps = null;
        if (CollUtil.isNotEmpty(moduleList)) {
            modulePageMaps = moduleList.stream().collect(Collectors.groupingBy(SiteFolderPageModule::getFolderPageId));
        }
        List<SiteFolderPage> objects = new ArrayList<>();
        objects.add(folderPages);
        queryModuleAllInfo(null, pageId, modulePageMaps, objects);

    }
    /**
     * 给组件 设置 他的跳转，标签，板块。样式设置信息
     * @param modules
     * @param titleStyleMaps
     * @param plateMaps
     * @param labelsMaps
     * @param jumpsMaps
     */
    private void buildPageInfo(List<SiteFolderPageModule> modules, Map<Long, List<SiteModuleTitleStyle>> titleStyleMaps,
                               Map<Long, List<SiteModulePlate>> plateMaps,
                               Map<Long, List<SiteModuleLabel>> labelsMaps,
                               Map<Long, List<SiteJumpInformation>> jumpsMaps) {
        if (Objects.isNull(modules)) {
            return;
        }
        for (SiteFolderPageModule module : modules) {
            Long moduleId = module.getId();
            SitePageModuleType moduleType = module.getModuleType();
            List<SiteModuleTitleStyle> titleStyles = titleStyleMaps.get(moduleId);
            module.setModuleTitleStyles(titleStyles);

            List<SiteModulePlate> plates = plateMaps.get(moduleId);
            List<SiteJumpInformation> jumpInformations = jumpsMaps.get(moduleId);
            // 如果组件是 文本， 图片， 按钮
            if (SitePageModuleType.SITE_TEXT.equals(moduleType)
                    || SitePageModuleType.PICTURE.equals(moduleType)
                    || SitePageModuleType.BUTTON.equals(moduleType)
                    || SitePageModuleType.GRAPHICAL.equals(moduleType)) {
                if (CollUtil.isNotEmpty(jumpInformations)) {
                    module.setJumpInformation(jumpInformations.get(0));
                }
                continue;
            }
            // 非 文本 图片 按钮 组件，跳转事件绑定在板块上。
            if (CollUtil.isNotEmpty(jumpInformations) && CollUtil.isNotEmpty(plates)) {
                Map<Long, SiteJumpInformation> jumpInformationMap =  jumpInformations.stream()
                        .filter(item -> Objects.nonNull(item.getModulePlateId()))
                        .collect(Collectors.toMap(SiteJumpInformation::getModulePlateId, item -> item, (o1, o2) -> o2));
                plates.forEach(item -> {
                    SiteJumpInformation information = jumpInformationMap.get(item.getId());
                    if (Objects.nonNull(information)) {
                        item.setJumpInformation(information);
                    }
                });
            }

            // 多功能导航
            if (SitePageModuleType.MULTIFUNCTIONAL_NAVIGATION.equals(moduleType)) {
                List<SiteModuleLabel> moduleLabelList = labelsMaps.get(moduleId);
                // 如果组件是多功能导航。 那么有标签，标签下有板块，跳转。
                if (CollUtil.isEmpty(moduleLabelList)) {
                    continue;
                }
                module.setModuleLabelList(moduleLabelList);
                if (CollUtil.isEmpty(plates)) {
                    continue;
                }
                Map<Long, List<SiteModulePlate>> labelPlateMap = plates.stream()
                        .filter(siteModulePlate -> Objects.nonNull(siteModulePlate.getModuleLabelId()))
                        .collect(Collectors.groupingBy(SiteModulePlate::getModuleLabelId));
                moduleLabelList.forEach(siteModuleLabel -> {
                    List<SiteModulePlate> plateList = labelPlateMap.get(siteModuleLabel.getId());
                    siteModuleLabel.setLabelPlates(plateList);
                });

            } else
            // 文章组件。 多功能导航组件。 轮播图组件
            if (SitePageModuleType.ARTICLE.equals(moduleType)
                    || SitePageModuleType.MULTIPLE_VIDEOS.equals(moduleType)
                    || SitePageModuleType.SWIPER.equals(moduleType)
                    || SitePageModuleType.MULTI_GRAPH.equals(moduleType)
                    || SitePageModuleType.NAVIGATION.equals(moduleType)) {
                if (CollUtil.isEmpty(plates)) {
                    continue;
                }
                module.setModulePlates(plates);

            }

        }

    }


    /**
     * 查询 文件夹所有页面 或单个页面的 标题样式，版本，跳转， 标签信息，并使用 组件ID分组添加到map中
     * @param folderId
     * @param pageId
     */
    public void queryModuleAllInfo(Long folderId, Long pageId,
                                   Map<Long, List<SiteFolderPageModule>> modulePageMaps,
                                   List<SiteFolderPage> folderPages) {

        Map<Long, List<SiteModuleTitleStyle>> titleStyleMaps = new HashMap<>();

        Map<Long, List<SiteModulePlate>> plateMaps = new HashMap<>();

        Map<Long, List<SiteModuleLabel>> labelsMaps = new HashMap<>();

        Map<Long, List<SiteJumpInformation>> jumpsMaps = new HashMap<>();
        LbqWrapper<SiteModuleTitleStyle> styleLbqWrapper = Wraps.<SiteModuleTitleStyle>lbQ();
        LbqWrapper<SiteModulePlate> plateLbqWrapper = Wraps.<SiteModulePlate>lbQ();
        LbqWrapper<SiteModuleLabel> labelLbqWrapper = Wraps.<SiteModuleLabel>lbQ();
        LbqWrapper<SiteJumpInformation> jumpInformationLbqWrapper = Wraps.<SiteJumpInformation>lbQ();
        if (folderId == null && pageId == null) {
            return;
        }
        if (Objects.nonNull(folderId)) {
            styleLbqWrapper.eq(SiteModuleTitleStyle::getFolderId, folderId);
            plateLbqWrapper.eq(SiteModulePlate::getFolderId, folderId);
            labelLbqWrapper.eq(SiteModuleLabel::getFolderId, folderId);
            jumpInformationLbqWrapper.eq(SiteJumpInformation::getFolderId, folderId);
        }
        if (Objects.nonNull(pageId)) {
            styleLbqWrapper.eq(SiteModuleTitleStyle::getPageId, pageId);
            plateLbqWrapper.eq(SiteModulePlate::getPageId, pageId);
            labelLbqWrapper.eq(SiteModuleLabel::getPageId, pageId);
            jumpInformationLbqWrapper.eq(SiteJumpInformation::getPageId, pageId);
        }

        List<SiteModuleTitleStyle> titleStyles = siteModuleTitleStyleService.list(styleLbqWrapper);
        if (CollUtil.isNotEmpty(titleStyles)) {
            titleStyleMaps = titleStyles.stream().collect(Collectors.groupingBy(SiteModuleTitleStyle::getModuleId));
        }
        List<SiteModulePlate> plates = siteModulePlateService.list(plateLbqWrapper);
        if (CollUtil.isNotEmpty(plates)) {
            plateMaps = plates.stream().collect(Collectors.groupingBy(SiteModulePlate::getModuleId));
        }

        List<SiteModuleLabel> labels = siteModuleLabelService.list(labelLbqWrapper);
        if (CollUtil.isNotEmpty(labels)) {
            labelsMaps = labels.stream().collect(Collectors.groupingBy(SiteModuleLabel::getModuleId));
        }

        List<SiteJumpInformation> jumpInformations = siteJumpInformationService.list(jumpInformationLbqWrapper);
        if (CollUtil.isNotEmpty(jumpInformations)) {
            jumpsMaps = jumpInformations.stream().collect(Collectors.groupingBy(SiteJumpInformation::getModuleId));
        }

        // 将上面这些页面的组件， 标签， 板块， 样式， 跳转。 分类设置到对应的页面中。

        // 第一步。 先将 标签， 板块， 样式， 跳转 设置到对应的组件中。
        for (SiteFolderPage folderPage : folderPages) {
            Long folderPageId = folderPage.getId();
            List<SiteFolderPageModule> pageModules = modulePageMaps.get(folderPageId);
            buildPageInfo(pageModules, titleStyleMaps, plateMaps, labelsMaps, jumpsMaps);
            folderPage.setModuleList(pageModules);
        }
    }

    /**
     * 检查跳转信息是否符合分享要求
     * @param information
     * @return
     */
    private String checkJump(SiteJumpInformation information) {
        if (Objects.isNull(information)) {
            return "未设置跳转。";
        }
        SiteJumpTypeEnum jumpType = information.getJumpType();
        if (Objects.isNull(jumpType)) {
            return "未设置跳转。";
        }
        switch (jumpType) {
            case ARTICLE:
                if (Objects.isNull(information.getJumpCmsId())) {
                    return "未设置跳转文章。";
                }
                break;
            case PAGE:
                if (Objects.isNull(information.getJumpPageId())) {
                    return "未设置跳转页面。";
                }
                break;
            case APPLET:
                if (StrUtil.isEmpty(information.getJumpAppId()) || StrUtil.isEmpty(information.getJumpAppPageUrl())) {
                    return "未设置跳转小程序信息。";
                }
                break;
            case WEBSITE:
                if (StrUtil.isEmpty(information.getJumpWebsite())) {
                    return "未设置跳转的网址。";
                }
                break;
            case VIDEO:
                if (StrUtil.isEmpty(information.getJumpVideoUrl())) {
                    return "未设置跳转的视频地址。";
                }
                break;
        }
        return null;

    }

    /**
     * 检查组件是否符合 分享的标准
     * @param module
     * @return
     */
    private String checkModule(SiteFolderPageModule module) {

        StringBuilder stringBuilder = new StringBuilder();
        if (module.getModuleType() == null) {
            return stringBuilder.toString();
        }
        switch (module.getModuleType()) {
            case ARTICLE: {
                List<SiteModulePlate> modulePlates = module.getModulePlates();
                if (CollUtil.isEmpty(modulePlates)) {
                    stringBuilder.append("文章组件未设置关联的文章。");
                    break;
                }
                for (SiteModulePlate modulePlate : modulePlates) {
                    SiteJumpInformation information = modulePlate.getJumpInformation();
                    String jump = checkJump(information);
                    if (StrUtil.isNotEmpty(jump)) {
                        stringBuilder.append("文章组件").append(jump);
                        break;
                    }
                }
                break;
            }
            case SEARCH_FOR: {
                String pageIdJsonArray = module.getPageIdJsonArray();
                if (StrUtil.isEmpty(pageIdJsonArray)) {
                    stringBuilder.append("检索组件未关联页面。");
                } else {
                    JSONArray jsonArray = JSON.parseArray(pageIdJsonArray);
                    if (CollUtil.isEmpty(jsonArray)) {
                        stringBuilder.append("检索组件未关联页面。");
                        break;
                    }
                }
                break;
            }
            case MULTIPLE_VIDEOS: {
                List<SiteModulePlate> modulePlates = module.getModulePlates();
                if (CollUtil.isEmpty(modulePlates)) {
                    stringBuilder.append("多视频组件未设置关联的视频。");
                    break;
                }
                for (SiteModulePlate modulePlate : modulePlates) {
                    SiteJumpInformation information = modulePlate.getJumpInformation();
                    String jump = checkJump(information);
                    if (StrUtil.isNotEmpty(jump)) {
                        stringBuilder.append("多视频组件").append(jump);
                        break;
                    }
                }
                break;
            }
            case MULTIFUNCTIONAL_NAVIGATION: {
                // 要求组件内的 板块设置有跳转
                List<SiteModuleLabel> moduleLabelList = module.getModuleLabelList();
                if (CollUtil.isEmpty(moduleLabelList)) {
                    stringBuilder.append("多功能导航组件没有设置标签。");
                    break;
                }
                // 纯标签导航不需要设置板块
                String className = module.getClassName();
                if ("PURE_LABEL_BAR".equals(className)) {
                    break;
                }
                for (SiteModuleLabel moduleLabel : moduleLabelList) {
                    List<SiteModulePlate> labelPlates = moduleLabel.getLabelPlates();
                    if (CollUtil.isEmpty(labelPlates)) {
                        stringBuilder.append("多功能导航组件标签:").append(moduleLabel.getLabelName()).append("没有设置板块。");
                        break;
                    }
                    for (SiteModulePlate labelPlate : labelPlates) {
                        SitePlateType plateType = labelPlate.getPlateType();
                        if (SitePlateType.IMAGE.equals(plateType)) {
                            continue;
                        }
                        SiteJumpInformation information = labelPlate.getJumpInformation();
                        String jump = checkJump(information);
                        if (StrUtil.isNotEmpty(jump)) {
                            stringBuilder.append("多功能导航组件标签").append(moduleLabel.getLabelName()).append("板块").append(jump);
                            break;
                        }

                    }
                }
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 将还需要保留的 板块 和板块的跳转 清除掉
     * @param labelPlates
     * @param oldPlateMap
     * @param oldJumpMap
     */
    private void removeExistPlate(List<SiteModulePlate> labelPlates, Map<Long, SiteModulePlate> oldPlateMap, Map<Long, SiteJumpInformation> oldJumpMap) {
        if (CollUtil.isEmpty(labelPlates)) {
            return;
        }
        for (SiteModulePlate labelPlate : labelPlates) {
            if (Objects.nonNull(labelPlate.getId())) {
                oldPlateMap.remove(labelPlate.getId());
                SiteJumpInformation jumpInformation = labelPlate.getJumpInformation();
                if (Objects.nonNull(jumpInformation) && Objects.nonNull(jumpInformation.getId())) {
                    oldJumpMap.remove(jumpInformation.getId());
                }
            }
        }
    }

    /**
     * 将前端提交过来 还需要保留的样式设置移除
     * @param titleStyles
     * @param oldTitleMap
     */
    private void removeExistStyle(List<SiteModuleTitleStyle> titleStyles, Map<Long, SiteModuleTitleStyle> oldTitleMap) {

        if (CollUtil.isEmpty(titleStyles)) {
            return;
        }
        for (SiteModuleTitleStyle titleStyle : titleStyles) {
            if (Objects.nonNull(titleStyle.getId())) {
                oldTitleMap.remove(titleStyle.getId());
            }
        }
    }


    /**
     * 页面没有组件时，清除页面内的信息
     * @param pageId
     */
    @Override
    public void deletePageInfo(Long pageId) {

        // 清除页面内 组件的跳转
        siteJumpInformationService.remove(Wraps.<SiteJumpInformation>lbQ().eq(SiteJumpInformation::getPageId, pageId));

        // 清除页面内 组件的 标签
        siteModuleLabelService.remove(Wraps.<SiteModuleLabel>lbQ().eq(SiteModuleLabel::getPageId, pageId));

        // 清除页面内 组件的 板块
        siteModulePlateService.remove(Wraps.<SiteModulePlate>lbQ().eq(SiteModulePlate::getPageId, pageId));

        // 清除页面内 组件的 样式设置
        siteModuleTitleStyleService.remove(Wraps.<SiteModuleTitleStyle>lbQ().eq(SiteModuleTitleStyle::getPageId, pageId));

        baseMapper.delete(Wraps.<SiteFolderPageModule>lbQ().eq(SiteFolderPageModule::getFolderPageId, pageId));
    }


    /**
     * 批量将组件的宽度 从350 替换到400
     */
    @Override
    public void batchBuildModuleStyle() {

        List<SiteFolderPageModule> moduleList = baseMapper.selectAllNoTenantCode();
        if (CollUtil.isEmpty(moduleList)) {
            return;
        }
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps("BUILD_FOLDER_PAGE");
        Map<String, List<SiteFolderPageModule>> stringListMap = moduleList.stream().collect(Collectors.groupingBy(SiteFolderPageModule::getTenantCode));
        Set<String> keySet = stringListMap.keySet();
        for (String s : keySet) {
            BaseContextHandler.setTenant(s);
            List<SiteFolderPageModule> pageModuleList = stringListMap.get(s);
            Long pageId = null;
            for (SiteFolderPageModule module : pageModuleList) {
                pageId = module.getFolderPageId();
                String moduleStyle = module.getModuleStyle();
                if (StrUtil.isEmpty(moduleStyle)) {
                    continue;
                }
                JSONObject object = JSON.parseObject(moduleStyle);
                Object width = object.get("width");
                if (Objects.nonNull(width)) {
                    String string = width.toString();
                    String widthNumber = string.substring(0, string.indexOf("px"));
                    if (StrUtil.isNotEmpty(widthNumber)) {
                        double parseDouble = Double.parseDouble(widthNumber);
                        if (parseDouble >= 349) {
                            object.put("width", "400px");
                        }
                    }
                }
                Object left = object.get("left");
                if (Objects.nonNull(left)) {
                    String string = left.toString();
                    String widthNumber = string.substring(0, string.indexOf("px"));
                    if (StrUtil.isNotEmpty(widthNumber)) {
                        double parseDouble = Double.parseDouble(widthNumber);
                        if (parseDouble < 0) {
                            object.put("left", "0px");
                        }
                    }
                }
                module.setModuleStyle(object.toJSONString());
                baseMapper.updateById(module);
            }
            if (pageId != null) {
                boundHashOps.delete(pageId.toString());
            }
        }

    }

    /**
     * 复制页面。保存页面为模板。保存系统模板时使用
     * @param moduleList
     * @param pageId
     * @param saveJumpPage
     */
    @Override
    public void copyModule(List<SiteFolderPageModule> moduleList, Long pageId, Boolean saveJumpPage, Boolean saveJump) {
        for (SiteFolderPageModule module : moduleList) {
            module.setId(null);
            module.setFolderPageId(pageId);
            if (!saveJumpPage) {
                module.setPageIdJsonArray(null);
                // 图形组件去掉设置的图片
                if (module.getModuleType() != null) {
                    if (SitePageModuleType.GRAPHICAL.equals(module.getModuleType())) {
                        module.setModuleShowContent(null);
                    }
                    if (SitePageModuleType.VIDEO.equals(module.getModuleType())) {
                        module.setVideoCover(null);
                        module.setVideoUrl(null);
                        module.setWareHouseVideoId(null);
                    }
                    if (SitePageModuleType.PICTURE.equals(module.getModuleType())) {
                        module.setModuleImageUrl(null);
                    }
                }
            }
            baseMapper.insert(module);
            Long moduleId = module.getId();
            List<SiteModulePlate> modulePlates = module.getModulePlates();
            List<SiteModuleLabel> moduleLabelList = module.getModuleLabelList();
            List<SiteModuleTitleStyle> titleStyles = module.getModuleTitleStyles();
            SiteJumpInformation jumpInformation = module.getJumpInformation();
            if (Objects.nonNull(jumpInformation) && saveJump) {
                jumpInformation.setId(null);
                jumpInformation.setPageId(pageId);
                jumpInformation.setModuleId(moduleId);
                if (!saveJumpPage) {
                    jumpInformation.setJumpPageId(null);
                }
                siteJumpInformationService.save(jumpInformation);
            }

            if (CollUtil.isNotEmpty(titleStyles)) {
                for (SiteModuleTitleStyle titleStyle : titleStyles) {
                    titleStyle.setId(null);
                    titleStyle.setModuleId(moduleId);
                    titleStyle.setPageId(pageId);
                }
                siteModuleTitleStyleService.saveBatch(titleStyles);
            }

            if (CollUtil.isNotEmpty(modulePlates)) {
                for (SiteModulePlate plate : modulePlates) {
                    plate.setId(null);
                    plate.setModuleId(moduleId);
                    plate.setPageId(pageId);
                    SiteJumpInformation information = plate.getJumpInformation();
                    siteModulePlateService.save(plate);
                    if (Objects.nonNull(information) && saveJump) {
                        information.setModuleId(moduleId);
                        information.setPageId(pageId);
                        information.setModulePlateId(plate.getId());
                        if (!saveJumpPage) {
                            information.setJumpPageId(null);
                        }
                        information.setId(null);
                        siteJumpInformationService.save(information);
                    }
                }
            }
            if (CollUtil.isNotEmpty(moduleLabelList)) {
                for (SiteModuleLabel label : moduleLabelList) {
                    List<SiteModulePlate> labelPlates = label.getLabelPlates();
                    label.setId(null);
                    label.setPageId(pageId);
                    label.setModuleId(moduleId);
                    siteModuleLabelService.save(label);
                    if (CollUtil.isNotEmpty(labelPlates)) {
                        for (SiteModulePlate plate : labelPlates) {
                            plate.setModuleLabelId(label.getId());
                            SiteJumpInformation information = plate.getJumpInformation();
                            plate.setPageId(pageId);
                            plate.setModuleId(moduleId);
                            plate.setId(null);
                            siteModulePlateService.save(plate);
                            if (Objects.nonNull(information) && saveJump) {
                                information.setId(null);
                                information.setPageId(pageId);
                                information.setModuleId(moduleId);
                                if (!saveJumpPage) {
                                    information.setJumpPageId(null);
                                }
                                information.setModulePlateId(plate.getId());
                                siteJumpInformationService.save(information);
                            }
                        }
                    }
                }
            }
        }


    }

    /**
     * 复制页面下的组件。样式，标签，板块，跳转
     */
    @Override
    public void copyModule(Long oldPageId, Long newPageId, Long newFolderId, Map<Long, Long> pageIdMap) {

        List<SiteFolderPageModule> moduleList = baseMapper.selectList(Wraps.<SiteFolderPageModule>lbQ().eq(SiteFolderPageModule::getFolderPageId, oldPageId));
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        Map<Long, Long> oldModuleNewModuleMap = new HashMap<>();
        if (CollUtil.isNotEmpty(moduleList)) {
            for (SiteFolderPageModule pageModule : moduleList) {
                long nextId = snowflake.nextId();

                oldModuleNewModuleMap.put(pageModule.getId(), nextId);
                pageModule.setId(nextId);
                pageModule.setFolderPageId(newPageId);
                pageModule.setFolderId(newFolderId);
                pageModule.setCreateTime(LocalDateTime.now());
            }
            baseMapper.insertBatchSomeColumn(moduleList);
        }

        Map<Long, Long> oldLabelNewLabelMap = new HashMap<>();
        List<SiteModuleLabel> labelList = siteModuleLabelService.list(Wraps.<SiteModuleLabel>lbQ().eq(SiteModuleLabel::getPageId, oldPageId));
        if (CollUtil.isNotEmpty(labelList)) {
            for (SiteModuleLabel label : labelList) {
                long nextId = snowflake.nextId();

                oldLabelNewLabelMap.put(label.getId(), nextId);
                label.setId(nextId);
                label.setFolderId(newFolderId);
                label.setPageId(newPageId);
                label.setModuleId(oldModuleNewModuleMap.get(label.getModuleId()));
                label.setCreateTime(LocalDateTime.now());
            }
            siteModuleLabelService.saveBatch(labelList);
        }

        Map<Long, Long> oldPlateNewPlateMap = new HashMap<>();
        List<SiteModulePlate> modulePlates = siteModulePlateService.list(Wraps.<SiteModulePlate>lbQ().eq(SiteModulePlate::getPageId, oldPageId));
        if (CollUtil.isNotEmpty(modulePlates)) {
            for (SiteModulePlate modulePlate : modulePlates) {
                long nextId = snowflake.nextId();

                oldPlateNewPlateMap.put(modulePlate.getId(), nextId);
                modulePlate.setId(nextId);
                modulePlate.setFolderId(newFolderId);
                modulePlate.setPageId(newPageId);
                if (Objects.nonNull(modulePlate.getModuleId())) {
                    modulePlate.setModuleId(oldModuleNewModuleMap.get(modulePlate.getModuleId()));
                }
                if (Objects.nonNull(modulePlate.getModuleLabelId())) {
                    modulePlate.setModuleLabelId(oldLabelNewLabelMap.get(modulePlate.getModuleLabelId()));
                }
                modulePlate.setCreateTime(LocalDateTime.now());
            }
            siteModulePlateService.saveBatch(modulePlates);
        }


        List<SiteJumpInformation> jumpInformations = siteJumpInformationService.list(Wraps.<SiteJumpInformation>lbQ().eq(SiteJumpInformation::getPageId, oldPageId));

        if (CollUtil.isNotEmpty(jumpInformations)) {
            for (SiteJumpInformation information : jumpInformations) {
                information.setId(null);
                information.setFolderId(newFolderId);
                information.setPageId(newPageId);
                if (Objects.nonNull(information.getModuleId())) {
                    information.setModuleId(oldModuleNewModuleMap.get(information.getModuleId()));
                }
                if (Objects.nonNull(information.getModulePlateId())) {
                    information.setModulePlateId(oldPlateNewPlateMap.get(information.getModulePlateId()));
                }
                if (SiteJumpTypeEnum.PAGE.equals(information.getJumpType())) {
                    if (Objects.nonNull(information.getJumpPageId())) {
                        information.setJumpPageId(pageIdMap.get(information.getJumpPageId()));
                    }
                }
                information.setCreateTime(LocalDateTime.now());
            }
            siteJumpInformationService.saveBatch(jumpInformations);
        }

        List<SiteModuleTitleStyle> titleStyles = siteModuleTitleStyleService.list(Wraps.<SiteModuleTitleStyle>lbQ().eq(SiteModuleTitleStyle::getPageId, oldPageId));
        if (CollUtil.isNotEmpty(titleStyles)) {
            for (SiteModuleTitleStyle titleStyle : titleStyles) {
                titleStyle.setFolderId(newFolderId);
                titleStyle.setPageId(newPageId);
                titleStyle.setModuleId(oldModuleNewModuleMap.get(titleStyle.getModuleId()));
                titleStyle.setId(null);
                titleStyle.setCreateTime(LocalDateTime.now());
            }
            siteModuleTitleStyleService.saveBatch(titleStyles);
        }


    }
}
