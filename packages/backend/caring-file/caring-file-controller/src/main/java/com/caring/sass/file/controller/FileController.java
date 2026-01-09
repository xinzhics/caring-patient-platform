package com.caring.sass.file.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.file.dto.*;
import com.caring.sass.file.entity.File;
import com.caring.sass.file.manager.FileRestManager;
import com.caring.sass.file.service.FileService;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.utils.BeanPlusUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文件管理 前端控制器
 * </p>
 *
 * @author caring
 * @since 2019-04-29s
 */
@Validated
@RestController
@RequestMapping("/file")
@Slf4j
@Api(value = "文件管理", tags = "文件管理")
public class FileController extends SuperController<FileService, Long, File, FilePageReqDTO, FolderSaveDTO, FileUpdateDTO> {
    @Autowired
    private FileRestManager fileRestManager;

    @Override
    public void query(PageParams<FilePageReqDTO> params, IPage<File> page, Long defSize) {
        FilePageReqDTO paramsModel = params.getModel();
        String tenantCode = paramsModel.getTenantCode();
        if (StringUtils.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        fileRestManager.page(page, paramsModel);
    }

    @ApiOperation(value = "保存上传后的回调")
    @PostMapping("saveFile")
    public R<File> saveFile(@RequestBody FileSaveDto fileSaveDto) {
        String tenantCode = fileSaveDto.getTenantCode();
        if (StringUtils.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }

        File file = baseService.saveFile(fileSaveDto);
        return R.success(file);
    }

    @Override
    public R<File> handlerSave(FolderSaveDTO model) {
        String tenantCode = model.getTenantCode();
        if (StringUtils.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        FolderDTO folder = baseService.saveFolder(model);
        return success(BeanPlusUtil.toBean(folder, File.class));
    }



    @ApiOperation(value = "检查前端调用obs上传之前，本地的token是否有效")
    @PostMapping("checkUpdateObsFileToken")
    public R<String> checkUpdateObsFileToken() {
        // 检查token的逻辑已经由gateway执行。
        return R.success("success");
    }


    @ApiOperation(value = "删除文件")
    @DeleteMapping("deleteFile/{id}")
    public R<Boolean> delete(@PathVariable Long id, @RequestParam(required = false) String tenantCode) {
        if (StringUtils.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        R<Boolean> delete = handlerDelete(ids);
        return delete;
    }

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件", notes = "上传文件 ")
    @ApiResponses({
            @ApiResponse(code = 60102, message = "文件夹为空"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "folderId", value = "文件夹id", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "附件", dataType = "MultipartFile", allowMultiple = true, required = true),
    })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @SysLog("上传文件")
    public R<File> upload(
            @NotNull(message = "文件夹不能为空")
            @RequestParam(value = "folderId") Long folderId,
            @RequestParam(value = "file") MultipartFile simpleFile) {
        //1，先将文件存在本地,并且生成文件名
        log.info("contentType={}, name={} , sfname={}", simpleFile.getContentType(), simpleFile.getName(), simpleFile.getOriginalFilename());
        // 忽略路径字段,只处理文件类型
        if (simpleFile.getContentType() == null) {
            return fail("文件为空");
        }
        File file = baseService.upload(simpleFile, folderId);
        return success(file);
    }

    @ApiOperation(value = "上传音频并转码", notes = "上传文件 ")
    @RequestMapping(value = "/upload/audio", method = RequestMethod.POST)
    public R<File> uploadAudio(@RequestParam(value = "file") MultipartFile simpleFile) {
        // 将 file 转成 File
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "saas" + java.io.File.separator + "audio";
        String originalFilename = simpleFile.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".pcm")) {
            java.io.File file2 = new java.io.File(path);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            java.io.File file1 = new java.io.File(path + java.io.File.separator + originalFilename.substring(0, originalFilename.indexOf(".")) + ".wav");
            try {
                byte[] fileBytes = simpleFile.getBytes();
                AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 4, 16000, false);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
                AudioInputStream audioInputStream = new AudioInputStream(inputStream, audioFormat, fileBytes.length / audioFormat.getFrameSize());
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file1);
                MockMultipartFile multipartFile = FileUtils.fileToFileItem(file1);
                File file = baseService.upload(multipartFile, 1L);
                return R.success(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                file1.delete();
            }
        } else {
            File file = baseService.upload(simpleFile, 1L);
            return R.success(file);
        }
    }



