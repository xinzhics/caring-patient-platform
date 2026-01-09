package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "DoctorAuditUpdateDTO", description = "医生审核表")
@AllArgsConstructor
public class DoctorAuditUpdateDTO {

    @ApiModelProperty(value = "id")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "医生名字")
    @Length(max = 32, message = "医生名字长度不能超过32")
    @Excel(name = "医生名字")
    private String name;

    @ApiModelProperty(value = "微信昵称")
    @Length(max = 50, message = "微信昵称长度不能超过32")
    @Excel(name = "微信昵称")
    private String nickName;

    @ApiModelProperty(value = "openId")
    private String openId;

    @ApiModelProperty(value = "wxAppId")
    private String wxAppId;

    @ApiModelProperty(value = "医助ID")
    @Excel(name = "医助ID")
    private Long nursingId;

    @ApiModelProperty(value = "医助")
    @Length(max = 100, message = "医助长度不能超过100")
    @Excel(name = "医助")
    private String nursingName;

    @ApiModelProperty(value = "手机号码")
    @Length(max = 12, message = "手机号码长度不能超过12")
    @Excel(name = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "医院")
    private Long hospitalId;

    @ApiModelProperty(value = "医院名称")
    @Length(max = 50, message = "医院名称长度不能超过50")
    @Excel(name = "医院名称")
    private String hospitalName;

    @ApiModelProperty(value = "科室名称")
    @Length(max = 100, message = "科室名称长度不能超过100")
    @Excel(name = "科室名称")
    private String deptartmentName;

    @ApiModelProperty(value = "职称")
    @Length(max = 32, message = "职称长度不能超过32")
    @Excel(name = "职称")
    private String title;

    @ApiModelProperty(value = "专业特长")
    private String specialties;

    @ApiModelProperty(value = "详细介绍")
    private String introduction;

    @ApiModelProperty(value = "审核状态, apply 申请, pass_through 通过， reject 拒绝")
    private String auditStatus;


}
