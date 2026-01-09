package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 点赞记录
 * </p>
 *
 * @author leizhi
 * @since 2021-03-03
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_reply_like_log")
@ApiModel(value = "ReplyLikeLog", description = "点赞记录")
@AllArgsConstructor
public class ReplyLikeLog extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞人id
     */
    @ApiModelProperty(value = "点赞人id")
    @NotNull(message = "点赞人id不能为空")
    @TableField("user_id")
    @Excel(name = "点赞人id")
    private Long userId;


    @ApiModelProperty(value = "角色")
    @NotNull(message = "角色不能为空")
    @TableField("role_type")
    @Excel(name = "角色")
    private String roleType;

    /**
     * 评论id
     */
    @ApiModelProperty(value = "评论id")
    @NotNull(message = "评论id不能为空")
    @TableField("reply_id")
    @Excel(name = "评论id")
    private Long replyId;

    /**
     * 是否取消点赞：1未取消，2已取消
     */
    @ApiModelProperty(value = "是否取消点赞：1未取消，2已取消")
    @TableField("has_cancel")
    @Excel(name = "是否取消点赞：1未取消，2已取消")
    private Integer hasCancel;

    @Builder
    public ReplyLikeLog(Long id, LocalDateTime createTime, Long createUser,
                        Long userId, Long replyId, Integer hasCancel, String roleType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.userId = userId;
        this.replyId = replyId;
        this.hasCancel = hasCancel;
        this.roleType = roleType;
    }

}
