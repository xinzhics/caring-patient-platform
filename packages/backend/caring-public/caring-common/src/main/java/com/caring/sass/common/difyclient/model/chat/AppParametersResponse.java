package com.caring.sass.common.difyclient.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 应用参数响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppParametersResponse {

    /**
     * 开场白
     */
    private String openingStatement;

    /**
     * 开场推荐问题列表
     */
    private List<String> suggestedQuestions;

    /**
     * 启用回答后给出推荐问题
     */
    private SuggestedQuestionsAfterAnswer suggestedQuestionsAfterAnswer;

    /**
     * 语音转文本
     */
    private SpeechToText speechToText;

    /**
     * 引用和归属
     */
    private RetrieverResource retrieverResource;

    /**
     * 标记回复
     */
    private AnnotationReply annotationReply;

    /**
     * 用户输入表单配置
     */
    private List<Map<String, Object>> userInputForm;

    /**
     * 文件上传配置
     */
    private FileUpload fileUpload;

    /**
     * 系统参数
     */
    private SystemParameters systemParameters;

    /**
     * 启用回答后给出推荐问题
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SuggestedQuestionsAfterAnswer {
        /**
         * 是否开启
         */
        private Boolean enabled;
    }

    /**
     * 语音转文本
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpeechToText {
        /**
         * 是否开启
         */
        private Boolean enabled;
    }

    /**
     * 引用和归属
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RetrieverResource {
        /**
         * 是否开启
         */
        private Boolean enabled;
    }

    /**
     * 标记回复
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnnotationReply {
        /**
         * 是否开启
         */
        private Boolean enabled;
    }

    /**
     * 文件上传配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FileUpload {
        /**
         * 图片设置
         */
        private Image image;
        /**
         * 是否启用文件上传功能
         */
        private Boolean enabled;
        /**
         * 允许的文件类型
         */
        private List<String> allowedFileTypes;
        /**
         * 允许的文件扩展名
         */
        private List<String> allowedFileExtensions;
        /**
         * 传递方式列表
         */
        private List<String> allowedFileUploadMethods;
        /**
         * 文件数量限制
         */
        private Integer numberLimits;
        /**
         * 文件上传配置
         * dify该字段返回为驼峰命名 导致 获取数据失败
         */
        @JsonProperty("fileUploadConfig")
        private FileUploadConfig fileUploadConfig;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class FileUploadConfig {
            /**
             * 文件大小限制 (MB)
             */
            private Integer fileSizeLimit;
            /**
             * 批量上传数量限制
             */
            private Integer batchCountLimit;
            /**
             * 图片文件大小限制 (MB)
             */
            private Integer imageFileSizeLimit;
            /**
             * 视频文件大小限制 (MB)
             */
            private Integer videoFileSizeLimit;
            /**
             * 音频文件大小限制 (MB)
             */
            private Integer audioFileSizeLimit;
            /**
             * 工作流文件上传数量限制
             */
            private Integer workflowFileUploadLimit;
        }

        /**
         * 图片设置
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Image {
            /**
             * 是否开启
             */
            private Boolean enabled;

            /**
             * 图片数量限制
             */
            private Integer numberLimits;

            /**
             * 传递方式列表
             */
            private List<String> transferMethods;
        }
    }

    /**
     * 系统参数
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SystemParameters {
        /**
         * 文档上传大小限制 (MB)
         */
        private Integer fileSizeLimit;

        /**
         * 图片文件上传大小限制 (MB)
         */
        private Integer imageFileSizeLimit;

        /**
         * 音频文件上传大小限制 (MB)
         */
        private Integer audioFileSizeLimit;

        /**
         * 视频文件上传大小限制 (MB)
         */
        private Integer videoFileSizeLimit;
    }
}
