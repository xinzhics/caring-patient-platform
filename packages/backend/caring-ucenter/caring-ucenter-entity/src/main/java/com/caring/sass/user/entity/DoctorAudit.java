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
 * @ClassName DoctorAudit
 * @Description
 * @Author yangShuai
 * @Date 2022/2/22 11:13
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_doctor_audit")
@ApiModel(value = "DoctorAudit", description = "医生审核表")
@AllArgsConstructor
public class DoctorAudit extends Entity<Long> {

    @ApiModelProperty(value = "头像")
    @TableField(value = "avatar", condition = LIKE)
    private String avatar;

    @ApiModelProperty(value = "医生名字")
    @Length(max = 32, message = "医生名字长度不能超过32")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "医生名字")
    private String name;

    @ApiModelProperty(value = "微信昵称")
    @Length(max = 50, message = "微信昵称长度不能超过32")
    @TableField(value = "nick_name", condition = LIKE)
    @Excel(name = "微信昵称")
    private String nickName;

    @ApiModelProperty(value = "openId")
    @TableField(value = "open_id", condition = EQUAL)
    private String openId;

    @ApiModelProperty(value = "wxAppId")
    @TableField(value = "wx_app_id", condition = EQUAL)
    private String wxAppId;

    @ApiModelProperty(value = "医助ID")
    @TableField(value = "nursing_id", condition = EQUAL)
    @Excel(name = "医助ID")
    private Long nursingId;

    @ApiModelProperty(value = "医助")
    @Length(max = 100, message = "医助长度不能超过100")
    @TableField(value = "nursing_name", condition = LIKE)
    @Excel(name = "医助")
    private String nursingName;

    @ApiModelProperty(value = "密码")
    @TableField(value = "password", condition = EQUAL)
    private String password;

    @ApiModelProperty(value = "手机号码")
    @Length(max = 12, message = "手机号码长度不能超过12")
    @TableField(value = "mobile", condition = LIKE)
    @Excel(name = "手机号码")
    private String mobile;
    /**
     * 医院
     */
    @ApiModelProperty(value = "医院")
    @TableField("hospital_id")
    @Excel(name = "医院")
    private Long hospitalId;


    @ApiModelProperty(value = "医院名称")
    @Length(max = 50, message = "医院名称长度不能超过50")
    @TableField(value = "hospital_name", condition = LIKE)
    @Excel(name = "医院名称")
    private String hospitalName;

    @ApiModelProperty(value = "科室名称")
    @Length(max = 100, message = "科室名称长度不能超过100")
    @TableField(value = "deptartment_name", condition = LIKE)
    @Excel(name = "科室名称")
    private String deptartmentName;

    @ApiModelProperty(value = "职称")
    @Length(max = 32, message = "职称长度不能超过32")
    @TableField(value = "title", condition = LIKE)
    @Excel(name = "职称")
    private String title;

    @ApiModelProperty(value = "专业特长")
    @TableField("specialties")
    private String specialties;

    @ApiModelProperty(value = "详细介绍")
    @TableField("introduction")
    private String introduction;

    @ApiModelProperty(value = "审核排序, 已审核 1，未审核 0，审核失败 2")
    @TableField("audit_sort")
    private Integer auditSort;

    @ApiModelProperty(value = "审核状态, apply 申请, pass_through 通过， reject 拒绝")
    @TableField("audit_status")
    private String auditStatus;


}
