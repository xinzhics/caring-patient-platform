package com.caring.sass.common.difyclient.model.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    /**
     * 文件 ID
     */
    private String id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 文件 MIME 类型
     */
    private String mimeType;

    /**
     * 创建者 ID
     */
    private String createdBy;

    /**
     * 创建时间（时间戳）
     */
    private Long createdAt;
}
