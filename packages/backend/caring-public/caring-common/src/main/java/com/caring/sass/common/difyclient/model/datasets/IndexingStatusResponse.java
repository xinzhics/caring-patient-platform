package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文档嵌入状态响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexingStatusResponse {
    /**
     * 状态列表
     */
    private List<IndexingStatus> data;

    /**
     * 嵌入状态
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexingStatus {
        /**
         * 文档ID
         */
        private String id;

        /**
         * 索引状态
         */
        private String indexingStatus;

        /**
         * 处理开始时间
         */
        private Double processingStartedAt;

        /**
         * 解析完成时间
         */
        private Double parsingCompletedAt;

        /**
         * 清洗完成时间
         */
        private Double cleaningCompletedAt;

        /**
         * 分段完成时间
         */
        private Double splittingCompletedAt;

        /**
         * 完成时间
         */
        private Double completedAt;

        /**
         * 暂停时间
         */
        private Double pausedAt;

        /**
         * 错误信息
         */
        private String error;

        /**
         * 停止时间
         */
        private Double stoppedAt;

        /**
         * 已完成分段数
         */
        private Integer completedSegments;

        /**
         * 总分段数
         */
        private Integer totalSegments;
    }
}
