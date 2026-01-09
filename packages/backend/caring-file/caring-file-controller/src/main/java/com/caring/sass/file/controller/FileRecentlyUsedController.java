package com.caring.sass.file.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.dto.image.*;
import com.caring.sass.file.entity.File;
import com.caring.sass.file.entity.FileMy;
import com.caring.sass.file.entity.FilePublicImage;
import com.caring.sass.file.entity.FileRecentlyUsed;

import com.caring.sass.file.service.FileMyService;
import com.caring.sass.file.service.FilePublicImageService;
import com.caring.sass.file.service.FileRecentlyUsedService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.file.service.FileService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.taskdefs.War;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;


/**
 * <p>
 * 前端控制器
 * 最近使用图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/fileRecentlyUsed")
@Api(value = "FileRecentlyUsed", tags = "最近使用图片")
//@PreAuth(replace = "fileRecentlyUsed:")
public class FileRecentlyUsedController extends SuperController<FileRecentlyUsedService, Long, FileRecentlyUsed, FileRecentlyUsedPageDTO, FileRecentlyUsedSaveDTO, FileRecentlyUsedUpdateDTO> {

    @Autowired
    FileService fileService;

    @Autowired
    FileMyService fileMyService;

    @Autowired
    FilePublicImageService filePublicImageService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FileRecentlyUsed> fileRecentlyUsedList = list.stream().map((map) -> {
            FileRecentlyUsed fileRecentlyUsed = FileRecentlyUsed.builder().build();
            //TODO 请在这里完成转换
            return fileRecentlyUsed;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(fileRecentlyUsedList));
    }

    @ApiOperation("cms中图片解析")
    @PostMapping("cmsImageSaveRecentlyUsed")
    public R<Boolean> cmsImageSaveRecentlyUsed(@RequestBody @Validated FileRecentlyUsedImageDTO fileRecentlyUsedImageDTO) {

        List<String> imageUrl = fileRecentlyUsedImageDTO.getImageUrl();
        String tenant = fileRecentlyUsedImageDTO.getTenant();
        Long userId = fileRecentlyUsedImageDTO.getUserId();
        String userType = fileRecentlyUsedImageDTO.getUserType();

        BaseContextHandler.setTenant(tenant);
        BaseContextHandler.setUserId(userId);
        BaseContextHandler.setUserType(userType);

        for (String url : imageUrl) {

            if (StrUtil.isBlank(url)) {
                continue;
            }
            // 查询链接地址是否已经上传到华为云
            File one = fileService.getOne(Wraps.<File>lbQ().eq(File::getUrl, url).last("limit 1"));
            if (Objects.isNull(one)) {
                // 下载并上传图片。
                java.io.File file = baseService.downloadFile(url);
                if (file == null) {
                    continue;
                }
                MockMultipartFile multipartFile = FileUtils.fileToFileItem(file);
                one = fileService.upload(multipartFile, 100L);
                if (file.exists()) {
                    file.delete();
                }
            }
            // 新增或更新 最近使用记录
            if (Objects.nonNull(one)) {
                LbqWrapper<FileRecentlyUsed> lbqWrapper = Wraps.<FileRecentlyUsed>lbQ()
                        .eq(FileRecentlyUsed::getFileId, one.getId())
                        .last(" limit 0,1 ");
                if (UserType.GLOBAL_ADMIN.equals(userType) || UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
                    lbqWrapper.eq(SuperEntity::getCreateUser, userId);
                }
                FileRecentlyUsed recentlyUsed = baseService.getOne(lbqWrapper);
                if (recentlyUsed == null) {
                    recentlyUsed = new FileRecentlyUsed();
                    recentlyUsed.setFileId(one.getId());
                    recentlyUsed.setFileName(one.getSubmittedFileName());
                    baseService.save(recentlyUsed);
                } else {
                    recentlyUsed.setCreateTime(LocalDateTime.now());
                    baseService.updateById(recentlyUsed);
                }
            }
        }

        return R.success(true);
    }

    @ApiOperation("最近使用新增一个")
    @PostMapping("saveRecentlyUsed")
    public R<Boolean> saveRecentlyUsed(@RequestBody @Validated FileRecentlyUsedSaveDTO fileRecentlyUsedSaveDTO) {
        String tenantCode = fileRecentlyUsedSaveDTO.getTenantCode();
        setTenant(tenantCode);
        List<Long> businessIds = fileRecentlyUsedSaveDTO.getBusinessIds();
        String fileType = fileRecentlyUsedSaveDTO.getFileType();
        Map<Long, String> fileNameMap;
        Map<Long, Long> fileIdMap;
        if (fileType.equals("my")) {
            List<FileMy> list = fileMyService.list(Wraps.<FileMy>lbQ().in(FileMy::getId, businessIds));
            fileNameMap = list.stream().collect(Collectors.toMap(FileMy::getId, FileMy::getFileName, (o1, o2) -> o2));
            fileIdMap = list.stream().collect(Collectors.toMap(FileMy::getId, FileMy::getFileId, (o1, o2) -> o2));
        } else if (fileType.equals("publicImage")) {
            List<FilePublicImage> publicImages = filePublicImageService.list(Wraps.<FilePublicImage>lbQ().in(FilePublicImage::getId, businessIds));
            fileNameMap = publicImages.stream().collect(Collectors.toMap(FilePublicImage::getId, FilePublicImage::getFileName, (o1, o2) -> o2));
            fileIdMap = publicImages.stream().collect(Collectors.toMap(FilePublicImage::getId, FilePublicImage::getFileId, (o1, o2) -> o2));
        } else {
            List<FileRecentlyUsed> publicImages = baseService.list(Wraps.<FileRecentlyUsed>lbQ().in(FileRecentlyUsed::getId, businessIds));
            fileNameMap = publicImages.stream().collect(Collectors.toMap(FileRecentlyUsed::getId, FileRecentlyUsed::getFileName, (o1, o2) -> o2));
            fileIdMap = publicImages.stream().collect(Collectors.toMap(FileRecentlyUsed::getId, FileRecentlyUsed::getFileId, (o1, o2) -> o2));
        }
        String userType = BaseContextHandler.getUserType();
        Long userId = BaseContextHandler.getUserId();

        for (Long businessId : businessIds) {
            String fileName = fileNameMap.get(businessId);
            Long fileId = fileIdMap.get(businessId);
            LbqWrapper<FileRecentlyUsed> lbqWrapper = Wraps.<FileRecentlyUsed>lbQ()
                    .eq(FileRecentlyUsed::getFileId, fileId)
                    .last(" limit 0,1 ");
            if (UserType.GLOBAL_ADMIN.equals(userType) || UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
                lbqWrapper.eq(SuperEntity::getCreateUser, userId);
            }
            FileRecentlyUsed recentlyUsed = baseService.getOne(lbqWrapper);
            if (recentlyUsed == null) {
                recentlyUsed = new FileRecentlyUsed();
                recentlyUsed.setFileName(fileName);
                recentlyUsed.setFileId(fileId);
                baseService.save(recentlyUsed);
            } else {
                recentlyUsed.setCreateTime(LocalDateTime.now());
                baseService.updateById(recentlyUsed);
            }
        }
        return R.success(true);
    }

    @PostMapping("pageFileMy")
    @ApiOperation("分页查询最近使用列表")
    public R<IPage<File>> pageFileMy(@RequestBody  @Validated PageParams<FileRecentlyUsedPageDTO> pageDTOPageParams) {
        IPage buildPage = pageDTOPageParams.buildPage();
        FileRecentlyUsedPageDTO model = pageDTOPageParams.getModel();
        setTenant(model.getTenantCode());
        LbqWrapper<FileRecentlyUsed> wrapper = Wraps.<FileRecentlyUsed>lbQ();
        wrapper.orderByDesc(Entity::getCreateTime);
        String userType = BaseContextHandler.getUserType();
        Long userId = BaseContextHandler.getUserId();
        if (UserType.GLOBAL_ADMIN.equals(userType) || UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            wrapper.eq(SuperEntity::getCreateUser, userId);
        }
        if (model.getFileName() != null) {
            wrapper.like(FileRecentlyUsed::getFileName, model.getFileName());
        }
        IPage<FileRecentlyUsed> page = baseService.page(buildPage, wrapper);
        List<FileRecentlyUsed> records = page.getRecords();
        Page<File> filePage = new Page<>();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> fileIds = records.stream().map(FileRecentlyUsed::getFileId).collect(Collectors.toList());
            List<File> fileList = fileService.listNoCodeByIds(fileIds);
            Map<Long, File> longFileMap = fileList.stream().collect(Collectors.toMap(File::getId, item -> item));
            List<File> listResultFile = new ArrayList<>();
            for (FileRecentlyUsed record : records) {
                File file = longFileMap.get(record.getFileId());
                File file1 = new File();
                BeanUtils.copyProperties(file, file1);
                file1.setId(file.getId());
                file1.setSubmittedFileName(record.getFileName());
                file1.setBusinessId(record.getId());
                listResultFile.add(file1);
            }
            filePage.setRecords(listResultFile);
            filePage.setTotal(page.getTotal());
            filePage.setSize(page.getSize());
            filePage.setPages(page.getPages());
            filePage.setCurrent(page.getCurrent());
        }
        return R.success(filePage);

    }



    @ApiOperation("删除最近使用中的一个")
    @PutMapping("deleteMyFile/{businessId}")
    public R<Boolean> deleteMyFile(@PathVariable("businessId") Long businessId,
                                   @RequestParam(value = "tenantCode", required = false) String tenantCode) {
        setTenant(tenantCode);
        baseService.removeById(businessId);
        return R.success(true);
    }

    @ApiOperation("最近使用重命名图片")
    @PutMapping("updateRecentlyUsedFileName")
    public R<FileRecentlyUsed> updateFileClassification(@RequestBody FileRecentlyUsedUpdateDTO fileRecentlyUsedUpdateDTO) {
        String tenantCode = fileRecentlyUsedUpdateDTO.getTenantCode();
        setTenant(tenantCode);
        Long businessId = fileRecentlyUsedUpdateDTO.getBusinessId();
        FileRecentlyUsed fileRecentlyUsed = baseService.getById(businessId);

        if (StrUtil.isNotEmpty(fileRecentlyUsedUpdateDTO.getFileName())) {
            fileRecentlyUsed.setFileName(fileRecentlyUsedUpdateDTO.getFileName());
        }
        baseService.updateAllById(fileRecentlyUsed);
        return R.success(fileRecentlyUsed);
    }

    private void setTenant(String tenantCode) {
        if (StrUtil.isEmpty(tenantCode)) {
            String tenant = BaseContextHandler.getTenant();
            if (StrUtil.isEmpty(tenant)) {
                throw new BizException("租户不能为空");
            }
        } else {
            BaseContextHandler.setTenant(tenantCode);
        }
    }
}
