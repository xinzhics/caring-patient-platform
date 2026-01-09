package com.caring.sass.wx.dto.keyword;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 微信服务号自动回复关键词
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KeywordPageDTO", description = "微信服务号自动回复关键词")
public class KeywordPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    @Length(max = 50, message = "关键词长度不能超过50")
    private String keyWord;
    /**
     * 类型（0：半匹配 1：全匹配）
     */
    @ApiModelProperty(value = "类型（0：半匹配 1：全匹配）")
    private Integer matchType;
    /**
     * 消息类型（0：文本  1：图文  ）
     */
    @ApiModelProperty(value = "消息类型（0：文本  1：图文  ）")
    private Integer msgType;
    /**
     * 媒体ID
     */
    @ApiModelProperty(value = "媒体ID")
    @Length(max = 200, message = "媒体ID长度不能超过200")
    private String mediaId;
    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    /**
     * 回复
     */
    @ApiModelProperty(value = "回复")
    @Length(max = 200, message = "回复长度不能超过200")
    private String reply;

    /**
     * 规则名
     */
    @ApiModelProperty(value = "规则名")
    @Length(max = 200, message = "规则名长度不能超过200")
    private String ruleName;
}
