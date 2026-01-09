package com.caring.sass.ai.entity.chat;

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
 * 敏智用户历史对话
 * </p>
 *
 * @author 杨帅
 * @since 2024-03-28
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_miniapp_user_chat_history_group")
@ApiModel(value = "MiniAppUserChatHistoryGroup", description = "敏智用户历史对话")
@AllArgsConstructor
public class MiniAppUserChatHistoryGroup extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 发送者ID
     */
    @ApiModelProperty(value = "发送者ID")
    @TableField("sender_id")
    @Excel(name = "发送者ID")
    private Long senderId;

    /**
     * 登录的用户类型(miniAppUser, doctor)
     */
    @ApiModelProperty(value = "登录的用户类型(miniAppUser, doctor)")
    @Length(max = 50, message = "登录的用户类型(miniAppUser, doctor)长度不能超过50")
    @TableField(value = "current_user_type", condition = LIKE)
    @Excel(name = "登录的用户类型(miniAppUser, doctor)")
    private String currentUserType;

    /**
     * 对话的历史标题
     */
    @ApiModelProperty(value = "对话的历史标题")

    @TableField("content_title")
    @Excel(name = "对话的历史标题")
    private String contentTitle;



}
