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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 小组
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_group")
@ApiModel(value = "Group", description = "小组")
@AllArgsConstructor
public class Group extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @TableField(value = "nurse_id", condition = EQUAL)
    @Excel(name = "医助ID")
    private Long nurseId;

    /**
     * 医助名字
     */
    @ApiModelProperty(value = "医助名字")
    @Length(max = 20, message = "医助名字长度不能超过20")
    @TableField(value = "nurse_name", condition = LIKE)
    @Excel(name = "医助名字")
    private String nurseName;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Length(max = 500, message = "图标长度不能超过500")
    @TableField(value = "icon", condition = LIKE)
    @Excel(name = "图标")
    private String icon;

    /**
     * 小组名字
     */
    @ApiModelProperty(value = "小组名字")
    @Length(max = 32, message = "小组名字长度不能超过32")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "小组名字")
    private String name;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("order_")
    @Excel(name = "排序")
    private Integer order;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    @TableField("org_id")
    @Excel(name = "机构ID")
    private Long organId;

    /**
     * 机构名字
     */
    @ApiModelProperty(value = "机构名字")
    @Length(max = 50, message = "机构名字长度不能超过50")
    @TableField(value = "organ_name", condition = LIKE)
    @Excel(name = "机构名字")
    private String organName;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    @Length(max = 32, message = "联系人长度不能超过32")
    @TableField(value = "contact_name", condition = LIKE)
    @Excel(name = "联系人")
    private String contactName;

    /**
     * 联系人电话
     */
    @ApiModelProperty(value = "联系人电话")
    @Length(max = 12, message = "联系人电话长度不能超过12")
    @TableField(value = "contact_mobile", condition = LIKE)
    @Excel(name = "联系人电话")
    private String contactMobile;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    @Length(max = 500, message = "地址长度不能超过500")
    @TableField(value = "address", condition = LIKE)
    @Excel(name = "地址")
    private String address;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 500, message = "备注长度不能超过500")
    @TableField(value = "remarks", condition = LIKE)
    @Excel(name = "备注")
    private String remarks;

    /**
     * 机构CLASSCODE
     */
    @ApiModelProperty(value = "权限代码")
    @Length(max = 65535, message = "权限代码长度不能超过65535")
    @TableField("class_code")
    @Excel(name = "权限代码")
    private String classCode;

    @ApiModelProperty(value = "会员人数")
    @TableField(exist = false)
    private long patientCount;

    @ApiModelProperty(value = "医生人数")
    @TableField(exist = false)
    private long doctorCount;

}
