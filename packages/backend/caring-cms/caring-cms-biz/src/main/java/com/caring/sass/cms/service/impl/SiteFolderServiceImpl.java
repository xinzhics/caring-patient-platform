package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.SiteFolderMapper;
import com.caring.sass.cms.dao.SiteFolderPageMapper;
import com.caring.sass.cms.entity.SiteFolder;
import com.caring.sass.cms.entity.SiteFolderPage;
import com.caring.sass.cms.service.SiteFolderPageService;
import com.caring.sass.cms.service.SiteFolderService;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.ChineseToEnglishUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.api.PlanApi;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.wx.WeiXinApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 建站文件夹表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Service

public class SiteFolderServiceImpl extends SuperServiceImpl<SiteFolderMapper, SiteFolder> implements SiteFolderService {

    @Autowired
    SiteFolderPageMapper siteFolderPageMapper;

    @Autowired
    SiteFolderPageService siteFolderPageService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    H5RouterApi h5RouterApi;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    PlanApi planApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean removeById(Serializable id) {

        List<SiteFolderPage> folderPageList = siteFolderPageService.list(Wraps.<SiteFolderPage>lbQ().eq(SiteFolderPage::getFolderId, id));
        if (CollUtil.isNotEmpty(folderPageList)) {
            List<Long> pageIds = folderPageList.stream().map(SuperEntity::getId).collect(Collectors.toList());
            siteFolderPageService.removeByIds(pageIds);
        }
        baseMapper.deleteById(id);

        return false;
    }

    /**
     * 将文件夹复制一个存为模板。并将文件夹内的内容复制到模板文件夹下。
     * @param id
     * @param folderName
     */
    @Override
    @Transactional
    public void saveForTemplateFolder(Long id, String folderName) {
        SiteFolder siteFolder = baseMapper.selectById(id);
        if (nameCanUse(null, folderName, CommonStatus.YES)) {

            siteFolder.setId(null);
            siteFolder.setFolderName(folderName);
            String letter = ChineseToEnglishUtil.getPinYinFirstLetter(folderName);
            siteFolder.setNameFirstLetter(letter);
            siteFolder.setCreateTime(LocalDateTime.now());
            siteFolder.setReplica(CommonStatus.NO);
            siteFolder.setDeleteStatus(CommonStatus.NO);
            siteFolder.setCopyNumber(0);
            siteFolder.setTemplate(CommonStatus.YES);
            baseMapper.insert(siteFolder);

            siteFolderPageService.copyPageByFolder(id, siteFolder.getId());
        } else {
            throw new BizException("名称重复，请重新输入");
        }

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

        return siteFolderPageService.saveTemplateToFolder(templateFolderId, folderId, homeId, replaceHome);
    }

    /**
     * 复制文件夹
     * @param id
     * @return
     */
    @Transactional
    @Override
    public SiteFolder copyFolder(Long id) {

        SiteFolder siteFolder = baseMapper.selectById(id);
        siteFolder.setCopyNumber(siteFolder.getCopyNumber() + 1);

        Integer replica = siteFolder.getReplica();
        String siteFolderName = "";
        @Length(max = 50, message = "文件夹名称长度不能超过50") String folderName = siteFolder.getFolderName();
        if (StrUtil.isBlank(folderName)) {
            folderName = SiteFolder.DEFAULT_FOLDER_NAME;
        }
        if (CommonStatus.YES.equals(replica)) {
            siteFolderName = folderName + "(副件" + siteFolder.getCopyNumber() + ")";
        } else {
            siteFolderName = folderName + "(复制" + siteFolder.getCopyNumber() + ")";

        }
        baseMapper.updateById(siteFolder);

        siteFolder.setId(null);
        siteFolder.setCreateTime(LocalDateTime.now());
        siteFolder.setReplica(CommonStatus.YES);
        siteFolder.setDeleteStatus(CommonStatus.NO);
        siteFolder.setCopyNumber(0);
        siteFolder.setTemplate(CommonStatus.NO);
        siteFolder.setFolderName(siteFolderName);
        String letter = ChineseToEnglishUtil.getPinYinFirstLetter(siteFolderName);
        siteFolder.setNameFirstLetter(letter);
        baseMapper.insert(siteFolder);

        siteFolderPageService.copyPageByFolder(id, siteFolder.getId());
        return siteFolder;

    }

