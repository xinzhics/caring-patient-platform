package com.caring.sass.wx.entity.keyword;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_keyword")
@ApiModel(value = "Keyword", description = "微信服务号自动回复关键词")
@AllArgsConstructor
public class Keyword extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 全匹配
     */
    public static final int MATCH_TYPE_ALL = 1;

    /**
     * 不匹配 即自动回复
     */
    public static final int MATCH_TYPE_NO = -1;

    /**
     * 半匹配
     */
    public static final int MATCH_TYPE_HALF = 0;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    @Length(max = 50, message = "关键词长度不能超过50")
    @TableField(value = "key_word", condition = LIKE)
    @Excel(name = "关键词")
    private String keyWord;

    /**
     * 类型（-1:不匹配，0：半匹配 1：全匹配）
     */
    @ApiModelProperty(value = "类型（-1:不匹配，0：半匹配 1：全匹配）")
    @TableField("match_type")
    @Excel(name = "类型（-1:不匹配，0：半匹配 1：全匹配）")
    private Integer matchType;

    /**
     * 消息类型（0：文本  1：图文  ）
     */
    @ApiModelProperty(value = "消息类型（0：文本  1：图文  ）")
    @TableField(value = "message_type")
    @Excel(name = "消息类型（0：文本  1：图文  ）")
    private Integer msgType;

    /**
     * 媒体ID
     */
    @ApiModelProperty(value = "媒体ID")
    @Length(max = 200, message = "媒体ID长度不能超过200")
    @TableField(value = "media_id", condition = LIKE)
    @Excel(name = "媒体ID")
    private String mediaId;

    /**
     * 规则名
     */
    @ApiModelProperty(value = "规则名")
    @Length(max = 200, message = "规则名长度不能超过200")
    @TableField(value = "rule_name", condition = LIKE)
    @Excel(name = "规则名")
    private String ruleName;


    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    @TableField("del_flag")
    @Excel(name = "删除标志")
    private Integer delFlag;

    /**
     * 回复
     */
    @ApiModelProperty(value = "回复")
    @Length(max = 200, message = "回复长度不能超过200")
    @TableField(value = "reply", condition = LIKE)
    @Excel(name = "回复")
    private String reply;


    @Builder
    public Keyword(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime,
                   String keyWord, Integer matchType, Integer msgType, String mediaId, Integer delFlag,
                   String reply,String ruleName) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.keyWord = keyWord;
        this.matchType = matchType;
        this.msgType = msgType;
        this.mediaId = mediaId;
        this.delFlag = delFlag;
        this.reply = reply;
        this.ruleName = ruleName;
    }

}
