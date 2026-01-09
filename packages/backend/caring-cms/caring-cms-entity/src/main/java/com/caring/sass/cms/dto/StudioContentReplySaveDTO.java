package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "StudioContentReplySaveDTO", description = "医生cms留言表")
public class StudioContentReplySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    private Long cParentCommentId;
    /**
     * 留言人
     */
    @ApiModelProperty(value = "留言人")
    @NotNull(message = "留言人不能为空")
    private Long cReplierId;
    /**
     * 文章Id
     */
    @ApiModelProperty(value = "文章Id")
    @NotNull(message = "文章Id不能为空")
    private Long cCmsId;


    @ApiModelProperty(value = "评论内容")
    @Length(max = 2000, message = "长度不能超过2000")
    private String cContent;
    /**
     * 审核状态：1通过，2不通过
     */
    @ApiModelProperty(value = "审核状态：1通过，2不通过")
    @NotNull(message = "审核状态：1通过，2不通过不能为空")
    private Integer auditStatus;

    /**
     * 微信昵称
     */
    @ApiModelProperty(value = "评论人昵称")
    private String replierName;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String replierAvatar;


}
