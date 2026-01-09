package com.caring.sass.ai.controller.knows;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.KnowledgeFileSaveDTO;
import com.caring.sass.ai.dto.know.KnowledgeFileUpdateDTO;
import com.caring.sass.ai.dto.know.LanguageSearchKnowledge;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.dao.KnowledgeFileAcademicMaterialsLabelMapper;
import com.caring.sass.ai.know.dao.KnowledgeFileCaseDatabaseLabelMapper;
import com.caring.sass.ai.know.dao.KnowledgeFilePersonalAchievementsLabelMapper;
import com.caring.sass.ai.know.service.KnowledgeFileService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeFile")
@Api(value = "KnowledgeFile", tags = "知识库-dify知识库文档关联表")
public class KnowledgeFileController {


    @Autowired
    KnowledgeFileService knowledgeFileService;

    @Autowired
    KnowledgeUserService knowledgeUserService;

    @Autowired
    KnowledgeFileCaseDatabaseLabelMapper knowledgeFileCaseDatabaseLabelMapper;

    @Autowired
    KnowledgeFilePersonalAchievementsLabelMapper knowledgeFilePersonalAchievementsLabelMapper;

    @Autowired
    KnowledgeFileAcademicMaterialsLabelMapper knowledgeFileAcademicMaterialsLabelMapper;

    @ApiOperation("保存文档到知识库")
    @PostMapping("{knowledgeType}")
    @SysLog("保存文档到知识库")
    public R<Boolean> saveKnowledgeFile(
            @PathVariable KnowledgeType knowledgeType,
            @RequestBody @Validated List<KnowledgeFileSaveDTO> fileList) {

        knowledgeFileService.saveKnowledgeFile(knowledgeType, fileList);

        return R.success(true);
    }



    @ApiOperation(value = "初始化现有知识库文档的元数据")
    @GetMapping("initKnowledgeFileMetadata")
    public R<Boolean> initKnowledgeFileMetadata() {
        knowledgeFileService.initKnowledgeFileMetadata();
        knowledgeFileService.initKnowledgeFileDailyMetadata();
        return R.success(true);
    }


    @ApiOperation("保存一个视频到知识库")
    @PostMapping("{knowledgeType}/{fileType}")
    @SysLog("保存一个视频到知识库")
    public R<Boolean> saveKnowledgeFile(
            @PathVariable KnowledgeType knowledgeType,
            @PathVariable KnowFileType fileType,
            @RequestBody @Validated KnowledgeFileSaveDTO fileSaveDTO) {

        knowledgeFileService.saveKnowledgeFile(knowledgeType, fileType, fileSaveDTO);

        return R.success(true);
    }

    @ApiOperation("删除知识库文档")
    @DeleteMapping("{fileId}")
    @SysLog("删除知识库文档")
    public R<Boolean> deleteKnowledgeFile(@PathVariable Long fileId) {

        knowledgeFileService.deleteKnowledgeFile(fileId);

        return R.success(true);
    }


