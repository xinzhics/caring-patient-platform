package com.caring.sass.common.difyclient.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型用量信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usage {

    /**
     * 提示词使用的 token 数量
     */
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    /**
     * 提示词单价
     */
    @JsonProperty("prompt_unit_price")
    private String promptUnitPrice;

    /**
     * 提示词价格单位
     */
    @JsonProperty("prompt_price_unit")
    private String promptPriceUnit;

    /**
     * 提示词总价
     */
    @JsonProperty("prompt_price")
    private String promptPrice;

    /**
     * 补全使用的 token 数量
     */
    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    /**
     * 补全单价
     */
    @JsonProperty("completion_unit_price")
    private String completionUnitPrice;

    /**
     * 补全价格单位
     */
    @JsonProperty("completion_price_unit")
    private String completionPriceUnit;

    /**
     * 补全总价
     */
    @JsonProperty("completion_price")
    private String completionPrice;

    /**
     * 总 token 数量
     */
    @JsonProperty("total_tokens")
    private Integer totalTokens;

    /**
     * 总价
     */
    @JsonProperty("total_price")
    private String totalPrice;

    /**
     * 货币单位
     */
    @JsonProperty("currency")
    private String currency;

    /**
     * 延迟时间（秒）
     */
    @JsonProperty("latency")
    private Double latency;
}
