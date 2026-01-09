package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 新增文档分段请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSegmentsRequest {
    /**
     * 分段列表
     */
    private List<SegmentInfo> segments;

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
    }
}
