package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @since 2020-09-09
 */
@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_content_reply")
@ApiModel(value = "ContentReply", description = "内容留言")
@AllArgsConstructor
public class ContentReply extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    @Length(max = 32, message = "父ID长度不能超过32")
    @TableField(value = "c_parent_comment_id", condition = LIKE)
    @Excel(name = "父ID")
    private String parentCommentId;

    /**
     * 留言人
     */
    @ApiModelProperty(value = "留言人")
    @Length(max = 32, message = "留言人长度不能超过32")
    @TableField(value = "c_replier_id")
    @Excel(name = "留言人")
    private Long replierId;

    @ApiModelProperty(value = "角色")
    @NotNull(message = "角色不能为空")
    @TableField("role_type")
    @Excel(name = "角色")
    private String roleType;
    /**
     * 文章Id
     */
    @ApiModelProperty(value = "文章Id")
    @Length(max = 32, message = "文章Id长度不能超过32")
    @TableField(value = "c_content_id")
    @Excel(name = "文章Id")
    private Long contentId;

    /**
     * 留言内容
     */
    @ApiModelProperty(value = "留言内容")
    @Length(max = 2000, message = "留言内容长度不能超过2000")
    @TableField(value = "c_content", condition = LIKE)
    @Excel(name = "留言内容")
    private String content;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    @TableField("n_audit_status")
    @Excel(name = "审核状态：1通过，2不通过")
    private Integer auditStatus;

    /**
     * 点赞量
     */
    @ApiModelProperty(value = "点赞量")
    @TableField("like_count")
    @Excel(name = "点赞量")
    private Integer likeCount;


    /**
     * 微信昵称
     */
    @ApiModelProperty(value = "微信昵称")
    @Length(max = 40, message = "微信昵称长度不能超过40")
    @TableField(value = "c_replier_wx_name", condition = LIKE)
    @Excel(name = "微信昵称")
    private String replierWxName;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Length(max = 255, message = "头像长度不能超过255")
    @TableField(value = "c_replier_avatar", condition = LIKE)
    @Excel(name = "头像")
    private String replierAvatar;

    @ApiModelProperty(value = "是否点赞")
    @TableField(exist = false)
    private Boolean hasLike;


    @ApiModelProperty(value = "评论所在的内容")
    @TableField(exist = false)
    private ChannelContent channelContent;
}
