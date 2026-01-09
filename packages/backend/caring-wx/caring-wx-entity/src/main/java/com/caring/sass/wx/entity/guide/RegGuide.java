package com.caring.sass.wx.entity.guide;

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
 * 微信注册引导
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_reg_guide")
@ApiModel(value = "RegGuide", description = "微信注册引导")
@AllArgsConstructor
public class RegGuide extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 入组引导
     */
    @ApiModelProperty(value = "入组引导")
    @Length(max = 100, message = "入组引导长度不能超过100")
    @TableField(value = "guide", condition = LIKE)
    @Excel(name = "入组引导")
    private String guide;

    /**
     * 入组描素
     */
    @ApiModelProperty(value = "入组描述")
    @Length(max = 100, message = "入组描述长度不能超过100")
    @TableField(value = "describe_", condition = LIKE)
    @Excel(name = "入组描述")
    private String describe;

    /**
     * 引导图片
     */
    @ApiModelProperty(value = "引导图片")
    @Length(max = 500, message = "引导图片长度不能超过500")
    @TableField(value = "icon", condition = LIKE)
    @Excel(name = "引导图片")
    private String icon;

    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    @Length(max = 200, message = "链接地址长度不能超过200")
    @TableField(value = "url", condition = LIKE)
    @Excel(name = "链接地址")
    private String url;

    /**
     * 删除标记(0：未删除)
     */
    @ApiModelProperty(value = "删除标记(0：未删除)")
    @TableField("del_flag")
    @Excel(name = "删除标记(0：未删除)")
    private Integer delFlag;

    /**
     * 是否添加项目介绍 0:添加  1：不添加
     */
    @ApiModelProperty(value = "是否添加项目介绍 0:添加  1：不添加")
    @TableField("enable_intro")
    @Excel(name = "是否添加项目介绍 0:添加  1：不添加")
    private Integer enableIntro;

    /**
     * 项目介绍url
     */
    @ApiModelProperty(value = "项目介绍url")
    @TableField("intro")
    @Excel(name = "项目介绍url")
    private String intro;

    /**
     * 协议
     */
    @ApiModelProperty(value = "协议")
    @Length(max = 65535, message = "协议长度不能超过65535")
    @TableField("agreement")
    @Excel(name = "协议")
    private String agreement;

    @ApiModelProperty(value = "医生协议")
    @Length(max = 65535, message = "协议长度不能超过65535")
    @TableField("doctor_agreement")
    private String doctorAgreement;

    /**
     * 护理目标
     */
    @Deprecated
    @ApiModelProperty(value = "护理目标")
    @Length(max = 500, message = "护理目标长度不能超过500")
    @TableField(value = "nursing_target", condition = LIKE)
    @Excel(name = "护理目标")
    private String nursingTarget;

    /**
     * 入组成功消息类型 0：图文消息   1：文字消息
     */
    @ApiModelProperty(value = "入组成功消息类型 0：图文消息   1：文字消息")
    @TableField("success_msg_type")
    @Excel(name = "入组成功消息类型 0：图文消息   1：文字消息")
    private Integer successMsgType;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    @Length(max = 500, message = "消息内容长度不能超过500")
    @TableField(value = "success_msg", condition = LIKE)
    @Excel(name = "消息内容")
    private String successMsg;

    /**
     * 是否显示医生和机构：0显示，1不显示
     */
    @ApiModelProperty(value = "是否显示医生和机构：0显示，1不显示")
    @TableField("has_show_doctor")
    @Excel(name = "是否显示医生和机构：0显示，1不显示")
    private Integer hasShowDoctor;

    /**
     * 是否显示机构名称：0显示，1不显示
     */
    @ApiModelProperty(value = "是否显示机构名称：0显示，1不显示")
    @TableField("has_show_org_name")
    @Excel(name = "是否显示机构名称：0显示，1不显示")
    private Integer hasShowOrgName;

    /**
     * 是否填充微信名：0：填充 1：不填充
     */
    @Deprecated
    @ApiModelProperty(value = "是否填充微信名：0：填充 1：不填充")
    @TableField("has_fill_wx_name")
    @Excel(name = "是否填充微信名：0：填充 1：不填充")
    private Integer hasFillWxName;

    /**
     * 是否在入组界面中是否显示推荐用药：0显示，1不显示
     */
    @ApiModelProperty(value = "是否在入组界面中是否显示推荐用药：0显示，1不显示")
    @TableField("has_show_recommend_drugs")
    @Excel(name = "是否在入组界面中是否显示推荐用药：0显示，1不显示")
    private Integer hasShowRecommendDrugs;

    /**
     * 是否在注册时填写用药信息：0填写，1不填写
     */
    @ApiModelProperty(value = "是否在注册时填写用药信息：0填写，1不填写")
    @TableField("has_fill_drugs")
    @Excel(name = "是否在注册时填写用药信息：0填写，1不填写")
    private Integer hasFillDrugs;

    /**
     * 表单历史记录功能： 0不记录， 1记录
     */
    @ApiModelProperty(value = "表单历史记录功能： 0不记录， 1记录")
    @TableField("form_history_record")
    @Excel(name = "表单历史记录功能： 0不记录， 1记录")
    private Integer formHistoryRecord;

    /**
     * 基本信息表单名称
     */
    @ApiModelProperty(value = "基本信息表单名称")
    @Length(max = 50, message = "基本信息表单名称长度不能超过50")
    @TableField(value = "base_info_name")
    @Excel(name = "基本信息表单名称")
    private String baseInfoName;

    /**
     * 疾病信息表单名称
     */
    @ApiModelProperty(value = "疾病信息表单名称")
    @Length(max = 50, message = "基本信息表单名称长度不能超过50")
    @TableField(value = "health_info_name", condition = LIKE)
    @Excel(name = "疾病信息表单名称")
    private String healthInfoName;

    @ApiModelProperty("47小时未注册是否提醒")
    @TableField(value = "has_unregistered_reminder", condition = EQUAL)
    private Integer hasUnregisteredReminder;

    @ApiModelProperty("47小时未注册提醒内容")
    @TableField(value = "unregistered_reminder")
    private String unregisteredReminder;

    @TableField(value = "tenant_code", condition = EQUAL)
    private String tenantCode;

    /**
     * 是否开启购药跳转url：0开启，1不开启
     */
    @ApiModelProperty(value = "是否开启购药跳转url：0开启，1不开启")
    @TableField("buy_drugs_url_switch")
    private Integer buyDrugsUrlSwitch;

    /**
     * 购药跳转url
     */
    @ApiModelProperty(value = "购药跳转url")
    @TableField("buy_drugs_url")
    private String buyDrugsUrl;


    @ApiModelProperty(value = "微信用户直接关注进来默认的角色 saas_tourists: 游客，doctor: 医生，patient: 患者")
    @TableField("wx_user_default_role")
    private String wxUserDefaultRole;

}
