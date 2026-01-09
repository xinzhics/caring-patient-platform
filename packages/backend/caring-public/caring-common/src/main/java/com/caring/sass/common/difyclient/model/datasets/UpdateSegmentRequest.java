package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新分段请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSegmentRequest {
    /**
     * 分段信息
     */
    private SegmentInfo segment;

    /**
     * 分段信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SegmentInfo {
        /**
         * 文本内容/问题内容
         */
        private String content;

        /**
         * 答案内容
         */
        private String answer;

        /**
         * 关键字
         */
        private List<String> keywords;

        /**
         * 是否启用
         */
        private Boolean enabled;

        /**
         * 是否重新生成子分段
         */
        private Boolean regenerateChildChunks;
    }
}
