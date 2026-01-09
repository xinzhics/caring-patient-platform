package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 元数据响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataResponse {

    /**
     * 元数据ID
     */
    private String id;

    /**
     * 元数据类型
     */
    private String type;

    /**
     * 元数据名称
     */
    private String name;

}
