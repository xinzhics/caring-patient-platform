package com.caring.sass.common.difyclient.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMetadataRequest {
    /**
     * 元数据名称，必填
     */
    private String name;
}
