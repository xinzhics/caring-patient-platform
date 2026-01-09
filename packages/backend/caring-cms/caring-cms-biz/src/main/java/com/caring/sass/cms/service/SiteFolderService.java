package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.SiteFolder;
import com.caring.sass.cms.entity.SiteFolderPage;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 建站文件夹表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
public interface SiteFolderService extends SuperService<SiteFolder> {

    /**
     * 重命名
     * @param siteFolder
     */
    void updateFolderName(SiteFolder siteFolder);


    /**
     * 保存建站成果
     * @param siteFolder
     */
    void updateFolderAndPage(SiteFolder siteFolder);

    /**
     * PC查询建站的所有页面内容
     * @param folderId
     */
    SiteFolder querySiteFolderAllPage(Long folderId);


    String shareFolderFirstPage(Long folderId);


    SiteFolder copyFolder(Long id);


    /**
     * 文件夹存为模板
     * @param id
     * @param folderName
     */
    void saveForTemplateFolder(Long id, String folderName);

    /**
     * 名称是否可以用
     * @param id
     * @param name
     * @param template
     * @return
     */
    boolean nameCanUse(Long id, String name, Integer template);

    /**
     * 检查文件夹是否可以删除
     */
    String checkFolderCanDelete(Long folderId);


    List<SiteFolderPage> saveTemplateToFolder(Long templateFolderId, Long folderId, Long homeId, Boolean replaceHome);


    /**
     * 每日执行一次。更新建站中被删除的文件夹的剩余时长。
     * 当时长等于 0 时。 删除文件夹所有的内容
     */
    void siteFolderUpdateDeleteDay();


    /**
     * 复原被标记删除的文件夹
     * @param id
     */
    void restorationDeleteFolder(Long id);
}
