package com.caring.sass.ai.entity.textual;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.article.ArticleNoticeType;
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
 * 文献解读系统通知
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_textual_interpretation_user_notice")
@ApiModel(value = "TextualInterpretationUserNotice", description = "文献解读系统通知")
@AllArgsConstructor
public class TextualInterpretationUserNotice extends Entity<Long> {

    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull
    private Long userId;

    /**
     * 是否已读
     */
    @ApiModelProperty(value = "是否已读 1 已读， 0 未读")
    private Integer readStatus = 0;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    @Length(max = 255, message = "消息类型长度不能超过255")
    @TableField(value = "notice_type", condition = LIKE)
    @Excel(name = "消息类型")
    private TextualNoticeType noticeType;


    /**
     * 通知内容
     */
    @ApiModelProperty(value = "通知内容")
    @Length(max = 255, message = "通知内容长度不能超过255")
    @TableField(value = "notice_content", condition = LIKE)
    @Excel(name = "通知内容")
    private String noticeContent;




}
