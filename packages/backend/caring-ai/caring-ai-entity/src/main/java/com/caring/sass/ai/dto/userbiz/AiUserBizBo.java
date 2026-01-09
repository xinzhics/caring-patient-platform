package com.caring.sass.ai.dto.userbiz;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.ai.entity.know.KnowledgeUserQualification;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * 用于接收AI用户相关的所有数据参数。
 */
@Data
public class AiUserBizBo {


    @ApiModelProperty(value = "数字分身平台用户ID")
    private Long userId;

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

    @ApiModelProperty(value = "ai工作室医生ID")
    private String aiStudioDoctorId;

    @ApiModelProperty(value = "打招呼视频")
    private String greetingVideo;


    @ApiModelProperty(value = "打招呼视频封面")
    private String greetingVideoCover;


    @ApiModelProperty(value = "真人头像")
    @TableField(value = "real_human_avatar", condition = EQUAL)
    private String realHumanAvatar;

    @ApiModelProperty(value = "所属菜单ID")
    private String knowledgeMenuId;


    @ApiModelProperty(value = "用户资质")
    @TableField(exist = false)
    private List<KnowledgeUserQualification> userQualification;

    @ApiModelProperty(value = "删除的用户资质Id")
    @TableField(exist = false)
    private List<Long> deleteUserQualificationIds;




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
