package com.caring.sass.ai.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.ai.entity.card.BusinessCardStudio;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessCardUpdateDTO", description = "AI名片")
public class BusinessCardUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 医生ID
     */
//    @ApiModelProperty(value = "医生ID")
//    private Long doctorId;
    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名")
    @Length(max = 100, message = "医生姓名长度不能超过100")
    private String doctorName;
    /**
     * 医生职称
     */
    @ApiModelProperty(value = "医生职称")
    @Length(max = 100, message = "医生职称长度不能超过100")
    private String doctorTitle;
    /**
     * 医生科室
     */
    @ApiModelProperty(value = "医生科室")
    @Length(max = 200, message = "医生科室长度不能超过200")
    private String doctorDepartment;
    /**
     * 医生医院
     */
    @ApiModelProperty(value = "医生医院")
    @Length(max = 300, message = "医生医院长度不能超过300")
    private String doctorHospital;
    /**
     * 擅长(60字内)
     */
    @ApiModelProperty(value = "擅长(300字内)")
    @Length(max = 300, message = "擅长(300字内)长度不能超过300")
    private String doctorBeGoodAt;
    /**
     * 个人介绍(120字)
     */
    @ApiModelProperty(value = "个人介绍(1000字)")
    @Length(max = 1000, message = "个人介绍(1000字)长度不能超过1000")
    private String doctorPersonal;
    /**
     * 医生数字人
     */
    @ApiModelProperty(value = "医生数字人")
    @Length(max = 300, message = "医生数字人长度不能超过300")
    private String doctorMetaHuman;
    /**
     * 工作室
     */
    @ApiModelProperty(value = "工作室")
    @Length(max = 300, message = "工作室长度不能超过300")
    private String doctorStudio;

    @ApiModelProperty(value = "工作室多个二维码")
    @Deprecated
    private List<String> doctorStudioList;

    /**
     * AI对话地址
     */
    @ApiModelProperty(value = "AI对话地址")
    @Length(max = 300, message = "AI对话地址长度不能超过300")
    private String doctorAiDialogue;
    /**
     * 数字人封面
     */
    @ApiModelProperty(value = "数字人封面")
    @Length(max = 255, message = "数字人封面长度不能超过255")
    private String doctorMetaHumanPoster;
    /**
     * 医生头像
     */
    @ApiModelProperty(value = "医生头像")
    @Length(max = 255, message = "医生头像长度不能超过255")
    private String doctorAvatar;
    /**
     * 0 通用， 1定制
     */
    @ApiModelProperty(value = "0 通用， 1定制")
    private Integer doctorAiType;

    @ApiModelProperty(value = "机构id")
    private Long organId;

    @ApiModelProperty("编辑或创建入口隐藏")
    private Integer createButtonStatus;


    @ApiModelProperty("开通视频号")
    private Boolean openVideoAccount;

    @ApiModelProperty("视频号ID：以“sph”开头的id，可在视频号助手获取")
    private String finderUserName;

    @ApiModelProperty("视频 feedId")
    private String feedId;

    @ApiModelProperty("开启联系我")
    private Boolean openContactMe;


    @ApiModelProperty("开启联系我")
    private String contactImgUrl;

    @ApiModelProperty(value = "工作室列表")
    @TableField(exist = false)
    List<BusinessCardStudio> studios;

    @ApiModelProperty("AI对话秘钥")
    private String doctorAiDialogueKey;

    @ApiModelProperty("对话问题，json列表")
    private String doctorAiDialogueQuestion;
}
