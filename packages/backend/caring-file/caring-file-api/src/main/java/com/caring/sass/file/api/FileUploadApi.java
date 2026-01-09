package com.caring.sass.file.api;

import com.caring.sass.base.R;
import com.caring.sass.file.dto.FolderSaveDTO;
import com.caring.sass.file.entity.File;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@FeignClient(name = "${caring.feign.file-server:caring-file-server}" /*fallback = FileUploadApiFallback.class*/)
public interface FileUploadApi {


    /**
     * 新建文件夹
     *
     * @param folderSaveDTO 文件夹参数
     */
    @PostMapping(value = "/file/")
    R<FolderSaveDTO> save(@RequestBody @Validated FolderSaveDTO folderSaveDTO);

    /**
     * 批量查询文件或文件夹
     *
     * @param data 批量查询
     * @return 查询结果
     */
    @PostMapping("/file/query")
    R<List<File>> query(@RequestBody File data);

    /**
     * 上传文件
     *
     * @param folderId   文件夹id
     * @param simpleFile 文件
     */
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> upload(
            @NotNull(message = "文件夹不能为空") @RequestParam(value = "folderId") Long folderId,
            @RequestPart(value = "file") MultipartFile simpleFile);

    /**
     * 上传文件
     *
     * @param folderId   文件夹id
     * @param simpleFile 文件
     */
    @PostMapping(value = "/file/uploadAppFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> uploadAppFile(@RequestParam(value = "folderId") Long folderId,
            @RequestPart(value = "file") MultipartFile simpleFile,  @RequestParam(value = "obsFileName") String obsFileName);


    @GetMapping("/file/getFile")
    R<List<File>> getFile();

}
