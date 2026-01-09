package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文档列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListResponse {
    /**
     * 文档列表
     */
    private List<DocumentInfo> data;

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
     * 文档信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        /**
         * 文档ID
         */
        private String id;

        /**
         * 位置
         */
        private Integer position;

        /**
         * 数据源类型
         */
        private String dataSourceType;

        /**
         * 数据源信息
         */
        private Object dataSourceInfo;

        /**
         * 知识库处理规则ID
         */
        private String datasetProcessRuleId;

        /**
         * 文档名称
         */
        private String name;

        /**
         * 创建来源
         */
        private String createdFrom;

        /**
         * 创建者
         */
        private String createdBy;

        /**
         * 创建时间
         */
        private Long createdAt;

        /**
         * 令牌数
         */
        private Integer tokens;

        /**
         * 索引状态
         */
        private String indexingStatus;

        /**
         * 错误信息
         */
        private String error;

        /**
         * 是否启用
         */
        private Boolean enabled;

        /**
         * 禁用时间
         */
        private Long disabledAt;

        /**
         * 禁用者
         */
        private String disabledBy;

        /**
         * 是否归档
         */
        private Boolean archived;
    }
}
