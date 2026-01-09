package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检索请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveRequest {
    /**
     * 检索关键词
     */
    private String query;

    /**
     * 检索参数
     */
    private RetrievalModel retrievalModel;

    /**
     * 外部检索模型
     */
    private Object externalRetrievalModel;
}
