package com.caring.sass.cms.dto;

import java.time.LocalDateTime;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @since 2020-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ContentReplyPageDTO", description = "内容留言")
public class ContentReplyPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 留言人
     */
    @ApiModelProperty(value = "留言人", required = true)
    @Length(max = 32, message = "留言人长度不能超过32")
    private String replierId;


    @ApiModelProperty(value = "角色")
    @Excel(name = "角色")
    private String roleType;

    /**
     * 文章Id
     */
    @ApiModelProperty(value = "文章Id", required = true)
    @Length(max = 32, message = "文章Id长度不能超过32")
    private String contentId;
    /**
     * 留言内容
     */
    @ApiModelProperty(value = "留言内容")
    @Length(max = 2000, message = "留言内容长度不能超过2000")
    private String content;
    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private Integer auditStatus;
    /**
     * 微信昵称
     */
    @ApiModelProperty(value = "微信昵称")
    @Length(max = 40, message = "微信昵称长度不能超过40")
    private String replierWxName;
    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Length(max = 255, message = "头像长度不能超过255")
    private String replierAvatar;

}
