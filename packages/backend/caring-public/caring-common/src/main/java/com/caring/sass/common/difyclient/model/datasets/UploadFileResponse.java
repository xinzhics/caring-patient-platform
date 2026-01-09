package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传文件响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponse {
    /**
     * 文件ID
     */
    private String id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件下载URL
     */
    private String downloadUrl;

    /**
     * 文件MIME类型
     */
    private String mimeType;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Long createdAt;
}
