package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName ConsultationGroupMemberDto
 * @Description
 * @Author yangShuai
 * @Date 2021/6/4 10:45
 * @Version 1.0
 */
@Data
public class ConsultationGroupMemberDto {


    @ApiModelProperty(value = "会诊组ID")
    @NotNull
    private Long consultationGroupId;

    @ApiModelProperty(value = "用户手机号")
    private String mobile;

    @ApiModelProperty(value = "成员的openId")
    private String memberOpenId;

    @ApiModelProperty(value = "成员头像")
    @Length(max = 500, message = "成员头像长度不能超过20")
    @TableField(value = "member_avatar", condition = LIKE)
    @Excel(name = "成员头像")
    private String memberAvatar;

    @ApiModelProperty(value = "成员姓名")
    @Length(max = 255, message = "成员姓名长度不能超过20")
    @TableField(value = "member_name", condition = LIKE)
    @Excel(name = "成员姓名")
    private String memberName;

    @ApiModelProperty(value = "成员角色")
    @Length(max = 255, message = "成员角色长度不能超过20")
    private String memberRole;

    @ApiModelProperty(value = "成员角色备注")
    @Length(max = 255, message = "成员角色备注长度不能超过20")
    private String memberRoleRemarks;



}
