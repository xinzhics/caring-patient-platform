package com.caring.sass.ai.dto.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.ai.entity.article.ArticleNoticeType;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 科普用户系统通知
 * </p>
 *
 * @author 杨帅
 * @since 2025-03-26
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "ArticleUserNoticePageDTO", description = "科普用户系统通知")
@AllArgsConstructor
public class ArticleUserNoticePageDTO {

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
    private ArticleNoticeType noticeType;




}
