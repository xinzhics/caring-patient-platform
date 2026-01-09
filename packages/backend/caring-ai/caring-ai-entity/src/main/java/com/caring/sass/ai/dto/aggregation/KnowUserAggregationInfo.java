package com.caring.sass.ai.dto.aggregation;

import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KnowUserAggregationInfo {

    @ApiModelProperty(value = "知识库博主手机号对应的AI名片信息")
    private BusinessCard businessCard;

    @ApiModelProperty(value = "科普患教用户ID： 不存在时科普患教无内容")
    private Long articleUserId;

    @ApiModelProperty(value = "文献解读用户ID")
    private Long textualInterpretationUserId;

    @ApiModelProperty(value = "知识库博主自己信息")
    private KnowledgeUser knowledgeUser;


}
