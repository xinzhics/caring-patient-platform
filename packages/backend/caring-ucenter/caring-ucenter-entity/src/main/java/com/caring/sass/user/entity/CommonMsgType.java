package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @className: CommonMsgType
 * @author: 杨帅
 * @date: 2024/1/17
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_common_msg_type")
@ApiModel(value = "CommonMsgType", description = "常用语分类")
@AllArgsConstructor
public class CommonMsgType extends Entity<Long> {

    @ApiModelProperty(value = "常用语分类")
    @TableField(value = "title", condition = LIKE)
    @Excel(name = "常用语分类")
    private String title;

    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;

    @ApiModelProperty(value = "用户类型 NursingStaff, doctor")
    @TableField(value = "user_type", condition = EQUAL)
    @Excel(name = "用户类型")
    private String userType;

    @ApiModelProperty(value = "1： 来自模版 0： 自己的分类")
    @TableField(exist = false)
    private Integer formTemplate = 0;


}
