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

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName CustomGrouping
 * @Description
 * @Author yangShuai
 * @Date 2021/8/27 11:10
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_custom_grouping")
@ApiModel(value = "CustomGrouping", description = "自定义分组")
@AllArgsConstructor
public class CustomGrouping extends Entity<Long> {


    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户Id")
    @TableField(value = "user_id", condition = EQUAL)
    @Excel(name = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "小组名字")
    @Length(max = 200, message = "小组名字长度不能超过32")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "小组名字")
    private String name;

    @ApiModelProperty(value = "角色(doctor, NursingStaff)")
    @Length(max = 100, message = "用户的角色")
    @TableField(value = "role_type", condition = EQUAL)
    private String roleType;

    @ApiModelProperty(value = "排序 默认0 (从大到小排)")
    @TableField("group_sort")
    @Excel(name = "排序")
    private Integer groupSort;

    @ApiModelProperty(value = "会员人数")
    @TableField(exist = false)
    private long patientCount;

    @ApiModelProperty(value = "患者ID")
    @TableField(exist = false)
    private List<Long> patientIds;

    @ApiModelProperty(value = "患者ID(英文 , 分隔)")
    @TableField(exist = false)
    private String patientIdStrings;

}
