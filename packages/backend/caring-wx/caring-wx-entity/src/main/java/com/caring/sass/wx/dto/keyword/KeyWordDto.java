package com.caring.sass.wx.dto.keyword;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName KeyWordDto
 * @Description
 * @Author yangShuai
 * @Date 2020/9/17 11:36
 * @Version 1.0
 */
@Getter
@Setter
public class KeyWordDto {

    @ApiModelProperty(value = "会员消息内容")
    private String content;

}
