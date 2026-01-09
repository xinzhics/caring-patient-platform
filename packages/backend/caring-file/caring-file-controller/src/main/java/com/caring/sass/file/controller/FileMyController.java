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
import com.caring.sass.file.dto.image.CatcherImageDto;
import com.caring.sass.file.dto.image.FileMyPageDTO;
import com.caring.sass.file.dto.image.FileMySaveDTO;
import com.caring.sass.file.dto.image.FileMyUpdateDTO;
import com.caring.sass.file.entity.File;
import com.caring.sass.file.entity.FileMy;

import com.caring.sass.file.entity.FilePublicImage;
import com.caring.sass.file.entity.FileRecentlyUsed;
import com.caring.sass.file.service.FileMyService;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.file.service.FilePublicImageService;
import com.caring.sass.file.service.FileRecentlyUsedService;
import com.caring.sass.file.service.FileService;
import com.caring.sass.log.annotation.SysLog;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
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
 * 我的图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/fileMy")
@Api(value = "FileMy", tags = "我的图片")
//@PreAuth(replace = "fileMy:")
public class FileMyController extends SuperController<FileMyService, Long, FileMy, FileMyPageDTO, FileMySaveDTO, FileMyUpdateDTO> {


    @Autowired
    FileService fileService;

    @Autowired
    FileRecentlyUsedService fileRecentlyUsedService;

    @Autowired
    FilePublicImageService filePublicImageService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FileMy> fileMyList = list.stream().map((map) -> {
            FileMy fileMy = FileMy.builder().build();
            //TODO 请在这里完成转换
            return fileMy;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(fileMyList));
    }

    @ApiOperation("抓取远程图片到本地")
    @PostMapping("catcherImage")
    public R<File> catcherImage(@RequestBody CatcherImageDto catcherImageDto) {
        String tenantCode = catcherImageDto.getTenantCode();
        setTenant(tenantCode);
        String imageUrl = catcherImageDto.getImageUrl();
        java.io.File localFile = fileRecentlyUsedService.downloadFile(imageUrl);
        if (localFile == null) {
            File url = new File().setUrl(imageUrl);
            return R.success(url);
        }
        Long folderId = catcherImageDto.getFolderId();
        MockMultipartFile multipartFile = FileUtils.fileToFileItem(localFile);
        File file = fileService.upload(multipartFile, folderId);
        // cms 编辑器中抓取的远程图片不放入图片库我的图片中
//        FileMy fileMy = new FileMy();
//        fileMy.setFileId(file.getId());
//        fileMy.setFileName(file.getSubmittedFileName());
//        baseService.save(fileMy);
        if (localFile.exists()) {
            localFile.delete();
        }
        return R.success(file);
    }


