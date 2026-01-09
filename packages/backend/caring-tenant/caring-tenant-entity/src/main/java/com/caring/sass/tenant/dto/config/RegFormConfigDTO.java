package com.caring.sass.tenant.dto.config;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author leizhi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SaveRegFormConfigDTO", description = "微信注册引导&表单配置")
public class RegFormConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入组引导
     */
    @ApiModelProperty(value = "入组引导")
    @Length(max = 100, message = "入组引导长度不能超过100")
    private String guide;
    /**
     * 入组描素
     */
    @ApiModelProperty(value = "入组描素")
    @Length(max = 100, message = "入组描素长度不能超过100")
    private String describe;
    /**
     * 引导图片
     */
    @ApiModelProperty(value = "引导图片")
    @Length(max = 500, message = "引导图片长度不能超过500")
    private String icon;
    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    @Deprecated
    @Length(max = 200, message = "链接地址长度不能超过200")
    private String url;
    /**
     * 是否添加项目介绍 0:添加  1：不添加
     */
    @ApiModelProperty(value = "是否添加项目介绍 0:添加  1：不添加")
    private Integer enableIntro;
    /**
     * 项目介绍url
     */
    @ApiModelProperty(value = "项目介绍url")
    private String intro;
    /**
     * 协议
     */
    @ApiModelProperty(value = "患者协议")
    @Length(max = 65535, message = "患者协议长度不能超过65,535")
    private String agreement;

    @ApiModelProperty(value = "医生服务协议")
    @Length(max = 65535, message = "协议长度不能超过65,535")
    private String doctorAgreement;
    /**
     * 护理目标
     */
    @ApiModelProperty(value = "护理目标")
    @Length(max = 500, message = "护理目标长度不能超过500")
    private String nursingTarget;
    /**
     * 入组成功消息类型 0：图文消息   1：文字消息
     */
    @ApiModelProperty(value = "入组成功消息类型 0：图文消息   1：文字消息")
    private Integer successMsgType;
    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    @Length(max = 500, message = "消息内容长度不能超过500")
    private String successMsg;

    /**
     * 是否显示医生和机构：0显示，1不显示
     */
    @ApiModelProperty(value = "是否显示医生和机构：0显示，1不显示")
    private Integer hasShowDoctor;

    /**
     * 是否显示机构名称：0显示，1不显示
     */
    @ApiModelProperty(value = "是否显示机构名称：0显示，1不显示")
    private Integer hasShowOrgName;

    /**
     * 是否填充微信名：0：填充 1：不填充
     */
    @ApiModelProperty(value = "是否填充微信名：0：填充 1：不填充")
    private Integer hasFillWxName = 1;

    /**
     * 基本信息表单名称
     */
    @ApiModelProperty(value = "基本信息表单名称")
    @Deprecated
    private String basicFormName;

    /**
     * 健康信息表单名称
     */
    @ApiModelProperty(value = "健康信息表单名称")
    @Deprecated
    private String heathFormName;


    @ApiModelProperty(value = "已有基础表单")
    @Deprecated
    private Boolean hasBasicForm;

    @ApiModelProperty(value = "已有健康信息表单")
    @Deprecated
    private Boolean hasHealthForm;

    @ApiModelProperty(value = "表单历史记录功能： 0不记录， 1记录")
    private Integer formHistoryRecord;

    @ApiModelProperty(value = "基本信息表单id")
    @Deprecated
    private Long basicFormId;

    @ApiModelProperty(value = "健康信息表单id")
    @Deprecated
    private Long heathFormId;

    /**
     * 是否在入组界面中是否显示推荐用药：0显示，1不显示
     */
    @ApiModelProperty(value = "是否在入组界面中是否显示推荐用药：0显示，1不显示")
    private Integer hasShowRecommendDrugs;

    /**
     * 是否在注册时填写用药信息：0填写，1不填写
     */
    @ApiModelProperty(value = "是否在注册时填写用药信息：0填写，1不填写")
    private Integer hasFillDrugs;

    @ApiModelProperty("47小时未注册是否提醒")
    @Deprecated
    private Integer hasUnregisteredReminder;

    @ApiModelProperty("47小时未注册提醒内容")
    @Deprecated
    private String unregisteredReminder;

    /**
     * 是否开启购药跳转url：0开启，1不开启
     */
    @ApiModelProperty(value = "是否开启购药跳转url：0开启，1不开启")
    private Integer buyDrugsUrlSwitch;

    /**
     * 购药跳转url
     */
    @ApiModelProperty(value = "购药跳转url")
    private String buyDrugsUrl;

}
