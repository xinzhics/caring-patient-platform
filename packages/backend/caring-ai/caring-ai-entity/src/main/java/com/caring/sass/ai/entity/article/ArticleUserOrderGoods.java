package com.caring.sass.ai.entity.article;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("科普创作用户下单参数")
@Data
public class ArticleUserOrderGoods {


    private String uid;

    private String openId;

    private ArticleOrderGoodsType orderGoodsType;


}
