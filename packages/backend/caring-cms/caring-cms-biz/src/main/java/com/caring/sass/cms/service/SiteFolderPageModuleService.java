package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.SiteFolderPage;
import com.caring.sass.cms.entity.SiteFolderPageModule;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 建站文件夹中的页面表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
public interface SiteFolderPageModuleService extends SuperService<SiteFolderPageModule> {


    /**
     * 新增。或修改。或删除页面的组件。
     * 数据库存在，前端没有传的组件默认为删除
     * @param folderId
     * @param pageId
     * @param stringBuilder
     * @param moduleList
     */
    void saveOrUpdate(Long folderId, Long pageId, StringBuilder stringBuilder, List<SiteFolderPageModule> moduleList);


    /**
     * 查询文件夹下所有页面的组件
     * @param folderId
     * @param folderPages
     */
    void queryModule(Long folderId, List<SiteFolderPage> folderPages);

    /**
     * 查询单个页面的所有组件
     * @param pageId
     * @param folderPages
     */
    void queryPageModule(Long pageId, SiteFolderPage folderPages);

    /**
     * 清除页面内的信息
     * @param pageId
     */
    void deletePageInfo(Long pageId);

    void copyModule(Long oldPageId, Long newPageId, Long newFolderId, Map<Long, Long> pageIdMap);


    /**
     * 复制页面。保存页面为模板。保存系统模板时使用
     * @param moduleList
     * @param pageId
     * @param saveJumpPage
     */
    void copyModule(List<SiteFolderPageModule> moduleList, Long pageId, Boolean saveJumpPage, Boolean saveJump);



    void batchBuildModuleStyle();


}
