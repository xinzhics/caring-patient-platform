package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("AI创作开启下一步的参数")
public class ArticleSee {

    @ApiModelProperty("会话ID")
    @NotNull
    private String uid;


    @ApiModelProperty("任务ID")
    @NotNull
    private Long taskId;

    private Boolean rewrite;

}
