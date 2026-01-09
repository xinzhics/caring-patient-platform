package com.caring.sass.user.redis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KeywordSettingRedisDTO", description = "项目关键字redis存储")
public class KeywordSettingRedisDTO {


    private Long id;

    @ApiModelProperty(value = "关键字")
    private String keywordName;

    @ApiModelProperty(value = "匹配类型(full_match: 全匹配， semi_match 半匹配)")
    private String matchType;

    @ApiModelProperty(value = "关键字规则回复id")
    private Long keywordReplyId;

}
