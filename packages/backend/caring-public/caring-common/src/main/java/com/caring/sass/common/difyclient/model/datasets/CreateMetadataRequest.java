package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMetadataRequest {
    /**
     * 元数据类型，必填
     */
    private String type;

    /**
     * 元数据名称，必填
     */
    private String name;

}
