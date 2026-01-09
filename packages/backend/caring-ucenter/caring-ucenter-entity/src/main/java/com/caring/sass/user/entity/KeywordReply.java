package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 关键字回复内容
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-06
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_keyword_reply")
@ApiModel(value = "KeywordReply", description = "关键字回复内容")
@AllArgsConstructor
public class KeywordReply extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 规则名
     */
    @ApiModelProperty(value = "规则名")
    @Length(max = 60, message = "规则名长度不能超过60")
    @TableField(value = "rule_name", condition = LIKE)
    @Excel(name = "规则名")
    private String ruleName;

    /**
     * 回复类型 (text, cms)
     * {@link com.caring.sass.user.constant.KeyWordEnum}
     */
    @ApiModelProperty(value = "回复类型 (text, cms)")
    @Length(max = 255, message = "回复类型 (text, cms)长度不能超过255")
    @TableField(value = "reply_type", condition = LIKE)
    @Excel(name = "回复类型 (text, cms)")
    private String replyType;

    /**
     * 回复内容
     */
    @ApiModelProperty(value = "回复内容")
    @Length(max = 600, message = "回复内容长度不能超过600")
    @TableField(value = "reply_content", condition = LIKE)
    @Excel(name = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "启用或关闭（open, close）")
    @TableField(value = "status", condition = EQUAL)
    @Excel(name = "启用或关闭")
    private String status;


}
