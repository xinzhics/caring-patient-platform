package com.caring.sass.ai.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = FaceConfig.PREFIX)
public class FaceConfig {
    public static final String PREFIX = "face.config";

    /**
     * 旷视face的appKey
     */
    @ApiModelProperty(value = "face的appKey")
    private String apiKey;

    /**
     * 旷视face的appSecret
     */
    @ApiModelProperty(value = "face的appSecret")
    private String apiSecret;

    /**
     * 旷视face的merge_rate 融合比例 范围 [0,100]
     */
    @ApiModelProperty(value = "face的merge_rate 融合比例 范围 [0,100]")
    private int merge_rate;

    /**
     * 旷视face的feature_rate 五官融合比例 范围 [0,100]
     */
    @ApiModelProperty(value = "face的feature_rate 五官融合比例 范围 [0,100]")
    private int feature_rate;

    @ApiModelProperty(value = "新用户免费次数")
    private int new_user_free = 0;

    @ApiModelProperty(value = "合成图片的费用，单位分")
    private int merge_image_cost;

    @ApiModelProperty(value = "合成图片商品名称")
    private String merge_image_goods_name;


    @ApiModelProperty(value = "人脸相似度: 范围[0~1]，越大越相似")
    String source_similarity;

    @ApiModelProperty(value = "高清效果")
    Float gpen;

    @ApiModelProperty(value = "美化效果")
    Float skin;

    @ApiModelProperty(value = "版本号")
    String reqKey;

    @ApiModelProperty(value = "火山引擎AccessKeyId")
    private String accessKeyId;

    @ApiModelProperty(value = "火山引擎SecretAccessKey")
    private String secretAccessKey;

}
