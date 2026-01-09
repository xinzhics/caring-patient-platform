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
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 关键字设置
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
@TableName("u_user_keyword_setting")
@ApiModel(value = "KeywordSetting", description = "关键字设置")
@AllArgsConstructor
public class KeywordSetting extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    @Length(max = 30, message = "关键字长度不能超过30")
    @TableField(value = "keyword_name", condition = LIKE)
    @Excel(name = "关键字")
    private String keywordName;

    /**
     * 匹配类型(full_match: 全匹配， semi_match 半匹配)
     */
    @ApiModelProperty(value = "匹配类型(full_match: 全匹配， semi_match 半匹配)")
    @Length(max = 255, message = "匹配类型(full_match: 全匹配， semi_match 半匹配)长度不能超过255")
    @TableField(value = "match_type", condition = LIKE)
    @Excel(name = "匹配类型(full_match: 全匹配， semi_match 半匹配)")
    private String matchType;

    /**
     * 关键字规则id
     */
    @ApiModelProperty(value = "关键字规则id")
    @TableField("keyword_reply_id")
    @Excel(name = "关键字规则id")
    private Long keywordReplyId;

    @ApiModelProperty(value = "租户")
    @TableField("tenant_code")
    private String tenantCode;

}