    /**
     * 更新文件夹和页面
     * @param siteFolder
     */
    @Override
    public void updateFolderAndPage(SiteFolder siteFolder) {
        Long folderId = siteFolder.getId();
        String key = String.format(SaasRedisBusinessKey.CMS_SITE_BUILD_PAGE_SAVE, folderId);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey != null && hasKey) {
            throw new BizException("文件夹内容未保存完毕，请稍后提交");
        }

        List<SiteFolderPage> siteFolderPages = siteFolder.getSiteFolderPageList();
        String errorMessage = siteFolderPageService.saveOrUpdateSiteFolder(folderId, siteFolderPages);

        if (StrUtil.isEmpty(errorMessage)) {
            siteFolder.setCanShare(CommonStatus.YES);
            siteFolder.setShareErrorMessage("");
            // 使用异步方法。将上述的信息 按页面 缓存到 redis

            siteFolderPageService.savePageToRedis(siteFolderPages);
        } else {
            siteFolder.setCanShare(CommonStatus.NO);
            siteFolder.setShareErrorMessage(errorMessage);
        }
        baseMapper.updateById(siteFolder);

    }



    /**
     * 查询文件夹下所有的页面
     * @param folderId
     * @return
     */
    @Override
    public SiteFolder querySiteFolderAllPage(Long folderId) {
        String key = String.format(SaasRedisBusinessKey.CMS_SITE_BUILD_PAGE_SAVE, folderId);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey != null && hasKey) {
            throw new BizException("文件夹内容未保存完毕，请稍候刷新");
        }
        SiteFolder siteFolder = baseMapper.selectById(folderId);
        if (Objects.isNull(siteFolder)) {
            return null;
        }
        List<SiteFolderPage> siteFolderPages = siteFolderPageService.queryAllPageByFolderId(folderId);
        siteFolder.setSiteFolderPageList(siteFolderPages);
        return siteFolder;
    }


    /**
     * 分享文件夹的第一个页面
     * @param folderId
     * @return
     */
    @Override
    public String shareFolderFirstPage(Long folderId) {
        SiteFolderPage folderPage = siteFolderPageService.getOne(Wraps.<SiteFolderPage>lbQ()
                .eq(SiteFolderPage::getFolderId, folderId)
                .orderByAsc(SuperEntity::getCreateTime).last(" limit 0,1 "));
        if (Objects.isNull(folderPage)) {
            throw new BizException("首页不存在");
        }
        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        if (tenantR.getIsSuccess()) {
            Tenant tenant = tenantR.getData();
            return ApplicationDomainUtil.cmsShowDomain(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),"index/" + folderPage.getId());
        }
        throw new BizException("查询租户失败");
    }

    /**
     * 只更新文件夹的名称
     * @param siteFolder
     */
    @Override
    public void updateFolderName(SiteFolder siteFolder) {
        if (StrUtil.isEmpty(siteFolder.getFolderName())) {
            UpdateWrapper<SiteFolder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", siteFolder.getId());
            updateWrapper.set("folder_name", null);
            updateWrapper.set("name_first_letter", null);
            updateWrapper.set("folder_name_pinyin", null);
            baseMapper.update(new SiteFolder(), updateWrapper);
            return;
        }
        if (nameCanUse(siteFolder.getId(), siteFolder.getFolderName(), CommonStatus.NO)) {
            String letter = ChineseToEnglishUtil.getPinYinFirstLetter(siteFolder.getFolderName());
            siteFolder.setNameFirstLetter(letter);
            baseMapper.updateById(siteFolder);
        } else {
            throw new BizException("文件名重复，请重新输入！");
        }
    }

    /**
     * 新建一个文件夹，并初始化一个首页
     * @param model
     * @return
     */
    @Override
    public boolean save(SiteFolder model) {


        model.setTemplate(CommonStatus.NO);
        model.setCopyNumber(0);
        model.setReplica(CommonStatus.NO);
        model.setCanShare(CommonStatus.NO);
        model.setShareErrorMessage("页面配置未完成");
        super.save(model);

        SiteFolderPage folderPage = new SiteFolderPage();
        folderPage.setPageName(SiteFolderPage.DEFAULT_PAGE_NAME);
        folderPage.setHomeStatus(CommonStatus.YES);
        folderPage.setFolderId(model.getId());
        folderPage.setCopyNumber(0);
        folderPage.setTemplate(CommonStatus.NO);
        siteFolderPageMapper.insert(folderPage);

        List<SiteFolderPage> objects = new ArrayList<>();
        objects.add(folderPage);
        model.setSiteFolderPageList(objects);
        return true;
    }


    /**
     * 检查文件夹是否可以删除
     * @param folderId
     */
    @Override
    public String checkFolderCanDelete(Long folderId) {

        String firstPage = shareFolderFirstPage(folderId);
        String redirectUri;
        try {
            redirectUri = URLEncoder.encode(firstPage, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        // 访问租户服务，检查患者菜单。医生菜单。医助菜单中是否有此链接。并返回记录
        R<List<String>> h5CheckUrlExistR = h5RouterApi.checkFolderShareUrlExist(redirectUri);

        Set<String> tenantSets = new HashSet<>();
        if (h5CheckUrlExistR.getIsSuccess()) {
            List<String> h5RouterTenantCode = h5CheckUrlExistR.getData();
            if (CollUtil.isNotEmpty(h5RouterTenantCode)) {
                tenantSets.addAll(h5RouterTenantCode);
            }
        }

        // 访问微信服务，检查公众号菜单 是否此链接。
        R<List<String>> configMenuUrlExistR = weiXinApi.checkFolderShareUrlExist(redirectUri);
        if (configMenuUrlExistR.getIsSuccess()) {
            List<String> configMenuTenantCode = configMenuUrlExistR.getData();
            if (CollUtil.isNotEmpty(configMenuTenantCode)) {
                tenantSets.addAll(configMenuTenantCode);
            }
        }

        // 访问随访服务。检查随访配置中是否有此链接。
        R<List<String>> planCheckUrlExistR = planApi.checkFolderShareUrlExist(redirectUri);
        if (planCheckUrlExistR.getIsSuccess()) {
            List<String> planTenantCode = planCheckUrlExistR.getData();
            if (CollUtil.isNotEmpty(planTenantCode)) {
                tenantSets.addAll(planTenantCode);
            }
        }
        if (CollUtil.isNotEmpty(tenantSets)) {
            R<List<Tenant>> tenantNameByCodes = tenantApi.queryTenantNameByCodes(tenantSets);
            if (tenantNameByCodes.getIsSuccess()) {
                List<Tenant> tenantList = tenantNameByCodes.getData();
                if (CollUtil.isNotEmpty(tenantList)) {
                    List<String> tenantName = tenantList.stream().map(Tenant::getName).collect(Collectors.toList());
                    return StrUtil.join(",", tenantName);
                }
            }
        }
        return null;
    }

    /**
     * 检查名称是否重复
     * @param id
     * @param name
     * @return
     */
    @Override
    public boolean nameCanUse(Long id, String name, Integer template) {
        LbqWrapper<SiteFolder> wrapper = Wraps.<SiteFolder>lbQ()
                .eq(SiteFolder::getTemplate, template)
                .eq(SiteFolder::getDeleteStatus, CommonStatus.NO)
                .eq(SiteFolder::getFolderName, name);
        if (Objects.nonNull(id)) {
            wrapper.ne(SuperEntity::getId, id);
        }
        Integer integer = baseMapper.selectCount(wrapper);
        if (integer != null && integer > 0) {
            return false;
        }
        return true;
    }


    /**
     * 更新文件夹的删除天数。
     * 天数小于等于0的。直接删除。
     */
    @Override
    public void siteFolderUpdateDeleteDay() {

        List<SiteFolder> siteFolders = baseMapper.queryDeleteFolder();
        if (CollUtil.isNotEmpty(siteFolders)) {
            for (SiteFolder folder : siteFolders) {
                String tenantCode = folder.getTenantCode();
                BaseContextHandler.setTenant(tenantCode);
                Integer deleteDay = folder.getDeleteDay();
                folder.setDeleteDay(deleteDay == null ? 30 : deleteDay - 1);
                if (folder.getDeleteDay() <= 0) {
                    this.removeById(folder.getId());
                } else {
                    baseMapper.updateById(folder);
                }
            }
        }

    }

    /**
     * 复原被标记为删除的文件夹
     * @param id
     */
    @Override
    public void restorationDeleteFolder(Long id) {

        SiteFolder folder = baseMapper.selectById(id);
        folder.setDeleteStatus(CommonStatus.NO);
        folder.setDeleteTime(null);
        folder.setDeleteDay(30);
        folder.setFolderName(folder.getFolderName() == null ? "未命名文件-复原" : folder.getFolderName() + "复原");

        String letter = ChineseToEnglishUtil.getPinYinFirstLetter(folder.getFolderName());
        folder.setNameFirstLetter(letter);
        baseMapper.updateById(folder);
    }
}
