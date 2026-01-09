package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.SiteFolderPage;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 建站文件夹中的页面表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
public interface SiteFolderPageService extends SuperService<SiteFolderPage> {



    /**
     * 保存或更新页面
     * @param folderId
     * @param siteFolderPages
     * @return 返回 null 表示没有异常，可以分享。 否则不可分享
     */
    String saveOrUpdateSiteFolder(Long folderId, List<SiteFolderPage> siteFolderPages);


    /**
     * 查询所有页面的 通过文件夹ID
     * @param folderId
     * @return
     */
    List<SiteFolderPage> queryAllPageByFolderId(Long folderId);


    /**
     * 查询模版页面的所有信息
     * @param pageId
     * @return
     */
    SiteFolderPage getTemplatePageInfo(Long pageId);

    /**
     * 复制文件夹内的所有的页面
     * @param oldFolder
     * @param newFolder
     */
    void copyPageByFolder(Long oldFolder, Long newFolder);


    /**
     * 检查名称是否可以用
     * @param folderName
     * @return
     */
    boolean nameCanUse(String folderName);

    /**
     * 保存页面为模板
     */
    void savePageTemplate(SiteFolderPage siteFolderPage,  String pageName);

    /**
     * 保存页面到redis缓存
     * @param siteFolderPages
     */
    void savePageToRedis(List<SiteFolderPage> siteFolderPages);

    /**
     * 获取页面
     * @param pageId
     * @param domain
     */
    SiteFolderPage getPage(Long pageId, String domain);

    /**
     * 复制页面
     */
    void copyPage(SiteFolderPage siteFolderPage);


    List<SiteFolderPage> saveTemplateToFolder(Long templateFolderId, Long folderId, Long homeId, Boolean replaceHome);

    /**
     * 分享时的页面列表
     * @return
     */
    String preview(Long pageId);


    /**
     * 保存系统模板
     * @param pageId
     */
    void saveSystemPageTemplate(Long pageId);



}
