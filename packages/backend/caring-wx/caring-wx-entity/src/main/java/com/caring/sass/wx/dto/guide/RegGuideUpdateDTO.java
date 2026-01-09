package com.caring.sass.wx.dto.guide;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "RegGuideUpdateDTO", description = "微信注册引导")
public class RegGuideUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
    @Length(max = 200, message = "链接地址长度不能超过200")
    private String url;
    /**
     * 删除标记(0：未删除)
     */
    @ApiModelProperty(value = "删除标记(0：未删除)")
    private Integer delFlag;
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
    @ApiModelProperty(value = "协议")
    @Length(max = 65535, message = "协议长度不能超过65,535")
    private String agreement;

    @ApiModelProperty(value = "医生协议")
    @Length(max = 65535, message = "医生协议长度不能超过65,535")
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
    @NotNull
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
    @NotNull
    private Integer hasFillWxName;

    @ApiModelProperty(value = "表单历史记录功能： 0不记录， 1记录")
    private Integer formHistoryRecord;

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

    /**
     * 基本信息表单名称
     */
    @ApiModelProperty(value = "基本信息表单名称")
    @Length(max = 50, message = "基本信息表单名称长度不能超过50")
    private String baseInfoName;

    /**
     * 疾病信息表单名称
     */
    @ApiModelProperty(value = "疾病信息表单名称")
    @Length(max = 50, message = "基本信息表单名称长度不能超过50")
    private String healthInfoName;
}
