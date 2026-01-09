package com.caring.sass.ai.article.config;

import com.caring.sass.ai.entity.article.ArticleOrderGoodsType;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = ArticleUserPayConfig.PREFIX)
public class ArticleUserPayConfig {

    public static final String PREFIX = "article.wxpay";

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "公众号appId")
    private String appId;

    @ApiModelProperty(value = "公众号支付appId")
    private String gongnZhifuAppId;

    @ApiModelProperty(value = "年度会员费用，单位是分")
    private Integer annualCost;

    @ApiModelProperty(value = "年度会员包名称")
    private String annualCostName;

    @ApiModelProperty(value = "新用户注册送能量豆")
    private Integer newUserRegister;

    @ApiModelProperty(value = "知识库博主注册送能量豆")
    private Integer chiefPhysicianUseRegister;


    @ApiModelProperty(value = "买会员送能量豆")
    private Integer annualGetEnergyBeans;

    @ApiModelProperty(value = "200能量币价格")
    private Integer energyBeans200Cost;

    @ApiModelProperty(value = "800能量币价格")
    private Integer energyBeans800Cost;


    @ApiModelProperty(value = "500能量币价格+50")
    private Integer energyBeans500Cost;

    @ApiModelProperty(value = "1000能量币价格")
    private Integer energyBeans1000Cost;

    @ApiModelProperty(value = "1500能量币价格+150")
    private Integer energyBeans1500Cost;

    @ApiModelProperty(value = "2000能量币价格+200")
    private Integer energyBeans2000Cost;


    @ApiModelProperty(value = "3000能量币价格+300")
    private Integer energyBeans3000Cost;

    @ApiModelProperty(value = "5000能量币价格+500")
    private Integer energyBeans5000Cost;

    @ApiModelProperty(value = "声音克隆花费")
    private Integer soundCloningSpend;

    @ApiModelProperty(value = "图文创作花费")
    private Integer imageTextSpend;

    @ApiModelProperty(value = "创建播客创作花费")
    private Integer createPodcastSpend;

    @ApiModelProperty(value = "易智声速听精华")
    private Integer podcastQuickListeningEssence;

    @ApiModelProperty(value = "易智声深度发现")
    private Integer podcastDeepDiscovery;

    @ApiModelProperty(value = "创建视频花费")
    private Integer createVideoSpend;

    @ApiModelProperty(value = "形象模版花费")
    private Integer humanTemplateSpend;



    /**
     * 根据购买类型。获取金额
     * @param memberType
     * @return
     */
    public Integer getEnergyBeansNumber(ArticleOrderGoodsType memberType) {

        switch (memberType) {
            case annual:
                return 2000;
            case energyBeans200Cost:
                return 200;
            case energyBeans800Cost:
                return 800;
            case energyBeans3000Cost:
                return 3000;

            case energyBeans500Cost:
                return 550;
            case energyBeans1000Cost:
                return 1100;
            case energyBeans1500Cost:
                return 1650;
            case energyBeans2000Cost:
                return 2200;

            case energyBeans5000Cost:
                return 5500;

        }
        throw new BizException("不存在的商品");
    }



    /**
     * 根据购买类型。获取金额
     * @param memberType
     * @return
     */
    public Integer getCost(ArticleOrderGoodsType memberType) {

        switch (memberType) {
            case annual:
                return annualCost;
            case energyBeans200Cost:
                return energyBeans200Cost;
            case energyBeans800Cost:
                return energyBeans800Cost;
            case energyBeans3000Cost:
                return energyBeans3000Cost;

            case energyBeans500Cost:
                return energyBeans500Cost;
            case energyBeans1000Cost:
                return energyBeans1000Cost;
            case energyBeans1500Cost:
                return energyBeans1500Cost;
            case energyBeans2000Cost:
                return energyBeans2000Cost;

            case energyBeans5000Cost:
                return energyBeans5000Cost;

        }
        throw new BizException("不存在的商品");
    }

    /**
     * 根据购买类型。获取金额
     * @param memberType
     * @return
     */
    public String getCostName(ArticleOrderGoodsType memberType) {

        switch (memberType) {
            case annual:
                return annualCostName;
            case energyBeans200Cost:
                return "200能量豆";
            case energyBeans800Cost:
                return "800能量豆";
            case energyBeans500Cost:
                return "500能量豆";
            case energyBeans1000Cost:
                return "1000能量豆";
            case energyBeans1500Cost:
                return "1500能量豆";
            case energyBeans2000Cost:
                return "2000能量豆";
            case energyBeans3000Cost:
                return "3000能量豆";
            case energyBeans5000Cost:
                return "5000能量豆";
        }
        throw new BizException("不存在的商品");

    }


}
