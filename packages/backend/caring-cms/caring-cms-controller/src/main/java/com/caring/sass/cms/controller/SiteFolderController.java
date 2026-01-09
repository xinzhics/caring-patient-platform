package com.caring.sass.cms.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.api.UserBizApi;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dto.site.SiteFolderPageDTO;
import com.caring.sass.cms.dto.site.SiteFolderSaveDTO;
import com.caring.sass.cms.dto.site.SiteFolderUpdateDTO;
import com.caring.sass.cms.dto.site.SiteNameCheckDto;
import com.caring.sass.cms.entity.SiteFolder;
import com.caring.sass.cms.entity.SiteFolderPage;
import com.caring.sass.cms.service.SiteFolderService;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.ChineseToEnglishUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.api.UserApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 建站文件夹表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteFolder")
@Api(value = "SiteFolder", tags = "建站文件夹表")
public class SiteFolderController extends SuperController<SiteFolderService, Long, SiteFolder, SiteFolderPageDTO, SiteFolderSaveDTO, SiteFolderUpdateDTO> {

    @Autowired
    UserBizApi userBizApi;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteFolder> siteFolderList = list.stream().map((map) -> {
            SiteFolder siteFolder = SiteFolder.builder().build();
            //TODO 请在这里完成转换
            return siteFolder;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteFolderList));
    }


    @Override
    public R<IPage<SiteFolder>> page(PageParams<SiteFolderPageDTO> params) {
        IPage<SiteFolder> buildPage = params.buildPage();
        SiteFolderPageDTO model = params.getModel();
        LbqWrapper<SiteFolder> lbqWrapper = Wraps.<SiteFolder>lbQ();
        lbqWrapper.eq(SiteFolder::getDeleteStatus, model.getDeleteStatus());
        lbqWrapper.eq(SiteFolder::getTemplate, model.getTemplate());
        if (StrUtil.isNotEmpty(model.getFolderName())) {
            String letter = ChineseToEnglishUtil.getPinYinFirstLetter(model.getFolderName());
            if (StrUtil.isNotEmpty(letter) && model.getTemplate() == 0) {
                lbqWrapper.and(siteFolderLbqWrapper -> siteFolderLbqWrapper.like(SiteFolder::getFolderName, model.getFolderName()).or().eq(SiteFolder::getNameFirstLetter, letter));
            } else {
                lbqWrapper.like(SiteFolder::getFolderName, model.getFolderName());
            }
        }
        baseService.page(buildPage, lbqWrapper);

        // 查询被删除的记录。 需要填充操作人。
        if (model.getDeleteStatus() != null && model.getDeleteStatus().equals(1)) {
            List<SiteFolder> records = buildPage.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                Set<Serializable> updateUserIds = records.stream().map(Entity::getUpdateUser).collect(Collectors.toSet());
                R<List<User>> userList = userBizApi.findUserList(updateUserIds);
                if (userList.getIsSuccess()) {
                    List<User> listData = userList.getData();
                    if (CollUtil.isNotEmpty(listData)) {
                        Map<Long, String> stringMap = listData.stream().collect(Collectors.toMap(SuperEntity::getId, User::getName));
                        for (SiteFolder record : records) {
                            String s = stringMap.get(record.getUpdateUser());
                            record.setUpdateUserName(s);
                        }
                    }
                }
            }
        }
        return R.success(buildPage);
    }

    @ApiOperation("修改文件夹名称")
    @PutMapping("updateFolderName")
    public R<Boolean> updateFolderName(@RequestBody SiteFolderUpdateDTO siteFolderUpdateDTO) {
        Long updateDTOId = siteFolderUpdateDTO.getId();
        String folderName = siteFolderUpdateDTO.getFolderName();

        SiteFolder siteFolder = new SiteFolder();
        siteFolder.setId(updateDTOId);
        siteFolder.setFolderName(folderName);
        baseService.updateFolderName(siteFolder);
        return R.success(true);
    };


    @ApiOperation("删除文件夹")
    @DeleteMapping("deleteFolder")
    public R<Boolean> deleteFolder(@RequestParam Long id) {

        SiteFolder siteFolder = new SiteFolder();
        siteFolder.setId(id);
        siteFolder.setDeleteStatus(CommonStatus.YES);
        siteFolder.setDeleteTime(LocalDateTime.now());
        baseService.updateById(siteFolder);
        return R.success(true);
    }

    @ApiOperation("删除模版文件夹")
    @DeleteMapping("deleteTemplateFolder")
    public R<Boolean> deleteTemplateFolder(@RequestParam Long id) {

        baseService.removeById(id);
        return R.success(true);
    }


    @ApiOperation("检查文件是否可以删除")
    @DeleteMapping("checkFolderCanDelete")
    public R<Boolean> checkFolderCanDelete(@RequestParam Long id) {
        String tenantNames = baseService.checkFolderCanDelete(id);
        if (StrUtil.isEmpty(tenantNames)) {
            return R.success(true);
        } else {
            return R.fail(-1, tenantNames);
        }
    }

    @ApiOperation("复原被标记删除的文件夹")
    @PutMapping("restorationDeleteFolder")
    public R<Boolean> restorationDeleteFolder(@RequestParam Long id) {

        baseService.restorationDeleteFolder(id);
        return R.success(true);
    }


    @ApiOperation("复制文件夹")
    @PutMapping("copyFolder")
    public R<SiteFolder> copyFolder(@RequestParam Long id) {

        SiteFolder siteFolder = baseService.copyFolder(id);
        return R.success(siteFolder);
    }

    @ApiOperation("检查名称是否可以使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件夹ID"),
            @ApiImplicitParam(name = "folderName", value = "文件夹名称"),
            @ApiImplicitParam(name = "template", value = "是否模板使用")
    })
    @PostMapping("checkFolderNameExist")
    public R<Boolean> checkFolderNameExist(@RequestBody SiteNameCheckDto siteNameCheckDto) {
        boolean canUse = baseService.nameCanUse(siteNameCheckDto.getId(), siteNameCheckDto.getFolderName(), siteNameCheckDto.getTemplate());
        return R.success(canUse);
    }


    @ApiOperation("存为模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件夹ID"),
            @ApiImplicitParam(name = "folderName", value = "模板文件夹名称")
    })
    @PutMapping("saveForTemplateFolder")
    public R<Boolean> saveForTemplateFolder(@RequestBody SiteFolderUpdateDTO updateDTO) {

        baseService.saveForTemplateFolder(updateDTO.getId(), updateDTO.getFolderName());

        return R.success(true);
    }



    @ApiOperation("保存建站成果")
    @PutMapping("updateFolderAndPage")
    public R<Boolean> updateFolderAndPage(@RequestBody SiteFolder siteFolder) {

        baseService.updateFolderAndPage(siteFolder);
        return R.success(true);
    }

    /**
     * 将模版文件夹页面带入的文件夹，并根据条件处理首页
     * @param templateFolderId
     * @param folderId
     * @param replaceHome
     * @return
     */
    @ApiOperation("保存模板页面到新文件夹")
    @PutMapping("saveTemplateToFolder")
    public R<List<SiteFolderPage>> saveTemplateToFolder(@RequestParam Long templateFolderId,
                                                        @RequestParam Long folderId,
                                                        @RequestParam Long homeId,
                                                        @RequestParam Boolean replaceHome) {

        List<SiteFolderPage> siteFolderPages = baseService.saveTemplateToFolder(templateFolderId, folderId, homeId, replaceHome);
        return R.success(siteFolderPages);
    }


    @ApiOperation("PC查询建站的所有页面内容")
    @GetMapping("querySiteFolderAllPage")
    public R<SiteFolder> querySiteFolderAllPage(@RequestParam Long folderId) {

        SiteFolder siteFolder = baseService.querySiteFolderAllPage(folderId);
        return R.success(siteFolder);

    }

    @ApiOperation("PC预览首页链接")
    @GetMapping("preview")
    public R<String> preview(@RequestParam Long folderId) {

        String url = baseService.shareFolderFirstPage(folderId);
        return R.success(url + "?preview=true");
    }

    @ApiOperation("PC分享文件夹的第一个页面")
    @GetMapping("shareFolderFirstPage")
    public R<String> shareFolderFirstPage(@RequestParam Long folderId) {

        SiteFolder folder = baseService.getById(folderId);
        if (folder.getCanShare() == null || CommonStatus.NO.equals(folder.getCanShare())) {
            return R.fail(-1, folder.getShareErrorMessage());
        }
        String url = baseService.shareFolderFirstPage(folderId);
        return R.success(url);
    }


}
