package com.caring.sass.ai.dto.userbiz;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * 用于接收AI用户相关的所有数据参数。
 */
@Data
public class AiKnowUserSaveBo {

    @ApiModelProperty(value = "AI工作室租户")
    private String tenantId;

    @ApiModelProperty(value = "AI工作室医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "数字头像")
    @TableField(value = "user_avatar", condition = LIKE)
    private String userAvatar;

    @ApiModelProperty(value = "用户名称 科普名称的医生名称")
    @TableField(value = "user_name", condition = LIKE)
    @Excel(name = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户工作单位 医生医院")
    @Length(max = 400, message = "用户工作单位长度不能超过400")
    private String workUnit;

    @ApiModelProperty(value = "科室")
    @Length(max = 300, message = "科室长度不能超过300")
    private String department;

    @ApiModelProperty(value = "职称")
    @Length(max = 300, message = "用户工作单位长度不能超过300")
    private String doctorTitle;

    @ApiModelProperty(value = "个人简介")
    @Length(max = 1000, message = "个人简介长度不能超过1000")
    private String personalProfile;

    @ApiModelProperty(value = "擅长")
    @Length(max = 1000, message = "擅长长度不能超过1000")
    private String specialty;


    @ApiModelProperty(value = "AI交互试听音频连接")
    private String aiInteractiveAudioTrialListening;


    @ApiModelProperty(value = "打招呼视频")
    private String greetingVideo;


    @ApiModelProperty(value = "打招呼视频封面")
    private String greetingVideoCover;


    @ApiModelProperty(value = "真人头像")
    @TableField(value = "real_human_avatar", condition = EQUAL)
    private String realHumanAvatar;

    @ApiModelProperty(value = "总平台分类")
    private String domain1Name;

    @ApiModelProperty(value = "子平台域名")
    private String menuDomainName;

    @ApiModelProperty(value = "子平台名称")
    private String domain2;

    @ApiModelProperty(value = "子平台分类")
    private String domain2Name;


    @ApiModelProperty(value = "是否开通知识库用户")
    private String openDocuknow;

    @ApiModelProperty(value = "是否开通科普创作")
    private String openArticle;

    @ApiModelProperty(value = "是否展示到聚合页")
    private String showInDocuknow;


    @ApiModelProperty(value = "数据来源 ai工作室， saibolan ")
    private String dataSource = "ai_studio";

    @ApiModelProperty(value = "赛柏蓝平台博主ID")
    private Long childUserId;


    // 科普名片小程序的
//    @ApiModelProperty(value = "擅长(300字内)")
//    @Length(max = 300, message = "擅长(300字内)长度不能超过300")
//    private String doctorBeGoodAt;


    // AI 工作室的
//
//    @ApiModelProperty(value = "AI工作室-擅长标签")
//    private String specialtyLabel;
//
//    @ApiModelProperty(value = "AI工作室-执业证书号")
//    private String licenseNumber;
//
//    @ApiModelProperty(value = "AI工作室-专业背景")
//    private String professionalBackground;
//
//
//    @ApiModelProperty(value = "AI工作室-教育经历")
//    private String educationExperience;
//
//    @ApiModelProperty(value = "AI工作室-研究方向")
//    private String researchDirection;
//
//
//    @ApiModelProperty(value = "AI工作室-发表论文")
//    private String publishedPapers;
//
//
//    @ApiModelProperty(value = "AI工作室-专长领域详细描述")
//    private String specialtyDetails;
//
//    @ApiModelProperty(value = "AI工作室-诊所详细地址")
//    private String clinicAddress;
//
//    @ApiModelProperty(value = "AI工作室-诊所联系方式")
//    private String clinicContact;
//
//    @ApiModelProperty(value = "AI工作室-诊所地图链接")
//    private String mapLink;
//
//    @ApiModelProperty(value = "AI工作室-自我评价")
//    private String selfEvaluation;



}
