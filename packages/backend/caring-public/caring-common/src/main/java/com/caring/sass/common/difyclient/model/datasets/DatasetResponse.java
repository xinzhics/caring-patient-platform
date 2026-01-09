package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetResponse {
    /**
     * 知识库ID
     */
    private String id;

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 提供者
     */
    private String provider;

    /**
     * 权限
     */
    private String permission;

    /**
     * 数据源类型
     */
    private String dataSourceType;

    /**
     * 索引技术
     */
    private String indexingTechnique;

    /**
     * 应用数量
     */
    private Integer appCount;

    /**
     * 文档数量
     */
    private Integer documentCount;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Long createdAt;

    /**
     * 更新者
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private Long updatedAt;

    /**
     * Embedding模型
     */
    private String embeddingModel;

    /**
     * Embedding模型提供商
     */
    private String embeddingModelProvider;

    /**
     * Embedding是否可用
     */
    private Boolean embeddingAvailable;
}