    @ApiOperation(value = "上传文件", notes = "上传文件 ")
    @ApiResponses({
            @ApiResponse(code = 60102, message = "文件夹为空"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classificationId", value = "分组ID", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "folderId", value = "文件夹id", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "附件", dataType = "MultipartFile", allowMultiple = true, required = true),
    })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public R<File> upload(
            @RequestParam(value = "classificationId", required = false) Long classificationId,
            @RequestParam(value = "tenantCode", required = false) String tenantCode,
            @NotNull(message = "文件夹不能为空")
            @RequestParam(value = "folderId") Long folderId,
            @RequestParam(value = "file") MultipartFile simpleFile) {
        //1，先将文件存在本地,并且生成文件名
        log.info("contentType={}, name={} , sfname={}", simpleFile.getContentType(), simpleFile.getName(), simpleFile.getOriginalFilename());
        // 忽略路径字段,只处理文件类型
        if (simpleFile.getContentType() == null) {
            return fail("文件为空");
        }
        setTenant(tenantCode);
        String tenant = BaseContextHandler.getTenant();
        if (!tenant.equals(BizConstant.SUPER_TENANT)) {
            // 项目有上传图片限制
            int count = baseService.count(Wraps.<FileMy>lbQ());
            if (count >= 1000) {
                return R.fail(403, "达到上传数量限制，最多上传1000张");
            }
        }
        File file = fileService.upload(simpleFile, folderId);
        FileMy fileMy = new FileMy();
        fileMy.setFileClassificationId(classificationId);
        fileMy.setFileId(file.getId());
        fileMy.setFileName(file.getSubmittedFileName());
        baseService.save(fileMy);
        return success(file);
    }


    @PostMapping("pageFileMy")
    @ApiOperation("分页查询我的图片列表")
    public R<IPage<File>> pageFileMy(@RequestBody PageParams<FileMyPageDTO> pageDTOPageParams) {
        IPage buildPage = pageDTOPageParams.buildPage();
        FileMyPageDTO model = pageDTOPageParams.getModel();
        setTenant(model.getTenantCode());
        LbqWrapper<FileMy> wrapper = Wraps.<FileMy>lbQ();
        if (model.getFileClassificationId() != null) {
            wrapper.eq(FileMy::getFileClassificationId, model.getFileClassificationId());
        }
        if (model.getFileName() != null) {
            wrapper.like(FileMy::getFileName, model.getFileName());
        }
        String userType = BaseContextHandler.getUserType();
        Long userId = BaseContextHandler.getUserId();
        if (UserType.GLOBAL_ADMIN.equals(userType) || UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            wrapper.eq(SuperEntity::getCreateUser, userId);
        }
        wrapper.orderByDesc(Entity::getUpdateTime);
        IPage<FileMy> page = baseService.page(buildPage, wrapper);
        List<FileMy> records = page.getRecords();
        Page<File> filePage = new Page<>();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> fileIds = records.stream().map(FileMy::getFileId).collect(Collectors.toList());
            List<File> fileList = fileService.listByIds(fileIds);
            Map<Long, File> longFileMap = fileList.stream().collect(Collectors.toMap(File::getId, item -> item));
            List<File> listResultFile = new ArrayList<>();
            for (FileMy record : records) {
                File file = longFileMap.get(record.getFileId());
                File file1 = new File();
                BeanUtils.copyProperties(file, file1);
                file1.setId(file.getId());
                file1.setSubmittedFileName(record.getFileName());
                file1.setBusinessId(record.getId());
                file1.setFileClassificationId(record.getFileClassificationId());
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


    @ApiOperation("修改图片的分组或图片名称")
    @PutMapping("updateFileClassification")
    public R<FileMy> updateFileClassification(@RequestBody FileMyUpdateDTO fileMyUpdateDTO) {
        String tenantCode = fileMyUpdateDTO.getTenantCode();
        setTenant(tenantCode);
        Long businessId = fileMyUpdateDTO.getBusinessId();
        FileMy fileMy = baseService.getById(businessId);
        fileMy.setFileClassificationId(fileMyUpdateDTO.getFileClassificationId());
        if (StrUtil.isNotEmpty(fileMyUpdateDTO.getFileName())) {
            fileMy.setFileName(fileMyUpdateDTO.getFileName());
        }
        baseService.updateAllById(fileMy);
        return R.success(fileMy);
    }

    @ApiOperation("删除我的图片中的一个")
    @PutMapping("deleteMyFile/{businessId}")
    public R<Boolean> deleteMyFile(@PathVariable("businessId") Long businessId,
                                  @RequestParam(value = "tenantCode", required = false) String tenantCode) {
        setTenant(tenantCode);
        baseService.removeById(businessId);
        return R.success(true);
    }



    @ApiOperation("移动最近使用或公共的图片到我的图片中")
    @ApiImplicitParam(value = "fileType", name = "(publicImage)公共图片， (recentlyUsed) 最近使用", type = "String")
    @PutMapping("moveRecentlyUsedToMy")
    public R<Boolean> deleteMyFile(@RequestParam("fileType") String fileType,
                                   @RequestParam("businessId") Long businessId,
                                   @RequestParam(value = "classificationId", required = false) Long classificationId,
                                   @RequestParam(value = "tenantCode", required = false) String tenantCode) {
        setTenant(tenantCode);
        String tenant = BaseContextHandler.getTenant();
        if (!tenant.equals(BizConstant.SUPER_TENANT)) {
            // 项目有上传图片限制
            int count = baseService.count(Wraps.<FileMy>lbQ());
            if (count >= 1000) {
                return R.fail(403, "达到上传数量限制，最多上传1000张");
            }
        }
        FileMy fileMy = new FileMy();
        fileMy.setFileClassificationId(classificationId);
        // 吧公共图片加入到 我的图片
        if (fileType.equals("publicImage")) {
            FilePublicImage publicImage = filePublicImageService.getById(businessId);
            fileMy.setFileId(publicImage.getFileId());
            fileMy.setFileName(publicImage.getFileName());
        } else {
            // 吧最近使用 加入到我的图片
            FileRecentlyUsed recentlyUsed = fileRecentlyUsedService.getById(businessId);
            fileMy.setFileId(recentlyUsed.getFileId());
            fileMy.setFileName(recentlyUsed.getFileName());
        }
        baseService.save(fileMy);
        return R.success(true);
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
