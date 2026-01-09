package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识库列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetListResponse {
    /**
     * 知识库列表
     */
    private List<DatasetInfo> data;

    /**
     * 是否有更多
     */
    private Boolean hasMore;

    /**
     * 每页数量
     */
    private Integer limit;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 知识库信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatasetInfo {
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
        private String createdAt;

        /**
         * 更新者
         */
        private String updatedBy;

        /**
         * 更新时间
         */
        private String updatedAt;
    }
}
