package com.caring.sass.ai.dto.know;

import com.caring.sass.ai.entity.know.KnowledgeType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LanguageSearchKnowledge {

    @ApiModelProperty("用户输入的内容")
    @Length(max = 400, message = "请在400字以内说明您的要求")
    private String query;

    @ApiModelProperty("博主域名")
    @Length(max = 100, message = "博主域名不能为空")
    private String userDomain;

    @ApiModelProperty("知识库类型")
    @NotNull
    private KnowledgeType knowledgeType;

    private Long userId;
}
