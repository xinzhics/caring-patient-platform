package com.caring.sass.file.api.fallback;

import com.caring.sass.base.R;
import com.caring.sass.file.api.AttachmentApi;
import com.caring.sass.file.dto.AttachmentDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 熔断
 *
 * @author caring
 * @date 2019/07/25
 */
@Component
public class AttachmentApiFallback implements AttachmentApi {
    @Override
    public R<AttachmentDTO> upload(MultipartFile file, Boolean isSingle, Long id, String bizId, String bizType) {
        return R.timeout();
    }
}
