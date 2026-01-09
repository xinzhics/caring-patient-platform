package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 通过文件更新文档请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentByFileRequest {
    /**
     * 文档名称（选填）
     */
    private String name;

    /**
     * 处理规则（选填）
     */
    private ProcessRule processRule;

    /**
     * 文档类型（选填）
     */
    private String docType;

    /**
     * 文档元数据（如提供文档类型则必填）
     */
    private Map<String, Object> docMetadata;
}