    @ApiOperation(value = "根据租户上传项目图片", notes = "根据租户上传项目图片 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "folderId", value = "文件夹id", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "附件", dataType = "MultipartFile", allowMultiple = true, required = true),
    })
    @RequestMapping(value = "/projectUpload", method = RequestMethod.POST)
    public R<File> projectUpload(
            @NotNull(message = "文件夹不能为空")
            @RequestParam(value = "tenantCode") String tenantCode,
            @RequestParam(value = "folderId") Long folderId,
            @RequestParam(value = "file") MultipartFile simpleFile) {
        //1，先将文件存在本地,并且生成文件名
        BaseContextHandler.setTenant(tenantCode);
        // 忽略路径字段,只处理文件类型
        if (simpleFile.getContentType() == null) {
            return fail("文件为空");
        }
        File file = baseService.upload(simpleFile, folderId);
        return success(file);
    }



    @ApiOperation(value = "租户服务上传", notes = "上传文件 ")
    @RequestMapping(value = "/uploadAppFile", method = RequestMethod.POST)
    @SysLog("上传文件")
    public R<File> uploadAppFile(@NotNull(message = "文件夹不能为空") @RequestParam(value = "folderId") Long folderId,
                                 @RequestParam(value = "file") MultipartFile simpleFile,
                                 @RequestParam(value = "obsFileName") String obsFileName) {

        File file = baseService.uploadAppFile(simpleFile, folderId, obsFileName);
        return success(file);

    }

    @ApiOperation(value = "医生端无授权上传图片", notes = "上传文件 ")
    @RequestMapping(value = "anno/doctor/upload", method = RequestMethod.POST)
    @SysLog("上传文件")
    public R<File> annoDoctorUpload(@RequestParam(value = "file") MultipartFile file) {
        // 忽略路径字段,只处理文件类型
        if (file.getContentType() == null) {
            return fail("文件为空");
        }
        File upload = baseService.upload(file, 0L);
        return success(upload);
    }

    @ApiOperation(value = "app上传文件", notes = "上传文件 ")
    @RequestMapping(value = "/app/upload", method = RequestMethod.POST)
    @SysLog("上传文件")
    public R<File> upload(@RequestParam(value = "file") MultipartFile file) {
        // 忽略路径字段,只处理文件类型
        if (file.getContentType() == null) {
            return fail("文件为空");
        }
        File upload = baseService.upload(file, 0L);
        return success(upload);
    }



    @Override
    public R<File> handlerUpdate(FileUpdateDTO fileUpdateDTO) {
        // 判断文件名是否有 后缀
        if (StringUtils.isNotEmpty(fileUpdateDTO.getSubmittedFileName())) {
            File oldFile = baseService.getById(fileUpdateDTO.getId());
            if (oldFile.getExt() != null && !fileUpdateDTO.getSubmittedFileName().endsWith(oldFile.getExt())) {
                fileUpdateDTO.setSubmittedFileName(fileUpdateDTO.getSubmittedFileName() + "." + oldFile.getExt());
            }
        }
        File file = BeanPlusUtil.toBean(fileUpdateDTO, File.class);

        baseService.updateById(file);
        return success(file);
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        Long userId = getUserId();
        return success(baseService.removeList(userId, ids));
    }

    /**
     * 下载一个文件或多个文件打包下载
     *
     * @param ids
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "下载一个文件或多个文件打包下载", notes = "下载一个文件或多个文件打包下载")
    @GetMapping(value = "/download", produces = "application/octet-stream")
    @SysLog("下载文件")
    public void download(
            @ApiParam(name = "ids[]", value = "文件id 数组")
            @RequestParam(value = "ids[]") Long[] ids,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileRestManager.download(request, response, ids, null);
    }

    @GetMapping(value = "/getFile")
    public R<List<File>> getFile() {
        LbqWrapper<File> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(File::getDataType, "OTHER");
        LocalDateTime now = LocalDateTime.now();
        queryWrapper.gt(File::getCreateTime, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0 ,0));
        List<File> fileList = baseService.list(queryWrapper);
        return R.success(fileList);
    }


}
