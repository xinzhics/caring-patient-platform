package com.caring.sass.file.api;


import com.caring.sass.base.R;
import com.caring.sass.file.dto.AttachmentDTO;
import com.caring.sass.file.dto.image.FileRecentlyUsedImageDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 *
 * @author caring
 * @date 2019/06/21
 */
@FeignClient(name = "${caring.feign.file-server:caring-file-server}"/*, fallback = AttachmentApiFallback.class*/)
public interface FileRecentlyUsedApi {

    @ApiOperation("cms中图片解析")
    @PostMapping("/fileRecentlyUsed/cmsImageSaveRecentlyUsed")
    R<Boolean> cmsImageSaveRecentlyUsed(@RequestBody @Validated FileRecentlyUsedImageDTO fileRecentlyUsedImageDTO);

}
