package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 常用语模板分类
 * </p>
 *
 * @author 杨帅
 * @since 2023-05-08
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_common_msg_template_content")
@ApiModel(value = "CommonMsgTemplateContent", description = "常用语模板分类")
@AllArgsConstructor
public class CommonMsgTemplateContent extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型的ID")
    @NotNull(message = "不能为空")
    @TableField("template_type_id")
    private Long templateTypeId;

    /**
     * 模板内容
     */
    @ApiModelProperty(value = "模板内容")
    @Length(max = 800, message = "模板内容长度不能超过800")
    @TableField(value = "template_content", condition = LIKE)
    @Excel(name = "模板内容")
    private String templateContent;

    /**
     * 模板标题
     */
    @ApiModelProperty(value = "模板标题")
    @TableField("template_title")
    @Excel(name = "模板标题")
    private String templateTitle;

    /**
     * 用户类型 NursingStaff, doctor
     */
    @ApiModelProperty(value = "用户类型 NursingStaff, doctor")
    @Length(max = 255, message = "用户类型 NursingStaff, doctor长度不能超过255")
    @TableField(value = "user_type", condition = EQUAL)
    @Excel(name = "用户类型 NursingStaff, doctor")
    private String userType;

    /**
     * 模板发布（0未发布， 1发布）
     */
    @ApiModelProperty(value = "模板发布（0未发布， 1发布）")
    @TableField("template_release")
    @Excel(name = "模板发布（0未发布， 1发布）")
    private Integer templateRelease;


    @ApiModelProperty(value = "常用语已经存在 1 表示此模版常用语已经在 用户的常用语中")
    @TableField(exist = false)
    private Integer existed = 0;


    @Builder
    public CommonMsgTemplateContent(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long templateTypeId, String templateContent, String templateTitle, String userType, Integer templateRelease) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.templateTypeId = templateTypeId;
        this.templateContent = templateContent;
        this.templateTitle = templateTitle;
        this.userType = userType;
        this.templateRelease = templateRelease;
    }

}
