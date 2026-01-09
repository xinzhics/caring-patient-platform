package com.caring.sass.cms.entity;

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
 * 医生cms留言表
 * </p>
 *
 * @author 杨帅
 * @since 2025-11-11
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_studio_content_reply")
@ApiModel(value = "StudioContentReply", description = "医生cms留言表")
@AllArgsConstructor
public class StudioContentReply extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    @TableField("c_parent_comment_id")
    @Excel(name = "父ID")
    private Long cParentCommentId;

    /**
     * 留言人
     */
    @ApiModelProperty(value = "留言人")
    @NotNull(message = "留言人不能为空")
    @TableField("c_replier_id")
    @Excel(name = "留言人")
    private Long cReplierId;

    /**
     * 文章Id
     */
    @ApiModelProperty(value = "文章Id")
    @NotNull(message = "文章Id不能为空")
    @TableField("c_cms_id")
    @Excel(name = "文章Id")
    private Long cCmsId;

    @ApiModelProperty(value = "评论内容")
    @Length(max = 2000, message = "长度不能超过2000")
    @TableField(value = "c_content", condition = LIKE)
    private String cContent;

    /**
     * 审核状态：1通过，2不通过
     */
    @ApiModelProperty(value = "审核状态：1通过，2不通过")
    @NotNull(message = "审核状态：1通过，2不通过不能为空")
    @TableField("n_audit_status")
    @Excel(name = "审核状态：1通过，2不通过")
    private Integer auditStatus;

    /**
     * 点赞量
     */
    @ApiModelProperty(value = "点赞量")
    @TableField("like_count")
    @Excel(name = "点赞量")
    private Long likeCount;

    /**
     * 微信昵称
     */
    @ApiModelProperty(value = "评论人昵称")
    @TableField(value = "replier_name", condition = LIKE)
    private String replierName;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @TableField(value = "replier_avatar", condition = LIKE)
    private String replierAvatar;


    @ApiModelProperty(value = "文章信息")
    @TableField(exist = false)
    private StudioCms studioCms;


    @Builder
    public StudioContentReply(Long id, LocalDateTime createTime, Long createUser, 
                    Long cParentCommentId, Long cReplierId, Long cCmsId, String cContent, Integer auditStatus, Long likeCount) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.cParentCommentId = cParentCommentId;
        this.cReplierId = cReplierId;
        this.cCmsId = cCmsId;
        this.cContent = cContent;
        this.auditStatus = auditStatus;
        this.likeCount = likeCount;
    }


}
