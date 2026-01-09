package com.caring.sass.common.difyclient.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 引用和归属分段信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetrieverResource {

    /**
     * 在消息中的位置
     */
    @JsonProperty("position")
    private Integer position;

    /**
     * 数据集ID
     */
    @JsonProperty("dataset_id")
    private String datasetId;

    /**
     * 数据集名称
     */
    @JsonProperty("dataset_name")
    private String datasetName;

    /**
     * 文档ID
     */
    @JsonProperty("document_id")
    private String documentId;

    /**
     * 文档名称
     */
    @JsonProperty("document_name")
    private String documentName;

    /**
     * 分段ID
     */
    @JsonProperty("segment_id")
    private String segmentId;

    /**
     * 相似度分数
     */
    @JsonProperty("score")
    private Double score;

    /**
     * 分段内容
     */
    @JsonProperty("content")
    private String content;
}
