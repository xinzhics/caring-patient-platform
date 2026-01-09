package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * AI名片
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-10
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_business_card",  autoResultMap = true)
@ApiModel(value = "BusinessCard", description = "AI名片")
@AllArgsConstructor
public class BusinessCard extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 300, message = "用户手机号长度不能超过300")
    @Excel(name = "用户手机号")
    @TableField(value = "phone", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    private String phone;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名")
    @Length(max = 100, message = "医生姓名长度不能超过100")
    @TableField(value = "doctor_name", condition = LIKE)
    @Excel(name = "医生姓名")
    private String doctorName;


    @ApiModelProperty(value = "会员版本， 默认基础版")
    @TableField(value = "member_version", condition = EQUAL)
    private BusinessCardMemberVersionEnum memberVersion;


    @ApiModelProperty(value = "名片点赞数量")
    @TableField("give_thumbs_up_count")
    private Integer giveThumbsUpCount;


    @ApiModelProperty(value = "浏览量")
    @TableField("number_of_views")
    private Integer numberOfViews;

    /**
     * 医生职称
     */
    @ApiModelProperty(value = "医生职称")
    @Length(max = 100, message = "医生职称长度不能超过100")
    @TableField(value = "doctor_title", condition = LIKE)
    @Excel(name = "医生职称")
    private String doctorTitle;

    /**
     * 医生科室
     */
    @ApiModelProperty(value = "医生科室")
    @Length(max = 200, message = "医生科室长度不能超过200")
    @TableField(value = "doctor_department", condition = LIKE)
    @Excel(name = "医生科室")
    private String doctorDepartment;

    /**
     * 医生医院
     */
    @ApiModelProperty(value = "医生医院")
    @Length(max = 300, message = "医生医院长度不能超过300")
    @TableField(value = "doctor_hospital", condition = LIKE)
    @Excel(name = "医生医院")
    private String doctorHospital;

    /**
     * 擅长(60字内)
     */
    @ApiModelProperty(value = "擅长(60字内)")
    @Length(max = 300, message = "擅长(60字内)长度不能超过300")
    @TableField(value = "doctor_be_good_at", condition = LIKE)
    @Excel(name = "擅长(60字内)")
    private String doctorBeGoodAt;

    /**
     * 个人介绍(120字)
     */
    @ApiModelProperty(value = "个人介绍(100字)")
    @Length(max = 1000, message = "个人介绍(1000字)长度不能超过100")
    @TableField(value = "doctor_personal", condition = LIKE)
    private String doctorPersonal;

    /**
     * 医生数字人
     */
    @ApiModelProperty(value = "医生数字人")
    @Length(max = 300, message = "医生数字人长度不能超过300")
    @TableField(value = "doctor_meta_human", condition = LIKE)
    @Excel(name = "医生数字人")
    private String doctorMetaHuman;

    /**
     * 工作室
     */
    @ApiModelProperty(value = "工作室")
    @Length(max = 300, message = "工作室长度不能超过300")
    @TableField(value = "doctor_studio", condition = LIKE)
    @Excel(name = "工作室")
    private String doctorStudio;


    /**
     * AI对话地址
     */
    @ApiModelProperty(value = "AI对话地址")
    @Length(max = 300, message = "AI对话地址长度不能超过300")
    @TableField(value = "doctor_AI_dialogue", condition = LIKE)
    @Excel(name = "AI对话地址")
    private String doctorAiDialogue;

    /**
     * 数字人封面
     */
    @ApiModelProperty(value = "数字人封面")
    @Length(max = 255, message = "数字人封面长度不能超过255")
    @TableField(value = "doctor_meta_human_poster", condition = LIKE)
    @Excel(name = "数字人封面")
    private String doctorMetaHumanPoster;

    /**
     * 医生头像
     */
    @ApiModelProperty(value = "医生头像")
    @Length(max = 255, message = "医生头像长度不能超过255")
    @TableField(value = "doctor_avatar", condition = LIKE)
    @Excel(name = "医生头像")
    private String doctorAvatar;

    /**
     * 0 通用， 1定制
     */
    @ApiModelProperty(value = "0 通用， 1定制")
    @TableField("doctor_ai_type")
    @Excel(name = "0 通用， 1定制")
    private Integer doctorAiType;

    /**
     * 小程序用户ID
     */
    @ApiModelProperty(value = "小程序用户ID")
    @TableField("user_id")
    @Excel(name = "小程序用户ID")
    private Long userId;

    @ApiModelProperty(value = "机构id")
    @TableField("organ_id")
    private Long organId;

    @ApiModelProperty("有系统医生工作室(内置才有) 0 没有， 1 有")
    @TableField("has_doctor_studio")
    private Integer hasDoctorStudio;

    @ApiModelProperty("编辑或创建入口隐藏")
    @TableField("create_button_status")
    private Integer createButtonStatus;

    @ApiModelProperty("小程序二维码")
    @TableField("mini_or_code")
    private String miniQrCode;


    @ApiModelProperty("小程序分享二维码")
    @TableField("business_or_code")
    private String businessQrCode;


    @ApiModelProperty("名片激活时间")
    @TableField("activation_time")
    private LocalDateTime activationTime;


    @ApiModelProperty("开通视频号")
    @TableField("open_video_account")
    private Boolean openVideoAccount;

    @ApiModelProperty("视频号ID：以“sph”开头的id，可在视频号助手获取")
    @TableField("finder_user_name")
    private String finderUserName;

    @ApiModelProperty("视频 feedId")
    @TableField("feed_id")
    private String feedId;

    @ApiModelProperty("开启联系我")
    @TableField("open_contact_me")
    private Boolean openContactMe;


    @ApiModelProperty("联系我名片")
    @TableField("contact_img_url")
    private String contactImgUrl;

    @ApiModelProperty(value = "工作室列表")
    @TableField(exist = false)
    @Deprecated
    private List<String> doctorStudioList;


    @ApiModelProperty(value = "工作室列表")
    @TableField(exist = false)
    List<BusinessCardStudio> studios;

    @ApiModelProperty(value = "重置名片二维码")
    @TableField(exist = false)
    private Boolean resetBusinessQrCode;


    @ApiModelProperty("AI对话秘钥")
    @TableField("doctor_ai_dialogue_key")
    private String doctorAiDialogueKey;

    @ApiModelProperty("对话问题，json列表")
    @TableField("doctor_ai_dialogue_question")
    private String doctorAiDialogueQuestion;
}
