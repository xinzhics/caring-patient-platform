package com.caring.sass.common.difyclient.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 应用信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppInfoResponse {
    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用标签
     */
    private List<String> tags;
}
