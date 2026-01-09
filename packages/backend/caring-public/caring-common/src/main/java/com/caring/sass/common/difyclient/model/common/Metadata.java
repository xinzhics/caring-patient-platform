package com.caring.sass.common.difyclient.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 元数据信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {

    /**
     * 模型用量信息
     */
    @JsonProperty("usage")
    private Usage usage;

    /**
     * 引用和归属分段列表
     */
    @JsonProperty("retriever_resources")
    private List<RetrieverResource> retrieverResources;
}
