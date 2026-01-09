package com.caring.sass.file.api.fallback;

import com.caring.sass.base.R;
import com.caring.sass.file.api.AttachmentApi;
import com.caring.sass.file.api.FileRecentlyUsedApi;
import com.caring.sass.file.dto.AttachmentDTO;
import com.caring.sass.file.dto.image.FileRecentlyUsedImageDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 熔断
 *
 * @author caring
 * @date 2019/07/25
 */
@Component
public class FileRecentlyUsedApiFallback implements FileRecentlyUsedApi {

    @Override
    public R<Boolean> cmsImageSaveRecentlyUsed(FileRecentlyUsedImageDTO fileRecentlyUsedImageDTO) {
        return R.timeout();
    }


}
