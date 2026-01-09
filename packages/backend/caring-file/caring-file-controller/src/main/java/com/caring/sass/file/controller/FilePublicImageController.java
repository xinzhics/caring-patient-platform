package com.caring.sass.file.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.dto.image.*;
import com.caring.sass.file.entity.File;
import com.caring.sass.file.entity.FileMy;
import com.caring.sass.file.entity.FilePublicImage;

import com.caring.sass.file.service.FilePublicImageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.file.service.FileService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;


/**
 * <p>
 * 前端控制器
 * 公共图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/filePublicImage")
@Api(value = "FilePublicImage", tags = "公共图片")
//@PreAuth(replace = "filePublicImage:")
public class FilePublicImageController extends SuperController<FilePublicImageService, Long, FilePublicImage, FilePublicImagePageDTO, FilePublicImageSaveDTO, FilePublicImageUpdateDTO> {

    @Autowired
    FileService fileService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FilePublicImage> filePublicImageList = list.stream().map((map) -> {
            FilePublicImage filePublicImage = FilePublicImage.builder().build();
            //TODO 请在这里完成转换
            return filePublicImage;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(filePublicImageList));
    }

    @ApiOperation(value = "上传图片到公众图片")
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
            @NotNull(message = "文件夹不能为空")
            @RequestParam(value = "folderId") Long folderId,
            @RequestParam(value = "file") MultipartFile simpleFile) {
        //1，先将文件存在本地,并且生成文件名
        log.info("contentType={}, name={} , sfname={}", simpleFile.getContentType(), simpleFile.getName(), simpleFile.getOriginalFilename());
        // 忽略路径字段,只处理文件类型
        if (simpleFile.getContentType() == null) {
            return fail("文件为空");
        }
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        File file = fileService.upload(simpleFile, folderId);
        FilePublicImage filePublicImage = new FilePublicImage();
        filePublicImage.setFileClassificationId(classificationId);
        filePublicImage.setFileId(file.getId());
        filePublicImage.setFileName(file.getSubmittedFileName());
        baseService.save(filePublicImage);
        return success(file);
    }


    @PostMapping("pageFileMy")
    @ApiOperation("分页查询公共图片列表")
    public R<IPage<File>> pageFileMy(@RequestBody PageParams<FilePublicImagePageDTO> pageDTOPageParams) {
        IPage buildPage = pageDTOPageParams.buildPage();
        FilePublicImagePageDTO model = pageDTOPageParams.getModel();
        LbqWrapper<FilePublicImage> wrapper = Wraps.<FilePublicImage>lbQ();
        if (model.getFileClassificationId() != null) {
            wrapper.eq(FilePublicImage::getFileClassificationId, model.getFileClassificationId());
        }
        if (model.getFileName() != null) {
            wrapper.like(FilePublicImage::getFileName, model.getFileName());
        }
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        wrapper.orderByDesc(Entity::getUpdateTime);
        IPage<FilePublicImage> page = baseService.page(buildPage, wrapper);
        List<FilePublicImage> records = page.getRecords();
        Page<File> filePage = new Page<>();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> fileIds = records.stream().map(FilePublicImage::getFileId).collect(Collectors.toList());
            List<File> fileList = fileService.listByIds(fileIds);
            Map<Long, File> longFileMap = fileList.stream().collect(Collectors.toMap(File::getId, item -> item));
            List<File> listResultFile = new ArrayList<>();
            for (FilePublicImage record : records) {
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


    @ApiOperation("修改图片的分组或重命名图片")
    @PutMapping("updateFileClassification")
    public R<FilePublicImage> updateFileClassification(@RequestBody FilePublicImageUpdateDTO filePublicImageUpdateDTO) {
        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        Long businessId = filePublicImageUpdateDTO.getBusinessId();
        FilePublicImage filePublicImage = baseService.getById(businessId);
        filePublicImage.setFileClassificationId(filePublicImageUpdateDTO.getFileClassificationId());
        if (StrUtil.isNotEmpty(filePublicImageUpdateDTO.getFileName())) {
            filePublicImage.setFileName(filePublicImageUpdateDTO.getFileName());
        }
        baseService.updateAllById(filePublicImage);
        return R.success(filePublicImage);
    }

    @ApiOperation("删除公共图片中的一个")
    @PutMapping("deleteMyFile/{businessId}")
    public R<Boolean> deleteMyFile(@PathVariable("businessId") Long businessId) {
        BaseContextHandler.setTenant(BizConstant.SUPER_ADMIN);
        baseService.removeById(businessId);
        return R.success(true);
    }







}