    @PostMapping("languageSearch")
    @ApiOperation(value = "自然语言搜索日常收集")
    public R<IPage<KnowledgeFile>> languageSearch(@RequestBody PageParams<LanguageSearchKnowledge> params) {

        IPage<KnowledgeFile> builtPage = params.buildPage();
        LanguageSearchKnowledge model = params.getModel();

        String query = model.getQuery();
        KnowledgeType knowledgeType = model.getKnowledgeType();
        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            return R.success(null);
        }
        String userDomain = model.getUserDomain();
        knowledgeFileService.page(builtPage, knowledgeType, query, userId, userDomain);
        return R.success(builtPage);
    }


    @ApiOperation("根据dify文件id查询知识库文档")
    @GetMapping("anno/queryByDifyFileId/{difyFileId}")
    public R<KnowledgeFile> queryByDifyFileId(@PathVariable String difyFileId) {

        KnowledgeFile knowledgeFile = knowledgeFileService.getOne(Wraps.<KnowledgeFile>lbQ()
                .eq(KnowledgeFile::getDifyFileId, difyFileId)
                .last(" limit 0,1 "));
        return R.success(knowledgeFile);

    }


    @ApiOperation("查询一个文档的详情")
    @GetMapping("detail/{id}")
    public R<KnowledgeFile> getAllKnowledgeFile(@PathVariable Long id) {

        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserService.getById(userId);
        if (Objects.isNull(knowledgeUser)) {
            return R.fail("用户不存在");
        }
        KnowledgeFile knowledgeFile = knowledgeFileService.getById(id);
        // 博主
//        KnowledgeUser domainUser = knowledgeUserService.getById(knowledgeFile.getFileUserId());
//        if (Objects.isNull(domainUser)) {
//            return R.fail("博主不存在");
//        }
//        if (KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
//            // 查询当前用户 在博主这里的会员信息
//            List<KnowledgeUserSubscribe> subscribes = knowledgeUserService.subscribeList(Wraps.<KnowledgeUserSubscribe>lbQ()
//                    .eq(KnowledgeUserSubscribe::getUserDomain, domainUser.getUserDomain())
//                    .eq(KnowledgeUserSubscribe::getKnowledgeUserId, userId));
//            KnowledgeUserSubscribe userSubscribe = new KnowledgeUserSubscribe();
//            if (CollUtil.isNotEmpty(subscribes)) {
//                userSubscribe = subscribes.get(0);
//            }
//
//            knowledgeUser.setMembershipLevel(userSubscribe.getMembershipLevel());
//            knowledgeUser.setMembershipExpiration(userSubscribe.getMembershipExpiration());
//            knowledgeUser.setViewPermissions(userSubscribe.getViewPermissions());
//            knowledgeUser.setDownloadPermission(userSubscribe.getDownloadPermission());
//
//            Integer viewPermissions = knowledgeUser.getViewPermissions();
//            if (knowledgeUser.getMembershipExpiration() == null || knowledgeUser.getMembershipExpiration().isBefore(LocalDateTime.now())) {
//                viewPermissions = 1;
//            }
//            if (knowledgeFile.getViewPermissions() > viewPermissions) {
//                return R.fail("权限不足");
//            }
//        }
        if (knowledgeFile.getKnowType().equals(KnowledgeType.ACADEMIC_MATERIALS)) {
            KnowledgeFileAcademicMaterialsLabel materialsLabel = knowledgeFileAcademicMaterialsLabelMapper.selectOne(Wraps.<KnowledgeFileAcademicMaterialsLabel>lbQ()
                            .orderByDesc(SuperEntity::getCreateTime)
                    .last(" limit 1 ")
                    .eq(KnowledgeFileAcademicMaterialsLabel::getFileId, id));
            knowledgeFile.setAcademicMaterialsLabel(materialsLabel);
        } else if (knowledgeFile.getKnowType().equals(KnowledgeType.CASE_DATABASE)) {
            KnowledgeFileCaseDatabaseLabel caseDatabaseLabel = knowledgeFileCaseDatabaseLabelMapper.selectOne(Wraps.<KnowledgeFileCaseDatabaseLabel>lbQ()
                    .last(" limit 1 ")
                    .orderByDesc(SuperEntity::getCreateTime)
                    .eq(KnowledgeFileCaseDatabaseLabel::getFileId, id));
            knowledgeFile.setCaseDatabaseLabel(caseDatabaseLabel);
        } else if (knowledgeFile.getKnowType().equals(KnowledgeType.PERSONAL_ACHIEVEMENTS)) {
            KnowledgeFilePersonalAchievementsLabel personalAchievementsLabel = knowledgeFilePersonalAchievementsLabelMapper.selectOne(Wraps.<KnowledgeFilePersonalAchievementsLabel>lbQ()
                    .orderByDesc(SuperEntity::getCreateTime)
                    .last(" limit 1 ")
                    .eq(KnowledgeFilePersonalAchievementsLabel::getFileId, id));
            knowledgeFile.setPersonalAchievementsLabel(personalAchievementsLabel);
        }
        return R.success(knowledgeFile);

    }


    @ApiOperation("修改文件权限信息")
    @PutMapping("updatePermissions")
    @Deprecated
    @SysLog("修改文件权限信息")
    public R<String> updatePermissions(@RequestBody @Validated KnowledgeFileUpdateDTO knowledgeFileUpdateDTO) {
        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserService.getById(userId);
        if (knowledgeUser == null || KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
            throw new BizException("权限不足");
        }
//        KnowledgeFile knowledgeFile = knowledgeFileService.getById(knowledgeFileUpdateDTO.getId());
//        if (knowledgeFile.getViewPermissions() == null || !knowledgeFile.getViewPermissions().equals(knowledgeFileUpdateDTO.getViewPermissions())) {
//            knowledgeFile.setViewPermissions(knowledgeFileUpdateDTO.getViewPermissions());
//            knowledgeFile.setIsUpdatePermissions(true);
//        }
//        if (knowledgeFile.getDownloadPermission() == null || !knowledgeFile.getDownloadPermission().equals(knowledgeFileUpdateDTO.getDownloadPermission())) {
//            knowledgeFile.setDownloadPermission(knowledgeFileUpdateDTO.getDownloadPermission());
//            knowledgeFile.setIsUpdatePermissions(true);
//        }
//        knowledgeFileService.updateById(knowledgeFile);
        return R.success("success");
    }




    @ApiOperation("修改文件名称标签等信息")
    @PutMapping("nameAndLabel")
    @SysLog("修改文件名称标签等信息")
    public R<String> nameAndLabel(@RequestBody @Validated KnowledgeFileUpdateDTO knowledgeFileUpdateDTO) {


        Long id = knowledgeFileUpdateDTO.getId();
        String fileName = knowledgeFileUpdateDTO.getFileName();

        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserService.getById(userId);
        if (knowledgeUser == null || KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
            throw new BizException("权限不足");
        }

        KnowledgeFile file = new KnowledgeFile();
        file.setId(id);
        file.setFileName(fileName);
        file.setDifyFileStatus(KnowledgeFileStatus.CHECKED);
        knowledgeFileService.updateById(file);
        KnowledgeFile knowledgeFile = knowledgeFileService.getById(id);
        KnowledgeFileAcademicMaterialsLabel materialsLabel = knowledgeFileUpdateDTO.getAcademicMaterialsLabel();
        if (Objects.nonNull(materialsLabel)) {
            if (materialsLabel.getId() == null) {
                materialsLabel.setFileId(id);
                materialsLabel.setKnowId(knowledgeFile.getKnowId());
                materialsLabel.setKnowUserId(knowledgeFile.getFileUserId());
                knowledgeFileAcademicMaterialsLabelMapper.insert(materialsLabel);
            } else {
                knowledgeFileAcademicMaterialsLabelMapper.updateById(materialsLabel);
            }
        }

        KnowledgeFileCaseDatabaseLabel caseDatabaseLabel = knowledgeFileUpdateDTO.getCaseDatabaseLabel();
        if (Objects.nonNull(caseDatabaseLabel)) {
            if (caseDatabaseLabel.getId() == null) {
                caseDatabaseLabel.setFileId(id);
                caseDatabaseLabel.setKnowId(knowledgeFile.getKnowId());
                caseDatabaseLabel.setKnowUserId(knowledgeFile.getFileUserId());
                knowledgeFileCaseDatabaseLabelMapper.insert(caseDatabaseLabel);
            } else {
                knowledgeFileCaseDatabaseLabelMapper.updateById(caseDatabaseLabel);
            }
        }
        KnowledgeFilePersonalAchievementsLabel personalAchievementsLabel = knowledgeFileUpdateDTO.getPersonalAchievementsLabel();
        if (Objects.nonNull(personalAchievementsLabel)) {
            if (personalAchievementsLabel.getId() == null) {
                personalAchievementsLabel.setFileId(id);
                personalAchievementsLabel.setKnowId(knowledgeFile.getKnowId());
                personalAchievementsLabel.setKnowUserId(knowledgeFile.getFileUserId());
                knowledgeFilePersonalAchievementsLabelMapper.insert(personalAchievementsLabel);
            } else {
                knowledgeFilePersonalAchievementsLabelMapper.updateById(personalAchievementsLabel);
            }
        }

        return R.success("SUCCESS");
    }


}
