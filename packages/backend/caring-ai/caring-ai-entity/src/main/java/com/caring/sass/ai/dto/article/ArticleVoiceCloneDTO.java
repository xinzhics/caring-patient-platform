package com.caring.sass.ai.dto.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("声音克隆")
public class ArticleVoiceCloneDTO {


    @ApiModelProperty("克隆出的语音文本内容")
    @NotEmpty
    private String text;

    @ApiModelProperty("用户上传的声音ID")
    @NotNull
    private Long voice_id;

}
