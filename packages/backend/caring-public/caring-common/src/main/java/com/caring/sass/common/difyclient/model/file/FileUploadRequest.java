package com.caring.sass.common.difyclient.model.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;

/**
 * 文件上传请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    /**
     * 用户标识
     */
    private String user;

    @Builder.Default
    private MediaType mediaType = MediaType.parse("application/octet-stream");
}
