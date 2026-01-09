package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName CommonMsg
 * @Description
 * @Author yangShuai
 * @Date 2020/10/13 10:45
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_common_msg")
@ApiModel(value = "CommonMsg", description = "常用语")
@AllArgsConstructor
public class CommonMsg extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "来自哪个模版")
    @TableField(value = "source_template_id", condition = EQUAL)
    private Long sourceTemplateId;

    @ApiModelProperty(value = "分类")
    @TableField(value = "type_id", condition = EQUAL)
    private Long typeId;

    @ApiModelProperty(value = "分类名称")
    @TableField(exist = false)
    private String typeName;

    @ApiModelProperty(value = "分类来源：0 自己的分类，1 模版分类， 2 自定义分类名称")
    @TableField(exist = false)
    private Integer typeForm;

    @ApiModelProperty(value = "常用语标题")
    @TableField(value = "title", condition = LIKE)
    @Excel(name = "常用语标题")
    private String title;

    @ApiModelProperty(value = "常用语")
    @TableField(value = "content", condition = LIKE)
    @Excel(name = "常用语")
    private String content;

    @ApiModelProperty(value = "医助Id")
    @TableField(value = "account_id", condition = EQUAL)
    @Excel(name = "医助Id")
    private Long accountId;

    @ApiModelProperty(value = "用户类型 NursingStaff, doctor")
    @TableField(value = "user_type", condition = EQUAL)
    @Excel(name = "用户类型")
    private String userType;

    public String getUserType() {
        if (StringUtils.isEmpty(userType)) {
            return UserType.UCENTER_NURSING_STAFF;
        } else {
            return userType;
        }
    }

}
