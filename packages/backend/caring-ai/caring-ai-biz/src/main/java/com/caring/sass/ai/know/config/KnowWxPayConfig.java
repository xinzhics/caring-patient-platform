package com.caring.sass.ai.know.config;

import com.caring.sass.ai.entity.know.KnowledgeUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Data
@Component
@ConfigurationProperties(prefix = KnowWxPayConfig.PREFIX)
public class KnowWxPayConfig {

    public static final String PREFIX = "knowpay.config";

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "收款方的APPID（native支付使用）")
    private String appId;


    @ApiModelProperty(value = "基础会员的价格，单位是分")
    private Integer basicMembershipPrice;


    @ApiModelProperty(value = "基础会员名称")
    private String basicMembershipName;


    @ApiModelProperty(value = "专业版的价格，单位是分")
    private Integer professionalVersionPrice;

    @ApiModelProperty(value = "专业版会员名")
    private String professionalVersionName;


    @ApiModelProperty(value = "公众号appId（公众号用户授权使用）")
    private String gong_zhong_hao_app_id;

    /**
     * 获取 年度会员
     * 优先从博主的配置中获取，如果没有配置，则使用默认的配置
     * @param user
     * @return
     */
    public Integer getProfessionalVersionPrice(KnowledgeUser user) {

        if (Objects.nonNull(user) && Objects.nonNull(user.getProfessionalVersionPrice())) {
            return user.getProfessionalVersionPrice();
        }

        return professionalVersionPrice;
    }


    /**
     * 获取 月度会员
     * 优先从博主的配置中获取，如果没有配置，则使用默认的配置
     * @param user
     * @return
     */
    public Integer getBasicMembershipPrice(KnowledgeUser user) {

        if (Objects.nonNull(user) && Objects.nonNull(user.getBasicMembershipPrice())) {
            return user.getBasicMembershipPrice();
        }

        return basicMembershipPrice;
    }


}
