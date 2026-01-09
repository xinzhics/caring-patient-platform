package com.caring.sass.cms.dto;

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
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
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
@ApiModel(value = "StudioContentReplyUpdateDTO", description = "医生cms留言表")
public class StudioContentReplyUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
    @ApiModelProperty(value = "")
    @Length(max = 2000, message = "长度不能超过2000")
    private String cContent;
    /**
     * 审核状态：1通过，2不通过
     */
    @ApiModelProperty(value = "审核状态：1通过，2不通过")
    @NotNull(message = "审核状态：1通过，2不通过不能为空")
    private Integer auditStatus;
    /**
     * 点赞量
     */
    @ApiModelProperty(value = "点赞量")
    private Long likeCount;
}
