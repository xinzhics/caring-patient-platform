package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分段响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentResponse {
    /**
     * 分段列表
     */
    private List<SegmentInfo> data;

    /**
     * 文档形式
     */
    private String docForm;

    /**
     * 分段信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SegmentInfo {
        /**
         * 分段ID
         */
        private String id;

        /**
         * 位置
         */
        private Integer position;

        /**
         * 文档ID
         */
        private String documentId;

        /**
         * 内容
         */
        private String content;

        /**
         * 答案
         */
        private String answer;

        /**
         * 字数
         */
        private Integer wordCount;

        /**
         * 令牌数
         */
        private Integer tokens;

        /**
         * 关键字
         */
        private List<String> keywords;

        /**
         * 索引节点ID
         */
        private String indexNodeId;

        /**
         * 索引节点哈希
         */
        private String indexNodeHash;

        /**
         * 命中次数
         */
        private Integer hitCount;

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
         * 状态
         */
        private String status;

        /**
         * 创建者
         */
        private String createdBy;

        /**
         * 创建时间
         */
        private Long createdAt;

        /**
         * 索引时间
         */
        private Long indexingAt;

        /**
         * 完成时间
         */
        private Long completedAt;

        /**
         * 错误信息
         */
        private String error;

        /**
         * 停止时间
         */
        private Long stoppedAt;
    }
}
